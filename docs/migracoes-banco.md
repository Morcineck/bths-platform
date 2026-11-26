# BTHS Platform - Migrações de banco de dados (Flyway)
**Issues:** [#82 - Versionar schema e migrações](https://github.com/Morcineck/bths-platform/issues/82) · [#172 - Migração para PostgreSQL](https://github.com/Morcineck/bths-platform/issues/172)
**Stack:** Java 17 · Spring Boot 4.1.0 · PostgreSQL 17 · Hibernate/JPA · Flyway
**Estado deste documento:** V1 em PostgreSQL validada localmente (Docker e Docker Compose) em 25/11/2026. O histórico em MySQL está na seção 6.

## 1. Objetivo e responsabilidades

- **Flyway** versiona e executa mudanças de estrutura via arquivos SQL imutáveis.
- **Hibernate** usa `spring.jpa.hibernate.ddl-auto=validate` e **não altera** tabelas.
- **PostgreSQL** armazena dados e mantém o histórico do Flyway em `flyway_schema_history`.
- Alterações posteriores devem criar uma **nova migração** `V2__...sql`, `V3__...sql` etc. Não editar a V1 após merge/publicação: mudanças de checksum invalidam ambientes já migrados.

Migrações em `backend/src/main/resources/db/migration/`:

| Arquivo | Finalidade |
|---|---|
| `V1__estrutura_inicial_bths.sql` | Cria as **14 tabelas** da aplicação em PostgreSQL, com chaves estrangeiras, `CHECK` dos valores de enum e índices nas colunas de chave estrangeira. Consolida o schema anterior em MySQL (antigas V1 + V2). |

## 2. Perfis e configurações

| Ambiente | Comportamento previsto |
|---|---|
| Desenvolvimento padrão | PostgreSQL local em `localhost:5433/bths_platform`; `ddl-auto=validate`; Flyway desligado por padrão (`BTHS_FLYWAY_ENABLED:false`), ligado no ambiente local com `BTHS_FLYWAY_ENABLED=true`. |
| Testes H2 | `ddl-auto=create-drop`; `spring.flyway.enabled=false` no arquivo de configuração de testes. |
| Staging (`staging`) | URL externa `BTHS_DB_URL` (`jdbc:postgresql://...`), usuário/senha por variáveis, `ddl-auto=validate`, Flyway habilitado. |
| Produção (`prod`) | Igual ao staging; adicionalmente SQL de debug desligado. |

Credenciais **não** devem ser versionadas. Nunca usar um banco de staging e produção com a mesma URL, nem usar `baseline-on-migrate=true` como configuração permanente. Em especial, `BTHS_DB_URL` e `DB_USERNAME` devem ser validados explicitamente antes de cada operação.

## 3. Banco novo e vazio

**Pré-condições:** banco PostgreSQL 17 separado, vazio, credenciais disponíveis e conexão de banco verificada.

1. Conferir URL, porta, nome do banco e usuário efetivos **sem exibir senhas**.
2. Habilitar Flyway nesse ambiente (`spring.flyway.enabled=true` ou `BTHS_FLYWAY_ENABLED=true` no perfil apropriado).
3. Iniciar o Spring Boot. O Flyway aplica a V1 e cria `flyway_schema_history`.
4. Exigir inicialização bem-sucedida do Spring Boot com Hibernate em `validate`.
5. Validar, no **banco de destino**, os resultados:

```sql
SELECT installed_rank, version, type, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT COUNT(*) AS total_tabelas
FROM information_schema.tables
WHERE table_schema = 'public' AND table_type = 'BASE TABLE';

SELECT COUNT(*) AS total_fks
FROM information_schema.table_constraints
WHERE table_schema = 'public' AND constraint_type = 'FOREIGN KEY';
```

**Esperado para banco novo:** V1 como `SQL` com `success=true`; 15 tabelas (14 da aplicação + histórico do Flyway). Reiniciar a aplicação: nenhuma migração adicional deve ser aplicada.

## 4. Bancos existentes em MySQL

A migração para PostgreSQL (#172) não converte bancos MySQL existentes. Na data da migração, o BTHS não tinha produção, e os bancos MySQL eram de desenvolvimento e staging. Todo ambiente PostgreSQL começa vazio e recebe a V1 do zero, como na seção 3.

Se algum dia for preciso levar dados de um banco MySQL para o PostgreSQL, isso exige um procedimento próprio, com backup, conversão de tipos (`binary(16)` para `UUID`, `bit(1)` para `BOOLEAN`), conferência de contagens e teste em ambiente separado antes do destino real.

O baseline controlado (`baseline-on-migrate`) continua proibido como configuração permanente. Só pode ser usado em uma execução manual, pontual e revisada, com comprovação do destino.

## 5. Recuperação e forward fix

- No PostgreSQL, o DDL é transacional: se uma migração falhar no meio, o Flyway desfaz as mudanças daquela migração (`Changes successfully rolled back`). Mesmo assim, conferir o estado do banco antes de tentar de novo.
- Em falha **antes** de alteração do schema: interromper o rollout, diagnosticar e corrigir a causa.
- Em falha **durante** a migração: parar aplicação e escritas; inspecionar `flyway_schema_history`, estrutura efetiva e logs; **não executar `repair` nem editar migrações já aplicadas por tentativa**.
- Para falhas em migrações futuras, preferir **forward fix**: um novo script versionado que leve o schema ao estado correto, revisado e ensaiado.
- Se houver perda ou corrupção de dados, restaurar backup em ambiente isolado, conferir integridade e só então definir o procedimento de recuperação do ambiente afetado. Restaurar um backup anterior também exige considerar **dados criados após o backup** e um possível período de indisponibilidade.
- Manter cópia segura do backup, fora do GitHub e das imagens Docker; não registrar senhas, hashes, dados pessoais ou tokens na documentação.

## 6. Evidências

### 6.1. PostgreSQL local (#172) - 25/11/2026

| Cenário | Resultado observado |
|---|---|
| Testes Maven | 669 executados; 0 falhas; 0 erros; `BUILD SUCCESS` (com Flyway desabilitado no H2). |
| PostgreSQL 17.12 vazio em Docker (`bths_platform`) | V1 aplicada ("now at version v1"); aplicação iniciou com Hibernate `validate`. |
| Docker Compose com PostgreSQL 17 | Usuários de aplicação e de migração criados por `docker/postgres/criar-usuarios.sh`; V1 aplicada pelo usuário de migração; `postgres`, `backend` e `frontend` em `healthy`. |
| Falha de migração | Uma V2 antiga com sintaxe MySQL falhou e o Flyway desfez as mudanças dela, confirmando o DDL transacional. |

**Limites:** validação apenas local. O staging em PostgreSQL ainda precisa ser homologado.

### 6.2. Histórico em MySQL

Evidências do schema anterior, em MySQL 8.4, antes da issue #172:

- **13-14/10/2026, Docker:** V1 + V2 aplicadas em banco vazio (15 tabelas e 20 FKs); baseline controlado em banco legado restaurado, com V1 registrada como `BASELINE` e V2 como `SQL`, preservando os 5 usuários; 546 testes Maven verdes.
- **24/10/2026, staging no Aiven:** V1 e V2 aplicadas do zero pelo usuário de migração no deploy do Render; aplicação subiu com o usuário de runtime (só DML), readiness `UP` e login funcionando.

## 7. Checklist de PR e release

- [ ] `pom.xml`, perfis e migrações revisados no diff completo.
- [ ] `git diff --check` sem erros.
- [ ] `./mvnw clean test` verde (669 testes na execução registrada).
- [ ] Staging testado com o mesmo processo planejado para produção; **banco próprio**.
- [ ] Processo de backup, recuperação e correção progressiva revisado.
- [ ] Nenhuma credencial ou backup de dados incorporado ao Git.
- [ ] Revisão de branch, PR para `develop`, merge após CI; preparar integração futura com `main` separadamente.

**Regra central:** qualquer migração deve ser reproduzível, revisada e rastreável; operações em bases com dados exigem conferência explícita do destino e backup comprovadamente restaurável.
