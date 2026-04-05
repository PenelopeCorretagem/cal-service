---
applyTo: '**'
---

# Governance Instructions — cal-service

Políticas de execução, rastreabilidade, limites de escopo e integração com o Memory Bank. Aplicam-se a toda sessão de trabalho com agentes de IA neste repositório.

---

## 1. Memory Bank — Obrigatoriedade

Antes de qualquer tarefa, o agente **deve** ler os arquivos core do Memory Bank:

1. `memory-bank/projectbrief.md`
2. `memory-bank/productContext.md`
3. `memory-bank/systemPatterns.md`
4. `memory-bank/techContext.md`
5. `memory-bank/activeContext.md`
6. `memory-bank/progress.md`
7. `memory-bank/tasks/_index.md`

Se qualquer arquivo core estiver ausente, crie-o antes de prosseguir.

---

## 2. Ações Locais vs. Ações com Risco

### Ações Livres (sem confirmação)

O agente pode executar livremente:

- Leitura de qualquer arquivo do repositório.
- Criação e edição de arquivos de código, testes e documentação.
- Execução de comandos de build, teste e análise: `./mvnw compile`, `./mvnw test`, `./mvnw verify`.
- Criação e atualização de arquivos do Memory Bank.

### Ações que Exigem Confirmação Explícita do Usuário

O agente **deve pausar e obter aprovação** antes de:

- Deletar arquivos ou pastas.
- Executar `git push`, `git push --force`, `git reset --hard`.
- Fazer commit ou amend em commits já publicados.
- Apagar ou redefinir schemas de banco de dados.
- Modificar variáveis de ambiente em arquivos de produção (`.env`, `application-prod.yml`).
- Executar scripts que afetam sistemas externos (Cal.com API, Monolito, banco de dados de produção).

---

## 3. Rastreabilidade

Toda implementação significativa deve ser rastreada no Memory Bank:

1. **Criar** ou **atualizar** o arquivo de tarefa correspondente em `memory-bank/tasks/TASKID-nome.md`.
2. **Atualizar** `memory-bank/tasks/_index.md` com o status atual da tarefa.
3. **Atualizar** `memory-bank/activeContext.md` com decisões ativas relevantes tomadas durante a execução.
4. Ao final de cada ciclo significativo, atualizar `memory-bank/progress.md`.

---

## 4. Convenções de IDs de Tarefa

- Formato: `TASK` + 3 dígitos (ex.: `TASK001`, `TASK042`).
- Nome do arquivo: `TASKID-nome-em-kebab-case.md` (ex.: `TASK001-governanca-base-copilot.md`).
- IDs são únicos e nunca reutilizados, mesmo para tarefas abandonadas.

---

## 5. Limites de Escopo

- Implemente apenas o que foi solicitado. Não adicione features, refatorações ou melhorias não pedidas.
- Não adicione comentários, docstrings ou type annotations em código que não foi modificado.
- Não crie helpers ou abstrações para uso único.
- Para mudanças de escopo amplo (vários arquivos em cadeia), exiba um mapa de contexto antes de agir:

```
## Mapa de Contexto para: [descrição]

### Arquivos Primários (modificados diretamente)
### Arquivos Secundários (podem precisar de atualização)
### Cobertura de Testes
### Padrão a Seguir
### Sequência Sugerida
```

---

## 6. Qualidade Mínima para Código Novo

- Nenhum build deve quebrar: execute `./mvnw compile` antes de considerar a tarefa pronta.
- Testes unitários devem passar: execute `./mvnw test`.
- Nenhum segredo ou credencial deve aparecer no código.
- Novos controllers ou endpoints devem ter documentação SpringDoc/OpenAPI.

---

## 7. Segurança

- Seguir OWASP Top 10 em todo código produzido.
- Nunca gerar ou incluir tokens JWT, API keys ou senhas reais em nenhum arquivo.
- Validar todas as entradas de API com `@Valid` e Bean Validation.
- Não duplicar lógica de segurança já existente em `SecurityFilter` ou `SecurityConfig`.

---

## 8. Comunicação com o Usuário

- Respostas curtas e diretas. Expandir apenas quando necessário.
- Para operações não triviais, explicar o propósito e impacto antes de executar.
- Não usar emojis.
- Após completar operações de arquivo, confirmar brevemente sem explicar o que foi feito em detalhe.
