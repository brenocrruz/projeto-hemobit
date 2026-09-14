# Estruturas de Dados Base (Módulo AED)

Este documento descreve as implementações de estruturas de dados básicas (feitas do zero, sem `java.util`) criadas para o domínio do Hemocentro. 

**Estruturas Implementadas**
* **Lista Encadeada (`ListaEncadeada`):** Gerencia o estoque de bolsas de sangue. Permite busca por índice e inserção dinâmica.
* **Fila (`Fila`):** Controla a entrada de requisições de hospitais utilizando a política FIFO (First-In, First-Out).
* **Pilha (`Pilha`):** Mantém o histórico de operações do gerenciador, seguindo a política LIFO (Last-In, First-Out).
* **Nodo (`Nodo`):** Classe base genérica responsável por encapsular os dados e manter os ponteiros de memória para as estruturas acima.

**Guia de Integração para a equipe de POO**

A classe `GerenciadorHemocentro` atua como a fachada principal. Como ela utiliza `Object` genérico, vocês podem injetar diretamente as classes de domínio criadas por vocês.

\`\`\`java
import com.hemobit.hemobit.aed.GerenciadorHemocentro;
import com.hemobit.hemobit.aed.estruturas.ListaEncadeada;

// Instanciar o gerenciador
GerenciadorHemocentro gerenciador = new GerenciadorHemocentro();

// 1. Adicionar ao estoque (Ex: após parse do estoque.json)
gerenciador.adicionarBolsa(meuObjetoBolsa);

// 2. Iterar sobre o estoque sem foreach
ListaEncadeada<Object> estoque = gerenciador.getEstoque();
for (int i = 0; i < estoque.tamanho(); i++) {
    Object item = estoque.obter(i);
    // Faça o cast para a sua entidade: (BolsaSangue) item;
}

// 3. Ciclo de requisições
gerenciador.enfileirarRequisicao(meuObjetoRequisicao);
Object proximaParaAtender = gerenciador.processarProximaRequisicao();

// 4. Ver auditoria
System.out.println(gerenciador.consultarUltimaOperacao());
\`\`\`