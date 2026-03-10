# Copilot Instructions — CAL SERVICES

## Stack
- Java 21, Spring Boot 4.0.X, JPA, Maven, MySQL
- Clean Architecture + DDD + TDD
- Lombok, OAuth2/JWT

## Projeto
Cal Services - Integração com os serviços do cal.com.

## Regra de Ouro
Dependências apontam para o centro: **Infrastructure → Application → Domain**
- `domain/`: zero imports de framework; entidades, value objects, strategies
- `application/`: orquestra domain; use cases, ports, DTOs
- `infrastructure/`: Spring, JPA, batch JDBC, controllers, configurações, etc.

## Idioma
- **Português**: `@DisplayName`, mensagens de erro e `description` nos testes
- **Inglês**: nomes de classes, métodos, variáveis, pacotes

## Skills Disponíveis (leia antes de agir)
- `clean-architecture` — estrutura de camadas e pacotes
- `domain-validation` — padrão Notification + Strategy + Result
- `test-conventions` — BDD, fixtures, @Nested
- `spec-format` — formato de especificação e regras RV

> Regras detalhadas estão nas skills. NÃO repita aqui.

## Variáveis Maven
```bat
set PROJECT_BASEDIR=
set JAVA_HOME=
set MAVEN_SETTINGS_FILE=
set MAVEN_REPO_LOCAL=
```

## Comando Base (Estrutura)
```bat
cmd /c "setlocal&& set JAVA_HOME=%JAVA_HOME%&& cd /d %PROJECT_BASEDIR%&& .\mvnw.cmd -s %MAVEN_SETTINGS_FILE% -Dmaven.repo.local=%MAVEN_REPO_LOCAL% <GOAL_MAVEN>"
```

## Comandos Base (Prontos)
```bat
cmd /c "setlocal&& set JAVA_HOME=%JAVA_HOME%&& cd /d %PROJECT_BASEDIR%&& .\mvnw.cmd -s %MAVEN_SETTINGS_FILE% -Dmaven.repo.local=%MAVEN_REPO_LOCAL% clean verify"

cmd /c "setlocal&& set JAVA_HOME=%JAVA_HOME%&& cd /d %PROJECT_BASEDIR%&& .\mvnw.cmd -s %MAVEN_SETTINGS_FILE% -Dmaven.repo.local=%MAVEN_REPO_LOCAL% test"

cmd /c "setlocal&& set JAVA_HOME=%JAVA_HOME%&& cd /d %PROJECT_BASEDIR%&& .\mvnw.cmd -s %MAVEN_SETTINGS_FILE% -Dmaven.repo.local=%MAVEN_REPO_LOCAL% verify"
```

## Evitar Download Repetido
- Use sempre `-Dmaven.repo.local=%MAVEN_REPO_LOCAL%` para fixar cache local do projeto.
- Evite `clean` em toda execução; prefira `test`/`verify` no dia a dia.
- Faça um prefetch quando necessário:
```bat
cmd /c "setlocal&& set JAVA_HOME=%JAVA_HOME%&& cd /d %PROJECT_BASEDIR%&& .\mvnw.cmd -s %MAVEN_SETTINGS_FILE% -Dmaven.repo.local=%MAVEN_REPO_LOCAL% dependency:go-offline"
```
- Depois do cache aquecido, rode offline quando quiser velocidade máxima:
```bat
cmd /c "setlocal&& set JAVA_HOME=%JAVA_HOME%&& cd /d %PROJECT_BASEDIR%&& .\mvnw.cmd -o -s %MAVEN_SETTINGS_FILE% -Dmaven.repo.local=%MAVEN_REPO_LOCAL% test"
```
