---
applyTo: 'src/**/*.java'
---

# Segurança — Convenções do cal-service

Regras de segurança aplicadas a todo código Java do projeto. Baseadas no OWASP Top 10.

---

## 1. Credenciais e Segredos

- **Nunca** hardcodar tokens, API keys, senhas ou segredos no código-fonte.
- Todas as credenciais devem ser fornecidas via variáveis de ambiente.
- Variáveis obrigatórias: `JWT_API_KEY`, `CALCOM_API_KEY`, `MONOLITH_API_TOKEN`.
- Nunca logar o valor de variáveis de ambiente sensíveis.

```java
// Errado — hardcoded
private static final String API_KEY = "cal_live_abc123";

// Correto — via @Value
@Value("${calcom.api-key}")
private String calComApiKey;
```

---

## 2. Validação de Entrada (OWASP A03 — Injection)

- **Todo input de API** deve ser validado com `@Valid` no controller e constraints do Bean Validation.
- Nunca confiar em dados vindos do cliente sem validação.
- Usar constraints específicas: `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Max`, `@Pattern`.

```java
@PostMapping
public ResponseEntity<EventTypeOutput> create(@Valid @RequestBody CreateEventTypeRequest request) { ... }
```

**Constraints na request:**
```java
public record CreateEventTypeRequest(
    @NotBlank @Size(max = 100) String title,
    @NotBlank String description,
    @Min(15) @Max(480) int duration
) {}
```

---

## 3. Autenticação JWT (OWASP A07 — Auth Failures)

- Tokens JWT são validados exclusivamente em `SecurityFilter`.
- **Não duplicar** lógica de validação JWT em controllers, services ou adapters.
- Não criar rotas autenticadas sem passar pelo filtro de segurança.
- Não expor endpoints sensíveis em `SecurityConfig.permitAll()` sem revisão explícita.

---

## 4. CORS (OWASP A05 — Security Misconfiguration)

- CORS é configurado **apenas** em `SecurityConfig`.
- **Nunca** adicionar headers CORS manualmente nos controllers (`Access-Control-Allow-Origin`, etc.).
- Origins permitidos são controlados pela variável de ambiente `CORS_ALLOWED_ORIGINS`.

---

## 5. Exposição de Dados Sensíveis (OWASP A02 — Cryptographic Failures)

- Responses de API nunca devem incluir dados internos como: stack traces, IDs internos de JPA, credenciais, tokens.
- Erros são tratados pelo `GlobalExceptionHandler` — que retorna `ApiErrorResponse` padronizado sem stack trace.
- Em logs, nunca logar o corpo completo de requests que possam conter dados sensíveis.

---

## 6. Controle de Acesso (OWASP A01 — Broken Access Control)

- Toda rota deve ser autenticada por padrão, exceto as explicitamente liberadas em `SecurityConfig`.
- Rotas liberadas atualmente: `/swagger-ui/**`, `/api-docs/**`, `/h2-console/**` (apenas dev).
- Não adicionar `@PermitAll` ou `antMatchers(...).permitAll()` sem aprovação explícita.

---

## 7. SQL e ORM (OWASP A03 — Injection)

- Usar apenas consultas JPA com parâmetros nomeados (`@Query` com `:param` ou métodos derivados).
- **Nunca** concatenar strings para construir queries SQL.
- Usar `JpaRepository` e seus métodos para operações CRUD padrão.

```java
// Correto
@Query("SELECT e FROM EventTypeJpaEntity e WHERE e.calcomEventTypeId = :calcomId")
Optional<EventTypeJpaEntity> findByCalcomId(@Param("calcomId") Long calcomId);

// Errado — injeção SQL possível
@Query("SELECT e FROM EventTypeJpaEntity e WHERE e.slug = '" + slug + "'")
```

---

## 8. Gatilhos de Revisão de Risco

Qualquer uma das situações abaixo exige revisão de segurança antes de fazer commit:

- Nova rota adicionada ao `SecurityConfig.permitAll()`.
- Novo campo de senha, token ou segredo em qualquer classe.
- Novo endpoint que retorna dados de usuário ou integrações externas.
- Alteração em `SecurityFilter` ou `SecurityConfig`.
- Dependência nova com versão não verificada (verificar CVE no [NIST NVD](https://nvd.nist.gov/)).
