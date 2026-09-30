"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  Car,
  Check,
  ChevronLeft,
  Circle,
  CircleUserRound,
  Info,
  MapPin,
  Plane,
} from "lucide-react";

import {
  buscarMeusTraslados,
} from "@/features/hospede/services/hospedeService";

import type {
  MeuTraslado,
} from "@/features/hospede/types/meuTraslado";

type AbaTraslado =
  | "CHEGADA"
  | "RETORNO";

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

function separarDataHora(
  dataHora: string,
) {
  const [data, horario] =
    dataHora.split("T");

  const [
    ano,
    mes,
    dia,
  ] = data
    .split("-")
    .map(Number);

  return {
    data: `${String(dia).padStart(
      2,
      "0",
    )} ${MESES[mes - 1]} ${ano}`,
    horario: horario.slice(0, 5),
  };
}

function formatarStatus(
  status: string,
) {
  switch (status) {
    case "AGUARDANDO":
      return "Aguardando";

    case "EM_ANDAMENTO":
      return "Em andamento";

    case "CONCLUIDO":
      return "Concluído";

    case "CANCELADO":
      return "Cancelado";

    default:
      return status;
  }
}

function classeStatus(
  status: string,
) {
  switch (status) {
    case "AGUARDANDO":
      return "border-warning/30 bg-warning/10 text-warning";

    case "EM_ANDAMENTO":
      return "border-electric/30 bg-electric/10 text-electric";

    case "CONCLUIDO":
      return "border-success/30 bg-success/10 text-success";

    case "CANCELADO":
      return "border-red-500/30 bg-red-500/10 text-red-400";

    default:
      return "border-border bg-surface-secondary text-muted";
  }
}

