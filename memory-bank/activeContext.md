# Active Context — cal-service

**Última atualização**: 2026-04-19

## Foco Atual

- **TASK017 (Completed)**: `GET /event-types` migrado para resposta paginada com `Page<T>` na camada application e contrato OpenAPI atualizado.
- **TASK016 (Completed)**: Guia de integração de consumo da API refinado em formato endpoint-by-endpoint com request/response, observações críticas e diagramas Mermaid.
- **TASK015 (Completed)**: alterações da branch `feat/refatoracao-integracao-cal-service` separadas em commits temáticos para revisão.
- **TASK011 (Completed)**: Complemento pós-code-review concluído em `shared/error`, `shared/http`, `SecurityFilter`, testes de contrato e documentação.
- **TASK014 (Completed)**: Reestruturação semântica de packages de testes `eventtype` e varredura do módulo `shared` finalizada.
- **TASK012 (Completed)**: Hardening de infraestrutura REST finalizado com exemplos/respostas OpenAPI alinhados ao contrato `ApiErrorResponse`.
- **TASK013 (Completed)**: Suíte de validators criada e contrato HTTP do `GlobalExceptionHandler` finalizado com cenário 405.

**Próximo passo imediato**: retomar backlog técnico de média prioridade (CI/CD e estratégia de migrations).

## O Que Foi Feito Recentemente

- Listagem de `event-types` alterada para paginação obrigatória com query params `page` e `size`.
- Criada `Page<T>` em `eventtype.application.output` para evitar dependência de `org.springframework.data.domain.Page` na camada application.
- `EventTypeControllerSwagger` e guia de integração atualizados para o novo contrato paginado.
- `./mvnw compile` executado com sucesso em Java 21; `./mvnw test` permanece bloqueado por erros preexistentes fora de escopo nos testes de validators.
- Documentação de integração para consumidores externos evoluída para formato endpoint-by-endpoint com request/response por rota, erros comuns e observações importantes.
- Diagramas Mermaid adicionados para visão de arquitetura, fluxo de autenticação e ciclo de vida de agendamento.
- Refatoração da branch foi separada em commits por tema (auth/infra, core errors, REST/OpenAPI, testes e documentação) para facilitar code review.
- Concluídos os itens TASK011.6, TASK011.7, TASK011.8 e TASK011.9.
- `GlobalExceptionHandler` e `ErrorHttpStatusMapping` endurecidos com null-safety/fallbacks e proteção contra sobrescrita silenciosa.
- Criada `RestClientBuilderFactory` e aplicada nos configs de `auth`, `eventtype` e `appointment`.
- `LoggingInterceptor` atualizado para DEBUG, duração de chamada e mascaramento de query params sensíveis.
- `SecurityFilter` atualizado para comportamento resiliente sem vazamento de token.
- Testes adicionados: `GlobalExceptionHandlerTest`, `ErrorHttpStatusMappingTest`, `SecurityFilterTest`, `LoggingInterceptorTest`.
- Docs atualizados: `shared/error/doc.md` e `shared/http/doc.md`.
- Testes de `eventtype` foram reorganizados para packages/caminhos do bounded context (`eventtype.*`) preservando semântica.
- TASK012.3 concluída: `EventTypeControllerSwagger` e `AppointmentControllerSwagger` atualizados com exemplos `code/severity/violations` e status documentados alinhados ao mapeamento atual.
- TASK013 concluída com criação de 7 novos testes de validator em `eventtype`/`appointment` e ajuste de `GlobalExceptionHandlerTest` para `405 Method Not Allowed`.
- `./mvnw compile` e `./mvnw test` executados com Java 21 após TASK013 com sucesso (suite completa verde).

## Decisões Ativas

- Exceptions por camada permanecem mandatórias (`DomainException`, `GatewayException`, `ApplicationException`, `ValidationException`).
- Mapeamento HTTP é centralizado em registry/registrars (`shared/error/http`) sem acoplamento do domínio a HTTP.
- `ValidationCode` permanece apenas como compatibilidade; novos fluxos usam `ErrorContract` diretamente.
- `reconstitute()` e `createNew()` seguem como únicos factory methods válidos em entidades de domínio.

## Contexto de Integração

- Cal.com via adapters `CalComEventTypeAdapter` e `CalComBookingAdapter`.
- Monolith via `MonolithEstateAdapter`.
- Auth service via `AuthServiceAdapter` e `ValidateTokenUseCase` no filtro de segurança.
