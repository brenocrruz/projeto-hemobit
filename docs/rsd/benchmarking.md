# Relatório de Benchmarking de Rede — Hemobit

> Card W12 · Redes e Sistemas Distribuídos (RSD) · U2
> Métricas: latência, vazão, perda, jitter e erros 4xx/5xx do canal de telemetria e dos serviços da API.
> Dados brutos: [`benchmark-resultados/docker-local/`](benchmark-resultados/docker-local/) · Plano: [`plano-implantacao.md`](plano-implantacao.md)

## 1. Objetivo

Comprovar a qualidade do canal de telemetria (veículo → hemocentro) e dos serviços REST no ambiente de comunicação implantado, definindo limites de aceitação que servem tanto para este relatório quanto para o painel de monitoramento (`/rede`) e para os critérios de avanço do plano de implantação.

## 2. Ambiente medido

| Item | Valor |
|---|---|
| Topologia | `compose.yaml`: Nginx 1.27 (rede `publica` 10.0.1.0/24) → API Spring Boot (rede `aplicacao` 10.0.2.0/24) → PostgreSQL 16 (rede `banco` 10.0.3.0/28) |
| Entrada | Só o Nginx publica porta no host (`8080:80`); API e banco sem porta publicada |
| Host | macOS (Apple Silicon), Docker via OrbStack 29.4 |
| Cliente | `curl` no próprio host (`scripts/benchmark-rede.sh`) |
| Telemetria | 3 veículos simulados (CH, CP, PFC), 1 leitura/s cada, `POST /api/v1/telemetria/leituras` **passando pelo Nginx**; painel recebe por WebSocket (`/ws/telemetria`) |
| Degradação | `tc netem` na interface do Nginx voltada para a API (rede `aplicacao`) |
| Data | 01/11/2026, 00:28–00:33 (BRT) |

**Limitação:** cliente e servidor estão na mesma máquina, então o cenário nominal mede o custo da pilha (Nginx + Tomcat + Spring + redes virtuais do Docker), não um enlace WAN. O deploy público no Render estava fora do ar no dia (`x-render-routing: no-server`); a medição em nuvem é a fase F3 do plano. Para estudar o canal em condições reais de rede móvel, a seção 5 degrada o enlace **na camada de rede** com `tc netem` (perda de pacotes, atraso e jitter de verdade, tratados pelo TCP/IP do kernel), e não na aplicação.

## 3. Metodologia

| Métrica | Como foi medida | Ferramenta |
|---|---|---|
| **Latência (RTT)** | Tempo total da requisição HTTP (`time_total`) da sonda `GET /api/v1/rede/ping`; 200 sondas sequenciais, conexão TCP nova a cada sonda, intervalo de 50 ms. Também o handshake TCP (`time_connect`). Na telemetria, o RTT de cada `POST` medido pelo simulador. | `curl -w`, app |
| **Jitter** | Estimador da RFC 3550 §6.4.1 sobre latências consecutivas: `J += (|Dᵢ| − J)/16`. Na telemetria, calculado **por veículo** (cada veículo é um fluxo, como um SSRC no RTP) e combinado pela média ponderada. | script / app |
| **Perda** | Requisições sem resposta em até 2 s (timeout ou falha de conexão) ÷ total. Na telemetria, também por **lacunas de número de sequência** por veículo, observadas no servidor; leituras que chegam atrasadas e preenchem a lacuna deixam de contar como perda. | script / app |
| **Vazão** | (a) download de 1 MiB × 10 (`/api/v1/rede/carga`) → Mbit/s; (b) 500 requisições com 20 simultâneas → req/s; (c) telemetria em msg/s e kbit/s. | `curl`, `xargs -P` |
| **Erros 4xx/5xx** | Status HTTP no cliente (script) e no servidor (filtro `MetricasHttpFilter` em todas as rotas `/api/**`). No teste de ingestão, 5% das leituras foram enviadas **sem temperatura de propósito**, para verificar a rejeição com 400. | script / app |

Antes das medições, 20 requisições de aquecimento foram descartadas (JIT da JVM, pool de conexões). Percentis pelo método *nearest-rank*.

### Limites de aceitação

Definidos a partir do uso: a telemetria acompanha a cadeia fria (excursões de temperatura) e a posição dos veículos; um alerta precisa chegar ao operador em fração de segundo, e o painel considera um veículo "sem sinal" após alguns intervalos de 1 s sem leitura.

