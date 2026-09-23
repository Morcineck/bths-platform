"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import { PageHeader } from "@/components/layout/PageHeader";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";
import { cadastrarMotorista } from "@/features/motorista/services/motoristaService";

export default function NovoMotoristaPage() {
  const router = useRouter();

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
      <p className="text-sm text-red-400">
        Você não possui permissão para cadastrar motoristas.
      </p>
    );
  }

  return (
    <div className="space-y-8">
      <PageHeader
        title="Novo motorista"
        description="Cadastre um motorista para a operação."
      />

      <form
        onSubmit={handleCadastrarMotorista}
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

        <button
          type="submit"
          disabled={salvando}
          className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
        >
          {salvando
            ? "Cadastrando..."
            : "Cadastrar motorista"}
        </button>
      </form>
    </div>
  );
}