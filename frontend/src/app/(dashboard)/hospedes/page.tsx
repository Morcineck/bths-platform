"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";

import {
  CheckCircle2,
  Clock3,
  Search,
  UserPlus,
  UserX,
  Users,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";
import { Input } from "@/components/ui/Input";
import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";
import { listarHospedesPorViagem } from "@/features/hospede/services/hospedeService";
import type { Hospede } from "@/features/hospede/types/hospede";
import { listarViagens } from "@/features/viagem/services/viagemService";
import type { Viagem } from "@/features/viagem/types/viagem";

export default function HospedesPage() {
  const [hospedes, setHospedes] =
    useState<Hospede[]>([]);

  const [viagens, setViagens] =
    useState<Viagem[]>([]);

  const [viagemAtivaId, setViagemAtivaId] =
    useState<number | null>(null);

  const [busca, setBusca] =
    useState("");

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] =
    useState("");

  const [isAdmin, setIsAdmin] =
    useState(false);

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        setIsAdmin(
          usuario.perfil === "ADMIN",
        );

        const dados =
          await listarViagens();

        setViagens(dados);

        const viagemPreferencial =
          dados.find(
            (viagem) =>
              viagem.status ===
              "EM_ANDAMENTO",
          ) ??
          dados.find(
            (viagem) =>
              viagem.status ===
              "PLANEJADA",
          ) ??
          dados[0];

        if (viagemPreferencial) {
          setViagemAtivaId(
            viagemPreferencial.id,
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar as viagens.",
        );

        setCarregando(false);
      }
    }

    carregarPagina();
  }, []);

  useEffect(() => {
    if (viagemAtivaId === null) {
      return;
    }

    async function carregarHospedes() {
      try {
        setCarregando(true);
        setErro("");

        const dados =
          await listarHospedesPorViagem(
            viagemAtivaId!,
          );

        setHospedes(dados);
      } catch {
        setErro(
          "Não foi possível carregar os hóspedes.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarHospedes();
  }, [viagemAtivaId]);

  const hospedesFiltrados =
    useMemo(() => {
      const termo =
        busca
          .trim()
          .toLowerCase();

      if (!termo) {
        return hospedes;
      }

      return hospedes.filter(
        (hospede) =>
          hospede.nomeCompleto
            .toLowerCase()
            .includes(termo),
      );
    }, [busca, hospedes]);

  function obterIniciais(
    nome: string,
  ) {
    return nome
      .trim()
      .split(/\s+/)
      .map((parte) => parte[0])
      .slice(0, 2)
      .join("")
      .toUpperCase();
  }

  function formatarTelefone(
    telefone: string,
  ) {
    const numeros =
      telefone.replace(/\D/g, "");

    if (numeros.length === 11) {
      return numeros.replace(
        /(\d{2})(\d{5})(\d{4})/,
        "($1) $2-$3",
      );
    }

    if (numeros.length === 10) {
      return numeros.replace(
        /(\d{2})(\d{4})(\d{4})/,
        "($1) $2-$3",
      );
    }

    return telefone;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Hóspedes"
          description="Consulte e acompanhe os hóspedes da viagem selecionada."
        />

        {isAdmin && (
          <Link
            href="/hospedes/novo"
            className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] md:shadow-none"
          >
            <UserPlus
              size={18}
              strokeWidth={1.9}
            />

            Cadastrar hóspede
          </Link>
        )}
      </div>

      <section className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft">
        <div className="grid gap-3 lg:grid-cols-[minmax(0,1fr)_320px]">
          <div>
            <label
              htmlFor="busca"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Buscar hóspede
            </label>

            <div className="relative">
              <Search
                size={17}
                strokeWidth={1.9}
                className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-muted"
              />

              <Input
                id="busca"
                name="busca"
                type="search"
                placeholder="Digite o nome do hóspede"
                value={busca}
                onChange={(event) =>
                  setBusca(
                    event.target.value,
                  )
                }
                className="h-11 bg-background/30 pl-11"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="viagem"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Viagem ativa
            </label>

            <select
              id="viagem"
              value={
                viagemAtivaId ?? ""
              }
              onChange={(event) =>
                setViagemAtivaId(
                  Number(
                    event.target.value,
                  ),
                )
              }
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
            >
              {viagens.map(
                (viagem) => (
                  <option
                    key={viagem.id}
                    value={viagem.id}
                  >
                    {viagem.nome}
                  </option>
                ),
              )}
            </select>
          </div>
        </div>

        <div className="mt-4 flex items-center gap-2 border-t border-border/70 pt-3">
          <span className="flex h-8 w-8 items-center justify-center rounded-xl bg-primary/15 text-primary">
            <Users
              size={16}
              strokeWidth={1.9}
            />
          </span>

          <p className="text-xs text-muted">
            <span className="font-semibold text-foreground">
              {
                hospedesFiltrados.length
              }
            </span>{" "}
            {hospedesFiltrados.length ===
            1
              ? "hóspede encontrado"
              : "hóspedes encontrados"}
          </p>
        </div>
      </section>

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {carregando ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-4">
          <p className="text-sm text-muted">
            Carregando hóspedes...
          </p>
        </div>
      ) : hospedesFiltrados.length ===
        0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <Users
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhum hóspede encontrado
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Tente ajustar a busca ou selecionar outra viagem.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-2">
          {hospedesFiltrados.map(
            (hospede) => {
              const realizado =
                hospede.statusCheckIn ===
                "REALIZADO";

              const naoCompareceu =
                hospede.statusCheckIn ===
                "NAO_COMPARECEU";

              return (
                <Link
                  key={hospede.id}
                  href={`/hospedes/${hospede.id}`}
                  className="group block rounded-2xl border border-border bg-surface/50 px-3 py-2.5 shadow-soft transition-all hover:border-primary/30 hover:bg-primary/5 sm:p-3"
                >
                  <div className="flex items-center gap-3">
                    <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-xs font-semibold text-primary sm:h-11 sm:w-11 sm:text-sm">
                      {obterIniciais(
                        hospede.nomeCompleto,
                      )}
                    </span>

                    <div className="min-w-0 flex-1">
                      <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                        <div className="min-w-0">
                          <p className="truncate text-sm font-semibold text-foreground">
                            {
                              hospede.nomeCompleto
                            }
                          </p>

                          <div className="mt-1 flex flex-col gap-0.5 text-xs text-muted sm:flex-row sm:items-center sm:gap-3">
                            <span className="truncate">
                              {
                                hospede.email
                              }
                            </span>

                            <span className="hidden sm:inline">
                              •
                            </span>

                            <span className="whitespace-nowrap">
                              {formatarTelefone(
                                hospede.telefone,
                              )}
                            </span>
                          </div>
                        </div>

                        <div className="flex items-center md:justify-end">
                          <span
                            className={
                              realizado
                                ? "inline-flex items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                                : naoCompareceu
                                  ? "inline-flex items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                                  : "inline-flex items-center gap-1.5 rounded-full border border-warning/30 bg-warning/10 px-2.5 py-1 text-[11px] font-medium text-warning"
                            }
                          >
                            {realizado ? (
                              <CheckCircle2
                                size={13}
                                strokeWidth={2}
                              />
                            ) : naoCompareceu ? (
                              <UserX
                                size={13}
                                strokeWidth={2}
                              />
                            ) : (
                              <Clock3
                                size={13}
                                strokeWidth={2}
                              />
                            )}

                            {realizado
                              ? "Check-in realizado"
                              : naoCompareceu
                                ? "Não compareceu"
                                : "Check-in pendente"}
                          </span>
                        </div>
                      </div>

                    </div>
                  </div>
                </Link>
              );
            },
          )}
        </section>
      )}
    </div>
  );
}