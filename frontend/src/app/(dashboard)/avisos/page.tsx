"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  Bell,
  CalendarDays,
  Edit3,
  Plus,
  Trash2,
} from "lucide-react";

import {
  deletarAviso,
  listarAvisosPorViagem,
} from "@/features/aviso/services/avisoService";

import type {
  Aviso,
} from "@/features/aviso/types/aviso";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

function formatarDataHora(
  dataHora: string,
) {
  const data =
    new Date(dataHora);

  return new Intl.DateTimeFormat(
    "pt-BR",
    {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    },
  ).format(data);
}

function formatarTipo(
  tipo: Aviso["tipo"],
) {
  switch (tipo) {
    case "INFORMATIVO":
      return "Informativo";

    case "IMPORTANTE":
      return "Importante";

    case "TRANSPORTE":
      return "Transporte";

    case "HOSPEDAGEM":
      return "Hospedagem";

    case "EVENTO":
      return "Evento";

    default:
      return tipo;
  }
}

export default function AvisosPage() {
  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    viagemId,
    setViagemId,
  ] = useState("");

  const [
    avisos,
    setAvisos,
  ] = useState<Aviso[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    carregandoAvisos,
    setCarregandoAvisos,
  ] = useState(false);

  const [
    excluindoId,
    setExcluindoId,
  ] = useState<number | null>(
    null,
  );

  const [
    erro,
    setErro,
  ] = useState<string | null>(
    null,
  );

  const [
    sucesso,
    setSucesso,
  ] = useState<string | null>(
    null,
  );

  useEffect(() => {
    async function carregarViagens() {
      try {
        setErro(null);

        const dados =
          await listarViagens();

        setViagens(
          dados,
        );

        const viagemPreferencial =
          dados.find(
            (viagem) =>
              viagem.status ===
              "EM_ANDAMENTO",
          ) ??
          dados.find(
            (viagem) =>
              viagem.status ===
              "PLANEJADA",
          ) ??
          dados[0];

        if (viagemPreferencial) {
          setViagemId(
            String(
              viagemPreferencial.id,
            ),
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar as viagens.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarViagens();
  }, []);

  useEffect(() => {
    if (!viagemId) {
      return;
    }

    async function carregarAvisos() {
      try {
        setCarregandoAvisos(
          true,
        );

        setErro(null);

        const dados =
          await listarAvisosPorViagem(
            Number(
              viagemId,
            ),
          );

        setAvisos(
          dados,
        );
      } catch {
        setErro(
          "Não foi possível carregar os avisos da viagem.",
        );
      } finally {
        setCarregandoAvisos(
          false,
        );
      }
    }

    carregarAvisos();
  }, [viagemId]);

  async function handleExcluir(
    aviso: Aviso,
  ) {
    const confirmar =
      window.confirm(
        `Deseja realmente excluir "${aviso.titulo}"?`,
      );

    if (!confirmar) {
      return;
    }

    try {
      setExcluindoId(
        aviso.id,
      );

      setErro(null);
      setSucesso(null);

      await deletarAviso(
        aviso.id,
      );

      setAvisos(
        (atual) =>
          atual.filter(
            (item) =>
              item.id !==
              aviso.id,
          ),
      );

      setSucesso(
        "Aviso removido com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível remover o aviso.",
      );
    } finally {
      setExcluindoId(
        null,
      );
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando avisos...
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">
            Avisos
          </h1>

          <p className="mt-1 text-sm text-muted">
            Gerencie os comunicados enviados aos hóspedes.
          </p>
        </div>

        {viagemId && (
          <Link
            href={`/avisos/novo?viagemId=${viagemId}`}
            className="inline-flex h-11 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90"
          >
            <Plus
              size={17}
              strokeWidth={2}
            />

            Novo aviso
          </Link>
        )}
      </div>

      {erro && (
        <div className="rounded-xl border border-danger/30 bg-danger/10 px-4 py-3 text-sm text-danger">
          {erro}
        </div>
      )}

      {sucesso && (
        <div className="rounded-xl border border-success/30 bg-success/10 px-4 py-3 text-sm text-success">
          {sucesso}
        </div>
      )}

      <div className="rounded-2xl border border-border bg-surface p-4">
        <label
          htmlFor="viagem"
          className="text-sm font-medium text-foreground"
        >
          Viagem
        </label>

        <select
          id="viagem"
          value={viagemId}
          onChange={(event) => {
            setViagemId(
              event.target.value,
            );

            setAvisos(
              [],
            );

            setSucesso(
              null,
            );
          }}
          className="mt-2 h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
        >
          <option value="">
            Selecione uma viagem
          </option>

          {viagens.map(
            (viagem) => (
              <option
                key={viagem.id}
                value={viagem.id}
              >
                {viagem.nome}
              </option>
            ),
          )}
        </select>
      </div>

      {carregandoAvisos ? (
        <p className="text-sm text-muted">
          Carregando comunicados...
        </p>
      ) : avisos.length === 0 ? (
        <div className="rounded-3xl border border-border bg-surface/60 p-8 text-center">
          <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10 text-primary">
            <Bell
              size={22}
              strokeWidth={1.8}
            />
          </span>

          <h2 className="mt-4 text-base font-semibold text-foreground">
            Nenhum aviso cadastrado
          </h2>

          <p className="mt-2 text-sm text-muted">
            Cadastre comunicados importantes para os hóspedes desta viagem.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {avisos.map(
            (aviso) => (
              <article
                key={aviso.id}
                className="rounded-2xl border border-border bg-surface p-5 shadow-soft"
              >
                <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2">
                      <span className="inline-flex rounded-full border border-primary/30 bg-primary/10 px-2.5 py-1 text-xs font-medium text-primary">
                        {formatarTipo(
                          aviso.tipo,
                        )}
                      </span>

                      <span
                        className={
                          aviso.ativo
                            ? "inline-flex rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-xs font-medium text-success"
                            : "inline-flex rounded-full border border-border bg-background px-2.5 py-1 text-xs font-medium text-muted"
                        }
                      >
                        {aviso.ativo
                          ? "Publicado"
                          : "Inativo"}
                      </span>
                    </div>

                    <h2 className="mt-3 text-base font-semibold text-foreground">
                      {aviso.titulo}
                    </h2>

                    <div className="mt-2 flex items-center gap-2 text-sm text-muted">
                      <CalendarDays
                        size={15}
                        strokeWidth={1.8}
                      />

                      {formatarDataHora(
                        aviso.dataPublicacao,
                      )}
                    </div>

                    <p className="mt-3 text-sm leading-6 text-muted">
                      {aviso.mensagem}
                    </p>
                  </div>

                  <div className="flex shrink-0 gap-2">
                    <Link
                      href={`/avisos/${aviso.id}/editar`}
                      className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border px-3 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
                    >
                      <Edit3
                        size={15}
                      />

                      Editar
                    </Link>

                    <button
                      type="button"
                      onClick={() =>
                        handleExcluir(
                          aviso,
                        )
                      }
                      disabled={
                        excluindoId ===
                        aviso.id
                      }
                      className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-sm font-medium text-danger transition-colors hover:bg-danger/10 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      <Trash2
                        size={15}
                      />

                      {excluindoId ===
                      aviso.id
                        ? "Excluindo..."
                        : "Excluir"}
                    </button>
                  </div>
                </div>
              </article>
            ),
          )}
        </div>
      )}
    </div>
  );
}