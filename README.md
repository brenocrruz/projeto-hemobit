# 🩸 Hemobit

### Gestão e Distribuição Inteligente de Hemocomponentes

[![CI/CD Pipeline](https://github.com/brenocrruz/projeto-hemobit/actions/workflows/ci.yml/badge.svg)](https://github.com/brenocrruz/projeto-hemobit/actions/workflows/ci.yml)
[![Deploy](https://img.shields.io/badge/deploy-online-brightgreen)](https://projeto-hemobit.onrender.com/)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)

Projeto desenvolvido para o **Projeto Integrador III — ADS 2026.2**, da **CESAR School**, integrando as disciplinas de **Programação Orientada a Objetos**, **Algoritmos e Estruturas de Dados**, **Estatística e Probabilidade**, **Infraestrutura de Software** e **Infraestrutura de Comunicação**.

---

## 📖 Sobre o Projeto

### O Problema

A rede de sangue precisa garantir o **componente certo** (compatível e dentro da validade), no **lugar certo**, no **tempo certo** e na **temperatura certa**. Falhas nesse processo geram:

- Desabastecimento de hemocomponentes.
- Descarte de bolsas por vencimento.
- Risco à vida do paciente.

Falta uma plataforma que integre **estoque**, **compatibilidade**, **roteirização** e **monitoramento da cadeia fria**.

### A Solução

O **Hemobit** é uma aplicação web que funciona como uma camada facilitadora entre o **profissional solicitante** (hospital) e a **operação do centro de hemocomponentes** (hemocentro). O sistema organiza o fluxo operacional e torna as informações mais acessíveis e rastreáveis, **sem substituir decisões clínicas**.

### Funcionalidades do MVP

- Cadastro e autenticação de usuários e hospitais.
- Cadastro e gestão de hemocomponentes (CH, CP, PFC).
- Controle de estoque e validade (FEFO).
- Chatbot dentro da aplicação web para solicitações.
- Criação e acompanhamento de requisições.
- Compatibilidade ABO/Rh didática.
- Grafo de rotas limitado com cálculo de caminho mínimo.
- Telemetria simulada (GPS, temperatura, timestamps).
- Dashboard operacional com indicadores estatísticos.
- Alertas de estoque, validade, atraso, desvio de rota e temperatura.

### Público-Alvo

| Perfil | Responsabilidades |
|--------|-------------------|
| **Profissional solicitante** | Usa o chatbot para consultar, iniciar e acompanhar solicitações. |
| **Funcionário do centro** | Analisa solicitações, consulta estoque, separa unidades, prepara e acompanha transportes. |
| **Gestor** | Acompanha indicadores, alertas, estoque e desempenho operacional. |

### Limites e Premissas

> ⚠️ **Projeto acadêmico e didático.** Uso exclusivo de **dados sintéticos** (LGPD). A compatibilidade ABO/Rh é **didática** e não substitui protocolos clínicos. GPS, temperatura e telemetria são **simulados**. O sistema não decide indicação de transfusão.

---

## 🏗️ Arquitetura

A aplicação segue uma arquitetura em camadas:

```
Usuário → Web/Chatbot → API REST → Regras de Negócio → Banco de Dados → Monitoramento → Dashboard/Estatística
```

### Estrutura do Repositório

```
projeto-hemobit/
├── src/main/java/com/hemobit/hemobit/
│   ├── aed/              # Algoritmos e Estruturas de Dados
│   ├── controller/       # Endpoints REST
│   ├── domain/           # Entidades JPA
│   ├── repository/       # Repositórios Spring Data
│   └── service/          # Regras de negócio
├── src/main/resources/
├── src/test/             # Testes automatizados
├── docs/
│   ├── historias_de_usuario.md
│   ├── poo/              # Documentação de POO
│   ├── aed/              # Documentação de AED
│   └── rsd/              # Documentação de RSD
├── data/                 # Dados sintéticos
├── .github/workflows/    # Pipeline CI/CD
└── Dockerfile
```

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.x |
| Persistência | Spring Data JPA + PostgreSQL |
| Frontend | HTML / CSS / Thymeleaf |
| Build | Maven |
| CI/CD | GitHub Actions + Render |
| Containerização | Docker |
| Ferramentas | Git, GitHub, Jira, Figma, draw.io |

> **Restrição:** Não é permitido o uso de Lombok ou qualquer mecanismo de geração automática de código boilerplate.

---

## 👥 Equipe

| Integrante | E-mail CESAR School |
|------------|---------------------|
| Breno Luiz de Lima Cruz | [bllc@cesar.school](mailto:bllc@cesar.school) |
| Davi Magno Campelo do Nascimento | [dmcn@cesar.school](mailto:dmcn@cesar.school) |
| Lauan Goncalves dos Santos | [lgs5@cesar.school](mailto:lgs5@cesar.school) |
| Letícia Gomes da Silva | [lgs6@cesar.school](mailto:lgs6@cesar.school) |
| Lucas Felipe Barreto Cavalcante | [lfbc@cesar.school](mailto:lfbc@cesar.school) |
| Lucas Filipe de Lima Segundo | [lfls@cesar.school](mailto:lfls@cesar.school) |
| Mateus Valerino Barros de Santana | [mvbs2@cesar.school](mailto:mvbs2@cesar.school) |

### Histórico da Equipe

| Integrante | E-mail | Entrada | Saída |
|------------|--------|---------|-------|
| Nenhum registro | — | — | — |

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- **Java 21+**
- **Maven** (ou usar o wrapper `./mvnw`)
- **Git**
- **PostgreSQL** (opcional, para rodar localmente com banco real)

### Passo a Passo

1. Clone o repositório:

```bash
git clone https://github.com/brenocrruz/projeto-hemobit.git
cd projeto-hemobit
```

2. Execute a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows: `mvnw.cmd spring-boot:run`

3. Acesse a aplicação em [http://localhost:8080](http://localhost:8080).

### Variáveis de Ambiente

Para o deploy, as seguintes variáveis são configuradas no Render:

| Variável | Descrição |
|----------|-----------|
| `SPRING_DATASOURCE_URL` | URL JDBC do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |

---

## ☁️ Deploy e CI/CD

A aplicação está implantada e acessível publicamente:

🔗 **[https://projeto-hemobit.onrender.com/](https://projeto-hemobit.onrender.com/)**

### Pipeline de CI/CD

O projeto utiliza **GitHub Actions** para integração contínua e **Render** para deploy automatizado.

```
git push (main) → GitHub Actions (build + test) → Deploy Hook → Render (Docker build + deploy)
```

- **Arquivo do pipeline:** [`.github/workflows/ci.yml`](.github/workflows/ci.yml)
- **Dockerfile:** [`Dockerfile`](Dockerfile)

---

## 📚 Disciplinas Integradas

O Hemobit é um projeto integrador: cada disciplina contribui com uma parte essencial do produto.

| Disciplina | Papel no Projeto | Artefatos |
|------------|------------------|-----------|
| **Programação Orientada a Objetos (POO)** | Constrói a aplicação web em Java/Spring Boot, modela o domínio e implementa as regras de negócio. | [Modelo de Domínio](./docs/poo/modelo-dominio.md) |
| **Algoritmos e Estruturas de Dados (AED)** | Projeta e implementa os algoritmos de roteirização, compatibilidade ABO/Rh e priorização FEFO. | [Escopo do Grafo](./docs/aed/escopo-grafo.md) |
| **Estatística e Probabilidade (EST)** | Transforma os dados de estoque, demanda e operação em indicadores e análises. | Em desenvolvimento |
| **Infraestrutura de Software (SO)** | Garante desempenho, concorrência, CI/CD e operação em nuvem. | [Pipeline CI/CD](./.github/workflows/ci.yml) |
| **Infraestrutura de Comunicação (RSD)** | Define a arquitetura de comunicação entre unidades e a telemetria dos veículos. | [Contratos de API](./docs/rsd/contratos-api.md) |

---

## 📦 Entregas — Programação Orientada a Objetos

> As entregas abaixo são referentes à disciplina de **Programação Orientada a Objetos**. Os artefatos das demais disciplinas estão listados na seção **Disciplinas Integradas**.

### 🟢 Entrega 01 — Histórias de Usuário e Protótipo Lo-Fi

**Data:** 31/08/2026

- **7 Histórias de Usuário** com cenários BDD.
- **Protótipo Lo-Fi** no Figma (mínimo 5 histórias).
- **Screencast** de apresentação do protótipo.

| Artefato | Link |
|----------|------|
| Histórias de Usuário + BDD | [docs/historias_de_usuario.md](./docs/historias_de_usuario.md) |
| Protótipo Lo-Fi (Figma) | [Acessar](https://www.figma.com/design/2i20Xk8HdRaHD5qzrYGwvJ/Untitled?node-id=0-1&p=f&t=owGOIf38YrcZiYnx-0) |
| Screencast | [Assistir no YouTube](https://youtu.be/DexqcjN694o) |

**Histórias contempladas:**

| ID | História |
|----|----------|
| HU01 | Cadastro e Gestão de Profissionais Solicitantes |
| HU02 | Cadastro e Manutenção de Hospitais Solicitantes |
| HU03 | Cadastro do Acervo de Hemocomponentes |
| HU04 | Registro e Entrada de Bolsas de Sangue no Estoque |
| HU05 | Consulta e Listagem do Estoque do Hemocentro |
| HU06 | Criação de Solicitação de Hemocomponentes |
| HU07 | Atualização do Status da Solicitação |

---

### 🟡 Entrega 02 — Implementação de Histórias (Parte 1)

**Data:** 21/09/2026

> 🚧 Em andamento.

Itens previstos:

- Implementação de pelo menos 2 histórias de usuário.
- Commits semanais de código na branch `main`.
- Uso do issue/bug tracker do GitHub.
- Screencast do sistema funcionando.
- Screencast da explicação do código.

---

### 🟠 Entrega 03 — Implementação de Histórias (Parte 2)

**Data:** 19/10/2026

> 🚧 Em desenvolvimento.

---

### 🔴 Entrega 04 — Implementação de Histórias (Parte 3) e Apresentação Final

**Data:** 09/11/2026

> 🚧 Em desenvolvimento.

---

## 🔗 Links Úteis

- 🐙 [Repositório GitHub](https://github.com/brenocrruz/projeto-hemobit)
- 📋 [Jira (Board do Projeto)](https://algs2.atlassian.net/jira/software/c/projects/PI3E4/boards/11/backlog)
- 🎨 [Protótipo no Figma](https://www.figma.com/design/2i20Xk8HdRaHD5qzrYGwvJ/Untitled?node-id=0-1&p=f&t=owGOIf38YrcZiYnx-0)
- 🎥 [Screencast no YouTube](https://youtu.be/DexqcjN694o)

---

## 📄 Licença

Projeto acadêmico desenvolvido para a **CESAR School** — sem fins comerciais. Uso exclusivo de **dados sintéticos**.