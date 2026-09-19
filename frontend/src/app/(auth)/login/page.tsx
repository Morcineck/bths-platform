import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";

export default function LoginPage() {
  return (
    <Card>
      <header>
        <p className="text-sm font-medium text-primary">
          Beat Trips
        </p>

        <h1 className="mt-2 text-2xl font-semibold tracking-tight text-foreground">
          Acesse o BTHS
        </h1>

        <p className="mt-2 text-sm leading-6 text-muted">
          Entre com suas credenciais para acessar a plataforma.
        </p>
      </header>

      <form className="mt-8 space-y-5">
        <div className="space-y-2">
          <label
            htmlFor="email"
            className="text-sm font-medium text-foreground"
          >
            E-mail
          </label>

          <Input
            id="email"
            name="email"
            type="email"
            placeholder="seu@email.com"
            autoComplete="email"
            required
          />
        </div>

        <div className="space-y-2">
          <label
            htmlFor="senha"
            className="text-sm font-medium text-foreground"
          >
            Senha
          </label>

          <Input
            id="senha"
            name="senha"
            type="password"
            placeholder="Digite sua senha"
            autoComplete="current-password"
            required
          />
        </div>
        <Button type="submit">
          Entrar
        </Button>
      </form>
    </Card>
  );
}