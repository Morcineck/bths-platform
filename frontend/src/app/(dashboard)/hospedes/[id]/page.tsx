"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";
import { buscarHospedePorId } from "@/features/hospede/services/hospedeService";
import type { Hospede } from "@/features/hospede/types/hospede";

type HospedeDetalhePageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default function HospedeDetalhePage({
  params,
}: HospedeDetalhePageProps) {
  const { id } = use(params);

  const [hospede, setHospede] =
    useState<Hospede | null>(null);

  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    async function carregarHospede() {
      try {
        setErro("");

        const dados = await buscarHospedePorId(
          Number(id),
        );

        setHospede(dados);
      } catch {
        setErro(
          "Não foi possível carregar os dados do hóspede.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarHospede();
  }, [id]);

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando hóspede...
      </p>
    );
  }

  if (erro || !hospede) {
    return (
      <div className="space-y-4">
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro || "Hóspede não encontrado."}
        </p>

        <Link
          href="/hospedes"
          className="text-sm font-medium text-primary"
        >
          Voltar para hóspedes
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <header>
        <Link
          href="/hospedes"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para hóspedes
        </Link>

        <h1 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
          {hospede.nomeCompleto}
        </h1>

        <p className="mt-2 text-sm text-muted">
          {hospede.viagemNome}
        </p>
      </header>

      <section className="grid gap-4 md:grid-cols-2">
        <Card>
          <p className="text-sm text-muted">
            E-mail
          </p>

          <p className="mt-2 font-medium text-foreground">
            {hospede.email}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Telefone
          </p>

          <p className="mt-2 font-medium text-foreground">
            {hospede.telefone}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            CPF
          </p>

          <p className="mt-2 font-medium text-foreground">
            {hospede.cpf}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Data de nascimento
          </p>

          <p className="mt-2 font-medium text-foreground">
            {new Date(
              `${hospede.dataNascimento}T00:00:00`,
            ).toLocaleDateString("pt-BR")}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Check-in
          </p>

          <p className="mt-2 font-medium text-foreground">
            {hospede.statusCheckIn === "REALIZADO"
              ? "Realizado"
              : hospede.statusCheckIn ===
                  "NAO_COMPARECEU"
                ? "Não compareceu"
                : "Pendente"}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Chegada prevista
          </p>

          <p className="mt-2 font-medium text-foreground">
            {new Date(
              hospede.horarioPrevistoChegada,
            ).toLocaleString("pt-BR")}
          </p>
        </Card>
      </section>
    </div>
  );
}