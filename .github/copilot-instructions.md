# Copilot Instructions — cal-service

Regras gerais do repositório para o GitHub Copilot. Aplicam-se a todos os arquivos do projeto.

---

## 1. Visão Geral do Projeto

`cal-service` é um microserviço Spring Boot que funciona como **camada de integração centralizada com o Cal.com** dentro do ecossistema Penélope C (plataforma imobiliária). O frontend nunca chama o Cal.com diretamente — toda comunicação passa por este serviço.

**Bounded contexts ativos:**
- `eventtype` — Tipos de agendamento (vinculados a empreendimentos)
- `appointment` — Agendamentos concretos com ciclo de vida de status

---

## 2. Arquitetura

### Clean Architecture + DDD

Cada bounded context segue **três camadas** com regra de dependência estrita:

```
Infrastructure → Application → Domain
```

O **domínio nunca importa** nada de fora de si mesmo. A camada de aplicação usa interfaces (ports/gateways) que a infraestrutura implementa.

### Estrutura de pacotes

```
com.penelopec.calservice/
├── <context>/
│   ├── domain/         ← Entidades, Value Objects, Gateways (interfaces), Repository (interface)
│   ├── application/    ← Use Cases (services), Commands, Outputs, Ports, Mappers
│   └── infrastructure/ ← Controllers, JPA Adapters, Web Adapters, Config, Scheduler
```

### Padrões obrigatórios

- **Factory methods**: `Entity.createNew(...)` para novas instâncias; `Entity.reconstitute(...)` para reconstituição do banco. Construtores públicos **não são permitidos** nas entidades de domínio.
- **Command Objects**: operações de escrita recebem objetos Command imutáveis (sem lógica, apenas dados).
- **Output Objects**: use cases retornam Output Objects (DTOs de saída), nunca entidades de domínio diretamente.
- **JPA Entity separada**: a entidade JPA é um arquivo separado da entidade de domínio; o mapper (MapStruct) faz a conversão.
- **Use Case como Interface + Service**: cada use case é uma interface implementada por um `@Service`. A interface declara o contrato; o service implementa.

---

## 3. Código — Convenções

### Java

- **Java 21** — use records, sealed classes e pattern matching quando for mais expressivo.
- **Lombok** — use `@Builder`, `@Getter`, `@RequiredArgsConstructor` para reduzir boilerplate. Evite `@Data` em entidades de domínio.
- **MapStruct** — use para todos os mapeamentos entre camadas. Não faça conversão manual em loops.
- **Spring Annotations** — use esteriótipos corretos: `@Service`, `@Repository`, `@Component`, `@RestController`.
- Use `final` em campos de dependência injetados por construtor.
- Evite herança em favor de composição.
- Nomes em inglês para classes, métodos e variáveis. Comentários em português (se necessário).

### Proibições

- Nunca deixe lógica de negócio nos controllers ou adapters.
- Nunca importe classes de domínio diretamente em JPA/infraestrutura sem passar pelo mapper.
- Nunca retorne entidades de domínio ou JPA diretamente no controller — sempre use Output Objects.
- Nunca adicione `@Transactional` em use cases de domínio (pertence à camada de infraestrutura/application).

---

## 4. Testes

### Cobertura mínima

- **Use Cases (Application Services)**: cobertura mínima de 80% em linhas.
- **Domain Entities e Value Objects**: todos os factory methods e regras de negócio devem ter testes.
- **Adapters (Infrastructure)**: testes de integração são incentivados; testes unitários com mocks são aceitos.

### Convenções de teste

- Usar **JUnit 5** + **Mockito**.
- Nome da classe: `<ClasseTestada>Test`.
- Nome do método: `should<Comportamento>_when<Contexto>` ou `<comportamento>_<contexto>`.
- Organizar o corpo do teste em blocos `// Given`, `// When`, `// Then`.
- Usar `@ExtendWith(MockitoExtension.class)` para testes unitários.
- Não usar `@SpringBootTest` em testes unitários — apenas em testes de integração.

### Ferramentas

- **Surefire** para execução e relatórios em `target/surefire-reports/`.
- **JaCoCo** para cobertura em `target/site/jacoco/`.

---

## 5. Segurança

- Nunca exponha dados sensíveis (tokens, senhas, secrets) em logs, comentários ou testes.
- Nunca hardcode credenciais; use variáveis de ambiente.
- Valide todas as entradas de API no `@RestController` usando `@Valid` e constraints do Bean Validation.
- Siga as diretrizes OWASP Top 10: previna injeção, broken auth, exposição de dados sensíveis, etc.
- Tokens JWT são validados via `SecurityFilter` — não duplique essa lógica.
- CORS é configurado em `SecurityConfig` — não adicione headers CORS manualmente nos controllers.

---

## 6. API REST

- Use verbos HTTP semânticos: `GET` leitura, `POST` criação, `PUT`/`PATCH` atualização, `DELETE` remoção.
- Retorne códigos HTTP corretos: `200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found`, `422 Unprocessable Entity`, `500 Internal Server Error`.
- Documente todos os endpoints com anotações SpringDoc/OpenAPI (`@Operation`, `@ApiResponse`, `@Parameter`).
- Prefixo de path: `/event-types` para eventtype, `/appointments` para appointment.
- Use interfaces Swagger separadas (ex.: `EventTypeControllerSwagger`) para manter o controller limpo.

---

## 7. Definição de Pronto (DoD)

Uma tarefa ou feature é considerada pronta quando:

1. Código implementado seguindo Clean Architecture e todas as convenções acima.
2. Testes unitários criados e passando (`mvnw test`).
3. Cobertura mínima atingida para a camada de aplicação.
4. Nenhum erro de compilação (`mvnw compile`).
5. Nenhum segredo ou credencial exposta no código.
6. Endpoints documentados com SpringDoc/OpenAPI se houver controller novo ou modificado.
7. Memory Bank atualizado: `activeContext.md`, `progress.md` e tarefa correspondente em `tasks/`.

---

## 8. Comandos Úteis

```bash
# Build e testes
./mvnw clean verify

# Apenas testes
./mvnw test

# Build sem testes
./mvnw clean package -DskipTests

# Rodar localmente (perfil dev, H2 em memória)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Relatório de cobertura (após mvnw test)
# Abrir: target/site/jacoco/index.html
```

> **Atenção**: garanta que `JAVA_HOME` aponte para JDK 21 antes de executar o Maven.
