"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";

import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import {
  buscarUsuarioAutenticado,
  login,
} from "@/features/auth/services/authService";

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setErro("");
    setCarregando(true);

    try {
      await login({
        email,
        senha,
      });

      const usuario = await buscarUsuarioAutenticado();

      if (usuario.perfil === "HOSPEDE") {
        router.replace("/app");
        return;
      }

      router.replace("/dashboard");
    } catch {
      setErro(
        "Não foi possível entrar. Verifique suas credenciais e tente novamente.",
      );
    } finally {
      setCarregando(false);
    }
  }

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

      <form
        className="mt-8 space-y-5"
        onSubmit={handleSubmit}
      >
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
            value={email}
            onChange={(event) => setEmail(event.target.value)}
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
            value={senha}
            onChange={(event) => setSenha(event.target.value)}
          />
        </div>

        {erro && (
          <p
            role="alert"
            className="text-sm text-red-400"
          >
            {erro}
          </p>
        )}

        <Button
          type="submit"
          disabled={carregando}
        >
          {carregando ? "Entrando..." : "Entrar"}
        </Button>
      </form>
    </Card>
  );
}