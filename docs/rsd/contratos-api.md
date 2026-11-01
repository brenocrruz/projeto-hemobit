# Contratos de API REST — Hemobit

Este documento define os contratos da API REST disponibilizada pela aplicação, estabelecendo os endpoints, métodos HTTP, formatos de requisição e resposta, autenticação e códigos de status esperados.

A API utiliza versionamento por caminho e segue princípios REST, utilizando substantivos no plural para representar os recursos.
## 1. Protocolo de Telemetria
Para atualizações em tempo real do estoque de hemocomponentes e alteração crítica de status das solicitações, utiliza-se o protocolo **WebSocket** (`ws://` / `wss://`). Para as operações convencionais de cadastro e consulta, utiliza-se o protocolo **HTTP/REST**.

---
## 1. Informações Gerais

## Base URL
| Item | Valor |
| --- | --- |
| Base URL | `/api/v1` |
| Formato | `application/json` |
| Versionamento | Por caminho (`/api/v1/...`) |
| Autenticação | Indefinido |


## 2. Canal de Telemetria

A telemetria usa dois caminhos, cada um com o protocolo adequado ao sentido do tráfego:

| Sentido | Protocolo | Endpoint | Formato |
| --- | --- | --- | --- |
| Veículo → hemocentro (ingestão) | HTTP/REST `POST` | `/api/v1/telemetria/leituras` | JSON, uma leitura por requisição, resposta `202 Accepted` |
| Hemocentro → painel (tempo real) | WebSocket (`ws://` em desenvolvimento, `wss://` em produção) | `/ws/telemetria` | JSON, evento-driven |

A ingestão por HTTP é sem estado e passa por qualquer proxy/rede móvel; cada leitura carrega um número de `sequencia` por veículo, o que permite detectar perdas no servidor. O WebSocket só é usado para empurrar dados ao painel, sem *polling*. Conexões de outra origem (cabeçalho `Origin`) são recusadas com `403`.

**Leitura de telemetria (`POST /api/v1/telemetria/leituras`):**

```json
{
  "veiculo": "VTR-01",
  "sequencia": 42,
  "tipoHemocomponente": "CH",
  "latitude": -8.047600,
  "longitude": -34.877000,
  "temperaturaC": 4.12,
  "enviadoEm": 1793502715408
}
```

| Status | Quando |
| --- | --- |
| `202` | Leitura aceita. Corpo: `{"sequencia": 42, "recebidoEm": ..., "temperaturaConforme": true}` |
| `400` | Campo ausente/inválido (ex.: `temperaturaC` nula ou fora de -60 a 60 °C, `tipoHemocomponente` diferente de `CH`, `CP` ou `PFC`). Corpo: `{"erros": [...]}` |

**Eventos do WebSocket `/ws/telemetria`:**

| Evento | Frequência | Conteúdo |
| --- | --- | --- |
| `METRICAS_REDE` | A cada 1 s, para cada painel conectado | Mesmo conteúdo de `GET /api/v1/rede/metricas`: latência, jitter, perda, vazão, erros 4xx/5xx, série de 60 s e última leitura de cada veículo |
| `TEMPERATURA_FORA_DA_FAIXA` | Quando um veículo sai da faixa da cadeia fria (uma vez por saída) | Leitura que disparou o alerta |

```json
{
  "evento": "TEMPERATURA_FORA_DA_FAIXA",
  "veiculo": "VTR-01",
  "tipoHemocomponente": "CH",
  "temperaturaC": 7.4,
  "faixaC": [2.0, 6.0],
  "timestamp": 1793502715408
}
```

**Endpoints de apoio ao monitoramento:**

| Método | Caminho | Descrição | Status |
| --- | --- | --- | --- |
| GET | /api/v1/rede/metricas | Estado do painel (alternativa ao WebSocket) | 200 |
| GET | /api/v1/rede/ping | Sonda leve para medir RTT | 200 |
| GET | /api/v1/rede/carga?kb=N | Corpo de N KB (máx. 4096) para medir vazão | 200 |
| POST | /api/v1/rede/metricas/reiniciar | Zera a janela de métricas; só habilitado no ambiente local (`REDE_CONTROLE_HABILITADO=true`) | 204, 403 |
| GET | /api/v1/telemetria/veiculos | Última leitura de cada veículo | 200 |

---

## 3. Endpoints

### 3.1 Doadores


