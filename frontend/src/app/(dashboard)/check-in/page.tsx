"use client";

import Link from "next/link";

import {
  useRef,
  useState,
} from "react";

import { Html5Qrcode } from "html5-qrcode";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";

import {
  identificarHospedePorQr,
  realizarCheckIn,
} from "@/features/checkin/services/checkInService";

import type {
  QrCodeCheckInResponse,
} from "@/features/checkin/types/checkin";

export default function CheckInPage() {
    const [
      codigo,
      setCodigo,
    ] = useState("");

    const [
      hospedeIdentificado,
      setHospedeIdentificado,
    ] = useState<QrCodeCheckInResponse | null>(null);

    const [
      observacao,
      setObservacao,
    ] = useState("");

    const [
      identificando,
      setIdentificando,
    ] = useState(false);

    const [
      realizandoCheckIn,
      setRealizandoCheckIn,
    ] = useState(false);

    const [
      erro,
      setErro,
    ] = useState("");

    const [
      sucesso,
      setSucesso,
    ] = useState("");

    const [
      cameraAtiva,
      setCameraAtiva,
    ] = useState(false);

    const [
      erroCamera,
      setErroCamera,
    ] = useState("");

    const scannerRef =
      useRef<Html5Qrcode | null>(null);


async function handleFecharCamera() {
  if (!scannerRef.current) {
    setCameraAtiva(false);
    return;
  }

  try {
    await scannerRef.current.stop();
  } catch {
    // Scanner já pode estar parado.
  } finally {
    scannerRef.current.clear();
    scannerRef.current = null;
    setCameraAtiva(false);
  }
}

async function handleIdentificarHospede() {
  const codigoInformado =
    codigo.trim();

  if (!codigoInformado) {
    setErro(
      "Informe o código do QR Code.",
    );

    setHospedeIdentificado(null);
    setSucesso("");

    return;
  }

  try {
    setIdentificando(true);
    setErro("");
    setSucesso("");
    setHospedeIdentificado(null);

    const hospede =
      await identificarHospedePorQr(
        codigoInformado,
      );

    setHospedeIdentificado(
      hospede,
    );
  } catch (error) {
    setErro(
      error instanceof Error
        ? error.message
        : "Não foi possível identificar o hóspede.",
    );
  } finally {
    setIdentificando(false);
  }
}

async function handleRealizarCheckIn() {
  if (!hospedeIdentificado) {
    return;
  }

  if (
    hospedeIdentificado.quartoId ===
    null
  ) {
    setErro(
      "O hóspede precisa estar alocado em um quarto antes do check-in.",
    );

    return;
  }

  try {
    setRealizandoCheckIn(true);
    setErro("");
    setSucesso("");

    await realizarCheckIn(
      hospedeIdentificado.hospedeId,
      {
        observacao:
          observacao.trim() ||
          undefined,
      },
    );

    setHospedeIdentificado(
      (atual) =>
        atual
          ? {
              ...atual,
              statusCheckIn:
                "REALIZADO",
            }
          : null,
    );

    setSucesso(
      "Check-in realizado com sucesso.",
    );
  } catch (error) {
    setErro(
      error instanceof Error
        ? error.message
        : "Não foi possível realizar o check-in.",
    );
  } finally {
    setRealizandoCheckIn(false);
  }
}

async function handleAbrirCamera() {
  try {
    setErroCamera("");
    setErro("");
    setSucesso("");

    const cameras =
      await Html5Qrcode.getCameras();

    if (cameras.length === 0) {
      setErroCamera(
        "Nenhuma câmera foi encontrada neste dispositivo.",
      );

      return;
    }

    setCameraAtiva(true);

    await new Promise<void>(
      (resolve) => {
        requestAnimationFrame(() => {
          requestAnimationFrame(() => {
            resolve();
          });
        });
      },
    );

    const scanner =
      new Html5Qrcode(
        "reader-check-in",
      );

    scannerRef.current =
      scanner;

    const cameraId =
      cameras[0].id;

    await scanner.start(
      cameraId,
      {
        fps: 10,
        qrbox: {
          width: 250,
          height: 250,
        },
      },
      async (
        decodedText,
      ) => {
        setCodigo(
          decodedText,
        );

        await handleFecharCamera();

        try {
          setIdentificando(true);
          setErro("");
          setSucesso("");
          setHospedeIdentificado(null);

          const hospede =
            await identificarHospedePorQr(
              decodedText,
            );

          setHospedeIdentificado(
            hospede,
          );
        } catch (error) {
          setErro(
            error instanceof Error
              ? error.message
              : "Não foi possível identificar o hóspede.",
          );
        } finally {
          setIdentificando(false);
        }
      },
      () => {
        // Ignora leituras intermediárias sem QR válido.
      },
    );
  } catch (error) {
    console.error(
      "Erro ao acessar câmera:",
      error,
    );

    setErroCamera(
      "Não foi possível acessar a câmera. Verifique se ela está disponível e não está sendo usada por outro aplicativo.",
    );

    setCameraAtiva(false);

    scannerRef.current =
      null;
  }
}

return (
  <div className="space-y-8">
    <PageHeader
      title="Check-in"
      description="Identifique o hóspede pelo código do QR Code antes de confirmar o check-in."
    />

    <Card className="space-y-5">
      <div>
        <label
          htmlFor="codigo-check-in"
          className="mb-2 block text-sm font-medium text-foreground"
        >
          Código do QR Code
        </label>

        <div className="flex flex-col gap-3 sm:flex-row">
          <input
            id="codigo-check-in"
            type="text"
            value={codigo}
            onChange={(event) =>
              setCodigo(
                event.target.value,
              )
            }
            onKeyDown={(event) => {
              if (
                event.key === "Enter"
              ) {
                handleIdentificarHospede();
              }
            }}
            placeholder="Digite ou cole o código"
            className="h-12 flex-1 rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
          />

          <button
            type="button"
            onClick={
              handleIdentificarHospede
            }
            disabled={
              identificando ||
              !codigo.trim()
            }
            className="inline-flex h-12 items-center justify-center rounded-xl bg-primary px-5 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {identificando
              ? "Localizando..."
              : "Localizar hóspede"}
          </button>
        </div>
      </div>

      <div className="border-t border-border pt-5">
        <div className="flex flex-col gap-3 sm:flex-row">
          {!cameraAtiva ? (
            <button
              type="button"
              onClick={
                handleAbrirCamera
              }
              className="inline-flex h-11 items-center justify-center rounded-xl border border-primary px-5 text-sm font-semibold text-primary transition-opacity hover:opacity-80"
            >
              Ler QR Code
            </button>
          ) : (
            <button
              type="button"
              onClick={
                handleFecharCamera
              }
              className="inline-flex h-11 items-center justify-center rounded-xl border border-red-500/40 px-5 text-sm font-medium text-red-400 transition-colors hover:border-red-400 hover:text-red-300"
            >
              Fechar câmera
            </button>
          )}
        </div>

        {erroCamera && (
          <p className="mt-3 text-sm text-red-400">
            {erroCamera}
          </p>
        )}

        {cameraAtiva && (
          <div className="mt-5 overflow-hidden rounded-xl border border-border bg-background/40 p-3">
            <div
              id="reader-check-in"
              className="w-full"
            />
          </div>
        )}
      </div>
    </Card>

    {erro && (
      <p
        role="alert"
        className="text-sm text-red-400"
      >
        {erro}
      </p>
    )}

    {sucesso && (
      <p className="text-sm text-green-400">
        {sucesso}
      </p>
    )}

    {hospedeIdentificado && (
      <Card className="space-y-6">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <p className="text-sm text-muted">
              Hóspede identificado
            </p>

            <h2 className="mt-1 text-xl font-semibold text-foreground">
              {
                hospedeIdentificado.hospedeNome
              }
            </h2>
          </div>

          <span
            className={
              hospedeIdentificado.statusCheckIn ===
              "REALIZADO"
                ? "inline-flex rounded-full border border-green-400/40 px-3 py-1 text-xs font-semibold text-green-400"
                : hospedeIdentificado.statusCheckIn ===
                    "NAO_COMPARECEU"
                  ? "inline-flex rounded-full border border-red-400/40 px-3 py-1 text-xs font-semibold text-red-400"
                  : "inline-flex rounded-full border border-primary/40 px-3 py-1 text-xs font-semibold text-primary"
            }
          >
            {hospedeIdentificado.statusCheckIn ===
            "REALIZADO"
              ? "Check-in realizado"
              : hospedeIdentificado.statusCheckIn ===
                  "NAO_COMPARECEU"
                ? "Não compareceu"
                : "Pendente"}
          </span>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <div className="rounded-xl border border-border bg-background/40 p-4">
            <p className="text-sm text-muted">
              Viagem
            </p>

            <p className="mt-1 font-medium text-foreground">
              {
                hospedeIdentificado.viagemNome
              }
            </p>
          </div>

          <div className="rounded-xl border border-border bg-background/40 p-4">
            <p className="text-sm text-muted">
              Quarto
            </p>

            <p className="mt-1 font-medium text-foreground">
              {hospedeIdentificado.quartoNome ??
                "Não alocado"}
            </p>
          </div>
        </div>

        {hospedeIdentificado.quartoId ===
          null &&
          hospedeIdentificado.statusCheckIn ===
            "PENDENTE" && (
            <div className="rounded-xl border border-red-500/30 bg-red-500/5 p-4">
              <p className="font-medium text-red-400">
                Check-in indisponível
              </p>

              <p className="mt-2 text-sm text-muted">
                O hóspede precisa estar alocado em um quarto antes do check-in.
              </p>

              <Link
                href={`/hospedes/${hospedeIdentificado.hospedeId}`}
                className="mt-4 inline-flex text-sm font-medium text-primary transition-opacity hover:opacity-80"
              >
                Abrir hóspede
              </Link>
            </div>
          )}

        {hospedeIdentificado.statusCheckIn ===
          "PENDENTE" &&
          hospedeIdentificado.quartoId !==
            null && (
            <div className="space-y-4 border-t border-border pt-5">
              <div>
                <label
                  htmlFor="observacao-check-in"
                  className="mb-2 block text-sm font-medium text-foreground"
                >
                  Observação
                </label>

                <textarea
                  id="observacao-check-in"
                  value={observacao}
                  onChange={(event) =>
                    setObservacao(
                      event.target.value,
                    )
                  }
                  rows={3}
                  placeholder="Observação opcional sobre o check-in."
                  className="w-full resize-none rounded-xl border border-border bg-surface px-4 py-3 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
                />
              </div>

              <div className="flex flex-col gap-3 sm:flex-row">
                <button
                  type="button"
                  onClick={
                    handleRealizarCheckIn
                  }
                  disabled={
                    realizandoCheckIn
                  }
                  className="inline-flex h-11 items-center justify-center rounded-xl bg-primary px-5 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {realizandoCheckIn
                    ? "Realizando..."
                    : "Confirmar check-in"}
                </button>

                <Link
                  href={`/hospedes/${hospedeIdentificado.hospedeId}`}
                  className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
                >
                  Ver hóspede
                </Link>
              </div>
            </div>
          )}

        {hospedeIdentificado.statusCheckIn !==
          "PENDENTE" && (
          <div className="border-t border-border pt-5">
            <Link
              href={`/hospedes/${hospedeIdentificado.hospedeId}`}
              className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
            >
              Ver hóspede
            </Link>
          </div>
        )}
      </Card>
    )}
    </div>
    );
    }