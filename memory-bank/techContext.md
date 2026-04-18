# Tech Context — cal-service

## Stack Principal

| Tecnologia           | Versão        | Papel                                              |
|----------------------|---------------|----------------------------------------------------|
| Java                 | 21            | Linguagem principal (usa records, sealed classes)  |
| Spring Boot          | 4.0.3         | Framework base                                     |
| Spring Data JPA      | (via Boot)    | Persistência ORM                                   |
| Spring Security      | (via Boot)    | Autenticação JWT                                   |
| Spring Web MVC       | (via Boot)    | Controllers REST                                   |
| Spring Validation    | (via Boot)    | Validação de DTOs de entrada                       |
| Hibernate            | (via Boot)    | JPA Provider                                       |
| MySQL                | (prod)        | Banco de dados relacional                          |
| H2                   | (dev/test)    | Banco em memória para desenvolvimento e testes     |
| Lombok               | (via Boot)    | Redução de boilerplate (builders, getters, etc.)   |
| MapStruct            | 1.5.5.Final   | Mapeamento entre objetos (JPA ↔ Domain ↔ Output)   |
| Auth0 Java JWT       | 4.4.0         | Criação/validação de tokens JWT                    |
| SpringDoc OpenAPI    | 2.7.0         | Documentação automática da API (Swagger UI)        |
| Jackson              | 2.21.1 (v2)   | Serialização JSON                                  |
| Maven                | (via wrapper) | Build tool                                         |
| JaCoCo               | 0.8.12        | Cobertura de testes                                |
| Mockito              | (via Boot)    | Mocking em testes unitários                        |

## Configuração por Perfil

### `dev` (default local)
```yaml
datasource: H2 em memória (event_type_db)
jpa.ddl-auto: create-drop
scheduler.sync.enabled: false
H2 Console: /h2-console
```

### `prod`
```yaml
datasource: MySQL (configurado via env vars)
jpa.ddl-auto: none
scheduler.sync.enabled: true (cron: 0 */5 * * * *)
```

## Variáveis de Ambiente Necessárias

| Variável              | Obrigatória | Descrição                                      |
|-----------------------|-------------|------------------------------------------------|
| `JWT_API_KEY`         | Sim         | Segredo para validação de tokens JWT            |
| `CALCOM_API_KEY`      | Sim         | Chave de API do Cal.com                         |
| `SPRING_PROFILES_ACTIVE` | Não      | Padrão: `dev`                                  |
| `SERVER_PORT`         | Não         | Porta do servidor (padrão: 8080)                |
| `CORS_ALLOWED_ORIGINS`| Não         | Origins permitidos (padrão: localhost:8080)     |
| `MONOLITH_BASE_URL`   | Não (prod)  | URL base do monolito                            |
| `MONOLITH_API_TOKEN`  | Não (prod)  | Token de autenticação para o monolito           |
| `MONOLITH_ESTATES_PATH`| Não        | Path dos empreendimentos no monolito            |
| `SYNC_CRON`           | Não         | Expressão cron do scheduler (padrão: `0 */5 * * * *`) |
| `SYNC_ENABLED`        | Não         | Habilita/desabilita o scheduler (padrão: true)  |

O arquivo `.env.example` está em `src/main/resources/` com todos os templates.

## Endpoints API

| Basepath           | Controller                  |
|--------------------|-----------------------------|
| `/event-types`     | `EventTypeController`       |
| `/appointments`    | `AppointmentController`     |
| `/swagger-ui`      | Swagger UI (SpringDoc)      |
| `/api-docs`        | OpenAPI JSON                |
| `/h2-console`      | H2 Console (dev only)       |

## Dependências Externas

### Cal.com API
- **Base URL**: `https://api.cal.com`
- **Versão V1**: `2024-06-14` — Usado para EventTypes
- **Versão V2**: `2024-08-13` — Usado para Bookings (Appointments)
- **Autenticação**: `CALCOM_API_KEY` via header

### Monolith API
- **Base URL**: configurável via `MONOLITH_BASE_URL`
- **Função**: Buscar lista de empreendimentos (`/api/empreendimentos`)
- **Adaptor**: `MonolithEstateAdapter` → `EstateGateway`

## Build e Execução

```bash
# Executar em modo dev (requer Java 21)
./mvnw spring-boot:run

# Executar testes
./mvnw test

# Gerar JAR
./mvnw package

# Relatório de cobertura JaCoCo
./mvnw verify
# Resultado em: target/site/jacoco/
```

> **Nota**: O ambiente local pode ter Java 8 no PATH. Garantir `JAVA_HOME` apontando para JDK 21 antes de executar o Maven.

## Estrutura de Arquivos de Configuração

```
src/main/resources/
├── application.yml          ← Config base (todos os perfis)
├── application-dev.yml      ← Overrides para dev (H2)
├── application-prod.yml     ← Overrides para prod (MySQL)
└── .env.example             ← Template de variáveis de ambiente
```
