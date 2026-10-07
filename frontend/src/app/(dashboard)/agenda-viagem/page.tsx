"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";

import {
  CalendarDays,
  Clock3,
  Edit3,
  Eye,
  EyeOff,
  Plus,
  Trash2,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

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

function obterPartesDataHora(
  dataHora: string,
) {
  const data = new Date(dataHora);

  const dia = new Intl.DateTimeFormat(
    "pt-BR",
    {
      day: "2-digit",
    },
  ).format(data);

  const mes = new Intl.DateTimeFormat(
    "pt-BR",
    {
      month: "short",
    },
  )
    .format(data)
    .replace(".", "")
    .toUpperCase();

  const hora = new Intl.DateTimeFormat(
    "pt-BR",
    {
      hour: "2-digit",
      minute: "2-digit",
    },
  ).format(data);

  const ano = new Intl.DateTimeFormat(
    "pt-BR",
    {
      year: "numeric",
    },
  ).format(data);

  return {
    dia,
    mes,
    hora,
    ano,
  };
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

  const agendaOrdenada =
    useMemo(
      () =>
        [...agenda].sort(
          (a, b) =>
            new Date(
              a.dataHoraInicio,
            ).getTime() -
            new Date(
              b.dataHoraInicio,
            ).getTime() ||
            a.ordem -
              b.ordem,
        ),
      [agenda],
    );

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
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Agenda da viagem"
          description="Gerencie os eventos exibidos na timeline do hóspede."
        />

        <div className="flex w-full flex-col gap-3 sm:flex-row sm:items-end lg:w-auto">
          <div className="w-full sm:w-[260px]">
            <label
              htmlFor="viagem"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Viagem ativa
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
              className="h-11 w-full rounded-xl border border-border bg-surface/70 px-4 text-sm font-medium text-foreground outline-none transition-colors focus:border-primary"
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

          {viagemId && (
            <Link
              href={`/agenda-viagem/nova?viagemId=${viagemId}`}
              className="gradient-brand inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
            >
              <Plus
                size={18}
                strokeWidth={1.9}
              />

              Novo evento
            </Link>
          )}
        </div>
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

      {carregandoAgenda ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-4">
          <p className="text-sm text-muted">
            Carregando programação...
          </p>
        </div>
      ) : agendaOrdenada.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <CalendarDays
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhum evento cadastrado
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Cadastre os eventos que farão parte da timeline desta viagem.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Programação
              </h2>

              <p className="mt-1 text-xs text-muted">
                Eventos exibidos na timeline da viagem.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {agendaOrdenada.length}{" "}
              {agendaOrdenada.length === 1
                ? "evento"
                : "eventos"}
            </span>
          </div>

          <div className="relative">
            <div className="absolute bottom-0 left-[24px] top-0 hidden w-px bg-border sm:block" />

            <div className="space-y-3">
              {agendaOrdenada.map(
                (item) => {
                  const {
                    dia,
                    mes,
                    hora,
                    ano,
                  } =
                    obterPartesDataHora(
                      item.dataHoraInicio,
                    );

                  return (
                    <article
                      key={item.id}
                      className="relative sm:pl-16"
                    >
                      <span className="absolute left-[18px] top-5 z-10 hidden h-3.5 w-3.5 rounded-full border-2 border-primary bg-background sm:block" />

                      <div className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft">
                        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                          <div className="flex min-w-0 gap-3">
                            <div className="flex h-14 w-14 shrink-0 flex-col items-center justify-center rounded-xl border border-primary/20 bg-primary/10 text-center">
                              <span className="text-lg font-semibold leading-none text-foreground">
                                {dia}
                              </span>

                              <span className="mt-1 text-[10px] font-medium uppercase tracking-wide text-primary">
                                {mes}
                              </span>
                            </div>

                            <div className="min-w-0">
                              <div className="flex flex-wrap items-center gap-1.5">
                                <span className="inline-flex rounded-full border border-primary/30 bg-primary/10 px-2 py-0.5 text-[10px] font-medium text-primary">
                                  {formatarTipo(
                                    item.tipo,
                                  )}
                                </span>

                                <span
                                  className={
                                    item.ativo
                                      ? "inline-flex rounded-full border border-success/30 bg-success/10 px-2 py-0.5 text-[10px] font-medium text-success"
                                      : "inline-flex rounded-full border border-border bg-background/30 px-2 py-0.5 text-[10px] font-medium text-muted"
                                  }
                                >
                                  {item.ativo
                                    ? "Ativo"
                                    : "Inativo"}
                                </span>

                                <span className="inline-flex items-center gap-1 rounded-full border border-border bg-background/30 px-2 py-0.5 text-[10px] font-medium text-muted">
                                  {item.visivelHospede ? (
                                    <Eye
                                      size={11}
                                      strokeWidth={1.9}
                                    />
                                  ) : (
                                    <EyeOff
                                      size={11}
                                      strokeWidth={1.9}
                                    />
                                  )}

                                  {item.visivelHospede
                                    ? "Visível"
                                    : "Oculto"}
                                </span>
                              </div>

                              <h3 className="mt-2 truncate text-base font-semibold text-foreground">
                                {item.titulo}
                              </h3>

                              <div className="mt-1.5 flex items-center gap-2 text-xs text-muted">
                                <Clock3
                                  size={14}
                                  strokeWidth={1.9}
                                />

                                <span>
                                  {hora} · {ano}
                                </span>
                              </div>
                            </div>
                          </div>

                          <div className="flex shrink-0 gap-2">
                            <Link
                              href={`/agenda-viagem/${item.id}/editar`}
                              className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
                            >
                              <Edit3
                                size={14}
                                strokeWidth={1.9}
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
                              className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              <Trash2
                                size={14}
                                strokeWidth={1.9}
                              />

                              {excluindoId ===
                              item.id
                                ? "Excluindo..."
                                : "Excluir"}
                            </button>
                          </div>
                        </div>

                        {item.descricao && (
                          <p className="mt-3 border-t border-border pt-3 text-xs leading-5 text-muted">
                            {item.descricao}
                          </p>
                        )}

                        <div className="mt-3 flex items-center justify-between border-t border-border pt-3">
                          <p className="text-[11px] text-muted">
                            Ordem
                          </p>

                          <span className="rounded-lg bg-background/30 px-2 py-1 text-[11px] font-medium text-foreground">
                            {item.ordem}
                          </span>
                        </div>
                      </div>
                    </article>
                  );
                },
              )}
            </div>
          </div>
        </section>
      )}
    </div>
  );
}