# Especificação: Solicitação de hemocomponentes

## Linguagem do domínio

- **Solicitação:** pedido de uma unidade de saúde por bolsas de um tipo de hemocomponente para um paciente de um grupo sanguíneo.
- **Atender:** escolher e reservar as bolsas compatíveis (ABO/Rh) que vencem primeiro (FEFO), encerrando a solicitação como `ATENDIDA`.
- **Finalizada:** solicitação `ATENDIDA`, `CANCELADA` ou `NEGADA`. Nenhuma mudança é permitida depois disso.

## Invariantes

1. A quantidade é um número inteiro entre 1 e 50.
2. O tipo de hemocomponente, o grupo sanguíneo do paciente e a prioridade são obrigatórios.
3. Toda solicitação pertence a uma unidade de saúde.
4. Uma solicitação nasce `PENDENTE`.
5. Só uma solicitação `PENDENTE` pode ter o pedido editado.

## Ciclo de vida

```
PENDENTE ──► EM_SEPARACAO ──► ATENDIDA
   │              │
   ├──► CANCELADA ◄┤
   └──► NEGADA ◄───┘
```

`ATENDIDA` só se alcança pelo comando `atender`, nunca por `mudarStatus`.

## Comandos e regras

| Comando | Regra | Erro |
|---|---|---|
| `Solicitacao.abrir` | Aplica as invariantes 1 a 3. | 400 |
| `alterarPedido` | Exige `PENDENTE` e aplica a invariante 1 e 2. | 409 e 400 |
| `mudarStatus` | Segue o ciclo de vida, recusa o mesmo status e solicitação finalizada. Retorna o status anterior para o histórico. | 409 |
| `atender(candidatas, compativel, hoje, agora)` | Exige o grupo sanguíneo do paciente. Escolhe, na ordem recebida (FEFO), as primeiras bolsas compatíveis até a quantidade pedida. Se faltar bolsa, não reserva nenhuma e a solicitação continua como estava. | 409 |

O serviço consulta as bolsas disponíveis ordenadas pela validade, injeta a regra de compatibilidade (matriz ABO/Rh) e registra o histórico com o operador.

## Cenários

Os cenários de atendimento (FEFO e compatibilidade, estoque insuficiente) e o limite de 50 bolsas também executam como Cucumber em `src/test/resources/features/atendimento-solicitacao.feature`.

```gherkin
Cenário: Atender com FEFO e compatibilidade
  Dado um pedido de 2 bolsas para paciente A+
  E bolsas B+ (incompatível), O- , A+ e A+ em ordem de validade
  Quando a solicitação é atendida
  Então as bolsas O- e A+ mais próximas do vencimento são reservadas
  E a solicitação fica ATENDIDA com a data do atendimento
  # SolicitacaoTest.atenderReservaAsPrimeirasBolsasCompativeisNaOrdemRecebida

Cenário: Estoque compatível insuficiente
  Dado um pedido de 2 bolsas e apenas 1 bolsa compatível
  Quando a solicitação é atendida
  Então o sistema recusa, nenhuma bolsa é reservada e a solicitação continua PENDENTE
  # SolicitacaoTest.atenderComEstoqueInsuficienteNaoAlteraNada

Cenário: Mudar o status fora do ciclo de vida
  Dado uma solicitação EM_SEPARACAO
  Quando se tenta voltar para PENDENTE
  Então o sistema recusa com "Transição de status inválida: EM_SEPARACAO -> PENDENTE."
  # SolicitacaoTest.mudarStatusRejeitaTransicoesInvalidas

Cenário: Alterar solicitação finalizada
  Dado uma solicitação CANCELADA
  Quando se tenta mudar o status
  Então o sistema recusa
  # SolicitacaoTest.mudarStatusRejeitaSolicitacaoJaFinalizada

Cenário: Pedido acima do limite
  Dado um pedido de 51 bolsas
  Quando a solicitação é aberta
  Então o sistema recusa com "A quantidade máxima por solicitação é 50."
  # SolicitacaoTest.abrirRejeitaPedidosInvalidos
```
