#!/bin/bash
# Cria os usuarios do banco do Compose com privilegios minimos (issue #71).
# Idempotente: roda na primeira inicializacao do volume e pode ser executado
# manualmente em um volume ja existente:
#   docker compose --env-file .env.compose -f compose.yaml exec mysql bash /docker-entrypoint-initdb.d/criar-usuarios.sh
set -e

: "${MYSQL_ROOT_PASSWORD:?MYSQL_ROOT_PASSWORD nao definida}"
: "${BTHS_DB_NAME:?BTHS_DB_NAME nao definida}"
: "${BTHS_APP_USER:?BTHS_APP_USER nao definida}"
: "${BTHS_APP_PASSWORD:?BTHS_APP_PASSWORD nao definida}"
: "${BTHS_MIGRACAO_USER:?BTHS_MIGRACAO_USER nao definida}"
: "${BTHS_MIGRACAO_PASSWORD:?BTHS_MIGRACAO_PASSWORD nao definida}"

for variavel in BTHS_APP_PASSWORD BTHS_MIGRACAO_PASSWORD; do
  case "${!variavel}" in
    *\'* | *\\*)
      echo "${variavel} nao pode conter aspas simples nem barra invertida." >&2
      exit 1
      ;;
  esac
done

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<SQL
CREATE DATABASE IF NOT EXISTS \`${BTHS_DB_NAME}\`;

CREATE USER IF NOT EXISTS '${BTHS_APP_USER}'@'%' IDENTIFIED BY '${BTHS_APP_PASSWORD}';
ALTER USER '${BTHS_APP_USER}'@'%' IDENTIFIED BY '${BTHS_APP_PASSWORD}';

CREATE USER IF NOT EXISTS '${BTHS_MIGRACAO_USER}'@'%' IDENTIFIED BY '${BTHS_MIGRACAO_PASSWORD}';
ALTER USER '${BTHS_MIGRACAO_USER}'@'%' IDENTIFIED BY '${BTHS_MIGRACAO_PASSWORD}';

REVOKE ALL PRIVILEGES, GRANT OPTION FROM '${BTHS_APP_USER}'@'%';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM '${BTHS_MIGRACAO_USER}'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE
  ON \`${BTHS_DB_NAME}\`.* TO '${BTHS_APP_USER}'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE,
      CREATE, ALTER, DROP, INDEX, REFERENCES
  ON \`${BTHS_DB_NAME}\`.* TO '${BTHS_MIGRACAO_USER}'@'%';
SQL

echo "Usuarios do banco ${BTHS_DB_NAME} configurados com privilegios minimos."