# Skill: Spec Format

## Propósito
Definir o formato padrão para **arquivos `.md` de documentação** — especificação
de funcionalidades, regras de validação (RV), user stories e critérios de aceite.

> **Esta skill trata exclusivamente de documentação.** Nenhuma referência a RVs ou
> numeração spec-format deve aparecer no código-fonte ou em `@DisplayName` dos testes.

---

## Formato de Especificação de Funcionalidade

```markdown
# FEAT-<NNN>: <Título curto>

## Descrição
<Parágrafo descrevendo o contexto e objetivo da funcionalidade>

## Ator(es)
- <Ator principal> (ex: Corretor, Administrador, Sistema)

## Pré-condições
- <Lista de condições que devem ser verdadeiras antes da execução>

## Fluxo Principal
1. <Passo 1>
2. <Passo 2>
3. ...

## Fluxos Alternativos
### FA-01: <Título>
1. <Passo 1>
2. ...

## Fluxos de Exceção
### FE-01: <Título>
1. <Passo 1>
2. ...

## Regras de Validação
- RV-01: <Regra>
- RV-02: <Regra>
- ...

## Critérios de Aceite
- [ ] CA-01: <Critério>
- [ ] CA-02: <Critério>
- ...
```

---

## Regras de Validação (RV)

### Formato
```
RV-<NN>: <Campo/Conceito> — <Regra descritiva em português>
```

### Categorias

| Prefixo | Categoria | Exemplo |
|---------|-----------|---------|
| RV-01..09 | Campos obrigatórios | RV-01: Título — obrigatório, não pode ser vazio |
| RV-10..19 | Formato/tipo | RV-10: Slug — apenas letras minúsculas, números e hifens |
| RV-20..29 | Limites de tamanho | RV-20: Título — máximo 100 caracteres |
| RV-30..39 | Regras de negócio | RV-30: EventType — não pode ter ID externo reatribuído |
| RV-40..49 | Unicidade | RV-40: Slug — deve ser único por conta Cal.com |
| RV-50..59 | Dependência externa | RV-50: Cal.com — deve retornar status 2xx para criação |

### Exemplo Completo — EventType

```markdown
## Regras de Validação — EventType

### Campos Obrigatórios
- RV-01: Título — obrigatório, não pode ser nulo ou vazio
- RV-02: EstateId — obrigatório, não pode ser nulo

### Formato
- RV-10: Slug — gerado automaticamente a partir do título
- RV-11: Slug — apenas letras minúsculas (a-z), números (0-9) e hifens (-)
- RV-12: Slug — sem hifens no início ou final

### Regras de Negócio
- RV-30: ID Externo — só pode ser atribuído uma vez (imutável após atribuição)
- RV-31: ID Externo — deve ser positivo (> 0)
- RV-32: EventType — criado sem ID (id = null até persistência externa)

### Dependência Externa
- RV-50: Cal.com — criação retorna o EventType com ID atribuído
- RV-51: Cal.com — atualização mantém o ID original
- RV-52: Cal.com — exclusão por ID; falhas resultam em EventTypeDeletionException
```

---

## Formato de User Story (quando aplicável)

```markdown
### US-<NNN>: <Título>

**Como** <ator>,
**Quero** <ação>,
**Para** <benefício>.

#### Critérios de Aceite
- [ ] CA-01: <critério verificável>
- [ ] CA-02: <critério verificável>

#### Regras de Validação Relacionadas
- RV-01, RV-02, RV-10
```

### Exemplo

```markdown
### US-001: Criar Tipo de Evento

**Como** administrador do sistema,
**Quero** criar um tipo de evento vinculado a um imóvel,
**Para** disponibilizar agendamento de visitas no Cal.com.

#### Critérios de Aceite
- [ ] CA-01: EventType criado com sucesso no Cal.com
- [ ] CA-02: EventType persistido no banco local com ID externo
- [ ] CA-03: Slug gerado automaticamente a partir do título
- [ ] CA-04: Resposta contém id, title, slug e estateId

#### Regras de Validação Relacionadas
- RV-01, RV-02, RV-10, RV-11, RV-12, RV-30, RV-31, RV-32, RV-50
```

---

## Formato de Especificação de API

```markdown
### API-<NNN>: <Verbo HTTP> <Path>

**Descrição:** <O que o endpoint faz>
**Tag OpenAPI:** <Tag>
**Autenticação:** Bearer JWT

#### Request
| Campo | Tipo | Obrigatório | Regra |
|-------|------|-------------|-------|
| title | String | Sim | RV-01 |
| description | String | Não | — |
| estateId | Long | Sim | RV-02 |

#### Response (201 Created)
| Campo | Tipo |
|-------|------|
| id | Long |
| title | String |
| slug | String |
| estateId | Long |

#### Erros
| Status | Cenário | Regra |
|--------|---------|-------|
| 400 | Título vazio | RV-01 |
| 400 | EstateId nulo | RV-02 |
| 401 | Token JWT inválido/ausente | — |
| 500 | Falha na comunicação com Cal.com | RV-50 |
```

---

## Checklist — Nova Especificação

- [ ] Numeração sequencial (FEAT-NNN, US-NNN, API-NNN, RV-NN)
- [ ] Todas as RVs em português
- [ ] Critérios de aceite são verificáveis (sim/não)
- [ ] Fluxos de exceção documentados
- [ ] Documento salvo em `.md` no diretório de documentação do projeto
