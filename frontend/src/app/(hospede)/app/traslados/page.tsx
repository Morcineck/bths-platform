"use client";

import { useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";

import {
  buscarMeusTraslados,
} from "@/features/hospede/services/hospedeService";

import type {
  MeuTraslado,
} from "@/features/hospede/types/meuTraslado";

function formatarDataHora(dataHora: string) {
  const [data, horario] = dataHora.split("T");
  const [ano, mes, dia] = data.split("-");

  return `${dia}/${mes}/${ano} às ${horario.slice(0, 5)}`;
}

function formatarTipo(tipo: string) {
  switch (tipo) {
    case "AEROPORTO_PARA_HOSPEDAGEM":
      return "Aeroporto → Hospedagem";

    case "HOSPEDAGEM_PARA_AEROPORTO":
      return "Hospedagem → Aeroporto";

    case "HOSPEDAGEM_PARA_EVENTO":
      return "Hospedagem → Evento";

    case "EVENTO_PARA_HOSPEDAGEM":
      return "Evento → Hospedagem";

    default:
      return tipo
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/^./, (letra) =>
          letra.toUpperCase(),
        );
  }
}

function formatarStatus(status: string) {
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

export default function AppTrasladosPage() {
  const [
    traslados,
    setTraslados,
  ] = useState<MeuTraslado[]>([]);

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
      } catch {
        setErro(
          "Não foi possível carregar seus traslados.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarTraslados();
  }, []);

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando seus traslados...
      </p>
    );
  }

  if (erro) {
    return (
      <p
        role="alert"
        className="text-sm text-red-400"
      >
        {erro}
      </p>
    );
  }

  if (traslados.length === 0) {
    return (
      <div className="space-y-4">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">
            Meus traslados
          </h1>

          <p className="mt-2 text-sm text-muted">
            Consulte aqui os traslados vinculados à sua viagem.
          </p>
        </div>

        <Card>
          <p className="font-medium text-foreground">
            Nenhum traslado disponível.
          </p>

          <p className="mt-2 text-sm text-muted">
            Assim que um traslado for vinculado à sua viagem, as informações aparecerão aqui.
          </p>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm text-muted">
          Sua mobilidade
        </p>

        <h1 className="mt-1 text-2xl font-semibold text-foreground">
          Meus traslados
        </h1>

        <p className="mt-2 text-sm text-muted">
          Acompanhe os horários e informações dos seus deslocamentos.
        </p>
      </div>

      <div className="space-y-4">
        {traslados.map((traslado) => (
          <Card key={traslado.id}>
            <p className="text-sm text-muted">
              Traslado
            </p>

            <p className="mt-1 text-lg font-semibold text-foreground">
              {formatarTipo(traslado.tipo)}
            </p>

            <p className="mt-5 text-sm text-muted">
              Status
            </p>

            <p className="mt-1 font-medium text-foreground">
              {formatarStatus(traslado.status)}
            </p>

            <p className="mt-5 text-sm text-muted">
              Data e horário
            </p>

            <p className="mt-1 font-medium text-foreground">
              {formatarDataHora(
                traslado.dataHoraPrevista,
              )}
            </p>

            <p className="mt-5 text-sm text-muted">
              Origem
            </p>

            <p className="mt-1 font-medium text-foreground">
              {traslado.localOrigem}
            </p>

            <p className="mt-5 text-sm text-muted">
              Destino
            </p>

            <p className="mt-1 font-medium text-foreground">
              {traslado.localDestino}
            </p>

            {traslado.aeroporto && (
              <>
                <p className="mt-5 text-sm text-muted">
                  Aeroporto
                </p>

                <p className="mt-1 font-medium text-foreground">
                  {traslado.aeroporto}
                </p>
              </>
            )}

            {(traslado.numeroVoo ||
              traslado.companhiaAerea) && (
              <>
                <p className="mt-5 text-sm text-muted">
                  Voo
                </p>

                <p className="mt-1 font-medium text-foreground">
                  {[
                    traslado.companhiaAerea,
                    traslado.numeroVoo,
                  ]
                    .filter(Boolean)
                    .join(" • ")}
                </p>
              </>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
}