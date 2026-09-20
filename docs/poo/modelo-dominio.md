# Modelo de Domínio — Hemobit

Documento de referência do modelo de domínio da aplicação Hemobit (Rota Vital — 3º semestre, ADS, CESAR School), construído em Java/Spring Boot com persistência em PostgreSQL via JPA/Hibernate.

## Status de implementação

| Entidade | Status |
|---|---|
| Doador | ✅ Implementada (CRUD completo) |
| Doacao | ✅ Implementada |
| Hemocomponente | ✅ Implementada (CRUD completo, com DTO) |
| Localidade / UnidadeColeta | ✅ Implementada |
| UnidadeSaude (= "Hospital") | ✅ Implementada (CRUD completo) |
| Solicitacao | ✅ Implementada (CRUD completo) |
| Rota | ✅ Implementada (CRUD completo) |
| Veiculo | ✅ Implementada (CRUD completo) |
| Transporte | ⏳ Modelada neste documento, ainda não codada |
| TransporteHemocomponente | ⏳ Modelada neste documento, ainda não codada |

> Nota de nomenclatura: nos cards de entrega, "Hospital" e "Bolsa/Estoque" referem-se às entidades já existentes `UnidadeSaude` e `Hemocomponente`, respectivamente — não são entidades separadas.

## Diagrama de classes

```mermaid
classDiagram
    class Doador {
        +Long id
        +String nome
        +LocalDate dataNascimento
        +TipoSanguineo tipoSanguineo
        +String telefone
    }

    class Doacao {
        +Long id
        +LocalDateTime dataHora
        +double quantidade
    }

    class Hemocomponente {
        +Long id
        +TipoHemocomponente tipo
        +double quantidade
        +LocalDate dataProducao
        +LocalDate dataValidade
        +StatusHemocomponente status
    }

    class Localidade {
        <<abstract>>
        +Long id
        +String nome
        +String endereco
        +String cidade
        +String telefone
    }

    class UnidadeColeta
    class UnidadeSaude

    class Solicitacao {
        +Long id
        +TipoHemocomponente tipoHemocomponente
        +double quantidade
        +StatusSolicitacao status
        +LocalDateTime dataHora
    }

    class Rota {
        +Long id
        +double distancia
        +int tempoEstimado
    }

    class Veiculo {
        +Long id
        +String placa
        +String tipo
        +double capacidade
        +StatusVeiculo status
    }

    class Transporte {
        +Long id
        +LocalDateTime dataHoraSaida
        +LocalDateTime dataHoraChegada
        +StatusTransporte status
    }

    class TransporteHemocomponente {
        +Long id
        +double quantidade
    }

    class TipoSanguineo {
        <<enumeration>>
        A_POSITIVO
        A_NEGATIVO
        B_POSITIVO
        B_NEGATIVO
        AB_POSITIVO
        AB_NEGATIVO
        O_POSITIVO
        O_NEGATIVO
    }

    class StatusHemocomponente {
        <<enumeration>>
        DISPONIVEL
        RESERVADO
        TRANSPORTANDO
        ENTREGUE
        UTILIZADO
        VENCIDO
        DESCARTADO
    }

    class StatusSolicitacao {
        <<enumeration>>
        PENDENTE
        ATENDIDA
        NEGADA
    }

    Localidade <|-- UnidadeColeta
    Localidade <|-- UnidadeSaude

    Doador "1" --> "*" Doacao : realiza
    Doador ..> TipoSanguineo : usa

    UnidadeColeta "1" --> "*" Doacao : recebe

    Doacao "1" --> "*" Hemocomponente : origina

    Hemocomponente "1" --> "1" Localidade : localizacaoAtual
    Hemocomponente ..> StatusHemocomponente : usa

    UnidadeSaude "1" --> "*" Solicitacao : solicita
    Solicitacao ..> StatusSolicitacao : usa
    Solicitacao ..> TipoHemocomponente : usa

    Localidade "1" --> "*" Rota : origem
    Localidade "1" --> "*" Rota : destino

    Rota "1" --> "*" Transporte : é usada em

    Veiculo "1" --> "*" Transporte : realiza

    Transporte "1" --> "*" TransporteHemocomponente
    Hemocomponente "1" --> "*" TransporteHemocomponente
```

> Observação: `TipoHemocomponente` e `StatusVeiculo`/`StatusTransporte` são enums adicionais, omitidos do diagrama por brevidade visual — detalhados na seção de entidades abaixo.

## Lista de entidades

### Doador
Pessoa que realiza a doação de sangue.
- `id` (PK)
- `nome`
- `dataNascimento`
- `tipoSanguineo` (enum `TipoSanguineo`)
- `telefone`
- **Relacionamento:** 1 Doador → N Doacao

### Doacao
Evento de coleta de sangue de um doador.
- `id` (PK)
- `dataHora`
- `quantidade`
- `doador` (FK → Doador, N:1)
- `unidadeColeta` (FK → UnidadeColeta, N:1)
- **Relacionamento:** 1 Doacao → N Hemocomponente (uma doação é separada em múltiplos hemocomponentes)

### Hemocomponente
Unidade de sangue processada, pronta para uso ou distribuição. Equivale ao termo "Bolsa/Estoque" usado nos cards de entrega.
- `id` (PK)
- `tipo` (enum `TipoHemocomponente`: CONCENTRADO_HEMACIAS, CONCENTRADO_PLAQUETAS, PLASMA_FRESCO_CONGELADO)
- `quantidade`
- `dataProducao`
- `dataValidade`
- `status` (enum `StatusHemocomponente`)
- `doacao` (FK → Doacao, N:1)
- `localizacaoAtual` (FK → Localidade, N:1) — onde o hemocomponente está fisicamente agora
- **Implementação:** exposto via DTO (`HemocomponenteRequestDTO`/`HemocomponenteResponseDTO`) — ver `docs/poo/padroes-projeto.md`.

