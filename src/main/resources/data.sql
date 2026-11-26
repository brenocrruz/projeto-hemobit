ALTER TABLE IF EXISTS solicitacao DROP CONSTRAINT IF EXISTS solicitacao_status_check;

-- SINGLE_TABLE: coleta não tem CNPJ; a exigência permanece para saúde.
ALTER TABLE localidade ALTER COLUMN cnpj DROP NOT NULL;
ALTER TABLE localidade DROP CONSTRAINT IF EXISTS localidade_cnpj_unidade_saude_check;
ALTER TABLE localidade ADD CONSTRAINT localidade_cnpj_unidade_saude_check CHECK (tipo_localidade <> 'UNIDADE_SAUDE' OR (cnpj IS NOT NULL AND btrim(cnpj) <> ''));
