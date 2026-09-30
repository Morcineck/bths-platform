"use client";

import Link from "next/link";

import {
  ChevronLeft,
  Maximize2,
  QrCode,
  X,
} from "lucide-react";

import {
  useEffect,
  useState,
} from "react";

import {
  buscarMeuCheckIn,
  buscarMeuQrCode,
} from "@/features/hospede/services/hospedeService";

import type {
  MeuCheckIn,
  StatusCheckIn,
} from "@/features/hospede/types/meuCheckIn";

function obterStatusCheckIn(
  status: StatusCheckIn,
) {
  switch (status) {
    case "REALIZADO":
      return "Check-in realizado";

    case "NAO_COMPARECEU":
      return "Não compareceu";

    case "PENDENTE":
    default:
      return "Check-in disponível";
  }
}

function obterClasseStatus(
  status: StatusCheckIn,
) {
  switch (status) {
    case "REALIZADO":
      return "border-success/30 bg-success/10 text-success";

    case "NAO_COMPARECEU":
      return "border-danger/30 bg-danger/10 text-danger";

    case "PENDENTE":
    default:
      return "border-primary/30 bg-primary/10 text-primary";
  }
}

function formatarDataHora(
  dataHora: string | null,
) {
  if (!dataHora) {
    return null;
  }

  const [data, horario] =
    dataHora.split("T");

  const [ano, mes, dia] =
    data.split("-");

  return `${dia}/${mes}/${ano} às ${horario.slice(
    0,
    5,
  )}`;
}

