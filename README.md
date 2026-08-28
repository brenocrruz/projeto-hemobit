# 🩸 Hemobit

> **Uma plataforma facilitadora que conecta hospitais e centros de
> hemocomponentes, tornando a consulta, solicitação, distribuição e
> acompanhamento mais simples, rápida e eficiente.**

## 📌 Sobre o projeto

O **Hemobit** é uma plataforma facilitadora desenvolvida como parte do
**Projeto Integrador Interno 3 (PI3)** do 3º semestre de **Análise e
Desenvolvimento de Sistemas (ADS)**.

A proposta é simplificar a comunicação e a operação entre hospitais e
centros de hemocomponentes, centralizando informações de estoque,
requisições, compatibilidade, validade, distribuição e transporte.

## 💡 Proposta

O Hemobit concentra a complexidade operacional em uma plataforma,
enquanto oferece ao profissional uma interação simples por meio de um
**chatbot integrado à própria aplicação web**.

O chatbot não será um canal externo aberto: o acesso ocorre dentro da
aplicação, com autenticação, cadastro e permissões definidos pelo
sistema.

## 🩸 Hemocomponentes

A doação de sangue não se resume ao sangue total. Após a coleta, a bolsa
pode ser processada em diferentes componentes.

Para o MVP, o Hemobit considera:

-   Concentrado de Hemácias (CH)
-   Concentrado de Plaquetas (CP)
-   Plasma Fresco Congelado (PFC)

Outros componentes poderão ser incluídos conforme a evolução do escopo.
O sistema trata estoque e solicitações por tipo de hemocomponente.

## 🔄 Fluxo principal

**Necessidade → Chatbot → Solicitação → Consulta de estoque →
Compatibilidade didática → FEFO → Separação → Transporte → Monitoramento
→ Entrega → Dados/Estatística**

## ⚙️ Funcionalidades propostas

-   Cadastro e autenticação de usuários e hospitais.
-   Chatbot integrado à aplicação web.
-   Gestão de estoque por hemocomponente, tipo sanguíneo, quantidade,
    validade e status.
-   Registro e acompanhamento de requisições.
-   Verificação didática de compatibilidade ABO/Rh.
-   Priorização **FEFO (First Expire, First Out)**.
-   Roteirização por grafo.
-   Monitoramento simulado do transporte.
-   Dashboard operacional e indicadores estatísticos.
-   Alertas de estoque baixo, validade, atraso, rota e temperatura.

## 🚚 Monitoramento do transporte

Cada transporte possui um identificador e pode ser acompanhado pelo
dashboard. No protótipo, a telemetria será simulada.

-   📍 GPS/localização e progresso da rota.
-   🌡️ Temperatura e histórico de leituras.
-   ⏱️ Timestamp, tempo decorrido e previsão de chegada.
-   🚚 Status: aguardando coleta → preparação → em transporte → próximo
    do destino → entregue.
-   ⚠️ Alertas de atraso, desvio de rota, temperatura fora do limite
    configurado e falha de telemetria.

Os parâmetros de temperatura são configurações do cenário acadêmico. O
sistema não decide sozinho sobre utilização ou descarte de um
hemocomponente.

## 📊 Estatística

Os dados produzidos pelo Hemobit serão utilizados para transformar
informações operacionais em indicadores e análises:

-   Demanda por hemocomponente e período.
-   Tempo médio de atendimento e entrega.
-   Taxa de descarte e unidades próximas do vencimento.
-   Percentual de entregas dentro do prazo.
-   Desempenho e ocorrências dos transportes.
-   Risco/probabilidade estimada de desabastecimento.
-   Possíveis tendências de demanda.

## 🔄 CRISP-DM

O desenvolvimento orientado a dados será organizado pelo **CRISP-DM
(Cross-Industry Standard Process for Data Mining)**:

1.  **Business Understanding** --- entender o problema de comunicação,
    gestão e distribuição.
