import Link from "next/link";

type OperationalNavigationProps = {
  perfil: "ADMIN" | "STAFF" | "HOSPEDE";
};

export function OperationalNavigation({
  perfil,
}: OperationalNavigationProps) {
  return (
    <>
      {/* Navegação mobile */}
      <nav className="fixed inset-x-0 bottom-0 z-50 border-t border-border bg-surface md:hidden">
        <div className="grid grid-cols-4">
          <Link
            href="/dashboard"
            className="flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
          >
            <span>Início</span>
          </Link>

          <Link
            href="/hospedes"
            className="flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted transition-colors hover:text-primary"
          >
            <span>Hóspedes</span>
          </Link>

          <Link
            href="/check-in"
            className="flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted transition-colors hover:text-primary"
          >
            <span>QR</span>
          </Link>

          <button
            type="button"
            disabled
            className="flex min-h-16 flex-col items-center justify-center gap-1 text-xs text-muted opacity-50"
          >
            <span>Mais</span>
          </button>
        </div>
      </nav>

      {/* Navegação desktop */}
      <aside className="hidden w-64 shrink-0 border-r border-border bg-surface md:block">
        <div className="sticky top-0 space-y-6 p-6">
          <div>
            <p className="text-sm font-medium text-muted">
              Operação
            </p>

            <nav className="mt-4 space-y-2">
              <Link
                href="/dashboard"
                className="block rounded-xl bg-primary px-4 py-3 text-sm font-medium text-white"
              >
                Dashboard
              </Link>

              <Link
                href="/hospedes"
                className="block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground"
              >
                Hóspedes
              </Link>

              {perfil === "ADMIN" && (
                <>
                  <Link
                    href="/motoristas"
                    className="block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground"
                  >
                    Motoristas
                  </Link>

                  <Link
                    href="/veiculos"
                    className="block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground"
                  >
                    Veículos
                  </Link>
                </>
              )}

              <Link
                href="/traslados"
                className="block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground"
              >
                Traslados
              </Link>

              <Link
                href="/check-in"
                className="block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground"
              >
                Check-in
              </Link>

              <Link
                href="/quartos"
                className="block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground"
              >
                Quartos
              </Link>

            </nav>
          </div>
        </div>
      </aside>
    </>
  );
}