| Método | Caminho | Descrição | Autenticação | Resposta | Status |
| --- | --- | --- | --- | --- |---|
|GET| /api/v1/doadores | Lista todos os doadores | Indefinido | Lista de doadores | 200|
|POST| /api/v1/doadores | Cadastra um doador | Indefinido | Doador cadastrado | 201, 400|
|GET| /api/v1/doadores/{id} | Busca um doador pelo ID | Indefinido | Dados do doador | 200, 404|
|PUT| /api/v1/doadores/{id} | Atualiza um doador por ID | Indefinido | Doador atualizado | 200, 400, 404|
|PATCH| /api/v1/doadores/{id} | Atualiza parcialmente dados | Indefinido | Dados atualizados | 200|
|DELETE| /api/v1/doadores/{id} | Deleta um doador | Indefinido | Sem conteúdo |204, 404 |

### 3.2 Doações

| Método | Caminho | Descrição | Autenticação | Resposta | Status |
| --- | --- | --- | --- | --- |---|
|GET | /api/v1/doacoes | Lista todas as doações | Indefinido | Doações cadastradas | 200 |
|POST | /api/v1/doacoes | Cria uma nova doação | Indefinido | Doação Cadastrada | 201, 400|
|GET | /api/v1/doacoes/{id}| Consulta doação por ID | Indefinido | Dados da doação | 200,404|
|DELETE| /api/v1/doacoes/{id} | Deleta doação por ID | Indefinido | Sem conteúdo | 204,404 |

### 3.3 Unidades de Coleta

| Método | Caminho | Descrição | Autenticação | Resposta | Status |
| --- | --- | --- | --- | --- | --- |
| GET | /api/v1/unidades-coleta | Lista todas as unidades de coleta | Indefinido | Unidades cadastradas | 200 |
| POST | /api/v1/unidades-coleta | Cria uma nova unidade de coleta | Indefinido | Unidade cadastrada | 201, 400 |
| GET | /api/v1/unidades-coleta/{id} | Consulta unidade de coleta por ID | Indefinido | Dados da unidade | 200, 404 |
| PUT | /api/v1/unidades-coleta/{id} | Atualiza unidade de coleta por ID | Indefinido | Unidade atualizada | 200, 400, 404 |
| DELETE | /api/v1/unidades-coleta/{id} | Deleta unidade de coleta por ID | Indefinido | Sem conteúdo | 204, 404 |

### 3.4 Unidades de Saúde

| Método | Caminho | Descrição | Autenticação | Resposta | Status |
| --- | --- | --- | --- | --- | --- |
| GET | /api/v1/unidades-saude | Lista todas as unidades de saúde | Indefinido | Unidades cadastradas | 200 |
| POST | /api/v1/unidades-saude | Cria uma nova unidade de saúde | Indefinido | Unidade cadastrada | 201, 400 |
| GET | /api/v1/unidades-saude/{id} | Consulta unidade de saúde por ID | Indefinido | Dados da unidade | 200, 404 |
| PUT |/api/v1/unidades-saude/{id} | Atualiza unidade de saúde por ID | Indefinido | Unidade atualizada | 200, 400, 404 |
| DELETE | /api/v1/unidades-saude/{id} | Deleta unidade de saúde por ID | Indefinido | Sem conteúdo | 204, 404 |

## 4. Exemplos de Request/Response
---
### 4.1 Criar Doador

`POST /api/v1/doadores`

**Request:**
```
{
"nome" : "Carlos Antônio Silva",
"telefone" : "81987524632",
"dataNascimento" : "2006-09-14",
"tipoSanguineo" : "O_NEGATIVO"
}
```
**Response 201:**
```
{
"id": 1,
"nome": "Carlos Antônio Silva",
"telefone": "81987524632",
"dataNascimento": "2006-09-14",
"tipoSanguineo": "O_NEGATIVO"
}
```
---
### 4.2 Listar Unidades de Coleta

`GET /api/v1/unidades-coleta`

**Response 200:**
```
{
"id": 1,
"nome": "Unidade de Coleta Recife",
"endereco": "Av. Exemplo, 100",
"cidade": "Recife",
"telefone": "(81) 99999-9999"
},
{
"id": 2,
"nome": "Unidade de Coleta Olinda",
"endereco": "Rua Exemplo, 200",
"cidade": "Olinda",
"telefone": "(81) 98888-8888"
}
```
---
### 4.3 Atualizar Unidade de Saúde

`PUT /api/v1/unidades-saude/{id}`

**Request:**

```
{
 "nome": "Hospital das Clínicas",
  "endereco": "Av. Professor Moraes Rego, 1235",
  "cidade": "Recife",
  "telefone": "(81) 2126-8000"
}
```

**Response 200:**

```
{
"id": 5,
 "nome": "Hospital das Clínicas",
  "endereco": "Av. Professor Moraes Rego, 1235",
  "cidade": "Recife",
  "telefone": "(81) 2126-8000"
}
```

### 4.4 Deletar Doação

`DELETE /doacoes/{id}`

**Request:** Sem corpo

**Response 204:** Sem corpo