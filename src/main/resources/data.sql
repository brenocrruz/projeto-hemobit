-- Resumo: libera os novos status de solicitacao (EM_SEPARACAO e CANCELADA) em bancos que ja existiam.
-- Explicando facil: com ddl-auto=update o Hibernate nao altera uma regra (check constraint) que ele
-- mesmo criou antes, entao o banco continuaria recusando os status novos. Este comando remove essa regra
-- antiga. Ele e seguro de rodar sempre (IF EXISTS) e nao faz nada se a regra nao existir.
ALTER TABLE IF EXISTS solicitacao DROP CONSTRAINT IF EXISTS solicitacao_status_check;
