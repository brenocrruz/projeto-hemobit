# 🩸 Hemobit

> **Uma plataforma facilitadora que conecta hospitais e centros de hemocomponentes, tornando a consulta, solicitação e distribuição mais simples, rápida e eficiente.**

## 📌 Sobre o projeto

O **Hemobit** é um projeto desenvolvido como parte do **Projeto Integrador Interno 3 (PI3)** do curso de **Análise e Desenvolvimento de Sistemas (ADS)**.

A proposta surgiu a partir do desafio de tornar mais simples e eficiente a comunicação entre **hospitais, profissionais solicitantes e centros de hemocomponentes**, facilitando o acesso às informações necessárias para consulta, solicitação e acompanhamento de hemocomponentes.

Mais do que um sistema de gerenciamento de estoque, o Hemobit busca atuar como uma **plataforma facilitadora**, utilizando tecnologia, dados e estatística para reduzir a complexidade do processo e apoiar a tomada de decisões.

---

## 🎯 Objetivo

O principal objetivo do Hemobit é **facilitar e agilizar o processo de consulta, solicitação e distribuição de hemocomponentes**.

A plataforma busca centralizar e integrar informações relacionadas a:

* 🩸 Estoque de hemocomponentes;
* 🏥 Solicitações hospitalares;
* 🧬 Compatibilidade ABO/Rh;
* 📦 Priorização de bolsas por validade (FEFO);
* 🚚 Rotas de distribuição;
* ⏱️ Tempo estimado de atendimento e entrega;
* 📊 Demanda e indicadores estatísticos;
* ⚠️ Identificação de possíveis riscos de desabastecimento;
* 📈 Monitoramento da operação.

---

## 💡 A ideia

A principal característica do Hemobit é ser uma **facilitadora do processo**.

Um profissional de saúde poderia utilizar uma interface simples, inicialmente pensada como uma **interface conversacional via WhatsApp**, para consultar informações e iniciar uma solicitação.

Por exemplo:

```text
Médico:
Preciso de 3 bolsas de concentrado de hemácias O+.
Tem disponível?

Hemobit:
🔎 Consultando disponibilidade...

🩸 Concentrado de hemácias O+
📦 Disponíveis: 7 bolsas
📋 Solicitadas: 3 bolsas
⏱️ Tempo estimado: 35 minutos

Deseja iniciar a solicitação?
```

A interface conversacional seria a **porta de entrada** para o usuário, enquanto toda a lógica de gestão, análise e integração ficaria concentrada na plataforma.

> **A experiência para o usuário deve ser simples; a complexidade fica por trás do Hemobit.**

---

## 🔄 Fluxo conceitual

O funcionamento esperado do projeto pode ser representado da seguinte maneira:

```text
                    NECESSIDADE
                         │
                         ▼
                 💬 SOLICITAÇÃO
                         │
                         ▼
                  🔎 CONSULTA
                         │
             ┌───────────┴───────────┐
             ▼                       ▼
        DISPONÍVEL?              INDISPONÍVEL
             │                       │
             ▼                       ▼
       COMPATIBILIDADE          ALERTA / ALTERNATIVA
             │
             ▼
            FEFO
             │
             ▼
           ROTA
             │
             ▼
        DISTRIBUIÇÃO
             │
             ▼
        ACOMPANHAMENTO
             │
             ▼
            DADOS
             │
             ▼
       📊 ESTATÍSTICA
             │
             ▼
       MELHORES DECISÕES
```

---

## 🧩 Principais funcionalidades propostas

### 💬 Interface facilitadora

* Consulta de disponibilidade;
* Início de solicitações;
* Acompanhamento de solicitações;
* Identificação do profissional previamente cadastrado;
* Comunicação de status.

### 🩸 Gestão de estoque

* Cadastro de hemocomponentes;
* Tipo sanguíneo;
* Quantidade disponível;
* Data de validade;
* Controle de estoque;
* Alertas de estoque baixo.

### 📋 Requisições

* Registro de solicitações;
* Identificação do hospital;
* Identificação do profissional solicitante;
* Quantidade solicitada;
* Hemocomponente solicitado;
* Acompanhamento do status;
* Histórico de solicitações.

### 🧬 Compatibilidade e priorização

* Verificação didática de compatibilidade ABO/Rh;
* Priorização de bolsas por validade;
* Aplicação do conceito **FEFO (First Expire, First Out)**.

### 🚚 Distribuição

