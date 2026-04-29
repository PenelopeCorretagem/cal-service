---
applyTo: 'src/main/java/**/*{Controller,Swagger,Request,Output}*.java'
---

# API Contract — Convenções do cal-service

Regras para design e documentação de endpoints REST no projeto.

---

## 1. Verbos HTTP e Semântica

| Operação           | Verbo    | Exemplo                          |
|--------------------|----------|----------------------------------|
| Leitura única      | `GET`    | `GET /event-types/{id}`          |
| Listagem           | `GET`    | `GET /event-types`               |
| Criação            | `POST`   | `POST /event-types`              |
| Atualização total  | `PUT`    | `PUT /event-types/{id}`          |
| Atualização parcial| `PATCH`  | `PATCH /event-types/{id}/toggle` |
| Remoção            | `DELETE` | `DELETE /event-types/{id}`       |

---

## 2. Códigos HTTP de Resposta

| Situação                         | Código                    |
|----------------------------------|---------------------------|
| Leitura ou atualização bem-sucedida | `200 OK`               |
| Criação bem-sucedida             | `201 Created`             |
| Remoção bem-sucedida             | `204 No Content`          |
| Dados de entrada inválidos       | `400 Bad Request`         |
| Não autenticado                  | `401 Unauthorized`        |
| Recurso não encontrado           | `404 Not Found`           |
| Violação de regra de negócio     | `422 Unprocessable Entity`|
| Erro interno                     | `500 Internal Server Error`|

---

## 3. Path Prefixes

| Bounded Context  | Path Base        |
|------------------|------------------|
| `eventtype`      | `/event-types`   |
| `appointment`    | `/appointments`  |

---

## 4. Controllers — Regras

- Controllers são **apenas adaptadores de entrada**: recebem request, chamam use case, retornam response.
- **Nunca** colocar lógica de negócio no controller.
- **Nunca** retornar entidades de domínio ou JPA — sempre usar Output Objects.
- Injetar a **interface** do use case, nunca a implementação concreta.

```java
@RestController
@RequestMapping("/event-types")
@RequiredArgsConstructor
public class EventTypeController implements EventTypeControllerSwagger {

    private final CreateEventTypeUseCase createEventTypeUseCase;

    @PostMapping
    public ResponseEntity<EventTypeOutput> create(@Valid @RequestBody CreateEventTypeRequest request) {
        var command = new CreateEventTypeCommand(request.title(), request.description(), request.duration());
        var output = createEventTypeUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
}
```

---

## 5. Documentação SpringDoc/OpenAPI — Obrigatória

Todo endpoint **novo ou modificado** deve ser documentado. Usar interface Swagger separada para manter o controller limpo:

```java
// Interface Swagger — mantém o controller limpo
@Tag(name = "Event Types", description = "Gerenciamento de tipos de agendamento")
public interface EventTypeControllerSwagger {

    @Operation(summary = "Criar tipo de agendamento")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "422", description = "Violação de regra de negócio")
    })
    ResponseEntity<EventTypeOutput> create(@RequestBody CreateEventTypeRequest request);
}
```

**Obrigatório:**
- `@Tag` na interface ou controller.
- `@Operation(summary = ...)` em cada método.
- `@ApiResponse` para cada código HTTP possível.
- `@Parameter` em path variables e query params não óbvios.

---

## 6. Request DTOs

- Usar **records Java 21** para request DTOs.
- Validações Bean Validation diretamente no record.
- Nomenclatura: `[Ação][Entidade]Request`.

```java
public record CreateEventTypeRequest(
    @NotBlank @Size(max = 100) String title,
    @NotBlank @Size(max = 500) String description,
    @Min(15) @Max(480) int duration,
    @NotNull Boolean notificationEnabled
) {}
```

---

## 7. Output DTOs

- Usar **records Java 21** para output DTOs.
- Nomenclatura: `[Entidade]Output`, `[Entidade]ListOutput`.
- Nunca expor IDs internos JPA ou campos sensíveis.

---

## 8. Tratamento de Erros

- Erros são tratados centralmente pelo `GlobalExceptionHandler` (`@RestControllerAdvice`).
- Não duplicar `try/catch` nos controllers para exceções já tratadas pelo handler.
- Exceções de domínio mapeiam para `422 Unprocessable Entity`.
- `NotFoundException` mapeia para `404 Not Found`.
- Validação Bean Validation mapeia para `400 Bad Request`.
