# System Patterns — cal-service

## Arquitetura

O projeto segue **Clean Architecture** combinada com **DDD (Domain-Driven Design)**. Cada bounded context é um módulo independente com as mesmas três camadas.

```
src/main/java/com/penelopec/calservice/
├── eventtype/          ← Bounded Context: Tipos de Agendamento
│   ├── domain/         ← Entidades, Value Objects, Gateways (interfaces), Repositório (interface)
│   ├── application/    ← Use Cases (Services), Commands, Outputs, Mappers, Ports
│   └── infrastructure/ ← Controllers, JPA Adapters, Web Adapters, Config, Scheduler
└── appointment/        ← Bounded Context: Agendamentos
    ├── domain/
    ├── application/
    └── infrastructure/
```

### Regra de Dependência
```
Infrastructure → Application → Domain
```
O domínio nunca importa nada de fora de si mesmo. A aplicação usa interfaces (ports) que a infraestrutura implementa.

---

## Padrões Arquiteturais Chave

### 1. Ports & Adapters (Hexagonal)

- **Input ports**: Interfaces de Use Case em `application/port/in/` (eventtype) ou `application/usecase/` (appointment).
- **Output ports**: Gateways em `domain/gateway/` e Repositories em `domain/repository/`.
- **Adapters de entrada**: Controllers REST em `infrastructure/controller/`.
- **Adapters de saída**: `EventTypeRepositoryAdapter`, `CalComEventTypeAdapter`, `MonolithEstateAdapter`, `CalComBookingAdapter`.

### 2. Use Case como Service

Cada Use Case é uma interface implementada por um Service Spring (`@Service`). Exemplo:
```java
// Interface (porta de entrada)
public interface CreateEventTypeUseCase {
    EventTypeOutput execute(CreateEventTypeCommand command);
}

// Implementação (serviço de aplicação)
@Service
public class CreateEventTypeService implements CreateEventTypeUseCase { ... }
```

### 3. Command Objects

Operações de escrita recebem objetos Command imutáveis. Exemplo:
- `CreateEventTypeCommand`
- `UpdateEventTypeCommand`
- `CreateAppointmentCommand`

### 4. Output Objects

Use Cases retornam Output Objects (DTOs de saída da aplicação), nunca entidades de domínio diretamente. Exemplos:
- `EventTypeOutput`, `EventTypeListOutput`
- `AppointmentOutput`, `AppointmentListOutput`

### 5. JPA Entity separada da Domain Entity

A entidade JPA (`EventTypeJpaEntity`, `AppointmentJpaEntity`) é completamente separada da entidade de domínio. O mapper JPA (`EventTypeJpaMapper`, `AppointmentJpaMapper`) faz a conversão.

### 6. Factory Methods no Domínio

Entidades de domínio usam factory methods estáticos em vez de construtores públicos:
```java
EventType.createNew(title, description, ...)   // Nova entidade
EventType.reconstitute(id, slug, ...)           // Reconstituição do banco
```

### 7. Value Objects Imutáveis

- `Slug`: derivado do título, imutável, com normalização (lowercase, remove acentos, hifeniza).
- `Status` (enum): `PENDING`, `CONFIRMED`, `CONCLUDED`, `CANCELLED`. Tem método `isTerminal()`.

### 8. Tratamento de Exceções Centralizado

`GlobalExceptionHandler` (`@RestControllerAdvice`) captura exceções de domínio e retorna `ApiErrorResponse` padronizado.

---

## Padrões de Nomenclatura

| Tipo               | Convenção                                 | Exemplo                        |
|--------------------|-------------------------------------------|--------------------------------|
| Use Case           | `[Ação][Entidade]UseCase`                 | `CreateEventTypeUseCase`       |
| Service            | `[Ação][Entidade]Service`                 | `CreateEventTypeService`       |
| Command            | `[Ação][Entidade]Command`                 | `CreateEventTypeCommand`       |
| Output             | `[Entidade]Output`                        | `EventTypeOutput`              |
| Adapter (porta)    | `[Entidade]RepositoryAdapter`             | `EventTypeRepositoryAdapter`   |
| Adapter (web)      | `[Sistema][Entidade]Adapter`              | `CalComEventTypeAdapter`       |
| JPA Entity         | `[Entidade]JpaEntity`                     | `EventTypeJpaEntity`           |
| JPA Mapper         | `[Entidade]JpaMapper`                     | `EventTypeJpaMapper` (MapStruct) |
| Controller         | `[Entidade]Controller`                    | `EventTypeController`          |
| Request DTO        | `[Ação][Entidade]Request`                 | `CreateEventTypeRequest`       |
| Gateway (interface)| `[Sistema][Entidade]Gateway`              | `CalComEventTypeGateway`       |

---

## Organização de Testes

Testes espelham a estrutura de produção em `src/test/java/`:
- `application/service/` — Testes unitários dos Services/Use Cases (Mockito).
- `domain/entity/` — Testes da entidade de domínio.
- `domain/valueobject/` — Testes dos Value Objects.
- `domain/exception/` — Testes das exceções.
- `infrastructure/*/adapter/` — Testes dos adaptadores.

Cada classe de serviço tem seu próprio arquivo de teste (ex.: `CreateEventTypeServiceTest`).

---

## Segurança

- JWT validado via `SecurityFilter` (filtro customizado Spring Security).
- `SecurityConfig` define chain de segurança: stateless, sem CSRF, com CORS configurável.
- API Key JWT carregada de variável de ambiente `JWT_API_KEY`.
