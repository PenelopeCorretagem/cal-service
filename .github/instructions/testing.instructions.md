---
applyTo: 'src/test/java/**/*.java'
---

# Testes — Convenções do cal-service

Regras e padrões para criação de testes unitários e de integração no projeto.

---

## 1. Ferramentas

- **JUnit 5** — framework de testes.
- **Mockito** — mocking de dependências externas.
- **`@ExtendWith(MockitoExtension.class)`** — obrigatório em testes unitários.
- **H2 em memória** — banco de dados para testes de integração (perfil `dev`).

---

## 2. Estrutura e Nomenclatura

**Classe de teste:**
```
<ClasseTestada>Test
```

**Método de teste — escolha um dos formatos:**
```java
@Test
void shouldCreateEventType_whenCommandIsValid() { ... }

@Test
void createEventType_validCommand() { ... }
```

**Estrutura interna obrigatória (blocos comentados):**
```java
@Test
void shouldThrowException_whenTitleIsBlank() {
    // Given
    var command = new CreateEventTypeCommand("", "desc", 30);

    // When
    ThrowableAssert.ThrowingCallable action = () -> service.execute(command);

    // Then
    assertThatThrownBy(action).isInstanceOf(EventTypeTitleBlankException.class);
}
```

---

## 3. Cobertura Mínima

| Camada                        | Cobertura Mínima |
|-------------------------------|------------------|
| Use Cases (Application Services) | 80% em linhas |
| Domain Entities / Value Objects | Todos os factory methods e regras de negócio |
| Infrastructure Adapters       | Incentivados; mocks aceitos |

---

## 4. Regras de Escopo

- **Testes unitários**: nunca usar `@SpringBootTest`. Usar `@ExtendWith(MockitoExtension.class)`.
- **Testes de integração**: podem usar `@SpringBootTest` com perfil `test` e banco H2.
- Não testar lógica de negócio nos adapters de infraestrutura — testá-la nos use cases.
- Não mockar a entidade de domínio — testá-la diretamente com seus factory methods.

---

## 5. Mocks e Stubs

```java
@ExtendWith(MockitoExtension.class)
class CreateEventTypeServiceTest {

    @Mock
    private EventTypeRepositoryGateway repositoryGateway;

    @Mock
    private CalComEventTypeGateway calComGateway;

    @InjectMocks
    private CreateEventTypeService service;

    @Test
    void shouldReturnOutput_whenCommandIsValid() {
        // Given
        var command = new CreateEventTypeCommand("Visita Presencial", "Desc", 30, ...);
        when(repositoryGateway.save(any())).thenReturn(buildEventType());

        // When
        var output = service.execute(command);

        // Then
        assertThat(output.title()).isEqualTo("Visita Presencial");
        verify(calComGateway).createEventType(any());
    }
}
```

---

## 6. Entidades de Domínio — Testes Diretos

Testar diretamente via factory methods. Não mockar entidades:

```java
@Test
void shouldGenerateSlug_whenCreatingEventType() {
    // Given / When
    var eventType = EventType.createNew("Visita Presencial", "desc", 30, ...);

    // Then
    assertThat(eventType.getSlug().getValue()).isEqualTo("visita-presencial");
}
```

---

## 7. Relatórios

- Surefire gera relatórios em `target/surefire-reports/` após `./mvnw test`.
- JaCoCo gera relatório HTML em `target/site/jacoco/` após `./mvnw verify`.
- Verificar cobertura antes de considerar uma tarefa pronta.
