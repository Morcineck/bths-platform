"use client";

import {
  FormEvent,
  useEffect,
  useState,
} from "react";

import { useRouter } from "next/navigation";

import Image from "next/image";

import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";

import {
  buscarUsuarioAutenticado,
  logout,
  rotaInicial,
  trocarSenha,
} from "@/features/auth/services/authService";

import type { UsuarioAutenticado } from "@/features/auth/types/auth";

const TAMANHO_MINIMO_SENHA = 8;

export default function TrocarSenhaPage() {
  const router = useRouter();

  const [usuario, setUsuario] =
    useState<UsuarioAutenticado | null>(null);

  const [senhaAtual, setSenhaAtual] = useState("");
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmacao, setConfirmacao] = useState("");
  const [erro, setErro] = useState("");

  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    async function carregarUsuario() {
      try {
        setUsuario(await buscarUsuarioAutenticado());
      } catch {
        router.replace("/login");
      }
    }

    carregarUsuario();
  }, [router]);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setErro("");

    if (novaSenha.length < TAMANHO_MINIMO_SENHA) {
      setErro(
        `A nova senha deve possuir pelo menos ${TAMANHO_MINIMO_SENHA} caracteres.`,
      );
      return;
    }

    if (novaSenha !== confirmacao) {
      setErro("A confirmação não confere com a nova senha.");
      return;
    }

    if (!usuario) {
      return;
    }

    setSalvando(true);

    try {
      await trocarSenha({
        senhaAtual,
        novaSenha,
      });

      router.replace(
        rotaInicial({
          ...usuario,
          trocaSenhaObrigatoria: false,
        }),
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível trocar a senha.",
      );
    } finally {
      setSalvando(false);
    }
  }

  async function handleSair() {
    try {
      await logout();
    } finally {
      router.replace("/login");
    }
  }

  if (!usuario) {
    return null;
  }

  return (
    <Card className="glass-login shadow-soft">
      <header>
        <div className="flex flex-col items-center text-center">
          <Image
            src="/brand/beat-trips-simbolo.webp"
            alt="Beat Trips"
            width={96}
            height={96}
            priority
            className="brand-glow h-24 w-24"
          />

          <h1 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
            {usuario.trocaSenhaObrigatoria
              ? "Crie sua nova senha"
              : "Trocar senha"}
          </h1>

          <p className="mt-2 text-sm leading-6 text-muted">
            {usuario.trocaSenhaObrigatoria
              ? "No primeiro acesso, troque a senha temporária por uma senha só sua."
              : "Informe sua senha atual e escolha uma nova senha."}
          </p>
        </div>
      </header>

      <form
        className="mt-8 space-y-5"
        onSubmit={handleSubmit}
      >
        <div className="space-y-2">
          <label
            htmlFor="senhaAtual"
            className="text-sm font-medium text-foreground"
          >
            {usuario.trocaSenhaObrigatoria
              ? "Senha temporária"
              : "Senha atual"}
          </label>

          <Input
            id="senhaAtual"
            name="senhaAtual"
            type="password"
            autoComplete="current-password"
            required
            value={senhaAtual}
            onChange={(event) =>
              setSenhaAtual(event.target.value)
            }
          />
        </div>

        <div className="space-y-2">
          <label
            htmlFor="novaSenha"
            className="text-sm font-medium text-foreground"
          >
            Nova senha
          </label>

          <Input
            id="novaSenha"
            name="novaSenha"
            type="password"
            placeholder={`Mínimo de ${TAMANHO_MINIMO_SENHA} caracteres`}
            autoComplete="new-password"
            required
            value={novaSenha}
            onChange={(event) =>
              setNovaSenha(event.target.value)
            }
          />
        </div>

        <div className="space-y-2">
          <label
            htmlFor="confirmacao"
            className="text-sm font-medium text-foreground"
          >
            Confirme a nova senha
          </label>

          <Input
            id="confirmacao"
            name="confirmacao"
            type="password"
            autoComplete="new-password"
            required
            value={confirmacao}
            onChange={(event) =>
              setConfirmacao(event.target.value)
            }
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
          disabled={salvando}
        >
          {salvando
            ? "Salvando..."
            : "Salvar nova senha"}
        </Button>

        <button
          type="button"
          onClick={handleSair}
          className="w-full text-sm text-muted transition-colors hover:text-primary"
        >
          Sair
        </button>
      </form>
    </Card>
  );
}
