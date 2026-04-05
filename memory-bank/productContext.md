# Product Context — cal-service

## Por Que Este Projeto Existe

A plataforma **Penélope C** precisa oferecer agendamentos entre clientes e corretores imobiliários. O Cal.com foi escolhido como motor de agendamentos, mas sua API é complexa e possui duas versões ativas (V1 e V2). O `cal-service` surgiu para:

1. Centralizar toda lógica de integração com o Cal.com em um único serviço.
2. Evitar que o frontend chame o Cal.com diretamente (vazamento de API Key, acoplamento).
3. Aplicar regras de negócio específicas do domínio imobiliário sobre os agendamentos.
4. Permitir que o monolito existente continue funcionando sem ser reescrito.

## Problema que Resolve

**Antes:** Frontend e/ou monolito chamavam o Cal.com diretamente, gerando:
- Acoplamento forte com a API externa.
- Duplicação de lógica de integração.
- Impossibilidade de aplicar regras de negócio centralizadas (ex.: vincular tipo de agendamento a um empreendimento).
- Riscos de segurança (expor API Key no cliente).

**Depois:** `cal-service` abstrai completamente o Cal.com. Outros serviços consomem apenas a API do `cal-service`.

## Como Funciona

### Fluxo Principal — Event Types

```
Frontend/Monolito → cal-service REST API
  → Application (Use Cases)
    → Domain (Entities, Rules)
      → Infrastructure (JPA, CalCom Adapter, Monolith Adapter)
        → MySQL (prod) / H2 (dev)
        → Cal.com API
        → Monolith API
```

### Fluxo de Agendamento

1. Cliente escolhe empreendimento → frontend busca Event Types via `cal-service`.
2. Cliente escolhe horário → frontend cria agendamento via `cal-service`.
3. `cal-service` valida regras, persiste e espelha no Cal.com via `CalComBookingAdapter`.
4. Status do agendamento evolui: `PENDING → CONFIRMED → CONCLUDED` ou `CANCELLED`.

### Sincronização Automática

Um scheduler (`SyncScheduler`) roda a cada 5 minutos (configurável) para sincronizar Event Types do Cal.com com o banco local, garantindo consistência.

## Perfis de Usuário que se Beneficiam

| Perfil          | Benefício                                                              |
|-----------------|------------------------------------------------------------------------|
| Corretor        | Tem seus tipos de visita gerenciados automaticamente                   |
| Cliente         | Pode agendar visitas com disponibilidade real do corretor              |
| Administrador   | Controla quais tipos de agendamento estão visíveis por empreendimento  |
| Time de dev     | Integra com Cal.com sem conhecer detalhes da API externa               |

## Metas de Experiência

- API RESTful documentada (Swagger/OpenAPI acessível em `/swagger-ui`).
- Respostas de erro padronizadas (`ApiErrorResponse`).
- Sem latência perceptível para o usuário final nas operações de leitura (cache local via JPA).
- Sincronização transparente em background.