### Localidade (classe abstrata)
Representa um local físico do sistema. Superclasse de `UnidadeColeta` e `UnidadeSaude`, mapeada com herança `SINGLE_TABLE` (coluna discriminadora `tipo_localidade`).
- `id` (PK)
- `nome`
- `endereco`
- `cidade`
- `telefone`

### UnidadeColeta (extends Localidade)
Hemocentro — local onde doações são realizadas e hemocomponentes podem ser armazenados.
- **Relacionamento:** 1 UnidadeColeta → N Doacao

### UnidadeSaude (extends Localidade)
Hospital — destino de requisições de hemocomponentes. Equivale ao termo "Hospital" usado nos cards de entrega.

### Solicitacao
Pedido de hemocomponentes feito por uma unidade de saúde.
- `id` (PK)
- `unidadeSaude` (FK → UnidadeSaude, N:1)
- `tipoHemocomponente` (enum `TipoHemocomponente`)
- `quantidade`
- `status` (enum `StatusSolicitacao`: PENDENTE, ATENDIDA, NEGADA)
- `dataHora`
- **Regra de negócio:** uma solicitação só pode ser editada enquanto está `PENDENTE`; ao ser atendida (`PUT /solicitacoes/{id}/atender`), o sistema verifica existência de estoque `DISPONIVEL` do tipo solicitado antes de confirmar.

### Rota
Caminho entre duas localidades (origem e destino), usado para planejar transportes.
- `id` (PK)
- `origem` (FK → Localidade, N:1)
- `destino` (FK → Localidade, N:1)
- `distancia`
- `tempoEstimado`
- **Relacionamento:** 1 Rota → N Transporte (uma rota é usada em vários transportes ao longo do tempo)
- **Regra de validade:** origem e destino não podem ser a mesma localidade.

### Veiculo
Veículo usado para transportar hemocomponentes.
- `id` (PK)
- `placa` (única)
- `tipo`
- `capacidade`
- `status` (enum `StatusVeiculo`: DISPONIVEL, EM_ROTA, MANUTENCAO)

### Transporte — ainda não implementada
Evento de movimentação de hemocomponentes por uma rota, usando um veículo.
- `id` (PK)
- `dataHoraSaida`
- `dataHoraChegada`
- `status` (enum `StatusTransporte`: AGENDADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO)
- `rota` (FK → Rota, N:1)
- `veiculo` (FK → Veiculo, N:1)

### TransporteHemocomponente (entidade associativa) — ainda não implementada
Resolve o relacionamento N:N entre `Transporte` e `Hemocomponente`, carregando o atributo `quantidade` transportada de cada hemocomponente naquele transporte específico.
- `id` (PK)
- `transporte` (FK → Transporte, N:1)
- `hemocomponente` (FK → Hemocomponente, N:1)
- `quantidade`

## Regras de validade

**Ciclo de vida do status do Hemocomponente**, em ordem esperada de transição:

`DISPONIVEL` → `RESERVADO` → `TRANSPORTANDO` → `ENTREGUE` → `UTILIZADO`

Com dois estados terminais alternativos, alcançáveis a partir de `DISPONIVEL` ou `RESERVADO`:
- `VENCIDO` — quando `dataValidade` é ultrapassada antes do uso.
- `DESCARTADO` — quando o hemocomponente é inutilizado por outro motivo (contaminação, dano no transporte, etc.).

**Regra de validade (FEFO — First Expired, First Out):** ao selecionar um hemocomponente para atender uma requisição, o sistema deve priorizar o hemocomponente `DISPONIVEL` com a `dataValidade` mais próxima, para minimizar descarte por vencimento.

**Regra de localização:** `Hemocomponente.localizacaoAtual` deve ser atualizada a cada mudança relevante de estado — por exemplo, ao entrar em status `TRANSPORTANDO`, a localização reflete o destino planejado do `Transporte` em andamento; ao chegar (`ENTREGUE`), reflete a `UnidadeSaude` de destino.

**Regra de integridade doador–doação:** uma `Doacao` não pode existir sem um `Doador` e uma `UnidadeColeta` associados (campos `nullable = false` nas FKs).

**Regra de solicitação:** uma `Solicitacao` só pode ser editada enquanto `PENDENTE`; ao ser atendida, exige estoque `DISPONIVEL` do tipo pedido — aplicada na camada de serviço (`SolicitacaoService`), não apenas no banco.

**Regra de rota:** origem e destino de uma `Rota` não podem ser a mesma `Localidade` — aplicada na camada de serviço (`RotaService`).

**Regra de cadeia fria (simplificada):** todo `Transporte` está sujeito a um tempo máximo aceitável entre `dataHoraSaida` e `dataHoraChegada`; ultrapassar esse limite é tratado como risco de ruptura de cadeia fria (regra a ser refinada em conjunto com a disciplina de AED, que modela o grafo de rotas e pesos, ao codar `Transporte`).

## Como o modelo é consumido pelas outras disciplinas

- **AED:** o grafo de rotas (nós = `Localidade`/`Veiculo`, arestas = `Rota` com pesos) opera sobre os mesmos identificadores de `Localidade` definidos aqui, garantindo que o algoritmo de caminho mínimo e a compatibilidade ABO/Rh trabalhem sobre o mesmo domínio persistido por POO. A classe `GerenciadorHemocentro` (pacote `aed`) já consome diretamente as entidades `Hemocomponente` e `Solicitacao` deste modelo.
- **Estatística:** os indicadores (demanda por período, taxa de descarte, tempo médio de atendimento) são calculados a partir dos dados de `Doacao`, `Hemocomponente` e `Transporte` expostos pela camada de repositório.