| Métrica | BOM | ATENÇÃO | CRÍTICO | Justificativa |
|---|---|---|---|---|
| Latência RTT p95 | ≤ 200 ms | ≤ 500 ms | > 500 ms | Até 200 ms o alerta é percebido como instantâneo; acima de 500 ms começa a competir com o intervalo de envio (1 s). |
| Jitter | ≤ 30 ms | ≤ 100 ms | > 100 ms | Jitter alto obriga a aumentar o timeout de "veículo sem sinal" e gera chegadas fora de ordem. |
| Perda | ≤ 1% | ≤ 5% | > 5% | Com 1 leitura/s, 1% significa ~36 lacunas por hora; acima disso, uma excursão térmica curta pode não ser registrada. |
| Erros 4xx | ≤ 2% | ≤ 10% | > 10% | 4xx é erro do cliente (leitura inválida); taxa alta indica sensor defeituoso ou contrato quebrado. |
| Erros 5xx | 0% | ≤ 1% | > 1% | Qualquer 5xx é falha do servidor e deve ser investigado. |

## 4. Resultados — ambiente nominal

### 4.1 Sondas da API

| Teste | Amostras | Média | p50 | p95 | p99 | Máx | Jitter | Perda | 4xx | 5xx |
|---|---|---|---|---|---|---|---|---|---|---|
| Latência sequencial (RTT total) | 200 | 5,31 ms | 5,20 ms | 7,52 ms | 9,23 ms | 23,50 ms | 1,39 ms | 0,00% | 0 | 0 |
| Handshake TCP (`time_connect`) | 200 | 0,27 ms | 0,25 ms | 0,37 ms | — | — | — | — | — | — |
| Carga concorrente (20 simultâneas) | 500 | 2,49 ms | 2,34 ms | 3,94 ms | 5,48 ms | 8,96 ms | 0,62 ms | 0,00% | 0 | 0 |
| Ingestão de telemetria (POST, 10 simultâneas) | 300 | 3,37 ms | 3,25 ms | 5,31 ms | 7,41 ms | 8,03 ms | 0,42 ms | 0,00% | 15 | 0 |

- **Vazão de download:** média **1.743 Mbit/s** (mínima 339 Mbit/s) em 10 downloads de 1 MiB.
- **Vazão de requisições:** **624 req/s** com 20 conexões simultâneas.
- **Ingestão:** 285 leituras aceitas (202), **15 rejeitadas com 400** (exatamente as 5% inválidas), 0 respostas 5xx.

### 4.2 Telemetria medida pela aplicação (painel `/rede`, janela de 60 s)

| Métrica | Valor |
|---|---|
| Amostras | 180 envios (3 veículos × 60 s) |
| Latência RTT (via Nginx) | média 6,67 ms · p50 6,39 ms · **p95 10,14 ms** · p99 14,77 ms |
| Jitter (por veículo) | **1,66 ms** |
| Perda (cliente / sequência) | **0,00% / 0,00%** |
| Vazão | 3,05 msg/s · 5,44 kbit/s |
| Tempo de servidor p95 | 3,47 ms |
| Erros 4xx / 5xx | 0 / 0 |

### 4.3 Interpretação

