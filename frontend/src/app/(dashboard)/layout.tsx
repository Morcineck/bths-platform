"use client";

import { ReactNode, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";
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
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-4">
          <div>
            <p className="text-sm font-semibold text-primary">
              Beat Trips
            </p>

            <p className="text-lg font-semibold text-foreground">
              BTHS
            </p>
          </div>

          <div className="text-right">
            <p className="text-sm font-medium text-foreground">
              {usuario.nome}
            </p>

            <p className="text-xs text-muted">
              {usuario.perfil}
            </p>
          </div>
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl px-6 py-8">
        {children}
      </main>
    </div>
  );
}