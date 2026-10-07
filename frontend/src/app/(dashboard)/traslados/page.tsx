"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

import {
  Bus,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Clock3,
  History,
  Pencil,
  Play,
  RotateCcw,
  Trash2,
  UserRound,
  Users,
  XCircle,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  atualizarStatusOperacao,
  corrigirStatusOperacao,
  excluirOperacaoTraslado,
  listarHistoricoStatusOperacao,
  listarOperacoesPorViagem,
  listarPassageirosDaOperacao,
} from "@/features/traslado/operacao/services/operacaoTrasladoService";

import type {
  HistoricoStatusOperacaoTraslado,
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

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import type {
  PerfilUsuario,
} from "@/features/auth/types/auth";

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
    historicoPorOperacao,
    setHistoricoPorOperacao,
  ] = useState<
    Record<
      number,
      HistoricoStatusOperacaoTraslado[]
    >
  >({});

  const [
    operacaoExpandidaId,
    setOperacaoExpandidaId,
  ] = useState<number | null>(null);

  const [
    historicoExpandidoId,
    setHistoricoExpandidoId,
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
    carregandoHistoricoId,
    setCarregandoHistoricoId,
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
    perfilUsuario,
    setPerfilUsuario,
  ] = useState<PerfilUsuario | null>(null);

  const [
    corrigindoStatusId,
    setCorrigindoStatusId,
  ] = useState<number | null>(null);

  const [
    motivoCorrecao,
    setMotivoCorrecao,
  ] = useState("");

  const [
    enviandoCorrecaoId,
    setEnviandoCorrecaoId,
  ] = useState<number | null>(null);

  const [
    mensagemOperacao,
    setMensagemOperacao,
  ] = useState("");

  const [erro, setErro] =
    useState("");

  const isAdmin =
    perfilUsuario === "ADMIN";

  useEffect(() => {
    async function carregarPerfilUsuario() {
      try {
        const usuario =
          await buscarUsuarioAutenticado();

        setPerfilUsuario(
          usuario.perfil,
        );
      } catch {
        setPerfilUsuario(null);
      }
    }

    carregarPerfilUsuario();
  }, []);

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

  async function handleCorrigirStatus(
    operacao: OperacaoTraslado,
  ) {
    const motivo =
      motivoCorrecao.trim();

    if (!motivo) {
      setErro(
        "Informe o motivo da correção.",
      );

      return;
    }

    const novoStatus =
      operacao.status === "CONCLUIDO"
        ? "EM_ANDAMENTO"
        : "AGUARDANDO";

    try {
      setEnviandoCorrecaoId(
        operacao.id,
      );

      setErro("");
      setMensagemOperacao("");

      const operacaoAtualizada =
        await corrigirStatusOperacao(
          operacao.id,
          novoStatus,
          motivo,
        );

      setOperacoes((atuais) =>
        atuais.map((item) =>
          item.id === operacao.id
            ? operacaoAtualizada
            : item,
        ),
      );

      setCorrigindoStatusId(null);
      setMotivoCorrecao("");

      setMensagemOperacao(
        "Status da operação corrigido com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível corrigir o status da operação.",
      );
    } finally {
      setEnviandoCorrecaoId(null);
    }
  }

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

  async function handleVerHistorico(
    operacaoId: number,
  ) {
    if (
      historicoExpandidoId === operacaoId
    ) {
      setHistoricoExpandidoId(null);

      return;
    }

    if (
      historicoPorOperacao[
        operacaoId
      ]
    ) {
      setHistoricoExpandidoId(
        operacaoId,
      );

      return;
    }

    try {
      setCarregandoHistoricoId(
        operacaoId,
      );

      setErro("");

      const historico =
        await listarHistoricoStatusOperacao(
          operacaoId,
        );

      setHistoricoPorOperacao(
        (atuais) => ({
          ...atuais,
          [operacaoId]:
            historico,
        }),
      );

      setHistoricoExpandidoId(
        operacaoId,
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível carregar o histórico da operação.",
      );
    } finally {
      setCarregandoHistoricoId(
        null,
      );
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
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Traslados"
          description="Gestão das operações de transporte e ocupação dos veículos."
        />

        <div className="flex w-full flex-col gap-3 sm:flex-row sm:items-end lg:w-auto">
          <div className="w-full sm:w-[260px]">
            <label
              htmlFor="viagem"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Viagem ativa
            </label>

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
              className="h-11 w-full rounded-xl border border-border bg-surface/70 px-4 text-sm font-medium text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
          </div>

          <Link
            href="/traslados/nova"
            className="gradient-brand inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
          >
            <Bus
              size={18}
              strokeWidth={1.9}
            />

            Nova operação
          </Link>
        </div>
      </div>

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {mensagemOperacao && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {mensagemOperacao}
        </div>
      )}

      {carregandoOperacoes ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-4">
          <p className="text-sm text-muted">
            Carregando operações de traslado...
          </p>
        </div>
      ) : operacoes.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <Bus
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhuma operação encontrada
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Ainda não existem grupos de transporte cadastrados para esta viagem.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Operações de transporte
              </h2>

              <p className="mt-1 text-xs text-muted">
                Veículos, motoristas e ocupação da viagem selecionada.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {operacoes.length}{" "}
              {operacoes.length === 1
                ? "operação"
                : "operações"}
            </span>
          </div>

          <div className="grid gap-3 lg:grid-cols-2">
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

                const ocupacaoPercentual =
                  capacidade > 0
                    ? Math.min(
                        (passageiros /
                          capacidade) *
                          100,
                        100,
                      )
                    : 0;

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

                const listaHistorico =
                  historicoPorOperacao[
                    operacao.id
                  ] ?? [];

                const historicoExpandido =
                  historicoExpandidoId ===
                  operacao.id;

                const carregandoHistorico =
                  carregandoHistoricoId ===
                  operacao.id;

                const atualizandoStatus =
                  atualizandoStatusId ===
                  operacao.id;

                const aguardando =
                  operacao.status ===
                  "AGUARDANDO";

                const emAndamento =
                  operacao.status ===
                  "EM_ANDAMENTO";

                const concluido =
                  operacao.status ===
                  "CONCLUIDO";

                const cancelado =
                  operacao.status ===
                  "CANCELADO";

                return (
                  <article
                    key={operacao.id}
                    className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div className="min-w-0">
                        <p className="text-[11px] font-medium uppercase tracking-[0.12em] text-muted">
                          Operação #{operacao.id}
                        </p>

                        <h3 className="mt-1 truncate text-base font-semibold text-foreground">
                          {operacao.veiculoModelo ??
                            "Veículo não definido"}
                        </h3>

                        <p className="mt-1 text-xs text-muted">
                          {operacao.veiculoPlaca ??
                            "Placa não informada"}
                        </p>
                      </div>

                      <span
                        className={
                          concluido
                            ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                            : emAndamento
                              ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-magenta/30 bg-magenta/10 px-2.5 py-1 text-[11px] font-medium text-magenta"
                              : cancelado
                                ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                                : "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-warning/30 bg-warning/10 px-2.5 py-1 text-[11px] font-medium text-warning"
                        }
                      >
                        {concluido ? (
                          <CheckCircle2
                            size={13}
                            strokeWidth={2}
                          />
                        ) : emAndamento ? (
                          <Play
                            size={13}
                            strokeWidth={2}
                          />
                        ) : cancelado ? (
                          <XCircle
                            size={13}
                            strokeWidth={2}
                          />
                        ) : (
                          <Clock3
                            size={13}
                            strokeWidth={2}
                          />
                        )}

                        {aguardando
                          ? "Aguardando"
                          : emAndamento
                            ? "Em andamento"
                            : concluido
                              ? "Concluída"
                              : "Cancelada"}
                      </span>
                    </div>

                    <div className="mt-4 flex items-center gap-3 rounded-xl border border-border bg-background/25 px-3 py-2.5">
                      <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                        <UserRound
                          size={17}
                          strokeWidth={1.9}
                        />
                      </span>

                      <div className="min-w-0">
                        <p className="text-[11px] text-muted">
                          Motorista
                        </p>

                        <p className="truncate text-sm font-medium text-foreground">
                          {operacao.motoristaNome ??
                            "Não definido"}
                        </p>
                      </div>
                    </div>

                    <div className="mt-3 rounded-xl border border-border bg-background/25 p-3">
                      <div className="flex items-end justify-between gap-3">
                        <div>
                          <p className="text-[11px] text-muted">
                            Ocupação
                          </p>

                          <p className="mt-1 text-lg font-semibold text-foreground">
                            {passageiros} / {capacidade}
                          </p>
                        </div>

                        <div className="text-right">
                          <p className="text-xs font-medium text-foreground">
                            {vagas}{" "}
                            {vagas === 1
                              ? "vaga"
                              : "vagas"}
                          </p>

                          <p className="mt-1 text-[11px] text-muted">
                            disponíveis
                          </p>
                        </div>
                      </div>

                      {capacidade > 0 && (
                        <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-border">
                          <div
                            className={
                              ocupacaoPercentual >= 100
                                ? "h-full rounded-full bg-danger transition-all"
                                : ocupacaoPercentual >= 70
                                  ? "h-full rounded-full bg-warning transition-all"
                                  : "h-full rounded-full bg-primary transition-all"
                            }
                            style={{
                              width: `${ocupacaoPercentual}%`,
                            }}
                          />
                        </div>
                      )}
                    </div>

                    <div className="mt-3 grid grid-cols-2 gap-2">
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
                        className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-background/20 px-3 text-xs font-medium text-foreground transition-colors hover:border-primary/30 hover:bg-primary/5 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        <Users
                          size={15}
                          strokeWidth={1.9}
                        />

                        {carregandoPassageiros
                          ? "Carregando..."
                          : expandida
                            ? "Ocultar"
                            : "Passageiros"}

                        {expandida ? (
                          <ChevronUp
                            size={14}
                            strokeWidth={1.9}
                          />
                        ) : (
                          <ChevronDown
                            size={14}
                            strokeWidth={1.9}
                          />
                        )}
                      </button>

                      <button
                        type="button"
                        onClick={() =>
                          handleVerHistorico(
                            operacao.id,
                          )
                        }
                        disabled={
                          carregandoHistorico
                        }
                        className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-background/20 px-3 text-xs font-medium text-foreground transition-colors hover:border-primary/30 hover:bg-primary/5 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        <History
                          size={15}
                          strokeWidth={1.9}
                        />

                        {carregandoHistorico
                          ? "Carregando..."
                          : historicoExpandido
                            ? "Ocultar"
                            : "Histórico"}

                        {historicoExpandido ? (
                          <ChevronUp
                            size={14}
                            strokeWidth={1.9}
                          />
                        ) : (
                          <ChevronDown
                            size={14}
                            strokeWidth={1.9}
                          />
                        )}
                      </button>
                    </div>

                    {expandida && (
                      <div className="mt-2.5 space-y-1.5 rounded-xl border border-border bg-background/20 p-2.5">
                        {listaPassageiros.length ===
                        0 ? (
                          <p className="text-xs text-muted">
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
                                className="rounded-lg border border-border bg-surface/40 px-3 py-2"
                              >
                                <p className="text-sm font-medium text-foreground">
                                  {
                                    passageiro.hospedeNome
                                  }
                                </p>

                                <p className="mt-1 text-[11px] text-muted">
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

                    {historicoExpandido && (
                      <div className="mt-2.5 space-y-1.5 rounded-xl border border-border bg-background/20 p-2.5">
                        {listaHistorico.length ===
                        0 ? (
                          <p className="text-xs text-muted">
                            Nenhuma alteração de status registrada.
                          </p>
                        ) : (
                          [...listaHistorico]
                            .sort(
                              (a, b) =>
                                new Date(
                                  b.dataHora,
                                ).getTime() -
                                new Date(
                                  a.dataHora,
                                ).getTime(),
                            )
                            .map(
                              (historico) => (
                                <div
                                  key={historico.id}
                                  className="rounded-lg border border-border bg-surface/40 p-2.5"
                                >
                                  <div className="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
                                    <p className="text-xs font-medium text-foreground">
                                      {historico.statusAnterior === "AGUARDANDO"
                                        ? "Aguardando"
                                        : historico.statusAnterior === "EM_ANDAMENTO"
                                          ? "Em andamento"
                                          : historico.statusAnterior === "CONCLUIDO"
                                            ? "Concluída"
                                            : "Cancelada"}

                                      {" → "}

                                      {historico.novoStatus === "AGUARDANDO"
                                        ? "Aguardando"
                                        : historico.novoStatus === "EM_ANDAMENTO"
                                          ? "Em andamento"
                                          : historico.novoStatus === "CONCLUIDO"
                                            ? "Concluída"
                                            : "Cancelada"}
                                    </p>

                                    <p className="text-[11px] text-muted">
                                      {new Date(
                                        historico.dataHora,
                                      ).toLocaleString(
                                        "pt-BR",
                                      )}
                                    </p>
                                  </div>

                                  <p className="mt-2 text-[11px] text-muted">
                                    Motivo
                                  </p>

                                  <p className="mt-1 text-xs font-medium text-foreground">
                                    {historico.motivo}
                                  </p>
                                </div>
                              ),
                            )
                        )}
                      </div>
                    )}

                    {corrigindoStatusId ===
                      operacao.id && (
                      <div className="mt-3 space-y-3 rounded-xl border border-primary/20 bg-primary/5 p-3">
                        <div>
                          <p className="text-sm font-semibold text-foreground">
                            Corrigir status
                          </p>

                          <p className="mt-1 text-xs text-muted">
                            {operacao.status === "CONCLUIDO"
                              ? "A operação voltará para Em andamento."
                              : "A operação voltará para Aguardando."}
                          </p>
                        </div>

                        <textarea
                          id={`motivo-correcao-${operacao.id}`}
                          value={motivoCorrecao}
                          onChange={(event) =>
                            setMotivoCorrecao(
                              event.target.value,
                            )
                          }
                          rows={3}
                          placeholder="Explique por que o status precisa ser corrigido."
                          className="w-full resize-none rounded-xl border border-border bg-background/30 px-3 py-2.5 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
                        />

                        <div className="flex flex-col gap-2 sm:flex-row">
                          <button
                            type="button"
                            onClick={() =>
                              handleCorrigirStatus(
                                operacao,
                              )
                            }
                            disabled={
                              enviandoCorrecaoId ===
                                operacao.id ||
                              !motivoCorrecao.trim()
                            }
                            className="inline-flex h-10 flex-1 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            {enviandoCorrecaoId ===
                            operacao.id
                              ? "Corrigindo..."
                              : "Confirmar correção"}
                          </button>

                          <button
                            type="button"
                            onClick={() => {
                              setCorrigindoStatusId(
                                null,
                              );

                              setMotivoCorrecao("");
                              setErro("");
                            }}
                            disabled={
                              enviandoCorrecaoId ===
                              operacao.id
                            }
                            className="inline-flex h-10 flex-1 items-center justify-center rounded-xl border border-border px-4 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
                          >
                            Cancelar
                          </button>
                        </div>
                      </div>
                    )}

                    <div className="mt-4 border-t border-border pt-3">
                      <div className="flex flex-wrap gap-2">
                        {aguardando && (
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
                              className="inline-flex h-9 flex-1 items-center justify-center gap-2 rounded-xl bg-primary px-3 text-xs font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              <Play
                                size={14}
                                strokeWidth={2}
                              />

                              {atualizandoStatus
                                ? "Atualizando..."
                                : "Iniciar"}
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
                              className="inline-flex h-9 flex-1 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              <XCircle
                                size={14}
                                strokeWidth={2}
                              />

                              Cancelar
                            </button>
                          </>
                        )}

                        {emAndamento && (
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
                              className="inline-flex h-9 flex-1 items-center justify-center gap-2 rounded-xl bg-success px-3 text-xs font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              <CheckCircle2
                                size={14}
                                strokeWidth={2}
                              />

                              {atualizandoStatus
                                ? "Atualizando..."
                                : "Concluir"}
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
                              className="inline-flex h-9 flex-1 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              <XCircle
                                size={14}
                                strokeWidth={2}
                              />

                              Cancelar
                            </button>
                          </>
                        )}

                        {isAdmin &&
                          (concluido ||
                            cancelado) && (
                            <button
                              type="button"
                              onClick={() => {
                                setCorrigindoStatusId(
                                  operacao.id,
                                );

                                setMotivoCorrecao("");
                                setErro("");
                                setMensagemOperacao("");
                              }}
                              disabled={
                                enviandoCorrecaoId ===
                                operacao.id
                              }
                              className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              <RotateCcw
                                size={14}
                                strokeWidth={2}
                              />

                              Corrigir status
                            </button>
                          )}
                      </div>

                      <div className="mt-2 flex gap-2">
                        <Link
                          href={`/traslados/${operacao.id}/editar`}
                          className="inline-flex h-9 flex-1 items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-foreground transition-colors hover:border-primary/30 hover:text-primary"
                        >
                          <Pencil
                            size={14}
                            strokeWidth={1.9}
                          />

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
                          className="inline-flex h-9 flex-1 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          <Trash2
                            size={14}
                            strokeWidth={1.9}
                          />

                          {excluindoId ===
                          operacao.id
                            ? "Excluindo..."
                            : "Excluir"}
                        </button>
                      </div>
                    </div>
                  </article>
                );
              },
            )}
          </div>
        </section>
      )}
    </div>
  );
}