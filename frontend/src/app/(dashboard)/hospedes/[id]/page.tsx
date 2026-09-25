"use client";

import Link from "next/link";
import { use, useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";

import {
  alocarHospedeEmQuarto,
  buscarAlocacaoPorHospedeEViagem,
  trocarQuarto,
} from "@/features/alocacao/services/alocacaoService";

import type {
  AlocacaoQuarto,
} from "@/features/alocacao/types/alocacao";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import {
  consultarCheckIn,
  realizarCheckIn,
  registrarNaoComparecimento,
} from "@/features/checkin/services/checkInService";

import type {
  CheckInResponse,
} from "@/features/checkin/types/checkin";

import {
  buscarHospedePorId,
} from "@/features/hospede/services/hospedeService";

import type {
  Hospede,
} from "@/features/hospede/types/hospede";

import {
  listarMotoristas,
} from "@/features/motorista/services/motoristaService";

import type {
  Motorista,
} from "@/features/motorista/types/motorista";

import {
  associarOperacaoTraslado,
  listarTrasladosPorHospede,
} from "@/features/traslado/services/trasladoService";

import {
  listarVeiculos,
} from "@/features/veiculo/services/veiculoService";

import type {
  Veiculo,
} from "@/features/veiculo/types/veiculo";

import {
  listarOperacoesPorViagem,
  vincularTrasladoOperacao,
} from "@/features/traslado/operacao/services/operacaoTrasladoService";

import type {
  OperacaoTraslado,
} from "@/features/traslado/operacao/types/operacaoTraslado";

import {
  listarQuartosPorViagem,
} from "@/features/quarto/services/quartoService";

import type {
  Quarto,
} from "@/features/quarto/types/quarto";

import type {
  Traslado,
} from "@/features/traslado/types/traslado";

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

  const [alocacao, setAlocacao] =
    useState<AlocacaoQuarto | null>(null);

  const [
    quartoSelecionadoId,
    setQuartoSelecionadoId,
  ] = useState("");

  const [
    alocandoQuarto,
    setAlocandoQuarto,
  ] = useState(false);

  const [
    erroAlocacao,
    setErroAlocacao,
  ] = useState("");

  const [
    sucessoAlocacao,
    setSucessoAlocacao,
  ] = useState("");

  const [
    novoQuartoId,
    setNovoQuartoId,
  ] = useState("");

  const [
    trocandoQuarto,
    setTrocandoQuarto,
  ] = useState(false);

  const [
    erroTrocaQuarto,
    setErroTrocaQuarto,
  ] = useState("");

  const [
    sucessoTrocaQuarto,
    setSucessoTrocaQuarto,
  ] = useState("");

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState("");

  const [
    isAdmin,
    setIsAdmin,
  ] = useState(false);

  const [
    observacaoCheckIn,
    setObservacaoCheckIn,
  ] = useState("");

  const [
    realizandoCheckIn,
    setRealizandoCheckIn,
  ] = useState(false);

  const [
    erroCheckIn,
    setErroCheckIn,
  ] = useState("");

  const [
    motivoNaoComparecimento,
    setMotivoNaoComparecimento,
  ] = useState("");

  const [
    registrandoNaoComparecimento,
    setRegistrandoNaoComparecimento,
  ] = useState(false);

  const [
    erroNaoComparecimento,
    setErroNaoComparecimento,
  ] = useState("");

  const [
    sucessoNaoComparecimento,
    setSucessoNaoComparecimento,
  ] = useState("");

  const [
    motoristas,
    setMotoristas,
  ] = useState<Motorista[]>([]);

  const [
    veiculos,
    setVeiculos,
  ] = useState<Veiculo[]>([]);

  const [
    motoristaSelecionadoPorTraslado,
    setMotoristaSelecionadoPorTraslado,
  ] = useState<Record<number, string>>({});

  const [
    veiculoSelecionadoPorTraslado,
    setVeiculoSelecionadoPorTraslado,
  ] = useState<Record<number, string>>({});

  const [
    salvandoOperacaoId,
    setSalvandoOperacaoId,
  ] = useState<number | null>(null);

  const [
    erroOperacao,
    setErroOperacao,
  ] = useState<Record<number, string>>({});

  const [
    sucessoOperacao,
    setSucessoOperacao,
  ] = useState<Record<number, string>>({});

  const [
    operacoesCompartilhadas,
    setOperacoesCompartilhadas,
  ] = useState<OperacaoTraslado[]>([]);

  const [
    operacaoCompartilhadaSelecionadaPorTraslado,
    setOperacaoCompartilhadaSelecionadaPorTraslado,
  ] = useState<Record<number, string>>({});

  const [
    vinculandoOperacaoCompartilhadaId,
    setVinculandoOperacaoCompartilhadaId,
  ] = useState<number | null>(null);

  const [
    erroOperacaoCompartilhada,
    setErroOperacaoCompartilhada,
  ] = useState<Record<number, string>>({});

  const [
    sucessoOperacaoCompartilhada,
    setSucessoOperacaoCompartilhada,
  ] = useState<Record<number, string>>({});

  const [
    editandoOperacaoCompartilhadaId,
    setEditandoOperacaoCompartilhadaId,
  ] = useState<number | null>(null);

  useEffect(() => {
    async function carregarHospede() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        setIsAdmin(
          usuario.perfil === "ADMIN",
        );

        if (
          usuario.perfil === "ADMIN"
        ) {
          const [
            dadosMotoristas,
            dadosVeiculos,
          ] = await Promise.all([
            listarMotoristas(),
            listarVeiculos(),
          ]);

          setMotoristas(
            dadosMotoristas.filter(
              (motorista) =>
                motorista.ativo,
            ),
          );

          setVeiculos(
            dadosVeiculos.filter(
              (veiculo) =>
                veiculo.ativo,
            ),
          );
        }

        const dados =
          await buscarHospedePorId(
            Number(id),
          );

        setHospede(dados);

        const dadosCheckIn =
          await consultarCheckIn(
            Number(id),
          );

        setCheckIn(
          dadosCheckIn,
        );

        if (
          dadosCheckIn.quartoId !==
          null
        ) {
          const dadosAlocacao =
            await buscarAlocacaoPorHospedeEViagem(
              Number(id),
              dados.viagemId,
            );

          setAlocacao(
            dadosAlocacao,
          );
        }

        const dadosQuartos =
          await listarQuartosPorViagem(
            dados.viagemId,
          );

        setQuartos(
          dadosQuartos.filter(
            (quarto) =>
              quarto.status ===
              "DISPONIVEL",
          ),
        );

        const [
          dadosTraslados,
          dadosOperacoesCompartilhadas,
        ] = await Promise.all([
          listarTrasladosPorHospede(
            Number(id),
          ),

          listarOperacoesPorViagem(
            dados.viagemId,
          ),
        ]);

        setTraslados(
          dadosTraslados,
        );

        setOperacoesCompartilhadas(
          dadosOperacoesCompartilhadas,
        );

        setOperacaoCompartilhadaSelecionadaPorTraslado(
          Object.fromEntries(
            dadosTraslados.map(
              (traslado) => [
                traslado.id,

                traslado.operacaoTrasladoId !==
                null
                  ? String(
                      traslado.operacaoTrasladoId,
                    )
                  : "",
              ],
            ),
          ),
        );
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
      setSucessoAlocacao("");

      const novaAlocacao =
        await alocarHospedeEmQuarto({
          hospedeId: Number(id),

          quartoId: Number(
            quartoSelecionadoId,
          ),
        });

      setAlocacao(
        novaAlocacao,
      );

      setCheckIn(
        (checkInAtual) =>
          checkInAtual
            ? {
                ...checkInAtual,

                quartoId:
                  novaAlocacao.quartoId,

                quartoNome:
                  novaAlocacao.quartoNome,
              }
            : checkInAtual,
      );

      setQuartoSelecionadoId("");

      setSucessoAlocacao(
        `Hóspede alocado com sucesso no quarto ${novaAlocacao.quartoNome}.`,
      );
    } catch {
      setSucessoAlocacao("");

      setErroAlocacao(
        "Não foi possível alocar o hóspede no quarto.",
      );
    } finally {
      setAlocandoQuarto(false);
    }
  }

  async function handleTrocarQuarto() {
    if (!alocacao) {
      setErroTrocaQuarto(
        "Alocação do hóspede não encontrada.",
      );

      return;
    }

    if (!novoQuartoId) {
      setErroTrocaQuarto(
        "Selecione o novo quarto.",
      );

      return;
    }

    try {
      setTrocandoQuarto(true);
      setErroTrocaQuarto("");
      setSucessoTrocaQuarto("");

      const alocacaoAtualizada =
        await trocarQuarto(
          alocacao.id,
          Number(novoQuartoId),
        );

      setAlocacao(
        alocacaoAtualizada,
      );

      setCheckIn(
        (checkInAtual) =>
          checkInAtual
            ? {
                ...checkInAtual,

                quartoId:
                  alocacaoAtualizada.quartoId,

                quartoNome:
                  alocacaoAtualizada.quartoNome,
              }
            : checkInAtual,
      );

      setNovoQuartoId("");

      setSucessoTrocaQuarto(
        `Quarto alterado com sucesso para ${alocacaoAtualizada.quartoNome}.`,
      );
    } catch (error) {
      setSucessoTrocaQuarto("");

      setErroTrocaQuarto(
        error instanceof Error
          ? error.message
          : "Não foi possível trocar o quarto do hóspede.",
      );
    } finally {
      setTrocandoQuarto(false);
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

      setCheckIn(
        response,
      );

      setHospede(
        (hospedeAtual) =>
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

  async function handleRegistrarNaoComparecimento() {
    if (
      !motivoNaoComparecimento.trim()
    ) {
      setErroNaoComparecimento(
        "Informe o motivo do não comparecimento.",
      );

      return;
    }

    try {
      setRegistrandoNaoComparecimento(
        true,
      );

      setErroNaoComparecimento("");
      setSucessoNaoComparecimento("");

      const response =
        await registrarNaoComparecimento(
          Number(id),
          {
            motivo:
              motivoNaoComparecimento.trim(),
          },
        );

      setCheckIn(
        response,
      );

      setHospede(
        (hospedeAtual) =>
          hospedeAtual
            ? {
                ...hospedeAtual,

                statusCheckIn:
                  response.statusCheckIn,
              }
            : hospedeAtual,
      );

      setMotivoNaoComparecimento("");

      setSucessoNaoComparecimento(
        "Não comparecimento registrado com sucesso.",
      );
    } catch (error) {
      setSucessoNaoComparecimento("");

      setErroNaoComparecimento(
        error instanceof Error
          ? error.message
          : "Não foi possível registrar o não comparecimento.",
      );
    } finally {
      setRegistrandoNaoComparecimento(
        false,
      );
    }
  }

  async function handleAssociarOperacao(
    trasladoId: number,
  ) {
    const trasladoAtual =
      traslados.find(
        (traslado) =>
          traslado.id ===
          trasladoId,
      );

    const motoristaId =
      motoristaSelecionadoPorTraslado[
        trasladoId
      ] ??
      (trasladoAtual?.motoristaId
        ? String(
            trasladoAtual.motoristaId,
          )
        : "");

    const veiculoId =
      veiculoSelecionadoPorTraslado[
        trasladoId
      ] ??
      (trasladoAtual?.veiculoId
        ? String(
            trasladoAtual.veiculoId,
          )
        : "");

    if (
      !motoristaId ||
      !veiculoId
    ) {
      setErroOperacao(
        (atual) => ({
          ...atual,

          [trasladoId]:
            "Selecione motorista e veículo.",
        }),
      );

      return;
    }

    try {
      setSalvandoOperacaoId(
        trasladoId,
      );

      setErroOperacao(
        (atual) => ({
          ...atual,
          [trasladoId]: "",
        }),
      );

      setSucessoOperacao(
        (atual) => ({
          ...atual,
          [trasladoId]: "",
        }),
      );

      const atualizado =
        await associarOperacaoTraslado(
          trasladoId,
          {
            motoristaId:
              Number(motoristaId),

            veiculoId:
              Number(veiculoId),
          },
        );

      setTraslados(
        (atuais) =>
          atuais.map(
            (traslado) =>
              traslado.id ===
              atualizado.id
                ? atualizado
                : traslado,
          ),
      );

      setMotoristaSelecionadoPorTraslado(
        (atual) => ({
          ...atual,

          [trasladoId]:
            atualizado.motoristaId !==
            null
              ? String(
                  atualizado.motoristaId,
                )
              : "",
        }),
      );

      setVeiculoSelecionadoPorTraslado(
        (atual) => ({
          ...atual,

          [trasladoId]:
            atualizado.veiculoId !==
            null
              ? String(
                  atualizado.veiculoId,
                )
              : "",
        }),
      );

      setSucessoOperacao(
        (atual) => ({
          ...atual,

          [trasladoId]:
            "Traslado individual atualizado com sucesso.",
        }),
      );
    } catch (error) {
      setErroOperacao(
        (atual) => ({
          ...atual,

          [trasladoId]:
            error instanceof Error
              ? error.message
              : "Não foi possível atualizar o traslado individual.",
        }),
      );
    } finally {
      setSalvandoOperacaoId(null);
    }
  }

  async function handleVincularOperacaoCompartilhada(
    trasladoId: number,
  ) {
    const operacaoId =
      operacaoCompartilhadaSelecionadaPorTraslado[
        trasladoId
      ];

    if (!operacaoId) {
      setErroOperacaoCompartilhada(
        (atual) => ({
          ...atual,

          [trasladoId]:
            "Selecione uma operação compartilhada.",
        }),
      );

      return;
    }

    const trasladoAtual =
      traslados.find(
        (traslado) =>
          traslado.id ===
          trasladoId,
      );

    const operacaoAnteriorId =
      trasladoAtual?.operacaoTrasladoId ??
      null;

    const novaOperacaoId =
      Number(operacaoId);

    try {
      setVinculandoOperacaoCompartilhadaId(
        trasladoId,
      );

      setErroOperacaoCompartilhada(
        (atual) => ({
          ...atual,
          [trasladoId]: "",
        }),
      );

      setSucessoOperacaoCompartilhada(
        (atual) => ({
          ...atual,
          [trasladoId]: "",
        }),
      );

      await vincularTrasladoOperacao(
        novaOperacaoId,
        trasladoId,
      );

      setTraslados(
        (atuais) =>
          atuais.map(
            (traslado) =>
              traslado.id ===
              trasladoId
                ? {
                    ...traslado,

                    operacaoTrasladoId:
                      novaOperacaoId,
                  }
                : traslado,
          ),
      );

      if (
        operacaoAnteriorId !==
        novaOperacaoId
      ) {
        setOperacoesCompartilhadas(
          (atuais) =>
            atuais.map(
              (operacao) => {
                if (
                  operacao.id ===
                  operacaoAnteriorId
                ) {
                  return {
                    ...operacao,

                    quantidadePassageiros:
                      Math.max(
                        operacao.quantidadePassageiros -
                          1,
                        0,
                      ),

                    vagasDisponiveis:
                      operacao.vagasDisponiveis !==
                      null
                        ? operacao.vagasDisponiveis +
                          1
                        : null,
                  };
                }

                if (
                  operacao.id ===
                  novaOperacaoId
                ) {
                  return {
                    ...operacao,

                    quantidadePassageiros:
                      operacao.quantidadePassageiros +
                      1,

                    vagasDisponiveis:
                      operacao.vagasDisponiveis !==
                      null
                        ? Math.max(
                            operacao.vagasDisponiveis -
                              1,
                            0,
                          )
                        : null,
                  };
                }

                return operacao;
              },
            ),
        );
      }

      setEditandoOperacaoCompartilhadaId(
        null,
      );

      setSucessoOperacaoCompartilhada(
        (atual) => ({
          ...atual,

          [trasladoId]:
            operacaoAnteriorId ===
            null
              ? "Traslado vinculado à operação compartilhada."
              : operacaoAnteriorId ===
                  novaOperacaoId
                ? "O traslado já está vinculado a esta operação."
                : "Operação compartilhada alterada com sucesso.",
        }),
      );
    } catch (error) {
      setErroOperacaoCompartilhada(
        (atual) => ({
          ...atual,

          [trasladoId]:
            error instanceof Error
              ? error.message
              : "Não foi possível vincular o traslado à operação compartilhada.",
        }),
      );
    } finally {
      setVinculandoOperacaoCompartilhadaId(
        null,
      );
    }
  }

  function handleIniciarTrocaOperacao(
    traslado: Traslado,
  ) {
    setOperacaoCompartilhadaSelecionadaPorTraslado(
      (atual) => ({
        ...atual,

        [traslado.id]:
          traslado.operacaoTrasladoId !==
          null
            ? String(
                traslado.operacaoTrasladoId,
              )
            : "",
      }),
    );

    setErroOperacaoCompartilhada(
      (atual) => ({
        ...atual,
        [traslado.id]: "",
      }),
    );

    setSucessoOperacaoCompartilhada(
      (atual) => ({
        ...atual,
        [traslado.id]: "",
      }),
    );

    setEditandoOperacaoCompartilhadaId(
      traslado.id,
    );
  }

  function handleCancelarTrocaOperacao(
    traslado: Traslado,
  ) {
    setOperacaoCompartilhadaSelecionadaPorTraslado(
      (atual) => ({
        ...atual,

        [traslado.id]:
          traslado.operacaoTrasladoId !==
          null
            ? String(
                traslado.operacaoTrasladoId,
              )
            : "",
      }),
    );

    setErroOperacaoCompartilhada(
      (atual) => ({
        ...atual,
        [traslado.id]: "",
      }),
    );

    setEditandoOperacaoCompartilhadaId(
      null,
    );
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando hóspede...
      </p>
    );
  }

  if (
    erro ||
    !hospede
  ) {
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
          ← Voltar para hóspedes
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

        <div className="mt-4 flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight text-foreground">
              {hospede.nomeCompleto}
            </h1>

            <p className="mt-2 text-sm text-muted">
              {hospede.viagemNome}
            </p>
          </div>

          {isAdmin && (
            <Link
              href={`/hospedes/${id}/editar`}
              className="inline-flex h-11 items-center justify-center rounded-xl border border-primary px-5 text-sm font-semibold text-primary transition-opacity hover:opacity-90"
            >
              Editar hóspede
            </Link>
          )}
        </div>
      </header>

      <section className="grid gap-4 md:grid-cols-2">
        <Card>
          <p className="text-sm text-muted">
            E-mail
          </p>

          <p className="mt-2 font-medium text-foreground">
            {hospede.email ??
              "Não informado"}
          </p>
        </Card>

        <Card>
          <p className="text-sm text-muted">
            Telefone
          </p>

          <p className="mt-2 font-medium text-foreground">
            {hospede.telefone ??
              "Não informado"}
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
            {hospede.dataNascimento
              ? new Date(
                  `${hospede.dataNascimento}T00:00:00`,
                ).toLocaleDateString(
                  "pt-BR",
                )
              : "Não informada"}
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
            {hospede.horarioPrevistoChegada
              ? new Date(
                  hospede.horarioPrevistoChegada,
                ).toLocaleString(
                  "pt-BR",
                )
              : "Não informada"}
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
                  ).toLocaleString(
                    "pt-BR",
                  )
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
          checkIn.quartoId !==
            null &&
          alocacao && (
            <div className="space-y-4">
              <div>
                <label
                  htmlFor="novo-quarto"
                  className="mb-2 block text-sm font-medium text-foreground"
                >
                  Trocar quarto
                </label>

                <select
                  id="novo-quarto"
                  value={
                    novoQuartoId
                  }
                  onChange={(
                    event,
                  ) =>
                    setNovoQuartoId(
                      event.target
                        .value,
                    )
                  }
                  className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
                >
                  <option value="">
                    Selecione o novo quarto
                  </option>

                  {quartos
                    .filter(
                      (quarto) =>
                        quarto.id !==
                        checkIn.quartoId,
                    )
                    .map(
                      (quarto) => (
                        <option
                          key={
                            quarto.id
                          }
                          value={
                            quarto.id
                          }
                        >
                          {quarto.nome}{" "}
                          —{" "}
                          {quarto.tipo}
                        </option>
                      ),
                    )}
                </select>
              </div>

              {erroTrocaQuarto && (
                <p
                  role="alert"
                  className="text-sm text-red-400"
                >
                  {
                    erroTrocaQuarto
                  }
                </p>
              )}

              {sucessoTrocaQuarto && (
                <p className="text-sm text-green-400">
                  {
                    sucessoTrocaQuarto
                  }
                </p>
              )}

              <button
                type="button"
                onClick={
                  handleTrocarQuarto
                }
                disabled={
                  trocandoQuarto ||
                  !novoQuartoId
                }
                className="rounded-xl border border-primary px-5 py-3 text-sm font-semibold text-primary transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
              >
                {trocandoQuarto
                  ? "Trocando quarto..."
                  : "Trocar quarto"}
              </button>
            </div>
          )}

        {checkIn &&
          checkIn.quartoId ===
            null &&
          checkIn.statusCheckIn !==
            "NAO_COMPARECEU" && (
            <div className="space-y-4">
              {checkIn.statusCheckIn !==
                "REALIZADO" && (
                <div className="rounded-xl border border-border bg-surface p-4">
                  <p className="text-sm font-medium text-foreground">
                    Check-in indisponível
                  </p>

                  <p className="mt-1 text-sm text-muted">
                    Este hóspede precisa ser alocado em um quarto antes de realizar o check-in.
                  </p>
                </div>
              )}

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
                  onChange={(
                    event,
                  ) =>
                    setQuartoSelecionadoId(
                      event.target
                        .value,
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
                        key={
                          quarto.id
                        }
                        value={
                          quarto.id
                        }
                      >
                        {quarto.nome}{" "}
                        —{" "}
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
                  {
                    erroAlocacao
                  }
                </p>
              )}

              {sucessoAlocacao && (
                <p className="text-sm text-green-400">
                  {
                    sucessoAlocacao
                  }
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
          checkIn.statusCheckIn !==
            "NAO_COMPARECEU" &&
          checkIn.quartoId !==
            null && (
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
                  onChange={(
                    event,
                  ) =>
                    setObservacaoCheckIn(
                      event.target
                        .value,
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
                  {
                    erroCheckIn
                  }
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

        {sucessoNaoComparecimento && (
          <p className="text-sm text-green-400">
            {
              sucessoNaoComparecimento
            }
          </p>
        )}

        {checkIn &&
          checkIn.statusCheckIn ===
            "PENDENTE" && (
            <div className="space-y-4">
              <div>
                <label
                  htmlFor="motivo-nao-comparecimento"
                  className="mb-2 block text-sm font-medium text-foreground"
                >
                  Motivo do não comparecimento
                </label>

                <textarea
                  id="motivo-nao-comparecimento"
                  value={
                    motivoNaoComparecimento
                  }
                  onChange={(
                    event,
                  ) =>
                    setMotivoNaoComparecimento(
                      event.target
                        .value,
                    )
                  }
                  placeholder="Informe o motivo do não comparecimento."
                  rows={4}
                  className="w-full rounded-xl border border-border bg-surface px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
                />
              </div>

              {erroNaoComparecimento && (
                <p
                  role="alert"
                  className="text-sm text-red-400"
                >
                  {
                    erroNaoComparecimento
                  }
                </p>
              )}

              <button
                type="button"
                onClick={
                  handleRegistrarNaoComparecimento
                }
                disabled={
                  registrandoNaoComparecimento
                }
                className="rounded-xl border border-red-400 px-5 py-3 text-sm font-semibold text-red-400 transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
              >
                {registrandoNaoComparecimento
                  ? "Registrando..."
                  : "Marcar como não compareceu"}
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
              (traslado) => {
                const operacaoAtual =
                  operacoesCompartilhadas.find(
                    (operacao) =>
                      operacao.id ===
                      traslado.operacaoTrasladoId,
                  );

                const operacaoSelecionadaId =
                  operacaoCompartilhadaSelecionadaPorTraslado[
                    traslado.id
                  ] ?? "";

                const operacaoSelecionada =
                  operacoesCompartilhadas.find(
                    (operacao) =>
                      operacao.id ===
                      Number(
                        operacaoSelecionadaId,
                      ),
                  );

                const possuiOperacaoCompartilhada =
                  traslado.operacaoTrasladoId !==
                  null;

                const editandoOperacaoCompartilhada =
                  editandoOperacaoCompartilhadaId ===
                  traslado.id;

                const operacoesCompativeis =
                  operacoesCompartilhadas.filter(
                    (operacao) =>
                      operacao.tipo ===
                        traslado.tipo &&
                      (
                        traslado.tipo ===
                          "OUTRO" ||
                        operacao.aeroporto ===
                          traslado.aeroporto
                      ) &&
                      (operacao.vagasDisponiveis ??
                        0) >
                        0,
                  );

                const outrasOperacoesCompativeis =
                  operacoesCompartilhadas.filter(
                    (operacao) =>
                      operacao.id !==
                        traslado.operacaoTrasladoId &&
                      operacao.tipo ===
                        traslado.tipo &&
                      (
                        traslado.tipo ===
                          "OUTRO" ||
                        operacao.aeroporto ===
                          traslado.aeroporto
                      ) &&
                      (operacao.vagasDisponiveis ??
                        0) >
                        0,
                  );

                return (
                  <Card
                    key={traslado.id}
                  >
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

                      {!possuiOperacaoCompartilhada && (
                        <div className="rounded-xl border border-border bg-background/40 p-4">
                          <div>
                            <p className="text-sm font-semibold text-foreground">
                              Traslado individual
                            </p>

                            <p className="mt-1 text-sm text-muted">
                              Motorista e veículo exclusivos deste deslocamento.
                            </p>
                          </div>

                          <div className="mt-4 grid gap-4 md:grid-cols-2">
                            <div>
                              <p className="text-sm text-muted">
                                Motorista
                              </p>

                              <p className="mt-1 font-medium text-foreground">
                                {traslado.motoristaNome ??
                                  "Ainda não definido"}
                              </p>
                            </div>

                            <div>
                              <p className="text-sm text-muted">
                                Veículo
                              </p>

                              <p className="mt-1 font-medium text-foreground">
                                {traslado.veiculoModelo
                                  ? `${traslado.veiculoModelo}${
                                      traslado.veiculoPlaca
                                        ? ` | ${traslado.veiculoPlaca}`
                                        : ""
                                    }`
                                  : "Ainda não definido"}
                              </p>

                              {traslado.veiculoCapacidadePassageiros !==
                                null && (
                                <p className="mt-1 text-xs text-muted">
                                  Capacidade:{" "}
                                  {
                                    traslado.veiculoCapacidadePassageiros
                                  }{" "}
                                  passageiros
                                </p>
                              )}
                            </div>
                          </div>

                          {isAdmin && (
                            <div className="mt-5 space-y-4 border-t border-border pt-4">
                              <div className="grid gap-4 md:grid-cols-2">
                                <div>
                                  <label
                                    htmlFor={`motorista-${traslado.id}`}
                                    className="mb-2 block text-sm font-medium text-foreground"
                                  >
                                    Motorista
                                  </label>

                                  <select
                                    id={`motorista-${traslado.id}`}
                                    value={
                                      motoristaSelecionadoPorTraslado[
                                        traslado.id
                                      ] ??
                                      (traslado.motoristaId !==
                                      null
                                        ? String(
                                            traslado.motoristaId,
                                          )
                                        : "")
                                    }
                                    onChange={(
                                      event,
                                    ) =>
                                      setMotoristaSelecionadoPorTraslado(
                                        (
                                          atual,
                                        ) => ({
                                          ...atual,

                                          [traslado.id]:
                                            event
                                              .target
                                              .value,
                                        }),
                                      )
                                    }
                                    className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
                                  >
                                    <option value="">
                                      Selecione o motorista
                                    </option>

                                    {motoristas.map(
                                      (
                                        motorista,
                                      ) => (
                                        <option
                                          key={
                                            motorista.id
                                          }
                                          value={
                                            motorista.id
                                          }
                                        >
                                          {
                                            motorista.nomeCompleto
                                          }
                                        </option>
                                      ),
                                    )}
                                  </select>
                                </div>

                                <div>
                                  <label
                                    htmlFor={`veiculo-${traslado.id}`}
                                    className="mb-2 block text-sm font-medium text-foreground"
                                  >
                                    Veículo
                                  </label>

                                  <select
                                    id={`veiculo-${traslado.id}`}
                                    value={
                                      veiculoSelecionadoPorTraslado[
                                        traslado.id
                                      ] ??
                                      (traslado.veiculoId !==
                                      null
                                        ? String(
                                            traslado.veiculoId,
                                          )
                                        : "")
                                    }
                                    onChange={(
                                      event,
                                    ) =>
                                      setVeiculoSelecionadoPorTraslado(
                                        (
                                          atual,
                                        ) => ({
                                          ...atual,

                                          [traslado.id]:
                                            event
                                              .target
                                              .value,
                                        }),
                                      )
                                    }
                                    className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
                                  >
                                    <option value="">
                                      Selecione o veículo
                                    </option>

                                    {veiculos.map(
                                      (
                                        veiculo,
                                      ) => (
                                        <option
                                          key={
                                            veiculo.id
                                          }
                                          value={
                                            veiculo.id
                                          }
                                        >
                                          {
                                            veiculo.modelo
                                          }{" "}
                                          |{" "}
                                          {
                                            veiculo.placa
                                          }{" "}
                                          |{" "}
                                          {
                                            veiculo.capacidadePassageiros
                                          }{" "}
                                          passageiros
                                        </option>
                                      ),
                                    )}
                                  </select>
                                </div>
                              </div>

                              {erroOperacao[
                                traslado.id
                              ] && (
                                <p
                                  role="alert"
                                  className="text-sm text-red-400"
                                >
                                  {
                                    erroOperacao[
                                      traslado.id
                                    ]
                                  }
                                </p>
                              )}

                              {sucessoOperacao[
                                traslado.id
                              ] && (
                                <p className="text-sm text-green-400">
                                  {
                                    sucessoOperacao[
                                      traslado.id
                                    ]
                                  }
                                </p>
                              )}

                              <button
                                type="button"
                                onClick={() =>
                                  handleAssociarOperacao(
                                    traslado.id,
                                  )
                                }
                                disabled={
                                  salvandoOperacaoId ===
                                  traslado.id
                                }
                                className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                              >
                                {salvandoOperacaoId ===
                                traslado.id
                                  ? "Salvando..."
                                  : traslado.motoristaId !==
                                        null &&
                                      traslado.veiculoId !==
                                        null
                                    ? "Atualizar traslado individual"
                                    : "Salvar traslado individual"}
                              </button>
                            </div>
                          )}
                        </div>
                      )}

                      <div className="rounded-xl border border-primary/30 bg-primary/5 p-4">
                        <div>
                          <p className="text-sm font-semibold text-foreground">
                            Operação compartilhada
                          </p>

                          <p className="mt-1 text-sm text-muted">
                            Execução compartilhada deste deslocamento.
                          </p>
                        </div>

                        {operacaoAtual ? (
                          <div className="mt-4 rounded-xl border border-border bg-background/40 p-4">
                            <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                              <div>
                                <p className="text-xs font-medium uppercase tracking-wide text-muted">
                                  Operação #
                                  {
                                    operacaoAtual.id
                                  }
                                </p>

                                <p className="mt-1 font-semibold text-foreground">
                                  {new Date(
                                    operacaoAtual.dataHoraPrevista,
                                  ).toLocaleString(
                                    "pt-BR",
                                  )}
                                </p>
                              </div>
                            </div>

                            <div className="mt-4 grid gap-4 md:grid-cols-2">
                              <div>
                                <p className="text-sm text-muted">
                                  Origem
                                </p>

                                <p className="mt-1 font-medium text-foreground">
                                  {
                                    operacaoAtual.localOrigem
                                  }
                                </p>
                              </div>

                              <div>
                                <p className="text-sm text-muted">
                                  Destino
                                </p>

                                <p className="mt-1 font-medium text-foreground">
                                  {
                                    operacaoAtual.localDestino
                                  }
                                </p>
                              </div>

                              <div>
                                <p className="text-sm text-muted">
                                  Motorista
                                </p>

                                <p className="mt-1 font-medium text-foreground">
                                  {operacaoAtual.motoristaNome ??
                                    "Não definido"}
                                </p>
                              </div>

                              <div>
                                <p className="text-sm text-muted">
                                  Veículo
                                </p>

                                <p className="mt-1 font-medium text-foreground">
                                  {operacaoAtual.veiculoModelo
                                    ? `${operacaoAtual.veiculoModelo}${
                                        operacaoAtual.veiculoPlaca
                                          ? ` | ${operacaoAtual.veiculoPlaca}`
                                          : ""
                                      }`
                                    : "Não definido"}
                                </p>
                              </div>

                              <div>
                                <p className="text-sm text-muted">
                                  Ocupação
                                </p>

                                <p className="mt-1 font-medium text-foreground">
                                  {
                                    operacaoAtual.quantidadePassageiros
                                  }{" "}
                                  /{" "}
                                  {
                                    operacaoAtual.capacidadePassageiros
                                  }
                                </p>
                              </div>
                            </div>
                          </div>
                        ) : (
                          <p className="mt-4 text-sm text-muted">
                            Este traslado ainda não está vinculado a uma operação compartilhada.
                          </p>
                        )}

                        {isAdmin &&
                          !possuiOperacaoCompartilhada && (
                            <div className="mt-5 space-y-4 border-t border-border pt-4">
                              {operacoesCompativeis.length ===
                              0 ? (
                                <div className="space-y-4">
                                  <div className="rounded-xl border border-border bg-background/40 p-4">
                                    <p className="text-sm font-semibold text-foreground">
                                      Nenhuma operação compatível disponível
                                    </p>

                                    <p className="mt-2 text-sm text-muted">
                                      Para este traslado é necessária uma operação com os seguintes dados:
                                    </p>

                                    <div className="mt-4 grid gap-3 sm:grid-cols-2">
                                      <div>
                                        <p className="text-xs text-muted">
                                          Tipo
                                        </p>

                                        <p className="mt-1 text-sm font-medium text-foreground">
                                          {traslado.tipo ===
                                          "AEROPORTO_PARA_HOSPEDAGEM"
                                            ? "Aeroporto → Hospedagem"
                                            : traslado.tipo ===
                                                "HOSPEDAGEM_PARA_AEROPORTO"
                                              ? "Hospedagem → Aeroporto"
                                              : "Outro traslado"}
                                        </p>
                                      </div>

                                      {traslado.tipo !==
                                        "OUTRO" && (
                                        <div>
                                          <p className="text-xs text-muted">
                                            Aeroporto
                                          </p>

                                          <p className="mt-1 text-sm font-medium text-foreground">
                                            {traslado.aeroporto ??
                                              "Não informado"}
                                          </p>
                                        </div>
                                      )}
                                    </div>
                                  </div>

                                  <Link
                                    href="/traslados/nova"
                                    className="inline-flex h-11 items-center justify-center rounded-xl border border-primary px-5 text-sm font-semibold text-primary transition-opacity hover:opacity-90"
                                  >
                                    Criar operação compatível
                                  </Link>
                                </div>
                              ) : (
                                <>
                                  <div>
                                    <label
                                      htmlFor={`operacao-compartilhada-${traslado.id}`}
                                      className="mb-2 block text-sm font-medium text-foreground"
                                    >
                                      Operação compartilhada
                                    </label>

                                    <select
                                      id={`operacao-compartilhada-${traslado.id}`}
                                      value={
                                        operacaoSelecionadaId
                                      }
                                      onChange={(event) =>
                                        setOperacaoCompartilhadaSelecionadaPorTraslado(
                                          (atual) => ({
                                            ...atual,

                                            [traslado.id]:
                                              event.target.value,
                                          }),
                                        )
                                      }
                                      className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
                                    >
                                      <option value="">
                                        Selecione uma operação
                                      </option>

                                      {operacoesCompativeis.map(
                                        (operacao) => (
                                          <option
                                            key={operacao.id}
                                            value={operacao.id}
                                          >
                                            {new Date(
                                              operacao.dataHoraPrevista,
                                            ).toLocaleString(
                                              "pt-BR",
                                            )}{" "}
                                            |{" "}
                                            {
                                              operacao.localOrigem
                                            }{" "}
                                            →{" "}
                                            {
                                              operacao.localDestino
                                            }{" "}
                                            |{" "}
                                            {
                                              operacao.quantidadePassageiros
                                            }
                                            /
                                            {
                                              operacao.capacidadePassageiros
                                            }
                                          </option>
                                        ),
                                      )}
                                    </select>
                                  </div>

                                  {operacaoSelecionada && (
                                    <div className="grid gap-3 rounded-xl border border-border bg-background/40 p-4 md:grid-cols-2">
                                      <div>
                                        <p className="text-xs text-muted">
                                          Motorista
                                        </p>

                                        <p className="mt-1 text-sm font-medium text-foreground">
                                          {operacaoSelecionada.motoristaNome ??
                                            "Não definido"}
                                        </p>
                                      </div>

                                      <div>
                                        <p className="text-xs text-muted">
                                          Veículo
                                        </p>

                                        <p className="mt-1 text-sm font-medium text-foreground">
                                          {operacaoSelecionada.veiculoModelo ??
                                            "Não definido"}
                                        </p>
                                      </div>

                                      <div>
                                        <p className="text-xs text-muted">
                                          Horário
                                        </p>

                                        <p className="mt-1 text-sm font-medium text-foreground">
                                          {new Date(
                                            operacaoSelecionada.dataHoraPrevista,
                                          ).toLocaleString(
                                            "pt-BR",
                                          )}
                                        </p>
                                      </div>

                                      <div>
                                        <p className="text-xs text-muted">
                                          Ocupação
                                        </p>

                                        <p className="mt-1 text-sm font-medium text-foreground">
                                          {
                                            operacaoSelecionada.quantidadePassageiros
                                          }{" "}
                                          /{" "}
                                          {
                                            operacaoSelecionada.capacidadePassageiros
                                          }
                                        </p>
                                      </div>
                                    </div>
                                  )}

                                  {erroOperacaoCompartilhada[
                                    traslado.id
                                  ] && (
                                    <p
                                      role="alert"
                                      className="text-sm text-red-400"
                                    >
                                      {
                                        erroOperacaoCompartilhada[
                                          traslado.id
                                        ]
                                      }
                                    </p>
                                  )}

                                  {sucessoOperacaoCompartilhada[
                                    traslado.id
                                  ] && (
                                    <p className="text-sm text-green-400">
                                      {
                                        sucessoOperacaoCompartilhada[
                                          traslado.id
                                        ]
                                      }
                                    </p>
                                  )}

                                  <button
                                    type="button"
                                    onClick={() =>
                                      handleVincularOperacaoCompartilhada(
                                        traslado.id,
                                      )
                                    }
                                    disabled={
                                      vinculandoOperacaoCompartilhadaId ===
                                        traslado.id ||
                                      !operacaoSelecionadaId
                                    }
                                    className="rounded-xl border border-primary px-5 py-3 text-sm font-semibold text-primary transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                                  >
                                    {vinculandoOperacaoCompartilhadaId ===
                                    traslado.id
                                      ? "Vinculando..."
                                      : "Vincular à operação compartilhada"}
                                  </button>
                                </>
                              )}
                            </div>
                          )}

                        {isAdmin &&
                          possuiOperacaoCompartilhada &&
                          !editandoOperacaoCompartilhada && (
                            <div className="mt-5 border-t border-border pt-4">
                              <button
                                type="button"
                                onClick={() =>
                                  handleIniciarTrocaOperacao(
                                    traslado,
                                  )
                                }
                                className="rounded-xl border border-primary px-5 py-3 text-sm font-semibold text-primary transition-opacity hover:opacity-90"
                              >
                                Trocar operação compartilhada
                              </button>
                            </div>
                          )}

                        {isAdmin &&
                          possuiOperacaoCompartilhada &&
                          editandoOperacaoCompartilhada && (
                            <div className="mt-5 space-y-4 border-t border-border pt-4">
                              {outrasOperacoesCompativeis.length === 0 ? (
                                <>
                                  <div className="rounded-xl border border-border bg-background/40 p-4">
                                    <p className="text-sm font-semibold text-foreground">
                                      Nenhuma outra operação compatível disponível
                                    </p>

                                    <p className="mt-2 text-sm text-muted">
                                      Para trocar este traslado é necessária outra operação com os seguintes dados:
                                    </p>

                                    <div className="mt-4 grid gap-3 sm:grid-cols-2">
                                      <div>
                                        <p className="text-xs text-muted">
                                          Tipo
                                        </p>

                                        <p className="mt-1 text-sm font-medium text-foreground">
                                          {traslado.tipo ===
                                          "AEROPORTO_PARA_HOSPEDAGEM"
                                            ? "Aeroporto → Hospedagem"
                                            : traslado.tipo ===
                                                "HOSPEDAGEM_PARA_AEROPORTO"
                                              ? "Hospedagem → Aeroporto"
                                              : "Outro traslado"}
                                        </p>
                                      </div>

                                      {traslado.tipo !== "OUTRO" && (
                                        <div>
                                          <p className="text-xs text-muted">
                                            Aeroporto
                                          </p>

                                          <p className="mt-1 text-sm font-medium text-foreground">
                                            {traslado.aeroporto ??
                                              "Não informado"}
                                          </p>
                                        </div>
                                      )}
                                    </div>
                                  </div>

                                  <div className="flex flex-col gap-3 sm:flex-row">
                                    <Link
                                      href="/traslados/nova"
                                      className="inline-flex h-11 items-center justify-center rounded-xl border border-primary px-5 text-sm font-semibold text-primary transition-opacity hover:opacity-90"
                                    >
                                      Criar operação compatível
                                    </Link>

                                    <button
                                      type="button"
                                      onClick={() =>
                                        handleCancelarTrocaOperacao(
                                          traslado,
                                        )
                                      }
                                      className="rounded-xl border border-border px-5 py-3 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
                                    >
                                      Cancelar
                                    </button>
                                  </div>
                                </>
                              ) : (
                                <>
                                  <div>
                                    <label
                                      htmlFor={`operacao-compartilhada-${traslado.id}`}
                                      className="mb-2 block text-sm font-medium text-foreground"
                                    >
                                      Nova operação
                                    </label>

                                    <select
                                      id={`operacao-compartilhada-${traslado.id}`}
                                      value={
                                        operacaoSelecionadaId
                                      }
                                      onChange={(event) =>
                                        setOperacaoCompartilhadaSelecionadaPorTraslado(
                                          (atual) => ({
                                            ...atual,
                                            [traslado.id]:
                                              event.target.value,
                                          }),
                                        )
                                      }
                                      className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
                                    >
                                      <option value="">
                                        Selecione uma operação
                                      </option>

                                      {outrasOperacoesCompativeis.map(
                                        (operacao) => (
                                          <option
                                            key={operacao.id}
                                            value={operacao.id}
                                          >
                                            {new Date(
                                              operacao.dataHoraPrevista,
                                            ).toLocaleString(
                                              "pt-BR",
                                            )}{" "}
                                            |{" "}
                                            {operacao.localOrigem}{" "}
                                            →{" "}
                                            {operacao.localDestino}{" "}
                                            |{" "}
                                            {operacao.quantidadePassageiros}
                                            /
                                            {operacao.capacidadePassageiros}
                                          </option>
                                        ),
                                      )}
                                    </select>
                                  </div>

                                  {operacaoSelecionada &&
                                    operacaoSelecionada.id !==
                                      traslado.operacaoTrasladoId && (
                                      <div className="grid gap-3 rounded-xl border border-border bg-background/40 p-4 md:grid-cols-2">
                                        <div>
                                          <p className="text-xs text-muted">
                                            Motorista
                                          </p>

                                          <p className="mt-1 text-sm font-medium text-foreground">
                                            {operacaoSelecionada.motoristaNome ??
                                              "Não definido"}
                                          </p>
                                        </div>

                                        <div>
                                          <p className="text-xs text-muted">
                                            Veículo
                                          </p>

                                          <p className="mt-1 text-sm font-medium text-foreground">
                                            {operacaoSelecionada.veiculoModelo ??
                                              "Não definido"}
                                          </p>
                                        </div>

                                        <div>
                                          <p className="text-xs text-muted">
                                            Horário
                                          </p>

                                          <p className="mt-1 text-sm font-medium text-foreground">
                                            {new Date(
                                              operacaoSelecionada.dataHoraPrevista,
                                            ).toLocaleString(
                                              "pt-BR",
                                            )}
                                          </p>
                                        </div>

                                        <div>
                                          <p className="text-xs text-muted">
                                            Ocupação
                                          </p>

                                          <p className="mt-1 text-sm font-medium text-foreground">
                                            {
                                              operacaoSelecionada.quantidadePassageiros
                                            }{" "}
                                            /{" "}
                                            {
                                              operacaoSelecionada.capacidadePassageiros
                                            }
                                          </p>
                                        </div>
                                      </div>
                                    )}

                                  {erroOperacaoCompartilhada[
                                    traslado.id
                                  ] && (
                                    <p
                                      role="alert"
                                      className="text-sm text-red-400"
                                    >
                                      {
                                        erroOperacaoCompartilhada[
                                          traslado.id
                                        ]
                                      }
                                    </p>
                                  )}

                                  <div className="flex flex-col gap-3 sm:flex-row">
                                    <button
                                      type="button"
                                      onClick={() =>
                                        handleVincularOperacaoCompartilhada(
                                          traslado.id,
                                        )
                                      }
                                      disabled={
                                        vinculandoOperacaoCompartilhadaId ===
                                          traslado.id ||
                                        !operacaoSelecionadaId
                                      }
                                      className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                                    >
                                      {vinculandoOperacaoCompartilhadaId ===
                                      traslado.id
                                        ? "Alterando..."
                                        : "Confirmar troca"}
                                    </button>

                                    <button
                                      type="button"
                                      onClick={() =>
                                        handleCancelarTrocaOperacao(
                                          traslado,
                                        )
                                      }
                                      disabled={
                                        vinculandoOperacaoCompartilhadaId ===
                                        traslado.id
                                      }
                                      className="rounded-xl border border-border px-5 py-3 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
                                    >
                                      Cancelar
                                    </button>
                                  </div>
                                </>
                              )}
                            </div>
                          )}


                        {sucessoOperacaoCompartilhada[
                          traslado.id
                        ] &&
                          !editandoOperacaoCompartilhada && (
                            <p className="mt-4 text-sm text-green-400">
                              {
                                sucessoOperacaoCompartilhada[
                                  traslado.id
                                ]
                              }
                            </p>
                          )}
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
                );
              },
            )}
          </div>
        )}
      </section>
    </div>
  );
}