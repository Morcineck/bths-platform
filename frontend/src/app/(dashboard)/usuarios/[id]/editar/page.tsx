"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import { ArrowLeft } from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { RedefinirSenhaForm } from "@/features/usuario/components/RedefinirSenhaForm";
import { UsuarioForm } from "@/features/usuario/components/UsuarioForm";

import {
  atualizarUsuario,
  buscarUsuarioPorId,
} from "@/features/usuario/services/usuarioService";

import type { UsuarioEquipe } from "@/features/usuario/types/usuario";

type EditarUsuarioPageProps = {
  params: Promise<{ id: string }>;
};

export default function EditarUsuarioPage({ params }: EditarUsuarioPageProps) {
  const { id } = use(params);
  const router = useRouter();

  const [usuario, setUsuario] = useState<UsuarioEquipe | null>(null);
  const [erro, setErro] = useState("");

  useEffect(() => {
    buscarUsuarioPorId(id)
      .then(setUsuario)
      .catch((error) =>
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar o usuário.",
        ),
      );
  }, [id]);

  if (erro) {
    return (
      <div
        role="alert"
        className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
      >
        {erro}
      </div>
    );
  }

  if (!usuario) {
    return <p className="text-sm text-muted">Carregando...</p>;
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/usuarios"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft size={15} strokeWidth={1.9} />
          Voltar para usuários
        </Link>

        <PageHeader
          title="Editar usuário"
          description={usuario.email}
        />
      </div>

      <UsuarioForm
        usuarioInicial={usuario}
        textoBotao="Salvar alterações"
        textoSalvando="Salvando..."
        onSalvar={async (dados) => {
          await atualizarUsuario(id, {
            nome: dados.nome,
            email: dados.email,
            perfil: dados.perfil,
          });

          router.push("/usuarios");
        }}
      />

      <RedefinirSenhaForm usuarioId={id} />
    </div>
  );
}