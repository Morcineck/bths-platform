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

# URL pública definida durante o build
ARG NEXT_PUBLIC_API_URL=""
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}

# Destino interno da API utilizado nos rewrites
ARG BTHS_INTERNAL_API_URL="http://localhost:8080"
ENV BTHS_INTERNAL_API_URL=${BTHS_INTERNAL_API_URL}

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
