"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";
import {
  atualizarHospede,
  buscarHospedePorId,
} from "@/features/hospede/services/hospedeService";
import type { Hospede } from "@/features/hospede/types/hospede";

type EditarHospedePageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default function EditarHospedePage({
  params,
}: EditarHospedePageProps) {
  const { id } = use(params);

  const [hospede, setHospede] =
    useState<Hospede | null>(null);

  const [carregando, setCarregando] =
    useState(true);

  const [acessoPermitido, setAcessoPermitido] =
    useState(false);

  const [erro, setErro] =
    useState("");

  const [nomeCompleto, setNomeCompleto] =
    useState("");

  const [cpf, setCpf] =
    useState("");

  const [telefone, setTelefone] =
    useState("");

  const [email, setEmail] =
    useState("");

  const [
    dataNascimento,
    setDataNascimento,
  ] = useState("");

  const [
    horarioPrevistoChegada,
    setHorarioPrevistoChegada,
  ] = useState("");

  const [salvando, setSalvando] =
    useState(false);

  const [erroEdicao, setErroEdicao] =
    useState("");

  const [sucessoEdicao, setSucessoEdicao] =
    useState("");

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        const permitido =
          usuario.perfil === "ADMIN";

        setAcessoPermitido(permitido);

        if (!permitido) {
          return;
        }

        const dados =
          await buscarHospedePorId(
            Number(id),
          );

        setHospede(dados);

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
          dados.dataNascimento ?? "",
        );

        setHorarioPrevistoChegada(
          dados.horarioPrevistoChegada
            ? dados.horarioPrevistoChegada.slice(0, 16)
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
              telefone.trim() || undefined,
            email:
              email.trim() || undefined,
            dataNascimento:
              dataNascimento || undefined,
            horarioPrevistoChegada:
              horarioPrevistoChegada ||
              undefined,
            statusCheckIn:
              hospede.statusCheckIn,
            viagemId:
              hospede.viagemId,
          },
        );

      setHospede(hospedeAtualizado);

      setNomeCompleto(
        hospedeAtualizado.nomeCompleto ?? "",
      );

      setCpf(
        hospedeAtualizado.cpf ?? "",
      );

      setTelefone(
        hospedeAtualizado.telefone ?? "",
      );

      setEmail(
        hospedeAtualizado.email ?? "",
      );

      setDataNascimento(
        hospedeAtualizado.dataNascimento ?? "",
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
        <p className="text-sm text-red-400">
          Você não possui permissão para editar hóspedes.
        </p>

        <Link
          href={`/hospedes/${id}`}
          className="text-sm font-medium text-primary"
        >
          ← Voltar para o hóspede
        </Link>
      </div>
    );
  }

  if (erro || !hospede) {
    return (
      <div className="space-y-4">
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro || "Hóspede não encontrado."}
        </p>

        <Link
          href="/hospedes"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para hóspedes
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <header>
        <Link
          href={`/hospedes/${id}`}
          className="text-sm font-medium text-primary"
        >
          ← Voltar para o hóspede
        </Link>

        <h1 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
          Editar hóspede
        </h1>

        <p className="mt-2 text-sm text-muted">
          {hospede.nomeCompleto}
        </p>
      </header>

      <div className="space-y-4">
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
            htmlFor="cpf"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            CPF
          </label>

          <input
            id="cpf"
            type="text"
            value={cpf}
            onChange={(event) =>
              setCpf(event.target.value)
            }
            maxLength={11}
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
            htmlFor="email"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            E-mail
          </label>

          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) =>
              setEmail(event.target.value)
            }
            className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        <div>
          <label
            htmlFor="data-nascimento"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Data de nascimento
          </label>

          <input
            id="data-nascimento"
            type="date"
            value={dataNascimento}
            onChange={(event) =>
              setDataNascimento(event.target.value)
            }
            className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        <div>
          <label
            htmlFor="horario-previsto-chegada"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Horário previsto de chegada
          </label>

          <input
            id="horario-previsto-chegada"
            type="datetime-local"
            value={horarioPrevistoChegada}
            onChange={(event) =>
              setHorarioPrevistoChegada(
                event.target.value,
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        <div className="rounded-xl border border-border bg-surface p-4">
          <p className="text-sm text-muted">
            Viagem
          </p>

          <p className="mt-1 font-medium text-foreground">
            {hospede.viagemNome}
          </p>
        </div>

        <div className="rounded-xl border border-border bg-surface p-4">
          <p className="text-sm text-muted">
            Status do check-in
          </p>

          <p className="mt-1 font-medium text-foreground">
            {hospede.statusCheckIn === "REALIZADO"
              ? "Realizado"
              : hospede.statusCheckIn ===
                  "NAO_COMPARECEU"
                ? "Não compareceu"
                : "Pendente"}
          </p>
        </div>

        {erroEdicao && (
          <p
            role="alert"
            className="text-sm text-red-400"
          >
            {erroEdicao}
          </p>
        )}

        {sucessoEdicao && (
          <p className="text-sm text-green-400">
            {sucessoEdicao}
          </p>
        )}

        <button
          type="button"
          onClick={handleSalvarAlteracoes}
          disabled={salvando}
          className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
        >
          {salvando
            ? "Salvando..."
            : "Salvar alterações"}
        </button>
      </div>
    </div>
  );
}