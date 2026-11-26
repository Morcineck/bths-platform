"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";

import { useRouter } from "next/navigation";

import { ArrowLeft } from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import { ViagemForm } from "@/features/viagem/components/ViagemForm";

import {
  atualizarViagem,
  buscarViagemPorId,
} from "@/features/viagem/services/viagemService";

import type { Viagem } from "@/features/viagem/types/viagem";

type EditarViagemPageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default function EditarViagemPage({
  params,
}: EditarViagemPageProps) {
  const { id } = use(params);

  const router = useRouter();

  const [viagem, setViagem] = useState<Viagem | null>(
    null,
  );
  const [carregando, setCarregando] = useState(true);
  const [acessoPermitido, setAcessoPermitido] =
    useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        if (usuario.perfil !== "ADMIN") {
          setAcessoPermitido(false);

          return;
        }

        setAcessoPermitido(true);

        setViagem(await buscarViagemPorId(Number(id)));
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar a viagem.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, [id]);

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
          title="Editar viagem"
          description="Atualize os dados da viagem."
        />
      </div>

      {!acessoPermitido ? (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Você não possui permissão para editar viagens.
        </div>
      ) : erro || !viagem ? (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro || "Viagem não encontrada."}
        </div>
      ) : (
        <ViagemForm
          viagemInicial={viagem}
          textoBotao="Salvar alterações"
          textoSalvando="Salvando..."
          onSalvar={async (dados) => {
            await atualizarViagem(viagem.id, dados);

            router.push("/viagens");
          }}
        />
      )}
    </div>
  );
}