"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  Mail,
  Pencil,
  Plus,
  Power,
  RotateCcw,
  UsersRound,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import {
  desativarUsuario,
  listarUsuarios,
  reativarUsuario,
} from "@/features/usuario/services/usuarioService";

import type { UsuarioEquipe } from "@/features/usuario/types/usuario";

export default function UsuariosPage() {
  const [usuarios, setUsuarios] = useState<UsuarioEquipe[]>([]);
  const [idUsuarioLogado, setIdUsuarioLogado] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [alterandoId, setAlterandoId] = useState<string | null>(null);

  useEffect(() => {
    async function carregar() {
      try {
        const [lista, usuarioLogado] = await Promise.all([
          listarUsuarios(),
          buscarUsuarioAutenticado(),
        ]);

        setUsuarios(lista);
        setIdUsuarioLogado(usuarioLogado.id);
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os usuários.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, []);

  async function handleAlterarStatus(usuario: UsuarioEquipe) {
    setErro("");
    setMensagem("");
    setAlterandoId(usuario.id);

    try {
      const atualizado = usuario.ativo
        ? await desativarUsuario(usuario.id)
        : await reativarUsuario(usuario.id);

      setUsuarios((atuais) =>
        atuais.map((item) => (item.id === atualizado.id ? atualizado : item)),
      );

      setMensagem(
        atualizado.ativo
          ? `${atualizado.nome} foi reativado.`
          : `${atualizado.nome} foi desativado e não consegue mais entrar.`,
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível alterar o status do usuário.",
      );
    } finally {
      setAlterandoId(null);
    }
  }

  if (carregando) {
    return <p className="text-sm text-muted">Carregando usuários...</p>;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Usuários"
          description="Gerencie os acessos da equipe Beat Trips (ADMIN e STAFF)."
        />

        <Link
          href="/usuarios/novo"
          className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
        >
          <Plus size={18} strokeWidth={1.9} />
          Novo usuário
        </Link>
      </div>

      {mensagem && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {mensagem}
        </div>
      )}

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
        {usuarios.map((usuario) => {
          const ehUsuarioLogado = usuario.id === idUsuarioLogado;

          return (
            <article
              key={usuario.id}
              className={`rounded-2xl border border-border bg-surface/50 p-4 shadow-soft ${
                usuario.ativo ? "" : "opacity-60"
              }`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex min-w-0 items-start gap-3">
                  <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                    <UsersRound size={20} strokeWidth={1.9} />
                  </span>

                  <div className="min-w-0">
                    <p className="truncate text-sm font-semibold text-foreground">
                      {usuario.nome}
                      {ehUsuarioLogado && (
                        <span className="ml-1 text-xs font-normal text-muted">(você)</span>
                      )}
                    </p>

                    <p className="mt-1 flex items-center gap-1.5 truncate text-xs text-muted">
                      <Mail size={13} strokeWidth={1.9} className="shrink-0" />
                      {usuario.email}
                    </p>
                  </div>
                </div>

                <div className="flex shrink-0 flex-col items-end gap-1">
                  <span className="inline-flex items-center rounded-full border border-primary/30 bg-primary/10 px-2.5 py-1 text-[11px] font-medium text-primary">
                    {usuario.perfil}
                  </span>

                  {!usuario.ativo && (
                    <span className="inline-flex items-center rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger">
                      Inativo
                    </span>
                  )}
                </div>
              </div>

              <div className="mt-3 grid grid-cols-2 gap-2 border-t border-border pt-3">
                <Link
                  href={`/usuarios/${usuario.id}/editar`}
                  className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
                >
                  <Pencil size={14} strokeWidth={1.9} />
                  Editar
                </Link>

                {!ehUsuarioLogado && (
                  <button
                    type="button"
                    disabled={alterandoId === usuario.id}
                    onClick={() => handleAlterarStatus(usuario)}
                    className={`inline-flex h-9 items-center justify-center gap-2 rounded-xl border px-3 text-xs font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50 ${
                      usuario.ativo
                        ? "border-danger/30 text-danger hover:bg-danger/5"
                        : "border-success/30 text-success hover:bg-success/5"
                    }`}
                  >
                    {usuario.ativo ? (
                      <Power size={14} strokeWidth={1.9} />
                    ) : (
                      <RotateCcw size={14} strokeWidth={1.9} />
                    )}
                    {alterandoId === usuario.id
                      ? "Salvando..."
                      : usuario.ativo
                        ? "Desativar"
                        : "Reativar"}
                  </button>
                )}
              </div>
            </article>
          );
        })}
      </div>
    </div>
  );
}
