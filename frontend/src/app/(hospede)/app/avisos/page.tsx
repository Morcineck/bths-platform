"use client";

import Link from "next/link";

import {
  Bell,
  ChevronLeft,
} from "lucide-react";

export default function AvisosPage() {
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

        <div className="min-w-0">
          <h1 className="truncate text-xl font-semibold tracking-tight text-foreground">
            Avisos
          </h1>

          <p className="truncate text-xs text-muted">
            Central de comunicados
          </p>
        </div>
      </header>

      <section className="relative overflow-hidden rounded-3xl border border-border bg-surface/60 p-6 shadow-soft">
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,oklch(0.66_0.26_340_/_16%),transparent_38%),radial-gradient(circle_at_bottom_left,oklch(0.7_0.19_245_/_10%),transparent_42%)]" />

        <div className="relative flex flex-col items-center text-center">
          <span className="flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/15 text-primary">
            <Bell
              size={25}
              strokeWidth={1.8}
            />
          </span>

          <h2 className="mt-4 text-base font-semibold text-foreground">
            Nenhum aviso por enquanto
          </h2>

          <p className="mt-2 max-w-xs text-sm leading-6 text-muted">
            Os comunicados importantes da sua experiência
            Beat Trips aparecerão aqui.
          </p>

          <div className="mt-5 w-full max-w-[280px] rounded-2xl border border-border bg-background/35 p-4 text-left">
            <p className="text-xs font-medium text-primary">
              Fique tranquilo
            </p>

            <p className="mt-2 text-sm leading-6 text-muted">
              Quando houver novidades sobre transporte,
              hospedagem ou sua experiência no evento,
              você poderá acompanhar tudo por esta área.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}