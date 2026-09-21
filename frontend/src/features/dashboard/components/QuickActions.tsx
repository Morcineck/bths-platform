import Link from "next/link";

type QuickAction = {
  titulo: string;
  descricao: string;
  href?: string;
};

const acoes: QuickAction[] = [
  {
    titulo: "Hóspedes",
    descricao: "Buscar e consultar hóspedes da viagem.",
    href: "/hospedes",
  },
  {
    titulo: "Check-in",
    descricao: "Acompanhar e executar check-ins.",
  },
  {
    titulo: "Quartos",
    descricao: "Consultar ocupação e alocações.",
  },
  {
    titulo: "Traslados",
    descricao: "Acompanhar a operação de transporte.",
  },
];

export function QuickActions() {
  return (
    <section className="space-y-4">
      <div>
        <h2 className="text-lg font-semibold text-foreground">
          Ações rápidas
        </h2>

        <p className="mt-1 text-sm text-muted">
          Acesse rapidamente as principais áreas da operação.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {acoes.map((acao) =>
          acao.href ? (
            <Link
              key={acao.titulo}
              href={acao.href}
              className="rounded-2xl border border-border bg-surface p-5 text-left transition-colors hover:border-primary"
            >
              <p className="font-semibold text-foreground">
                {acao.titulo}
              </p>

              <p className="mt-2 text-sm leading-6 text-muted">
                {acao.descricao}
              </p>
            </Link>
          ) : (
            <button
              key={acao.titulo}
              type="button"
              disabled
              className="rounded-2xl border border-border bg-surface p-5 text-left opacity-50"
            >
              <p className="font-semibold text-foreground">
                {acao.titulo}
              </p>

              <p className="mt-2 text-sm leading-6 text-muted">
                {acao.descricao}
              </p>
            </button>
          ),
        )}
      </div>
    </section>
  );
}