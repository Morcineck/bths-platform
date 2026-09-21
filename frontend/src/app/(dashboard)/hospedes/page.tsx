"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";
import { listarHospedesPorViagem } from "@/features/hospede/services/hospedeService";
import type { Hospede } from "@/features/hospede/types/hospede";
import { listarViagens } from "@/features/viagem/services/viagemService";
import type { Viagem } from "@/features/viagem/types/viagem";

export default function HospedesPage() {
  const [hospedes, setHospedes] = useState<Hospede[]>([]);
  const [viagens, setViagens] = useState<Viagem[]>([]);

  const [viagemAtivaId, setViagemAtivaId] =
    useState<number | null>(null);

  const [busca, setBusca] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        setIsAdmin(
          usuario.perfil === "ADMIN",
        );

        const dados = await listarViagens();

        setViagens(dados);

        const viagemPreferencial =
          dados.find(
            (viagem) =>
              viagem.status === "EM_ANDAMENTO",
          ) ??
          dados.find(
            (viagem) =>
              viagem.status === "PLANEJADA",
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

  const hospedesFiltrados = useMemo(() => {
    const termo =
      busca.trim().toLowerCase();

    if (!termo) {
      return hospedes;
    }

    return hospedes.filter((hospede) =>
      hospede.nomeCompleto
        .toLowerCase()
        .includes(termo),
    );
  }, [busca, hospedes]);

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <PageHeader
          title="Hóspedes"
          description="Consulte os hóspedes da viagem selecionada."
        />

        <div className="flex w-full flex-col gap-3 md:w-auto md:items-end">
          {isAdmin && (
            <Link
              href="/hospedes/novo"
              className="inline-flex h-11 items-center justify-center rounded-xl bg-primary px-5 text-sm font-semibold text-white transition-opacity hover:opacity-90"
            >
              Cadastrar hóspede
            </Link>
          )}

          <div className="w-full md:w-80">
            <label
              htmlFor="viagem"
              className="mb-2 block text-sm font-medium text-foreground"
            >
              Viagem ativa
            </label>

            <select
              id="viagem"
              value={viagemAtivaId ?? ""}
              onChange={(event) =>
                setViagemAtivaId(
                  Number(event.target.value),
                )
              }
              className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
            >
              {viagens.map((viagem) => (
                <option
                  key={viagem.id}
                  value={viagem.id}
                >
                  {viagem.nome} —{" "}
                  {viagem.status}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      <div className="max-w-md">
        <label
          htmlFor="busca"
          className="mb-2 block text-sm font-medium text-foreground"
        >
          Buscar hóspede
        </label>

        <Input
          id="busca"
          name="busca"
          type="search"
          placeholder="Digite o nome do hóspede"
          value={busca}
          onChange={(event) =>
            setBusca(event.target.value)
          }
        />
      </div>

      {erro && (
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>
      )}

      {carregando ? (
        <p className="text-sm text-muted">
          Carregando hóspedes...
        </p>
      ) : hospedesFiltrados.length === 0 ? (
        <Card>
          <p className="text-sm text-muted">
            Nenhum hóspede encontrado.
          </p>
        </Card>
      ) : (
        <section className="space-y-3">
          {hospedesFiltrados.map(
            (hospede) => (
              <Link
                key={hospede.id}
                href={`/hospedes/${hospede.id}`}
                className="block"
              >
                <Card className="transition-colors hover:border-primary">
                  <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                    <div>
                      <p className="font-semibold text-foreground">
                        {hospede.nomeCompleto}
                      </p>

                      <p className="mt-1 text-sm text-muted">
                        {hospede.email}
                      </p>

                      <p className="mt-1 text-sm text-muted">
                        {hospede.telefone}
                      </p>
                    </div>

                    <div className="md:text-right">
                      <p className="text-sm font-medium text-foreground">
                        {hospede.statusCheckIn ===
                        "REALIZADO"
                          ? "Check-in realizado"
                          : hospede.statusCheckIn ===
                              "NAO_COMPARECEU"
                            ? "Não compareceu"
                            : "Check-in pendente"}
                      </p>

                      <p className="mt-1 text-xs text-muted">
                        {hospede.viagemNome}
                      </p>
                    </div>
                  </div>
                </Card>
              </Link>
            ),
          )}
        </section>
      )}
    </div>
  );
}