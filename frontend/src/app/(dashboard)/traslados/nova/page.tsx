"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  listarMotoristas,
} from "@/features/motorista/services/motoristaService";

import type {
  Motorista,
} from "@/features/motorista/types/motorista";

import {
  criarOperacaoTraslado,
} from "@/features/traslado/operacao/services/operacaoTrasladoService";

import type {
  Aeroporto,
  TipoTraslado,
} from "@/features/traslado/operacao/types/operacaoTraslado";

import {
  listarVeiculos,
} from "@/features/veiculo/services/veiculoService";

import type {
  Veiculo,
} from "@/features/veiculo/types/veiculo";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

const LOCAL_AEROPORTO: Record<
  Aeroporto,
  string
> = {
  GRU: "Aeroporto de Guarulhos",
  CGH: "Aeroporto de Congonhas",
  VCP: "Aeroporto de Viracopos",
};

export default function NovaOperacaoTrasladoPage() {
  const router = useRouter();

  const [viagens, setViagens] =
    useState<Viagem[]>([]);

  const [motoristas, setMotoristas] =
    useState<Motorista[]>([]);

  const [veiculos, setVeiculos] =
    useState<Veiculo[]>([]);

  const [viagemId, setViagemId] =
    useState("");

  const [tipo, setTipo] =
    useState<TipoTraslado | "">("");

  const [aeroporto, setAeroporto] =
    useState<Aeroporto | "">("");

  const [
    dataHoraPrevista,
    setDataHoraPrevista,
  ] = useState("");

  const [
    localOrigem,
    setLocalOrigem,
  ] = useState("");

  const [
    localDestino,
    setLocalDestino,
  ] = useState("");

  const [motoristaId, setMotoristaId] =
    useState("");

  const [veiculoId, setVeiculoId] =
    useState("");

  const [
    observacao,
    setObservacao,
  ] = useState("");

  const [carregando, setCarregando] =
    useState(true);

  const [salvando, setSalvando] =
    useState(false);

  const [erro, setErro] =
    useState("");

  useEffect(() => {
    async function carregarDados() {
      try {
        setErro("");

        const [
          dadosViagens,
          dadosMotoristas,
          dadosVeiculos,
        ] = await Promise.all([
          listarViagens(),
          listarMotoristas(),
          listarVeiculos(),
        ]);

        setViagens(dadosViagens);

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

        const viagemPreferencial =
          dadosViagens.find(
            (viagem) =>
              viagem.status ===
              "EM_ANDAMENTO",
          ) ??
          dadosViagens.find(
            (viagem) =>
              viagem.status ===
              "PLANEJADA",
          ) ??
          dadosViagens[0];

        if (viagemPreferencial) {
          setViagemId(
            String(
              viagemPreferencial.id,
            ),
          );
        }
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os dados para criação da operação.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarDados();
  }, []);

  function handleTipoChange(
    novoTipo: TipoTraslado | "",
  ) {
    if (
      tipo ===
      "AEROPORTO_PARA_HOSPEDAGEM"
    ) {
      setLocalOrigem("");
    }

    if (
      tipo ===
      "HOSPEDAGEM_PARA_AEROPORTO"
    ) {
      setLocalDestino("");
    }

    setTipo(novoTipo);
    setAeroporto("");

    if (
      novoTipo ===
      "AEROPORTO_PARA_HOSPEDAGEM"
    ) {
      setLocalOrigem("");
    }

    if (
      novoTipo ===
      "HOSPEDAGEM_PARA_AEROPORTO"
    ) {
      setLocalDestino("");
    }
  }

  function handleAeroportoChange(
    novoAeroporto: Aeroporto | "",
  ) {
    setAeroporto(
      novoAeroporto,
    );

    const localAeroporto =
      novoAeroporto === ""
        ? ""
        : LOCAL_AEROPORTO[
            novoAeroporto
          ];

    if (
      tipo ===
      "AEROPORTO_PARA_HOSPEDAGEM"
    ) {
      setLocalOrigem(
        localAeroporto,
      );
    }

    if (
      tipo ===
      "HOSPEDAGEM_PARA_AEROPORTO"
    ) {
      setLocalDestino(
        localAeroporto,
      );
    }
  }

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    const localAeroporto =
      aeroporto === ""
        ? ""
        : LOCAL_AEROPORTO[
            aeroporto
          ];

    const origemFinal =
      tipo ===
      "AEROPORTO_PARA_HOSPEDAGEM"
        ? localAeroporto
        : localOrigem.trim();

    const destinoFinal =
      tipo ===
      "HOSPEDAGEM_PARA_AEROPORTO"
        ? localAeroporto
        : localDestino.trim();

    if (
      !viagemId ||
      !tipo ||
      (
        tipo !== "OUTRO" &&
        !aeroporto
      ) ||
      !dataHoraPrevista ||
      !origemFinal ||
      !destinoFinal ||
      !motoristaId ||
      !veiculoId
    ) {
      setErro(
        "Preencha todos os campos obrigatórios.",
      );

      return;
    }

    try {
      setSalvando(true);
      setErro("");

      await criarOperacaoTraslado({
        viagemId:
          Number(viagemId),

        tipo,

        aeroporto:
          tipo === "OUTRO" ||
          aeroporto === ""
            ? null
            : aeroporto,

        dataHoraPrevista,

        localOrigem:
          origemFinal,

        localDestino:
          destinoFinal,

        motoristaId:
          Number(motoristaId),

        veiculoId:
          Number(veiculoId),

        observacao:
          observacao.trim() === ""
            ? null
            : observacao.trim(),
      });

      router.push(
        "/traslados",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível criar a operação de traslado.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando dados da operação...
      </p>
    );
  }

  return (
    <div className="space-y-8">
      <PageHeader
        title="Nova operação de traslado"
        description="Defina o trajeto, horário, motorista e veículo da operação."
      />

      <form
        onSubmit={
          handleSubmit
        }
        className="max-w-2xl space-y-6 rounded-2xl border border-border bg-surface p-6"
      >
        <div>
          <label
            htmlFor="viagem"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Viagem
          </label>

          <select
            id="viagem"
            value={
              viagemId
            }
            onChange={(
              event,
            ) =>
              setViagemId(
                event.target
                  .value,
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          >
            <option value="">
              Selecione a viagem
            </option>

            {viagens.map(
              (viagem) => (
                <option
                  key={
                    viagem.id
                  }
                  value={
                    viagem.id
                  }
                >
                  {
                    viagem.nome
                  }{" "}
                  —{" "}
                  {
                    viagem.status
                  }
                </option>
              ),
            )}
          </select>
        </div>

        <div>
          <label
            htmlFor="tipo"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Tipo de traslado
          </label>

          <select
            id="tipo"
            value={
              tipo
            }
            onChange={(
              event,
            ) =>
              handleTipoChange(
                event.target
                  .value as
                  | TipoTraslado
                  | "",
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          >
            <option value="">
              Selecione o tipo
            </option>

            <option value="AEROPORTO_PARA_HOSPEDAGEM">
              Aeroporto → Hospedagem
            </option>

            <option value="HOSPEDAGEM_PARA_AEROPORTO">
              Hospedagem → Aeroporto
            </option>

            <option value="OUTRO">
              Outro
            </option>
          </select>
        </div>

        <div>
          <label
            htmlFor="aeroporto"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Aeroporto
          </label>

          <select
            id="aeroporto"
            value={
              aeroporto
            }
            onChange={(
              event,
            ) =>
              handleAeroportoChange(
                event.target
                  .value as
                  | Aeroporto
                  | "",
              )
            }
            disabled={
              tipo === "" ||
              tipo ===
                "OUTRO"
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-60"
          >
            <option value="">
              {tipo === ""
                ? "Selecione o tipo primeiro"
                : tipo ===
                    "OUTRO"
                  ? "Não se aplica"
                  : "Selecione o aeroporto"}
            </option>

            <option value="GRU">
              GRU — Guarulhos
            </option>

            <option value="CGH">
              CGH — Congonhas
            </option>

            <option value="VCP">
              VCP — Viracopos
            </option>
          </select>

          {tipo !== "" &&
            tipo !==
              "OUTRO" && (
              <p className="mt-2 text-xs text-muted">
                O aeroporto selecionado define automaticamente o local correspondente do trajeto.
              </p>
            )}
        </div>

        <div>
          <label
            htmlFor="dataHoraPrevista"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Data e hora prevista
          </label>

          <input
            id="dataHoraPrevista"
            type="datetime-local"
            value={
              dataHoraPrevista
            }
            onChange={(
              event,
            ) =>
              setDataHoraPrevista(
                event.target
                  .value,
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
          />
        </div>

        <div>
          <label
            htmlFor="localOrigem"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Origem
          </label>

          <input
            id="localOrigem"
            type="text"
            value={
              localOrigem
            }
            onChange={(
              event,
            ) =>
              setLocalOrigem(
                event.target
                  .value,
              )
            }
            readOnly={
              tipo ===
              "AEROPORTO_PARA_HOSPEDAGEM"
            }
            placeholder={
              tipo ===
              "AEROPORTO_PARA_HOSPEDAGEM"
                ? "Definido automaticamente pelo aeroporto"
                : "Ex.: Hospedagem Beat Trips"
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary read-only:cursor-not-allowed read-only:opacity-70"
          />
        </div>

        <div>
          <label
            htmlFor="localDestino"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Destino
          </label>

          <input
            id="localDestino"
            type="text"
            value={
              localDestino
            }
            onChange={(
              event,
            ) =>
              setLocalDestino(
                event.target
                  .value,
              )
            }
            readOnly={
              tipo ===
              "HOSPEDAGEM_PARA_AEROPORTO"
            }
            placeholder={
              tipo ===
              "HOSPEDAGEM_PARA_AEROPORTO"
                ? "Definido automaticamente pelo aeroporto"
                : "Ex.: Hospedagem Beat Trips"
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary read-only:cursor-not-allowed read-only:opacity-70"
          />
        </div>

        <div>
          <label
            htmlFor="motorista"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Motorista
          </label>

          <select
            id="motorista"
            value={
              motoristaId
            }
            onChange={(
              event,
            ) =>
              setMotoristaId(
                event.target
                  .value,
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
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
            htmlFor="veiculo"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Veículo
          </label>

          <select
            id="veiculo"
            value={
              veiculoId
            }
            onChange={(
              event,
            ) =>
              setVeiculoId(
                event.target
                  .value,
              )
            }
            className="h-12 w-full rounded-xl border border-border bg-background px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
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
                  —{" "}
                  {
                    veiculo.placa
                  }{" "}
                  —{" "}
                  {
                    veiculo.capacidadePassageiros
                  }{" "}
                  passageiros
                </option>
              ),
            )}
          </select>
        </div>

        <div>
          <label
            htmlFor="observacao"
            className="mb-2 block text-sm font-medium text-foreground"
          >
            Observação
          </label>

          <textarea
            id="observacao"
            value={
              observacao
            }
            onChange={(
              event,
            ) =>
              setObservacao(
                event.target
                  .value,
              )
            }
            rows={4}
            placeholder="Informações adicionais sobre a operação."
            className="w-full rounded-xl border border-border bg-background px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
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

        <div className="flex flex-col gap-3 sm:flex-row">
          <button
            type="submit"
            disabled={
              salvando
            }
            className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {salvando
              ? "Criando operação..."
              : "Criar operação"}
          </button>

          <button
            type="button"
            onClick={() =>
              router.push(
                "/traslados",
              )
            }
            disabled={
              salvando
            }
            className="rounded-xl border border-border px-5 py-3 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}