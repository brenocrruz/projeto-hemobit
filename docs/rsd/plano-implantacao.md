# Plano de Implantação e Migração — Hemobit

> Card W12 · Redes e Sistemas Distribuídos (RSD) · U2
> Referências: topologia em [`arquitetura_hemobit_gomes.pdf`](arquitetura_hemobit_gomes.pdf), contratos em [`contratos-api.md`](contratos-api.md) e medições em [`benchmarking.md`](benchmarking.md).

## 1. Objetivo

Levar o Hemobit do estado atual (um único contêiner de aplicação publicado direto na Internet) para o ambiente segmentado definido na topologia, com o canal de telemetria medido e monitorado, de forma incremental e reversível. Cada fase só avança quando as métricas de rede do [benchmarking](benchmarking.md) ficam dentro dos limites aceitos.

## 2. Situação atual (*as-is*)

| Item | Situação |
|---|---|
| Produção | Render (Web Service a partir do `Dockerfile`). Em 01/11/2026 a URL pública respondia `x-render-routing: no-server` (serviço suspenso/sem instância), ou seja, não há produção ativa para migrar dados. |
| Rede | Aplicação exposta diretamente (porta 8080) sem proxy reverso, sem segmentação entre API e banco. |
| Telemetria | Apenas especificada (`/ws/telemetria` em `contratos-api.md`); nenhum dado era gerado nem medido. |
| Observabilidade | Nenhuma métrica de rede; erros 4xx/5xx só visíveis em log. |

## 3. Ambiente alvo (*to-be*)

```
Internet ──HTTPS 443──▶ [Nginx]  sub-rede pública 10.0.1.0/24
                           │ HTTP 8080 (rede interna)
                           ▼
                 [API REST + simulador de telemetria]  sub-rede de aplicação 10.0.2.0/24 (internal)
                           │ PostgreSQL 5432
                           ▼
                      [PostgreSQL]  sub-rede de banco 10.0.3.0/28 (internal, sem rota externa)
```

| Componente | Rede / IP (Compose) | Porta | Exposição |
|---|---|---|---|
| Nginx 1.27 (proxy reverso, imagem com `tc`) | `publica` 10.0.1.10 · `aplicacao` 10.0.2.20 | 80 (443 na fase 2) | Única porta publicada no host (`8080:80` em dev); repassa HTTP e o WebSocket `/ws/telemetria` |
| API Spring Boot + simulador | `aplicacao` 10.0.2.10 · `banco` 10.0.3.11 | 8080 | Somente rede interna |
| PostgreSQL 16 | `banco` 10.0.3.10 | 5432 | Somente rede interna (`internal: true`); `pg_hba.conf` aceita só a API |

O fluxo de telemetria simulada sai da aplicação e **entra novamente pelo Nginx** (`TELEMETRIA_URL_INGESTAO=http://nginx/...`), atravessando o mesmo caminho que um veículo real vindo da Internet percorreria. Assim, a latência medida inclui o salto do proxy. O painel recebe o estado por WebSocket (`/ws/telemetria`, push a cada 1 s e alertas de cadeia fria na hora) e volta para *polling* HTTP se a conexão cair.

**Configuração por ambiente:** o simulador (`TELEMETRIA_SIMULADOR_HABILITADO`) e o endpoint de controle `POST /api/v1/rede/metricas/reiniciar` (`REDE_CONTROLE_HABILITADO`) vêm **desligados por padrão**; só o `compose.yaml` local os liga. Em produção, o painel mostra apenas o tráfego real da API, e ninguém consegue zerar as métricas.

## 4. Fases

