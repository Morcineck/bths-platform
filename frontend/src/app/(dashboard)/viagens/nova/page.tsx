"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import { useRouter } from "next/navigation";

import { ArrowLeft } from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import { ViagemForm } from "@/features/viagem/components/ViagemForm";

import { cadastrarViagem } from "@/features/viagem/services/viagemService";

export default function NovaViagemPage() {
  const router = useRouter();

  const [carregando, setCarregando] = useState(true);
  const [acessoPermitido, setAcessoPermitido] =
    useState(false);

  useEffect(() => {
    async function verificarPermissao() {
      try {
        const usuario =
          await buscarUsuarioAutenticado();

        setAcessoPermitido(usuario.perfil === "ADMIN");
      } catch {
        setAcessoPermitido(false);
      } finally {
        setCarregando(false);
      }
    }

    verificarPermissao();
  }, []);

  if (carregando) {
    return (
      <p className="text-sm text-muted">Carregando...</p>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/viagens"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft size={15} strokeWidth={1.9} />

          Voltar para viagens
        </Link>

        <PageHeader
          title="Nova viagem"
          description="Cadastre uma viagem da Beat Trips."
        />
      </div>

      {acessoPermitido ? (
        <ViagemForm
          textoBotao="Cadastrar viagem"
          textoSalvando="Cadastrando..."
          onSalvar={async (dados) => {
            await cadastrarViagem(dados);

            router.push("/viagens");
          }}
        />
      ) : (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Você não possui permissão para cadastrar viagens.
        </div>
      )}
    </div>
  );
}