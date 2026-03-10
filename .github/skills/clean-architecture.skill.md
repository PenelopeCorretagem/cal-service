# Skill: Clean Architecture

## Regra de Ouro
Dependências apontam **sempre** para o centro:
```
Infrastructure → Application → Domain
```
Nenhuma camada interna pode referenciar uma camada externa.

---

## Estrutura de Pacotes

```
com.penelopec.calservice/
├── domain/                        # Núcleo — zero imports de framework
│   ├── entity/                    # Agregados e entidades
│   ├── valueobject/               # Value Objects imutáveis
│   ├── exception/                 # Exceções de domínio (RuntimeException)
│   ├── gateway/                   # Ports de saída (interfaces p/ serviços externos)
│   └── repository/                # Ports de saída (interfaces p/ persistência)
│
├── application/                   # Orquestração — depende apenas de domain
│   ├── command/                   # Records de entrada (input DTOs)
│   ├── output/                    # Records de saída (output DTOs)
│   ├── mapper/                    # Mappers domain → output (classes utilitárias)
│   ├── port/
│   │   └── in/                    # Interfaces dos use cases
│   └── service/                   # Implementações dos use cases
│
└── infrastructure/                # Frameworks & Drivers — depende de application e domain
    ├── calcom/
    │   ├── adapter/               # Implementações de gateways (@Component)
    │   ├── dto/                   # DTOs de integração externa (records)
    │   └── mapper/                # Mappers infra ↔ domain (classes utilitárias)
    └── config/                    # @Configuration, Filters, OpenAPI, Security
        └── properties/            # @ConfigurationProperties (records)
```

---

## Regras por Camada

### Domain (zero Spring)
| Regra | Detalhe |
|-------|---------|
| Imports proibidos | Nenhum de `org.springframework.*`, `jakarta.*`, `lombok.*` em entidades |
| Entidades | Classes com construtor `private`; criação via factory methods (`createNew`, `reconstitute`) |
| Value Objects | Classe `final`, imutável, com `equals`/`hashCode` baseado em valor |
| Exceções | Estendem `RuntimeException`; mensagens em **português** |
| Ports de saída | Interfaces puras (`gateway/`, `repository/`); retornam tipos do domínio |
| Validação | Métodos `private static validate*` no aggregado; lançam `IllegalArgumentException` |

#### Exemplo — Entity (factory methods)
```java
public class EventType {
    private Long id;
    private String title;
    private Slug slug;

    private EventType(Long id, String title, Slug slug, String description, Long estateId) { ... }

    public static EventType createNew(String title, String description, Long estateId) {
        validateTitle(title);
        validateEstateId(estateId);
        Slug slug = Slug.fromTitle(title);
        return new EventType(null, title, slug, description, estateId);
    }

    public static EventType reconstitute(Long id, String title, String slugValue, String description, Long estateId) {
        return new EventType(id, title, Slug.of(slugValue), description, estateId);
    }
}
```

#### Exemplo — Value Object
```java
public final class Slug {
    private final String value;

    private Slug(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Slug não pode ser vazio");
        this.value = value;
    }

    public static Slug fromTitle(String title) { ... }
    public static Slug of(String value) { return new Slug(value); }

    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { return value.hashCode(); }
}
```

### Application (orquestra domain)
| Regra | Detalhe |
|-------|---------|
| Commands | Java `record` com compact constructor validando inputs; mensagens em **português** |
| Outputs | Java `record` simples (sem lógica) |
| Use Case ports | Interface com método único `execute(...)` |
| Services | Classe POJO (sem `@Service`); recebe ports de saída via construtor |
| Mappers | Classe com construtor `private` + métodos `static` |
| Sem annotations Spring | Nenhum `@Component`, `@Service`, `@Transactional` nesta camada |

#### Exemplo — Command (record com validação)
```java
public record CreateEventTypeCommand(String title, String description, Long estateId) {
    public CreateEventTypeCommand {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Título é obrigatório");
        if (estateId == null)
            throw new IllegalArgumentException("ID do imóvel é obrigatório");
    }
}
```

#### Exemplo — Use Case Port
```java
public interface CreateEventTypeUseCase {
    EventTypeOutput execute(CreateEventTypeCommand command);
}
```

#### Exemplo — Service
```java
public class CreateEventTypeService implements CreateEventTypeUseCase {
    private final CalComEventTypeGateway calComGateway;
    private final EventTypeRepository eventTypeRepository;

    public CreateEventTypeService(CalComEventTypeGateway calComGateway,
                                  EventTypeRepository eventTypeRepository) {
        this.calComGateway = calComGateway;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public EventTypeOutput execute(CreateEventTypeCommand command) {
        EventType eventType = EventType.createNew(command.title(), command.description(), command.estateId());
        EventType created = calComGateway.create(eventType, false);
        EventType persisted = eventTypeRepository.save(created);
        return EventTypeOutputMapper.toOutput(persisted);
    }
}
```

### Infrastructure (Spring, REST, BD)
| Regra | Detalhe |
|-------|---------|
| Adapters | `@Component` implementando interfaces do domínio (gateway/repository) |
| DTOs externos | Java `record`; refletem contrato da API externa (Cal.com) |
| Mappers infra | Classe utilitária (`private` constructor + `static` methods) |
| Config de beans | `@Configuration` com `@Bean` instanciando serviços da camada application |
| Properties | `@ConfigurationProperties` como `record` (type-safe config) |
| RestClient | Spring 6+ `RestClient` (não `RestTemplate`); uso de `ParameterizedTypeReference` |
| Segurança | JWT via `OncePerRequestFilter`; sessão `STATELESS` |

#### Exemplo — Bean factory (desacoplamento)
```java
@Configuration
public class EventTypeConfig {
    @Bean
    public CreateEventTypeUseCase createEventTypeUseCase(CalComEventTypeGateway calComGateway,
                                                         EventTypeRepository repository) {
        return new CreateEventTypeService(calComGateway, repository);
    }
}
```

---

## Checklist ao Criar Novo Artefato

- [ ] Identificar a camada correta (domain, application, infrastructure)
- [ ] Verificar que nenhum import viola a direção de dependência
- [ ] Domain: sem annotations de framework
- [ ] Application services: sem `@Service` — instanciados via `@Bean` na infra
- [ ] Ports (interfaces): localizados em `domain/gateway/`, `domain/repository/` ou `application/port/in/`
- [ ] DTOs de entrada: `record` em `application/command/`
- [ ] DTOs de saída: `record` em `application/output/`
- [ ] DTOs externos: `record` em `infrastructure/**/dto/`
- [ ] Exceções de domínio: em `domain/exception/`, estendem `RuntimeException`

## Anti-Patterns (NÃO fazer)

- ❌ Anotar services da application com `@Service` ou `@Component`
- ❌ Importar Spring no domain
- ❌ Usar `RestTemplate` — preferir `RestClient`
- ❌ Colocar DTOs da API externa em `application/`
- ❌ Colocar regras de negócio em controllers ou adapters
- ❌ Repository Spring (`JpaRepository`) diretamente na camada application
