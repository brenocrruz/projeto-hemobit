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

Para atualizações em tempo real do estoque de hemocomponentes e alterações críticas de status, a aplicação utiliza o protocolo **WebSocket**. Para as operações convencionais de cadastro e consulta, utiliza-se **HTTP/REST**.

| Item | Valor |
| --- | --- |
| Protocolo | WebSocket (`ws://` em desenvolvimento, `wss://` em produção) |
| Endpoint | `/ws/telemetria` |
| Formato | JSON |
| Frequência | Evento-driven (dispara ao detectar alteração crítica) |

**Exemplo de mensagem WebSocket:**

```json
{
  "evento": "ESTOQUE_CRITICO",
  "tipoSanguineo": "O_NEGATIVO",
  "quantidadeAtual": 2,
  "timestamp": "2026-09-14T10:30:00"
}
```
 
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