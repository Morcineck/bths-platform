"use client";

import Image from "next/image";
import Link from "next/link";

import {
  Building2,
  ChevronLeft,
  Clock3,
  Info,
  MapPin,
  Phone,
  Wifi,
} from "lucide-react";

import {
  useEffect,
  useState,
} from "react";

import {
  buscarMinhaHospedagem,
} from "@/features/hospede/services/hospedeService";

import type {
  MinhaHospedagem,
} from "@/features/hospede/types/minhaHospedagem";

function formatarHorario(
  horario: string | null,
) {
  if (!horario) {
    return "A definir";
  }

  return horario.slice(
    0,
    5,
  );
}

function montarLocalizacao(
  hospedagem: MinhaHospedagem,
) {
  const partes = [
    hospedagem.endereco,
    hospedagem.cidade,
    hospedagem.estado,
  ].filter(Boolean);

  if (partes.length === 0) {
    return "Localização a definir";
  }

  return partes.join(", ");
}

export default function MinhaHospedagemPage() {
  const [
    hospedagem,
    setHospedagem,
  ] =
    useState<MinhaHospedagem | null>(
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
    async function carregarHospedagem() {
      try {
        setErro("");

        const response =
          await buscarMinhaHospedagem();

        setHospedagem(
          response,
        );
      } catch {
        setErro(
          "Não foi possível carregar sua hospedagem.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarHospedagem();
  }, []);

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando sua hospedagem...
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

  if (!hospedagem) {
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
              Hospedagem
            </h1>

            <p className="text-xs text-muted">
              Sua estadia Beat Trips
            </p>
          </div>
        </header>

        <section className="relative overflow-hidden rounded-3xl border border-border bg-surface/60 p-6 text-center shadow-soft">
          <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_16%),transparent_38%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_10%),transparent_42%)]" />

          <div className="relative">
            <span className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/15 text-primary">
              <Building2
                size={26}
                strokeWidth={1.8}
              />
            </span>

            <h2 className="mt-4 text-base font-semibold text-foreground">
              Hospedagem ainda não definida
            </h2>

            <p className="mt-2 text-sm leading-6 text-muted">
              Assim que sua hospedagem for vinculada pela
              equipe Beat Trips, todas as informações
              aparecerão aqui.
            </p>
          </div>
        </section>
      </div>
    );
  }

  const localizacao =
    montarLocalizacao(
      hospedagem,
    );

  const possuiWifi =
    hospedagem.wifiNome ||
    hospedagem.wifiSenha;

  const possuiContato =
    hospedagem.contatoNome ||
    hospedagem.contatoTelefone;

  return (
    <div className="space-y-6 pb-16">
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
            Hospedagem
          </h1>

          <p className="truncate text-xs text-muted">
            {hospedagem.viagemNome}
          </p>
        </div>
      </header>

      <section className="relative overflow-hidden rounded-3xl border border-border bg-surface shadow-soft">
        {hospedagem.imagemUrl ? (
          <div className="relative h-52 w-full overflow-hidden">
            <Image
              src={
                hospedagem.imagemUrl
              }
              alt={
                hospedagem.nome
              }
              fill
              sizes="100vw"
              className="object-cover"
            />

            <div className="absolute inset-0 bg-gradient-to-t from-background via-background/25 to-transparent" />
          </div>
        ) : (
          <div className="relative h-44 overflow-hidden bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_35%),transparent_42%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_24%),transparent_40%)]">
            <div className="absolute inset-0 bg-gradient-to-br from-primary/20 via-transparent to-magenta/15" />

            <div className="relative flex h-full items-center justify-center">
              <Building2
                size={46}
                strokeWidth={1.5}
                className="text-primary/70"
              />
            </div>
          </div>
        )}

        <div className="p-5">
          <div className="flex flex-col items-center text-center">
            <h2 className="text-2xl font-semibold tracking-tight text-foreground">
              {hospedagem.nome}
            </h2>

            <div className="mt-4 flex max-w-[290px] items-center justify-center gap-2">
              <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-md bg-primary/15 text-primary">
                <MapPin
                  size={12}
                  strokeWidth={1.9}
                />
              </span>

              <p className="text-xs font-medium leading-5 text-muted">
                {localizacao}
              </p>
            </div>

            {hospedagem.localizacaoUrl && (
              <div className="mt-5 flex w-full justify-center">
                <a
                  href={hospedagem.localizacaoUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex h-12 w-[78%] items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-primary via-magenta to-electric px-4 text-sm font-semibold text-white shadow-glow transition-transform active:scale-[0.98]"
                >
                  <MapPin
                    size={15}
                    strokeWidth={1.9}
                  />
                  Abrir localização
                </a>
              </div>
            )}
          </div>
        </div>
      </section>

      <section className="space-y-4">
        <div className="flex flex-col items-center text-center">
          <p className="text-[11px] font-semibold uppercase tracking-[0.2em] text-primary">
            Informações da estadia
          </p>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="rounded-2xl border border-border bg-surface/60 p-4 shadow-soft">
            <div className="flex items-center gap-2 text-xs text-muted">
              <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <Clock3
                  size={14}
                  strokeWidth={1.8}
                />
              </span>

              Check-in
            </div>

            <p className="mt-3 text-lg font-semibold tracking-tight text-foreground">
              {formatarHorario(
                hospedagem.horarioCheckIn,
              )}
            </p>
          </div>

          <div className="rounded-2xl border border-border bg-surface/60 p-4 shadow-soft">
            <div className="flex items-center gap-2 text-xs text-muted">
              <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <Clock3
                  size={14}
                  strokeWidth={1.8}
                />
              </span>

              Check-out
            </div>

            <p className="mt-3 text-lg font-semibold tracking-tight text-foreground">
              {formatarHorario(
                hospedagem.horarioCheckOut,
              )}
            </p>
          </div>
        </div>

        <div className="overflow-hidden rounded-3xl border border-border bg-surface/60 shadow-soft">
          <div className="flex items-center gap-3 border-b border-border px-5 py-4">
            <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-electric/10 text-electric">
              <Wifi
                size={18}
                strokeWidth={1.8}
              />
            </span>

            <div>
              <p className="text-sm font-semibold text-foreground">
                Wi-Fi da hospedagem
              </p>

              <p className="mt-0.5 text-xs text-muted">
                Acesso disponível durante sua estadia
              </p>
            </div>
          </div>

          {possuiWifi ? (
            <div className="grid grid-cols-2 divide-x divide-border">
              <div className="px-5 py-4">
                <p className="text-xs text-muted">
                  Rede
                </p>

                <p className="mt-1 break-words text-sm font-semibold text-foreground">
                  {hospedagem.wifiNome ??
                    "A definir"}
                </p>
              </div>

              <div className="px-5 py-4 text-right">
                <p className="text-xs text-muted">
                  Senha
                </p>

                <p
                  onClick={() => {
                    if (
                      hospedagem.wifiSenha
                    ) {
                      void navigator.clipboard.writeText(
                        hospedagem.wifiSenha,
                      );
                    }
                  }}
                  className="mt-1 cursor-pointer break-all text-sm font-semibold text-primary transition-opacity active:opacity-60"
                  title="Clique para copiar a senha"
                >
                  {hospedagem.wifiSenha ??
                    "A definir"}
                </p>
              </div>
            </div>
          ) : (
            <div className="px-5 py-4">
              <p className="text-sm text-muted">
                Os dados da rede ainda não foram cadastrados.
              </p>
            </div>
          )}
        </div>

        {possuiContato && (
          <div className="overflow-hidden rounded-3xl border border-border bg-surface/60 shadow-soft">
            <div className="flex items-center gap-4 p-5">
              <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-primary/15 text-primary">
                <Phone
                  size={19}
                  strokeWidth={1.8}
                />
              </span>

              <div className="min-w-0 flex-1">
                <p className="text-xs text-muted">
                  Precisa de ajuda?
                </p>

                <p className="mt-1 text-sm font-semibold text-foreground">
                  {hospedagem.contatoNome ??
                    "Equipe Beat Trips"}
                </p>

                {hospedagem.contatoTelefone && (
                  <a
                    href={`https://wa.me/55${hospedagem.contatoTelefone.replace(
                      /\D/g,
                      "",
                    )}`}
                    target="_blank"
                    rel="noreferrer"
                    className="mt-1 inline-flex text-sm font-medium text-primary transition-opacity active:opacity-70"
                  >
                    {
                      hospedagem.contatoTelefone
                    }
                  </a>
                )}
              </div>

              {hospedagem.contatoTelefone && (
                <a
                  href={`https://wa.me/55${hospedagem.contatoTelefone.replace(
                    /\D/g,
                    "",
                  )}`}
                  target="_blank"
                  rel="noreferrer"
                  aria-label="Abrir conversa no WhatsApp"
                  className="inline-flex h-9 shrink-0 items-center justify-center rounded-xl border border-primary/30 bg-primary/10 px-3 text-xs font-semibold text-primary transition-colors active:bg-primary/20"
                >
                  WhatsApp
                </a>
              )}
            </div>
          </div>
        )}

        {hospedagem.observacaoPublica && (
          <div className="relative overflow-hidden rounded-3xl border border-primary/20 bg-primary/[0.06] p-5 shadow-soft">
            <div className="pointer-events-none absolute right-0 top-0 h-24 w-24 rounded-full bg-primary/10 blur-3xl" />

            <div className="relative flex items-start gap-3">
              <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                <Info
                  size={17}
                  strokeWidth={1.9}
                />
              </span>

              <div className="min-w-0">
                <p className="text-sm font-semibold text-foreground">
                  Informações importantes
                </p>

                <p className="mt-2 text-sm leading-6 text-muted">
                  {
                    hospedagem.observacaoPublica
                  }
                </p>
              </div>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}