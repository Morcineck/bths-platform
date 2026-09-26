"use client";

import { useEffect, useState } from "react";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";

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
      carregandoViagens,
      setCarregandoViagens,
    ] = useState(true);

    const [
      carregandoQuartos,
      setCarregandoQuartos,
    ] = useState(false);

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

if (carregandoViagens) {
  return (
    <p className="text-sm text-muted">
      Carregando quartos...
    </p>
  );
}

return (
  <div className="space-y-8">
    <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
      <PageHeader
        title="Quartos"
        description="Gestão da capacidade, ocupação e disponibilidade dos quartos."
      />

      <div className="relative w-full sm:w-[260px]">
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
          aria-label="Selecionar viagem"
          className="
            h-12
            w-full
            appearance-none
            rounded-xl
            border
            border-border
            bg-surface
            px-4
            pr-11
            text-sm
            font-medium
            text-foreground
            outline-none
            transition-all
            duration-200
            hover:border-primary/60
            focus:border-primary
            focus:ring-2
            focus:ring-primary/20
            disabled:cursor-not-allowed
            disabled:opacity-50
          "
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

        <div className="pointer-events-none absolute inset-y-0 right-4 flex items-center">
          <svg
            viewBox="0 0 20 20"
            fill="none"
            aria-hidden="true"
            className="h-4 w-4 text-muted"
          >
            <path
              d="M6 8L10 12L14 8"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </div>
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

    {carregandoQuartos ? (
      <Card>
        <p className="text-sm text-muted">
          Carregando quartos da viagem...
        </p>
      </Card>
    ) : quartos.length === 0 ? (
      <Card>
        <p className="font-medium text-foreground">
          Nenhum quarto encontrado.
        </p>

        <p className="mt-2 text-sm text-muted">
          Ainda não existem quartos cadastrados para esta viagem.
        </p>
      </Card>
    ) : (
      <section className="space-y-4">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Ocupação dos quartos
          </h2>

          <p className="mt-1 text-sm text-muted">
            Capacidade e disponibilidade atuais da viagem selecionada.
          </p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {quartos.map(
            (quarto) => {
              const lotado =
                quarto.capacidade > 0 &&
                quarto.ocupacao >=
                  quarto.capacidade;

              return (
                <Card
                  key={quarto.quartoId}
                  className="space-y-5"
                >
                  <div className="flex items-start justify-between gap-4">
                    <div>
                      <p className="text-xs font-medium uppercase tracking-wide text-muted">
                        {quarto.tipo ===
                        "SUITE"
                          ? "Suíte"
                          : "Alojamento"}
                      </p>

                      <h3 className="mt-1 text-lg font-semibold text-foreground">
                        {quarto.nome}
                      </h3>
                    </div>

                    <span
                      className={
                        quarto.status ===
                        "INDISPONIVEL"
                          ? "inline-flex rounded-full border border-red-400/40 px-3 py-1 text-xs font-semibold text-red-400"
                          : "inline-flex rounded-full border border-primary/40 px-3 py-1 text-xs font-semibold text-primary"
                      }
                    >
                      {quarto.status ===
                      "DISPONIVEL"
                        ? "Disponível"
                        : "Indisponível"}
                    </span>
                  </div>

                  <div className="rounded-xl border border-border bg-background/40 p-4">
                    <div className="flex items-end justify-between gap-4">
                      <div>
                        <p className="text-sm text-muted">
                          Ocupação
                        </p>

                        <p className="mt-1 text-2xl font-semibold text-foreground">
                          {quarto.ocupacao} /{" "}
                          {quarto.capacidade}
                        </p>
                      </div>

                      <p className="text-sm font-medium text-muted">
                        {quarto.vagasDisponiveis}{" "}
                        {quarto.vagasDisponiveis ===
                        1
                          ? "vaga disponível"
                          : "vagas disponíveis"}
                      </p>
                    </div>

                    {quarto.capacidade > 0 && (
                      <div className="mt-4 h-2 overflow-hidden rounded-full bg-border">
                        <div
                          className="h-full rounded-full bg-primary transition-all"
                          style={{
                            width: `${Math.min(
                              (quarto.ocupacao /
                                quarto.capacidade) *
                                100,
                              100,
                            )}%`,
                          }}
                        />
                      </div>
                    )}
                  </div>

                  <div className="flex items-center justify-between gap-4 border-t border-border pt-4">
                    <p className="text-sm text-muted">
                      Situação
                    </p>

                    <p className="text-sm font-medium text-foreground">
                      {quarto.status ===
                      "INDISPONIVEL"
                        ? "Fora de operação"
                        : lotado
                          ? "Lotado"
                          : "Com disponibilidade"}
                    </p>
                  </div>
                </Card>
              );
            },
          )}
        </div>
      </section>
    )}
  </div>
);
}


