"use client";

import Link from "next/link";

import {
  BedDouble,
  ChevronLeft,
  ShieldCheck,
  Users,
} from "lucide-react";

import {
  useEffect,
  useState,
} from "react";

import {
  buscarMeuQuarto,
} from "@/features/hospede/services/hospedeService";

import type {
  MeuQuarto,
  TipoCama,
  TipoQuarto,
} from "@/features/hospede/types/meuQuarto";

function formatarTipoQuarto(
  tipo: TipoQuarto,
) {
  switch (tipo) {
    case "SUITE":
      return "Suíte";

    case "ALOJAMENTO":
      return "Alojamento";

    default:
      return tipo;
  }
}

function formatarTipoCama(
  tipo: TipoCama | null,
) {
  if (!tipo) {
    return "A definir";
  }

  switch (tipo) {
    case "CASAL":
      return "Casal";

    case "BELICHE":
      return "Beliche";

    default:
      return tipo;
  }
}

export default function MeuQuartoPage() {
  const [
    quarto,
    setQuarto,
  ] = useState<MeuQuarto | null>(
    null,
  );

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function carregarQuarto() {
      try {
        setErro("");

        const response =
          await buscarMeuQuarto();

        setQuarto(
          response,
        );
      } catch {
        setErro(
          "Não foi possível carregar seu quarto.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarQuarto();
  }, []);

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando seu quarto...
        </p>
      </div>
    );
  }

  if (erro) {
    return (
      <div className="py-10">
        <p
          role="alert"
          className="text-sm text-danger"
        >
          {erro}
        </p>
      </div>
    );
  }

  if (!quarto) {
    return (
      <div className="space-y-5">
        <header className="flex items-center gap-3">
          <Link
            href="/app"
            aria-label="Voltar"
            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border bg-surface/40 text-foreground transition-colors active:scale-95"
          >
            <ChevronLeft
              size={20}
              strokeWidth={1.9}
            />
          </Link>

          <div>
            <h1 className="text-xl font-semibold tracking-tight text-foreground">
              Meu Quarto
            </h1>

            <p className="text-xs text-muted">
              Sua acomodação Beat Trips
            </p>
          </div>
        </header>

        <section className="relative overflow-hidden rounded-3xl border border-border bg-surface/60 p-6 text-center shadow-soft">
          <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_16%),transparent_38%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_10%),transparent_42%)]" />

          <div className="relative">
            <span className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/15 text-primary">
              <BedDouble
                size={26}
                strokeWidth={1.8}
              />
            </span>

            <h2 className="mt-4 text-base font-semibold text-foreground">
              Quarto ainda não definido
            </h2>

            <p className="mt-2 text-sm leading-6 text-muted">
              Assim que sua acomodação for definida pela
              equipe Beat Trips, os detalhes aparecerão aqui.
            </p>
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <header className="flex items-center gap-3">
        <Link
          href="/app"
          aria-label="Voltar"
          className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border bg-surface/40 text-foreground transition-colors active:scale-95"
        >
          <ChevronLeft
            size={20}
            strokeWidth={1.9}
          />
        </Link>

        <div className="min-w-0">
          <h1 className="truncate text-xl font-semibold tracking-tight text-foreground">
            Meu Quarto
          </h1>

          <p className="truncate text-xs text-muted">
            {quarto.viagemNome}
          </p>
        </div>
      </header>

      <section className="gradient-brand rounded-3xl p-[1px] shadow-glow">
        <div className="rounded-[23px] bg-surface p-5">
          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="text-xs text-muted">
                {formatarTipoQuarto(
                  quarto.quartoTipo,
                )}
              </p>

              <h2 className="mt-1 text-2xl font-semibold tracking-tight text-foreground">
                {quarto.quartoNome}
              </h2>
            </div>

            <span className="shrink-0 rounded-full border border-success/30 bg-success/10 px-3 py-1 text-xs font-medium text-success">
              Alocado
            </span>
          </div>

          <div className="mt-5 grid grid-cols-2 gap-3">
            <div className="rounded-2xl bg-surface-secondary p-4">
              <div className="flex items-center gap-2 text-xs text-muted">
                <BedDouble
                  size={14}
                  strokeWidth={1.8}
                />

                Sua cama
              </div>

              <p className="mt-2 text-sm font-semibold text-foreground">
                {formatarTipoCama(
                  quarto.tipoCama,
                )}
              </p>
            </div>

            <div className="rounded-2xl bg-surface-secondary p-4">
              <div className="flex items-center gap-2 text-xs text-muted">
                <Users
                  size={14}
                  strokeWidth={1.8}
                />

                Capacidade
              </div>

              <p className="mt-2 text-sm font-semibold text-foreground">
                {quarto.capacidade}{" "}
                {quarto.capacidade === 1
                  ? "hóspede"
                  : "hóspedes"}
              </p>
            </div>
          </div>
        </div>
      </section>

      <section>
        <h2 className="mb-3 text-sm font-semibold text-foreground">
          Informações do quarto
        </h2>

        <div className="rounded-3xl border border-border bg-surface/60 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
              <ShieldCheck
                size={19}
                strokeWidth={1.8}
              />
            </span>

            <div>
              <p className="text-sm font-semibold text-foreground">
                Regras da acomodação
              </p>

              <p className="mt-1 text-sm leading-6 text-muted">
                As regras específicas do seu quarto serão
                exibidas aqui quando estiverem cadastradas
                pela equipe Beat Trips.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}