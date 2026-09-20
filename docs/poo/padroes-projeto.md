# Padrão de Projeto Aplicado — DTO (Data Transfer Object)

## Onde foi aplicado

O padrão DTO foi implementado na camada de `Hemocomponente`, como referência para as demais entidades do sistema. Envolve as classes:

- `dto/HemocomponenteRequestDTO.java`
- `dto/HemocomponenteResponseDTO.java`
- `service/HemocomponenteService.java`
- `controller/HemocomponenteController.java`

## O problema que motivou a escolha

Antes da aplicação do padrão, o `HemocomponenteController` recebia e devolvia a entidade JPA (`Hemocomponente`) diretamente no corpo das requisições HTTP. Isso trazia dois problemas concretos:

1. **Acoplamento entre a API pública e o modelo de persistência.** Qualquer mudança na entidade (renomear um campo, adicionar uma coluna interna) quebraria o contrato da API sem aviso, mesmo quando a mudança não deveria ser visível para quem consome o sistema.
2. **Risco de serialização incorreta dos relacionamentos.** `Hemocomponente` possui `@ManyToOne` para `Doacao` e `Localidade`. Ao serializar a entidade inteira para JSON, o Spring tentaria também serializar esses objetos relacionados, podendo expor dados desnecessários, lançar `LazyInitializationException` fora do contexto de sessão do Hibernate, ou, caso os relacionamentos se tornem bidirecionais no futuro, causar recursão infinita.

## Como foi resolvido

**Entrada (`HemocomponenteRequestDTO`):** recebe apenas os campos necessários para criar um hemocomponente, incluindo os relacionamentos como IDs simples (`doacaoId`, `localizacaoAtualId`) em vez de objetos completos. O `HemocomponenteService` é responsável por buscar as entidades reais (`Doacao`, `Localidade`) a partir desses IDs antes de montar o objeto de domínio — e é também onde as regras de validade são aplicadas (data de validade não anterior à data de produção, quantidade positiva), antes de qualquer persistência.

**Saída (`HemocomponenteResponseDTO`):** construído a partir da entidade já persistida, expõe apenas os campos relevantes para o consumidor da API, incluindo os relacionamentos apenas como seus IDs (`doacaoId`, `localizacaoAtualId`), nunca os objetos completos.

```java
// Fluxo simplificado no service:
public HemocomponenteResponseDTO criar(HemocomponenteRequestDTO dto) {
    // 1. valida regras de negócio a partir do DTO de entrada
    // 2. busca as entidades relacionadas pelos IDs recebidos
    // 3. monta a entidade Hemocomponente
    // 4. persiste e converte o resultado para o DTO de saída
    return new HemocomponenteResponseDTO(hemocomponenteRepository.save(hemocomponente));
}
```

## Por que este padrão, e não outro

Entre os padrões considerados para o card, o DTO foi escolhido em vez de, por exemplo, um padrão criacional (Factory, Builder) porque o problema central identificado não era *como construir* objetos complexos, mas sim *o que trafega pela borda da aplicação* — a fronteira entre o mundo externo (requisições HTTP) e o modelo interno de persistência (entidades JPA). O DTO resolve diretamente esse problema de acoplamento e de controle de exposição de dados, sendo também a solução mais amplamente adotada em aplicações Spring Boot para esse cenário específico.

## Próximos passos

O mesmo padrão pode ser estendido às demais entidades com relacionamentos (`Doacao`, `Solicitacao`, `Transporte`) nas próximas iterações, seguindo a estrutura já estabelecida aqui como referência.
