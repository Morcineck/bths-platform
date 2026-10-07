"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  AlertTriangle,
  BedDouble,
  Bus,
  CheckCircle2,
  Clock3,
  Users,
} from "lucide-react";

import { QuickActions } from "@/features/dashboard/components/QuickActions";
import { PageHeader } from "@/components/layout/PageHeader";
import { buscarDashboard } from "@/features/dashboard/services/dashboardService";
import type { DashboardResponse } from "@/features/dashboard/types/dashboard";
import { listarViagens } from "@/features/viagem/services/viagemService";
import type { Viagem } from "@/features/viagem/types/viagem";

export default function DashboardPage() {
  const [dashboard, setDashboard] =
    useState<DashboardResponse | null>(null);

  const [viagens, setViagens] =
    useState<Viagem[]>([]);

  const [viagemAtivaId, setViagemAtivaId] =
    useState<number | null>(null);

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] =
    useState("");

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
          setViagemAtivaId(
            viagemPreferencial.id,
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar as viagens.",
        );

        setCarregando(false);
      }
    }

    carregarViagens();
  }, []);

  useEffect(() => {
    if (viagemAtivaId === null) {
      return;
    }

    async function carregarDashboard() {
      try {
        setCarregando(true);
        setErro("");

        const dados =
          await buscarDashboard(
            viagemAtivaId!,
          );

        setDashboard(dados);
      } catch {
        setErro(
          "Não foi possível carregar os dados do dashboard.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarDashboard();
  }, [viagemAtivaId]);

  if (carregando && !dashboard) {
    return (
      <p className="text-sm text-muted">
        Carregando dashboard...
      </p>
    );
  }

  if (erro && !dashboard) {
    return (
      <p
        role="alert"
        className="text-sm text-red-400"
      >
        {erro}
      </p>
    );
  }

  if (!dashboard) {
    return null;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <PageHeader
          title="Dashboard"
          description="Visão geral da operação da Beat Trips."
        />

        <div className="w-full md:w-80">
          <label
            htmlFor="viagem"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Viagem ativa
          </label>

          <select
            id="viagem"
            value={
              viagemAtivaId ?? ""
            }
            onChange={(event) =>
              setViagemAtivaId(
                Number(
                  event.target.value,
                ),
              )
            }
            className="h-11 w-full rounded-xl border border-border bg-surface/70 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          >
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
      </div>

      {erro && (
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>
      )}

      <section className="grid grid-cols-3 gap-2.5 lg:grid-cols-6">
        <div className="relative overflow-hidden rounded-2xl border border-primary/20 bg-gradient-to-br from-primary/20 to-transparent p-3 shadow-soft sm:p-4">
          <div className="flex items-center justify-between gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary sm:h-9 sm:w-9">
              <Users
                size={17}
                strokeWidth={1.9}
              />
            </span>

            <p className="text-2xl font-semibold tracking-tight text-foreground">
              {
                dashboard
                  .hospedes.total
              }
            </p>
          </div>

          <p className="mt-3 text-xs font-medium text-foreground">
            Hóspedes
          </p>

          <p className="mt-1 truncate text-[11px] text-muted">
            {
              dashboard
                .hospedes
                .presentes
            }{" "}
            presentes
          </p>
        </div>

        <div className="relative overflow-hidden rounded-2xl border border-warning/20 bg-gradient-to-br from-warning/15 to-transparent p-3 shadow-soft sm:p-4">
          <div className="flex items-center justify-between gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-warning/15 text-warning sm:h-9 sm:w-9">
              <Clock3
                size={17}
                strokeWidth={1.9}
              />
            </span>

            <p className="text-2xl font-semibold tracking-tight text-foreground">
              {
                dashboard
                  .hospedes
                  .pendentes
              }
            </p>
          </div>

          <p className="mt-3 text-xs font-medium text-foreground">
            Check-ins
          </p>

          <p className="mt-1 truncate text-[11px] text-warning">
            {dashboard.hospedes.taxaCheckIn.toFixed(
              1,
            )}
            % feitos
          </p>
        </div>

        <div className="relative overflow-hidden rounded-2xl border border-electric/20 bg-gradient-to-br from-electric/15 to-transparent p-3 shadow-soft sm:p-4">
          <div className="flex items-center justify-between gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-electric/15 text-electric sm:h-9 sm:w-9">
              <BedDouble
                size={17}
                strokeWidth={1.9}
              />
            </span>

            <p className="text-2xl font-semibold tracking-tight text-foreground">
              {
                dashboard
                  .hospedagem
                  .disponiveis
              }
            </p>
          </div>

          <p className="mt-3 text-xs font-medium text-foreground">
            Vagas
          </p>

          <p className="mt-1 truncate text-[11px] text-muted">
            {
              dashboard
                .hospedagem
                .ocupadas
            }
            /
            {
              dashboard
                .hospedagem
                .vagasTotais
            }{" "}
            ocupadas
          </p>
        </div>

        <div className="relative overflow-hidden rounded-2xl border border-border bg-surface/60 p-3 shadow-soft sm:p-4">
          <div className="flex items-center justify-between gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted sm:h-9 sm:w-9">
              <Bus
                size={17}
                strokeWidth={1.9}
              />
            </span>

            <p className="text-2xl font-semibold tracking-tight text-foreground">
              {
                dashboard
                  .traslados
                  .aguardando
              }
            </p>
          </div>

          <p className="mt-3 text-xs font-medium text-foreground">
            Aguardando
          </p>

          <p className="mt-1 text-[11px] text-muted">
            Traslados
          </p>
        </div>

        <div className="relative overflow-hidden rounded-2xl border border-magenta/20 bg-gradient-to-br from-magenta/15 to-transparent p-3 shadow-soft sm:p-4">
          <div className="flex items-center justify-between gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-magenta/15 text-magenta sm:h-9 sm:w-9">
              <Bus
                size={17}
                strokeWidth={1.9}
              />
            </span>

            <p className="text-2xl font-semibold tracking-tight text-foreground">
              {
                dashboard
                  .traslados
                  .emAndamento
              }
            </p>
          </div>

          <p className="mt-3 text-xs font-medium text-foreground">
            Em andamento
          </p>

          <p className="mt-1 text-[11px] text-muted">
            Traslados
          </p>
        </div>

        <div className="relative overflow-hidden rounded-2xl border border-success/20 bg-gradient-to-br from-success/15 to-transparent p-3 shadow-soft sm:p-4">
          <div className="flex items-center justify-between gap-2">
            <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-success/15 text-success sm:h-9 sm:w-9">
              <CheckCircle2
                size={17}
                strokeWidth={1.9}
              />
            </span>

            <p className="text-2xl font-semibold tracking-tight text-foreground">
              {
                dashboard
                  .traslados
                  .concluidos
              }
            </p>
          </div>

          <p className="mt-3 text-xs font-medium text-foreground">
            Concluídos
          </p>

          <p className="mt-1 text-[11px] text-muted">
            Traslados
          </p>
        </div>
      </section>

      <section
        className={
          dashboard.atencao
            .hospedesSemQuarto > 0
            ? "rounded-2xl border border-danger/25 bg-gradient-to-br from-danger/15 to-transparent p-4 shadow-soft"
            : "rounded-2xl border border-success/20 bg-gradient-to-br from-success/10 to-transparent p-4 shadow-soft"
        }
      >
        <div className="flex items-start gap-3">
          <span
            className={
              dashboard
                .atencao
                .hospedesSemQuarto > 0
                ? "flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-danger/15 text-danger"
                : "flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-success/15 text-success"
            }
          >
            {dashboard.atencao
              .hospedesSemQuarto >
            0 ? (
              <AlertTriangle
                size={19}
                strokeWidth={1.9}
              />
            ) : (
              <CheckCircle2
                size={19}
                strokeWidth={1.9}
              />
            )}
          </span>

          <div className="min-w-0 flex-1">
            <p className="text-sm font-semibold text-foreground">
              {dashboard.atencao
                .hospedesSemQuarto >
              0
                ? "Atenção necessária"
                : "Operação sem pendências"}
            </p>

            <p className="mt-1 text-xs leading-5 text-muted">
              {dashboard.atencao
                .hospedesSemQuarto >
              0
                ? `${dashboard.atencao.hospedesSemQuarto} hóspede(s) pendente(s) de check-in ainda não possuem quarto alocado.`
                : "Todos os hóspedes pendentes de check-in possuem quarto alocado."}
            </p>

            {dashboard.atencao
              .hospedesSemQuarto >
              0 && (
              <div className="mt-3 space-y-2">
                {dashboard.atencao.hospedesSemQuartoDetalhes.map(
                  (hospede) => (
                    <Link
                      key={
                        hospede.hospedeId
                      }
                      href={`/hospedes/${hospede.hospedeId}`}
                      className="flex items-center justify-between rounded-xl border border-border bg-background/30 px-3 py-2.5 transition-all hover:border-primary/40 hover:bg-primary/5"
                    >
                      <span className="truncate text-sm font-medium text-foreground">
                        {
                          hospede.hospedeNome
                        }
                      </span>

                      <span className="ml-3 text-xs font-medium text-primary">
                        Abrir
                      </span>
                    </Link>
                  ),
                )}
              </div>
            )}
          </div>
        </div>
      </section>

      <QuickActions />

      <section className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft">
        <div className="flex items-start gap-3">
          <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-electric/15 text-electric">
            <Bus
              size={19}
              strokeWidth={1.9}
            />
          </span>

          <div className="min-w-0 flex-1">
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm font-semibold text-foreground">
                Próximos traslados
              </p>

              {dashboard
                .traslados
                .proximos.length >
                0 && (
                <Link
                  href="/traslados"
                  className="text-xs font-medium text-primary"
                >
                  Ver todos
                </Link>
              )}
            </div>

            {dashboard
              .traslados
              .proximos.length ===
            0 ? (
              <p className="mt-1 text-xs leading-5 text-muted">
                Nenhum deslocamento próximo.
              </p>
            ) : (
              <div className="mt-3 space-y-2">
                {dashboard.traslados.proximos
                  .slice(0, 3)
                  .map(
                    (
                      traslado,
                    ) => (
                      <Link
                        key={
                          traslado.id
                        }
                        href="/traslados"
                        className="block rounded-xl border border-border bg-background/25 px-3 py-2.5 transition-all hover:border-primary/30"
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div className="min-w-0">
                            <p className="truncate text-sm font-medium text-foreground">
                              {
                                traslado.hospedeNome
                              }
                            </p>

                            <p className="mt-1 truncate text-xs text-muted">
                              {
                                traslado.localOrigem
                              }
                              {
                                " → "
                              }
                              {
                                traslado.localDestino
                              }
                            </p>
                          </div>

                          <span className="shrink-0 text-xs font-medium text-electric">
                            {new Date(
                              traslado.dataHoraPrevista,
                            ).toLocaleString(
                              "pt-BR",
                              {
                                day: "2-digit",
                                month:
                                  "2-digit",
                                hour: "2-digit",
                                minute:
                                  "2-digit",
                              },
                            )}
                          </span>
                        </div>
                      </Link>
                    ),
                  )}
              </div>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}