# Análise de Complexidade: Módulo de Compatibilidade ABO/Rh

A verificação de compatibilidade sanguínea foi implementada utilizando uma Matriz de Adjacência (matriz bidimensional estática `boolean[][]`).

## 1. Complexidade de Tempo (Time Complexity)
* **Acesso à Matriz:** $O(1)$. Como convertemos as strings dos tipos sanguíneos em índices inteiros (0 a 7), a verificação de compatibilidade vai diretamente à posição de memória `matrizABORh[receptor][doador]`. Não há laços de repetição na verificação isolada.
* **Busca Integrada (FEFO):** O método `retirarCompativel` percorre, no pior dos casos, os 8 índices da matriz e das gavetas da Tabela Hash. Como 8 é um limite superior constante (não cresce com o tamanho do estoque), a complexidade desta iteração é $O(1)$. A remoção da bolsa na Fila FEFO também ocorre em $O(1)$.
* **Complexidade de Tempo Total:** $O(1)$ (Tempo Constante).

## 2. Complexidade de Espaço (Space Complexity)
* **Estrutura da Matriz:** $O(1)$. A matriz possui um tamanho fixo de 8x8 (64 valores booleanos). Ela ocupa um espaço minúsculo e constante na memória RAM, independentemente de existirem 10 ou 10.000 bolsas de sangue cadastradas no estoque do Hemocentro.
* **Complexidade de Espaço Total:** $O(1)$ (Espaço Constante).