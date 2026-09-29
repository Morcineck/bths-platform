"use client";

import { useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";

import {
  buscarMinhaViagem,
} from "@/features/hospede/services/hospedeService";

import type {
  MinhaViagem,
} from "@/features/hospede/types/minhaViagem";

function formatarData(data: string) {
  const [ano, mes, dia] = data.split("-");

  return `${dia}/${mes}/${ano}`;
}

function formatarStatus(status: string) {
  switch (status) {
    case "PLANEJADA":
      return "Viagem planejada";

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

export default function AppViagemPage() {
  const [
    minhaViagem,
    setMinhaViagem,
  ] = useState<MinhaViagem | null>(
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
    async function carregarMinhaViagem() {
      try {
        setErro("");

        const response =
          await buscarMinhaViagem();

        setMinhaViagem(
          response,
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
      <p className="text-sm text-muted">
        Carregando sua viagem...
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

  if (!minhaViagem) {
    return (
      <div className="space-y-4">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">
            Minha viagem
          </h1>

          <p className="mt-2 text-sm text-muted">
            Consulte aqui as informações da sua experiência Beat Trips.
          </p>
        </div>

        <Card>
          <p className="font-medium text-foreground">
            Nenhuma viagem disponível.
          </p>

          <p className="mt-2 text-sm text-muted">
            Assim que sua hospedagem estiver vinculada à sua conta, as informações aparecerão aqui.
          </p>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm text-muted">
          Sua experiência
        </p>

        <h1 className="mt-1 text-2xl font-semibold text-foreground">
          {minhaViagem.viagemNome}
        </h1>

        <p className="mt-2 text-sm text-muted">
          {minhaViagem.evento}
        </p>
      </div>

      <Card>
        <p className="text-sm text-muted">
          Status
        </p>

        <p className="mt-1 font-medium text-foreground">
          {formatarStatus(minhaViagem.status)}
        </p>

        <p className="mt-5 text-sm text-muted">
          Período
        </p>

        <p className="mt-1 font-medium text-foreground">
          {formatarData(minhaViagem.dataInicio)}
          {" até "}
          {formatarData(minhaViagem.dataFim)}
        </p>

        <p className="mt-5 text-sm text-muted">
          Hospedagem
        </p>

        <p className="mt-1 font-medium text-foreground">
          {minhaViagem.endereco}
        </p>

        <p className="mt-1 text-sm text-muted">
          {minhaViagem.cidade}
          {" - "}
          {minhaViagem.estado}
        </p>
      </Card>

      <Card>
        <p className="text-sm text-muted">
          Minha hospedagem
        </p>

        <p className="mt-4 text-sm text-muted">
          Meu quarto
        </p>

        {minhaViagem.quartoNome ? (
          <p className="mt-2 text-lg font-semibold text-foreground">
            {minhaViagem.quartoNome}
          </p>
        ) : (
          <>
            <p className="mt-2 font-medium text-foreground">
              Quarto ainda não definido
            </p>

            <p className="mt-2 text-sm text-muted">
              Assim que sua acomodação for definida pela Beat Trips, ela aparecerá aqui.
            </p>
          </>
        )}
      </Card>
    </div>
  );
}