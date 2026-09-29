"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Bell,
  Home,
  Map,
  QrCode,
  User,
} from "lucide-react";

export function HospedeNavigation() {
  const pathname = usePathname();

  function isActive(href: string) {
    if (href === "/app") {
      return pathname === "/app";
    }

    return pathname.startsWith(href);
  }

  function mobileLinkClass(href: string) {
    return isActive(href)
      ? "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-primary"
      : "flex min-h-16 flex-col items-center justify-center gap-1 text-xs font-medium text-muted transition-colors hover:text-primary";
  }

  function desktopLinkClass(href: string) {
    return isActive(href)
      ? "flex items-center gap-2 rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white"
      : "flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-medium text-muted transition-colors hover:bg-background hover:text-foreground";
  }

  return (
    <>
      {/* Navegação desktop */}
      <nav className="hidden border-b border-border bg-surface md:block">
        <div className="mx-auto flex w-full max-w-xl items-center justify-between gap-2 px-5 py-3">
          <Link
            href="/app"
            className={desktopLinkClass("/app")}
          >
            <Home size={17} />
            Início
          </Link>

          <Link
            href="/app/viagem"
            className={desktopLinkClass("/app/viagem")}
          >
            <Map size={17} />
            Viagem
          </Link>

          <Link
            href="/app/check-in"
            className={desktopLinkClass("/app/check-in")}
          >
            <QrCode size={17} />
            QR Code
          </Link>

          <Link
            href="/app/avisos"
            className={desktopLinkClass("/app/avisos")}
          >
            <Bell size={17} />
            Avisos
          </Link>

          <Link
            href="/app/perfil"
            className={desktopLinkClass("/app/perfil")}
          >
            <User size={17} />
            Perfil
          </Link>
        </div>
      </nav>

      {/* Navegação mobile */}
      <nav className="fixed inset-x-0 bottom-0 z-50 border-t border-border bg-surface md:hidden">
        <div className="grid grid-cols-5">
          {/* Início */}
          <Link
            href="/app"
            className={mobileLinkClass("/app")}
          >
            <Home size={20} strokeWidth={2} />
            <span>Início</span>
          </Link>

          {/* Viagem */}
          <Link
            href="/app/viagem"
            className={mobileLinkClass("/app/viagem")}
          >
            <Map size={20} strokeWidth={2} />
            <span>Viagem</span>
          </Link>

          {/* QR Code - ação principal */}
          <Link
            href="/app/check-in"
            className="relative flex min-h-16 flex-col items-center justify-end pb-2 text-xs font-medium"
          >
            <span
              className={`absolute -top-4 flex h-14 w-14 items-center justify-center rounded-full border-4 border-surface shadow-lg transition-transform ${
                isActive("/app/check-in")
                  ? "scale-105 bg-primary text-white"
                  : "bg-primary text-white hover:scale-105"
              }`}
            >
              <QrCode size={25} strokeWidth={2.2} />
            </span>

            <span
              className={
                isActive("/app/check-in")
                  ? "font-medium text-primary"
                  : "text-muted"
              }
            >
              QR Code
            </span>
          </Link>

          {/* Avisos */}
          <Link
            href="/app/avisos"
            className={mobileLinkClass("/app/avisos")}
          >
            <Bell size={20} strokeWidth={2} />
            <span>Avisos</span>
          </Link>

          {/* Perfil */}
          <Link
            href="/app/perfil"
            className={mobileLinkClass("/app/perfil")}
          >
            <User size={20} strokeWidth={2} />
            <span>Perfil</span>
          </Link>
        </div>
      </nav>
    </>
  );
}