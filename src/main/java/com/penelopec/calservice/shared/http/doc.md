# Modulo shared/http

Infraestrutura centralizada para comunicacao HTTP entre o cal-service e sistemas externos.

## Estrutura Atual

```text
shared/http/
├── config/
│   └── RestClientBuilderFactory.java
├── executor/
│   └── RestExecutor.java
├── log/
│   └── LoggingInterceptor.java
└── exception/
    ├── RemoteServiceException.java
    ├── RemoteNotFoundException.java
    └── RemoteUnauthorizedException.java
```

## Objetivo

- Padronizar criacao de `RestClient` com interceptors comuns.
- Padronizar traducao de erros de transporte para excecoes de integracao.
- Reduzir boilerplate em adapters de infraestrutura.
- Evitar vazamento de dados sensiveis nos logs.

## RestClientBuilderFactory

`RestClientBuilderFactory` e o ponto unico para iniciar builders HTTP.

```java
@Component
public class RestClientBuilderFactory {
  public RestClient.Builder builder(String baseUrl) {
    return RestClient.builder()
      .baseUrl(baseUrl)
      .requestInterceptors(interceptors -> interceptors.add(loggingInterceptor));
  }
}
```

### Uso recomendado em configs

```java
@Bean
RestClient monolithRestClient(MonolithProperties properties,
                              RestClientBuilderFactory factory) {
  RestClient.Builder builder = factory.builder(properties.api().baseUrl());

  if (properties.api().token() != null && !properties.api().token().isBlank()) {
    builder.defaultHeader("Authorization", "Bearer " + properties.api().token());
  }

  return builder.build();
}
```

## LoggingInterceptor

Interceptor global para chamadas outbound.

Comportamento atual:

- Logs em `DEBUG` (nao em `INFO`) para evitar ruido em producao.
- Loga metodo HTTP, URI sanitizada e duracao total da chamada em ms.
- Mascara query params sensiveis (`token`, `secret`, `password`, `api_key`, `authorization`).

Exemplo:

```text
OUTBOUND GET https://api.externa.com/v1/resource?token=***
RESPONSE 200 GET https://api.externa.com/v1/resource?token=*** (83 ms)
```

## RestExecutor

`RestExecutor` encapsula chamadas e converte erros de transporte em excecoes semanticamente padronizadas.

### Metodos

- `execute(system, supplier)`: resposta obrigatoria (null vira erro).
- `executeOrNull(system, supplier)`: resposta opcional (null permitido).
- `executeVoid(system, runnable)`: operacoes sem retorno.

### Mapeamento HTTP -> excecao

- `404` -> `RemoteNotFoundException`
- `401`, `403` -> `RemoteUnauthorizedException`
- outros erros HTTP -> `RemoteServiceException`
- timeout/conexao -> `RemoteServiceException`

## Integracao com camada de erro

Adapters devem capturar `RemoteServiceException` e converter para `GatewayException` com `ErrorContract` do contexto.

Nao deixe `RestClientException` subir para controller.

## SecurityFilter (relacao com shared/http)

Embora esteja em `eventtype.infrastructure.config`, o filtro participa do hardening de borda HTTP:

- Ignora validacao de token quando ja existe autenticacao no contexto.
- Trata token bearer em branco como ausente.
- Em falha de validacao, limpa o contexto e segue sem autenticar.
- Nao loga token bruto.

## Testes Relevantes

- `LoggingInterceptorTest`: mascara de query params sensiveis.
- `SecurityFilterTest`: comportamento resiliente para token invalido/ausente e autenticacao preexistente.

## O que nao fazer

- Nao criar `RestClient` direto com `RestClient.builder()` em cada config.
- Nao instanciar `LoggingInterceptor` manualmente.
- Nao logar header `Authorization` ou valores de token.
- Nao duplicar try/catch de transporte fora do `RestExecutor` sem justificativa.
