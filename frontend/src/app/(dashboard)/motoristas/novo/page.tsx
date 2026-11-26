"use client";

import Link from "next/link";
import {
  FormEvent,
  useEffect,
  useState,
} from "react";

import { useRouter } from "next/navigation";

import {
  ArrowLeft,
  Save,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import {
  cadastrarMotorista,
} from "@/features/motorista/services/motoristaService";

export default function NovoMotoristaPage() {
  const router = useRouter();

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    acessoPermitido,
    setAcessoPermitido,
  ] = useState(false);

  const [
    nomeCompleto,
    setNomeCompleto,
  ] = useState("");

  const [
    telefone,
    setTelefone,
  ] = useState("");

  const [
    observacao,
    setObservacao,
  ] = useState("");

  const [
    salvando,
    setSalvando,
  ] = useState(false);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function verificarPermissao() {
      try {
        const usuario =
          await buscarUsuarioAutenticado();

        setAcessoPermitido(
          usuario.perfil === "ADMIN",
        );
      } catch {
        setAcessoPermitido(false);
      } finally {
        setCarregando(false);
      }
    }

    verificarPermissao();
  }, []);

  async function handleCadastrarMotorista(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (!nomeCompleto.trim()) {
      setErro(
        "Informe o nome do motorista.",
      );
      return;
    }

    try {
      setSalvando(true);
      setErro("");

      const motorista =
        await cadastrarMotorista({
          nomeCompleto:
            nomeCompleto.trim(),
          telefone:
            telefone.trim() || undefined,
          observacao:
            observacao.trim() || undefined,
        });

      router.push(
        `/motoristas?criado=${motorista.id}`,
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar o motorista.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando...
      </p>
    );
  }

  if (!acessoPermitido) {
    return (
      <div className="space-y-4">
        <Link
          href="/motoristas"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para motoristas
        </Link>

        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Você não possui permissão para cadastrar motoristas.
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/motoristas"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para motoristas
        </Link>

        <PageHeader
          title="Novo motorista"
          description="Cadastre um motorista para a operação."
        />
      </div>

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      <form
        onSubmit={handleCadastrarMotorista}
        className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
      >
        <div className="space-y-5">
          <div>
            <label
              htmlFor="nome-completo"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Nome completo
            </label>

            <input
              id="nome-completo"
              type="text"
              value={nomeCompleto}
              onChange={(event) =>
                setNomeCompleto(
                  event.target.value,
                )
              }
              placeholder="Nome do motorista"
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
            />
          </div>

          <div>
            <label
              htmlFor="telefone"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Telefone
            </label>

            <input
              id="telefone"
              type="text"
              value={telefone}
              onChange={(event) =>
                setTelefone(
                  event.target.value,
                )
              }
              placeholder="Telefone do motorista"
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
            />
          </div>

          <div>
            <label
              htmlFor="observacao"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Observação
            </label>

            <textarea
              id="observacao"
              value={observacao}
              onChange={(event) =>
                setObservacao(
                  event.target.value,
                )
              }
              rows={3}
              placeholder="Informações adicionais sobre o motorista..."
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
            />
          </div>
        </div>

        <div className="mt-5 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <Link
            href="/motoristas"
            className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
          >
            Cancelar
          </Link>

          <button
            type="submit"
            disabled={salvando}
            className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Save
              size={17}
              strokeWidth={1.9}
            />

            {salvando
              ? "Cadastrando..."
              : "Cadastrar motorista"}
          </button>
        </div>
      </form>
    </div>
  );
}