import Link from "next/link";

import {
  BedDouble,
  Bus,
  QrCode,
  Users,
} from "lucide-react";

type QuickAction = {
  titulo: string;
  href: string;
  icon: typeof Users;
};

const acoes: QuickAction[] = [
  {
    titulo: "Hóspedes",
    href: "/hospedes",
    icon: Users,
  },
  {
    titulo: "Check-in",
    href: "/check-in",
    icon: QrCode,
  },
  {
    titulo: "Quartos",
    href: "/quartos",
    icon: BedDouble,
  },
  {
    titulo: "Traslados",
    href: "/traslados",
    icon: Bus,
  },
];

export function QuickActions() {
  return (
    <section className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft">
      <div className="mb-3">
        <p className="text-sm font-semibold text-foreground">
          Ações rápidas
        </p>

        <p className="mt-1 text-xs text-muted">
          Principais áreas da operação.
        </p>
      </div>

      <div className="grid grid-cols-2 gap-2 lg:grid-cols-4">
        {acoes.map(
          ({
            titulo,
            href,
            icon: Icon,
          }) => (
            <Link
              key={titulo}
              href={href}
              className="group flex items-center gap-3 rounded-xl border border-border bg-background/25 p-3 transition-all hover:border-primary/30 hover:bg-primary/5"
            >
              <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary transition-transform group-hover:scale-105">
                <Icon
                  size={18}
                  strokeWidth={1.9}
                />
              </span>

              <span className="text-sm font-medium text-foreground">
                {titulo}
              </span>
            </Link>
          ),
        )}
      </div>
    </section>
  );
}