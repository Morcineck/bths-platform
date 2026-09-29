"use client";

import { useEffect, useState } from "react";
import { QrCode } from "lucide-react";

import {
  buscarMeuCheckIn,
  buscarMeuQrCode,
} from "@/features/hospede/services/hospedeService";
import type {
  MeuCheckIn,
  StatusCheckIn,
} from "@/features/hospede/types/meuCheckIn";

function obterStatusCheckIn(status: StatusCheckIn) {
  switch (status) {
    case "REALIZADO":
      return "Check-in realizado";

    case "NAO_COMPARECEU":
      return "Não compareceu";

    case "PENDENTE":
    default:
      return "Check-in pendente";
  }
}

function formatarDataHora(dataHora: string | null) {
  if (!dataHora) {
    return null;
  }

  const [data, horario] = dataHora.split("T");
  const [ano, mes, dia] = data.split("-");

  return `${dia}/${mes}/${ano} às ${horario.slice(0, 5)}`;
}

export default function CheckInPage() {
  const [checkIn, setCheckIn] =
    useState<MeuCheckIn | null>(null);

  const [qrCodeUrl, setQrCodeUrl] =
    useState<string | null>(null);

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] =
    useState<string | null>(null);

  useEffect(() => {
    let urlTemporaria: string | null = null;
    let ativo = true;

    async function carregarCheckIn() {
      try {
        const [dadosCheckIn, qrCode] =
          await Promise.all([
            buscarMeuCheckIn(),
            buscarMeuQrCode(),
          ]);

        if (!ativo) {
          return;
        }

        setCheckIn(dadosCheckIn);

        if (qrCode) {
          urlTemporaria =
            URL.createObjectURL(qrCode);

          setQrCodeUrl(urlTemporaria);
        }
      } catch {
        if (ativo) {
          setErro(
            "Não foi possível carregar seu check-in.",
          );
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }

    carregarCheckIn();

    return () => {
      ativo = false;

      if (urlTemporaria) {
        URL.revokeObjectURL(
          urlTemporaria,
        );
      }
    };
  }, []);

  if (carregando) {
    return (
      <div className="py-10 text-center text-sm text-zinc-500">
        Carregando seu check-in...
      </div>
    );
  }

  if (erro) {
    return (
      <div className="py-10 text-center">
        <p className="text-sm text-red-500">
          {erro}
        </p>
      </div>
    );
  }

  if (!checkIn) {
    return (
      <div className="py-10 text-center">
        <QrCode className="mx-auto mb-4 h-10 w-10 text-zinc-400" />

        <h1 className="text-lg font-semibold">
          Meu check-in
        </h1>

        <p className="mt-2 text-sm text-zinc-500">
          Ainda não encontramos informações
          de check-in vinculadas à sua conta.
        </p>
      </div>
    );
  }

  const dataHoraCheckIn =
    formatarDataHora(
      checkIn.dataHoraCheckIn,
    );

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm text-zinc-500">
          Sua identificação
        </p>

        <h1 className="text-2xl font-semibold">
          Meu QR Code
        </h1>

        <p className="mt-1 text-sm text-zinc-500">
          {checkIn.hospedeNome}
        </p>
      </div>

      <div className="rounded-2xl border bg-white p-5 shadow-sm">
        <div className="text-center">
          <p className="text-sm text-zinc-500">
            Status do check-in
          </p>

          <p className="mt-1 font-semibold">
            {obterStatusCheckIn(
              checkIn.statusCheckIn,
            )}
          </p>

          {dataHoraCheckIn && (
            <p className="mt-1 text-sm text-zinc-500">
              {dataHoraCheckIn}
            </p>
          )}
        </div>

        {qrCodeUrl ? (
          <div className="mt-6 flex justify-center">
            <div className="rounded-2xl border bg-white p-4">
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img
                src={qrCodeUrl}
                alt="QR Code para check-in"
                className="h-64 w-64"
              />
            </div>
          </div>
        ) : (
          <p className="mt-6 text-center text-sm text-zinc-500">
            Seu QR Code ainda não está disponível.
          </p>
        )}

        <div className="mt-6 rounded-xl bg-zinc-50 p-4">
          <p className="text-sm font-medium">
            Como usar
          </p>

          <p className="mt-1 text-sm leading-6 text-zinc-500">
            Apresente este QR Code para a equipe
            Beat Trips no momento do seu check-in.
            Ele é pessoal e vinculado à sua
            hospedagem.
          </p>
        </div>
      </div>
    </div>
  );
}