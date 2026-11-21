# Especificação: Hemocomponente (bolsa de sangue)

## Linguagem do domínio

- **Bolsa (Hemocomponente):** unidade de um hemocomponente produzida a partir de uma doação.
- **Código:** identificador de negócio da bolsa, em maiúsculas, único no estoque.
- **Disponível:** bolsa com status `DISPONIVEL` e validade que ainda não passou. Uma bolsa que vence hoje ainda é válida.
- **Reservar:** vincular a bolsa a uma solicitação, tirando-a do estoque disponível.

## Invariantes

1. O código é obrigatório e é guardado sem espaços nas pontas e em maiúsculas.
2. O tipo do hemocomponente, a data de produção e a data de validade são obrigatórios.
3. A validade não pode ser anterior à produção.
4. A quantidade é maior que zero.
5. Uma bolsa nasce `DISPONIVEL` e com validade igual ou posterior a hoje.
6. O grupo sanguíneo da bolsa vem do doador da doação de origem e não é guardado na bolsa.

## Comandos e regras

| Comando | Regra | Erro |
|---|---|---|
| `Hemocomponente.registrar` | Aplica as invariantes 1 a 5 e exige a doação de origem. | 400 |
| `corrigirDados` | Aplica as invariantes 2 a 4. Só bloqueia validade vencida quando a validade muda, então editar a quantidade de uma bolsa já vencida é permitido. | 400 |
| `reservarPara(solicitacao, hoje)` | Só reserva bolsa disponível, e exige a solicitação. | 409 |
| `marcarComoVencida(hoje)` | Só vale para bolsa disponível cuja validade já passou. | 409 |

Regras que dependem do repositório ficam no serviço: código único (409), doação e localidade existentes, e grupo sanguíneo informado igual ao da doação (400).

## Cenários

```gherkin
Cenário: Registrar bolsa com código fora do padrão
  Dado o código "  b-001 "
  Quando a bolsa é registrada
  Então o código guardado é "B-001" e o status é DISPONIVEL
  # HemocomponenteTest.registrarNormalizaOCodigoEIniciaComoDisponivel

Cenário: Registrar bolsa que vence hoje
  Dado uma validade igual a hoje
  Quando a bolsa é registrada
  Então ela está disponível
  # HemocomponenteTest.registrarAceitaValidadeNoProprioDia

Cenário: Registrar bolsa já vencida
  Dado uma validade anterior a hoje
  Quando a bolsa é registrada
  Então o sistema recusa com "Não é permitido cadastrar bolsas com data de validade vencida!"
  # HemocomponenteTest.registrarRejeitaBolsaJaVencida

Cenário: Editar a quantidade de uma bolsa já vencida
  Dado uma bolsa cuja validade já passou
  Quando só a quantidade é alterada
  Então a edição é aceita
  # HemocomponenteTest.corrigirDadosPermiteManterAValidadeDeUmaBolsaJaVencida

Cenário: Reservar bolsa já reservada
  Dado uma bolsa RESERVADA
  Quando outra solicitação tenta reservá-la
  Então o sistema recusa com "A bolsa B1 não está disponível para reserva."
  # HemocomponenteTest.reservarParaRejeitaBolsaJaReservada

Cenário: Código repetido
  Dado uma bolsa com o código "B1" no estoque
  Quando outra bolsa é cadastrada com "b1"
  Então o sistema responde conflito
  # HemocomponenteEstoqueServiceTest
```
