# ADR 0002: Monólito modular na implementação da Parte 3

Status: proposto

## Contexto

A decomposição do ADR 0001 é uma projeção. O squad tem sete integrantes, prazo até 01/12, e o deploy atual é uma única imagem Docker na PaaS. O domínio core (Requisição e Estoque) já tem agregados ricos, objetos de valor e testes. Ainda não há evidência de carga que justifique operar vários serviços, banco por serviço e broker.

## Decisão

Implementar a Parte 3 como **monólito modular**: pacotes por contexto e por camada, com a regra de dependência verificada por teste de arquitetura (ArchUnit), e a comunicação entre contextos por chamadas de aplicação (e, depois, por eventos de domínio em processo). O monólito continua sendo uma única unidade de deploy, e os microsserviços ficam como evolução quando a carga justificar.

## Consequências

- **Melhora:** transação única e consistência forte no atendimento; um pipeline e um deploy; menor custo e menor risco de falha de rede no prazo da entrega; as fronteiras por contexto deixam a extração futura de serviços mais barata.
- **Piora:** todo o sistema escala junto, e uma falha em um módulo pode afetar a instância inteira; a disciplina de fronteiras depende dos testes de arquitetura, e não da separação física.
- **Alternativa descartada:** implementar já Requisição e Estoque como dois serviços com broker. O ganho de escala não aparece na carga atual, e a complexidade de saga, outbox e operação em duas semanas colocaria em risco a entrega.