- **Latência:** o p95 de 7,5 ms (sonda) e 10 ms (telemetria) está **20 vezes abaixo** do limite de 200 ms. Como o tempo de servidor p95 é de ~3,5 ms, cerca de **um terço do RTT é processamento da API e o restante é caminho de rede** (salto pelo Nginx, redes virtuais do Docker e criação da conexão). A telemetria tem RTT maior que a sonda porque atravessa o Nginx a partir da própria rede `aplicacao` e envia um corpo JSON. O handshake TCP custa só 0,3 ms em rede local; em WAN ele passa a pesar (1 RTT a mais por conexão nova, 2 com TLS), por isso o Nginx mantém `keepalive` com a API.
- **Cauda (p99/máx):** os máximos (até 23 ms) ficam 3 a 4 vezes acima da mediana. São picos isolados (coleta de lixo da JVM, agendamento do SO) que não afetam o p95, e mostram por que o painel usa o p95 e não a média.
- **Jitter:** 0,4–1,7 ms significa intervalos de chegada praticamente constantes; o timeout de "veículo sem sinal" pode ser curto (ex.: 3 s) sem falso alarme.
- **Concorrência:** com 20 requisições simultâneas a latência não piorou (p95 3,9 ms): conexões já aquecidas e a sonda é leve. O servidor não enfileirou nessa carga.
- **Perda:** 0% em todos os testes nominais. A infraestrutura (Nginx, redes e API) **não introduz perda**; a perda real virá do enlace móvel do veículo (seção 5).
- **Vazão:** 1,7 Gbit/s de download e ~620 req/s mostram que a vazão **não é gargalo**: a telemetria de 3 veículos usa 5,4 kbit/s (~1,8 kbit/s por veículo). O valor de req/s é limitado pelo próprio cliente (um processo `curl` por requisição) e variou entre 295 e 624 req/s em execuções diferentes; serve como piso, não como capacidade máxima do servidor.
- **Erros:** os 15 erros 4xx são exatamente as leituras inválidas enviadas de propósito — a API **rejeita dados corrompidos sem derrubar o serviço** (0 respostas 5xx).

## 5. Resultados — enlace degradado com `tc netem`

O script aplica `tc qdisc add dev <iface> root netem <regra>` na interface do Nginx ligada à rede `aplicacao` (Nginx → API). Cada cenário roda 100 sondas e 65 s de telemetria, lidos pelo script e pelo painel. As regras imitam rede móvel em trânsito:

- **Degradado** (4G ruim): `delay 50ms 25ms distribution normal loss 2%`
- **Crítico** (zona de sombra): `delay 200ms 100ms distribution normal loss 10%`

As sondas atravessam o enlace degradado **uma vez** (pedido Nginx → API). A telemetria do simulador atravessa **duas** (o pedido repassado para a API e a resposta do Nginx de volta ao simulador, que está na mesma rede), por isso o atraso dela é cerca do dobro.

### 5.1 Sondas (script)

| Cenário | Sondas | Média | p50 | p95 | p99 | Máx | Jitter | Perda HTTP | 5xx |
|---|---|---|---|---|---|---|---|---|---|
| Nominal | 100 | 3,67 ms | 3,69 ms | 5,37 ms | 5,94 ms | 6,15 ms | 0,95 ms | 0,00% | 0 |
| Degradado | 100 | 75,78 ms | 66,91 ms | 125,56 ms | 365,60 ms | 418,83 ms | 71,80 ms | 0,00% | 0 |
| Crítico | 100 | 290,46 ms | 234,75 ms | 747,37 ms | 918,68 ms | 1.420,06 ms | 234,02 ms | 0,00% | 0 |

### 5.2 Telemetria (painel)

| Cenário | RTT p50 | RTT p95 | RTT p99 | Máx | Jitter | Perda cliente | Perda sequência | Fora de ordem | Vazão | Tempo servidor p95 | Classificação no painel |
|---|---|---|---|---|---|---|---|---|---|---|---|
| Nominal | 6,4 ms | 10,1 ms | 14,8 ms | 16,6 ms | 1,7 ms | 0% | 0% | 0 | 3,05 msg/s | 3,5 ms | Tudo BOM |
| Degradado | 122,6 ms | 219,9 ms | 397,4 ms | 408,4 ms | 57,1 ms | 0% | 0% | 0 | 3,02 msg/s | 2,9 ms | Latência e jitter ATENÇÃO; perda BOM |
| Crítico | 508,4 ms | 1.284,7 ms | 1.961,9 ms | 2.904,2 ms | 331,1 ms | 0% | 0,51%* | 1 | 3,01 msg/s | 3,0 ms | Latência e jitter CRÍTICO; perda BOM |

\* A única "lacuna" do cenário crítico era uma leitura que **chegou atrasada**, depois da seguinte (contada como fora de ordem). Esse achado levou a uma correção: agora, quando uma leitura atrasada preenche uma lacuna, ela deixa de contar como perda. Com a correção, a perda por sequência desse cenário seria 0%.

### 5.3 Interpretação

