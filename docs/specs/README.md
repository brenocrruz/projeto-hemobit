# Especificações (SDD)

Cada agregado do domínio tem uma especificação curta, escrita antes de mudar o código. A especificação descreve a linguagem do domínio, as invariantes, os comandos e os erros esperados. Cada cenário aponta para o teste que o verifica.

Fluxo de trabalho para qualquer regra nova:

1. Escrever ou alterar o cenário na especificação do agregado.
2. Escrever o teste de domínio que falha (vermelho).
3. Implementar o comportamento na entidade (verde).
4. Refatorar e só então ajustar o serviço, o controlador e a tela.

| Agregado | Especificação | Testes |
|---|---|---|
| Hemocomponente | [hemocomponente.md](./hemocomponente.md) | `HemocomponenteTest`, `HemocomponenteEstoqueServiceTest`, `HemocomponenteAcervoServiceTest` |
| Solicitação | [solicitacao.md](./solicitacao.md) | `SolicitacaoTest`, `StatusSolicitacaoTest`, `SolicitacaoServiceTest` |
