# Demonstração — Entrega 03 (HU06 e HU07)

Dados e roteiro para demonstrar a criação de solicitações (HU06) e o atendimento com status, histórico, ABO/Rh e FEFO (HU07). Todos os dados são sintéticos.

## 1. Dados de demonstração

Rode uma vez no pgAdmin (Query Tool, banco `hemobit`), com a aplicação já iniciada pelo menos uma vez para as tabelas existirem. Cria um hemocentro, um hospital e 8 bolsas de concentrado de hemácias.

| Bolsa | Tipo do doador | Vence em | Para paciente A+ |
|---|---|---|---|
| 1 | O- | 3 dias | compatível |
| 2 | O+ | 10 dias | compatível |
| 3 | A+ | 5 dias | compatível |
| 4 | A- | 25 dias | compatível |
| 5 | B+ | 12 dias | incompatível |
| 6 | AB+ | 20 dias | incompatível |
| 7 | A+ | vencida (-2 dias) | ignorada |
| 8 | O- | 40 dias | compatível |

```sql
DO $$
DECLARE
  coleta_id bigint;
  hosp_id bigint;
  d_id bigint;
  dc_id bigint;
  tipos text[] := ARRAY['O_NEGATIVO','O_POSITIVO','A_POSITIVO','A_NEGATIVO','B_POSITIVO','AB_POSITIVO','A_POSITIVO','O_NEGATIVO'];
  dias int[] := ARRAY[3, 10, 5, 25, 12, 20, -2, 40];
  i int;
BEGIN
  INSERT INTO localidade (nome, endereco, cidade, telefone, tipo_localidade, cnpj)
  VALUES ('[DEMO] Hemocentro Central', 'Av. Demo, 100', 'Recife', '8130000000', 'UNIDADE_COLETA', '00000000000000')
  RETURNING id INTO coleta_id;

  INSERT INTO localidade (nome, endereco, cidade, telefone, tipo_localidade, cnpj)
  VALUES ('[DEMO] Hospital São Marcos', 'Rua Demo, 200', 'Recife', '8130000001', 'UNIDADE_SAUDE', '11222333000181')
  RETURNING id INTO hosp_id;

  FOR i IN 1..array_length(tipos, 1) LOOP
    INSERT INTO doador (nome, telefone, data_nascimento, tipo_sanguineo)
    VALUES ('[DEMO] Doador ' || i, '81900000000', '1990-01-01', tipos[i])
    RETURNING id INTO d_id;

    INSERT INTO doacao (data_hora, quantidade, doador_id, unidade_coleta_id)
    VALUES (now() - interval '1 day', 450, d_id, coleta_id)
    RETURNING id INTO dc_id;

    INSERT INTO hemocomponente (tipo, quantidade, data_producao, data_validade, status, doacao_id, localizacao_atual_id)
    VALUES ('CONCENTRADO_HEMACIAS', 300, CURRENT_DATE - 1, CURRENT_DATE + dias[i], 'DISPONIVEL', dc_id, coleta_id);
  END LOOP;

  RAISE NOTICE 'Hospital demo com id %', hosp_id;
END $$;
```

Para descobrir o id do hospital: `GET /unidades-saude` (procure "[DEMO] Hospital São Marcos"). Nos exemplos abaixo ele aparece como `HOSPITAL_ID`.

Para limpar depois: apague as linhas cujo nome começa com `[DEMO]` (bolsas, doações, doadores e localidades, nessa ordem) e as solicitações criadas na demonstração.

## 2. Roteiro de requisições

Use Postman, Insomnia ou Thunder Client (VS Code). Base: `http://localhost:8080`.

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /solicitacoes` | `{"unidadeSaudeId": HOSPITAL_ID, "tipoHemocomponente": "CONCENTRADO_HEMACIAS", "tipoSanguineo": "A_POSITIVO", "quantidade": 0, "prioridade": "URGENTE"}` | **400** "A quantidade solicitada deve ser de no mínimo 1 bolsa." |
| 2 | `POST /solicitacoes` | igual, com `"quantidade": 2` | **201**, status `PENDENTE`, campo `id` (código) |
| 3 | `PUT /solicitacoes/{id}/status` | `{"status": "EM_SEPARACAO", "operador": "Maria"}` | **200**, status `EM_SEPARACAO` |
| 4 | `PUT /solicitacoes/{id}/atender?operador=Joao` | — | **200**, status `ATENDIDA`, `dataAtendimento` preenchida |
| 5 | `GET /solicitacoes/{id}/bolsas` | — | 2 bolsas: a de 3 dias (O-) e a de 5 dias (A+), nessa ordem (FEFO) |
| 6 | `GET /hemocomponentes` | — | as duas bolsas `RESERVADO`; a vencida e as incompatíveis continuam `DISPONIVEL` |
| 7 | `GET /solicitacoes/{id}/historico` | — | 3 linhas: criação, Maria (`EM_SEPARACAO`), Joao (`ATENDIDA`), com data e hora |
| 8 | `PUT /solicitacoes/{id}/status` | `{"status": "PENDENTE", "operador": "Maria"}` | **409** "Não é permitido alterar o status de solicitações já finalizadas." |
| 9 | `POST /solicitacoes` | tipo sanguíneo `AB_POSITIVO`, `"quantidade": 7` | **201** (novo id) |
| 10 | `PUT /solicitacoes/{id}/atender` | — | **409** "Estoque compatível insuficiente…" (há só 5 bolsas válidas); nada muda |

## 3. Pontos para mostrar no vídeo de código

- `SolicitacaoRequestDTO` e `SolicitacaoResponseDTO`: o padrão DTO aplicado também às solicitações.
- `SolicitacaoService.criar` e `validar`: regras da HU06 antes de gravar.
- `SolicitacaoService.alterarStatus` e `transicaoPermitida`: regras da HU07.
- `SolicitacaoService.atender`: filtro por validade e tipo, ordem FEFO, compatibilidade e reserva tudo-ou-nada.
- `HemocomponenteRepository.buscarParaReserva`: consulta ordenada por validade com bloqueio contra atendimentos simultâneos.
- `CompatibilidadeService`: ponte com a matriz ABO/Rh do módulo de AED.
- `HistoricoSolicitacao`: quem mudou, quando e de/para qual status.
- Testes: `SolicitacaoServiceTest` e `CompatibilidadeServiceTest`.
