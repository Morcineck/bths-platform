"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";
import { alocarHospedeEmQuarto } from "@/features/alocacao/services/alocacaoService";
import {
  consultarCheckIn,
  realizarCheckIn,
} from "@/features/checkin/services/checkInService";
import type { CheckInResponse } from "@/features/checkin/types/checkin";
import { buscarHospedePorId } from "@/features/hospede/services/hospedeService";
import type { Hospede } from "@/features/hospede/types/hospede";
import { listarQuartosPorViagem } from "@/features/quarto/services/quartoService";
import type { Quarto } from "@/features/quarto/types/quarto";
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

  const [quartos, setQuartos] =
    useState<Quarto[]>([]);

  const [
    quartoSelecionadoId,
    setQuartoSelecionadoId,
  ] = useState("");

  const [alocandoQuarto, setAlocandoQuarto] =
    useState(false);

  const [erroAlocacao, setErroAlocacao] =
    useState("");

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] = useState("");

  const [
    observacaoCheckIn,
    setObservacaoCheckIn,
  ] = useState("");

  const [
    realizandoCheckIn,
    setRealizandoCheckIn,
  ] = useState(false);

  const [erroCheckIn, setErroCheckIn] =
    useState("");

  useEffect(() => {
    async function carregarHospede() {
      try {
        setErro("");

        const dados = await buscarHospedePorId(
          Number(id),
        );

        setHospede(dados);

        const dadosCheckIn =
          await consultarCheckIn(
            Number(id),
          );

        setCheckIn(dadosCheckIn);

        const dadosQuartos =
          await listarQuartosPorViagem(
            dados.viagemId,
          );

        setQuartos(
          dadosQuartos.filter(
            (quarto) =>
              quarto.status === "DISPONIVEL",
          ),
        );

        const dadosTraslados =
          await listarTrasladosPorHospede(
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

  async function handleAlocarQuarto() {
    if (!quartoSelecionadoId) {
      setErroAlocacao(
        "Selecione um quarto para continuar.",
      );
      return;
    }

    try {
      setAlocandoQuarto(true);
      setErroAlocacao("");

      const alocacao =
        await alocarHospedeEmQuarto({
          hospedeId: Number(id),
          quartoId: Number(
            quartoSelecionadoId,
          ),
        });

      setCheckIn((checkInAtual) =>
        checkInAtual
          ? {
              ...checkInAtual,
              quartoId: alocacao.quartoId,
              quartoNome:
                alocacao.quartoNome,
            }
          : checkInAtual,
      );

      setQuartoSelecionadoId("");
    } catch {
      setErroAlocacao(
        "Não foi possível alocar o hóspede no quarto.",
      );
    } finally {
      setAlocandoQuarto(false);
    }
  }

  async function handleRealizarCheckIn() {
    try {
      setRealizandoCheckIn(true);
      setErroCheckIn("");

      const response =
        await realizarCheckIn(
          Number(id),
          {
            observacao:
              observacaoCheckIn ||
              undefined,
          },
        );

      setCheckIn(response);

      setHospede((hospedeAtual) =>
        hospedeAtual
          ? {
              ...hospedeAtual,
              statusCheckIn:
                response.statusCheckIn,
            }
          : hospedeAtual,
      );

      setObservacaoCheckIn("");
    } catch {
      setErroCheckIn(
        "Não foi possível realizar o check-in.",
      );
    } finally {
      setRealizandoCheckIn(false);
    }
  }

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
          {erro ||
            "Hóspede não encontrado."}
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
            {hospede.statusCheckIn ===
            "REALIZADO"
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
              {checkIn?.statusCheckIn ===
              "REALIZADO"
                ? "Realizado"
                : checkIn?.statusCheckIn ===
                    "NAO_COMPARECEU"
                  ? "Não compareceu"
                  : "Pendente"}
            </p>
          </Card>

          <Card>
            <p className="text-sm text-muted">
              Quarto
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.quartoNome ??
                "Ainda não alocado"}
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
              {checkIn?.responsavel ??
                "Não informado"}
            </p>
          </Card>

          <Card className="md:col-span-2">
            <p className="text-sm text-muted">
              Observação
            </p>

            <p className="mt-2 font-medium text-foreground">
              {checkIn?.observacao ??
                "Nenhuma observação registrada"}
            </p>
          </Card>
        </div>

        {checkIn &&
          checkIn.statusCheckIn !==
            "REALIZADO" &&
          checkIn.quartoId === null && (
            <div className="space-y-4">
              <div className="rounded-xl border border-border bg-surface p-4">
                <p className="text-sm font-medium text-foreground">
                  Check-in indisponível
                </p>

                <p className="mt-1 text-sm text-muted">
                  Este hóspede precisa ser alocado em um quarto antes de realizar o check-in.
                </p>
              </div>

              <div>
                <label
                  htmlFor="quarto"
                  className="mb-2 block text-sm font-medium text-foreground"
                >
                  Alocar quarto
                </label>

                <select
                  id="quarto"
                  value={
                    quartoSelecionadoId
                  }
                  onChange={(event) =>
                    setQuartoSelecionadoId(
                      event.target.value,
                    )
                  }
                  className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
                >
                  <option value="">
                    Selecione um quarto
                  </option>

                  {quartos.map(
                    (quarto) => (
                      <option
                        key={quarto.id}
                        value={quarto.id}
                      >
                        {quarto.nome} —{" "}
                        {quarto.tipo}
                      </option>
                    ),
                  )}
                </select>
              </div>

              {erroAlocacao && (
                <p
                  role="alert"
                  className="text-sm text-red-400"
                >
                  {erroAlocacao}
                </p>
              )}

              <button
                type="button"
                onClick={
                  handleAlocarQuarto
                }
                disabled={
                  alocandoQuarto ||
                  !quartoSelecionadoId
                }
                className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
              >
                {alocandoQuarto
                  ? "Alocando..."
                  : "Alocar quarto"}
              </button>
            </div>
          )}

        {checkIn &&
          checkIn.statusCheckIn !==
            "REALIZADO" &&
          checkIn.quartoId !== null && (
            <div className="space-y-4">
              <div>
                <label
                  htmlFor="observacao-checkin"
                  className="mb-2 block text-sm font-medium text-foreground"
                >
                  Observação do check-in
                </label>

                <textarea
                  id="observacao-checkin"
                  value={
                    observacaoCheckIn
                  }
                  onChange={(event) =>
                    setObservacaoCheckIn(
                      event.target.value,
                    )
                  }
                  placeholder="Adicione uma observação, se necessário."
                  rows={4}
                  className="w-full rounded-xl border border-border bg-surface px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
                />
              </div>

              {erroCheckIn && (
                <p
                  role="alert"
                  className="text-sm text-red-400"
                >
                  {erroCheckIn}
                </p>
              )}

              <button
                type="button"
                onClick={
                  handleRealizarCheckIn
                }
                disabled={
                  realizandoCheckIn
                }
                className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
              >
                {realizandoCheckIn
                  ? "Realizando check-in..."
                  : "Realizar check-in"}
              </button>
            </div>
          )}
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
            {traslados.map(
              (traslado) => (
                <Card key={traslado.id}>
                  <div className="space-y-4">
                    <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                      <div>
                        <p className="font-semibold text-foreground">
                          {traslado.tipo ===
                          "AEROPORTO_PARA_HOSPEDAGEM"
                            ? "Aeroporto → Hospedagem"
                            : traslado.tipo ===
                                "HOSPEDAGEM_PARA_AEROPORTO"
                              ? "Hospedagem → Aeroporto"
                              : "Outro traslado"}
                        </p>

                        <p className="mt-1 text-sm text-muted">
                          {
                            traslado.localOrigem
                          }{" "}
                          →{" "}
                          {
                            traslado.localDestino
                          }
                        </p>
                      </div>

                      <p className="text-sm font-medium text-foreground">
                        {traslado.status ===
                        "AGUARDANDO"
                          ? "Aguardando"
                          : traslado.status ===
                              "EM_ANDAMENTO"
                            ? "Em andamento"
                            : traslado.status ===
                                "CONCLUIDO"
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
                          ).toLocaleString(
                            "pt-BR",
                          )}
                        </p>
                      </div>

                      <div>
                        <p className="text-sm text-muted">
                          Aeroporto
                        </p>

                        <p className="mt-1 font-medium text-foreground">
                          {traslado.aeroporto ??
                            "Não informado"}
                        </p>
                      </div>

                      <div>
                        <p className="text-sm text-muted">
                          Voo
                        </p>

                        <p className="mt-1 font-medium text-foreground">
                          {traslado.numeroVoo ??
                            "Não informado"}
                        </p>
                      </div>

                      <div>
                        <p className="text-sm text-muted">
                          Companhia aérea
                        </p>

                        <p className="mt-1 font-medium text-foreground">
                          {traslado.companhiaAerea ??
                            "Não informada"}
                        </p>
                      </div>
                    </div>

                    {traslado.observacoes && (
                      <div>
                        <p className="text-sm text-muted">
                          Observações
                        </p>

                        <p className="mt-1 font-medium text-foreground">
                          {
                            traslado.observacoes
                          }
                        </p>
                      </div>
                    )}
                  </div>
                </Card>
              ),
            )}
          </div>
        )}
      </section>
    </div>
  );
}