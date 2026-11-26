// redirect: função do Next.js que, num componente de servidor, interrompe a renderização e manda o navegador para outra rota
import { redirect } from "next/navigation";

// A raiz do app não tem conteúdo próprio: quem abre app.beattrips.com.br quer entrar no sistema
// O /login já trata quem está logado (ADMIN e STAFF vão para /dashboard, HOSPEDE para /app), desde a #151
export default function Home() {
  redirect("/login");
}