2.  **Data Understanding** --- compreender dados de estoque, demanda,
    requisições e transporte.
3.  **Data Preparation** --- preparar dados sintéticos e telemetria
    simulada.
4.  **Modeling** --- aplicar estatística, probabilidade,
    compatibilidade, FEFO e roteirização.
5.  **Evaluation** --- avaliar indicadores, rotas, alertas e cenários.
6.  **Deployment** --- disponibilizar aplicação web, chatbot e módulos
    de gestão/monitoramento.

## 📋 POP --- Procedimento Operacional Padrão

O fluxo operacional do funcionário é organizado em:

**Autenticar → Conferir solicitação → Consultar estoque → Verificar
compatibilidade didática → Aplicar FEFO → Separar → Preparar transporte
→ Iniciar telemetria → Monitorar → Registrar entrega → Encerrar
operação**

O POP do projeto detalha as etapas, exceções, alertas e dados gerados
pela operação.

## 🧩 Integração das disciplinas

  -----------------------------------------------------------------------
  Disciplina                          Participação
  ----------------------------------- -----------------------------------
  Programação Orientada a Objetos     Backend, aplicação web e regras de
                                      negócio

  Algoritmos e Estruturas de Dados    Grafos, rotas, estoque e FEFO

  Estatística e Probabilidade         Indicadores, demanda, risco e
                                      análise de transporte

  Infraestrutura de Software          Concorrência, CI/CD, deploy e
                                      operação

  Infraestrutura de Comunicação       APIs, comunicação e telemetria
                                      simulada

  Projeto Integrador                  Planejamento e integração
  -----------------------------------------------------------------------

## 🚀 MVP

-   [ ] Cadastro de usuários, hospitais e hemocomponentes
-   [ ] Estoque e validade
-   [ ] Chatbot dentro da aplicação
-   [ ] Requisições e acompanhamento
-   [ ] Compatibilidade ABO/Rh didática e FEFO
-   [ ] Grafo de rotas limitado
-   [ ] GPS, temperatura, timestamp e status simulados
-   [ ] Dashboard com indicadores e alertas

## ⚠️ Limites do projeto

-   Projeto acadêmico e didático.
-   Uso exclusivo de dados sintéticos.
-   Não diagnostica pacientes nem decide indicação de transfusão.
-   Compatibilidade ABO/Rh é didática.
-   GPS, temperatura e telemetria são simulados.
-   Não substitui protocolos clínicos ou procedimentos oficiais de
    hemoterapia.

## 👥 Equipe

  Membro
  ----------------------
  Davi Magno
  Breno Cruz
  Lucas Felipe Barreto
  Lucas Segundo
  Mateus Valerino
  Letícia Gomes
  Lauan Gonçalves

## 🎓 Informações acadêmicas

-   **Projeto:** Hemobit
-   **Projeto Integrador:** Interno 3 --- PI3
-   **Curso:** Análise e Desenvolvimento de Sistemas (ADS)
-   **Semestre:** 3º semestre
-   **Período:** 2026.2

## 🛠️ Tecnologias

A stack definitiva será definida pela equipe. A arquitetura prevista
contempla aplicação web, backend, banco de dados, APIs, chatbot,
estruturas de dados, infraestrutura, CI/CD e monitoramento.

## 📈 Roadmap

-   🟢 **Planejamento:** conceito, escopo e requisitos.
-   🟡 **Modelagem:** domínio, banco, APIs, dados sintéticos e
    indicadores.
-   🟠 **Desenvolvimento:** backend, banco, algoritmos, dashboard,
    chatbot e infraestrutura.
-   🔵 **Integração e testes:** módulos, algoritmos, comunicação e
    cenários.
-   🟣 **Deploy e apresentação:** CI/CD, deploy, documentação e
    demonstração.

------------------------------------------------------------------------

> **Hemobit --- Facilitando o caminho entre a necessidade e o
> hemocomponente.**
