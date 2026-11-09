# Demonstração — Entrega 04 (HU04 e HU05)

Dados e roteiro para demonstrar a entrada de bolsas no estoque (HU04) e a consulta com filtros (HU05). Todos os dados são sintéticos.

## 1. Dados de demonstração

Rode uma vez no pgAdmin (Query Tool, banco `hemobit`), com a aplicação já iniciada pelo menos uma vez para a coluna `codigo` existir. Cria 6 bolsas com código e 4 doações. Não há nenhuma bolsa AB- de propósito, para mostrar a consulta sem resultado.

| Código | Hemocomponente | Tipo sanguíneo | Status | Vence em |
|---|---|---|---|---|
| E4-001 | Concentrado de hemácias | O- | DISPONIVEL | 10 dias |
| E4-002 | Concentrado de hemácias | O- | DISPONIVEL | 25 dias |
| E4-003 | Concentrado de hemácias | O- | RESERVADO | 15 dias |
| E4-004 | Concentrado de plaquetas | A+ | DISPONIVEL | 5 dias |
| E4-005 | Plasma fresco congelado | B+ | DISPONIVEL | 30 dias |
| E4-006 | Concentrado de hemácias | A+ | DISPONIVEL | 20 dias |

Os resultados esperados abaixo valem para um banco que só tenha essas bolsas. Se você rodou a demonstração da Entrega 03, apague antes as bolsas daquela demonstração (só as de doadores `[DEMO]`):

```sql
DELETE FROM hemocomponente WHERE doacao_id IN (
  SELECT dc.id FROM doacao dc JOIN doador d ON d.id = dc.doador_id WHERE d.nome LIKE '[DEMO]%');
```

As doações criadas para a entrada de bolsas novas são de doadores O+ e O-. O script mostra os ids na aba **Messages** do pgAdmin (`DOACAO_O_POSITIVO_ID` e `DOACAO_O_NEGATIVO_ID`).

```sql
DO $$
DECLARE
  coleta_id bigint;
  d_id bigint;
  dc_id bigint;
  tipos text[] := ARRAY['O_NEGATIVO','O_NEGATIVO','O_NEGATIVO','A_POSITIVO','B_POSITIVO','A_POSITIVO'];
  comp text[] := ARRAY['CONCENTRADO_HEMACIAS','CONCENTRADO_HEMACIAS','CONCENTRADO_HEMACIAS','CONCENTRADO_PLAQUETAS','PLASMA_FRESCO_CONGELADO','CONCENTRADO_HEMACIAS'];
  situacao text[] := ARRAY['DISPONIVEL','DISPONIVEL','RESERVADO','DISPONIVEL','DISPONIVEL','DISPONIVEL'];
  dias int[] := ARRAY[10, 25, 15, 5, 30, 20];
  i int;
BEGIN
  IF EXISTS (SELECT 1 FROM hemocomponente WHERE codigo = 'E4-001') THEN
    RAISE NOTICE 'Os dados da Entrega 04 já existem.';
    RETURN;
  END IF;

  SELECT id INTO coleta_id FROM localidade WHERE nome = '[DEMO] Hemocentro Central' LIMIT 1;
  IF coleta_id IS NULL THEN
    INSERT INTO localidade (nome, endereco, cidade, telefone, tipo_localidade, cnpj)
    VALUES ('[DEMO] Hemocentro Central', 'Av. Demo, 100', 'Recife', '8130000000', 'UNIDADE_COLETA', '00000000000000')
    RETURNING id INTO coleta_id;
  END IF;

  FOR i IN 1..array_length(tipos, 1) LOOP
    INSERT INTO doador (nome, telefone, data_nascimento, tipo_sanguineo)
    VALUES ('[E4] Doador ' || i, '81900000000', '1990-01-01', tipos[i])
    RETURNING id INTO d_id;

    INSERT INTO doacao (data_hora, quantidade, doador_id, unidade_coleta_id)
    VALUES (now() - interval '1 day', 450, d_id, coleta_id)
    RETURNING id INTO dc_id;

    INSERT INTO hemocomponente (codigo, tipo, quantidade, data_producao, data_validade, status, doacao_id, localizacao_atual_id)
    VALUES ('E4-00' || i, comp[i], 300, CURRENT_DATE - 1, CURRENT_DATE + dias[i], situacao[i], dc_id, coleta_id);
  END LOOP;

  INSERT INTO doador (nome, telefone, data_nascimento, tipo_sanguineo)
  VALUES ('[E4] Doador O+', '81900000000', '1990-01-01', 'O_POSITIVO') RETURNING id INTO d_id;
  INSERT INTO doacao (data_hora, quantidade, doador_id, unidade_coleta_id)
  VALUES (now() - interval '1 day', 450, d_id, coleta_id) RETURNING id INTO dc_id;
  RAISE NOTICE 'DOACAO_O_POSITIVO_ID=%', dc_id;

  INSERT INTO doador (nome, telefone, data_nascimento, tipo_sanguineo)
  VALUES ('[E4] Doador O-', '81900000000', '1990-01-01', 'O_NEGATIVO') RETURNING id INTO d_id;
  INSERT INTO doacao (data_hora, quantidade, doador_id, unidade_coleta_id)
  VALUES (now() - interval '1 day', 450, d_id, coleta_id) RETURNING id INTO dc_id;
  RAISE NOTICE 'DOACAO_O_NEGATIVO_ID=%', dc_id;
END $$;
```

