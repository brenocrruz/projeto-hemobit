#!/usr/bin/env bash
# Benchmarking de rede do Hemobit: latência, jitter, perda, vazão e erros 4xx/5xx.
#
# Uso:
#   scripts/benchmark-rede.sh [BASE_URL] [SAIDA]
#   BASE_URL padrão: http://localhost:8080   SAIDA padrão: docs/rsd/benchmark-resultados/<rótulo>
#
# Variáveis opcionais:
#   ROTULO=local           nome do ambiente no relatório
#   N_LATENCIA=200         sondas sequenciais (latência/jitter/perda)
#   INTERVALO=0.05         pausa entre sondas, em segundos
#   N_CONCORRENTE=500      requisições no teste de carga
#   CONCORRENCIA=20        requisições simultâneas no teste de carga
#   N_DOWNLOAD=10          downloads para medir vazão
#   KB_DOWNLOAD=1024       tamanho de cada download (KB)
#   N_INGESTAO=300         leituras de telemetria enviadas no teste de ingestão
#   PING_PATH=/api/v1/rede/ping   sonda leve (use outra rota se o ambiente não tiver a sonda)
#   NETEM=1                degrada o enlace Nginx -> API com tc netem (perda/atraso reais) e mede
#                          os cenários nominal, degradado e crítico (~4 min). Só no compose local.
#   N_CENARIO=100          sondas sequenciais por cenário netem
#   TIMEOUT=2              tempo máximo por requisição (s); acima disso conta como perda
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
ROTULO="${ROTULO:-local}"
SAIDA="${2:-docs/rsd/benchmark-resultados/$ROTULO}"
N_LATENCIA="${N_LATENCIA:-200}"
INTERVALO="${INTERVALO:-0.05}"
N_CONCORRENTE="${N_CONCORRENTE:-500}"
CONCORRENCIA="${CONCORRENCIA:-20}"
N_DOWNLOAD="${N_DOWNLOAD:-10}"
KB_DOWNLOAD="${KB_DOWNLOAD:-1024}"
N_INGESTAO="${N_INGESTAO:-300}"
PING_PATH="${PING_PATH:-/api/v1/rede/ping}"
NETEM="${NETEM:-0}"
N_CENARIO="${N_CENARIO:-100}"
TIMEOUT="${TIMEOUT:-2}"

mkdir -p "$SAIDA"
RESUMO="$SAIDA/resumo.md"
FORMATO='%{http_code},%{time_connect},%{time_starttransfer},%{time_total},%{size_download}\n'

log() { printf '[%s] %s\n' "$(date +%H:%M:%S)" "$*" >&2; }

