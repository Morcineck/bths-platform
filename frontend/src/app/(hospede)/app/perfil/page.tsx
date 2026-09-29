"use client";

import { LogOut } from "lucide-react";
import { useRouter } from "next/navigation";
import { useState } from "react";

import {
  logout,
} from "@/features/auth/services/authService";

export default function PerfilPage() {
  const router = useRouter();

  const [
    saindo,
    setSaindo,
  ] = useState(false);

  const [
    erro,
    setErro,
  ] = useState("");

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
      setSaindo(false);
    }
  }

  return (
    <section className="space-y-5">
      <div>
        <p className="text-sm text-muted">
          Sua conta
        </p>

        <h1 className="mt-1 text-2xl font-semibold text-foreground">
          Perfil
        </h1>
      </div>

      <div className="rounded-3xl border border-border bg-surface/70 p-5">
        <p className="text-sm text-muted">
          Seus dados e configurações da conta aparecerão aqui.
        </p>
      </div>

      {erro && (
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>
      )}

      <button
        type="button"
        onClick={handleLogout}
        disabled={saindo}
        className="flex h-12 w-full items-center justify-center gap-2 rounded-2xl border border-red-500/30 text-sm font-medium text-red-400 transition-colors hover:bg-red-500/10 disabled:cursor-not-allowed disabled:opacity-50"
      >
        <LogOut size={17} />

        {saindo
          ? "Saindo..."
          : "Sair"}
      </button>
    </section>
  );
}