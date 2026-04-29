# Memory Governance — Referência para Checkpoints

Regras extraídas de `governance.instructions.md` e `memory-bank.instructions.md`.  
Use este documento como referência rápida ao registrar checkpoints no Memory Bank do `cal-service`.

---

## 1. Leitura Obrigatória no Início de Cada Tarefa

Antes de qualquer checkpoint, confirme que os seguintes arquivos foram lidos:

| Arquivo | Conteúdo |
|---------|----------|
| `memory-bank/projectbrief.md` | Escopo e objetivos do projeto |
| `memory-bank/productContext.md` | Por que existe, problemas que resolve |
| `memory-bank/systemPatterns.md` | Arquitetura, padrões técnicos, convenções |
| `memory-bank/techContext.md` | Stack, dependências, variáveis de ambiente |
| `memory-bank/activeContext.md` | Foco atual, decisões ativas, próximos passos |
| `memory-bank/progress.md` | O que funciona, o que falta, status geral |
| `memory-bank/tasks/_index.md` | Índice de todas as tarefas com status |

Se qualquer arquivo core estiver ausente, **crie-o antes de prosseguir**.

---

## 2. Rastreabilidade Obrigatória

Toda implementação significativa deve produzir os seguintes artefatos no Memory Bank:

1. **Arquivo de tarefa**: `memory-bank/tasks/TASKID-nome.md` — criado ou atualizado.
2. **Índice de tarefas**: `memory-bank/tasks/_index.md` — status sincronizado.
3. **Contexto ativo**: `memory-bank/activeContext.md` — decisões ativas e próximos passos.
4. **Progresso**: `memory-bank/progress.md` — atualizado ao final de ciclos significativos.

---

## 3. Formato de IDs de Tarefa

- Formato: `TASK` + 3 dígitos: `TASK001`, `TASK042`.
- Nome de arquivo: `TASKID-nome-em-kebab-case.md`.
- IDs são **únicos e nunca reutilizados**, mesmo para tarefas abandonadas.
- Próximo ID disponível: consultar sempre `memory-bank/tasks/_index.md`.

---

## 4. Checkpoints do Ciclo de Vida de uma Tarefa

Cada tarefa percorre quatro checkpoints obrigatórios:

| Checkpoint | Momento | O que registrar |
|------------|---------|-----------------|
| 0 | Início — contexto inicial lido | Estado atual do projeto; objetivo da tarefa |
| 1 | Plano aprovado | Plano de implementação finalizado; subtarefas definidas |
| 2 | Implementação concluída | O que foi feito; testes passando; build limpo |
| 3 | Retro e lições | Lições aprendidas; decisões que devem virar rules formais |

---

## 5. Regras de Atualização do Memory Bank

### activeContext.md

Atualizar sempre que:
- O foco da sessão mudar.
- Uma decisão arquitetural for tomada.
- O próximo passo previsto mudar.

Campos obrigatórios:
- **Foco Atual**: o que está sendo trabalhado agora.
- **O Que Foi Feito Recentemente**: resumo do ciclo anterior.
- **Próximos Passos**: lista priorizada de ações.
- **Decisões Ativas**: decisões em vigor que impactam o desenvolvimento.

### progress.md

Atualizar ao final de cada fase ou ciclo significativo:
- Marcar o que funciona como entregue.
- Atualizar o que falta.
- Registrar status geral (percentual de conclusão do projeto).

### tasks/_index.md

Atualizar **imediatamente** ao:
- Criar nova tarefa → adicionar em **Pending** ou **In Progress**.
- Iniciar tarefa → mover de **Pending** para **In Progress**.
- Concluir tarefa → mover para **Completed** com data `YYYY-MM-DD`.
- Abandonar tarefa → mover para **Abandoned** com motivo.

### tasks/TASKID-nome.md

Atualizar a cada checkpoint:
- Marcar subtarefas concluídas com `[x]`.
- Adicionar entrada no **Progress Log** com data e descrição.
- Atualizar **Overall Status** e percentual.

---

## 6. Convergência de Lições em Regras Formais

Critérios para uma lição tornar-se instruction formal:

| Critério | Ação |
|----------|------|
| Problema ocorreu mais de uma vez | Candidato a instruction no arquivo relevante |
| Decisão impacta todos os bounded contexts | Candidato a `copilot-instructions.md` |
| Específica de tecnologia (Spring, MapStruct, JUnit) | `java-spring.instructions.md` ou `testing.instructions.md` |
| Específica de segurança | `security.instructions.md` |
| Específica de contrato de API | `api-contract.instructions.md` |

Fluxo de convergência:
```
Lição identificada (Checkpoint 3)
  → checkpoint-template.md preenchido
  → revisão quinzenal
  → instruction relevante atualizada
  → Memory Bank reflete a mudança
```

---

## 7. Ações que Exigem Confirmação do Usuário

Nunca execute sem aprovação explícita:

- Deletar arquivos ou pastas.
- `git push`, `git push --force`, `git reset --hard`.
- Commit ou amend em commits já publicados.
- Apagar ou redefinir schemas de banco de dados.
- Modificar variáveis de ambiente em arquivos de produção (`.env`, `application-prod.yml`).
- Executar scripts que afetam sistemas externos (Cal.com API, Monolito, banco de produção).

---

## 8. Definição de Pronto (DoD)

Uma tarefa é considerada pronta quando **todos** os itens abaixo estão satisfeitos:

- [ ] Código implementado seguindo Clean Architecture e convenções do projeto.
- [ ] Testes unitários criados e passando (`./mvnw test`).
- [ ] Cobertura mínima de 80% nos use cases.
- [ ] Sem erros de compilação (`./mvnw compile`).
- [ ] Sem segredos ou credenciais no código.
- [ ] Endpoints documentados com SpringDoc/OpenAPI (se houver controller novo).
- [ ] Memory Bank atualizado: task file, `_index.md`, `activeContext.md`, `progress.md`.

---

## Fontes

- `.github/instructions/governance.instructions.md`
- `.github/instructions/memory-bank.instructions.md`
