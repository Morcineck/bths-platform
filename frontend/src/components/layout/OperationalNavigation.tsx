"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";

type OperationalNavigationProps = {
  perfil: "ADMIN" | "STAFF" | "HOSPEDE";
};

export function OperationalNavigation({
  perfil,
}: OperationalNavigationProps) {
  const pathname = usePathname();

  const [menuMaisAberto, setMenuMaisAberto] =
    useState(false);

  function isActive(href: string) {
    if (href === "/dashboard") {
      return pathname === "/dashboard";
    }

    return pathname.startsWith(href);
  }

  function desktopLinkClass(href: string) {
    return isActive(href)
      ? "block rounded-xl bg-primary px-4 py-3 text-sm font-medium text-white"
      : "block rounded-xl px-4 py-3 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground";
  }

  function mobileLinkClass(href: string) {
    return isActive(href)
      ? "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
      : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted transition-colors hover:text-primary";
  }

  function mobileMenuLinkClass(href: string) {
    return isActive(href)
      ? "block rounded-xl bg-primary/10 px-4 py-3 text-sm font-medium text-primary"
      : "block rounded-xl px-4 py-3 text-sm font-medium text-foreground transition-colors hover:bg-background";
  }

  const menuMaisAtivo =
    isActive("/traslados") ||
    isActive("/quartos") ||
    (perfil === "ADMIN" &&
      (isActive("/motoristas") ||
        isActive("/veiculos")));

  return (
    <>
      {/* Navegação mobile */}
      <nav className="fixed inset-x-0 bottom-0 z-50 border-t border-border bg-surface md:hidden">
        {menuMaisAberto && (
          <div className="absolute bottom-16 right-3 w-56 rounded-2xl border border-border bg-surface p-2 shadow-lg">
            <Link
              href="/traslados"
              onClick={() =>
                setMenuMaisAberto(false)
              }
              className={mobileMenuLinkClass(
                "/traslados",
              )}
            >
              Traslados
            </Link>

            <Link
              href="/quartos"
              onClick={() =>
                setMenuMaisAberto(false)
              }
              className={mobileMenuLinkClass(
                "/quartos",
              )}
            >
              Quartos
            </Link>

            {perfil === "ADMIN" && (
              <>
                <Link
                  href="/motoristas"
                  onClick={() =>
                    setMenuMaisAberto(false)
                  }
                  className={mobileMenuLinkClass(
                    "/motoristas",
                  )}
                >
                  Motoristas
                </Link>

                <Link
                  href="/veiculos"
                  onClick={() =>
                    setMenuMaisAberto(false)
                  }
                  className={mobileMenuLinkClass(
                    "/veiculos",
                  )}
                >
                  Veículos
                </Link>
              </>
            )}
          </div>
        )}

        <div className="grid grid-cols-4">
          <Link
            href="/dashboard"
            className={mobileLinkClass(
              "/dashboard",
            )}
            onClick={() =>
              setMenuMaisAberto(false)
            }
          >
            <span>Início</span>
          </Link>

          <Link
            href="/hospedes"
            className={mobileLinkClass(
              "/hospedes",
            )}
            onClick={() =>
              setMenuMaisAberto(false)
            }
          >
            <span>Hóspedes</span>
          </Link>

          <Link
            href="/check-in"
            className={mobileLinkClass(
              "/check-in",
            )}
            onClick={() =>
              setMenuMaisAberto(false)
            }
          >
            <span>Check-in</span>
          </Link>

          <button
            type="button"
            onClick={() =>
              setMenuMaisAberto(
                (aberto) => !aberto,
              )
            }
            className={
              menuMaisAberto ||
              menuMaisAtivo
                ? "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
                : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted transition-colors hover:text-primary"
            }
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
                className={desktopLinkClass(
                  "/dashboard",
                )}
              >
                Dashboard
              </Link>

              <Link
                href="/hospedes"
                className={desktopLinkClass(
                  "/hospedes",
                )}
              >
                Hóspedes
              </Link>

              {perfil === "ADMIN" && (
                <>
                  <Link
                    href="/motoristas"
                    className={desktopLinkClass(
                      "/motoristas",
                    )}
                  >
                    Motoristas
                  </Link>

                  <Link
                    href="/veiculos"
                    className={desktopLinkClass(
                      "/veiculos",
                    )}
                  >
                    Veículos
                  </Link>
                </>
              )}

              <Link
                href="/traslados"
                className={desktopLinkClass(
                  "/traslados",
                )}
              >
                Traslados
              </Link>

              <Link
                href="/check-in"
                className={desktopLinkClass(
                  "/check-in",
                )}
              >
                Check-in
              </Link>

              <Link
                href="/quartos"
                className={desktopLinkClass(
                  "/quartos",
                )}
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