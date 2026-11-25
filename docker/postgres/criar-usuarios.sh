#!/bin/bash
# Cria os usuarios do banco do Compose com privilegios minimos (issues #71 e #172).
# Idempotente: roda na primeira inicializacao do volume e pode ser executado
# manualmente em um volume ja existente:
#   docker compose --env-file .env.compose -f compose.yaml exec postgres bash /docker-entrypoint-initdb.d/criar-usuarios.sh
set -e

: "${POSTGRES_USER:?POSTGRES_USER nao definida}"
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


psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname "${BTHS_DB_NAME}" <<SQL
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '${BTHS_APP_USER}') THEN
    CREATE ROLE ${BTHS_APP_USER} LOGIN;
  END IF;
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '${BTHS_MIGRACAO_USER}') THEN
    CREATE ROLE ${BTHS_MIGRACAO_USER} LOGIN;
  END IF;
END
\$\$;

ALTER ROLE ${BTHS_APP_USER} WITH LOGIN PASSWORD '${BTHS_APP_PASSWORD}';
ALTER ROLE ${BTHS_MIGRACAO_USER} WITH LOGIN PASSWORD '${BTHS_MIGRACAO_PASSWORD}';
REVOKE ALL ON DATABASE ${BTHS_DB_NAME} FROM PUBLIC;
GRANT CONNECT ON DATABASE ${BTHS_DB_NAME} TO ${BTHS_APP_USER}, ${BTHS_MIGRACAO_USER};

REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO ${BTHS_APP_USER};
GRANT USAGE, CREATE ON SCHEMA public TO ${BTHS_MIGRACAO_USER};
ALTER DEFAULT PRIVILEGES FOR ROLE ${BTHS_MIGRACAO_USER} IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ${BTHS_APP_USER};
ALTER DEFAULT PRIVILEGES FOR ROLE ${BTHS_MIGRACAO_USER} IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO ${BTHS_APP_USER};

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO ${BTHS_APP_USER};
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO ${BTHS_APP_USER};
SQL

echo "Usuarios do banco ${BTHS_DB_NAME} configurados com privilegios minimos."