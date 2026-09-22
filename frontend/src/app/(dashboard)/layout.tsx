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
    <div className="min-h-screen bg-background">
      <header className="border-b border-border bg-surface">
        <div className="mx-auto flex w-full items-center justify-between px-6 py-4">
          <div>
            <p className="text-sm font-semibold text-primary">
              Beat Trips
            </p>

            <p className="text-lg font-semibold text-foreground">
              BTHS
            </p>
          </div>

          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="text-sm font-medium text-foreground">
                {usuario.nome}
              </p>

              <p className="text-xs text-muted">
                {usuario.perfil}
              </p>
            </div>

            <button
              type="button"
              onClick={handleLogout}
              disabled={saindo}
              className="rounded-xl border border-border px-4 py-2 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
            >
              {saindo ? "Saindo..." : "Sair"}
            </button>
          </div>
        </div>
      </header>

      {erroLogout && (
        <div className="px-6 pt-4">
          <p
            role="alert"
            className="text-sm text-red-400"
          >
            {erroLogout}
          </p>
        </div>
      )}

      <div className="flex min-h-[calc(100vh-73px)]">
        <OperationalNavigation />

        <main className="w-full min-w-0 px-6 py-8 pb-24 md:pb-8">
          <div className="mx-auto w-full max-w-6xl">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}