export default function AppTrasladosPage() {
  const [
    traslados,
    setTraslados,
  ] = useState<MeuTraslado[]>([]);

  const [
    abaAtiva,
    setAbaAtiva,
  ] =
    useState<AbaTraslado>(
      "CHEGADA",
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
    async function carregarTraslados() {
      try {
        setErro("");

        const response =
          await buscarMeusTraslados();

        setTraslados(
          response,
        );

        const possuiChegada =
          response.some(
            (traslado) =>
              traslado.tipo ===
              "AEROPORTO_PARA_HOSPEDAGEM",
          );

        const possuiRetorno =
          response.some(
            (traslado) =>
              traslado.tipo ===
              "HOSPEDAGEM_PARA_AEROPORTO",
          );

        if (
          !possuiChegada &&
          possuiRetorno
        ) {
          setAbaAtiva(
            "RETORNO",
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar seus transportes.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarTraslados();
  }, []);

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando seus transportes...
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

  if (traslados.length === 0) {
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
              Meu Transporte
            </h1>

            <p className="text-xs text-muted">
              Beat Trips Transportes
            </p>
          </div>
        </header>

        <div className="rounded-3xl border border-border bg-surface/60 p-4">
          <p className="font-medium text-foreground">
            Nenhum transporte disponível.
          </p>

          <p className="mt-2 text-sm leading-6 text-muted">
            Assim que um transporte for vinculado à sua viagem, as informações aparecerão aqui.
          </p>
        </div>
      </div>
    );
  }

  const chegada =
    traslados.find(
      (traslado) =>
        traslado.tipo ===
        "AEROPORTO_PARA_HOSPEDAGEM",
    );

  const retorno =
    traslados.find(
      (traslado) =>
        traslado.tipo ===
        "HOSPEDAGEM_PARA_AEROPORTO",
    );

  const trasladoAtivo =
    abaAtiva === "CHEGADA"
      ? chegada
      : retorno;

  const outrosTraslados =
    traslados.filter(
      (traslado) =>
        traslado.tipo !==
          "AEROPORTO_PARA_HOSPEDAGEM" &&
        traslado.tipo !==
          "HOSPEDAGEM_PARA_AEROPORTO",
    );

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

          <div>
            <h1 className="text-lg font-semibold tracking-tight text-foreground">
              Meu Transporte
            </h1>

            <p className="text-xs text-muted">
              Beat Trips Transportes
            </p>
          </div>
        </div>
      </header>

      {(chegada || retorno) && (
        <>
          <div className="grid grid-cols-2 gap-1 rounded-2xl border border-border bg-surface/60 p-1">
            <button
              type="button"
              disabled={!chegada}
              onClick={() =>
                setAbaAtiva(
                  "CHEGADA",
                )
              }
              className={`h-11 rounded-xl text-sm font-medium transition-all ${
                abaAtiva ===
                "CHEGADA"
                  ? "gradient-brand text-white shadow-glow"
                  : "text-muted"
              } ${
                !chegada
                  ? "cursor-not-allowed opacity-35"
                  : ""
              }`}
            >
              Chegada
            </button>

            <button
              type="button"
              disabled={!retorno}
              onClick={() =>
                setAbaAtiva(
                  "RETORNO",
                )
              }
              className={`h-11 rounded-xl text-sm font-medium transition-all ${
                abaAtiva ===
                "RETORNO"
                  ? "gradient-brand text-white shadow-glow"
                  : "text-muted"
              } ${
                !retorno
                  ? "cursor-not-allowed opacity-35"
                  : ""
              }`}
            >
              Retorno
            </button>
          </div>

          {trasladoAtivo && (
            <TrasladoCard
              traslado={
                trasladoAtivo
              }
            />
          )}
        </>
      )}

      {outrosTraslados.length >
        0 && (
        <section>
          <h2 className="mb-3 text-sm font-semibold text-foreground">
            Transporte do evento
          </h2>

          <div className="space-y-5">
            {outrosTraslados.map(
              (traslado) => (
                <TrasladoCard
                  key={
                    traslado.id
                  }
                  traslado={
                    traslado
                  }
                />
              ),
            )}
          </div>
        </section>
      )}
    </div>
  );
}

function TrasladoCard({
  traslado,
}: {
  traslado: MeuTraslado;
}) {
  const {
    data,
    horario,
  } = separarDataHora(
    traslado.dataHoraPrevista,
  );

  const voo = [
    traslado.companhiaAerea,
    traslado.numeroVoo,
  ]
    .filter(Boolean)
    .join(" · ");

  const veiculo = [
    traslado.veiculoModelo,
    traslado.veiculoPlaca,
  ]
    .filter(Boolean)
    .join(" · ");

  const emAndamento =
    traslado.status ===
    "EM_ANDAMENTO";

  const concluido =
    traslado.status ===
    "CONCLUIDO";

  const motoristaDesignado =
    Boolean(
      traslado.motoristaNome,
    ) ||
    emAndamento ||
    concluido;

  const cancelado =
    traslado.status ===
    "CANCELADO";

  return (
    <div className="space-y-5">
      <section className="rounded-3xl border border-border bg-surface/60 p-4 shadow-soft">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p className="text-xs text-muted">
              {data}
            </p>

            <p className="mt-1 text-2xl font-semibold tracking-tight text-foreground">
              {horario}
            </p>
          </div>

          <span
            className={`shrink-0 rounded-full border px-3 py-1 text-xs font-medium ${classeStatus(
              traslado.status,
            )}`}
          >
            {formatarStatus(
              traslado.status,
            )}
          </span>
        </div>

        <div className="mt-5 divide-y divide-border">
          <div className="grid grid-cols-[18px_72px_1fr] items-start gap-3 pb-3">
            <MapPin
              size={16}
              strokeWidth={1.8}
              className="mt-0.5 text-muted"
            />

            <p className="text-xs leading-5 text-muted">
              Local
            </p>

            <p className="text-right text-[13px] font-semibold leading-5 text-foreground">
              {traslado.localOrigem}
              {" → "}
              {traslado.localDestino}
            </p>
          </div>

          {voo && (
            <div className="grid grid-cols-[18px_72px_1fr] items-center gap-3 py-3">
              <Plane
                size={16}
                strokeWidth={1.8}
                className="text-muted"
              />

              <p className="text-xs text-muted">
                Voo
              </p>

              <p className="text-right text-sm font-semibold text-foreground">
                {voo}
              </p>
            </div>
          )}

          <div className="grid grid-cols-[18px_72px_1fr] items-center gap-3 py-3">
            <Car
              size={16}
              strokeWidth={1.8}
              className="text-muted"
            />

            <p className="text-xs text-muted">
              Veículo
            </p>

            <p
              className={`text-right text-sm font-semibold ${
                veiculo
                  ? "text-foreground"
                  : "text-muted"
              }`}
            >
              {veiculo || "A definir"}
            </p>
          </div>

          <div className="grid grid-cols-[18px_72px_1fr] items-center gap-3 pt-3">
            <CircleUserRound
              size={16}
              strokeWidth={1.8}
              className="text-muted"
            />

            <p className="text-xs text-muted">
              Motorista
            </p>

            <p
              className={`text-right text-sm font-semibold ${
                traslado.motoristaNome
                  ? "text-foreground"
                  : "text-muted"
              }`}
            >
              {traslado.motoristaNome ||
                "A definir"}
            </p>
          </div>
        </div>

        {traslado.orientacaoHospede && (
          <div className="mt-5 rounded-2xl border border-warning/25 bg-warning/10 p-4">
            <div className="flex items-start gap-3">
              <Info
                size={17}
                strokeWidth={1.9}
                className="mt-0.5 shrink-0 text-warning"
              />

              <div>
                <p className="text-xs font-semibold text-warning">
                  Orientação para embarque
                </p>

                <p className="mt-1 text-sm leading-6 text-foreground">
                  {
                    traslado.orientacaoHospede
                  }
                </p>
              </div>
            </div>
          </div>
        )}
      </section>

      {!cancelado && (
        <section>
          <h2 className="mb-4 text-sm font-semibold text-foreground">
            Acompanhamento
          </h2>

          <div className="space-y-3">
            <EtapaTransporte
              concluida
              label="Aguardando"
            />

            <EtapaTransporte
              concluida={
                motoristaDesignado
              }
              label="Motorista designado"
            />

            <EtapaTransporte
              concluida={
                emAndamento ||
                concluido
              }
              label="Em andamento"
            />

            <EtapaTransporte
              concluida={
                concluido
              }
              label="Concluído"
            />
          </div>
        </section>
      )}
    </div>
  );
}

function EtapaTransporte({
  concluida,
  label,
}: {
  concluida: boolean;
  label: string;
}) {
  return (
    <div className="flex items-center gap-3">
      <span
        className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-full border ${
          concluida
            ? "border-success/40 bg-success/10 text-success"
            : "border-border bg-surface/60 text-muted"
        }`}
      >
        {concluida ? (
          <Check
            size={14}
            strokeWidth={2}
          />
        ) : (
          <Circle
            size={6}
            fill="currentColor"
          />
        )}
      </span>

      <span
        className={`text-sm font-medium ${
          concluida
            ? "text-foreground"
            : "text-muted"
        }`}
      >
        {label}
      </span>
    </div>
  );
}