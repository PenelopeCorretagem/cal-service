# Project Brief — cal-service

## Visão Geral

`cal-service` é um microserviço Spring Boot que atua como **camada de integração centralizada com o Cal.com**, plataforma de agendamentos. Faz parte do ecossistema **Penélope C** — uma plataforma imobiliária que conecta corretores, clientes e empreendimentos.

## Objetivo Principal

Centralizar toda a comunicação com o Cal.com (API v1/v2), abstraindo a complexidade de integração do frontend e do monolito existente. O frontend **nunca** chama o Cal.com diretamente.

## Escopo do Serviço

### Funcionalidades Implementadas
- **Gerenciamento de Event Types**: CRUD completo, vinculação com empreendimento (`estateId`), toggle de visibilidade, sincronização automática via scheduler.
- **Agendamentos (Appointments)**: Criação, consulta, listagem, reagendamento, cancelamento, confirmação e conclusão.
- **Segurança**: Autenticação via JWT (Auth0).
- **Integração Cal.com**: Adaptadores para API Cal.com V1 (EventTypes) e V2 (Bookings).
- **Integração Monolito**: Adaptador para buscar empreendimentos da API do monolito.

### Fora do Escopo
- Autenticação de usuários finais (delegada ao monolito).
- Notificações (fora deste serviço).
- Frontend/UI.

## Metas de Qualidade

- Testes unitários cobrindo casos de uso e domínio (Surefire + JaCoCo).
- Sem acoplamento do domínio com infraestrutura (Clean Architecture).
- Contratos de API documentados via SpringDoc/OpenAPI.
- Código livre de vulnerabilidades OWASP Top 10.

## Bounded Contexts

| Contexto      | Responsabilidade                                        |
|---------------|---------------------------------------------------------|
| `eventtype`   | Tipos de agendamento, vinculados a empreendimentos      |
| `appointment` | Agendamentos concretos com status e ciclo de vida       |

## Repositório

Localizado em: `d:\sptech\projeto-pi\backend\cal-service`  
Parte do projeto integrador (PI) — SPTech.
