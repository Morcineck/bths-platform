"use client";

import Link from "next/link";

import {
  FormEvent,
  use,
  useEffect,
  useState,
} from "react";

import {
  useRouter,
} from "next/navigation";

import {
  ArrowLeft,
  Save,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import {
  atualizarMotorista,
  buscarMotoristaPorId,
} from "@/features/motorista/services/motoristaService";

import type {
  Motorista,
} from "@/features/motorista/types/motorista";

type EditarMotoristaPageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default function EditarMotoristaPage({
  params,
}: EditarMotoristaPageProps) {
  const { id } = use(
    params,
  );

  const router = useRouter();

  const [
    motorista,
    setMotorista,
  ] = useState<Motorista | null>(
    null,
  );

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

  const [
    sucesso,
    setSucesso,
  ] = useState("");

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        if (
          usuario.perfil !==
          "ADMIN"
        ) {
          setAcessoPermitido(false);
          return;
        }

        setAcessoPermitido(true);

        const dados =
          await buscarMotoristaPorId(
            Number(id),
          );

        setMotorista(
          dados,
        );

        setNomeCompleto(
          dados.nomeCompleto ?? "",
        );

        setTelefone(
          dados.telefone ?? "",
        );

        setObservacao(
          dados.observacao ?? "",
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar o motorista.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, [id]);

  async function handleSalvar(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (!motorista) {
      return;
    }

    if (!nomeCompleto.trim()) {
      setErro(
        "Informe o nome do motorista.",
      );

      return;
    }

    try {
      setSalvando(true);
      setErro("");
      setSucesso("");

      const atualizado =
        await atualizarMotorista(
          Number(id),
          {
            nomeCompleto:
              nomeCompleto.trim(),

            telefone:
              telefone.trim() ||
              undefined,

            observacao:
              observacao.trim() ||
              undefined,
          },
        );

      setMotorista(
        atualizado,
      );

      setSucesso(
        "Motorista atualizado com sucesso.",
      );
    } catch (error) {
      setSucesso("");

      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar o motorista.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando motorista...
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
          Você não possui permissão para editar motoristas.
        </div>
      </div>
    );
  }

  if (
    erro &&
    !motorista
  ) {
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
          {erro}
        </div>
      </div>
    );
  }

  if (!motorista) {
    return null;
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
          title="Editar motorista"
          description="Atualize os dados cadastrais do motorista."
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

      {sucesso && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {sucesso}
        </div>
      )}

      <form
        onSubmit={handleSalvar}
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
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
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
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
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
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
            />
          </div>
        </div>

        <div className="mt-5 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <button
            type="button"
            onClick={() =>
              router.push(
                "/motoristas",
              )
            }
            className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
          >
            Cancelar
          </button>

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
              ? "Salvando..."
              : "Salvar alterações"}
          </button>
        </div>
      </form>
    </div>
  );
}