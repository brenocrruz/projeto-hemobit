# Planejamento da refatoração (DDD, camadas, TDD e SDD)

Este documento responde aos comentários do professor para a entrega de 26/11 e define o que entra na entrega final de 01/12.

## 1. Diagnóstico

O comentário foi: ainda há entidades anêmicas (só `get` e `set`), e as regras estão fora do domínio. A medição no código confirmou, e as regras ficavam nos serviços.

| Entidade | Antes (getters e setters de 22 métodos públicos) | Depois |
|---|---|---|
| `Hemocomponente` | 9 setters, regras no `HemocomponenteService` | 0 setters, 8 métodos de comportamento |
| `Solicitacao` | 8 setters, regras no `SolicitacaoService` | 0 setters, 5 métodos de comportamento |

As demais entidades (`Doacao`, `Doador`, `Rota`, `Veiculo`, `Localidade`, `UnidadeSaude`, `UnidadeColeta`, `ProfissionalSolicitante`, `HistoricoSolicitacao`) continuam com quase só `get` e `set`. Elas entram no plano abaixo.

## 2. DDD e modelagem rica

**Linguagem ubíqua** (usada nos nomes dos métodos e nos testes): bolsa, código, disponível, reservar, vencida, solicitação, atender, FEFO, compatibilidade ABO/Rh, finalizada.

**O que já mudou** (detalhes em [docs/specs](../specs/README.md)):

- `Hemocomponente.registrar` cria a bolsa já válida: código normalizado, datas coerentes, quantidade positiva e validade que não passou.
- `Hemocomponente.corrigirDados`, `reservarPara`, `marcarComoVencida`, `estaVencida` e `estaDisponivel` concentram as regras da bolsa.
- `Solicitacao.abrir`, `alterarPedido`, `mudarStatus` e `atender` concentram as regras da solicitação. O ciclo de vida de status mora em `StatusSolicitacao.podeIrPara`.
- `Solicitacao.atender` é atômica: se o estoque compatível não basta, nenhuma bolsa é reservada.
- Três objetos de valor imutáveis (`record`) concentram regras que antes eram números e textos soltos: `CodigoBolsa` (normaliza e valida o código), `QuantidadeDeBolsas` (inteira, de 1 a 50) e `PeriodoDeValidade` (produção e validade coerentes, e se está vencido em uma data). Eles não são persistidos: as entidades continuam guardando os mesmos campos e usam os objetos para validar, o que mantém o esquema do banco intacto.
- Não há mais setters públicos nesses dois agregados. O estado só muda por comandos que protegem as invariantes.
- Os serviços viraram orquestradores: buscam no repositório, chamam o domínio e gravam o histórico.

**Decisões conscientes:**

- O construtor público de `Hemocomponente` e de `Solicitacao` continua existindo, porque o JPA e os testes reconstroem estados que a fábrica recusaria (por exemplo, uma bolsa já vencida). A criação pelo sistema passa sempre por `registrar` e `abrir`.
- A regra de compatibilidade ABO/Rh continua na matriz do módulo de AED (`MatrizCompatibilidade`). O domínio recebe essa regra como parâmetro em `atender`, para não duplicar a matriz.

## 3. Arquitetura em camadas e Clean Architecture

**Hoje:** `controller → service → repository → domain`, com `dto` na borda. A regra de dependência já aponta para dentro (o domínio não conhece controladores nem serviços).

**Dívida conhecida:**

- As entidades têm anotações JPA, então o domínio depende do framework de persistência.
- Os serviços usam os repositórios do Spring Data diretamente.

**Alvo (Clean Architecture):**

| Camada | Conteúdo | Pode depender de |
|---|---|---|
| Domínio | Entidades, enums, regras, exceções de negócio | Nada além do Java |
| Aplicação | Casos de uso (hoje os `*Service`) e as portas (interfaces dos repositórios) | Domínio |
| Infraestrutura | Adaptadores JPA, controladores REST, DTOs, telemetria | Aplicação e domínio |

**Passos:**

1. Concluído: regras no domínio e serviços finos.
2. Até 01/12: definir as portas dos agregados `Hemocomponente` e `Solicitacao` na camada de aplicação, com o Spring Data como adaptador.
3. Até 01/12: adicionar um teste de arquitetura (ArchUnit) que falha se o domínio importar `service`, `controller` ou `dto`.
4. Depois: avaliar separar o modelo de persistência do modelo de domínio. É um custo alto para o ganho no tamanho atual do projeto.

