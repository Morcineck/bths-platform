"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  BedDouble,
  CalendarDays,
  ChevronLeft,
  Clock3,
  KeyRound,
  LogOut,
  MapPin,
  Music2,
  PartyPopper,
  PlaneLanding,
} from "lucide-react";

import {
  buscarMinhaTimeline,
  buscarMinhaViagem,
} from "@/features/hospede/services/hospedeService";

import type {
  MinhaViagem,
} from "@/features/hospede/types/minhaViagem";

import type {
  MinhaTimeline,
} from "@/features/hospede/types/minhaTimeline";

const MESES = [
  "JAN",
  "FEV",
  "MAR",
  "ABR",
  "MAI",
  "JUN",
  "JUL",
  "AGO",
  "SET",
  "OUT",
  "NOV",
  "DEZ",
];

function formatarDataCurta(
  data: string,
) {
  const [
    ano,
    mes,
    dia,
  ] = data
    .split("-")
    .map(Number);

  return `${String(dia).padStart(
    2,
    "0",
  )} ${MESES[mes - 1]} ${ano}`;
}

function formatarPeriodo(
  inicio: string,
  fim: string,
) {
  const [
    anoInicio,
    mesInicio,
    diaInicio,
  ] = inicio
    .split("-")
    .map(Number);

  const [
    anoFim,
    mesFim,
    diaFim,
  ] = fim
    .split("-")
    .map(Number);

  if (anoInicio === anoFim) {
    return `${String(
      diaInicio,
    ).padStart(
      2,
      "0",
    )} ${MESES[mesInicio - 1]} / ${String(
      diaFim,
    ).padStart(
      2,
      "0",
    )} ${MESES[mesFim - 1]}`;
  }

  return `${formatarDataCurta(
    inicio,
  )} — ${formatarDataCurta(
    fim,
  )}`;
}

function formatarStatus(
  status: string,
) {
  switch (status) {
    case "PLANEJADA":
      return "Viagem confirmada";

    case "EM_ANDAMENTO":
      return "Viagem em andamento";

    case "FINALIZADA":
      return "Viagem finalizada";

    case "CANCELADA":
      return "Viagem cancelada";

    default:
      return status;
  }
}

function classeStatus(
  status: string,
) {
  switch (status) {
    case "PLANEJADA":
      return "border-primary/30 bg-primary/10 text-primary";

    case "EM_ANDAMENTO":
      return "border-success/30 bg-success/10 text-success";

    case "FINALIZADA":
      return "border-border bg-surface-secondary text-muted";

    case "CANCELADA":
      return "border-danger/30 bg-danger/10 text-danger";

    default:
      return "border-border bg-surface-secondary text-muted";
  }
}

function mostrarEvento(
  viagemNome: string,
  evento: string,
) {
  if (!evento.trim()) {
    return false;
  }

  return (
    viagemNome
      .trim()
      .toLowerCase() !==
    evento
      .trim()
      .toLowerCase()
  );
}

function formatarDataHoraTimeline(
  dataHora: string,
) {
  const [
    data,
    horarioCompleto,
  ] = dataHora.split("T");

  const [
    ,
    mes,
    dia,
  ] = data
    .split("-")
    .map(Number);

  const horario =
    horarioCompleto?.slice(
      0,
      5,
    );

  const dataFormatada = `${String(
    dia,
  ).padStart(
    2,
    "0",
  )} ${MESES[mes - 1]}`;

  if (!horario) {
    return dataFormatada;
  }

  return `${dataFormatada} · ${horario}`;
}

