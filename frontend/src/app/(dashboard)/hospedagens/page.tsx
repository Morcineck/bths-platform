"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";

import {
  listarHospedagensPorViagem,
} from "@/features/hospedagem/services/hospedagemService";

import type {
  Hospedagem,
} from "@/features/hospedagem/types/hospedagem";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

export default function HospedagensPage() {
  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    viagemSelecionadaId,
    setViagemSelecionadaId,
  ] = useState<number | null>(null);

  const [
    hospedagens,
    setHospedagens,
  ] = useState<Hospedagem[]>([]);

  const [
    carregandoViagens,
    setCarregandoViagens,
  ] = useState(true);

  const [
    carregandoHospedagens,
    setCarregandoHospedagens,
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
        setCarregandoViagens(
          false,
        );
      }
    }

    carregarViagens();
  }, []);

  useEffect(() => {
    if (
      viagemSelecionadaId === null
    ) {
      return;
    }

    async function carregarHospedagens() {
      try {
        setCarregandoHospedagens(
          true,
        );

        setErro("");

        const dados =
          await listarHospedagensPorViagem(
            viagemSelecionadaId!,
          );

        setHospedagens(
          dados,
        );
      } catch (error) {
        setHospedagens([]);

        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar as hospedagens.",
        );
      } finally {
        setCarregandoHospedagens(
          false,
        );
      }
    }

    carregarHospedagens();
  }, [viagemSelecionadaId]);

  if (carregandoViagens) {
    return (
      <p className="text-sm text-muted">
        Carregando hospedagens...
      </p>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
        <PageHeader
          title="Hospedagens"
          description="Cadastre e gerencie os locais de hospedagem de cada viagem."
        />

        <div className="flex w-full flex-col gap-3 sm:w-auto sm:flex-row">
          <div className="relative w-full sm:w-[260px]">
            <select
              id="viagem"
              value={
                viagemSelecionadaId ??
                ""
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

          {viagemSelecionadaId && (
            <Link
              href={`/hospedagens/nova?viagemId=${viagemSelecionadaId}`}
              className="inline-flex h-12 items-center justify-center rounded-xl bg-primary px-5 text-sm font-semibold text-white transition-opacity hover:opacity-90"
            >
              Nova hospedagem
            </Link>
          )}
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

      {carregandoHospedagens ? (
        <Card>
          <p className="text-sm text-muted">
            Carregando hospedagens da viagem...
          </p>
        </Card>
      ) : hospedagens.length === 0 ? (
        <Card>
          <p className="font-medium text-foreground">
            Nenhuma hospedagem cadastrada.
          </p>

          <p className="mt-2 text-sm text-muted">
            Cadastre a primeira hospedagem desta viagem para
            começar a vincular os quartos.
          </p>
        </Card>
      ) : (
        <section className="space-y-4">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Hospedagens da viagem
            </h2>

            <p className="mt-1 text-sm text-muted">
              Cada quarto pode ser associado a uma dessas
              hospedagens.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {hospedagens.map(
              (hospedagem) => (
                <Card
                  key={hospedagem.id}
                  className="space-y-5"
                >
                  <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-muted">
                      Hospedagem
                    </p>

                    <h3 className="mt-1 text-lg font-semibold text-foreground">
                      {hospedagem.nome}
                    </h3>

                    <p className="mt-2 text-sm text-muted">
                      {[
                        hospedagem.cidade,
                        hospedagem.estado,
                      ]
                        .filter(Boolean)
                        .join(" / ") ||
                        "Localização a definir"}
                    </p>
                  </div>

                  <div className="rounded-xl border border-border bg-background/40 p-4">
                    <p className="text-xs text-muted">
                      Endereço
                    </p>

                    <p className="mt-1 text-sm font-medium leading-6 text-foreground">
                      {hospedagem.endereco ??
                        "A definir"}
                    </p>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div className="rounded-xl border border-border bg-background/40 p-3">
                      <p className="text-xs text-muted">
                        Check-in
                      </p>

                      <p className="mt-1 text-sm font-semibold text-foreground">
                        {hospedagem.horarioCheckIn
                          ? hospedagem.horarioCheckIn.slice(
                              0,
                              5,
                            )
                          : "A definir"}
                      </p>
                    </div>

                    <div className="rounded-xl border border-border bg-background/40 p-3">
                      <p className="text-xs text-muted">
                        Check-out
                      </p>

                      <p className="mt-1 text-sm font-semibold text-foreground">
                        {hospedagem.horarioCheckOut
                          ? hospedagem.horarioCheckOut.slice(
                              0,
                              5,
                            )
                          : "A definir"}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center justify-end border-t border-border pt-4">
                    <Link
                      href={`/hospedagens/${hospedagem.id}/editar`}
                      className="text-sm font-medium text-primary transition-opacity hover:opacity-80"
                    >
                      Editar hospedagem
                    </Link>
                  </div>
                </Card>
              ),
            )}
          </div>
        </section>
      )}
    </div>
  );
}