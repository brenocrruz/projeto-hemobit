# Formalização dos Modelos Probabilísticos — Hemobit

## 1. Modelo de Desabastecimento (Distribuição de Poisson)
A demanda diária de hemocomponentes por hospital (ex: $X$ requisições por dia) é modelada através de uma **Distribuição de Poisson**, cuja probabilidade de ocorrer $k$ eventos em um intervalo é dada por:

$$P(X = k) = \frac{e^{-\lambda} \lambda^k}{k!}$$

- **$\lambda$ (taxa média):** Média histórica de requisições diárias de bolsas de sangue por unidade hospitalar (ex: $\lambda = 4.5$ requisições/dia).
- **Aplicação no Hemobit:** Permite calcular a probabilidade de o estoque de um determinado tipo sanguíneo atingir zero antes da próxima reposição, disparando alertas preditivos de desabastecimento.

## 2. Modelo de Descarte por Vencimento (Exponencial / Binomial)
O risco de descarte de bolsas de concentrado de hemácias, plaquetas ou plasma devido ao término da janela de validade (FEFO) é avaliado considerando a taxa de giro do estoque versus o tempo limite de prateleira.
- **Probabilidade de Perda ($P_{descarte}$):** Relação percentual entre o volume de bolsas vencidas não solicitadas e o total de entradas no período de captação.
