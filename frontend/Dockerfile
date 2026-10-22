# Etapa 1: Instalação das dependências
FROM node:22-bookworm-slim AS deps

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

# Etapa 2: Build do Next.js
FROM node:22-bookworm-slim AS builder

WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY . .

# URL pública definida durante o build. Vazia significa mesma origem (recomendado).
ARG NEXT_PUBLIC_API_URL=""
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}

# Destino interno da API utilizado nos rewrites. Sem valor padrão: precisa ser informado.
ARG BTHS_INTERNAL_API_URL
ENV BTHS_INTERNAL_API_URL=${BTHS_INTERNAL_API_URL}

# Impede uma imagem de produção que dependa de localhost
RUN [ -n "${BTHS_INTERNAL_API_URL}" ] \
    || { echo "ERRO: informe --build-arg BTHS_INTERNAL_API_URL (endereco interno do backend)." >&2; exit 1; }; \
    for valor in "${NEXT_PUBLIC_API_URL}" "${BTHS_INTERNAL_API_URL}"; do \
      case "$valor" in \
        *localhost*|*127.0.0.1*|*0.0.0.0*|*"[::1]"*) \
          echo "ERRO: o build do frontend nao pode apontar para localhost ($valor)." >&2; exit 1;; \
      esac; \
    done

RUN npm run build

# Etapa 3: Imagem de execução
FROM node:22-bookworm-slim AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

# Usuário sem privilégios de root
RUN groupadd --gid 1001 nodejs \
    && useradd --uid 1001 --gid nodejs \
       --no-create-home --shell /usr/sbin/nologin nextjs \
    && mkdir -p .next \
    && chown nextjs:nodejs .next

# Copiar somente os arquivos necessários
COPY --from=builder --chown=nextjs:nodejs /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

CMD ["node", "server.js"]