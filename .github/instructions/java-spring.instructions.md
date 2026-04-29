---
applyTo: 'src/main/java/**/*.java'
---

# Java & Spring — Convenções do cal-service

Regras de codificação Java e Spring aplicadas a todo código de produção do projeto.

---

## 1. Clean Architecture — Regras de Dependência

A direção de dependência é estrita e nunca pode ser invertida:

```
Infrastructure → Application → Domain
```

- **Domínio** (`domain/`): zero dependências externas. Nenhum import de Spring, JPA, HTTP ou bibliotecas de terceiros.
- **Aplicação** (`application/`): depende apenas do domínio. Usa interfaces (gateways/repositories) nunca implementações concretas.
- **Infraestrutura** (`infrastructure/`): implementa as interfaces do domínio/aplicação. Pode usar Spring, JPA, HTTP clients, etc.

---

## 2. Entidades de Domínio

**Obrigatório:**
- Instanciar novas entidades apenas via `Entity.createNew(...)`.
- Reconstituir do banco apenas via `Entity.reconstitute(...)`.
- Construtores públicos **não são permitidos** em entidades de domínio.
- Usar `@Getter` e `@Builder` do Lombok; **nunca** `@Data` em entidades de domínio.

```java
// Correto
EventType eventType = EventType.createNew(title, description, duration, ...);
EventType eventType = EventType.reconstitute(id, slug, title, ...);

// Errado — nunca usar construtor público
EventType eventType = new EventType(...);
```

---

## 3. Use Cases

- Cada use case **é uma interface** em `application/` que declara o contrato.
- Um `@Service` implementa a interface. O nome segue `[Ação][Entidade]Service`.
- Interfaces são injetadas — nunca as implementações diretamente.

```java
public interface CreateEventTypeUseCase {
    EventTypeOutput execute(CreateEventTypeCommand command);
}

@Service
@RequiredArgsConstructor
public class CreateEventTypeService implements CreateEventTypeUseCase {
    private final EventTypeGateway gateway;
    // ...
}
```

---

## 4. Command Objects

- Operações de escrita recebem **Command Objects** imutáveis (apenas dados, sem lógica).
- Nomenclatura: `[Ação][Entidade]Command`.
- Prefer `record` Java 21 para commands.

```java
public record CreateEventTypeCommand(String title, String description, int duration) {}
```

---

## 5. Output Objects

- Use cases **sempre** retornam Output Objects — nunca entidades de domínio ou entidades JPA diretamente.
- Nomenclatura: `[Entidade]Output`, `[Entidade]ListOutput`.
- Conversão feita por mapper MapStruct — sem mapeamento manual em loops.

---

## 6. Entidade JPA

- A entidade JPA (`[Entidade]JpaEntity`) é **completamente separada** da entidade de domínio.
- Não importar classes do domínio na entidade JPA sem passar pelo mapper.
- O mapper MapStruct (`[Entidade]JpaMapper`) é o único ponto de conversão.

---

## 7. Lombok — Uso Correto

| Anotação               | Usar em                                      |
|------------------------|----------------------------------------------|
| `@Getter`              | Entidades de domínio, JPA entities, Commands |
| `@Builder`             | Entidades de domínio, JPA entities           |
| `@RequiredArgsConstructor` | `@Service`, `@Component` (injeção por construtor) |
| `@Data`                | **Proibido** em entidades de domínio e JPA   |

---

## 8. Spring Annotations

- Usar esteriótipos corretos: `@Service`, `@Repository`, `@Component`, `@RestController`.
- Injeção por construtor com `@RequiredArgsConstructor` — nunca `@Autowired` em campo.
- Usar `final` em todos os campos injetados por construtor.
- `@Transactional` pertence à camada de infraestrutura/application services — **nunca** no domínio.

---

## 9. MapStruct — Mapeamento

- Todo mapeamento entre camadas passa por um mapper MapStruct explícito.
- Nunca converter manualmente em loops dentro de services ou adapters.
- Mappers ficam em `application/mapper/` (para Domain → Output) e `infrastructure/persistence/mapper/` (para JPA).

---

## 10. Value Objects

- Value Objects são imutáveis.
- `Slug`: derivado do título via normalização (lowercase, sem acentos, hifenizado). Nunca atribuir slug arbitrário.
- `Status` (enum): `PENDING`, `CONFIRMED`, `CONCLUDED`, `CANCELLED`. Use `isTerminal()` para verificar estado final.
