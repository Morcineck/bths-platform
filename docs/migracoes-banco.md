# BTHS Platform - Migrações de banco de dados (Flyway)
**Issue:** [#82 - Versionar schema e migrações](https://github.com/Morcineck/bths-platform/issues/82)
**Stack:** Java 17 · Spring Boot 4.1.0 · MySQL 8.4 · Hibernate/JPA · Flyway
**Estado deste documento:** procedimentos homologados no MySQL Docker de testes em 13–14/10/2026; **não executados no banco local original**.

## 1. Objetivo e responsabilidades

- **Flyway** versiona e executa mudanças de estrutura via arquivos SQL imutáveis.
- **Hibernate** usa `spring.jpa.hibernate.ddl-auto=validate` e **não altera** tabelas.
- **MySQL** armazena dados e mantém o histórico do Flyway em `flyway_schema_history`.
- Alterações posteriores devem criar uma **nova migração** `V3__...sql`, `V4__...sql` etc. Não editar V1/V2 após merge/publicação: mudanças de checksum invalidam ambientes já migrados.

Migrações em `backend/src/main/resources/db/migration/`:

| Arquivo | Finalidade |
|---|---|
| `V1__estrutura_inicial_bths.sql` | Reproduz as **14 tabelas** do schema legado, com **20 chaves estrangeiras**. Preserva intencionalmente duas inconsistências históricas para permitir baseline em banco existente. |
| `V2__corrigir_schema_legado_bths.sql` | Remove a coluna legada `operacoes_traslado.orientecao_hospede` e retira `CONFIRMADO` do `ENUM` de `traslados.status`. |

**Atenção:** V2 pode causar perda de dados caso a coluna legada contenha valores ou haja traslados com status `CONFIRMADO`. As pré-condições são obrigatórias em **cada ambiente**, mesmo que tenham sido verificadas no banco local em outubro de 2026.

## 2. Perfis e configurações

| Ambiente | Comportamento previsto |
|---|---|
| Desenvolvimento padrão | `ddl-auto=validate`; Flyway temporariamente desligado por padrão (`BTHS_FLYWAY_ENABLED:false`) durante transição do banco local. |
| Testes H2 | `ddl-auto=create-drop`; `spring.flyway.enabled=false` no arquivo de configuração de testes. |
| Staging (`staging`) | URL externa `BTHS_DB_URL`, usuário/senha por variáveis, `ddl-auto=validate`, Flyway habilitado. |
| Produção (`prod`) | Igual ao staging; adicionalmente SQL de debug desligado. |

Credenciais **não** devem ser versionadas. Nunca usar um banco de staging e produção com a mesma URL, nem usar `baseline-on-migrate=true` como configuração permanente. Em especial, `BTHS_DB_URL` e `DB_USERNAME` devem ser validados explicitamente antes de cada operação.

## 3. Banco novo e vazio

**Pré-condições:** banco MySQL 8.4 separado, vazio, credenciais disponíveis e conexão de banco verificada.

1. Conferir URL, porta, nome do banco e usuário efetivos **sem exibir senhas**.
2. Habilitar Flyway nesse ambiente (`spring.flyway.enabled=true` ou `BTHS_FLYWAY_ENABLED=true` no perfil apropriado).
3. Iniciar o Spring Boot. O Flyway aplica V1 e V2 em ordem e cria `flyway_schema_history`.
4. Exigir inicialização bem-sucedida do Spring Boot com Hibernate em `validate`.
5. Validar, no **banco de destino**, os resultados:

```sql
SELECT installed_rank, version, type, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT COUNT(*) AS total_tabelas
FROM information_schema.tables
WHERE table_schema = DATABASE();

SELECT COUNT(*) AS total_fks
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE()
  AND REFERENCED_TABLE_NAME IS NOT NULL;
```

**Esperado para banco novo:** V1 e V2 como `SQL`, ambas `success=1`; 15 tabelas (14 da aplicação + histórico); 20 chaves estrangeiras. Reiniciar a aplicação: nenhuma migração adicional deve ser aplicada.

## 4. Banco existente - baseline controlado

**NUNCA aplicar V1 diretamente em um banco que já possui tabelas.** O registro de `BASELINE` indica que a estrutura V1 já existe; ele **não valida automaticamente a equivalência das tabelas**.

### 4.1. Pré-condições obrigatórias

- Janela de manutenção/backup e plano de restauração aprovados; parar escritas na aplicação durante a intervenção.
- Confirmar ambiente e endpoint real (hostname, porta, schema e conta). Não confiar apenas no nome da variável.
- Exportar **backup integral de estrutura e dados**, guardar fora do repositório e testar a restauração em ambiente separado.
- Comparar a estrutura preexistente com a V1 (tabelas, tipos, índices, constraints e FKs). Se houver divergências inesperadas, **parar** e elaborar migração específica.
- Confirmar as pré-condições da V2 **no banco que será migrado**:

```sql
SELECT COUNT(*) AS valores_coluna_legada
FROM operacoes_traslado
WHERE orientecao_hospede IS NOT NULL;

SELECT COUNT(*) AS traslados_confirmados
FROM traslados
WHERE status = 'CONFIRMADO';
```

Ambos os resultados precisam ser `0` antes de executar V2; do contrário, tratar os dados e revisar o SQL da V2.

- Registrar contagens de tabelas e registros críticos, incluindo quantidade de ADMIN ativo (não copiar senhas/hashes para relatórios):

```sql
SELECT perfil, COUNT(*) AS total FROM usuarios GROUP BY perfil;
SELECT COUNT(*) AS admins_ativos FROM usuarios
WHERE perfil = 'ADMIN' AND ativo = b'1';
```

### 4.2. Aplicar baseline e V2

**Somente após todos os controles acima:** no ambiente exato, configurar uma execução excepcional com Flyway habilitado, `spring.flyway.baseline-on-migrate=true` e `spring.flyway.baseline-version=1`. A V1 é registrada como `BASELINE`, sem executar SQL da V1; a V2 é executada.

Não ativar `baseline-on-migrate` globalmente em `application.properties`, staging ou produção. **A execução de baseline precisa ser manual, pontual e revisada**, com comprovação de destino. Após o sucesso, remover essa opção excepcional.

Em um banco pré-existente já versionado, basta iniciar normalmente sem baseline; o Flyway não deverá executar migrações concluídas.

### 4.3. Verificação pós-migração

```sql
SELECT installed_rank, version, type, success
FROM flyway_schema_history ORDER BY installed_rank;

SELECT COLUMN_NAME
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'operacoes_traslado'
  AND COLUMN_NAME IN ('orientecao_hospede', 'orientacao_hospede');

SELECT COLUMN_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'traslados' AND COLUMN_NAME = 'status';
```

**Esperado para banco legado:** versão `1` como `BASELINE` e versão `2` como `SQL`, ambas `success=1`; apenas coluna `orientacao_hospede`; enum sem `CONFIRMADO`; contagens de usuários e ADMIN inalteradas; inicialização do Spring Boot sem `baseline-on-migrate` bem-sucedida.

## 5. Recuperação e forward fix

- **Não contar com rollback automático de DDL no MySQL**. `ALTER TABLE` e outros DDL podem produzir commits implícitos e persistir parcialmente após falha.
- Em falha **antes** de alteração do schema: interromper o rollout, diagnosticar e corrigir a causa.
- Em falha **durante** a migração: parar aplicação e escritas; inspecionar `flyway_schema_history`, estrutura efetiva e logs; **não executar `repair` nem editar V1/V2 por tentativa**.
- Para falhas em migrações futuras, preferir **forward fix**: um novo script versionado que leve o schema ao estado correto, revisado e ensaiado.
- Se houver perda ou corrupção de dados, restaurar backup em ambiente isolado, conferir integridade e só então definir o procedimento de recuperação do ambiente afetado. Restaurar um backup anterior também exige considerar **dados criados após o backup** e um possível período de indisponibilidade.
- Manter cópia segura do backup, fora do GitHub e das imagens Docker; não registrar senhas, hashes, dados pessoais ou tokens na documentação.

## 6. Evidências obtidas no Docker - 13-14/10/2026

| Cenário                                             | Resultado observado |
|-----------------------------------------------------|---|
| Testes Maven                                        | 546 executados; 0 falhas; 0 erros; `BUILD SUCCESS` (com Flyway desabilitado no H2). |
| MySQL 8.4.11 vazio (`bths_flyway_test`)             | V1 + V2 aplicadas e registradas; aplicação iniciou com Hibernate `validate`. |
| Banco vazio - contagem                              | 15 tabelas e 20 FKs após V1/V2; V1 anterior foi refeito e revalidado junto da V2. |
| Banco legado restaurado (`bths_flyway_legacy_test`) | Backup de 11/10/2026 restaurado; 14 tabelas, 3 HÓSPEDES, 1 STAFF e 1 ADMIN ativo antes da migração. |
| Legado - baseline + V2                              | Flyway registrou `V1 BASELINE` e `V2 SQL`, ambas sucesso. Coluna antiga removida, enum corrigido; 5 usuários preservados. |
| Segunda inicialização sem baseline                  | Spring Boot iniciou normalmente, com apenas duas entradas no histórico (`V1 BASELINE`, `V2 SQL`). |

**Limites das evidências:** os testes não constituem deploy de produção; o banco local original `bths_platform` **não foi migrado**. O backup usado representa o estado de 11/10/2026, não alterações posteriores. Uma implantação real precisa repetir verificações, backup, simulação e aprovação no destino efetivo.

## 7. Checklist de PR e release

- [ ] `pom.xml`, perfis e migrações V1/V2 revisados no diff completo.
- [ ] `git diff --check` sem erros.
- [ ] `./mvnw clean test` verde (546 testes na execução registrada).
- [ ] Staging testado com o mesmo processo planejado para produção; **banco próprio**.
- [ ] Evidências MySQL de schema vazio e de baseline legado anexadas à issue/PR, sem dados sensíveis.
- [ ] Processo de backup, recuperação e correção progressiva revisado.
- [ ] Nenhuma credencial ou backup de dados incorporado ao Git.
- [ ] Revisão de branch, PR para `develop`, merge após CI; preparar integração futura com `main` separadamente.

**Regra central:** qualquer migração deve ser reproduzível, revisada e rastreável; operações em bases com dados exigem conferência explícita do destino e backup comprovadamente restaurável.
