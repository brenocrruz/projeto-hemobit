# Especificação da API REST — Hemobit

# Especificação da API REST e Telemetria — Hemobit

## 1. Protocolo de Telemetria
Para atualizações em tempo real do estoque de hemocomponentes e alteração crítica de status das solicitações, utiliza-se o protocolo **WebSocket** (`ws://` / `wss://`). Para as operações convencionais de cadastro e consulta, utiliza-se o protocolo **HTTP/REST**.

---

## 2. Endpoints, Recursos e Status HTTP

### Recurso: Hospitais (`/hospitais`)
* `GET /hospitais` — Lista todos os hospitais cadastrados.
  * **Status:** `200 OK`
* `GET /hospitais/{id}` — Busca hospital por ID.
  * **Status:** `200 OK`, `404 Not Found`
* `POST /hospitais` — Cadastra um novo hospital.
  * **Status:** `201 Created`, `400 Bad Request`, `409 Conflict`
* `PUT /hospitais/{id}` — Atualiza os dados de um hospital.
  * **Status:** `200 OK`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`
* `DELETE /hospitais/{id}` — Remove um hospital.
  * **Status:** `204 No Content`, `401 Unauthorized`

#### Exemplo JSON — Cadastro de Hospital (POST /hospitais):
```json
{
  "nome": "Hospital Restauração",
  "cnpj": "12.345.678/0001-90",
  "telefone": "(81) 3181-5000",
  "endereco": "Av. Agamenon Magalhães, s/n - Recife/PE"
}
}
