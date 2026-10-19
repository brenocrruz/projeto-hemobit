# Análise Probabilística — Desabastecimento e Descarte

> Disciplina: Estatística e Probabilidade (EST) · Unidade 2 · Projeto Hemobit
> Painel: `/painel-estatistico.html` (integrado à aplicação Spring Boot)

## 1. Objetivo

Apoiar decisões de estoque e priorização do centro de hemocomponentes respondendo a duas perguntas:

1. **Qual é a probabilidade de faltar hemocomponente** de cada tipo (CH, CP, PFC) nos próximos dias?
2. **Que parcela das bolsas está sendo perdida por vencimento?**

Os resultados se somam aos indicadores descritivos da U1 (estoque por tipo, demanda por hospital e tempos de atendimento) no mesmo painel.

## 2. Fonte dos dados

Todos os números vêm do banco da aplicação, sem valores digitados à mão:

| Dado | Origem | Regra |
|---|---|---|
| Estoque válido | `Hemocomponente` | status `DISPONIVEL` e `dataValidade` ≥ hoje |
| Demanda | `Solicitacao` | soma de `quantidade` dos últimos 30 dias, exceto solicitações `CANCELADA` |
| Total de bolsas | `Hemocomponente` | todas as bolsas do tipo |
| Bolsas vencidas | `Hemocomponente` | status `VENCIDO` ou `DESCARTADO`, ou `DISPONIVEL` com validade já passada |

Premissa de modelagem: cada registro de `Hemocomponente` é contado como **1 bolsa**.

Aviso: o projeto usa dados sintéticos (LGPD). Os resultados têm finalidade didática.

## 3. Método

### 3.1 Probabilidade de desabastecimento

A demanda de bolsas em um horizonte de tempo é modelada como uma variável aleatória **D com distribuição de Poisson**, com parâmetro λ igual à demanda esperada no horizonte:

```
λ = (demanda total na janela ÷ dias da janela) × dias do horizonte
```

Há desabastecimento quando a demanda supera o estoque válido **S**:

```
P(desabastecimento) = P(D > S) = 1 − Σ (k = 0 até S) [ e^(−λ) · λ^k / k! ]
```

Parâmetros adotados:

- janela de histórico: **30 dias**;
- horizonte (tempo de reposição): **3 dias**.

Classificação de risco (escolha do grupo, ajustável):

| Probabilidade | Nível |
|---|---|
| ≥ 50% | ALTO |
| entre 20% e 50% | MÉDIO |
| < 20% | BAIXO |

### 3.2 Taxa de descarte por vencimento

```
taxa de descarte (%) = bolsas vencidas ÷ total de bolsas do tipo × 100
```

## 4. Exemplos calculados à mão

Os exemplos abaixo são **ilustrativos** e correspondem aos testes automatizados de `EstatisticaUtilTest`.

**Exemplo A — risco alto.** Demanda de 100 bolsas em 10 dias, horizonte de 3 dias:
λ = 100 ÷ 10 × 3 = **10**. Com estoque S = 8: P(D > 8) = 1 − P(D ≤ 8) ≈ **66,7%** → risco ALTO.

**Exemplo B — sem estoque.** λ = 2 e S = 0: P(D > 0) = 1 − e^(−2) ≈ **86,5%**.

**Exemplo C — descarte.** 14 bolsas vencidas em 200: 14 ÷ 200 × 100 = **7%**.

## 5. Resultados com os dados do projeto

> Preencher com os valores exibidos no painel (`/painel-estatistico.html`) e anexar o print.

| Tipo | Estoque válido | Demanda diária | λ (3 dias) | P(desabastecimento) | Risco | Descarte |
|---|---|---|---|---|---|---|
| Concentrado de Hemácias (CH) | _ | _ | _ | _ % | _ | _ % |
| Concentrado de Plaquetas (CP) | _ | _ | _ | _ % | _ | _ % |
| Plasma Fresco Congelado (PFC) | _ | _ | _ | _ % | _ | _ % |

## 6. Interpretação e decisões de estoque

Regras de decisão propostas, para serem aplicadas sobre os números da seção 5:

| Situação | Decisão sugerida |
|---|---|
| Risco **ALTO** | Priorizar coleta e reposição do tipo; atender primeiro solicitações de emergência e urgência; reavaliar pedidos de rotina. |
| Risco **MÉDIO** | Programar reposição dentro do horizonte e acompanhar o estoque diariamente. |
| Risco **BAIXO** | Manter o estoque atual; atenção ao descarte, pois estoque excessivo aumenta perdas. |
| Descarte elevado | Reforçar a política FEFO (primeiro a vencer, primeiro a sair) e revisar o volume de coleta daquele tipo. |

### Achados

> Preencher depois de ver o painel com dados. Cada achado deve ligar um número a uma decisão.

1. _Exemplo de formato:_ "O tipo ___ tem ___% de probabilidade de desabastecimento em 3 dias. Recomendamos ___."
2. _Exemplo de formato:_ "A taxa de descarte de ___ é ___%. Recomendamos ___."
3. _..._

## 7. Limitações

- O modelo de Poisson supõe pedidos independentes e com intensidade constante. Como uma solicitação pode pedir várias bolsas de uma vez, a variação real da demanda tende a ser maior que a do modelo, e o risco pode estar **subestimado**.
- A estimativa de demanda usa só os últimos 30 dias; períodos com pouco histórico geram resultados instáveis.
- Dados sintéticos: os números servem para demonstrar o método, não para decisão clínica.
- O sistema não substitui protocolos clínicos nem decide indicação de transfusão.

## 8. Onde está no código

| Parte | Arquivo |
|---|---|
| Fórmulas | `src/main/java/com/hemobit/hemobit/util/EstatisticaUtil.java` |
| Consultas ao banco | `src/main/java/com/hemobit/hemobit/repository/AnaliseRepository.java` |
| Regras e parâmetros | `src/main/java/com/hemobit/hemobit/service/AnaliseProbabilisticaService.java` |
| Endpoint | `GET /api/analise/probabilistica` (`AnaliseProbabilisticaController.java`) |
| Painel | `src/main/resources/static/painel-estatistico.html` |
| Testes das fórmulas | `src/test/java/com/hemobit/hemobit/util/EstatisticaUtilTest.java` |