function TimelineIconComponent({
  tipo,
}: {
  tipo: MinhaTimeline["tipo"];
}) {
  switch (tipo) {
    case "CHEGADA":
      return (
        <PlaneLanding
          size={14}
          strokeWidth={1.9}
        />
      );

    case "CHECK_IN":
      return (
        <KeyRound
          size={14}
          strokeWidth={1.9}
        />
      );

    case "WELCOME_BEAT":
      return (
        <PartyPopper
          size={14}
          strokeWidth={1.9}
        />
      );

    case "FESTIVAL":
      return (
        <Music2
          size={14}
          strokeWidth={1.9}
        />
      );

    case "CHECK_OUT":
      return (
        <LogOut
          size={14}
          strokeWidth={1.9}
        />
      );

    case "OUTRO":
    default:
      return (
        <CalendarDays
          size={14}
          strokeWidth={1.9}
        />
      );
  }
}

function obterBadgeTimeline() {
  return {
    texto: "Programado",
    classe:
      "border-border bg-surface-secondary text-muted",
  };
}

export default function AppViagemPage() {
  const [
    minhaViagem,
    setMinhaViagem,
  ] =
    useState<MinhaViagem | null>(
      null,
    );

  const [
    timeline,
    setTimeline,
  ] = useState<MinhaTimeline[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function carregarMinhaViagem() {
      try {
        setErro("");

        const [
          viagemResponse,
          timelineResponse,
        ] = await Promise.all([
          buscarMinhaViagem(),
          buscarMinhaTimeline(),
        ]);

        setMinhaViagem(
          viagemResponse,
        );

        setTimeline(
          timelineResponse,
        );
      } catch {
        setErro(
          "Não foi possível carregar sua viagem.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarMinhaViagem();
  }, []);

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando sua viagem...
        </p>
      </div>
    );
  }

  if (erro) {
    return (
      <div className="py-10">
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>
      </div>
    );
  }

  if (!minhaViagem) {
    return (
      <div className="space-y-5">
        <header className="flex items-center gap-3">
          <Link
            href="/app"
            aria-label="Voltar"
            className="flex h-10 w-10 items-center justify-center rounded-full border border-border bg-surface/60"
          >
            <ChevronLeft
              size={20}
            />
          </Link>

          <div>
            <h1 className="text-xl font-semibold text-foreground">
              Minha Viagem
            </h1>
          </div>
        </header>

        <div className="rounded-3xl border border-border bg-surface/60 p-5">
          <p className="font-medium text-foreground">
            Nenhuma viagem disponível.
          </p>

          <p className="mt-2 text-sm text-muted">
            Assim que uma viagem estiver vinculada à sua conta, as informações aparecerão aqui.
          </p>
        </div>
      </div>
    );
  }

  const exibirEvento =
    mostrarEvento(
      minhaViagem.viagemNome,
      minhaViagem.evento,
    );

  const badgeTimeline =
    obterBadgeTimeline();

  return (
    <div className="space-y-5">
      <header className="-mx-5 -mt-3 border-b border-border bg-surface/45 px-5 py-3 backdrop-blur-md">
        <div className="flex items-center gap-3">
          <Link
            href="/app"
            aria-label="Voltar para início"
            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border bg-background/20 text-foreground transition-colors hover:border-primary hover:text-primary"
          >
            <ChevronLeft
              size={20}
              strokeWidth={1.9}
            />
          </Link>

          <div className="min-w-0">
            <h1 className="text-lg font-semibold tracking-tight text-foreground">
              Minha Viagem
            </h1>

            <p className="truncate text-xs text-muted">
              {minhaViagem.evento}
            </p>
          </div>
        </div>
      </header>

      <section className="rounded-3xl border border-border bg-surface/60 p-3 shadow-soft">
        <span
          className={`inline-flex rounded-full border px-3 py-1 text-xs font-medium ${classeStatus(
            minhaViagem.status,
          )}`}
        >
          {formatarStatus(
            minhaViagem.status,
          )}
        </span>

        <h2 className="mt-4 text-xl font-semibold tracking-tight text-foreground">
          {minhaViagem.viagemNome}
        </h2>

        {exibirEvento && (
          <p className="mt-1 text-sm text-muted">
            {minhaViagem.evento}
          </p>
        )}

        <div className="mt-5 grid grid-cols-2 gap-x-5 gap-y-5">
          <div>
            <div className="flex items-center gap-2 text-xs text-muted">
              <CalendarDays
                size={14}
                strokeWidth={1.8}
              />

              Datas
            </div>

            <p className="mt-1 text-sm font-semibold text-foreground">
              {formatarPeriodo(
                minhaViagem.dataInicio,
                minhaViagem.dataFim,
              )}
            </p>
          </div>

          <div>
            <div className="flex items-center gap-2 text-xs text-muted">
              <MapPin
                size={14}
                strokeWidth={1.8}
              />

              Local
            </div>

            <p className="mt-1 text-sm font-semibold text-foreground">
              {minhaViagem.cidade}
              {" / "}
              {minhaViagem.estado}
            </p>
          </div>

          <div>
            <div className="flex items-center gap-2 text-xs text-muted">
              <BedDouble
                size={14}
                strokeWidth={1.8}
              />

              Meu quarto
            </div>

            <p className="mt-1 text-sm font-semibold text-foreground">
              {minhaViagem.quartoNome
                ? minhaViagem.quartoNome
                : "A definir"}
            </p>
          </div>
        </div>

        <div className="mt-3 rounded-2xl bg-surface-secondary p-4">
          <p className="text-xs text-muted">
            Endereço da hospedagem
          </p>

          <p className="mt-1 text-sm font-medium leading-6 text-foreground">
            {minhaViagem.endereco}
          </p>

          <p className="mt-1 text-xs text-muted">
            {minhaViagem.cidade}
            {" / "}
            {minhaViagem.estado}
          </p>
        </div>
      </section>

      <section className="space-y-4">
        <div>
          <h2 className="text-center text-sm font-semibold text-foreground">
            Timeline da experiência
          </h2>


        </div>

        {timeline.length === 0 ? (
          <div className="rounded-3xl border border-border bg-surface/60 p-5 text-center shadow-soft">
            <span className="mx-auto flex h-10 w-10 items-center justify-center rounded-2xl bg-primary/10 text-primary">
              <CalendarDays
                size={19}
                strokeWidth={1.8}
              />
            </span>

            <p className="mt-3 text-sm font-medium text-foreground">
              Programação ainda não disponível
            </p>

            <p className="mt-2 text-xs leading-5 text-muted">
              Assim que a programação da experiência for publicada pela Beat Trips, ela aparecerá aqui.
            </p>
          </div>
        ) : (
          <div className="relative">
            <div className="absolute bottom-7 left-[14px] top-7 w-px bg-border" />

            <div className="space-y-4">
              {timeline.map(
                (item) => (
                  <div
                    key={item.id}
                    className="relative flex items-center gap-3"
                  >
                    <div className="relative z-10 flex w-7 shrink-0 items-center justify-center">
                      <span className="flex h-7 w-7 items-center justify-center rounded-full border border-primary/30 bg-background text-primary">
                        <TimelineIconComponent
                          tipo={
                            item.tipo
                          }
                        />
                      </span>
                    </div>

                    <div className="min-w-0 flex-1 rounded-3xl border border-border bg-surface/60 px-4 py-4 shadow-soft">
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <p className="text-sm font-semibold text-foreground">
                            {item.titulo}
                          </p>

                          <p className="mt-1 text-xs text-muted">
                            {formatarDataHoraTimeline(
                              item.dataHoraInicio,
                            )}
                          </p>

                          {item.descricao && (
                            <p className="mt-2 text-xs leading-5 text-muted">
                              {item.descricao}
                            </p>
                          )}
                        </div>

                        <span
                          className={`inline-flex shrink-0 items-center gap-1.5 rounded-full border px-3 py-1 text-[11px] font-medium ${badgeTimeline.classe}`}
                        >
                          <Clock3
                            size={12}
                            strokeWidth={2}
                          />

                          {
                            badgeTimeline.texto
                          }
                        </span>
                      </div>
                    </div>
                  </div>
                ),
              )}
            </div>
          </div>
        )}
      </section>
    </div>
  );
}