Se as bolsas das entregas anteriores aparecerem sem código, a coluna fica vazia (traço na tela). Para dar um código a elas: `UPDATE hemocomponente SET codigo = 'DEMO-' || lpad(id::text, 4, '0') WHERE codigo IS NULL;`

Para limpar depois, apague as bolsas cujo código começa com `E4-` ou `B102030`, depois as doações e doadores `[E4]`, nessa ordem.

## 2. Roteiro de requisições

Use Insomnia, Postman ou Thunder Client. Base: `http://localhost:8080`. Nos exemplos, `DOACAO_O_POSITIVO_ID` e `DOACAO_O_NEGATIVO_ID` são os ids mostrados pelo script (ou `GET /api/estoque/opcoes`).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /hemocomponentes` | — | todas as bolsas, a que vence primeiro no topo |
| 2 | `GET /hemocomponentes?tipoSanguineo=O_NEGATIVO&status=DISPONIVEL` | — | **200**, só E4-001 e E4-002 (a E4-003 é O- mas está RESERVADO) |
| 3 | `GET /hemocomponentes?tipoSanguineo=AB_NEGATIVO&status=DISPONIVEL` | — | **200** com lista vazia `[]` |
| 4 | `GET /hemocomponentes?status=INEXISTENTE` | — | **400** (filtro inválido) |
| 5 | `GET /hemocomponentes/estoque` | — | contagem por tipo: `O_NEGATIVO` 2, `A_POSITIVO` 2, `B_POSITIVO` 1, `O_POSITIVO` 0 |
| 6 | `POST /hemocomponentes` | `{"codigo": "B777000", "tipo": "PLASMA_FRESCO_CONGELADO", "quantidade": 300, "dataProducao": "2026-10-01", "dataValidade": "2026-10-10", "doacaoId": DOACAO_O_POSITIVO_ID}` | **400** "Não é permitido cadastrar bolsas com data de validade vencida!" |
| 7 | `POST /hemocomponentes` | `{"codigo": "B102030", "tipo": "PLASMA_FRESCO_CONGELADO", "quantidade": 300, "dataProducao": "HOJE_MENOS_1", "dataValidade": "HOJE_MAIS_30", "tipoSanguineo": "O_POSITIVO", "doacaoId": DOACAO_O_POSITIVO_ID}` | **201**, status `DISPONIVEL`, `codigo` "B102030", `tipoSanguineo` `O_POSITIVO` |
| 8 | `GET /hemocomponentes/estoque` | — | `O_POSITIVO` passou de 0 para 1 |
| 9 | `POST /hemocomponentes` | o mesmo corpo do 7 | **409** "Já existe uma bolsa cadastrada com o código B102030." |
| 10 | `POST /hemocomponentes` | corpo do 7 com `"codigo": "B102031"` e `"tipoSanguineo": "A_POSITIVO"` | **400** "O tipo sanguíneo informado não confere com o da doação selecionada." |
| 11 | `POST /hemocomponentes` | corpo do 7 com `"codigo": ""` | **400** "Informe o código da bolsa." |

Troque `HOJE_MENOS_1` e `HOJE_MAIS_30` por datas no formato `AAAA-MM-DD` (ontem e daqui a 30 dias). A requisição 6 usa datas fixas de outubro, que já passaram.

## 3. Roteiro pela tela

Abra `http://localhost:8080/estoque`.

1. A tabela mostra as bolsas com a coluna **Código**.
2. Escolha **Grupo sanguíneo = O-** e **Status = Disponível**: só E4-001 e E4-002 aparecem (a tela consulta a API com os filtros).
3. Escolha **Grupo sanguíneo = AB-**: a tela mostra "Nenhum hemocomponente encontrado para os filtros selecionados.".
4. Clique em **Limpar** e role até **Novo hemocomponente**.
5. Cadastre com uma validade antiga: a tela exibe "Não é permitido cadastrar bolsas com data de validade vencida!".
6. Cadastre `B102030` com validade futura, doação do O+: a tela exibe "Bolsa B102030 cadastrada com sucesso. Estoque disponível de O+: 1 bolsa(s)." e a bolsa entra na tabela.
7. Cadastre `B102030` de novo: a tela exibe "Já existe uma bolsa cadastrada com o código B102030.".

## 4. Pontos para mostrar no vídeo de código

- `HemocomponenteRequestDTO` e `HemocomponenteResponseDTO`: campos novos (`codigo`, `tipoSanguineo`) no padrão DTO.
- `Hemocomponente`: campo `codigo` único e `getTipoSanguineo()`, que vem da doação.
- `HemocomponenteService.criar`: validações da HU04 na ordem em que acontecem.
- `HemocomponenteService.listar`: filtros opcionais combinados e ordenação pela validade (HU05).
- `HemocomponenteService.resumoEstoque`: total disponível por tipo sanguíneo, ignorando reservadas e vencidas.
- `HemocomponenteController`: parâmetros opcionais da consulta, rota `/estoque` e o 409 de conflito.
- `HemocomponenteRepository.existsByCodigo`: a consulta que impede o código repetido.
- Tela `estoque.html` e `estoque.js`: campo de código, filtro de grupo sanguíneo ligado à API e mensagens.
- Testes: `HemocomponenteEstoqueServiceTest` e `HemocomponenteControllerTest`.
