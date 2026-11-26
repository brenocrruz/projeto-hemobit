# Observabilidade do Hemobit

O Hemobit é uma aplicação Java 21 / Spring Boot 4.1.1 em camadas. O mesmo processo serve Spring MVC, Thymeleaf e WebSocket e executa o simulador de telemetria. A persistência usa Spring Data JPA e HikariCP com PostgreSQL no Compose. A configuração fora do Compose dispõe de H2. O Nginx encaminha o tráfego para a aplicação.

## Execução

Requisitos: Docker com contêineres Linux, Docker Compose e Git. No Docker Desktop, os exporters observam o ambiente Linux do Docker. Para validar CPU e memória por contêiner, confirme a presença de séries com nome do contêiner no cAdvisor.

1. Copie `.env.example` para `.env`. Defina uma senha própria para o Grafana e use a mesma senha nos dois campos de senha do PostgreSQL. Em um volume Grafana existente, a variável de inicialização não troca a senha já cadastrada.
2. Execute `docker compose up -d --build` e `docker compose ps`.
3. Na raiz, execute `.\scripts\carga-observabilidade.ps1 -Segundos 180` em PowerShell. A carga consulta estoque no banco e inclui respostas HTTP 404 de uma rota inexistente.

| Componente | Endereço e finalidade |
| --- | --- |
| Hemobit via Nginx | http://localhost:8080 |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090/targets |
| Actuator | `app:8081/actuator/{health,info,metrics,prometheus}` via rede Docker |
| Node Exporter / cAdvisor | `node-exporter:9100` / `cadvisor:8080` |
| Loki | `loki:3100` |
| Tempo | `tempo:3200` para consultas, `tempo:4318` para ingestão OTLP/HTTP |
| PostgreSQL | `db:5432` |

## Métricas de negócio

| Micrometer | Tipo | Semântica |
| --- | --- | --- |
| `hemobit.estoque.consultas` | Counter | Consultas de estoque disponível concluídas pelo fluxo de estoque real |
| `hemobit.estoque.consulta.duracao` | Timer | Tempo da agregação de estoque pelo repositório JPA, inclusive tentativas com falha |

Os dois painéis estão em **Hemobit → Hemobit | Aplicação**. No Prometheus, consulte `hemobit_estoque_consultas_total{application="hemobit"}` e `hemobit_estoque_consulta_duracao_seconds_count{application="hemobit"}`.

## Validação e evidências

- E1. Execute `docker compose exec -T app wget -qO- http://localhost:8081/actuator/prometheus`. Confira a tag `application="hemobit"` e séries HTTP, JVM, processo e Hikari.
- E2/E3. Confira os quatro targets em `/targets`. Faça uma consulta PromQL com `up` e outra com a métrica de negócio após gerar carga.
- E4. Abra os dashboards **Aplicação** e **Infraestrutura** durante a carga. Confira valores de negócio, CPU e memória por contêiner.
- E5/E6. No Grafana Explore, escolha Loki e execute `{application="hemobit"} |= "Consulta ao estoque"`. Abra uma linha e clique no campo derivado `traceId` para abrir o Tempo. Confira a requisição HTTP, a consulta de estoque e a agregação no banco.
- Acesso ao Actuator pelo proxy deve retornar 404. Teste com `curl.exe -i http://localhost:8080/actuator/prometheus`.
- Registre data, hora e fuso ao capturar cada evidência. Para a entrega final, inclua Compose, Actuator, Targets, PromQL, os dois dashboards, painel de negócio e a sequência Loki → Tempo.

## Acesso remoto

Em um host autorizado, use um proxy HTTPS ou túnel apontado apenas para `127.0.0.1:3000`. Ajuste `GRAFANA_ROOT_URL` no `.env` ao domínio HTTPS e recrie o Grafana. Crie uma conta sem papel de administrador, com papel **Viewer** na organização do Hemobit. Faça login de outra rede, confira os dois dashboards e rode `.\scripts\verificar-viewer.ps1 -Url https://SEU-DOMINIO -Credencial (Get-Credential)`. Mantenha a carga durante a correção e informe URL, janela e credencial por canal privado.

As portas publicadas no host estão vinculadas a loopback. A porta de gerenciamento 8081 e os serviços de coleta permanecem na rede Docker.
