# language: pt
Funcionalidade: Atendimento de solicitação de hemocomponentes
  Como técnico da central de sangue
  Quero atender solicitações com bolsas compatíveis e mais próximas do vencimento
  Para que nenhum paciente receba sangue incompatível e nenhuma bolsa seja desperdiçada

  Cenário: Atender reservando as bolsas compatíveis mais próximas do vencimento
    Dado uma solicitação de 2 bolsas de concentrado de hemácias para paciente "A_POSITIVO"
    E as seguintes bolsas disponíveis em ordem de validade:
      | codigo | tipoSanguineo |
      | B-001  | B_POSITIVO    |
      | B-002  | O_NEGATIVO    |
      | B-003  | A_POSITIVO    |
      | B-004  | A_POSITIVO    |
    Quando a solicitação é atendida
    Então as bolsas "B-002, B-003" ficam reservadas
    E a bolsa "B-001" continua disponível
    E a bolsa "B-004" continua disponível
    E a solicitação fica com status "ATENDIDA"

  Cenário: Recusar o atendimento quando o estoque compatível é insuficiente
    Dado uma solicitação de 2 bolsas de concentrado de hemácias para paciente "A_POSITIVO"
    E as seguintes bolsas disponíveis em ordem de validade:
      | codigo | tipoSanguineo |
      | B-001  | B_POSITIVO    |
      | B-002  | A_POSITIVO    |
    Quando a solicitação é atendida
    Então o atendimento é recusado com a mensagem "Estoque compatível insuficiente: solicitadas 2 bolsa(s) de CONCENTRADO_HEMACIAS para paciente A_POSITIVO, mas há apenas 1 compatível(is) e dentro da validade."
    E nenhuma bolsa fica reservada
    E a solicitação fica com status "PENDENTE"

  Cenário: Recusar solicitação acima do limite por pedido
    Quando tento abrir uma solicitação de 51 bolsas de concentrado de hemácias para paciente "A_POSITIVO"
    Então o pedido é recusado com a mensagem "A quantidade máxima por solicitação é 50."
