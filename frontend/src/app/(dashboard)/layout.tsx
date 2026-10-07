"use client";

import { ReactNode, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { OperationalNavigation } from "@/components/layout/OperationalNavigation";

import {
  buscarUsuarioAutenticado,
  logout,
} from "@/features/auth/services/authService";
import type { UsuarioAutenticado } from "@/features/auth/types/auth";

type DashboardLayoutProps = {
  children: ReactNode;
};

export default function DashboardLayout({
  children,
}: DashboardLayoutProps) {
  const router = useRouter();

  const [usuario, setUsuario] =
    useState<UsuarioAutenticado | null>(null);

  const [carregando, setCarregando] = useState(true);

  const [saindo, setSaindo] = useState(false);

  const [erroLogout, setErroLogout] =
    useState("");

  useEffect(() => {
    async function carregarUsuario() {
      try {
        const usuarioAutenticado =
          await buscarUsuarioAutenticado();

        if (usuarioAutenticado.perfil === "HOSPEDE") {
          router.replace("/app");
          return;
        }

        setUsuario(usuarioAutenticado);
      } catch {
        router.replace("/login");
      } finally {
        setCarregando(false);
      }
    }

    carregarUsuario();
  }, [router]);

  async function handleLogout() {
    try {
      setSaindo(true);
      setErroLogout("");

      await logout();

      router.replace("/login");
      router.refresh();
    } catch {
      setErroLogout(
        "Não foi possível encerrar a sessão.",
      );
    } finally {
      setSaindo(false);
    }
  }

  if (carregando) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-6">
        <p className="text-sm text-muted">
          Carregando BTHS...
        </p>
      </main>
    );
  }

  if (!usuario) {
    return null;
  }

  return (
    <div className="min-h-screen">
      <header className="glass sticky top-0 z-40 border-b border-border">
        <div className="flex w-full items-center justify-between gap-4 px-5 py-3 md:px-6">
          <div className="flex items-center gap-3">
            <span className="gradient-brand shadow-glow flex h-10 w-10 items-center justify-center rounded-2xl text-sm font-bold text-white">
              B
            </span>

            <div>
              <p className="text-sm font-semibold tracking-tight text-foreground">
                BTHS
              </p>

              <p className="text-xs text-muted">
                {usuario.perfil === "ADMIN"
                  ? "Administração"
                  : "Operação"}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="hidden text-right sm:block">
              <p className="text-sm font-medium text-foreground">
                {usuario.nome}
              </p>

              <p className="text-xs text-muted">
                {usuario.perfil === "ADMIN"
                  ? "Administrador"
                  : "Staff"}
              </p>
            </div>

            <button
              type="button"
              onClick={handleLogout}
              disabled={saindo}
              className="rounded-xl border border-border bg-surface/50 px-4 py-2 text-sm font-medium text-muted transition-all hover:border-primary/50 hover:bg-primary/10 hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
            >
              {saindo ? "Saindo..." : "Sair"}
            </button>
          </div>
        </div>
      </header>

      {erroLogout && (
        <div className="mx-auto w-full max-w-7xl px-5 pt-4 md:px-6">
          <div
            role="alert"
            className="rounded-2xl border border-danger/30 bg-danger/10 px-4 py-3 text-sm text-danger"
          >
            {erroLogout}
          </div>
        </div>
      )}

      <div className="mx-auto flex min-h-[calc(100vh-65px)] w-full max-w-[1600px]">
        <OperationalNavigation
          perfil={usuario.perfil}
        />

        <main className="w-full min-w-0 px-4 py-6 pb-24 sm:px-5 md:px-7 md:py-8 md:pb-10 lg:px-8">
          <div className="mx-auto w-full max-w-7xl">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}