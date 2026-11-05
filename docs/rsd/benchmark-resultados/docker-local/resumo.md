# Resultado do benchmarking — docker-local

- Alvo: `http://localhost:8080` · sonda: `/api/v1/rede/ping`
- Executado em: 2026-11-01 00:32:42 -0300 · cliente: Darwin arm64

| Teste | Amostras | Média | p50 | p95 | p99 | Máx | Jitter | Perda | 4xx | 5xx |
|---|---|---|---|---|---|---|---|---|---|---|
| Latência sequencial (RTT total) | 200 | 5.31 ms | 5.20 ms | 7.52 ms | 9.23 ms | 23.50 ms | 1.39 ms | 0.00% | 0 | 0 |
| Handshake TCP (time_connect) | 200 | 0.27 ms | 0.25 ms | 0.37 ms | — | — | — | — | — | — |
| Carga concorrente (x20) | 500 | 2.49 ms | 2.34 ms | 3.94 ms | 5.48 ms | 8.96 ms | 0.62 ms | 0.00% | 0 | 0 |
| Ingestão de telemetria (POST, x10) | 300 | 3.37 ms | 3.25 ms | 5.31 ms | 7.41 ms | 8.03 ms | 0.42 ms | 0.00% | 15 | 0 |

- **Vazão de download:** média 1743.34 Mbit/s (mínima 338.97 Mbit/s) em 10 downloads de 1024 KB.
- **Vazão de requisições:** 624.2 req/s com 20 conexões simultâneas.
- **Ingestão:** 285 leituras aceitas (2xx), 15 rejeitadas (4xx, inválidas de propósito), 0 falhas (5xx).

## Cenários com tc netem (enlace Nginx -> API)

| Cenário | Regra netem | Sondas | Média | p50 | p95 | p99 | Máx | Jitter | Perda | 5xx |
|---|---|---|---|---|---|---|---|---|---|---|
| nominal | — | 100 | 3.67 ms | 3.69 ms | 5.37 ms | 5.94 ms | 6.15 ms | 0.95 ms | 0.00% | 0 |
| degradado | delay 50ms 25ms distribution normal loss 2% | 100 | 75.78 ms | 66.91 ms | 125.56 ms | 365.60 ms | 418.83 ms | 71.80 ms | 0.00% | 0 |
| critico | delay 200ms 100ms distribution normal loss 10% | 100 | 290.46 ms | 234.75 ms | 747.37 ms | 918.68 ms | 1420.06 ms | 234.02 ms | 0.00% | 0 |

Telemetria do simulador em cada cenário: `netem-<cenário>.json` (snapshot do painel).
