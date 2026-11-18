# Roteiro de demonstração ao vivo para a banca (U2, POO)

Duração prevista: 10 a 12 minutos. A jornada percorre o sistema de ponta a ponta, na ordem do card: entrada de bolsas, consulta, solicitação, atendimento com FEFO e ABO/Rh, rotas, estatística e rede. A demonstração usa o sistema em produção: https://hemobit.onrender.com/

## 1. Antes de entrar na sala

1. Cinco minutos antes, abra https://hemobit.onrender.com/ e espere carregar. O plano gratuito do Render hiberna e a primeira abertura demora. Deixe a aba aberta.
2. Abra as telas do menu uma vez cada (Visão geral, Estoque sanguíneo, Solicitações, Rotas de entrega, Painel estatístico, Rede e telemetria) e confirme que nenhuma mostra erro.
3. Confira que existem, nesta ordem, os cadastros abaixo. Se faltar algum, cadastre pelo menu com dados fictícios (Unidades de coleta, Doadores, Doações, Unidades de saúde):
   - uma unidade de coleta;
   - um doador O- e um doador O+, cada um com uma doação registrada;
   - duas unidades de saúde, por exemplo "Hospital da Restauração" e "Hospital São Marcos".
4. Abra em outra aba o repositório no GitHub (README, aba Actions com o `build` verde e aba Issues) e deixe um terminal com `.\mvnw.cmd clean test` pronto.
5. Plano B, se o Render não responder: rode a aplicação local (`.\mvnw.cmd spring-boot:run`, porta 8080) e use http://localhost:8080 com o mesmo roteiro.

## 2. Abertura (1 min)

1. Em **Visão geral**, diga em uma frase o problema: hemocentros precisam saber o que há em estoque, atender hospitais com a bolsa certa e levá-la no prazo.
2. Diga que vai percorrer a jornada de um hospital pedindo sangue, do estoque até a entrega.

## 3. HU04: entrada de bolsas (2 min)

1. Abra **Estoque sanguíneo** e role até "Novo hemocomponente".
2. Preencha: código `DEMO-001`, tipo "Concentrado de hemácias", quantidade 300, produção hoje, validade daqui a 7 dias, doação do doador O-. Cadastre.
3. Mostre a mensagem de sucesso e a bolsa na tabela com status "Disponível".
4. Cadastre uma segunda bolsa, `DEMO-002`, mesmo tipo, validade daqui a 20 dias, doação do doador O+.
5. Mostre as duas regras de erro:
   - repita o código `DEMO-001` e mostre o erro de código já cadastrado (HTTP 409 na API);
   - informe validade de ontem e mostre "Não é permitido cadastrar bolsas com data de validade vencida!".

## 4. HU05: consulta com filtros (1 min)

1. Na tabela, filtre por grupo sanguíneo O- e depois por status "Disponível" combinado com o tipo "Concentrado de hemácias". A lista mostra só as bolsas que atendem a todos os filtros, ordenadas pela validade.
2. Escolha um filtro sem resultado (por exemplo AB-) e mostre a mensagem "Nenhum hemocomponente encontrado para os filtros selecionados.".
3. Limpe os filtros.

## 5. HU06: solicitação do hospital (2 min)

1. No menu, abra **Solicitações**. Explique que a tela lista os pedidos e tem o formulário de nova solicitação.
2. Em "Registrar solicitação", escolha "Hospital São Marcos", "Concentrado de hemácias", paciente **O+**, quantidade 1, prioridade "Urgente". Registre.
3. Mostre a solicitação na tabela com status "Pendente".
4. Mostre a regra de erro: tente quantidade 0 e leia a mensagem de quantidade mínima.

## 6. HU07: atendimento com FEFO e ABO/Rh (3 min)

1. Preencha o campo "Operador responsável" com seu nome. Explique que ele vai para o histórico.
2. Clique em **Separar**. O status vira "Em separação".
3. Clique em **Atender**. O sistema reserva a bolsa compatível que vence primeiro.
4. Clique em **Detalhes** e mostre:
   - o histórico: Criada → Pendente → Em separação → Atendida, com operador e horário;
   - a bolsa reservada: `DEMO-001` (O-, a que vence primeiro). Explique FEFO (first expired, first out) e que O- é doador universal, compatível com paciente O+ pela matriz ABO/Rh.
5. Volte ao **Estoque sanguíneo** e mostre que `DEMO-001` agora está "Reservado".
6. Registre uma nova solicitação para o mesmo hospital pedindo uma quantidade maior que o estoque compatível, clique em Atender e mostre a mensagem de estoque compatível insuficiente. A solicitação continua pendente e nenhuma bolsa é reservada.

## 7. Visualização de rotas (1 min)

1. Abra **Rotas de entrega**.
2. Cadastre uma rota: origem "Hemocentro" (ou a unidade de coleta), destino "Hospital São Marcos", distância 12,5 km, tempo 25 minutos.
3. Cadastre uma segunda rota para "Hospital da Restauração", 30 km e 75 minutos.
4. Mostre o resumo (quantidade de rotas, distância total, rota mais rápida) e as barras proporcionais à distância.
5. Tente origem igual ao destino e mostre o erro.

## 8. Estatística e rede (1 min)

1. Abra **Painel estatístico**. Mostre a análise probabilística (probabilidade de desabastecimento e taxa de descarte) e os indicadores descritivos (estoque por tipo sanguíneo, demanda por hospital, tempos de atendimento). Quem apresenta a parte de estatística explica a origem dos dados: os indicadores descritivos atuais usam valores de exemplo, e o endpoint `/api/indicadores/estoque-real` calcula o estoque disponível a partir das bolsas cadastradas. Não diga que o gráfico reflete as bolsas que você acabou de cadastrar sem antes conferir isso no painel.
2. Abra **Rede e telemetria**. Mostre a latência da telemetria, o diagnóstico do canal e as rotas da API (requisições, p95, erros 4xx e 5xx). Aproveite para mostrar que os erros de validação que você provocou aparecem como 4xx e que não há 5xx.

## 9. Qualidade de código e testes (1 min)

1. No GitHub, abra a aba Actions e mostre o `build` verde da `main`.
2. No terminal, rode `.\mvnw.cmd clean test` e mostre o resultado com todos os testes passando.
3. Cite em uma frase cada: testes unitários de serviço (regras de negócio isoladas), testes de controlador (contratos HTTP e mensagens de erro), regras separadas em camadas (controller, service, repository, domain, dto) e nomes de métodos que descrevem o comportamento.
4. Mostre o README: histórias implementadas em formato POST-IT, links dos screencasts, issues e pull requests com revisão.

## 10. Fechamento (30 s)

1. Resuma a jornada: bolsa entra, é consultada, hospital pede, sistema reserva a bolsa compatível que vence primeiro, rota leva até o hospital, estatística e rede monitoram.
2. Diga uma limitação conhecida: bolsas vencidas ainda não mudam sozinhas para o status "Vencido" (issue aberta), embora sejam ignoradas no resumo do estoque e no atendimento.

## Perguntas prováveis

- **Por que FEFO?** Para reduzir o descarte: a bolsa que vence primeiro sai primeiro.
- **Como a compatibilidade é decidida?** Pela matriz ABO/Rh do serviço de compatibilidade; o atendimento só considera bolsas compatíveis com o grupo do paciente.
- **O que acontece se não houver estoque?** O atendimento falha com mensagem clara (HTTP 409), a solicitação continua pendente e nada é reservado.
- **Onde fica o banco?** Em nuvem, ligado ao Render por variáveis de ambiente (`SPRING_DATASOURCE_URL` e outras), sem senha no código.