- **10% de perda de pacotes virou 0% de perda HTTP.** É o principal resultado do teste: o TCP **esconde a perda convertendo-a em latência**. Cada segmento perdido é retransmitido após o RTO (no Linux, mínimo de 200 ms), o que aparece como cauda longa: no crítico, a mediana das sondas (235 ms) é próxima do atraso configurado (200 ms), mas o p99 (919 ms) e o máximo (1.420 ms) são várias vezes maiores. Na telemetria, o pior envio levou 2,9 s, perto do timeout de 2 s do cliente em cada etapa. **Consequência para o monitoramento:** em canais TCP, medir só a "perda" engana; o sinal de perda no enlace é a **cauda da latência (p99) e o jitter**. Por isso o painel classifica latência e jitter como CRÍTICO mesmo com perda BOM.
- **A perda HTTP só aparece quando as retransmissões passam do timeout.** Com 10% de perda, uma retransmissão dupla (1%) ainda cabe em 2 s; com cobertura pior (perda maior ou atraso maior), as requisições começariam a estourar o timeout e a perda HTTP e de sequência subiria de forma abrupta. É por isso que o *buffer* com reenvio no veículo (plano, risco R4) é necessário: o TCP sozinho não cobre uma queda longa de sinal.
- **O instrumento mede o enlace, não a aplicação.** O tempo de servidor p95 ficou em ~3 ms nos três cenários enquanto o RTT subiu 100 vezes. O painel permite ao operador separar "rede ruim" (RTT, jitter e cauda altos com servidor rápido) de "servidor ruim" (tempo de servidor alto, 5xx).
- **Valores batem com a teoria.** No degradado, o atraso normal N(50, 25) ms dá p95 teórico de ≈ 91 ms por travessia; as sondas mediram p95 de 126 ms (o excedente são retransmissões) e a telemetria, com duas travessias, mediu p50 de 123 ms (≈ 2 × 50 ms + pilha).
- **Jitter cresce mais rápido que a latência média.** Do nominal ao degradado, o jitter da telemetria subiu ~34× (1,7 → 57 ms) e a mediana ~19×. Jitter alto causa chegada fora de ordem (vista no crítico) e exige timeout de "sem sinal" maior.
- **Vazão de telemetria não caiu (≈ 3 msg/s nos três).** Mensagens pequenas e assíncronas não dependem de banda; enquanto não há perda HTTP, cada leitura chega, só que mais tarde.
- **Nenhum 5xx** nos três cenários: a degradação do enlace não derrubou a API nem o Nginx.

## 6. Conclusões e capacidade

1. **No ambiente implantado, o canal atende com folga a todos os limites** (latência p95 de 7,5–10 ms vs 200 ms; jitter de 1,7 ms vs 30 ms; perda 0% vs 1%; 0 respostas 5xx).
2. **Em rede móvel degradada, o problema aparece como latência e jitter, não como perda.** O TCP absorveu 10% de perda de pacotes; o custo foi um RTT p95 de 1,3 s na telemetria. O monitoramento precisa olhar p95/p99 e jitter, que é o que o painel faz.
3. **O gargalo esperado em produção é o enlace móvel do veículo, não a infraestrutura.** Mitigação: *buffer* local no veículo com reenvio por número de sequência (plano, risco R4).
4. **Capacidade:** cada veículo gera ~1 req/s e ~1,8 kbit/s. Os 295–624 req/s medidos (limitados pelo cliente) comportam **centenas de veículos simultâneos** só na ingestão, contra uma frota prevista de dezenas — margem de mais de uma ordem de grandeza.
5. **Próximas medições** (fases do plano): repetir o script contra o ambiente em nuvem (`ROTULO=nuvem scripts/benchmark-rede.sh https://<url>`) para obter números de WAN reais, com TLS, e comparar com esta linha de base.

## 7. Como reproduzir

```bash
docker compose up -d --build
NETEM=1 ROTULO=docker-local scripts/benchmark-rede.sh http://localhost:8080
```

Saídas em `docs/rsd/benchmark-resultados/<ROTULO>/`: `latencia.csv`, `carga.csv`, `ingestao.csv`, `vazao.csv`, `netem-<cenário>.csv` (dados brutos), `metricas-painel.json`, `netem-<cenário>.json` (snapshots do painel) e `resumo.md`. O `NETEM=1` exige o `compose.yaml` local (usa `docker compose exec` no Nginx). Sem ele, o script funciona contra qualquer URL. O painel ao vivo fica em `http://localhost:8080/rede`.
