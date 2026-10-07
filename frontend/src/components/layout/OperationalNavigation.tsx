"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";

import {
  BedDouble,
  Bell,
  Bus,
  Car,
  Ellipsis,
  Hotel,
  LayoutDashboard,
  QrCode,
  Users,
  CalendarDays,
  UserRoundCog,
} from "lucide-react";

type OperationalNavigationProps = {
  perfil: "ADMIN" | "STAFF" | "HOSPEDE";
};

const itensBase = [
  {
    href: "/dashboard",
    label: "Dashboard",
    icon: LayoutDashboard,
  },
  {
    href: "/hospedes",
    label: "Hóspedes",
    icon: Users,
  },
  {
    href: "/traslados",
    label: "Traslados",
    icon: Bus,
  },
  {
    href: "/check-in",
    label: "Check-in",
    icon: QrCode,
  },
  {
    href: "/quartos",
    label: "Quartos",
    icon: BedDouble,
  },
  {
    href: "/hospedagens",
    label: "Hospedagens",
    icon: Hotel,
  },
  {
    href: "/agenda-viagem",
    label: "Agenda",
    icon: CalendarDays,
  },
  {
    href: "/avisos",
    label: "Avisos",
    icon: Bell,
  },
] as const;

const itensAdmin = [
  {
    href: "/motoristas",
    label: "Motoristas",
    icon: UserRoundCog,
  },
  {
    href: "/veiculos",
    label: "Veículos",
    icon: Car,
  },
] as const;

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

  const menuMaisAtivo =
    isActive("/quartos") ||
    isActive("/hospedagens") ||
    isActive("/agenda-viagem") ||
    isActive("/avisos") ||
    (perfil === "ADMIN" &&
      (isActive("/motoristas") ||
        isActive("/veiculos")));

  return (
    <>
      {/* Navegação mobile */}
      <nav className="glass safe-bottom fixed inset-x-0 bottom-0 z-50 border-t border-border md:hidden">
        {menuMaisAberto && (
          <div className="absolute bottom-[76px] right-3 w-60 rounded-2xl border border-border bg-surface/95 p-2 shadow-soft backdrop-blur-xl">
            {[
              ...itensBase.filter(
                (item) =>
                  ![
                    "/dashboard",
                    "/hospedes",
                    "/check-in",
                    "/traslados",
                  ].includes(item.href),
              ),
              ...(perfil === "ADMIN"
                ? itensAdmin
                : []),
            ].map(
              ({
                href,
                label,
                icon: Icon,
              }) => (
                <Link
                  key={href}
                  href={href}
                  onClick={() =>
                    setMenuMaisAberto(false)
                  }
                  className={
                    isActive(href)
                      ? "flex items-center gap-3 rounded-xl bg-primary/15 px-3 py-3 text-sm font-medium text-primary"
                      : "flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium text-foreground transition-colors hover:bg-surface-secondary"
                  }
                >
                  <Icon
                    size={18}
                    strokeWidth={1.9}
                  />

                  <span>{label}</span>
                </Link>
              ),
            )}
          </div>
        )}

        <div className="grid grid-cols-5 px-2 pt-2">
          <Link
            href="/dashboard"
            onClick={() =>
              setMenuMaisAberto(false)
            }
            className={
              isActive("/dashboard")
                ? "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
                : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted"
            }
          >
            <span
              className={
                isActive("/dashboard")
                  ? "flex h-9 w-9 items-center justify-center rounded-xl bg-primary/15"
                  : "flex h-9 w-9 items-center justify-center rounded-xl"
              }
            >
              <LayoutDashboard
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <span>Início</span>
          </Link>

          <Link
            href="/hospedes"
            onClick={() =>
              setMenuMaisAberto(false)
            }
            className={
              isActive("/hospedes")
                ? "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
                : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted"
            }
          >
            <span
              className={
                isActive("/hospedes")
                  ? "flex h-9 w-9 items-center justify-center rounded-xl bg-primary/15"
                  : "flex h-9 w-9 items-center justify-center rounded-xl"
              }
            >
              <Users
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <span>Hóspedes</span>
          </Link>

          <Link
            href="/check-in"
            onClick={() =>
              setMenuMaisAberto(false)
            }
            className="flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium"
          >
            <span className="gradient-brand shadow-glow -mt-6 flex h-14 w-14 items-center justify-center rounded-2xl text-white">
              <QrCode
                size={23}
                strokeWidth={2}
              />
            </span>

            <span
              className={
                isActive("/check-in")
                  ? "text-foreground"
                  : "text-muted"
              }
            >
              Check-in
            </span>
          </Link>

          <Link
            href="/traslados"
            onClick={() =>
              setMenuMaisAberto(false)
            }
            className={
              isActive("/traslados")
                ? "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
                : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted"
            }
          >
            <span
              className={
                isActive("/traslados")
                  ? "flex h-9 w-9 items-center justify-center rounded-xl bg-primary/15"
                  : "flex h-9 w-9 items-center justify-center rounded-xl"
              }
            >
              <Bus
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <span>Traslados</span>
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
                : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted"
            }
          >
            <span
              className={
                menuMaisAberto ||
                menuMaisAtivo
                  ? "flex h-9 w-9 items-center justify-center rounded-xl bg-primary/15"
                  : "flex h-9 w-9 items-center justify-center rounded-xl"
              }
            >
              <Ellipsis
                size={20}
                strokeWidth={1.9}
              />
            </span>

            <span>Mais</span>
          </button>
        </div>
      </nav>

      {/* Navegação desktop */}
      <aside className="hidden w-64 shrink-0 border-r border-border/80 md:block">
        <div className="sticky top-[65px] flex h-[calc(100vh-65px)] flex-col px-3 py-5">
          <div className="mb-5 px-2">
            <p className="text-xs font-medium uppercase tracking-[0.16em] text-muted">
              {perfil === "ADMIN"
                ? "Administração"
                : "Operação"}
            </p>
          </div>

          <nav className="space-y-1">
            {itensBase.map(
              ({
                href,
                label,
                icon: Icon,
              }) => (
                <Link
                  key={href}
                  href={href}
                  className={
                    isActive(href)
                      ? "flex items-center gap-3 rounded-xl border border-primary/20 bg-primary/15 px-3 py-2.5 text-sm font-medium text-foreground shadow-soft"
                      : "flex items-center gap-3 rounded-xl border border-transparent px-3 py-2.5 text-sm font-medium text-muted transition-all hover:border-border hover:bg-surface/50 hover:text-foreground"
                  }
                >
                  <span
                    className={
                      isActive(href)
                        ? "flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/20 text-primary"
                        : "flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-surface/60 text-muted transition-colors"
                    }
                  >
                    <Icon
                      size={18}
                      strokeWidth={1.9}
                    />
                  </span>

                  <span>{label}</span>
                </Link>
              ),
            )}

            {perfil === "ADMIN" && (
              <>
                <div className="my-4 border-t border-border/70" />

                <p className="px-3 pb-1 text-[11px] font-medium uppercase tracking-[0.14em] text-muted">
                  Recursos
                </p>

                {itensAdmin.map(
                  ({
                    href,
                    label,
                    icon: Icon,
                  }) => (
                    <Link
                      key={href}
                      href={href}
                      className={
                        isActive(href)
                          ? "flex items-center gap-3 rounded-xl border border-primary/20 bg-primary/15 px-3 py-2.5 text-sm font-medium text-foreground shadow-soft"
                          : "flex items-center gap-3 rounded-xl border border-transparent px-3 py-2.5 text-sm font-medium text-muted transition-all hover:border-border hover:bg-surface/50 hover:text-foreground"
                      }
                    >
                      <span
                        className={
                          isActive(href)
                            ? "flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/20 text-primary"
                            : "flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-surface/60 text-muted transition-colors"
                        }
                      >
                        <Icon
                          size={18}
                          strokeWidth={1.9}
                        />
                      </span>

                      <span>{label}</span>
                    </Link>
                  ),
                )}
              </>
            )}
          </nav>

          <div className="mt-auto rounded-2xl border border-border bg-surface/40 p-3">
            <p className="text-xs font-medium text-foreground">
              Beat Trips
            </p>

            <p className="mt-1 text-[11px] leading-4 text-muted">
              BTHS Platform
            </p>
          </div>
        </div>
      </aside>
    </>
  );
}