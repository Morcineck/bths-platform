"use client";

import Link from "next/link";

import {
  useRef,
  useState,
} from "react";

import { Html5Qrcode } from "html5-qrcode";

import {
  Camera,
  CheckCircle2,
  Clock3,
  QrCode,
  Search,
  UserRound,
  UserX,
  X,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

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
  ] =
    useState<QrCodeCheckInResponse | null>(
      null,
    );

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

  const checkInRealizado =
    hospedeIdentificado
      ?.statusCheckIn ===
    "REALIZADO";

  const naoCompareceu =
    hospedeIdentificado
      ?.statusCheckIn ===
    "NAO_COMPARECEU";

  return (
    <div className="space-y-6">
      <PageHeader
        title="Check-in"
        description="Identifique o hóspede pelo QR Code e confirme a entrada."
      />

      <section className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft">
        {!cameraAtiva ? (
          <button
            type="button"
            onClick={
              handleAbrirCamera
            }
            className="gradient-brand flex h-14 w-full items-center justify-center gap-3 rounded-2xl text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
          >
            <QrCode
              size={22}
              strokeWidth={2}
            />

            Ler QR Code
          </button>
        ) : (
          <button
            type="button"
            onClick={
              handleFecharCamera
            }
            className="flex h-11 w-full items-center justify-center gap-2 rounded-xl border border-danger/30 bg-danger/5 text-sm font-medium text-danger transition-colors hover:bg-danger/10"
          >
            <X
              size={18}
              strokeWidth={2}
            />

            Fechar câmera
          </button>
        )}

        {erroCamera && (
          <div className="mt-3 rounded-xl border border-danger/25 bg-danger/10 px-3 py-2.5 text-xs leading-5 text-danger">
            {erroCamera}
          </div>
        )}

        {cameraAtiva && (
          <div className="mt-4 overflow-hidden rounded-2xl border border-border bg-background/40 p-2">
            <div
              id="reader-check-in"
              className="w-full"
            />
          </div>
        )}

        <div className="my-4 flex items-center gap-3">
          <div className="h-px flex-1 bg-border" />

          <span className="text-[11px] font-medium uppercase tracking-[0.12em] text-muted">
            ou
          </span>

          <div className="h-px flex-1 bg-border" />
        </div>

        <div>
          <label
            htmlFor="codigo-check-in"
            className="mb-2 block text-xs font-medium text-muted"
          >
            Código do QR Code
          </label>

          <div className="flex flex-col gap-2 sm:flex-row">
            <div className="relative flex-1">
              <Camera
                size={17}
                strokeWidth={1.9}
                className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-muted"
              />

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
                className="h-11 w-full rounded-xl border border-border bg-background/30 pl-11 pr-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
              />
            </div>

            <button
              type="button"
              onClick={
                handleIdentificarHospede
              }
              disabled={
                identificando ||
                !codigo.trim()
              }
              className="inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <Search
                size={17}
                strokeWidth={1.9}
              />

              {identificando
                ? "Localizando..."
                : "Localizar"}
            </button>
          </div>
        </div>
      </section>

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {sucesso && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {sucesso}
        </div>
      )}

      {hospedeIdentificado && (
        <section className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
              <UserRound
                size={20}
                strokeWidth={1.9}
              />
            </span>

            <div className="min-w-0 flex-1">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div className="min-w-0">
                  <p className="text-[11px] text-muted">
                    Hóspede identificado
                  </p>

                  <h2 className="mt-1 truncate text-base font-semibold text-foreground">
                    {
                      hospedeIdentificado.hospedeNome
                    }
                  </h2>
                </div>

                <span
                  className={
                    checkInRealizado
                      ? "inline-flex w-fit items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                      : naoCompareceu
                        ? "inline-flex w-fit items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                        : "inline-flex w-fit items-center gap-1.5 rounded-full border border-warning/30 bg-warning/10 px-2.5 py-1 text-[11px] font-medium text-warning"
                  }
                >
                  {checkInRealizado ? (
                    <CheckCircle2
                      size={13}
                      strokeWidth={2}
                    />
                  ) : naoCompareceu ? (
                    <UserX
                      size={13}
                      strokeWidth={2}
                    />
                  ) : (
                    <Clock3
                      size={13}
                      strokeWidth={2}
                    />
                  )}

                  {checkInRealizado
                    ? "Check-in realizado"
                    : naoCompareceu
                      ? "Não compareceu"
                      : "Pendente"}
                </span>
              </div>

              <div className="mt-4 grid gap-2 sm:grid-cols-2">
                <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
                  <p className="text-[11px] text-muted">
                    Viagem
                  </p>

                  <p className="mt-1 truncate text-sm font-medium text-foreground">
                    {
                      hospedeIdentificado.viagemNome
                    }
                  </p>
                </div>

                <div className="rounded-xl border border-border bg-background/25 px-3 py-2.5">
                  <p className="text-[11px] text-muted">
                    Quarto
                  </p>

                  <p className="mt-1 truncate text-sm font-medium text-foreground">
                    {hospedeIdentificado.quartoNome ??
                      "Não alocado"}
                  </p>
                </div>
              </div>
            </div>
          </div>

          {hospedeIdentificado.quartoId ===
            null &&
            hospedeIdentificado.statusCheckIn ===
              "PENDENTE" && (
              <div className="mt-4 rounded-xl border border-danger/25 bg-danger/10 p-3">
                <p className="text-sm font-medium text-danger">
                  Check-in indisponível
                </p>

                <p className="mt-1 text-xs leading-5 text-muted">
                  O hóspede precisa estar alocado em um quarto antes do check-in.
                </p>

                <Link
                  href={`/hospedes/${hospedeIdentificado.hospedeId}`}
                  className="mt-3 inline-flex text-xs font-medium text-primary transition-opacity hover:opacity-80"
                >
                  Abrir hóspede
                </Link>
              </div>
            )}

          {hospedeIdentificado.statusCheckIn ===
            "PENDENTE" &&
            hospedeIdentificado.quartoId !==
              null && (
              <div className="mt-4 border-t border-border pt-4">
                <label
                  htmlFor="observacao-check-in"
                  className="mb-2 block text-xs font-medium text-muted"
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
                  className="w-full resize-none rounded-xl border border-border bg-background/30 px-3 py-2.5 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
                />

                <div className="mt-3 flex flex-col gap-2 sm:flex-row">
                  <button
                    type="button"
                    onClick={
                      handleRealizarCheckIn
                    }
                    disabled={
                      realizandoCheckIn
                    }
                    className="inline-flex h-11 flex-1 items-center justify-center gap-2 rounded-xl bg-success px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    <CheckCircle2
                      size={17}
                      strokeWidth={2}
                    />

                    {realizandoCheckIn
                      ? "Realizando..."
                      : "Confirmar check-in"}
                  </button>

                  <Link
                    href={`/hospedes/${hospedeIdentificado.hospedeId}`}
                    className="inline-flex h-11 flex-1 items-center justify-center rounded-xl border border-border px-4 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
                  >
                    Ver hóspede
                  </Link>
                </div>
              </div>
            )}

          {hospedeIdentificado.statusCheckIn !==
            "PENDENTE" && (
            <div className="mt-4 border-t border-border pt-4">
              <Link
                href={`/hospedes/${hospedeIdentificado.hospedeId}`}
                className="inline-flex h-10 items-center justify-center rounded-xl border border-border px-4 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
              >
                Ver hóspede
              </Link>
            </div>
          )}
        </section>
      )}
    </div>
  );
}