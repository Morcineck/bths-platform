"use client";

import Link from "next/link";

import {
  use,
  useEffect,
  useState,
} from "react";

import {
  ArrowLeft,
  CheckCircle2,
  Clock3,
  Save,
  UserX,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import {
  atualizarHospede,
  buscarHospedePorId,
} from "@/features/hospede/services/hospedeService";

import type {
  Hospede,
} from "@/features/hospede/types/hospede";

type EditarHospedePageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default function EditarHospedePage({
  params,
}: EditarHospedePageProps) {
  const { id } = use(
    params,
  );

  const [
    hospede,
    setHospede,
  ] = useState<Hospede | null>(
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
    erro,
    setErro,
  ] = useState("");

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
    salvando,
    setSalvando,
  ] = useState(false);

  const [
    erroEdicao,
    setErroEdicao,
  ] = useState("");

  const [
    sucessoEdicao,
    setSucessoEdicao,
  ] = useState("");

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

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

        const dados =
          await buscarHospedePorId(
            Number(id),
          );

        setHospede(
          dados,
        );

        setNomeCompleto(
          dados.nomeCompleto ?? "",
        );

        setCpf(
          dados.cpf ?? "",
        );

        setTelefone(
          dados.telefone ?? "",
        );

        setEmail(
          dados.email ?? "",
        );

        setDataNascimento(
          dados.dataNascimento ??
            "",
        );

        setHorarioPrevistoChegada(
          dados.horarioPrevistoChegada
            ? dados.horarioPrevistoChegada.slice(
                0,
                16,
              )
            : "",
        );
      } catch {
        setErro(
          "Não foi possível carregar os dados do hóspede.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, [id]);

  async function handleSalvarAlteracoes() {
    if (!hospede) {
      return;
    }

    if (!nomeCompleto.trim()) {
      setErroEdicao(
        "Informe o nome completo.",
      );

      return;
    }

    if (cpf.length !== 11) {
      setErroEdicao(
        "O CPF deve conter 11 números.",
      );

      return;
    }

    try {
      setSalvando(true);
      setErroEdicao("");
      setSucessoEdicao("");

      const hospedeAtualizado =
        await atualizarHospede(
          Number(id),
          {
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
              hospede.statusCheckIn,

            viagemId:
              hospede.viagemId,
          },
        );

      setHospede(
        hospedeAtualizado,
      );

      setNomeCompleto(
        hospedeAtualizado.nomeCompleto ??
          "",
      );

      setCpf(
        hospedeAtualizado.cpf ??
          "",
      );

      setTelefone(
        hospedeAtualizado.telefone ??
          "",
      );

      setEmail(
        hospedeAtualizado.email ??
          "",
      );

      setDataNascimento(
        hospedeAtualizado.dataNascimento ??
          "",
      );

      setHorarioPrevistoChegada(
        hospedeAtualizado.horarioPrevistoChegada
          ? hospedeAtualizado.horarioPrevistoChegada.slice(
              0,
              16,
            )
          : "",
      );

      setSucessoEdicao(
        "Hóspede atualizado com sucesso.",
      );
    } catch (error) {
      setSucessoEdicao("");

      setErroEdicao(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar o hóspede.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando hóspede...
      </p>
    );
  }

  if (!acessoPermitido) {
    return (
      <div className="space-y-4">
        <Link
          href={`/hospedes/${id}`}
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para o hóspede
        </Link>

        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Você não possui permissão para editar hóspedes.
        </div>
      </div>
    );
  }

  if (
    erro ||
    !hospede
  ) {
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
          {erro ||
            "Hóspede não encontrado."}
        </div>
      </div>
    );
  }

  const checkInRealizado =
    hospede.statusCheckIn ===
    "REALIZADO";

  const naoCompareceu =
    hospede.statusCheckIn ===
    "NAO_COMPARECEU";

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="space-y-4">
        <Link
          href={`/hospedes/${id}`}
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para o hóspede
        </Link>

        <PageHeader
          title="Editar hóspede"
          description={
            hospede.nomeCompleto
          }
        />
      </div>

      {erroEdicao && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erroEdicao}
        </div>
      )}

      {sucessoEdicao && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {sucessoEdicao}
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
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
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
                maxLength={11}
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
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
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

          <div className="grid gap-2 sm:grid-cols-2">
            <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
              <p className="text-[11px] text-muted">
                Viagem
              </p>

              <p className="mt-1 text-sm font-medium text-foreground">
                {
                  hospede.viagemNome
                }
              </p>
            </div>

            <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
              <p className="text-[11px] text-muted">
                Status do check-in
              </p>

              <div className="mt-1.5">
                <span
                  className={
                    checkInRealizado
                      ? "inline-flex items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                      : naoCompareceu
                        ? "inline-flex items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                        : "inline-flex items-center gap-1.5 rounded-full border border-warning/30 bg-warning/10 px-2.5 py-1 text-[11px] font-medium text-warning"
                  }
                >
                  {checkInRealizado ? (
                    <CheckCircle2
                      size={13}
                      strokeWidth={2}
                    />
                  ) : naoCompareceu ? (
                    <UserX
                      size={13}
                      strokeWidth={2}
                    />
                  ) : (
                    <Clock3
                      size={13}
                      strokeWidth={2}
                    />
                  )}

                  {checkInRealizado
                    ? "Realizado"
                    : naoCompareceu
                      ? "Não compareceu"
                      : "Pendente"}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="mt-5 flex justify-end border-t border-border pt-4">
          <button
            type="button"
            onClick={
              handleSalvarAlteracoes
            }
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
      </div>
    </div>
  );
}