| Fase | Entrega | Critério de saída (go/no-go) | Status |
|---|---|---|---|
| **F0 — Linha de base** | Simulador de telemetria, coleta de métricas (`/api/v1/rede/metricas`), painel `/rede` e script `scripts/benchmark-rede.sh`, com cenários de degradação real (`tc netem`). | Benchmark executado e registrado em `benchmarking.md`. | ✅ Concluída |
| **F1 — Ambiente segmentado local** | `compose.yaml` com 3 redes (pública, aplicação, banco), Nginx como único ponto de entrada, banco e API sem porta publicada. | `docker compose ps` mostra só o Nginx com porta no host; telemetria passando pelo Nginx com p95 < 200 ms, perda < 1% e 0 respostas 5xx. | ✅ Concluída |
| **F2 — Endurecimento** | TLS no Nginx (porta 443, redirect 80→443, HSTS); autenticação na ingestão de telemetria (token por veículo); `rate limit` na ingestão. Os endpoints de controle já vêm desligados fora do ambiente local. | Varredura de portas só encontra 443; ingestão sem token responde 401; benchmark repetido sem regressão de p95 > 20%. | Pendente (controles ✅) |
| **F3 — Implantação em nuvem** | Render: Web Service (imagem atual) + PostgreSQL gerenciado na rede privada do Render; ou VM única com o mesmo `compose.yaml`. Variáveis via painel de *secrets*. | `scripts/benchmark-rede.sh https://<url>` com `ROTULO=nuvem`: p95 < 500 ms, perda < 1%, 5xx = 0 por 24 h. | Pendente |
| **F4 — Telemetria em tempo real** | Canal WebSocket `/ws/telemetria` empurra métricas (1 s) e alertas de cadeia fria ao painel; ingestão continua por HTTP POST; *polling* como reserva. | Atraso fim a fim de alerta < 1 s no p95; reconexão automática testada derrubando o Nginx. | ✅ Implementada no ambiente local (falta medir em nuvem) |
| **F5 — Operação** | Alertas automáticos a partir dos limites do painel; retenção das métricas em banco; *runbook* de incidentes. | 7 dias sem incidente não detectado pelo painel. | Pendente |

## 5. Procedimento de implantação (F1, reproduzível)

```bash
cp .env.example .env              # ajustar POSTGRES_PASSWORD / SPRING_DATASOURCE_PASSWORD
docker compose up -d --build
docker compose ps                 # só o nginx deve ter porta publicada (0.0.0.0:8080->80)
curl -s localhost:8080/api/v1/rede/ping
open http://localhost:8080/rede   # painel de rede
NETEM=1 ROTULO=docker-local scripts/benchmark-rede.sh http://localhost:8080
```

Verificações de isolamento:

```bash
docker compose exec nginx wget -qO- http://app:8080/api/v1/rede/ping   # deve responder (rede aplicacao)
docker compose exec nginx sh -c 'nc -zw2 db 5432 && echo ABERTO || echo BLOQUEADO'   # BLOQUEADO: nginx não está na rede banco
docker compose exec app sh -c 'wget -qO- -T 3 http://example.com >/dev/null && echo INTERNET || echo SEM-INTERNET'   # SEM-INTERNET
# Mesmo com a senha certa, um cliente fora da API é recusado pelo pg_hba.conf:
source .env && docker run --rm --network projeto-hemobit_publica -e PGPASSWORD=$POSTGRES_PASSWORD \
  postgres:16-alpine psql -h 10.0.3.10 -U postgres -d hemobit -c "select 1"
# FATAL: no pg_hba.conf entry for host "10.0.3.1" ...
```

**Achado da verificação (01/11/2026):** no Docker do macOS (OrbStack), o Nginx **alcança o IP** do banco (10.0.3.10:5432) mesmo sem estar na rede `banco`; só a resolução do nome `db` é isolada. No Docker Engine do Linux, as regras `DOCKER-ISOLATION` bloqueiam esse tráfego. Por isso foi adicionada uma segunda camada: [`infra/postgres/pg_hba.conf`](../../infra/postgres/pg_hba.conf) só aceita conexões do IP da API (10.0.3.11) e do socket local. O teste acima confirma a recusa.

## 6. Estratégia de corte (*cutover*) e retorno (*rollback*)

1. **Antes do corte:** `pg_dump` do banco de origem (quando houver produção ativa); rodar o benchmark no ambiente novo e comparar com a linha de base.
2. **Corte:** como não há produção ativa no Render, o corte é direto (*big bang*) para o ambiente novo. Havendo produção, usar *blue/green*: subir o ambiente novo em paralelo, validar pelo painel `/rede` e só então trocar o DNS/URL.
3. **Rollback:** em qualquer violação do critério da fase (p95, perda ou 5xx), voltar para a imagem anterior (`Manual Deploy → commit anterior` no Render ou `git checkout <tag> && docker compose up -d --build`) e restaurar o `pg_dump`. O simulador pode ser desligado sem novo build com `TELEMETRIA_SIMULADOR_HABILITADO=false`.

## 7. Riscos e mitigações

