"use client";

import Link from "next/link";
import {
  FormEvent,
  use,
  useEffect,
  useState,
} from "react";

import { useRouter } from "next/navigation";


import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import {
  atualizarMotorista,
  buscarMotoristaPorId,
} from "@/features/motorista/services/motoristaService";

import type { Motorista } from "@/features/motorista/types/motorista";

type EditarMotoristaPageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default function EditarMotoristaPage({
  params,
}: EditarMotoristaPageProps) {
  const { id } = use(params);

  const router = useRouter();

  const [motorista, setMotorista] =
    useState<Motorista | null>(null);

  const [carregando, setCarregando] =
    useState(true);

  const [acessoPermitido, setAcessoPermitido] =
    useState(false);

  const [nomeCompleto, setNomeCompleto] =
    useState("");

  const [telefone, setTelefone] =
    useState("");

  const [observacao, setObservacao] =
    useState("");

  const [salvando, setSalvando] =
    useState(false);

  const [erro, setErro] =
    useState("");

  const [sucesso, setSucesso] =
    useState("");

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

        const dados =
          await buscarMotoristaPorId(
            Number(id),
          );

        setMotorista(dados);

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
              telefone.trim() || undefined,
            observacao:
              observacao.trim() || undefined,
          },
        );

      setMotorista(atualizado);

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
        <p className="text-sm text-red-400">
          Você não possui permissão para editar motoristas.
        </p>

        <Link
          href="/motoristas"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para motoristas
        </Link>
      </div>
    );
  }

  if (erro && !motorista) {
    return (
      <div className="space-y-4">
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>

        <Link
          href="/motoristas"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para motoristas
        </Link>
      </div>
    );
  }

  if (!motorista) {
    return null;
  }

  return (
    <div className="space-y-8">
      <header>
        <Link
          href="/motoristas"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para motoristas
        </Link>

        <h1 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
          Editar motorista
        </h1>

        <p className="mt-2 text-sm text-muted">
          Atualize os dados cadastrais do motorista.
        </p>
      </header>

      <form
        onSubmit={handleSalvar}
        className="space-y-5"
      >
        <div>
          <label
            htmlFor="nome-completo"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Nome completo
          </label>

          <input
            id="nome-completo"
            type="text"
            value={nomeCompleto}
            onChange={(event) =>
              setNomeCompleto(event.target.value)
            }
            className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        <div>
          <label
            htmlFor="telefone"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Telefone
          </label>

          <input
            id="telefone"
            type="text"
            value={telefone}
            onChange={(event) =>
              setTelefone(event.target.value)
            }
            className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        <div>
          <label
            htmlFor="observacao"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Observação
          </label>

          <textarea
            id="observacao"
            value={observacao}
            onChange={(event) =>
              setObservacao(event.target.value)
            }
            rows={4}
            className="w-full rounded-xl border border-border bg-surface px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        {erro && (
          <p
            role="alert"
            className="text-sm text-red-400"
          >
            {erro}
          </p>
        )}

        {sucesso && (
          <p className="text-sm text-green-400">
            {sucesso}
          </p>
        )}

        <div className="flex flex-wrap gap-3">
          <button
            type="submit"
            disabled={salvando}
            className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
          >
            {salvando
              ? "Salvando..."
              : "Salvar alterações"}
          </button>

          <button
            type="button"
            onClick={() =>
              router.push("/motoristas")
            }
            className="rounded-xl border border-border px-5 py-3 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}