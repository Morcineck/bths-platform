"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";

import {
  atualizarStatusOperacao,
  excluirOperacaoTraslado,
  listarOperacoesPorViagem,
  listarPassageirosDaOperacao,
} from "@/features/traslado/operacao/services/operacaoTrasladoService";

import type {
  OperacaoTraslado,
  OperacaoTrasladoPassageiro,
  StatusTraslado,
} from "@/features/traslado/operacao/types/operacaoTraslado";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

export default function TrasladosPage() {
  const [viagens, setViagens] =
    useState<Viagem[]>([]);

  const [
    viagemSelecionadaId,
    setViagemSelecionadaId,
  ] = useState<number | null>(null);

  const [
    operacoes,
    setOperacoes,
  ] = useState<OperacaoTraslado[]>([]);

  const [
    passageirosPorOperacao,
    setPassageirosPorOperacao,
  ] = useState<
    Record<
      number,
      OperacaoTrasladoPassageiro[]
    >
  >({});

  const [
    operacaoExpandidaId,
    setOperacaoExpandidaId,
  ] = useState<number | null>(null);

  const [
    carregandoViagens,
    setCarregandoViagens,
  ] = useState(true);

  const [
    carregandoOperacoes,
    setCarregandoOperacoes,
  ] = useState(false);

  const [
    carregandoPassageirosId,
    setCarregandoPassageirosId,
  ] = useState<number | null>(null);

  const [
    excluindoId,
    setExcluindoId,
  ] = useState<number | null>(null);

  const [
    atualizandoStatusId,
    setAtualizandoStatusId,
  ] = useState<number | null>(null);

  const [
    mensagemOperacao,
    setMensagemOperacao,
  ] = useState("");

  const [erro, setErro] =
    useState("");

  useEffect(() => {
    async function carregarViagens() {
      try {
        setErro("");

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
          setViagemSelecionadaId(
            viagemPreferencial.id,
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar as viagens.",
        );
      } finally {
        setCarregandoViagens(false);
      }
    }

    carregarViagens();
  }, []);

  useEffect(() => {
    if (viagemSelecionadaId === null) {
      return;
    }

    async function carregarOperacoes() {
      try {
        setCarregandoOperacoes(true);
        setErro("");
        setMensagemOperacao("");
        setOperacaoExpandidaId(null);
        setPassageirosPorOperacao({});

        const dados =
          await listarOperacoesPorViagem(
            viagemSelecionadaId!,
          );

        setOperacoes(dados);
      } catch (error) {
        setOperacoes([]);

        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar as operações de traslado.",
        );
      } finally {
        setCarregandoOperacoes(false);
      }
    }

    carregarOperacoes();
  }, [viagemSelecionadaId]);

  async function handleAtualizarStatus(
    operacaoId: number,
    novoStatus: StatusTraslado,
  ) {
    if (novoStatus === "CANCELADO") {
      const confirmar = window.confirm(
        "Deseja realmente cancelar esta operação de traslado?",
      );

      if (!confirmar) {
        return;
      }
    }

    try {
      setAtualizandoStatusId(
        operacaoId,
      );

      setErro("");
      setMensagemOperacao("");

      const operacaoAtualizada =
        await atualizarStatusOperacao(
          operacaoId,
          novoStatus,
        );

      setOperacoes((atuais) =>
        atuais.map((operacao) =>
          operacao.id === operacaoId
            ? operacaoAtualizada
            : operacao,
        ),
      );

      const mensagem =
        novoStatus === "EM_ANDAMENTO"
          ? "Operação iniciada com sucesso."
          : novoStatus === "CONCLUIDO"
            ? "Operação concluída com sucesso."
            : "Operação cancelada com sucesso.";

      setMensagemOperacao(
        mensagem,
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar o status da operação.",
      );
    } finally {
      setAtualizandoStatusId(null);
    }
  }

  async function handleExcluirOperacao(
    operacaoId: number,
  ) {
    const confirmar = window.confirm(
      "Deseja realmente excluir esta operação de traslado?",
    );

    if (!confirmar) {
      return;
    }

    try {
      setExcluindoId(
        operacaoId,
      );

      setErro("");
      setMensagemOperacao("");

      await excluirOperacaoTraslado(
        operacaoId,
      );

      setOperacoes((atuais) =>
        atuais.filter(
          (operacao) =>
            operacao.id !== operacaoId,
        ),
      );

      setPassageirosPorOperacao(
        (atuais) => {
          const copia = {
            ...atuais,
          };

          delete copia[operacaoId];

          return copia;
        },
      );

      if (
        operacaoExpandidaId ===
        operacaoId
      ) {
        setOperacaoExpandidaId(null);
      }

      setMensagemOperacao(
        "Operação excluída com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível excluir a operação.",
      );
    } finally {
      setExcluindoId(null);
    }
  }

  async function handleVerPassageiros(
    operacaoId: number,
  ) {
    if (
      operacaoExpandidaId === operacaoId
    ) {
      setOperacaoExpandidaId(null);

      return;
    }

    if (
      passageirosPorOperacao[
        operacaoId
      ]
    ) {
      setOperacaoExpandidaId(
        operacaoId,
      );

      return;
    }

    try {
      setCarregandoPassageirosId(
        operacaoId,
      );

      setErro("");

      const passageiros =
        await listarPassageirosDaOperacao(
          operacaoId,
        );

      setPassageirosPorOperacao(
        (atuais) => ({
          ...atuais,
          [operacaoId]:
            passageiros,
        }),
      );

      setOperacaoExpandidaId(
        operacaoId,
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível carregar os passageiros da operação.",
      );
    } finally {
      setCarregandoPassageirosId(
        null,
      );
    }
  }

  if (carregandoViagens) {
    return (
      <p className="text-sm text-muted">
        Carregando traslados...
      </p>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
        <PageHeader
          title="Traslados"
          description="Gestão das operações de transporte e ocupação dos veículos."
        />

        <div className="flex w-full flex-col gap-3 sm:flex-row sm:items-center md:w-auto">
          <div className="relative w-full sm:w-[235px]">
            <select
              id="viagem"
              value={
                viagemSelecionadaId ?? ""
              }
              onChange={(event) =>
                setViagemSelecionadaId(
                  Number(
                    event.target.value,
                  ),
                )
              }
              disabled={
                viagens.length === 0
              }
              aria-label="Selecionar viagem"
              className="
                h-12
                w-full
                appearance-none
                rounded-xl
                border
                border-border
                bg-surface
                px-4
                pr-11
                text-sm
                font-medium
                text-foreground
                outline-none
                transition-all
                duration-200
                hover:border-primary/60
                focus:border-primary
                focus:ring-2
                focus:ring-primary/20
                disabled:cursor-not-allowed
                disabled:opacity-50
              "
            >
              {viagens.length === 0 ? (
                <option value="">
                  Nenhuma viagem disponível
                </option>
              ) : (
                viagens.map(
                  (viagem) => (
                    <option
                      key={viagem.id}
                      value={viagem.id}
                    >
                      {viagem.nome}
                    </option>
                  ),
                )
              )}
            </select>

            <div className="pointer-events-none absolute inset-y-0 right-4 flex items-center">
              <svg
                viewBox="0 0 20 20"
                fill="none"
                aria-hidden="true"
                className="h-4 w-4 text-muted"
              >
                <path
                  d="M6 8L10 12L14 8"
                  stroke="currentColor"
                  strokeWidth="1.8"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            </div>
          </div>

          <Link
            href="/traslados/nova"
            className="
              inline-flex
              h-12
              shrink-0
              items-center
              justify-center
              rounded-xl
              bg-primary
              px-6
              text-sm
              font-semibold
              text-white
              transition-all
              duration-200
              hover:opacity-90
              focus:outline-none
              focus:ring-2
              focus:ring-primary/30
            "
          >
            Nova operação
          </Link>
        </div>
      </div>

      {erro && (
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>
      )}

      {mensagemOperacao && (
        <p className="text-sm text-green-400">
          {mensagemOperacao}
        </p>
      )}

      {carregandoOperacoes ? (
        <Card>
          <p className="text-sm text-muted">
            Carregando operações de traslado...
          </p>
        </Card>
      ) : operacoes.length === 0 ? (
        <Card>
          <p className="font-medium text-foreground">
            Nenhuma operação de traslado encontrada.
          </p>

          <p className="mt-2 text-sm text-muted">
            Ainda não existem grupos de transporte
            cadastrados para esta viagem.
          </p>
        </Card>
      ) : (
        <section className="space-y-4">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Operações de transporte
            </h2>

            <p className="mt-1 text-sm text-muted">
              Veículos, motoristas e ocupação atual
              da viagem selecionada.
            </p>
          </div>

          <div className="grid gap-4 lg:grid-cols-2">
            {operacoes.map(
              (operacao) => {
                const capacidade =
                  operacao.capacidadePassageiros ??
                  0;

                const passageiros =
                  operacao.quantidadePassageiros;

                const vagas =
                  operacao.vagasDisponiveis ??
                  0;

                const lotada =
                  capacidade > 0 &&
                  passageiros >=
                    capacidade;

                const listaPassageiros =
                  passageirosPorOperacao[
                    operacao.id
                  ] ?? [];

                const expandida =
                  operacaoExpandidaId ===
                  operacao.id;

                const carregandoPassageiros =
                  carregandoPassageirosId ===
                  operacao.id;

                const atualizandoStatus =
                  atualizandoStatusId ===
                  operacao.id;

                return (
                  <Card
                    key={operacao.id}
                    className="space-y-5"
                  >
                    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                      <div>
                        <p className="text-xs font-medium uppercase tracking-wide text-muted">
                          Operação #
                          {operacao.id}
                        </p>

                        <h3 className="mt-1 text-lg font-semibold text-foreground">
                          {operacao.veiculoModelo ??
                            "Veículo não definido"}
                        </h3>

                        <p className="mt-1 text-sm text-muted">
                          {operacao.veiculoPlaca ??
                            "Placa não informada"}
                        </p>
                      </div>

                      <span
                        className={
                          lotada
                            ? "inline-flex w-fit rounded-full border border-red-400/40 px-3 py-1 text-xs font-semibold text-red-400"
                            : "inline-flex w-fit rounded-full border border-primary/40 px-3 py-1 text-xs font-semibold text-primary"
                        }
                      >
                        {lotada
                          ? "Lotado"
                          : `${vagas} ${
                              vagas === 1
                                ? "vaga"
                                : "vagas"
                            }`}
                      </span>

                      <span
                        className="inline-flex w-fit rounded-full border border-border px-3 py-1 text-xs font-semibold text-foreground"
                      >
                        {operacao.status === "AGUARDANDO"
                          ? "Aguardando"
                          : operacao.status ===
                              "EM_ANDAMENTO"
                            ? "Em andamento"
                            : operacao.status ===
                                "CONCLUIDO"
                              ? "Concluída"
                              : "Cancelada"}
                      </span>
                    </div>

                    <div className="grid gap-4 sm:grid-cols-2">
                      <div>
                        <p className="text-sm text-muted">
                          Motorista
                        </p>

                        <p className="mt-1 font-medium text-foreground">
                          {operacao.motoristaNome ??
                            "Não definido"}
                        </p>
                      </div>

                      <div>
                        <p className="text-sm text-muted">
                          Viagem
                        </p>

                        <p className="mt-1 font-medium text-foreground">
                          {
                            operacao.viagemNome
                          }
                        </p>
                      </div>
                    </div>

                    <div className="rounded-xl border border-border bg-background/40 p-4">
                      <div className="flex items-end justify-between gap-4">
                        <div>
                          <p className="text-sm text-muted">
                            Ocupação
                          </p>

                          <p className="mt-1 text-2xl font-semibold text-foreground">
                            {passageiros} /{" "}
                            {capacidade}
                          </p>
                        </div>

                        <p className="text-sm font-medium text-muted">
                          {vagas}{" "}
                          {vagas === 1
                            ? "vaga disponível"
                            : "vagas disponíveis"}
                        </p>
                      </div>

                      {capacidade > 0 && (
                        <div className="mt-4 h-2 overflow-hidden rounded-full bg-border">
                          <div
                            className="h-full rounded-full bg-primary transition-all"
                            style={{
                              width: `${Math.min(
                                (passageiros /
                                  capacidade) *
                                  100,
                                100,
                              )}%`,
                            }}
                          />
                        </div>
                      )}
                    </div>

                    <div className="border-t border-border pt-4">
                      <div className="flex items-center justify-between gap-4">
                        <div>
                          <p className="text-sm text-muted">
                            Passageiros
                          </p>

                          <p className="mt-1 text-sm font-medium text-foreground">
                            {passageiros === 0
                              ? "Nenhum traslado vinculado."
                              : `${passageiros} ${
                                  passageiros ===
                                  1
                                    ? "hóspede vinculado"
                                    : "hóspedes vinculados"
                                }`}
                          </p>
                        </div>

                        <button
                          type="button"
                          onClick={() =>
                            handleVerPassageiros(
                              operacao.id,
                            )
                          }
                          disabled={
                            carregandoPassageiros
                          }
                          className="text-sm font-medium text-primary transition-opacity hover:opacity-80 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          {carregandoPassageiros
                            ? "Carregando..."
                            : expandida
                              ? "Ocultar passageiros"
                              : "Ver passageiros"}
                        </button>
                      </div>

                      {expandida && (
                        <div className="mt-4 space-y-2">
                          {listaPassageiros.length ===
                          0 ? (
                            <p className="text-sm text-muted">
                              Nenhum passageiro vinculado a esta operação.
                            </p>
                          ) : (
                            listaPassageiros.map(
                              (
                                passageiro,
                              ) => (
                                <div
                                  key={
                                    passageiro.trasladoId
                                  }
                                  className="rounded-xl border border-border bg-background/40 px-4 py-3"
                                >
                                  <p className="font-medium text-foreground">
                                    {
                                      passageiro.hospedeNome
                                    }
                                  </p>

                                  <p className="mt-1 text-xs text-muted">
                                    Hóspede #
                                    {
                                      passageiro.hospedeId
                                    }
                                  </p>
                                </div>
                              ),
                            )
                          )}
                        </div>
                      )}
                    </div>

                    <div className="flex flex-col justify-end gap-3 border-t border-border pt-4 sm:flex-row">
                      {operacao.status ===
                        "AGUARDANDO" && (
                        <>
                          <button
                            type="button"
                            onClick={() =>
                              handleAtualizarStatus(
                                operacao.id,
                                "EM_ANDAMENTO",
                              )
                            }
                            disabled={
                              atualizandoStatus
                            }
                            className="inline-flex h-10 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            {atualizandoStatus
                              ? "Atualizando..."
                              : "Iniciar operação"}
                          </button>

                          <button
                            type="button"
                            onClick={() =>
                              handleAtualizarStatus(
                                operacao.id,
                                "CANCELADO",
                              )
                            }
                            disabled={
                              atualizandoStatus
                            }
                            className="inline-flex h-10 items-center justify-center rounded-xl border border-red-500/40 px-4 text-sm font-medium text-red-400 transition-colors hover:border-red-400 hover:text-red-300 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            Cancelar operação
                          </button>
                        </>
                      )}
                      {operacao.status ===
                        "EM_ANDAMENTO" && (
                        <>
                          <button
                            type="button"
                            onClick={() =>
                              handleAtualizarStatus(
                                operacao.id,
                                "CONCLUIDO",
                              )
                            }
                            disabled={
                              atualizandoStatus
                            }
                            className="inline-flex h-10 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            {atualizandoStatus
                              ? "Atualizando..."
                              : "Concluir operação"}
                          </button>

                          <button
                            type="button"
                            onClick={() =>
                              handleAtualizarStatus(
                                operacao.id,
                                "CANCELADO",
                              )
                            }
                            disabled={
                              atualizandoStatus
                            }
                            className="inline-flex h-10 items-center justify-center rounded-xl border border-red-500/40 px-4 text-sm font-medium text-red-400 transition-colors hover:border-red-400 hover:text-red-300 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            Cancelar operação
                          </button>
                        </>
                      )}
                      <Link
                        href={`/traslados/${operacao.id}/editar`}
                        className="inline-flex h-10 items-center justify-center rounded-xl border border-border px-4 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
                      >
                        Editar
                      </Link>

                      <button
                        type="button"
                        onClick={() =>
                          handleExcluirOperacao(
                            operacao.id,
                          )
                        }
                        disabled={
                          excluindoId ===
                          operacao.id
                        }
                        className="inline-flex h-10 items-center justify-center rounded-xl border border-red-500/40 px-4 text-sm font-medium text-red-400 transition-colors hover:border-red-400 hover:text-red-300 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        {excluindoId ===
                        operacao.id
                          ? "Excluindo..."
                          : "Excluir"}
                      </button>
                    </div>
                  </Card>
                );
              },
            )}
          </div>
        </section>
      )}
    </div>
  );
}