| # | Risco | Prob. | Impacto | Mitigação |
|---|---|---|---|---|
| R1 | **Sub-redes 10.0.1-3.x colidem** com a rede local/VPN de quem roda o Compose (Docker não sobe ou rotas somem). | Média | Médio | Faixas centralizadas no `compose.yaml`; trocar para 172.30.x.x se houver conflito. Documentado neste plano. |
| R2 | **Cold start do PaaS gratuito** (Render dorme após inatividade): primeira requisição leva dezenas de segundos e aparece como pico de latência/perda. | Alta | Médio | O script descarta 20 requisições de aquecimento; em produção, plano pago ou *health check* periódico. O painel mostra o pico como “CRÍTICO” — não confundir com falha de rede. |
| R3 | **Endpoints sem autenticação**: o de controle (`/metricas/reiniciar`) zeraria as métricas; a ingestão aceita leituras de qualquer origem. | Média | Alto | Controle desligado por padrão (`REDE_CONTROLE_HABILITADO=false` → `403`); simulador desligado por padrão. Token por veículo na ingestão fica para a F2. |
| R4 | **Perda de telemetria em trânsito** (zona de sombra 4G, túneis): trilha de temperatura incompleta e excursão térmica não registrada. | Alta | Alto | Número de sequência por veículo detecta lacunas no servidor. O benchmark com `tc netem` mostrou que o TCP converte perda de pacotes em latência (retransmissão) até o timeout de 2 s; acima disso a leitura se perde. Próximo passo: *buffer* local no veículo com reenvio. Limite de aceitação: perda < 1%. |
| R5 | **Relógios dessincronizados** distorcem o “atraso unidirecional” (`recebidoEm − enviadoEm`). | Média | Baixo | Usar o RTT (medido em um único relógio) como métrica oficial; atraso unidirecional só como indicativo. NTP nos hosts. |
| R6 | **Banco indisponível** gera 5xx em cascata. | Baixa | Alto | `healthcheck` + `depends_on: service_healthy`; 5xx > 0 vira alerta “CRÍTICO” no painel. |
| R7 | **Nginx como ponto único de falha.** | Baixa | Alto | Contêiner reinicia com o Compose; em nuvem, usar o balanceador do provedor na frente. |
| R8 | **Capacidade insuficiente** se o número de veículos crescer. | Baixa | Médio | Benchmark mostra folga de mais de uma ordem de grandeza sobre a carga prevista (ver seção 6 do [benchmarking](benchmarking.md)). Reavaliar acima de 100 veículos. |
| R9 | **Dados pessoais em logs** (LGPD). | Baixa | Alto | Telemetria não carrega dados pessoais; log do Nginx registra só método, rota, status e tempos; `spring.mvc.log-request-details=false` mantido. |

## 8. Monitoramento pós-implantação

- **Painel `/rede`** (item *Rede e telemetria* no menu): latência p50/p95, jitter, perda (cliente e por sequência), vazão, erros 4xx/5xx e tempo de servidor por rota, com classificação BOM/ATENÇÃO/CRÍTICO e diagnóstico em texto.
- **API** `GET /api/v1/rede/metricas`: o mesmo conteúdo em JSON, para integração com outras ferramentas.
- **Logs do Nginx** (`docker compose logs nginx`): `rt=` (tempo total no proxy), `urt=` (tempo da API) e `uct=` (conexão com a API) por requisição — permitem separar atraso de rede do atraso de aplicação.
- **Limites de alerta** (os mesmos do painel e do benchmarking):

| Métrica | BOM | ATENÇÃO | CRÍTICO |
|---|---|---|---|
| Latência RTT p95 | ≤ 200 ms | ≤ 500 ms | > 500 ms |
| Jitter | ≤ 30 ms | ≤ 100 ms | > 100 ms |
| Perda | ≤ 1% | ≤ 5% | > 5% |
| Erros 4xx | ≤ 2% | ≤ 10% | > 10% |
| Erros 5xx | 0% | ≤ 1% | > 1% |
| Tempo de servidor p95 | ≤ 100 ms | ≤ 300 ms | > 300 ms |

## 9. Checklist de implantação

- [x] Redes segmentadas e Nginx como entrada única (`compose.yaml`, `infra/nginx/nginx.conf`)
- [x] Simulador de telemetria passando pelo proxy
- [x] Painel de rede integrado ao menu da aplicação
- [x] Benchmark de linha de base registrado
- [ ] TLS e autenticação nos endpoints de controle (F2)
- [ ] Benchmark no ambiente de nuvem (F3)
- [x] Canal WebSocket de métricas e alertas (F4, ambiente local)
- [x] Simulador e controles desligados por padrão fora do ambiente local