# Estatísticas de uma coluna (ms) de um CSV: média, p50, p95, p99, máx e jitter RFC 3550.
# Linhas com status 000 (timeout/erro de conexão) contam como perda e ficam fora da latência.
# Percentis pelo método nearest-rank, sobre os valores ordenados com sort -n (awk POSIX, sem gawk).
estatisticas() {
    local arquivo="$1" coluna="$2" ordem percentis
    ordem=$(awk -F, -v c="$coluna" '
        NR > 1 { total++; if ($1 == "000") { perdidos++; next }
                 v = $c * 1000; n++; soma += v
                 if (n > 1) { d = v - ant; if (d < 0) d = -d; j += (d - j) / 16 } ant = v
                 if ($1 >= 400 && $1 < 500) e4++; if ($1 >= 500) e5++ }
        END { printf "%.2f|%.2f|%.2f|%d|%d|%d|%d", (n ? soma / n : 0), j, (total ? 100 * perdidos / total : 0),
                     total, perdidos, e4, e5 }' "$arquivo")
    percentis=$(awk -F, -v c="$coluna" 'NR > 1 && $1 != "000" { printf "%.3f\n", $c * 1000 }' "$arquivo" | sort -n |
        awk '{ v[NR] = $1 } END {
            if (NR == 0) { print "—|—|—|—"; exit }
            r50 = int(0.50 * NR); if (r50 < 0.50 * NR) r50++
            r95 = int(0.95 * NR); if (r95 < 0.95 * NR) r95++
            r99 = int(0.99 * NR); if (r99 < 0.99 * NR) r99++
            printf "%.2f|%.2f|%.2f|%.2f", v[r50], v[r95], v[r99], v[NR] }')
    IFS='|' read -r media jitter perda total perdidos e4 e5 <<< "$ordem"
    IFS='|' read -r p50 p95 p99 max <<< "$percentis"
    echo "$media|$p50|$p95|$p99|$max|$jitter|$perda|$total|$perdidos|$e4|$e5"
}

agora() { perl -MTime::HiRes=time -e 'printf "%.3f", time'; }

log "Ambiente: $ROTULO ($BASE_URL) — resultados em $SAIDA"

# 0. Aquecimento (JIT, pool de conexões, cold start de PaaS) — descartado.
log "Aquecimento..."
for _ in $(seq 1 20); do curl -s -o /dev/null --max-time 30 "$BASE_URL$PING_PATH" || true; done

# 1. Latência, jitter e perda: sondas sequenciais, uma conexão TCP nova por sonda.
log "1/4 Latência/jitter/perda: $N_LATENCIA sondas sequenciais"
ARQ_LAT="$SAIDA/latencia.csv"
echo "status,connect_s,ttfb_s,total_s,bytes" > "$ARQ_LAT"
for _ in $(seq 1 "$N_LATENCIA"); do
    curl -s -o /dev/null --max-time "$TIMEOUT" -w "$FORMATO" "$BASE_URL$PING_PATH" >> "$ARQ_LAT" \
        || echo "000,0,0,$TIMEOUT,0" >> "$ARQ_LAT"
    sleep "$INTERVALO"
done
IFS='|' read -r L_MED L_P50 L_P95 L_P99 L_MAX L_JIT L_PERDA L_TOT L_PERD L_4XX L_5XX < <(estatisticas "$ARQ_LAT" 4)
IFS='|' read -r C_MED C_P50 C_P95 _ _ _ _ _ _ _ _ < <(estatisticas "$ARQ_LAT" 2)

# 2. Vazão de download (throughput) com corpo de tamanho conhecido.
log "2/4 Vazão: $N_DOWNLOAD downloads de $KB_DOWNLOAD KB"
ARQ_DL="$SAIDA/vazao.csv"
echo "status,total_s,bytes,velocidade_Bps" > "$ARQ_DL"
for _ in $(seq 1 "$N_DOWNLOAD"); do
    curl -s -o /dev/null --max-time 60 -w '%{http_code},%{time_total},%{size_download},%{speed_download}\n' \
        "$BASE_URL/api/v1/rede/carga?kb=$KB_DOWNLOAD" >> "$ARQ_DL" || echo "000,0,0,0" >> "$ARQ_DL"
done
VAZAO_MBPS=$(awk -F, 'NR > 1 && $1 == 200 { s += $4; n++ } END { if (n) printf "%.2f", s * 8 / 1e6 / n; else print "—" }' "$ARQ_DL")
VAZAO_MIN=$(awk -F, 'NR > 1 && $1 == 200 { v = $4 * 8 / 1e6; if (!m || v < m) m = v } END { if (m) printf "%.2f", m; else print "—" }' "$ARQ_DL")

# 3. Carga concorrente: requisições por segundo e degradação da latência sob concorrência.
log "3/4 Carga: $N_CONCORRENTE requisições, $CONCORRENCIA simultâneas"
ARQ_CARGA="$SAIDA/carga.csv"
echo "status,connect_s,ttfb_s,total_s,bytes" > "$ARQ_CARGA"
INICIO=$(agora)
seq 1 "$N_CONCORRENTE" | xargs -P "$CONCORRENCIA" -I{} \
    curl -s -o /dev/null --max-time "$TIMEOUT" -w "$FORMATO" "$BASE_URL$PING_PATH" >> "$ARQ_CARGA" || true
FIM=$(agora)
# Requisições que estouraram o timeout não escrevem linha: completa com 000 para contar como perda.
FALTANTES=$(( N_CONCORRENTE - $(tail -n +2 "$ARQ_CARGA" | wc -l) ))
# (laço com contador: o seq do macOS conta para trás quando FALTANTES = 0)
while [ "$FALTANTES" -gt 0 ]; do echo "000,0,0,$TIMEOUT,0" >> "$ARQ_CARGA"; FALTANTES=$(( FALTANTES - 1 )); done
IFS='|' read -r K_MED K_P50 K_P95 K_P99 K_MAX K_JIT K_PERDA K_TOT K_PERD K_4XX K_5XX < <(estatisticas "$ARQ_CARGA" 4)
RPS=$(awk -v n="$N_CONCORRENTE" -v p="$K_PERD" -v i="$INICIO" -v f="$FIM" 'BEGIN { printf "%.1f", (n - p) / (f - i) }')

# 4. Ingestão de telemetria: POSTs concorrentes; ~5% propositalmente inválidos para exercitar 4xx.
log "4/4 Ingestão: $N_INGESTAO leituras de telemetria"
ARQ_ING="$SAIDA/ingestao.csv"
echo "status,connect_s,ttfb_s,total_s,bytes" > "$ARQ_ING"
VEICULO="BENCH-$(date +%s)"
seq 0 $(( N_INGESTAO - 1 )) | xargs -P 10 -I{} sh -c '
    seq=$1; temp=4.0; [ $((seq % 20)) -eq 0 ] && temp=null
    curl -s -o /dev/null --max-time "$2" -w "$3" -H "Content-Type: application/json" \
        -d "{\"veiculo\":\"$4\",\"sequencia\":$seq,\"tipoHemocomponente\":\"CH\",\"latitude\":-8.05,\"longitude\":-34.88,\"temperaturaC\":$temp,\"enviadoEm\":$(date +%s)000}" \
        "$5/api/v1/telemetria/leituras"' _ {} "$TIMEOUT" "$FORMATO" "$VEICULO" "$BASE_URL" >> "$ARQ_ING" || true
IFS='|' read -r I_MED I_P50 I_P95 I_P99 I_MAX I_JIT I_PERDA I_TOT I_PERD I_4XX I_5XX < <(estatisticas "$ARQ_ING" 4)
I_2XX=$(awk -F, 'NR > 1 && $1 ~ /^2/ { n++ } END { print n + 0 }' "$ARQ_ING")

# Snapshot do painel (métricas medidas pela própria aplicação) e limpeza do veículo BENCH-*,
# para que o teste de ingestão não deixe lacunas artificiais no painel.
curl -s --max-time 10 "$BASE_URL/api/v1/rede/metricas" > "$SAIDA/metricas-painel.json" || true
curl -s -o /dev/null -X POST "$BASE_URL/api/v1/rede/metricas/reiniciar" || true

# 5. (Opcional) degradação real do enlace com tc netem na interface do Nginx voltada para a API
# (rede aplicacao 10.0.2.0/24). Afeta as sondas (host -> Nginx -> API) e a telemetria do simulador.
LINHAS_NETEM=""
if [ "$NETEM" = "1" ]; then
    IFACE=$(docker compose exec -T nginx sh -c 'ip -o -4 addr show | grep " 10\.0\.2\." | cut -d" " -f2' | tr -d '\r')
    [ -n "$IFACE" ] || { echo "Interface do Nginx na rede aplicacao não encontrada" >&2; exit 1; }
    limpar_netem() { docker compose exec -T nginx tc qdisc del dev "$IFACE" root 2>/dev/null || true; }
    trap limpar_netem EXIT
    for c in "nominal|" "degradado|delay 50ms 25ms distribution normal loss 2%" "critico|delay 200ms 100ms distribution normal loss 10%"; do
        nome="${c%%|*}"; regra="${c#*|}"
        limpar_netem
        # shellcheck disable=SC2086
        [ -n "$regra" ] && docker compose exec -T nginx tc qdisc add dev "$IFACE" root netem $regra
        log "Cenário netem $nome (${regra:-sem degradação}) — $N_CENARIO sondas + 65 s de telemetria"
        curl -s -o /dev/null -X POST "$BASE_URL/api/v1/rede/metricas/reiniciar"
        INICIO_CENARIO=$(agora)
        ARQ_C="$SAIDA/netem-$nome.csv"
        echo "status,connect_s,ttfb_s,total_s,bytes" > "$ARQ_C"
        for _ in $(seq 1 "$N_CENARIO"); do
            curl -s -o /dev/null --max-time "$TIMEOUT" -w "$FORMATO" "$BASE_URL$PING_PATH" >> "$ARQ_C" \
                || echo "000,0,0,$TIMEOUT,0" >> "$ARQ_C"
            sleep "$INTERVALO"
        done
        RESTANTE=$(awk -v i="$INICIO_CENARIO" -v f="$(agora)" 'BEGIN { r = 65 - (f - i); print (r > 0 ? int(r) : 0) }')
        sleep "$RESTANTE"
        curl -s "$BASE_URL/api/v1/rede/metricas" > "$SAIDA/netem-$nome.json"
        IFS='|' read -r N_MED N_P50 N_P95 N_P99 N_MAX N_JIT N_PERDA N_TOT _ _ N_5XX < <(estatisticas "$ARQ_C" 4)
        LINHAS_NETEM="$LINHAS_NETEM| $nome | ${regra:-—} | $N_TOT | $N_MED ms | $N_P50 ms | $N_P95 ms | $N_P99 ms | $N_MAX ms | $N_JIT ms | $N_PERDA% | $N_5XX |
"
    done
    limpar_netem
fi

cat > "$RESUMO" <<EOF
# Resultado do benchmarking — $ROTULO

- Alvo: \`$BASE_URL\` · sonda: \`$PING_PATH\`
- Executado em: $(date '+%Y-%m-%d %H:%M:%S %z') · cliente: $(uname -sm)

| Teste | Amostras | Média | p50 | p95 | p99 | Máx | Jitter | Perda | 4xx | 5xx |
|---|---|---|---|---|---|---|---|---|---|---|
| Latência sequencial (RTT total) | $L_TOT | $L_MED ms | $L_P50 ms | $L_P95 ms | $L_P99 ms | $L_MAX ms | $L_JIT ms | $L_PERDA% | $L_4XX | $L_5XX |
| Handshake TCP (time_connect) | $L_TOT | $C_MED ms | $C_P50 ms | $C_P95 ms | — | — | — | — | — | — |
| Carga concorrente (x$CONCORRENCIA) | $K_TOT | $K_MED ms | $K_P50 ms | $K_P95 ms | $K_P99 ms | $K_MAX ms | $K_JIT ms | $K_PERDA% | $K_4XX | $K_5XX |
| Ingestão de telemetria (POST, x10) | $I_TOT | $I_MED ms | $I_P50 ms | $I_P95 ms | $I_P99 ms | $I_MAX ms | $I_JIT ms | $I_PERDA% | $I_4XX | $I_5XX |

- **Vazão de download:** média $VAZAO_MBPS Mbit/s (mínima $VAZAO_MIN Mbit/s) em $N_DOWNLOAD downloads de $KB_DOWNLOAD KB.
- **Vazão de requisições:** $RPS req/s com $CONCORRENCIA conexões simultâneas.
- **Ingestão:** $I_2XX leituras aceitas (2xx), $I_4XX rejeitadas (4xx, inválidas de propósito), $I_5XX falhas (5xx).
EOF
if [ -n "$LINHAS_NETEM" ]; then
    cat >> "$RESUMO" <<EOF

## Cenários com tc netem (enlace Nginx -> API)

| Cenário | Regra netem | Sondas | Média | p50 | p95 | p99 | Máx | Jitter | Perda | 5xx |
|---|---|---|---|---|---|---|---|---|---|---|
$LINHAS_NETEM
Telemetria do simulador em cada cenário: \`netem-<cenário>.json\` (snapshot do painel).
EOF
fi
log "Resumo:"
cat "$RESUMO"
