"use client";

import Link from "next/link";
import {
  useEffect,
  useState,
} from "react";

import {
  Bell,
  ChevronLeft,
  CircleAlert,
  Hotel,
  Info,
  Megaphone,
  Plane,
} from "lucide-react";

import {
  buscarMeusAvisos,
} from "@/features/hospede/services/hospedeService";

import type {
  MeuAviso,
  TipoMeuAviso,
} from "@/features/hospede/types/meuAviso";

function formatarDataHora(
  dataHora: string,
) {
  return new Intl.DateTimeFormat(
    "pt-BR",
    {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    },
  ).format(
    new Date(dataHora),
  );
}

function formatarTipo(
  tipo: TipoMeuAviso,
) {
  switch (tipo) {
    case "IMPORTANTE":
      return "Importante";

    case "TRANSPORTE":
      return "Transporte";

    case "HOSPEDAGEM":
      return "Hospedagem";

    case "EVENTO":
      return "Evento";

    case "INFORMATIVO":
    default:
      return "Informativo";
  }
}

function iconeTipo(
  tipo: TipoMeuAviso,
) {
  switch (tipo) {
    case "IMPORTANTE":
      return CircleAlert;

    case "TRANSPORTE":
      return Plane;

    case "HOSPEDAGEM":
      return Hotel;

    case "EVENTO":
      return Megaphone;

    case "INFORMATIVO":
    default:
      return Info;
  }
}

function classeTipo(
  tipo: TipoMeuAviso,
) {
  switch (tipo) {
    case "IMPORTANTE":
      return "border-danger/30 bg-danger/10 text-danger";

    case "TRANSPORTE":
      return "border-primary/30 bg-primary/10 text-primary";

    case "HOSPEDAGEM":
      return "border-success/30 bg-success/10 text-success";

    case "EVENTO":
      return "border-primary/30 bg-primary/10 text-primary";

    case "INFORMATIVO":
    default:
      return "border-border bg-background text-muted";
  }
}

export default function AvisosPage() {
  const [
    avisos,
    setAvisos,
  ] = useState<MeuAviso[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function carregarAvisos() {
      try {
        setErro("");

        const dados =
          await buscarMeusAvisos();

        setAvisos(
          dados,
        );
      } catch {
        setErro(
          "Não foi possível carregar seus avisos.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarAvisos();
  }, []);

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
            Avisos
          </h1>

          <p className="truncate text-xs text-muted">
            Central de comunicados
          </p>
        </div>
      </header>

      {carregando ? (
        <p className="py-6 text-sm text-muted">
          Carregando avisos...
        </p>
      ) : erro ? (
        <div
          role="alert"
          className="rounded-2xl border border-danger/30 bg-danger/10 p-4 text-sm text-danger"
        >
          {erro}
        </div>
      ) : avisos.length === 0 ? (
        <section className="relative overflow-hidden rounded-3xl border border-border bg-surface/60 p-6 shadow-soft">
          <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_16%),transparent_38%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_10%),transparent_42%)]" />

          <div className="relative flex flex-col items-center text-center">
            <span className="flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/15 text-primary">
              <Bell
                size={25}
                strokeWidth={1.8}
              />
            </span>

            <h2 className="mt-4 text-base font-semibold text-foreground">
              Nenhum aviso por enquanto
            </h2>

            <p className="mt-2 max-w-xs text-sm leading-6 text-muted">
              Os comunicados importantes da sua experiência Beat Trips aparecerão aqui.
            </p>

            <div className="mt-5 w-full max-w-[280px] rounded-2xl border border-border bg-background/35 p-4 text-left">
              <p className="text-xs font-medium text-primary">
                Fique tranquilo
              </p>

              <p className="mt-2 text-sm leading-6 text-muted">
                Quando houver novidades sobre transporte, hospedagem ou sua experiência no evento, você poderá acompanhar tudo por esta área.
              </p>
            </div>
          </div>
        </section>
      ) : (
        <section className="space-y-3">
          {avisos.map(
            (aviso) => {
              const Icone =
                iconeTipo(
                  aviso.tipo,
                );

              return (
                <article
                  key={aviso.id}
                  className="rounded-3xl border border-border bg-surface/60 p-5 shadow-soft"
                >
                  <div className="flex items-start gap-3">
                    <span
                      className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border ${classeTipo(
                        aviso.tipo,
                      )}`}
                    >
                      <Icone
                        size={18}
                        strokeWidth={1.8}
                      />
                    </span>

                    <div className="min-w-0 flex-1">
                      <div className="flex flex-wrap items-center gap-2">
                        <span
                          className={`inline-flex rounded-full border px-2.5 py-1 text-[11px] font-medium ${classeTipo(
                            aviso.tipo,
                          )}`}
                        >
                          {formatarTipo(
                            aviso.tipo,
                          )}
                        </span>

                        <span className="text-xs text-muted">
                          {formatarDataHora(
                            aviso.dataPublicacao,
                          )}
                        </span>
                      </div>

                      <h2 className="mt-3 text-base font-semibold text-foreground">
                        {aviso.titulo}
                      </h2>

                      <p className="mt-2 whitespace-pre-line text-sm leading-6 text-muted">
                        {aviso.mensagem}
                      </p>
                    </div>
                  </div>
                </article>
              );
            },
          )}
        </section>
      )}
    </div>
  );
}