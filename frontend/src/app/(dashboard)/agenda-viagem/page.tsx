"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  CalendarDays,
  Clock3,
  Edit3,
  Eye,
  EyeOff,
  Plus,
  Trash2,
} from "lucide-react";

import {
  deletarAgenda,
  listarAgendaPorViagem,
} from "@/features/agenda/services/agendaViagemService";

import type {
  AgendaViagem,
} from "@/features/agenda/types/agendaViagem";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

function formatarDataHora(
  dataHora: string,
) {
  const data = new Date(
    dataHora,
  );

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
  tipo: AgendaViagem["tipo"],
) {
  switch (tipo) {
    case "CHEGADA":
      return "Chegada";

    case "CHECK_IN":
      return "Check-in";

    case "WELCOME_BEAT":
      return "Welcome Beat";

    case "FESTIVAL":
      return "Festival";

    case "CHECK_OUT":
      return "Check-out";

    case "OUTRO":
    default:
      return "Outro";
  }
}

export default function AgendaViagemPage() {
  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    viagemId,
    setViagemId,
  ] = useState("");

  const [
    agenda,
    setAgenda,
  ] = useState<AgendaViagem[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    carregandoAgenda,
    setCarregandoAgenda,
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
        setCarregando(false);
      }
    }

    carregarViagens();
  }, []);

  useEffect(() => {
    if (!viagemId) {
      return;
    }

    async function carregarAgenda() {
      try {
        setCarregandoAgenda(
          true,
        );

        setErro(null);

        const dados =
          await listarAgendaPorViagem(
            Number(
              viagemId,
            ),
          );

        setAgenda(
          dados,
        );
      } catch {
        setErro(
          "Não foi possível carregar a agenda da viagem.",
        );
      } finally {
        setCarregandoAgenda(
          false,
        );
      }
    }

    carregarAgenda();
  }, [viagemId]);

  async function handleExcluir(
    item: AgendaViagem,
  ) {
    const confirmar =
      window.confirm(
        `Deseja realmente excluir "${item.titulo}"?`,
      );

    if (!confirmar) {
      return;
    }

    try {
      setExcluindoId(
        item.id,
      );

      setErro(null);
      setSucesso(null);

      await deletarAgenda(
        item.id,
      );

      setAgenda(
        (atual) =>
          atual.filter(
            (evento) =>
              evento.id !==
              item.id,
          ),
      );

      setSucesso(
        "Item da agenda removido com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível remover o item da agenda.",
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
        Carregando agenda...
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">
            Agenda da viagem
          </h1>

          <p className="mt-1 text-sm text-muted">
            Gerencie os eventos exibidos na timeline do hóspede.
          </p>
        </div>

        {viagemId && (
          <Link
            href={`/agenda-viagem/nova?viagemId=${viagemId}`}
            className="inline-flex h-11 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90"
          >
            <Plus
              size={17}
              strokeWidth={2}
            />

            Novo evento
          </Link>
        )}
      </div>

      {erro && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {erro}
        </div>
      )}

      {sucesso && (
        <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">
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

            setAgenda(
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

      {carregandoAgenda ? (
        <p className="text-sm text-muted">
          Carregando programação...
        </p>
      ) : agenda.length === 0 ? (
        <div className="rounded-3xl border border-border bg-surface/60 p-8 text-center">
          <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10 text-primary">
            <CalendarDays
              size={22}
              strokeWidth={1.8}
            />
          </span>

          <h2 className="mt-4 text-base font-semibold text-foreground">
            Nenhum evento cadastrado
          </h2>

          <p className="mt-2 text-sm text-muted">
            Cadastre os eventos que farão parte da timeline desta viagem.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {agenda.map(
            (item) => (
              <article
                key={item.id}
                className="rounded-2xl border border-border bg-surface p-5 shadow-soft"
              >
                <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2">
                      <span className="inline-flex rounded-full border border-primary/30 bg-primary/10 px-2.5 py-1 text-xs font-medium text-primary">
                        {formatarTipo(
                          item.tipo,
                        )}
                      </span>

                      <span
                        className={
                          item.ativo
                            ? "inline-flex rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-xs font-medium text-success"
                            : "inline-flex rounded-full border border-border bg-background px-2.5 py-1 text-xs font-medium text-muted"
                        }
                      >
                        {item.ativo
                          ? "Ativo"
                          : "Inativo"}
                      </span>

                      <span className="inline-flex items-center gap-1 rounded-full border border-border bg-background px-2.5 py-1 text-xs font-medium text-muted">
                        {item.visivelHospede ? (
                          <Eye
                            size={12}
                          />
                        ) : (
                          <EyeOff
                            size={12}
                          />
                        )}

                        {item.visivelHospede
                          ? "Visível ao hóspede"
                          : "Oculto"}
                      </span>
                    </div>

                    <h2 className="mt-3 text-base font-semibold text-foreground">
                      {item.titulo}
                    </h2>

                    <div className="mt-2 flex items-center gap-2 text-sm text-muted">
                      <Clock3
                        size={15}
                        strokeWidth={1.8}
                      />

                      {formatarDataHora(
                        item.dataHoraInicio,
                      )}
                    </div>

                    {item.descricao && (
                      <p className="mt-3 text-sm leading-6 text-muted">
                        {item.descricao}
                      </p>
                    )}

                    <p className="mt-3 text-xs text-muted">
                      Ordem:{" "}
                      <span className="font-medium text-foreground">
                        {item.ordem}
                      </span>
                    </p>
                  </div>

                  <div className="flex shrink-0 gap-2">
                    <Link
                      href={`/agenda-viagem/${item.id}/editar`}
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
                          item,
                        )
                      }
                      disabled={
                        excluindoId ===
                        item.id
                      }
                      className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-red-500/30 px-3 text-sm font-medium text-red-400 transition-colors hover:bg-red-500/10 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      <Trash2
                        size={15}
                      />

                      {excluindoId ===
                      item.id
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