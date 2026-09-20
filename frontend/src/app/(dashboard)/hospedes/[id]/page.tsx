"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";
import { buscarHospedePorId } from "@/features/hospede/services/hospedeService";
import type { Hospede } from "@/features/hospede/types/hospede";
import { consultarCheckIn } from "@/features/checkin/services/checkInService";
import type { CheckInResponse } from "@/features/checkin/types/checkin";
import { listarTrasladosPorHospede } from "@/features/traslado/services/trasladoService";
import type { Traslado } from "@/features/traslado/types/traslado";

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

  const [checkIn, setCheckIn] =
    useState<CheckInResponse | null>(null);

  const [traslados, setTraslados] =
    useState<Traslado[]>([]);

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

        const dadosCheckIn = await consultarCheckIn(
          Number(id),
        );

        setCheckIn(dadosCheckIn);

        const dadosTraslados = await listarTrasladosPorHospede(
          Number(id),
        );

        setTraslados(dadosTraslados);

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
      <section className="space-y-4">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Check-in e hospedagem
          </h2>

          <p className="mt-1 text-sm text-muted">
            Situação operacional do hóspede na viagem.
          </p>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card>
            <p className="text-sm text-muted">
              Status do check-in
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.statusCheckIn === "REALIZADO"
                ? "Realizado"
                : checkIn?.statusCheckIn === "NAO_COMPARECEU"
                  ? "Não compareceu"
                  : "Pendente"}
            </p>
          </Card>

          <Card>
            <p className="text-sm text-muted">
              Quarto
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.quartoNome ?? "Ainda não alocado"}
            </p>
          </Card>

          <Card>
            <p className="text-sm text-muted">
              Data e hora do check-in
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.dataHoraCheckIn
                ? new Date(
                    checkIn.dataHoraCheckIn,
                  ).toLocaleString("pt-BR")
                : "Ainda não realizado"}
            </p>
          </Card>

          <Card>
            <p className="text-sm text-muted">
              Responsável
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.responsavel ?? "Não informado"}
            </p>
          </Card>

          <Card className="md:col-span-2">
            <p className="text-sm text-muted">
              Observação
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.observacao ?? "Nenhuma observação registrada"}
            </p>
          </Card>
        </div>
      </section>
      <section className="space-y-4">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Traslados
          </h2>

          <p className="mt-1 text-sm text-muted">
            Deslocamentos vinculados a este hóspede.
          </p>
        </div>

        {traslados.length === 0 ? (
          <Card>
            <p className="text-sm text-muted">
              Nenhum traslado cadastrado para este hóspede.
            </p>
          </Card>
        ) : (
          <div className="space-y-4">
            {traslados.map((traslado) => (
              <Card key={traslado.id}>
                <div className="space-y-4">
                  <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                    <div>
                      <p className="font-semibold text-foreground">
                        {traslado.tipo === "AEROPORTO_PARA_HOSPEDAGEM"
                          ? "Aeroporto → Hospedagem"
                          : traslado.tipo === "HOSPEDAGEM_PARA_AEROPORTO"
                            ? "Hospedagem → Aeroporto"
                            : "Outro traslado"}
                      </p>

                      <p className="mt-1 text-sm text-muted">
                        {traslado.localOrigem} → {traslado.localDestino}
                      </p>
                    </div>

                    <p className="text-sm font-medium text-foreground">
                      {traslado.status === "AGUARDANDO"
                        ? "Aguardando"
                        : traslado.status === "EM_ANDAMENTO"
                          ? "Em andamento"
                          : traslado.status === "CONCLUIDO"
                            ? "Concluído"
                            : "Cancelado"}
                    </p>
                  </div>

                  <div className="grid gap-4 md:grid-cols-2">
                    <div>
                      <p className="text-sm text-muted">
                        Data e hora prevista
                      </p>

                      <p className="mt-1 font-medium text-foreground">
                        {new Date(
                          traslado.dataHoraPrevista,
                        ).toLocaleString("pt-BR")}
                      </p>
                    </div>

                    <div>
                      <p className="text-sm text-muted">
                        Aeroporto
                      </p>

                      <p className="mt-1 font-medium text-foreground">
                        {traslado.aeroporto ?? "Não informado"}
                      </p>
                    </div>

                    <div>
                      <p className="text-sm text-muted">
                        Voo
                      </p>

                      <p className="mt-1 font-medium text-foreground">
                        {traslado.numeroVoo ?? "Não informado"}
                      </p>
                    </div>

                    <div>
                      <p className="text-sm text-muted">
                        Companhia aérea
                      </p>

                      <p className="mt-1 font-medium text-foreground">
                        {traslado.companhiaAerea ?? "Não informada"}
                      </p>
                    </div>
                  </div>

                  {traslado.observacoes && (
                    <div>
                      <p className="text-sm text-muted">
                        Observações
                      </p>

                      <p className="mt-1 font-medium text-foreground">
                        {traslado.observacoes}
                      </p>
                    </div>
                  )}
                </div>
              </Card>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}