* Representação dos hospitais e centros como nós de um grafo;
* Cálculo de rotas;
* Estimativa de tempo;
* Acompanhamento da distribuição;
* Simulação de telemetria.

### 📊 Estatística e indicadores

Os dados gerados pela operação poderão ser utilizados para:

* Análise de estoque;
* Análise de demanda;
* Tempo médio de atendimento;
* Taxa de descarte por vencimento;
* Probabilidade de desabastecimento;
* Identificação de tendências;
* Apoio à tomada de decisões.

---

# 📊 Estatística no Hemobit

A Estatística não será utilizada apenas para gerar gráficos.

A proposta é utilizar os dados produzidos pelo sistema para **transformar informações operacionais em indicadores capazes de apoiar decisões**.

Por exemplo:

> O Hemobit identifica que determinado hemocomponente apresenta estoque reduzido e demanda elevada.

A partir do histórico de dados, o sistema pode calcular uma estimativa de risco de desabastecimento e apresentar um alerta.

### Exemplos de perguntas que os dados podem responder

* Qual hemocomponente possui maior demanda?
* Qual hospital realiza mais solicitações?
* Qual é o tempo médio de atendimento?
* Quantas bolsas são descartadas por vencimento?
* Existe tendência de aumento da demanda?
* Qual a probabilidade de desabastecimento de determinado tipo?

Os dados utilizados no projeto serão **sintéticos**, conforme as diretrizes do Projeto Integrador.

---

# 🔄 CRISP-DM

O desenvolvimento orientado a dados do Hemobit será estruturado utilizando o **CRISP-DM (Cross-Industry Standard Process for Data Mining)**.

O CRISP-DM é uma metodologia que organiza projetos de dados em um ciclo composto por seis etapas.

## 1. Business Understanding — Entendimento do negócio

Identificar o problema e os objetivos do Hemobit.

**Problema:** tornar mais simples, rápido e eficiente o processo de consulta, solicitação e distribuição de hemocomponentes.

---

## 2. Data Understanding — Entendimento dos dados

Identificar e compreender os dados necessários para o projeto.

Exemplos:

* Estoque;
* Demanda;
* Solicitações;
* Validade;
* Hospitais;
* Tempos de atendimento;
* Distribuição.

---

## 3. Data Preparation — Preparação dos dados

Organizar, estruturar e preparar os dados sintéticos que serão utilizados pelo sistema e pelas análises estatísticas.

---

## 4. Modeling — Modelagem

Aplicar métodos, algoritmos e análises para solucionar os problemas identificados.

No Hemobit:

* Estatística descritiva;
* Probabilidade;
* Compatibilidade ABO/Rh;
* FEFO;
* Grafos;
* Caminho mínimo;
* Roteirização.

---

## 5. Evaluation — Avaliação

Verificar se as soluções desenvolvidas realmente atendem aos objetivos do projeto.

Serão considerados cenários simulados para avaliar, por exemplo:

* Disponibilidade de estoque;
* Risco de desabastecimento;
* Eficiência das rotas;
* Priorização de bolsas;
* Tempo de atendimento;
* Indicadores estatísticos.

---

## 6. Deployment — Implantação

Disponibilizar a solução desenvolvida, considerando a aplicação web, a interface conversacional e a infraestrutura necessária para sua execução.

O CRISP-DM será tratado como um **ciclo**, permitindo que resultados das avaliações levem a novos ajustes nos dados, modelos ou objetivos.

---

# 🏗️ Integração das disciplinas

O Hemobit foi pensado para integrar as diferentes disciplinas do **Projeto Integrador Interno 3**.

| Disciplina                           | Participação no Hemobit                                                                    |
| ------------------------------------ | ------------------------------------------------------------------------------------------ |
| **Programação Orientada a Objetos**  | Desenvolvimento da aplicação web em Java/Spring Boot e implementação das regras de negócio |
| **Algoritmos e Estruturas de Dados** | Grafos, caminhos mínimos, compatibilidade, estoque e FEFO                                  |
| **Estatística e Probabilidade**      | Indicadores, análise descritiva, probabilidade de desabastecimento e análise da demanda    |
| **Infraestrutura de Software**       | Concorrência, CI/CD, deploy e operação da aplicação                                        |
| **Infraestrutura de Comunicação**    | APIs, arquitetura de comunicação, rede e telemetria                                        |
| **Projeto Integrador**               | Organização, planejamento, acompanhamento e integração das entregas                        |

---

# 🚀 MVP

