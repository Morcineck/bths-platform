# Matriz de QA no staging

Checklist de validação do BTHS Platform no staging (issue #77): perfis ADMIN, STAFF e HOSPEDE × dispositivos × cenários. Usa **apenas contas e dados fictícios** (ver `docs/staging.md`). O backup com dados reais não deve ser restaurado no staging.

## Contas e dados de teste

| Item | Valor |
|---|---|
| ADMIN | `Admin Staging` |
| STAFF | `Staff QA` (`staff.qa@bths.test`) |
| HOSPEDE | `Hospede QA` (`hospede.qa@bths.test`, CPF fictício, sem telefone) |
| Viagem | `Viagem QA`, Itu-SP, 10 a 14/12/2026 |
| Hospedagem | `Tomorrowland Brasil 2027`, endereço fictício |
| Quarto | `QA-101`, suíte, capacidade 2 |
| Transporte | `Motorista QA`, `Van QA` (4 lugares), Operação #1 GRU → hospedagem |
| Conteúdo | `Aviso QA`, evento de agenda `Festival QA - Dia 1` |

Senhas não são registradas no repositório.

## Como executar

- Usar perfis de navegador separados (ou janelas anônimas) para manter dois perfis logados ao mesmo tempo.
- Chamadas diretas à API pelo console: buscar `/api/auth/csrf`, ler o cookie `XSRF-TOKEN` e enviar no cabeçalho `X-XSRF-TOKEN`, com `credentials: 'include'` e caminho relativo (`/api/...`).
- O staging publica só por deploy manual no Render (branch `develop`). Conferir o commit publicado antes de testar uma correção.
- Legenda: ✅ passou · ⚠️ passou com ressalva · ❌ falhou · ⏳ pendente.

## Matriz

### HOSPEDE

| Cenário | Desktop | Android | iPhone |
|---|---|---|---|
| Login válido cai em `/app` com a viagem | ✅ | ⏳ | ⏳ |
| Telas do app (viagem, quarto, hospedagem, transporte, QR, avisos, perfil) com dados corretos | ✅ (também em 375x812 emulado) | ⏳ | ⏳ |
| Perfil errado: `/dashboard` redireciona para `/app` | ✅ | ⏳ | ⏳ |
| APIs de ADMIN/STAFF (`/api/viagens`, `/api/hospedes`, `/api/quartos`, `/api/motoristas`, `/api/usuarios/{id}`) | ✅ 403 | n/a | n/a |
| Dados de outro hóspede (`/api/hospedes/2`, `/api/traslados/1`, imagem do QR por código) | ✅ 403 | n/a | n/a |
| APIs do app só com dados do próprio hóspede e `Cache-Control: no-store` | ✅ | n/a | n/a |
| Refresh (F5) mantém a sessão | ✅ | ⏳ | ⏳ |
| Logout limpa a sessão (`/api/auth/me` 401, rotas voltam a `/login`) | ✅ | ⏳ | ⏳ |
| QR Code legível na tela e lido pela câmera do STAFF | ⚠️ legível; leitura por câmera ⏳ | ⏳ | ⏳ |

### STAFF

| Cenário | Desktop | Android | iPhone |
|---|---|---|---|
| Login inválido recusado | ✅ | ⏳ | ⏳ |
| Login válido cai em `/dashboard`, menu sem Motoristas e Veículos | ✅ | ⏳ | ⏳ |
| Leituras permitidas (viagens, hóspedes, quartos, hospedagens, operações, avisos, agenda) | ✅ 200 | n/a | n/a |
| Ações só de ADMIN pela API (usuários, motoristas, veículos, criar/editar hóspede, acesso BTHS, criar/excluir operação) | ✅ 403 | n/a | n/a |
| `/motoristas` mostra mensagem de permissão | ✅ | ⏳ | ⏳ |
| `/veiculos` e `/traslados` escondem ações de ADMIN | ⚠️ não escondem (achado 10) | ⏳ | ⏳ |
| Check-in por código: inválido, válido e duplicado (409) | ✅ | ⏳ | ⏳ |
| Check-in pela câmera ("Ler QR Code") | ⏳ | ⏳ | ⏳ |
| Mudar status da operação (Iniciar) com histórico | ✅ | ⏳ | ⏳ |
| Refresh e logout | ✅ | ⏳ | ⏳ |

### ADMIN

| Cenário | Desktop | Android | iPhone |
|---|---|---|---|
| Sem autenticação: APIs protegidas respondem 401 | ✅ | n/a | n/a |
| Escrita sem cabeçalho CSRF responde 403 | ✅ | n/a | n/a |
| Perfil errado: `/app` redireciona para `/dashboard`; `/api/app/**` 403 | ✅ | ⏳ | ⏳ |
| Criar quarto, hospedagem, vincular quarto, alocar hóspede, motorista, veículo, operação, vincular traslado, aviso, agenda | ✅ | ⏳ | ⏳ |
| Criar viagem pela interface | ❌ não existe tela (achado 2) | | |
| Criar usuário STAFF pela interface | ❌ não existe tela (achado 3) | | |
| Adicionar passageiro (traslado) a uma operação pela interface | ❌ não existe tela (achado 6) | | |
| Refresh e logout | ✅ | ⏳ | ⏳ |

### Transversais

| Cenário | Situação |
|---|---|
| Sessão expirada (JWT vencido) redireciona para `/login` | ⏳ exige esperar a expiração ou reduzir o tempo do JWT no staging |
| Bloqueio de login por excesso de tentativas | ⏳ testar com conta descartável |
| Horários gerados pelo servidor no fuso de Brasília | ❌ saem em UTC, 3 h adiantados (achado 11) |

## Achados

| # | Achado | Gravidade | Situação |
|---|---|---|---|
| 1 | Dashboard trava em "Carregando dashboard..." sem viagem cadastrada | UX | aberto |
| 2 | Não existe tela para criar viagem | bloqueador para produção | aberto |
| 3 | Não existe tela de usuários (STAFF só por API) | alto | aberto |
| 4 | Lista de hóspedes quebrava com telefone nulo | bloqueador | corrigido no PR #144 e confirmado no staging |
| 6 | Não existe tela para criar o traslado individual, que é a vaga do hóspede numa operação | bloqueador para produção | aberto |
| 7 | Usuário logado que abre `/login` vê o formulário | baixo | aberto |
| 8 | Detalhe do hóspede leva cerca de 8 s no staging | observar | aberto |
| 9 | `/app/viagem` mostra o endereço da viagem (vazio) em vez do da hospedagem | baixo | aberto |
| 10 | STAFF vê ações de ADMIN em `/traslados` e `/veiculos` (backend bloqueia) | médio | aberto |
| 11 | Horários gerados pelo servidor em UTC (check-in, histórico de status) | alto | aberto |

O achado 5 (perfil não checado no `proxy.ts`) foi verificado e não gerou falha de segurança; o efeito de interface está no achado 10.