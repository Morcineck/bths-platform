"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

import {
  BedDouble,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Pencil,
  Plus,
  Users,
  XCircle,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  listarAlocacoesPorQuarto,
} from "@/features/alocacao/services/alocacaoService";

import type {
  AlocacaoQuarto,
} from "@/features/alocacao/types/alocacao";

import {
  listarOcupacaoQuartosPorViagem,
} from "@/features/quarto/services/quartoService";

import type {
  QuartoOcupacao,
} from "@/features/quarto/types/quarto";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

export default function QuartosPage() {
  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    viagemSelecionadaId,
    setViagemSelecionadaId,
  ] = useState<number | null>(null);

  const [
    quartos,
    setQuartos,
  ] = useState<QuartoOcupacao[]>([]);

  const [
    alocacoesPorQuarto,
    setAlocacoesPorQuarto,
  ] = useState<
    Record<number, AlocacaoQuarto[]>
  >({});

  const [
    carregandoViagens,
    setCarregandoViagens,
  ] = useState(true);

  const [
    carregandoQuartos,
    setCarregandoQuartos,
  ] = useState(false);

  const [
    quartoExpandidoId,
    setQuartoExpandidoId,
  ] = useState<number | null>(null);

  const [
    carregandoAlocacoesId,
    setCarregandoAlocacoesId,
  ] = useState<number | null>(null);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function carregarViagens() {
      try {
        setErro("");

        const dados =
          await listarViagens();

        setViagens(dados);

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
          setViagemSelecionadaId(
            viagemPreferencial.id,
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar as viagens.",
        );
      } finally {
        setCarregandoViagens(false);
      }
    }

    carregarViagens();
  }, []);

  useEffect(() => {
    if (viagemSelecionadaId === null) {
      return;
    }

    async function carregarQuartos() {
      try {
        setCarregandoQuartos(true);
        setErro("");

        const dados =
          await listarOcupacaoQuartosPorViagem(
            viagemSelecionadaId!,
          );

        setQuartos(dados);
      } catch (error) {
        setQuartos([]);

        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os quartos.",
        );
      } finally {
        setCarregandoQuartos(false);
      }
    }

    carregarQuartos();
  }, [viagemSelecionadaId]);

  async function handleVerHospedes(
    quartoId: number,
  ) {
    if (
      quartoExpandidoId === quartoId
    ) {
      setQuartoExpandidoId(null);

      return;
    }

    if (
      alocacoesPorQuarto[
        quartoId
      ]
    ) {
      setQuartoExpandidoId(
        quartoId,
      );

      return;
    }

    try {
      setCarregandoAlocacoesId(
        quartoId,
      );

      setErro("");

      const alocacoes =
        await listarAlocacoesPorQuarto(
          quartoId,
        );

      setAlocacoesPorQuarto(
        (atuais) => ({
          ...atuais,
          [quartoId]:
            alocacoes,
        }),
      );

      setQuartoExpandidoId(
        quartoId,
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível carregar os hóspedes do quarto.",
      );
    } finally {
      setCarregandoAlocacoesId(
        null,
      );
    }
  }

  if (carregandoViagens) {
    return (
      <p className="text-sm text-muted">
        Carregando quartos...
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Quartos"
          description="Gestão da capacidade, ocupação e disponibilidade dos quartos."
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
              value={
                viagemSelecionadaId ?? ""
              }
              onChange={(event) =>
                setViagemSelecionadaId(
                  Number(
                    event.target.value,
                  ),
                )
              }
              disabled={
                viagens.length === 0
              }
              className="h-11 w-full rounded-xl border border-border bg-surface/70 px-4 text-sm font-medium text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            >
              {viagens.length === 0 ? (
                <option value="">
                  Nenhuma viagem disponível
                </option>
              ) : (
                viagens.map(
                  (viagem) => (
                    <option
                      key={viagem.id}
                      value={viagem.id}
                    >
                      {viagem.nome}
                    </option>
                  ),
                )
              )}
            </select>
          </div>

          {viagemSelecionadaId && (
            <Link
              href={`/quartos/novo?viagemId=${viagemSelecionadaId}`}
              className="gradient-brand inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
            >
              <Plus
                size={18}
                strokeWidth={1.9}
              />

              Novo quarto
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

      {carregandoQuartos ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-4">
          <p className="text-sm text-muted">
            Carregando quartos da viagem...
          </p>
        </div>
      ) : quartos.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <BedDouble
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhum quarto encontrado
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Ainda não existem quartos cadastrados para esta viagem.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Ocupação dos quartos
              </h2>

              <p className="mt-1 text-xs text-muted">
                Capacidade e disponibilidade da viagem selecionada.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {quartos.length}{" "}
              {quartos.length === 1
                ? "quarto"
                : "quartos"}
            </span>
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            {quartos.map(
              (quarto) => {
                const lotado =
                  quarto.capacidade > 0 &&
                  quarto.ocupacao >=
                    quarto.capacidade;

                const indisponivel =
                  quarto.status ===
                  "INDISPONIVEL";

                const ocupacaoPercentual =
                  quarto.capacidade > 0
                    ? Math.min(
                        (quarto.ocupacao /
                          quarto.capacidade) *
                          100,
                        100,
                      )
                    : 0;

                const alocacoes =
                  alocacoesPorQuarto[
                    quarto.quartoId
                  ] ?? [];

                const expandido =
                  quartoExpandidoId ===
                  quarto.quartoId;

                const carregandoAlocacoes =
                  carregandoAlocacoesId ===
                  quarto.quartoId;

                return (
                  <article
                    key={quarto.quartoId}
                    className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div className="min-w-0">
                        <p className="text-[11px] font-medium uppercase tracking-[0.12em] text-muted">
                          {quarto.tipo ===
                          "SUITE"
                            ? "Suíte"
                            : "Alojamento"}
                        </p>

                        <h3 className="mt-1 truncate text-base font-semibold text-foreground">
                          {quarto.nome}
                        </h3>
                      </div>

                      <span
                        className={
                          indisponivel
                            ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                            : lotado
                              ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-warning/30 bg-warning/10 px-2.5 py-1 text-[11px] font-medium text-warning"
                              : "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                        }
                      >
                        {indisponivel ? (
                          <XCircle
                            size={13}
                            strokeWidth={2}
                          />
                        ) : (
                          <CheckCircle2
                            size={13}
                            strokeWidth={2}
                          />
                        )}

                        {indisponivel
                          ? "Indisponível"
                          : lotado
                            ? "Lotado"
                            : "Disponível"}
                      </span>
                    </div>

                    <div className="mt-4 rounded-xl border border-border bg-background/25 p-3">
                      <div className="flex items-end justify-between gap-3">
                        <div>
                          <p className="text-[11px] text-muted">
                            Ocupação
                          </p>

                          <p className="mt-1 text-lg font-semibold text-foreground">
                            {quarto.ocupacao} /{" "}
                            {quarto.capacidade}
                          </p>
                        </div>

                        <div className="text-right">
                          <p
                            className={
                              indisponivel
                                ? "text-xs font-medium text-danger"
                                : lotado
                                  ? "text-xs font-medium text-warning"
                                  : "text-xs font-medium text-foreground"
                            }
                          >
                            {indisponivel
                              ? "Fora de operação"
                              : `${quarto.vagasDisponiveis} ${
                                  quarto.vagasDisponiveis ===
                                  1
                                    ? "vaga"
                                    : "vagas"
                                }`}
                          </p>

                          {!indisponivel && (
                            <p className="mt-1 text-[11px] text-muted">
                              disponíveis
                            </p>
                          )}
                        </div>
                      </div>

                      {quarto.capacidade > 0 && (
                        <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-border">
                          <div
                            className={
                              indisponivel
                                ? "h-full rounded-full bg-danger transition-all"
                                : ocupacaoPercentual >=
                                    100
                                  ? "h-full rounded-full bg-warning transition-all"
                                  : "h-full rounded-full bg-primary transition-all"
                            }
                            style={{
                              width: `${ocupacaoPercentual}%`,
                            }}
                          />
                        </div>
                      )}
                    </div>

                    <div className="mt-3">
                      {quarto.ocupacao > 0 ? (
                        <button
                          type="button"
                          onClick={() =>
                            handleVerHospedes(
                              quarto.quartoId,
                            )
                          }
                          disabled={
                            carregandoAlocacoes
                          }
                          className="inline-flex h-10 w-full items-center justify-center gap-2 rounded-xl border border-border bg-background/20 px-3 text-xs font-medium text-foreground transition-colors hover:border-primary/30 hover:bg-primary/5 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          <Users
                            size={15}
                            strokeWidth={1.9}
                          />

                          {carregandoAlocacoes
                            ? "Carregando..."
                            : expandido
                              ? "Ocultar hóspedes"
                              : `${quarto.ocupacao} ${
                                  quarto.ocupacao ===
                                  1
                                    ? "hóspede"
                                    : "hóspedes"
                                }`}

                          {expandido ? (
                            <ChevronUp
                              size={14}
                              strokeWidth={1.9}
                            />
                          ) : (
                            <ChevronDown
                              size={14}
                              strokeWidth={1.9}
                            />
                          )}
                        </button>
                      ) : (
                        <div className="flex h-10 items-center justify-center rounded-xl border border-border bg-background/20 px-3 text-xs text-muted">
                          Nenhum hóspede alocado
                        </div>
                      )}

                      {expandido && (
                        <div className="mt-2.5 space-y-1.5 rounded-xl border border-border bg-background/20 p-2.5">
                          {alocacoes.length ===
                          0 ? (
                            <p className="text-xs text-muted">
                              Nenhum hóspede alocado neste quarto.
                            </p>
                          ) : (
                            alocacoes.map(
                              (alocacao) => (
                                <div
                                  key={alocacao.id}
                                  className="rounded-lg border border-border bg-surface/40 px-3 py-2"
                                >
                                  <Link
                                    href={`/hospedes/${alocacao.hospedeId}`}
                                    className="text-sm font-medium text-foreground transition-colors hover:text-primary"
                                  >
                                    {
                                      alocacao.hospedeNome
                                    }
                                  </Link>

                                  <p className="mt-1 text-[11px] text-muted">
                                    Hóspede #
                                    {
                                      alocacao.hospedeId
                                    }
                                  </p>
                                </div>
                              ),
                            )
                          )}
                        </div>
                      )}
                    </div>

                    <div className="mt-3 border-t border-border pt-3">
                      <Link
                        href={`/quartos/${quarto.quartoId}/editar`}
                        className="inline-flex h-9 w-full items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
                      >
                        <Pencil
                          size={14}
                          strokeWidth={1.9}
                        />

                        Editar quarto
                      </Link>
                    </div>
                  </article>
                );
              },
            )}
          </div>
        </section>
      )}
    </div>
  );
}