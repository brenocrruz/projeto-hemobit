## Uso de IA

**Ferramenta:** Claude (Anthropic), usado como assistente de código e de redação. Os dados usados são apenas os dados sintéticos do Hemobit. A IA não foi usada como fonte de preços: todo valor do orçamento foi lido na página de preços da Render e conferido pelo squad.

| Parte | Finalidade do uso da IA | Como foi validada |
|---|---|---|
| A. Domínio com DDD | Rascunhar a classificação dos subdomínios, os contextos, o glossário e o mapa de contextos a partir das classes e do backlog do projeto. | O squad comparou cada termo e cada contexto com as classes do repositório e com o backlog, e ajustou o que não correspondia ao código. |
| B. Modelo tático | Levantar as invariantes da `Solicitacao` a partir do código e redigir a história âncora e o cenário Gherkin. | As invariantes foram conferidas contra os métodos da entidade. O cenário Gherkin executa com Cucumber e os testes de domínio rodam na esteira de CI, que passou antes do merge. |
| C. Três camadas | Propor a divisão em camadas, a estrutura de pacotes por contexto e a distinção entre camada lógica e tier físico. | Revisão do squad contra os pacotes reais e contra o `compose.yaml` e o deploy no Render. Os desvios conhecidos (anotações JPA no domínio) foram declarados no texto. |
| D. Microsserviços | Esboçar a decomposição, a comunicação, a saga e os dois ADRs. | Revisão do squad: a saga foi checada contra o ciclo de vida de `StatusSolicitacao` (a compensação usa uma transição já permitida) e o ADR 0002 foi discutido e aceito pelo grupo. |
| E. Cenários e orçamento | Montar a planilha com fórmulas, a conta da Lei de Little e as tabelas de custo. | A planilha foi recalculada sem erros de fórmula. A conta do cenário alto foi refeita à mão. Os preços foram conferidos pelo squad em https://render.com/pricing. As premissas de carga são do squad e estão marcadas como tais. |
| Código da Parte 3 | Escrever a refatoração do domínio (`Hemocomponente` e `Solicitacao`), os objetos de valor, os testes e a configuração do Cucumber e do JaCoCo. | Testes automatizados na esteira de CI (`mvn verify`), cobertura medida pelo JaCoCo, revisão no pull request e execução local pelo squad antes do merge. |

**Declaração:** todo conteúdo produzido com auxílio de IA foi lido, executado ou conferido pelo squad antes de entrar neste documento ou na branch `main`.