Para manter o projeto dentro de um escopo controlado, a primeira versão do Hemobit deverá priorizar:

* [ ] Cadastro de profissionais;
* [ ] Cadastro de hospitais;
* [ ] Cadastro e gerenciamento de estoque;
* [ ] Consulta de disponibilidade;
* [ ] Registro de requisições;
* [ ] Compatibilidade ABO/Rh didática;
* [ ] Priorização FEFO;
* [ ] Grafo de rotas limitado;
* [ ] Dashboard com indicadores;
* [ ] Dados sintéticos;
* [ ] Protótipo de interface conversacional.

Funcionalidades mais avançadas poderão ser avaliadas posteriormente conforme o andamento do projeto e as necessidades das disciplinas.

---

# ⚠️ Limites do projeto

O Hemobit é um **projeto acadêmico e didático**.

Portanto:

* Utilizaremos exclusivamente **dados sintéticos**;
* Não serão utilizados dados reais de pacientes ou doadores;
* A compatibilidade ABO/Rh será implementada de forma didática;
* O sistema não realizará diagnóstico médico;
* O sistema não decidirá se um paciente deve receber uma transfusão;
* O sistema não substituirá protocolos clínicos;
* A telemetria de temperatura/GPS será simulada;
* O projeto não representa uma integração real com sistemas oficiais da rede de sangue.

---

# 👥 Equipe

| Membro                   |
| ------------------------ |
| **Davi Magno**           |
| **Breno Cruz**           |
| **Lucas Felipe Barreto** |
| **Lucas Segundo**        |
| **Mateus Valerino**      |
| **Letícia Gomes**        |
| **Lauan Gonçalves**      |

---

# 🎓 Informações acadêmicas

**Projeto:** Hemobit
**Projeto Integrador:** Interno 3 — PI3
**Curso:** Análise e Desenvolvimento de Sistemas (ADS)
**Semestre:** 3º semestre
**Período:** 2026.2

O projeto integra conteúdos de diferentes disciplinas técnicas do período, buscando desenvolver uma solução única a partir da colaboração entre as áreas.

---

# 🛠️ Tecnologias

As tecnologias serão definidas e implementadas ao longo do desenvolvimento do projeto.

A arquitetura prevista deverá contemplar, conforme as necessidades das disciplinas:

* **Java / Spring Boot**
* **APIs**
* **Banco de dados**
* **Aplicação Web**
* **Interface conversacional**
* **Estruturas de dados e algoritmos**
* **Infraestrutura em nuvem**
* **CI/CD**
* **Monitoramento**

> A stack definitiva será documentada conforme as decisões técnicas da equipe.

---

# 📈 Roadmap

### 🟢 Fase 1 — Planejamento

* [x] Definição inicial do conceito;
* [x] Definição do nome Hemobit;
* [x] Criação do repositório;
* [ ] Definição do escopo;
* [ ] Levantamento de requisitos.

### 🟡 Fase 2 — Modelagem

* [ ] Modelagem do domínio;
* [ ] Modelagem do banco de dados;
* [ ] Definição das APIs;
* [ ] Definição dos dados sintéticos;
* [ ] Definição dos indicadores estatísticos.

### 🟠 Fase 3 — Desenvolvimento

* [ ] Backend;
* [ ] Banco de dados;
* [ ] Algoritmos;
* [ ] Dashboard;
* [ ] Interface conversacional;
* [ ] Infraestrutura.

### 🔵 Fase 4 — Integração e testes

* [ ] Integração entre os módulos;
* [ ] Testes dos algoritmos;
* [ ] Testes estatísticos;
* [ ] Testes de comunicação;
* [ ] Testes de cenários.

### 🟣 Fase 5 — Deploy e apresentação

* [ ] CI/CD;
* [ ] Deploy;
* [ ] Documentação;
* [ ] Demonstração;
* [ ] Apresentação final.

---

# 📚 Referências do projeto

O Hemobit é desenvolvido a partir das diretrizes e do escopo definidos para o **Projeto Integrador Interno 3 — 3º semestre de ADS, 2026.2**.

O projeto-base prevê uma solução de distribuição de hemocomponentes envolvendo estoque, requisições hospitalares, compatibilidade, validade, roteirização, indicadores estatísticos, probabilidade, infraestrutura e comunicação.

---

## 🩸 Hemobit

> **Facilitando o caminho entre a necessidade e o hemocomponente.**

**Projeto Integrador Interno 3 — ADS 2026.2**
