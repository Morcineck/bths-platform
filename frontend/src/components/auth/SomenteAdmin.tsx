"use client";

import { ReactNode, useEffect, useState } from "react";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

type SomenteAdminProps = {
  mensagem: string;
  children: ReactNode;
};

export function SomenteAdmin({
  mensagem,
  children,
}: SomenteAdminProps) {
  const [verificando, setVerificando] = useState(true);
  const [permitido, setPermitido] = useState(false);

  useEffect(() => {
    async function verificarPerfil() {
      try {
        const usuario = await buscarUsuarioAutenticado();

        setPermitido(usuario.perfil === "ADMIN");
      } catch {
        setPermitido(false);
      } finally {
        setVerificando(false);
      }
    }

    verificarPerfil();
  }, []);

  if (verificando) {
    return (
      <p className="text-sm text-muted">Carregando...</p>
    );
  }

  if (!permitido) {
    return (
      <div
        role="alert"
        className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
      >
        {mensagem}
      </div>
    );
  }

  return <>{children}</>;
}