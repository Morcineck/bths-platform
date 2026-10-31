# Matriz de QA no staging

Checklist de validação do BTHS Platform no staging (issue #77): perfis ADMIN, STAFF e HOSPEDE × dispositivos × cenários. Usa **apenas contas e dados fictícios** (ver `docs/staging.md`). O backup com dados reais não deve ser restaurado no staging.

Execução: desktop em 30/10/2026; iPhone (Safari) e tablet Android em 31/10/2026.

## Contas e dados de teste

| Item | Valor |
|---|---|
| ADMIN | `Admin Staging` |
| STAFF | `Staff QA` (`staff.qa@bths.test`) |
| HOSPEDE | `Hospede QA` (`hospede.qa@bths.test`, CPF fictício, sem telefone) e `Hospede QA2` (dados fictícios) |
| Viagem | `Viagem QA`, Itu-SP, 10 a 14/12/2026 |
| Hospedagem | `Tomorrowland Brasil 2027`, endereço fictício |
| Quartos | `QA-101` e `QA-102`, suíte, capacidade 2 |
| Transporte | `Motorista QA`, `Van QA` (4 lugares), Operação #1 GRU → hospedagem |
| Conteúdo | `Aviso QA`, evento de agenda `Festival QA - Dia 1` |

Senhas não são registradas no repositório.

## Como executar

- Usar perfis de navegador separados (ou janelas anônimas) para manter dois perfis logados ao mesmo tempo.
- Chamadas diretas à API pelo console: buscar `/api/auth/csrf`, ler o cookie `XSRF-TOKEN` e enviar no cabeçalho `X-XSRF-TOKEN`, com `credentials: 'include'` e caminho relativo (`/api/...`).
- O staging publica só por deploy manual no Render (branch `develop`). Conferir o commit publicado antes de testar uma correção.
- Legenda: ✅ passou · ⚠️ passou com ressalva · ❌ falhou · ⏳ pendente · n/a não se aplica.

## Matriz

### HOSPEDE

| Cenário | Desktop | Android (tablet) | iPhone |
|---|---|---|---|
| Login válido cai em `/app` com a viagem | ✅ | ✅ | n/a |
| Telas do app (viagem, quarto, hospedagem, transporte, QR, avisos, perfil) com dados corretos | ✅ (também em 375x812 emulado) | ✅ | n/a |
| Perfil errado: `/dashboard` redireciona para `/app` | ✅ | n/a | n/a |
| APIs de ADMIN/STAFF (`/api/viagens`, `/api/hospedes`, `/api/quartos`, `/api/motoristas`, `/api/usuarios/{id}`) | ✅ 403 | n/a | n/a |
| Dados de outro hóspede (`/api/hospedes/2`, `/api/traslados/1`, imagem do QR por código) | ✅ 403 | n/a | n/a |
| APIs do app só com dados do próprio hóspede e `Cache-Control: no-store` | ✅ | n/a | n/a |
| Refresh (F5) mantém a sessão | ✅ | n/a | n/a |
| Logout limpa a sessão (`/api/auth/me` 401, rotas voltam a `/login`) | ✅ | n/a | n/a |
| QR Code legível na tela e lido pela câmera do STAFF | ✅ | ✅ | n/a |

O iPhone foi usado com o perfil STAFF e o tablet com o HOSPEDE, para testar a leitura do QR entre os dois aparelhos.

### STAFF

| Cenário | Desktop | Android (tablet) | iPhone |
|---|---|---|---|
| Login inválido recusado | ✅ | n/a | n/a |
| Login válido cai em `/dashboard`, menu sem Motoristas e Veículos | ✅ | ✅ | ✅ |
| Leituras permitidas (viagens, hóspedes, quartos, hospedagens, operações, avisos, agenda) | ✅ 200 | n/a | n/a |
| Ações só de ADMIN pela API (usuários, motoristas, veículos, criar/editar hóspede, acesso BTHS, criar/excluir operação) | ✅ 403 | n/a | n/a |
| `/motoristas` mostra mensagem de permissão | ✅ | n/a | n/a |
| `/veiculos` e `/traslados` escondem ações de ADMIN | ⚠️ não escondem (achado 10) | n/a | n/a |
| Check-in por código: inválido, válido e duplicado (409) | ✅ | n/a | n/a |
| Check-in pela câmera ("Ler QR Code") | n/a | ⚠️ abre a câmera frontal (achado 13) | ⚠️ lê quase na hora, mas abre a câmera frontal (achado 13) |
| Alocar hóspede sem quarto e trocar de quarto | ✅ | n/a | n/a |
| Mudar status da operação (Iniciar) com histórico | ✅ | n/a | n/a |
| Telas Dashboard, Hóspedes, Traslados e detalhe do hóspede | ✅ | n/a | ✅ (ver observação) |
| Refresh e logout | ✅ | n/a | n/a |

Observação: no iPhone, numa primeira abertura, o Dashboard ficou mais largo que a tela (cards de resumo e "Sair" cortados). Num segundo teste ficou normal, provavelmente por zoom acidental. Se repetir no iPhone ou em outro celular, revisar a grade de 3 colunas dos cards no celular.

### ADMIN

| Cenário | Desktop | Android (tablet) | iPhone |
|---|---|---|---|
| Sem autenticação: APIs protegidas respondem 401 | ✅ | n/a | n/a |
| Escrita sem cabeçalho CSRF responde 403 | ✅ | n/a | n/a |
| Perfil errado: `/app` redireciona para `/dashboard`; `/api/app/**` 403 | ✅ | n/a | n/a |
| Criar quarto, hospedagem, vincular quarto, alocar hóspede, motorista, veículo, operação, vincular traslado, aviso, agenda | ✅ | n/a | n/a |
| Criar viagem pela interface | ❌ não existe tela (achado 2) | | |
| Criar usuário STAFF pela interface | ❌ não existe tela (achado 3) | | |
| Adicionar passageiro (traslado) a uma operação pela interface | ❌ não existe tela (achado 6) | | |
| Refresh e logout | ✅ | n/a | n/a |

O ADMIN é usado no desktop; os testes de celular focaram no hóspede e no STAFF, que usam o app no evento.

### Transversais

| Cenário | Situação |
|---|---|
| Sessão expirada | ✅ com 1 h de sessão, abrir uma rota volta para `/login` (cookie expira junto com o JWT); com a tela aberta, o 401 leva a `/login?motivo=sessao-expirada`, que mostra "Sua sessão expirou. Faça login novamente." |
| Bloqueio de login por excesso de tentativas | ✅ "Muitas tentativas de login. Tente novamente em 5 minutos." (padrão: 5 falhas em 15 min, bloqueio de 5 min até 1 h) |
| Horários gerados pelo servidor no fuso de Brasília | ❌ saem em UTC, 3 h adiantados (achado 11) |

## Achados

| # | Achado | Gravidade | Issue |
|---|---|---|---|
| 1 | Dashboard trava em "Carregando dashboard..." sem viagem cadastrada | baixo | #151 |
| 2 | Não existe tela para criar viagem | bloqueador para produção | #148 |
| 3 | Não existe tela de usuários (STAFF só por API) | alto | #149 |
| 4 | Lista de hóspedes quebrava com telefone nulo | bloqueador | corrigido no PR #144 e confirmado no staging |
| 6 | Não existe tela para criar o traslado individual, que é a vaga do hóspede numa operação | bloqueador para produção | #147 |
| 7 | Usuário logado que abre `/login` vê o formulário | baixo | #151 |
| 8 | Detalhe do hóspede leva cerca de 8 s no staging | observar | sem issue |
| 9 | `/app/viagem` mostra o endereço da viagem (vazio) em vez do da hospedagem | baixo | #151 |
| 10 | STAFF vê ações de ADMIN em `/traslados` e `/veiculos` (backend bloqueia) | médio | #150 |
| 11 | Horários gerados pelo servidor em UTC (check-in, histórico de status) | alto | #146 |
| 12 | STAFF pode escrever pela API em rotas que devem ser só do ADMIN (viagem, quarto, hospedagem, agenda, excluir aviso, criar traslado) | alto (permissão) | #148 e #152 |
| 13 | Leitor de QR do check-in abre a câmera frontal no iPhone e no tablet | médio | #153 |

O achado 5 (perfil não checado no `proxy.ts`) foi verificado e não gerou falha de segurança; o efeito de interface está no achado 10.

Fora da matriz, a imagem da viagem no hero da Home do hóspede, prevista no protótipo, foi registrada na #154.