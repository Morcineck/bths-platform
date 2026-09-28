"use client";

import { HospedeNavigation } from "@/components/layout/HospedeNavigation";
import type { ReactNode } from "react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import {
  buscarUsuarioAutenticado,
  logout,
} from "@/features/auth/services/authService";

import type {
  UsuarioAutenticado,
} from "@/features/auth/types/auth";

type AppHospedeLayoutProps = {
  children: ReactNode;
};

export default function AppHospedeLayout({
  children,
}: AppHospedeLayoutProps) {
  const router = useRouter();

  const [
    usuario,
    setUsuario,
  ] = useState<UsuarioAutenticado | null>(
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
    erroLogout,
    setErroLogout,
  ] = useState("");

  useEffect(() => {
    async function carregarUsuario() {
      try {
        const usuarioAutenticado =
          await buscarUsuarioAutenticado();

        if (
          usuarioAutenticado.perfil !==
          "HOSPEDE"
        ) {
          router.replace(
            "/dashboard",
          );

          return;
        }

        setUsuario(
          usuarioAutenticado,
        );
      } catch {
        router.replace(
          "/login",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarUsuario();
  }, [router]);

  async function handleLogout() {
    try {
      setSaindo(true);
      setErroLogout("");

      await logout();

      router.replace(
        "/login",
      );

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
        <div className="mx-auto flex w-full max-w-xl items-center justify-between px-5 py-4">
          <div>
            <p className="text-sm font-semibold text-primary">
              Beat Trips
            </p>

            <p className="text-lg font-semibold text-foreground">
              BTHS
            </p>
          </div>

          <button
            type="button"
            onClick={handleLogout}
            disabled={saindo}
            className="rounded-xl border border-border px-4 py-2 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
          >
            {saindo
              ? "Saindo..."
              : "Sair"}
          </button>
        </div>
      </header>

      <HospedeNavigation />

      {erroLogout && (
        <div className="mx-auto w-full max-w-xl px-5 pt-4">
          <p
            role="alert"
            className="text-sm text-red-400"
          >
            {erroLogout}
          </p>
        </div>
      )}

      <main className="mx-auto w-full max-w-xl px-5 py-6 pb-24">
        {children}
      </main>
    </div>
  );
}