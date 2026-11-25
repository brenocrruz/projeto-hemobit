# ADR 0001: Decomposição do Hemobit

Status: proposto

## Contexto

O Hemobit é hoje um monólito Spring Boot com um banco PostgreSQL. O domínio tem seis contextos (Estoque, Requisição, Expedição, Captação, Monitoramento e Indicadores). Os contextos têm cargas muito diferentes: o Monitoramento recebe muitas leituras de sensores, a Requisição recebe picos de pedidos de hospitais, e o Estoque e a Captação mudam juntos. O time tem sete pessoas e prazo curto, e a aplicação roda em uma instância única na PaaS.

## Decisão

Na projeção com microsserviços, decompor em cinco serviços: **Estoque (com Captação), Requisição, Expedição, Monitoramento e Indicadores**. Cada um tem banco próprio e eles se comunicam por REST (consultas e reserva de bolsas) e por eventos em fila (fatos de negócio). Os fluxos que atravessam serviços usam saga orquestrada pela Requisição.

## Consequências

- **Melhora:** cada serviço escala conforme a sua carga (em especial Monitoramento e Requisição); uma falha em Indicadores ou Monitoramento não derruba o atendimento; as fronteiras seguem a linguagem do domínio.
- **Piora:** a consistência entre serviços passa a ser eventual, com saga e compensação; aparecem broker, gateway, versionamento de contratos e observabilidade distribuída; o custo mensal sobe mesmo com pouca carga.
- **Alternativa descartada:** um serviço por contexto (seis serviços). Separar Captação do Estoque só somaria rede e operação, porque os dois mudam juntos e Captação só alimenta o estoque.