## 4. O que vai mudar efetivamente

| Item | Situação | Prazo |
|---|---|---|
| `Hemocomponente` e `Solicitacao` com domínio rico e testes de domínio | Feito | 26/11 |
| Objetos de valor `CodigoBolsa`, `QuantidadeDeBolsas` e `PeriodoDeValidade`, com testes | Feito | 26/11 |
| Cenários Gherkin executando com Cucumber (`src/test/resources/features/atendimento-solicitacao.feature`) dentro do `mvn verify` e do CI | Feito | 26/11 |
| Cobertura de testes medida e publicada pelo CI (JaCoCo) | Feito | 26/11 |
| Especificações SDD dos dois agregados | Feito | 26/11 |
| `Rota` com invariantes no domínio (origem diferente do destino, distância e tempo positivos), hoje espalhadas em `RotaService` e `RotaApiController` | A fazer | 01/12 |
| `Veiculo` com transições de `StatusVeiculo` no domínio | A fazer | 01/12 |
| Pacotes por contexto (`estoque`, `atendimento`, `logistica`), portas para `Hemocomponente` e `Solicitacao` e teste ArchUnit | A fazer | 01/12 |
| Rotina que marca bolsas vencidas usando `marcarComoVencida` (issue aberta) | A fazer | 01/12 |
| `Doacao` e `Doador` com regras de domínio | A fazer | Depois |

## 5. TDD: cobertura atual e planejada

**Como medir:** o CI roda `mvn clean verify` e publica a cobertura no resumo do job e no artefato `relatorio-jacoco`. Localmente: `.\mvnw.cmd verify` e abrir `target/site/jacoco/index.html`.

| Indicador | Valor |
|---|---|
| Testes automatizados antes desta refatoração | 104 |
| Testes automatizados agora | 145 (41 novos: 38 de domínio e objetos de valor e 3 cenários Cucumber) |
| Cobertura de linhas atual | Preencher com o valor do primeiro CI após o merge: ______ % |
| Meta de cobertura de linhas para 01/12 | A cobertura atual mais 10 pontos percentuais |
| Meta para as camadas de domínio e de serviço | 80 % das linhas |

A meta mínima é aplicada pelo pipeline: a variável `COBERTURA_MINIMA_LINHAS` no `.github/workflows/ci.yml` começa em 0 e deve ser elevada, por pull request, para o valor atual assim que ele for medido. A partir daí o CI falha se a cobertura cair.

**Prática de TDD nesta refatoração:** o comportamento de cada agregado foi descrito primeiro como cenário na especificação e verificado por testes de domínio puros (sem Spring e sem banco), que rodam em milissegundos. Um teste antigo (`SolicitacaoServiceTest`) apontou uma regra que a refatoração tinha deixado cair (máximo de 50 bolsas por solicitação), e ela voltou para o domínio.

**BDD:** os cenários do atendimento de solicitação rodam como Cucumber (`ExecutarCenariosBddTest`) contra o domínio e a matriz de compatibilidade reais, sem Spring nem banco. Para rodar só eles: `.\mvnw.cmd test -Dtest=ExecutarCenariosBddTest`.

## 6. SDD

As especificações ficam em [docs/specs](../specs/README.md), uma por agregado, com linguagem do domínio, invariantes, comandos, erros e cenários Gherkin que apontam para o teste que os verifica. Regra de trabalho: toda regra nova entra primeiro na especificação, depois no teste e só então no código.

A IA foi usada para ajudar a levantar as invariantes a partir do código existente, propor a divisão de responsabilidades entre domínio e serviço e escrever os testes. Todas as mudanças foram revisadas e executadas pelo grupo.

## 7. Esteira de CI/CD

```
pull request → build (verify: testes + cobertura + meta mínima) → docker (build da imagem de produção)
merge na main → Render (build do Dockerfile e deploy)
```

- O check `build` é obrigatório para o merge na `main`.
- O job `docker` garante que a imagem usada no deploy continua construindo.
- O relatório de cobertura fica salvo em cada execução.

## 8. Riscos

- Mexer em entidades compartilhadas pode quebrar o código de outros módulos. Por isso a refatoração foi feita por agregado, mantendo os contratos HTTP e as mensagens de erro, e com a suíte completa no CI.
- A meta de cobertura só passa a valer depois que o primeiro valor for medido.
