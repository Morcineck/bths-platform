import Link from "next/link";

export function OperationalNavigation() {
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

          <button
            type="button"
            disabled
            className="flex min-h-16 flex-col items-center justify-center gap-1 text-xs text-muted opacity-50"
          >
            <span>QR</span>
          </button>

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

              <button
                type="button"
                disabled
                className="block w-full rounded-xl px-4 py-3 text-left text-sm text-muted opacity-50"
              >
                Check-in
              </button>

              <button
                type="button"
                disabled
                className="block w-full rounded-xl px-4 py-3 text-left text-sm text-muted opacity-50"
              >
                Quartos
              </button>

              <button
                type="button"
                disabled
                className="block w-full rounded-xl px-4 py-3 text-left text-sm text-muted opacity-50"
              >
                Traslados
              </button>
            </nav>
          </div>
        </div>
      </aside>
    </>
  );
}