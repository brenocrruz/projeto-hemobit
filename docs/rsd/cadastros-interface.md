# Cadastros pela interface

## Unidades de saúde

A tela `/cadastros/unidades-saude` usa diretamente GET e POST `/unidades-saude`, já existentes. Envia nome, CNPJ, cidade, endereço e telefone. Nome, cidade e CNPJ são obrigatórios pelo serviço original; endereço e telefone são opcionais no contrato JPA. O JavaScript valida formato e dígitos do CNPJ e envia somente os dígitos; o serviço original revalida. Nenhum CNPJ é gerado ou preenchido automaticamente.

UnidadeSaude e UnidadeColeta são subtipos distintos de Localidade. O cadastro de saúde não cria uma unidade de coleta nem desbloqueia sozinho o cadastro de doação. Após autorização, o mapeamento permite CNPJ ausente para coleta, e `data.sql` aplica uma restrição condicional que mantém CNPJ obrigatório para saúde. A aplicação autenticada precisa ser reiniciada para aplicar o ajuste ao banco; veja `diagnostico-unidade-coleta.md`.

Fluxo com banco vazio:

1. `/cadastros/doadores`: cadastrar nome, data de nascimento e tipo sanguíneo.
2. `/cadastros/unidades-coleta`: cadastrar nome obrigatório; cidade e endereço opcionais.
3. `/cadastros/doacoes`: selecionar doador e unidade existentes, informar data/hora e quantidade positiva.
4. `/estoque#novo-hemocomponente`: selecionar a doação criada e cadastrar o hemocomponente. A unidade de coleta também aparece como localização opcional, pois herda de Localidade.

As telas usam `GET` e `POST` de `/api/cadastros/doadores`, `/api/cadastros/unidades-coleta` e `/api/cadastros/doacoes`. O cadastro retorna HTTP 201; validações retornam HTTP 400 com `{"mensagem":"..."}`. O serviço de doador existente continua responsável por suas regras. Os endpoints legados `/doadores` permanecem compatíveis; a nova interface usa um adaptador com DTOs para limitar os campos recebidos e retornados.

`GET /api/cadastros/doacoes/opcoes` fornece apenas `{id, nome, tipoSanguineo}` para doadores e `{id, nome}` para unidades. Inclui o nome para identificação na seleção, sem contato ou nascimento do doador. A listagem de doadores inclui ID, nome e tipo sanguíneo, sem contato ou nascimento. A listagem de doações inclui somente ID, data/hora, quantidade e IDs de seus vínculos. Nenhuma nova resposta serializa entidades ou relacionamentos completos.

Não há coleta de CPF ou contato nessa versão. O domínio não define unidade de medida para quantidade nem regras clínicas de elegibilidade/intervalo; a interface não presume essas regras. O cadastro valida referências existentes e números finitos e positivos, sem alterar entidades ou reserva.

Os dados são persistidos pelos repositórios JPA na fonte PostgreSQL configurada em `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`. Essas variáveis devem estar disponíveis no processo Java; não há credenciais no código. Não são necessários inserts manuais.

Logs HTTP detalhados e logs de parâmetros/resultados JDBC são desativados na configuração principal. Os DTOs com nome e nascimento têm representação textual ocultada. A nova interface não registra corpos ou dados pessoais no console.

Os testes MVC executam os serviços reais com repositórios simulados, cobrindo telas Thymeleaf, DTOs, validações, privacidade e o fluxo até hemocomponente. Não substituem uma validação de persistência em PostgreSQL autenticado ou um teste da interação em navegador.
