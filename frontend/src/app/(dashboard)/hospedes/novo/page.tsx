"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  ArrowLeft,
  Save,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import {
  cadastrarHospede,
} from "@/features/hospede/services/hospedeService";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

export default function NovoHospedePage() {
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
    cpf,
    setCpf,
  ] = useState("");

  const [
    telefone,
    setTelefone,
  ] = useState("");

  const [
    email,
    setEmail,
  ] = useState("");

  const [
    dataNascimento,
    setDataNascimento,
  ] = useState("");

  const [
    horarioPrevistoChegada,
    setHorarioPrevistoChegada,
  ] = useState("");

  const [
    viagemId,
    setViagemId,
  ] = useState<number | null>(
    null,
  );

  const [
    viagemNome,
    setViagemNome,
  ] = useState("");

  const [
    cadastrando,
    setCadastrando,
  ] = useState(false);

  const [
    erroCadastro,
    setErroCadastro,
  ] = useState("");

  const [
    sucessoCadastro,
    setSucessoCadastro,
  ] = useState("");

  useEffect(() => {
    async function carregarPagina() {
      try {
        const usuario =
          await buscarUsuarioAutenticado();

        const permitido =
          usuario.perfil === "ADMIN";

        setAcessoPermitido(
          permitido,
        );

        if (!permitido) {
          return;
        }

        const viagens =
          await listarViagens();

        const viagem =
          viagens.find(
            (item) =>
              item.status ===
              "PLANEJADA",
          );

        if (!viagem) {
          throw new Error(
            "Nenhuma viagem planejada foi encontrada.",
          );
        }

        setViagemId(
          viagem.id,
        );

        setViagemNome(
          viagem.nome,
        );
      } catch {
        setErroCadastro(
          "Não foi possível carregar os dados necessários para o cadastro.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, []);

  async function handleCadastrarHospede() {
    if (!viagemId) {
      setErroCadastro(
        "Nenhuma viagem ativa foi encontrada.",
      );

      return;
    }

    if (!nomeCompleto.trim()) {
      setErroCadastro(
        "Informe o nome completo.",
      );

      return;
    }

    if (cpf.length !== 11) {
      setErroCadastro(
        "O CPF deve conter 11 números.",
      );

      return;
    }

    try {
      setCadastrando(true);
      setErroCadastro("");
      setSucessoCadastro("");

      await cadastrarHospede({
        nomeCompleto:
          nomeCompleto.trim(),

        cpf,

        telefone:
          telefone.trim() ||
          undefined,

        email:
          email.trim() ||
          undefined,

        dataNascimento:
          dataNascimento ||
          undefined,

        horarioPrevistoChegada:
          horarioPrevistoChegada ||
          undefined,

        statusCheckIn:
          "PENDENTE",

        viagemId,
      });

      setNomeCompleto("");
      setCpf("");
      setTelefone("");
      setEmail("");
      setDataNascimento("");
      setHorarioPrevistoChegada("");

      setSucessoCadastro(
        "Hóspede cadastrado com sucesso.",
      );
    } catch (error) {
      setSucessoCadastro("");

      setErroCadastro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar o hóspede.",
      );
    } finally {
      setCadastrando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Verificando permissão...
      </p>
    );
  }

  if (!acessoPermitido) {
    return (
      <div className="space-y-4">
        <Link
          href="/hospedes"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para hóspedes
        </Link>

        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Você não possui permissão para cadastrar hóspedes.
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/hospedes"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para hóspedes
        </Link>

        <PageHeader
          title="Novo hóspede"
          description="Cadastre um novo hóspede na viagem."
        />
      </div>

      {erroCadastro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erroCadastro}
        </div>
      )}

      {sucessoCadastro && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {sucessoCadastro}
        </div>
      )}

      <div className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5">
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
              placeholder="Digite o nome completo"
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="cpf"
                className="mb-2 block text-xs font-medium text-muted"
              >
                CPF
              </label>

              <input
                id="cpf"
                type="text"
                value={cpf}
                onChange={(event) =>
                  setCpf(
                    event.target.value,
                  )
                }
                placeholder="Somente os 11 números"
                maxLength={11}
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
                placeholder="Digite o telefone"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="email"
              className="mb-2 block text-xs font-medium text-muted"
            >
              E-mail
            </label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) =>
                setEmail(
                  event.target.value,
                )
              }
              placeholder="Digite o e-mail"
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="data-nascimento"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Data de nascimento
              </label>

              <input
                id="data-nascimento"
                type="date"
                value={dataNascimento}
                onChange={(event) =>
                  setDataNascimento(
                    event.target.value,
                  )
                }
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
              />
            </div>

            <div>
              <label
                htmlFor="horario-previsto-chegada"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Chegada prevista
              </label>

              <input
                id="horario-previsto-chegada"
                type="datetime-local"
                value={
                  horarioPrevistoChegada
                }
                onChange={(event) =>
                  setHorarioPrevistoChegada(
                    event.target.value,
                  )
                }
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
              />
            </div>
          </div>

          <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
            <p className="text-[11px] text-muted">
              Viagem
            </p>

            <p className="mt-1 text-sm font-medium text-foreground">
              {viagemNome ||
                "Carregando viagem..."}
            </p>
          </div>
        </div>

        <div className="mt-5 flex justify-end border-t border-border pt-4">
          <button
            type="button"
            onClick={
              handleCadastrarHospede
            }
            disabled={cadastrando}
            className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Save
              size={17}
              strokeWidth={1.9}
            />

            {cadastrando
              ? "Cadastrando..."
              : "Cadastrar hóspede"}
          </button>
        </div>
      </div>
    </div>
  );
}