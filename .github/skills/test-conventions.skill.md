# Skill: Test Conventions

## Stack de Testes
- **JUnit 5** (Jupiter) — framework principal
- **Mockito** — mocks e stubs para ports/gateways
- **AssertJ** — asserções fluentes (preferido sobre `assertEquals`)
- **Spring Boot Test** — testes de integração (controllers, security)
- **H2** — banco em memória para testes de persistência

---

## Estrutura de Diretórios

```
src/test/java/com/kennerlima/eventtypeservice/
├── domain/
│   ├── entity/
│   │   └── EventTypeTest.java
│   └── valueobject/
│       └── SlugTest.java
├── application/
│   ├── command/
│   │   ├── CreateEventTypeCommandTest.java
│   │   └── UpdateEventTypeCommandTest.java
│   └── service/
│       ├── CreateEventTypeServiceTest.java
│       ├── GetEventTypeServiceTest.java
│       ├── ListEventTypesServiceTest.java
│       ├── UpdateEventTypeServiceTest.java
│       └── DeleteEventTypeServiceTest.java
└── infrastructure/
    ├── calcom/
    │   └── adapter/
    │       └── CalComEventTypeAdapterTest.java  (integração)
    └── config/
        └── SecurityFilterTest.java              (integração)
```

> **Espelhar** a estrutura do `src/main/java`. Uma classe = um arquivo de teste correspondente.

---

## Convenções de Nomenclatura

### Classe de teste
```
<NomeDaClasse>Test.java
```

### Métodos — padrão BDD (Given-When-Then)
```
should<ResultadoEsperado>_when<Cenário>
```

Exemplos:
```java
void shouldCreateEventType_whenDataIsValid()
void shouldThrowException_whenTitleIsBlank()
void shouldNormalizeSlug_whenTitleHasSpecialCharacters()
void shouldThrowCreationException_whenGatewayFails()
```

---

## Idioma

| Elemento | Idioma |
|----------|--------|
| Nome de método de teste | **Inglês** |
| `@DisplayName` | **Português** |
| Mensagens em asserções | **Português** (quando usando AssertJ `.withFailMessage()`) |
| `@Nested` `@DisplayName` | **Português** (descreve grupo de cenários) |

---

## Estrutura com @Nested + BDD (obrigatório sempre)

Agrupar testes por método/funcionalidade usando `@Nested`.
Utilizar blocos **Given / When / Then** separados por comentários em todos os testes, sem exceção:

```java
class EventTypeTest {

    @Nested
    @DisplayName("createNew")
    class CreateNew {

        @Test
        @DisplayName("Deve criar EventType com dados válidos")
        void shouldCreateEventType_whenDataIsValid() {
            // Given
            String title = "Visita Guiada";
            String description = "Descrição";
            Long estateId = 1L;

            // When
            EventType eventType = EventType.createNew(title, description, estateId);

            // Then
            assertThat(eventType.getTitle()).isEqualTo("Visita Guiada");
            assertThat(eventType.getSlugValue()).isEqualTo("visita-guiada");
            assertThat(eventType.getId()).isNull();
        }

        @Test
        @DisplayName("Deve lançar exceção quando título é vazio")
        void shouldThrowException_whenTitleIsBlank() {
            // Given
            String blankTitle = "";

            // When / Then
            assertThatThrownBy(() -> EventType.createNew(blankTitle, "desc", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Título");
        }

        @Test
        @DisplayName("Deve lançar exceção quando estateId é nulo")
        void shouldThrowException_whenEstateIdIsNull() {
            // When / Then
            assertThatThrownBy(() -> EventType.createNew("Título", "desc", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("imóvel");
        }
    }

    @Nested
    @DisplayName("assignExternalId")
    class AssignExternalId {

        @Test
        @DisplayName("Deve atribuir ID externo quando EventType não possui ID")
        void shouldAssignId_whenEventTypeHasNoId() { ... }

        @Test
        @DisplayName("Deve lançar exceção quando ID externo é inválido")
        void shouldThrowException_whenExternalIdIsInvalid() { ... }
    }
}
```

---

## Fixtures & Test Data

### Constantes reutilizáveis
Definir no topo da classe de teste ou em classe utilitária:

```java
class CreateEventTypeServiceTest {

    private static final String VALID_TITLE = "Visita Guiada";
    private static final String VALID_DESCRIPTION = "Visita ao imóvel com corretor";
    private static final Long VALID_ESTATE_ID = 1L;
    private static final Long VALID_EXTERNAL_ID = 100L;
}
```

### Factory methods para objetos de teste
```java
private EventType createValidEventType() {
    return EventType.createNew(VALID_TITLE, VALID_DESCRIPTION, VALID_ESTATE_ID);
}

private EventType createPersistedEventType() {
    return EventType.reconstitute(VALID_EXTERNAL_ID, VALID_TITLE, "visita-guiada",
                                  VALID_DESCRIPTION, VALID_ESTATE_ID);
}

private CreateEventTypeCommand createValidCommand() {
    return new CreateEventTypeCommand(VALID_TITLE, VALID_DESCRIPTION, VALID_ESTATE_ID);
}
```

---

## Padrão BDD (Given-When-Then)

