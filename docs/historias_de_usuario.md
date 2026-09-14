# Histórias de Usuário e Cenários BDD — Projeto Hemobit (Entrega 1 - POO)

Este documento contém a especificação das Histórias de Usuário e seus respectivos Cenários de Aceitação (BDD) para a **Entrega 1** do projeto Hemobit na disciplina de Programação Orientada a Objetos (CESAR School - 3º Semestre de ADS).

---

## 1. Cadastro e Gestão de Profissionais Solicitantes

**História de Usuário:**  
Como gestor do sistema, eu gostaria de cadastrar e gerenciar os dados dos profissionais solicitantes (nome, CRM/matrícula, e-mail, hospital vinculado) para garantir o controle de acesso e a auditabilidade das requisições médicas.

### Cenário 1: Cadastro realizado com sucesso (Positivo)
* **Dado** que o gestor está autenticado no sistema Hemobit
* **Quando** ele preenche todos os campos obrigatórios do profissional solicitante com dados válidos e clica em "Salvar"
* **Então** o registro do profissional é persistido no banco de dados
* **E** o sistema exibe a mensagem de confirmação "Profissional cadastrado com sucesso!".

### Cenário 2: Tentativa de cadastro com CRM duplicado (Negativo)
* **Dado** que já existe um profissional cadastrado no sistema com o CRM "12345-PE"
* **Quando** o gestor tenta cadastrar outro profissional informando o mesmo CRM "12345-PE"
* **Então** a operação de inclusão no banco de dados não é realizada
* **E** o sistema exibe a mensagem de erro "CRM já cadastrado no sistema!".

---

## 2. Cadastro e Manutenção de Hospitais Solicitantes

**História de Usuário:**  
Como gestor do sistema, eu gostaria de cadastrar e atualizar as informações dos hospitais atendidos (nome, CNPJ, endereço e telefone) para que o centro de distribuição possa mapear as origens das solicitações e destinos de entrega.

### Cenário 1: Alteração de dados cadastrais do hospital (Positivo)
* **Dado** que o hospital "Hospital Restauração" está cadastrado na base de dados
* **Quando** o gestor altera o telefone de contato no formulário e clica em "Atualizar"
* **Então** os dados atualizados são persistidos no banco de dados
* **E** o sistema exibe a mensagem "Cadastro hospitalar atualizado com sucesso!".

### Cenário 2: Cadastro com CNPJ inválido ou incompleto (Negativo)
* **Dado** que o gestor está preenchendo a tela de cadastro de hospital
* **Quando** insere um CNPJ no formato incorreto ou com menos dígitos que o padrão
* **Então** o sistema impede a persistência dos dados
* **E** exibe a mensagem "CNPJ inválido. Digite um número válido para prosseguir.".

---

## 3. Cadastro do Acervo de Hemocomponentes

**História de Usuário:**  
Como funcionário do centro, eu gostaria de cadastrar novos tipos de hemocomponentes (Ex: CH, CP, PFC) definindo parâmetros padrão e tipos sanguíneos suportados para manter o catálogo de produtos da hemorrede organizado.

### Cenário 1: Inclusão de um novo hemocomponente no catálogo (Positivo)
* **Dado** que o funcionário do centro está autenticado com perfil operacional
* **Quando** preenche o formulário informando a sigla "CH" (Concentrado de Hemácias), descrição e prazo limite de armazenamento e confirma
* **Então** o tipo de hemocomponente é gravado no banco de dados
* **E** fica disponível para seleção nos cadastros de estoque.

### Cenário 2: Inclusão sem preenchimento de campo obrigatório (Negativo)
* **Dado** que o funcionário está na tela de cadastro de hemocomponentes
* **Quando** tenta salvar um registro omitindo o campo obrigatório "Nome/Sigla do Componente"
* **Então** o formulário recusa a submissão
* **E** exibe o alerta "O campo Nome/Sigla do Hemocomponente é obrigatório!".

---

## 4. Registro e Entrada de Bolsas de Sangue no Estoque

**História de Usuário:**  
Como funcionário do centro, eu gostaria de dar entrada em novas bolsas no estoque (informando o código da bolsa, tipo do hemocomponente, grupo sanguíneo ABO/Rh, data de coleta e data de validade) para manter a disponibilidade e o controle de validade atualizados.

