"use client";

import { useEffect, useState } from "react";

import { QuickActions } from "@/features/dashboard/components/QuickActions";
import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { buscarDashboard } from "@/features/dashboard/services/dashboardService";
import type { DashboardResponse } from "@/features/dashboard/types/dashboard";
import { listarViagens } from "@/features/viagem/services/viagemService";
import type { Viagem } from "@/features/viagem/types/viagem";

export default function DashboardPage() {
  const [dashboard, setDashboard] =
    useState<DashboardResponse | null>(null);

  const [viagens, setViagens] = useState<Viagem[]>([]);
  const [viagemAtivaId, setViagemAtivaId] =
    useState<number | null>(null);

  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    async function carregarViagens() {
      try {
        setErro("");

        const dados = await listarViagens();

        setViagens(dados);

        const viagemPreferencial =
          dados.find(
            (viagem) => viagem.status === "EM_ANDAMENTO",
          ) ??
          dados.find(
            (viagem) => viagem.status === "PLANEJADA",
          ) ??
          dados[0];

        if (viagemPreferencial) {
          setViagemAtivaId(viagemPreferencial.id);
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

        const dados = await buscarDashboard(
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
    <div className="space-y-8">
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
            value={viagemAtivaId ?? ""}
            onChange={(event) =>
              setViagemAtivaId(
                Number(event.target.value),
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          >
            {viagens.map((viagem) => (
              <option
                key={viagem.id}
                value={viagem.id}
              >
                {viagem.nome} — {viagem.status}
              </option>
            ))}
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

      <section className="grid gap-4 md:grid-cols-3">
        <Card>
          <p className="text-sm text-muted">
            Hóspedes
          </p>

          <p className="mt-2 text-3xl font-semibold text-foreground">
            {dashboard.hospedes.total}
          </p>

          <p className="mt-2 text-sm text-muted">
            {dashboard.hospedes.presentes} presentes
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Check-ins pendentes
          </p>

          <p className="mt-2 text-3xl font-semibold text-foreground">
            {dashboard.hospedes.pendentes}
          </p>

          <p className="mt-2 text-sm text-muted">
            {dashboard.hospedes.taxaCheckIn.toFixed(1)}% realizados
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Vagas disponíveis
          </p>

          <p className="mt-2 text-3xl font-semibold text-foreground">
            {dashboard.hospedagem.disponiveis}
          </p>

          <p className="mt-2 text-sm text-muted">
            {dashboard.hospedagem.ocupadas} de{" "}
            {dashboard.hospedagem.vagasTotais} ocupadas
          </p>
        </Card>
      </section>

      <section className="grid gap-4 md:grid-cols-3">
        <Card>
          <p className="text-sm text-muted">
            Traslados aguardando
          </p>

          <p className="mt-2 text-3xl font-semibold text-foreground">
            {dashboard.traslados.aguardando}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Em andamento
          </p>

          <p className="mt-2 text-3xl font-semibold text-foreground">
            {dashboard.traslados.emAndamento}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Concluídos
          </p>

          <p className="mt-2 text-3xl font-semibold text-foreground">
            {dashboard.traslados.concluidos}
          </p>
        </Card>
      </section>

      <section className="space-y-4">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Atenção necessária
          </h2>

          <p className="mt-1 text-sm text-muted">
            Situações que merecem acompanhamento da equipe.
          </p>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card>
            <p className="text-sm font-medium text-foreground">
              Check-ins pendentes
            </p>

            <p className="mt-2 text-3xl font-semibold text-foreground">
              {dashboard.hospedes.pendentes}
            </p>

            <p className="mt-2 text-sm text-muted">
              Hóspedes ainda aguardando check-in.
            </p>
          </Card>

          <Card>
            <p className="text-sm font-medium text-foreground">
              Capacidade disponível
            </p>

            <p className="mt-2 text-3xl font-semibold text-foreground">
              {dashboard.hospedagem.disponiveis}
            </p>

            <p className="mt-2 text-sm text-muted">
              Vagas ainda disponíveis na hospedagem.
            </p>
          </Card>
        </div>
      </section>

      <QuickActions />

      <section className="space-y-4">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Próximos traslados
          </h2>

          <p className="mt-1 text-sm text-muted">
            Próximos deslocamentos aguardando atendimento.
          </p>
        </div>

        {dashboard.traslados.proximos.length === 0 ? (
          <Card>
            <p className="text-sm text-muted">
              Nenhum traslado próximo encontrado.
            </p>
          </Card>
        ) : (
          <div className="space-y-3">
            {dashboard.traslados.proximos.map((traslado) => (
              <Card key={traslado.id}>
                <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                  <div>
                    <p className="font-medium text-foreground">
                      {traslado.hospedeNome}
                    </p>

                    <p className="mt-1 text-sm text-muted">
                      {traslado.localOrigem}
                      {" → "}
                      {traslado.localDestino}
                    </p>
                  </div>

                  <div className="md:text-right">
                    <p className="text-sm font-medium text-foreground">
                      {new Date(
                        traslado.dataHoraPrevista,
                      ).toLocaleString("pt-BR", {
                        day: "2-digit",
                        month: "2-digit",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </p>

                    <p className="mt-1 text-xs text-muted">
                      {traslado.tipo ===
                      "AEROPORTO_PARA_HOSPEDAGEM"
                        ? "Aeroporto → Hospedagem"
                        : traslado.tipo ===
                            "HOSPEDAGEM_PARA_AEROPORTO"
                          ? "Hospedagem → Aeroporto"
                          : "Outro traslado"}
                    </p>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}