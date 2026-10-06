"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  BedDouble,
  Bell,
  Building2,
  Bus,
  CalendarDays,
  ChevronRight,
  LifeBuoy,
  MapPin,
  QrCode,
} from "lucide-react";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import type {
  UsuarioAutenticado,
} from "@/features/auth/types/auth";

import {
  buscarMeusAvisos,
  buscarMinhaViagem,
} from "@/features/hospede/services/hospedeService";

import type {
  MeuAviso,
} from "@/features/hospede/types/meuAviso";

import type {
  MinhaViagem,
} from "@/features/hospede/types/minhaViagem";

function obterPrimeiroNome(
  nome: string | undefined,
) {
  if (!nome) {
    return "";
  }

  return nome.trim().split(" ")[0];
}

function formatarData(
  data: string,
) {
  const [ano, mes, dia] =
    data.split("-");

  return `${dia}/${mes}/${ano}`;
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

function obterClasseStatusViagem(
  status: string,
) {
  switch (status) {
    case "PLANEJADA":
      return "border-primary/30 bg-primary/10 text-primary";

    case "EM_ANDAMENTO":
      return "border-success/30 bg-success/10 text-success";

    case "FINALIZADA":
      return "border-border bg-background/40 text-muted";

    case "CANCELADA":
      return "border-danger/30 bg-danger/10 text-danger";

    default:
      return "border-border bg-background/40 text-muted";
  }
}

function calcularDiasAteViagem(
  dataInicio: string,
) {
  const hoje = new Date();

  hoje.setHours(
    0,
    0,
    0,
    0,
  );

  const [
    ano,
    mes,
    dia,
  ] = dataInicio
    .split("-")
    .map(Number);

  const inicioViagem = new Date(
    ano,
    mes - 1,
    dia,
  );

  const diferenca =
    inicioViagem.getTime() -
    hoje.getTime();

  return Math.ceil(
    diferenca /
      (1000 * 60 * 60 * 24),
  );
}

const atalhos = [
  {
    href: "/app/quarto",
    label: "Meu quarto",
    icon: BedDouble,
  },
  {
    href: "/app/traslados",
    label: "Transporte",
    icon: Bus,
  },
  {
    href: "/app/check-in",
    label: "QR Code",
    icon: QrCode,
  },
  {
    href: "/app/hospedagem",
    label: "Hospedagem",
    icon: Building2,
  },
  {
    href: "/app/avisos",
    label: "Avisos",
    icon: Bell,
  },
  {
    href: "/app/perfil",
    label: "Suporte",
    icon: LifeBuoy,
  },
] as const;

export default function AppHospedePage() {
  const [
    usuario,
    setUsuario,
  ] =
    useState<UsuarioAutenticado | null>(
      null,
    );

  const [
    viagem,
    setViagem,
  ] =
    useState<MinhaViagem | null>(
      null,
    );

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
    async function carregarHome() {
      try {
        setErro("");

        const [
          usuarioAutenticado,
          minhaViagem,
          meusAvisos,
        ] = await Promise.all([
          buscarUsuarioAutenticado(),
          buscarMinhaViagem(),
          buscarMeusAvisos(),
        ]);

        setUsuario(
          usuarioAutenticado,
        );

        setViagem(
          minhaViagem,
        );

        setAvisos(
          meusAvisos,
        );
      } catch {
        setErro(
          "Não foi possível carregar suas informações.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarHome();
  }, []);

  if (carregando) {
    return (
      <div className="py-10">
        <p className="text-sm text-muted">
          Carregando sua experiência...
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

  const primeiroNome =
    obterPrimeiroNome(
      usuario?.nome,
    );

  const diasAteViagem =
    viagem
      ? calcularDiasAteViagem(
          viagem.dataInicio,
        )
      : 0;

  return (
    <div className="space-y-6">
      <header className="flex items-start justify-between gap-4 pt-1">
        <div>
          <p className="text-xs font-medium text-primary">
            Beat Trips
          </p>

          <h1 className="mt-1 text-xl font-semibold tracking-tight text-foreground">
            Olá, {primeiroNome} 👋
          </h1>

          <p className="mt-2 text-sm text-muted">
            Sua experiência começa aqui.
          </p>
        </div>

        <Link
          href="/app/avisos"
          aria-label="Avisos"
          className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full border border-border bg-surface/60 text-foreground transition-colors hover:border-primary hover:text-primary"
        >
          <Bell
            size={20}
            strokeWidth={1.9}
          />
        </Link>
      </header>

      {!viagem ? (
        <section className="glass rounded-3xl p-5 shadow-soft">
          <p className="font-semibold text-foreground">
            Sua viagem ainda não foi vinculada
          </p>

          <p className="mt-2 text-sm leading-6 text-muted">
            Assim que a equipe Beat Trips concluir essa etapa, sua hospedagem, transporte, quarto e demais informações aparecerão aqui.
          </p>
        </section>
      ) : (
        <Link
          href="/app/viagem"
          className="group relative block overflow-hidden rounded-3xl border border-border bg-surface shadow-soft"
        >
          <div className="relative min-h-60 overflow-hidden">
            <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_35%),transparent_42%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_24%),transparent_40%)]" />

            <div className="absolute inset-0 bg-gradient-to-br from-primary/25 via-transparent to-magenta/15" />

            <div className="relative flex min-h-60 flex-col justify-between p-4">
              <div>
                <span
                  className={`inline-flex rounded-full border px-3 py-1 text-xs font-medium ${obterClasseStatusViagem(
                    viagem.status,
                  )}`}
                >
                  {formatarStatus(
                    viagem.status,
                  )}
                </span>
              </div>

              <div>
                <h2 className="text-2xl font-semibold tracking-tight text-foreground">
                  {viagem.viagemNome}
                </h2>

                <div className="mt-4 grid grid-cols-2 gap-2.5">
                  <div className="rounded-2xl border border-border bg-background/35 p-2.5 backdrop-blur-sm">
                    <div className="flex items-center gap-2 text-xs text-muted">
                      <CalendarDays
                        size={15}
                        className="text-primary"
                      />

                      Período
                    </div>

                    <p className="mt-2 text-sm font-medium text-foreground">
                      {formatarData(
                        viagem.dataInicio,
                      )}
                    </p>

                    <p className="text-xs text-muted">
                      até{" "}
                      {formatarData(
                        viagem.dataFim,
                      )}
                    </p>
                  </div>

                  <div className="rounded-2xl border border-border bg-background/35 p-2.5 backdrop-blur-sm">
                    <div className="flex items-center gap-2 text-xs text-muted">
                      <MapPin
                        size={15}
                        className="text-electric"
                      />

                      Destino
                    </div>

                    <p className="mt-2 text-sm font-medium text-foreground">
                      {viagem.cidade}
                    </p>

                    <p className="text-xs text-muted">
                      {viagem.estado}
                    </p>
                  </div>
                </div>

                <div className="mt-4 flex items-center justify-between gap-4">
                  <div className="flex items-baseline gap-1">
                    {viagem.status === "PLANEJADA" &&
                    diasAteViagem > 0 ? (
                      <>
                        <span className="text-sm text-muted">
                          Faltam
                        </span>

                        <span className="text-lg font-semibold text-primary">
                          {diasAteViagem}{" "}
                          {diasAteViagem === 1
                            ? "dia"
                            : "dias"}
                        </span>
                      </>
                    ) : viagem.status === "EM_ANDAMENTO" ? (
                      <span className="text-sm font-medium text-success">
                        Viagem em andamento
                      </span>
                    ) : viagem.status === "FINALIZADA" ? (
                      <span className="text-sm font-medium text-muted">
                        Viagem finalizada
                      </span>
                    ) : viagem.status === "CANCELADA" ? (
                      <span className="text-sm font-medium text-danger">
                        Viagem cancelada
                      </span>
                    ) : (
                      <span className="text-sm font-medium text-muted">
                        Aguardando início
                      </span>
                    )}
                  </div>

                  <div className="flex items-center gap-1 text-xs font-medium text-primary">
                    Ver minha viagem

                    <ChevronRight
                      size={16}
                      className="transition-transform group-hover:translate-x-1"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </Link>
      )}

      {viagem && (
        <section>
          <h2 className="mb-3 text-sm font-semibold text-foreground">
            Atalhos
          </h2>

          <div className="grid grid-cols-3 gap-3">
            {atalhos.map(
              ({
                href,
                label,
                icon: Icon,
              }) => (
                <Link
                  key={label}
                  href={href}
                  className="group flex aspect-square flex-col items-center justify-center gap-3 rounded-2xl border border-border bg-surface/60 p-3 text-center shadow-soft transition-all active:scale-95"
                >
                  <span className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary/15 text-primary transition-transform group-hover:scale-105">
                    <Icon
                      size={21}
                      strokeWidth={1.9}
                    />
                  </span>

                  <span className="text-xs font-medium text-foreground">
                    {label}
                  </span>
                </Link>
              ),
            )}
          </div>
        </section>
      )}

      {avisos.length > 0 && (
        <section>
          <div className="mb-3 flex items-center justify-between gap-3">
            <h2 className="text-sm font-semibold text-foreground">
              Avisos recentes
            </h2>

            <Link
              href="/app/avisos"
              className="text-xs font-medium text-primary"
            >
              Ver todos
            </Link>
          </div>

          <div className="space-y-3">
            {avisos
              .slice(0, 2)
              .map((aviso) => (
                <Link
                  key={aviso.id}
                  href="/app/avisos"
                  className="block rounded-2xl border border-border bg-surface/60 p-4 shadow-soft transition-colors active:scale-[0.99]"
                >
                  <div className="flex items-start gap-3">
                    <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                      <Bell
                        size={17}
                        strokeWidth={1.9}
                      />
                    </span>

                    <div className="min-w-0 flex-1">
                      <p className="text-xs font-medium uppercase tracking-wide text-primary">
                        {aviso.tipo}
                      </p>

                      <h3 className="mt-1 truncate text-sm font-semibold text-foreground">
                        {aviso.titulo}
                      </h3>

                      <p className="mt-1 line-clamp-2 text-sm leading-5 text-muted">
                        {aviso.mensagem}
                      </p>
                    </div>

                    <ChevronRight
                      size={17}
                      className="mt-1 shrink-0 text-muted"
                    />
                  </div>
                </Link>
              ))}
          </div>
        </section>
      )}
    </div>
  );
}