### Cenário 1: Dar entrada em bolsa de sangue válida (Positivo)
* **Dado** que o funcionário possui uma bolsa física recebida com data de vencimento futura
* **Quando** ele cadastra os dados da bolsa (Código: "B102030", Tipo: "PFC", Tipo Sanguíneo: "O+", Validade: "15/10/2026")
* **Então** a bolsa é salva no banco de dados com status "Disponível"
* **E** o estoque total daquele tipo sanguíneo é incrementado.

### Cenário 2: Tentativa de dar entrada em bolsa vencida (Negativo)
* **Dado** que o funcionário está na tela de registro de entrada de estoque
* **Quando** tenta cadastrar uma bolsa informando uma data de validade anterior à data atual do sistema
* **Então** o registro não é permitido no banco de dados
* **E** o sistema exibe a regra de validação: "Não é permitido cadastrar bolsas com data de validade vencida!".

---

## 5. Consulta e Listagem do Estoque do Hemocentro

**História de Usuário:**  
Como funcionário do centro ou gestor, eu gostaria de visualizar e filtrar a listagem das bolsas de sangue armazenadas por tipo sanguíneo, tipo de componente e status (disponível, reservado, descartado) para rápida tomada de decisão operacional.

### Cenário 1: Filtragem do estoque por tipo sanguíneo e status (Positivo)
* **Dado** que existem bolsas cadastradas de diversos tipos sanguíneos no banco de dados
* **Quando** o usuário aplica o filtro por Grupo Sanguíneo "O-" e Status "Disponível"
* **Então** o sistema recupera do banco e exibe exclusivamente as bolsas que atendem a ambos os critérios informados.

### Cenário 2: Consulta por filtro sem resultados correspondentes (Negativo)
* **Dado** que não há nenhuma bolsa do tipo "AB-" disponível no estoque atual
* **Quando** o usuário realiza a busca filtrando por Grupo Sanguíneo "AB-" e Status "Disponível"
* **Então** o sistema retorna uma lista vazia
* **E** exibe na tela a mensagem "Nenhum hemocomponente encontrado para os filtros selecionados.".

---

## 6. Criação de Solicitação de Hemocomponentes via Interface/Formulário

**História de Usuário:**  
Como profissional solicitante, eu gostaria de registrar uma nova requisição de hemocomponentes para um hospital (especificando componente, tipo sanguíneo, quantidade de bolsas e nível de prioridade) para dar início ao processo de atendimento.

### Cenário 1: Solicitação criada com sucesso (Positivo)
* **Dado** que o médico solicitante está logado e associado ao "Hospital São Marcos"
* **Quando** preenche a requisição solicitando 2 bolsas de "Concentrado de Hemácias", tipo "A+", com prioridade "Urgente", e envia
* **Então** o registro de solicitação é inserido no banco de dados com status "Pendente"
* **E** o sistema gera um código de identificação da solicitação.

### Cenário 2: Criação de solicitação com quantidade de bolsas inválida (Negativo)
* **Dado** que o médico está na página de nova solicitação
* **Quando** preenche a quantidade desejada de bolsas com o valor "0" ou um número negativo
* **Então** a solicitação é rejeitada antes da gravação no banco
* **E** a tela apresenta a mensagem de validação "A quantidade solicitada deve ser de no mínimo 1 bolsa.".

---

## 7. Atualização do Status da Solicitação pelo Centro Operacional

**História de Usuário:**  
Como funcionário do centro, eu gostaria de alterar o status das solicitações recebidas (ex: de "Pendente" para "Em Separação", "Atendida" ou "Cancelada") para manter os hospitais informados sobre o andamento dos seus pedidos.

### Cenário 1: Transição de status para Em Separação (Positivo)
* **Dado** que existe uma solicitação com status "Pendente" registrada na base de dados
* **Quando** o funcionário do centro acessa os detalhes da requisição e seleciona a opção "Iniciar Separação"
* **Então** o status da solicitação no banco de dados é atualizado para "Em Separação"
* **E** o histórico de movimentação do pedido atualiza o horário e o operador responsável.

### Cenário 2: Tentativa de transição para um status inválido (Negativo)
* **Dado** que uma solicitação já foi marcada e encerrada com o status "Entregue"
* **Quando** o funcionário tenta alterar diretamente o status dessa mesma solicitação para "Pendente"
* **Então** o sistema bloqueia a alteração
* **E** exibe a mensagem "Não é permitido alterar o status de solicitações já finalizadas.".
