"use client";

import Link from "next/link";

import {
  ChevronLeft,
  LogOut,
  Mail,
  ShieldCheck,
  UserRound,
} from "lucide-react";

import {
  useEffect,
  useState,
} from "react";

import {
  useRouter,
} from "next/navigation";

import {
  buscarUsuarioAutenticado,
  logout,
} from "@/features/auth/services/authService";

import type {
  UsuarioAutenticado,
} from "@/features/auth/types/auth";

function obterIniciais(
  nome: string,
) {
  const partes = nome
    .trim()
    .split(/\s+/)
    .filter(Boolean);

  if (partes.length === 0) {
    return "?";
  }

  if (partes.length === 1) {
    return partes[0]
      .charAt(0)
      .toUpperCase();
  }

  return (
    partes[0].charAt(0) +
    partes[partes.length - 1].charAt(0)
  ).toUpperCase();
}

function formatarPerfil(
  perfil: UsuarioAutenticado["perfil"],
) {
  switch (perfil) {
    case "HOSPEDE":
      return "Hóspede Beat Trips";

    case "ADMIN":
      return "Administrador";

    case "STAFF":
      return "Equipe Beat Trips";

    default:
      return perfil;
  }
}

export default function PerfilPage() {
  const router = useRouter();

  const [
    usuario,
    setUsuario,
  ] =
    useState<UsuarioAutenticado | null>(
      null,
    );

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    saindo,
    setSaindo,
  ] = useState(false);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function carregarPerfil() {
      try {
        setErro("");

        const usuarioAutenticado =
          await buscarUsuarioAutenticado();

        setUsuario(
          usuarioAutenticado,
        );
      } catch {
        setErro(
          "Não foi possível carregar seu perfil.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarPerfil();
  }, []);

  async function handleLogout() {
    try {
      setSaindo(true);
      setErro("");

      await logout();

      router.replace(
        "/login",
      );

      router.refresh();
    } catch {
      setErro(
        "Não foi possível encerrar a sessão.",
      );
    } finally {
      setSaindo(
        false,
      );
    }
  }

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando seu perfil...
        </p>
      </div>
    );
  }

  if (!usuario) {
    return (
      <div className="py-10">
        <p
          role="alert"
          className="text-sm text-danger"
        >
          {erro ||
            "Não foi possível carregar seu perfil."}
        </p>
      </div>
    );
  }

  const iniciais =
    obterIniciais(
      usuario.nome,
    );

  return (
    <div className="space-y-5">
      <header className="flex items-center gap-3">
        <Link
          href="/app"
          aria-label="Voltar"
          className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border bg-surface/40 text-foreground transition-colors active:scale-95"
        >
          <ChevronLeft
            size={20}
            strokeWidth={1.9}
          />
        </Link>

        <div className="min-w-0">
          <h1 className="truncate text-xl font-semibold tracking-tight text-foreground">
            Meu Perfil
          </h1>

          <p className="truncate text-xs text-muted">
            Sua conta BTHS
          </p>
        </div>
      </header>

      <section className="relative overflow-hidden rounded-3xl border border-border bg-surface/60 p-5 text-center shadow-soft">
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_18%),transparent_38%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_10%),transparent_42%)]" />

        <div className="relative">
          <span className="gradient-brand shadow-glow mx-auto flex h-20 w-20 items-center justify-center rounded-3xl text-xl font-semibold text-white">
            {iniciais}
          </span>

          <h2 className="mt-4 text-lg font-semibold text-foreground">
            {usuario.nome}
          </h2>

          <p className="mt-1 text-xs text-muted">
            {formatarPerfil(
              usuario.perfil,
            )}
          </p>

          <div className="mt-5 overflow-hidden rounded-2xl border border-border bg-background/35 text-left">
            <div className="flex items-center gap-3 border-b border-border p-4">
              <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                <Mail
                  size={17}
                  strokeWidth={1.8}
                />
              </span>

              <div className="min-w-0">
                <p className="text-xs text-muted">
                  E-mail
                </p>

                <p className="mt-1 truncate text-sm font-medium text-foreground">
                  {usuario.email}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-4">
              <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                <UserRound
                  size={17}
                  strokeWidth={1.8}
                />
              </span>

              <div>
                <p className="text-xs text-muted">
                  Perfil
                </p>

                <p className="mt-1 text-sm font-medium text-foreground">
                  {formatarPerfil(
                    usuario.perfil,
                  )}
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="rounded-2xl border border-border bg-surface/50 p-4">
        <div className="flex items-start gap-3">
          <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-success/10 text-success">
            <ShieldCheck
              size={18}
              strokeWidth={1.8}
            />
          </span>

          <div>
            <p className="text-sm font-semibold text-foreground">
              Conta protegida
            </p>

            <p className="mt-1 text-xs leading-5 text-muted">
              Seu acesso é pessoal e suas informações
              ficam vinculadas à sua experiência
              Beat Trips.
            </p>
          </div>
        </div>
      </section>

      {erro && (
        <p
          role="alert"
          className="text-sm text-danger"
        >
          {erro}
        </p>
      )}

      <button
        type="button"
        onClick={handleLogout}
        disabled={saindo}
        className="flex h-12 w-full items-center justify-center gap-2 rounded-2xl border border-danger/30 text-sm font-semibold text-danger transition-colors hover:bg-danger/10 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-50"
      >
        <LogOut
          size={17}
          strokeWidth={1.9}
        />

        {saindo
          ? "Saindo..."
          : "Sair da conta"}
      </button>
    </div>
  );
}