export default function CheckInPage() {
  const [
    checkIn,
    setCheckIn,
  ] = useState<MeuCheckIn | null>(
    null,
  );

  const [
    qrCodeUrl,
    setQrCodeUrl,
  ] = useState<string | null>(
    null,
  );

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState<string | null>(
    null,
  );

  const [
    qrAmpliado,
    setQrAmpliado,
  ] = useState(false);

  useEffect(() => {
    let urlTemporaria:
      | string
      | null = null;

    let ativo = true;

    async function carregarCheckIn() {
      try {
        setErro(null);

        const [
          dadosCheckIn,
          qrCode,
        ] = await Promise.all([
          buscarMeuCheckIn(),
          buscarMeuQrCode(),
        ]);

        if (!ativo) {
          return;
        }

        setCheckIn(
          dadosCheckIn,
        );

        if (qrCode) {
          urlTemporaria =
            URL.createObjectURL(
              qrCode,
            );

          setQrCodeUrl(
            urlTemporaria,
          );
        }
      } catch {
        if (ativo) {
          setErro(
            "Não foi possível carregar seu check-in.",
          );
        }
      } finally {
        if (ativo) {
          setCarregando(
            false,
          );
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

  useEffect(() => {
    if (!qrAmpliado) {
      return;
    }

    const overflowAnterior =
      document.body.style.overflow;

    document.body.style.overflow =
      "hidden";

    function fecharComEscape(
      event: KeyboardEvent,
    ) {
      if (
        event.key === "Escape"
      ) {
        setQrAmpliado(
          false,
        );
      }
    }

    window.addEventListener(
      "keydown",
      fecharComEscape,
    );

    return () => {
      document.body.style.overflow =
        overflowAnterior;

      window.removeEventListener(
        "keydown",
        fecharComEscape,
      );
    };
  }, [qrAmpliado]);

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando seu QR Code...
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

  if (!checkIn) {
    return (
      <div className="space-y-6">
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
              Meu QR Code
            </h1>

            <p className="text-xs text-muted">
              Passe digital Beat Trips
            </p>
          </div>
        </header>

        <section className="rounded-3xl border border-border bg-surface/55 p-6 text-center shadow-soft">
          <span className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/15 text-primary">
            <QrCode
              size={26}
              strokeWidth={1.8}
            />
          </span>

          <h2 className="mt-4 font-semibold text-foreground">
            QR Code indisponível
          </h2>

          <p className="mt-2 text-sm leading-6 text-muted">
            Ainda não encontramos informações de
            check-in vinculadas à sua conta.
          </p>
        </section>
      </div>
    );
  }

  const dataHoraCheckIn =
    formatarDataHora(
      checkIn.dataHoraCheckIn,
    );

  return (
    <>
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
              Meu QR Code
            </h1>

            <p className="truncate text-xs text-muted">
              Passe digital Beat Trips
            </p>
          </div>
        </header>

        <section className="relative overflow-hidden rounded-3xl border border-border bg-surface/60 p-5 shadow-soft">
          <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_18%),transparent_38%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_12%),transparent_42%)]" />

          <div className="relative">
            <div className="flex items-center gap-3">
              <span className="gradient-brand flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl text-lg font-semibold text-white shadow-glow">
                {checkIn.hospedeNome
                  .trim()
                  .charAt(0)
                  .toUpperCase()}
              </span>

              <div className="min-w-0">
                <p className="text-xs text-muted">
                  Hóspede
                </p>

                <p className="truncate text-base font-semibold text-foreground">
                  {checkIn.hospedeNome}
                </p>
              </div>
            </div>

            <div className="mt-6 flex justify-center">
              {qrCodeUrl ? (
                <button
                  type="button"
                  onClick={() =>
                    setQrAmpliado(
                      true,
                    )
                  }
                  aria-label="Ampliar QR Code"
                  className="rounded-3xl bg-white p-4 shadow-glow transition-transform active:scale-[0.98]"
                >
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img
                    src={qrCodeUrl}
                    alt="QR Code para check-in"
                    className="h-56 w-56"
                  />
                </button>
              ) : (
                <div className="flex h-60 w-60 flex-col items-center justify-center rounded-3xl border border-border bg-background/50 p-6 text-center">
                  <QrCode
                    size={42}
                    strokeWidth={1.6}
                    className="text-muted"
                  />

                  <p className="mt-4 text-sm text-muted">
                    Seu QR Code ainda não está disponível.
                  </p>
                </div>
              )}
            </div>

            <div className="mt-5 flex justify-center">
              <span
                className={`inline-flex rounded-full border px-3 py-1.5 text-xs font-semibold ${obterClasseStatus(
                  checkIn.statusCheckIn,
                )}`}
              >
                {obterStatusCheckIn(
                  checkIn.statusCheckIn,
                )}
              </span>
            </div>

            {dataHoraCheckIn && (
              <p className="mt-3 text-center text-sm font-medium text-foreground">
                {dataHoraCheckIn}
              </p>
            )}

            <div className="mt-5 border-t border-border pt-5 text-center">
              <p className="text-sm leading-6 text-muted">
                Apresente este QR Code à equipe Beat Trips
                  ao chegar à hospedagem.
              </p>

              <p className="mt-2 text-xs leading-5 text-muted">
                Este passe é pessoal e intransferível.
              </p>
            </div>

            {qrCodeUrl && (
              <button
                type="button"
                onClick={() =>
                  setQrAmpliado(
                    true,
                  )
                }
                className="mt-5 flex h-10 w-full items-center justify-center gap-2 rounded-2xl border border-border bg-background/40 text-sm font-semibold text-foreground transition-all active:scale-[0.98]"
              >
                <Maximize2
                  size={17}
                  strokeWidth={1.9}
                />

                Aumentar QR Code
              </button>
            )}
          </div>
        </section>
      </div>

      {qrAmpliado &&
        qrCodeUrl && (
          <div
            role="dialog"
            aria-modal="true"
            aria-label="QR Code ampliado"
            className="fixed inset-0 z-[100] flex flex-col items-center justify-center bg-background/95 px-6 backdrop-blur-xl"
          >
            <button
              type="button"
              onClick={() =>
                setQrAmpliado(
                  false,
                )
              }
              aria-label="Fechar QR Code ampliado"
              className="absolute right-5 top-5 flex h-11 w-11 items-center justify-center rounded-full border border-border bg-surface text-foreground active:scale-95"
            >
              <X
                size={21}
                strokeWidth={1.9}
              />
            </button>

            <div className="w-full max-w-sm text-center">
              <p className="text-xs font-medium text-primary">
                Beat Trips
              </p>

              <h2 className="mt-1 text-xl font-semibold text-foreground">
                Meu QR Code
              </h2>

              <p className="mt-1 text-sm text-muted">
                {checkIn.hospedeNome}
              </p>

              <div className="mt-7 rounded-3xl bg-white p-5 shadow-glow">
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img
                  src={qrCodeUrl}
                  alt="QR Code ampliado para check-in"
                  className="aspect-square w-full"
                />
              </div>

              <p className="mt-6 text-sm leading-6 text-muted">
                Aproxime esta tela do leitor da equipe Beat Trips.
              </p>

              <button
                type="button"
                onClick={() =>
                  setQrAmpliado(
                    false,
                  )
                }
                className="mt-6 h-12 w-full rounded-2xl border border-border bg-surface text-sm font-semibold text-foreground active:scale-[0.98]"
              >
                Fechar
              </button>
            </div>
          </div>
        )}
    </>
  );
}