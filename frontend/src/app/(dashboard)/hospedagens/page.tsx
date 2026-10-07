"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  Clock3,
  Hotel,
  MapPin,
  Pencil,
  Plus,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

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
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Hospedagens"
          description="Cadastre e gerencie os locais de hospedagem de cada viagem."
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
              href={`/hospedagens/nova?viagemId=${viagemSelecionadaId}`}
              className="gradient-brand inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
            >
              <Plus
                size={18}
                strokeWidth={1.9}
              />

              Nova hospedagem
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

      {carregandoHospedagens ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-4">
          <p className="text-sm text-muted">
            Carregando hospedagens da viagem...
          </p>
        </div>
      ) : hospedagens.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <Hotel
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhuma hospedagem cadastrada
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Cadastre a primeira hospedagem desta viagem para começar a vincular os quartos.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Hospedagens da viagem
              </h2>

              <p className="mt-1 text-xs text-muted">
                Locais disponíveis para associação com os quartos.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {hospedagens.length}{" "}
              {hospedagens.length === 1
                ? "hospedagem"
                : "hospedagens"}
            </span>
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            {hospedagens.map(
              (hospedagem) => (
                <article
                  key={hospedagem.id}
                  className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
                >
                  <div className="flex items-start gap-3">
                    <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                      <Hotel
                        size={18}
                        strokeWidth={1.9}
                      />
                    </span>

                    <div className="min-w-0 flex-1">
                      <p className="text-[11px] font-medium uppercase tracking-[0.12em] text-muted">
                        Hospedagem
                      </p>

                      <h3 className="mt-1 truncate text-base font-semibold text-foreground">
                        {hospedagem.nome}
                      </h3>

                      <div className="mt-2 flex items-center gap-2 text-xs text-muted">
                        <MapPin
                          size={14}
                          strokeWidth={1.9}
                          className="shrink-0"
                        />

                        <span className="truncate">
                          {[
                            hospedagem.cidade,
                            hospedagem.estado,
                          ]
                            .filter(Boolean)
                            .join(" / ") ||
                            "Localização a definir"}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="mt-4 rounded-xl border border-border bg-background/25 px-3 py-2.5">
                    <p className="text-[11px] text-muted">
                      Endereço
                    </p>

                    <p className="mt-1 text-sm font-medium leading-5 text-foreground">
                      {hospedagem.endereco ??
                        "A definir"}
                    </p>
                  </div>

                  <div className="mt-3 grid grid-cols-2 gap-2">
                    <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
                      <div className="flex items-center gap-2">
                        <Clock3
                          size={14}
                          strokeWidth={1.9}
                          className="text-success"
                        />

                        <p className="text-[11px] text-muted">
                          Check-in
                        </p>
                      </div>

                      <p className="mt-1.5 text-sm font-semibold text-foreground">
                        {hospedagem.horarioCheckIn
                          ? hospedagem.horarioCheckIn.slice(
                              0,
                              5,
                            )
                          : "A definir"}
                      </p>
                    </div>

                    <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
                      <div className="flex items-center gap-2">
                        <Clock3
                          size={14}
                          strokeWidth={1.9}
                          className="text-warning"
                        />

                        <p className="text-[11px] text-muted">
                          Check-out
                        </p>
                      </div>

                      <p className="mt-1.5 text-sm font-semibold text-foreground">
                        {hospedagem.horarioCheckOut
                          ? hospedagem.horarioCheckOut.slice(
                              0,
                              5,
                            )
                          : "A definir"}
                      </p>
                    </div>
                  </div>

                  <div className="mt-3 border-t border-border pt-3">
                    <Link
                      href={`/hospedagens/${hospedagem.id}/editar`}
                      className="inline-flex h-9 w-full items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
                    >
                      <Pencil
                        size={14}
                        strokeWidth={1.9}
                      />

                      Editar hospedagem
                    </Link>
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