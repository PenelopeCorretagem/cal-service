# Skill: Domain Validation

## Filosofia
Validação é responsabilidade do **domínio**. Nenhuma regra de negócio vaza para controllers
ou configurações do Spring. O projeto utiliza três camadas de validação complementares.

---

## Camadas de Validação

### 1. Entidade — Guard Clauses (self-validation)
Validações essenciais que protegem invariantes do agregado.
Executadas dentro dos **factory methods** (`createNew`, `reconstitute`) ou mutators.

```java
public class EventType {

    public static EventType createNew(String title, String description, Long estateId) {
        validateTitle(title);       // guard clause
        validateEstateId(estateId); // guard clause
        Slug slug = Slug.fromTitle(title);
        return new EventType(null, title, slug, description, estateId);
    }

    public void assignExternalId(Long externalId) {
        if (externalId == null || externalId <= 0)
            throw new IllegalArgumentException("ID externo inválido: " + externalId);
        if (this.id != null)
            throw new IllegalStateException("EventType já possui ID atribuído: " + this.id);
        this.id = externalId;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Título do EventType não pode ser vazio");
    }

    private static void validateEstateId(Long estateId) {
        if (estateId == null)
            throw new IllegalArgumentException("ID do imóvel é obrigatório");
    }
}
```

**Regras:**
- Métodos `private static validate*` para cada invariante
- Lançam `IllegalArgumentException` para dados inválidos
- Lançam `IllegalStateException` para transições de estado inválidas
- Mensagens em **português**, claras e descritivas
- Nunca retornam `null`; falham rápido (fail-fast)

### 2. Value Object — Validação na Construção
Value Objects são imutáveis e validam no construtor ou factory.

```java
public final class Slug {
    private final String value;

    private Slug(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Slug não pode ser vazio");
        this.value = value;
    }

    public static Slug fromTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Título não pode ser vazio para gerar slug");

        String normalized = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (normalized.isBlank())
            throw new IllegalArgumentException("Slug resultante é vazio para o título: " + title);

        return new Slug(normalized);
    }
}
```

**Regras:**
- Classe `final` (não extensível)
- Campos `private final` (imutável)
- Construtor `private` — criação via factory (`of`, `fromTitle`, etc.)
- Validação no construtor e/ou factory
- `equals`/`hashCode` baseados no valor semântico

### 3. Command — Compact Constructor (Application Layer)
Records de entrada validam dados na fronteira application ↔ domain.

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

**Regras:**
- Usar **compact constructor** do record (sem parênteses)
- Validar apenas presença/formato dos campos de entrada
- NÃO duplicar regras de negócio do domínio
- Lançar `IllegalArgumentException`
- Mensagens em **português**

---

## Padrão Notification (quando usar)

Para cenários com **múltiplas regras de validação** que precisam ser reportadas de uma vez
(ao invés de fail-fast), usar o padrão Notification:

```java
public class Notification {
    private final List<String> errors = new ArrayList<>();

    public void addError(String message) { errors.add(message); }
    public boolean hasErrors()           { return !errors.isEmpty(); }
    public List<String> getErrors()      { return List.copyOf(errors); }

    public void throwIfInvalid() {
        if (hasErrors())
            throw new DomainValidationException(errors);
    }
}
```

Uso no domínio:
```java
public static EventType createNew(String title, String description, Long estateId) {
    var notification = new Notification();
    if (title == null || title.isBlank())   notification.addError("Título é obrigatório");
    if (estateId == null)                    notification.addError("ID do imóvel é obrigatório");
    notification.throwIfInvalid();

    return new EventType(null, title, Slug.fromTitle(title), description, estateId);
}
```

> **Quando usar Notification vs Guard Clause:**
> - Guard Clause (fail-fast): cenários simples, 1-2 validações, criação de VOs
> - Notification: formulários complexos, múltiplas regras, APIs que devem retornar todos os erros

---

## Strategy Pattern para Validação

Para regras de validação que variam por contexto (ex: diferentes tipos de imóvel), encapsular em strategies:

```java
// domain/strategy/
public interface ValidationStrategy<T> {
    Notification validate(T target);
}

// Implementação específica
public class EventTypeCreationValidation implements ValidationStrategy<EventType> {
    @Override
    public Notification validate(EventType eventType) {
        var notification = new Notification();
        if (eventType.getTitle().length() > 100)
            notification.addError("Título excede 100 caracteres");
        return notification;
    }
}
```

---

## Result Pattern (operações que podem falhar)

Para operações do domínio que podem falhar de maneira previsível:

```java
public sealed interface Result<T> {
    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(List<String> errors) implements Result<T> {}

    static <T> Result<T> success(T value)          { return new Success<>(value); }
    static <T> Result<T> failure(String... errors)  { return new Failure<>(List.of(errors)); }

    default boolean isSuccess() { return this instanceof Success; }
}
```

---

## Exceções de Domínio

| Exceção | Uso | Hierarquia |
|---------|-----|------------|
| `IllegalArgumentException` | Dados de entrada inválidos (guard clauses) | JDK |
| `IllegalStateException` | Transição de estado inválida | JDK |
| `EventTypeNotFoundException` | Recurso não encontrado | `RuntimeException` |
| `EventTypeCreationException` | Falha ao criar (ex: erro na API externa) | `RuntimeException` |
| `EventTypeDeletionException` | Falha ao deletar | `RuntimeException` |

**Convenções:**
- Todas estendem `RuntimeException` (unchecked)
- Construtores: `(String message)` e/ou `(String message, Throwable cause)`
- Mensagens em **português**
- Pacote: `domain/exception/`

---

## Checklist — Adicionando Nova Validação

- [ ] Identificar a camada (entidade, VO ou command)
- [ ] Guard clause para invariantes simples; Notification para validações compostas
- [ ] Exceção adequada (`IllegalArgumentException`, domínio-específica, etc.)
- [ ] Mensagem em **português**
- [ ] Testes cobrindo cenários válidos e inválidos
- [ ] Nenhuma dependência de framework na lógica de validação
