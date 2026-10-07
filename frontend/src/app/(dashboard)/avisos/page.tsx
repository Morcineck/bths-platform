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

import { PageHeader } from "@/components/layout/PageHeader";

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
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Avisos"
          description="Gerencie os comunicados enviados aos hóspedes."
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

                setAvisos(
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
              href={`/avisos/novo?viagemId=${viagemId}`}
              className="gradient-brand inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
            >
              <Plus
                size={18}
                strokeWidth={1.9}
              />

              Novo aviso
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

      {carregandoAvisos ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-4">
          <p className="text-sm text-muted">
            Carregando comunicados...
          </p>
        </div>
      ) : avisos.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <Bell
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhum aviso cadastrado
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Cadastre comunicados importantes para os hóspedes desta viagem.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Comunicados
              </h2>

              <p className="mt-1 text-xs text-muted">
                Avisos publicados para os hóspedes da viagem.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {avisos.length}{" "}
              {avisos.length === 1
                ? "aviso"
                : "avisos"}
            </span>
          </div>

          <div className="space-y-3">
            {avisos.map(
              (aviso) => (
                <article
                  key={aviso.id}
                  className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
                >
                  <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-1.5">
                        <span className="inline-flex rounded-full border border-primary/30 bg-primary/10 px-2 py-0.5 text-[10px] font-medium text-primary">
                          {formatarTipo(
                            aviso.tipo,
                          )}
                        </span>

                        <span
                          className={
                            aviso.ativo
                              ? "inline-flex rounded-full border border-success/30 bg-success/10 px-2 py-0.5 text-[10px] font-medium text-success"
                              : "inline-flex rounded-full border border-border bg-background/30 px-2 py-0.5 text-[10px] font-medium text-muted"
                          }
                        >
                          {aviso.ativo
                            ? "Publicado"
                            : "Inativo"}
                        </span>
                      </div>

                      <h3 className="mt-2 text-base font-semibold text-foreground">
                        {aviso.titulo}
                      </h3>

                      <div className="mt-1.5 flex items-center gap-2 text-xs text-muted">
                        <CalendarDays
                          size={14}
                          strokeWidth={1.9}
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
                            aviso,
                          )
                        }
                        disabled={
                          excluindoId ===
                          aviso.id
                        }
                        className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        <Trash2
                          size={14}
                          strokeWidth={1.9}
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
        </section>
      )}
    </div>
  );
}