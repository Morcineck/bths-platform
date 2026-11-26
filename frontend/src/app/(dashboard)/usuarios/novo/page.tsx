"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

import { ArrowLeft } from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { UsuarioForm } from "@/features/usuario/components/UsuarioForm";

import { cadastrarUsuario } from "@/features/usuario/services/usuarioService";

export default function NovoUsuarioPage() {
  const router = useRouter();

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
          title="Novo usuário"
          description="Crie o acesso de um membro da equipe (ADMIN ou STAFF)."
        />
      </div>

      <UsuarioForm
        textoBotao="Cadastrar usuário"
        textoSalvando="Cadastrando..."
        onSalvar={async (dados) => {
          await cadastrarUsuario(dados);

          router.push("/usuarios");
        }}
      />
    </div>
  );
}