Todo teste segue obrigatoriamente o padrão BDD, com seções separadas por comentários `// Given`, `// When`, `// Then`:

```java
@Test
@DisplayName("Deve criar EventType e persistir no repositório")
void shouldCreateAndPersist_whenCommandIsValid() {
    // Given
    var command = createValidCommand();
    var persisted = createPersistedEventType();

    when(calComGateway.create(any(), eq(false))).thenReturn(persisted);
    when(repository.save(any())).thenReturn(persisted);

    // When
    EventTypeOutput result = service.execute(command);

    // Then
    assertThat(result.title()).isEqualTo(VALID_TITLE);
    assertThat(result.id()).isEqualTo(VALID_EXTERNAL_ID);
    verify(calComGateway).create(any(), eq(false));
    verify(repository).save(any());
}
```

> **Regra obrigatória:** não combinar etapas (`When/Then`) e não omitir nenhuma etapa.
> Mesmo em testes de validação/exceção, manter os 3 blocos explícitos para padronização.

---

## Testes por Camada

### Domain (unitários puros)
- Sem mocks, sem Spring, sem DI
- Testar: factory methods, validações, mutators, value objects
- Asserts com AssertJ

```java
class SlugTest {

    @Nested
    @DisplayName("fromTitle")
    class FromTitle {

        @Test
        @DisplayName("Deve gerar slug normalizado a partir do título")
        void shouldNormalizeSlug_whenTitleIsValid() {
            // Given
            String title = "Visita Guiada";

            // When
            Slug slug = Slug.fromTitle(title);

            // Then
            assertThat(slug.getValue()).isEqualTo("visita-guiada");
        }

        @Test
        @DisplayName("Deve remover caracteres especiais do slug")
        void shouldRemoveSpecialChars_whenTitleContainsThem() {
            // Given
            String title = "Café & Pão!";

            // When
            Slug slug = Slug.fromTitle(title);

            // Then
            assertThat(slug.getValue()).isEqualTo("caf-po");
        }
    }
}
```

### Application (unitários com mocks)
- Mockito para gateways e repositories (ports de saída)
- `@ExtendWith(MockitoExtension.class)`
- Verificar orquestração e interações

```java
@ExtendWith(MockitoExtension.class)
class CreateEventTypeServiceTest {

    @Mock private CalComEventTypeGateway calComGateway;
    @Mock private EventTypeRepository repository;
    @InjectMocks private CreateEventTypeService service;

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Deve criar EventType via gateway e persistir")
        void shouldCreateAndPersist_whenCommandIsValid() {
            // Given
            var command = new CreateEventTypeCommand("Título", "Desc", 1L);
            var created = EventType.reconstitute(10L, "Título", "titulo", "Desc", 1L);

            when(calComGateway.create(any(), eq(false))).thenReturn(created);
            when(repository.save(any())).thenReturn(created);

            // When
            var result = service.execute(command);

            // Then
            assertThat(result.title()).isEqualTo("Título");
            verify(calComGateway).create(any(), eq(false));
            verify(repository).save(any());
        }
    }
}
```

### Infrastructure (integração)
- Testes de controller: `@WebMvcTest` + `@MockBean`
- Testes de persistência: `@DataJpaTest` com H2
- Testes de adapter: mockar `RestClient` ou usar `MockRestServiceServer`

---

## Regras Gerais

| Regra | Detalhe |
|-------|---------|
| Um assert lógico por teste | Múltiplos `assertThat` OK se verificam a mesma coisa |
| Sem lógica condicional | Nada de `if`/`for` dentro de testes |
| Sem dependência entre testes | Cada teste é independente; usar `@BeforeEach` se necessário |
| @DisplayName obrigatório | Em todos os `@Test` e `@Nested` |
| Dados literais nos testes | Preferir constantes locais sobre valores mágicos |
| BDD obrigatório | Todos os testes devem conter `Given`, `When` e `Then` explícitos |
| Não testar getters/setters | A menos que contenham lógica |
| Cobertura mínima | 80% de cobertura de linhas no domínio e application |

---

## AssertJ — Padrões Preferidos

```java
// Igualdade
assertThat(result.title()).isEqualTo("Visita Guiada");

// Nulidade
assertThat(result.getId()).isNull();
assertThat(result.getId()).isNotNull();

// Exceções
assertThatThrownBy(() -> EventType.createNew("", "desc", 1L))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("Título");

// Coleções
assertThat(results).hasSize(3);
assertThat(results).extracting(EventTypeOutput::title)
    .containsExactly("A", "B", "C");

// Sem exceção
assertThatCode(() -> entity.assignExternalId(1L))
    .doesNotThrowAnyException();
```

---

## Checklist — Novo Teste

- [ ] Classe espelha a estrutura de `src/main`
- [ ] `@Nested` agrupando por método/funcionalidade
- [ ] `@DisplayName` em português em todos os `@Test` e `@Nested`
- [ ] Método segue padrão `should<Resultado>_when<Cenário>` (inglês)
- [ ] Padrão BDD obrigatório (Given/When/Then) com comentários de seção em todos os testes
- [ ] Fixtures reutilizáveis (constantes + factory methods)
- [ ] Sem Spring nos testes de domain/application (exceto integração)
- [ ] AssertJ para todas as asserções
