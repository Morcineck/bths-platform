"use client";

import {
  FormEvent,
  useEffect,
  useState,
} from "react";

import {
  useParams,
  useRouter,
} from "next/navigation";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  listarMotoristas,
} from "@/features/motorista/services/motoristaService";

import type {
  Motorista,
} from "@/features/motorista/types/motorista";

import {
  atualizarOperacaoTraslado,
  buscarOperacaoTrasladoPorId,
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

const LOCAL_AEROPORTO: Record<
  Aeroporto,
  string
> = {
  GRU: "Aeroporto de Guarulhos",
  CGH: "Aeroporto de Congonhas",
  VCP: "Aeroporto de Viracopos",
};

export default function EditarOperacaoTrasladoPage() {
  const router = useRouter();

  const params = useParams<{
    id: string;
  }>();

  const operacaoId =
    Number(params.id);

  const operacaoIdValido =
    !Number.isNaN(operacaoId);

  const [
    motoristas,
    setMotoristas,
  ] = useState<Motorista[]>([]);

  const [
    veiculos,
    setVeiculos,
  ] = useState<Veiculo[]>([]);

  const [
    tipo,
    setTipo,
  ] = useState<
    TipoTraslado | ""
  >("");

  const [
    aeroporto,
    setAeroporto,
  ] = useState<
    Aeroporto | ""
  >("");

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

  const [
    motoristaId,
    setMotoristaId,
  ] = useState("");

  const [
    veiculoId,
    setVeiculoId,
  ] = useState("");

  const [
    observacao,
    setObservacao,
  ] = useState("");

  const [
    viagemNome,
    setViagemNome,
  ] = useState("");

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    salvando,
    setSalvando,
  ] = useState(false);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    if (!operacaoIdValido) {
      return;
    }

    async function carregarDados() {
      try {
        setErro("");

        const [
          operacao,
          dadosMotoristas,
          dadosVeiculos,
        ] = await Promise.all([
          buscarOperacaoTrasladoPorId(
            operacaoId,
          ),
          listarMotoristas(),
          listarVeiculos(),
        ]);

        setViagemNome(
          operacao.viagemNome,
        );

        setTipo(
          operacao.tipo,
        );

        const aeroportoOperacao =
          operacao.aeroporto ?? "";

        setAeroporto(
          aeroportoOperacao,
        );

        setDataHoraPrevista(
          operacao.dataHoraPrevista
            ? operacao.dataHoraPrevista.slice(
                0,
                16,
              )
            : "",
        );

        const localAeroporto =
          aeroportoOperacao === ""
            ? ""
            : LOCAL_AEROPORTO[
                aeroportoOperacao
              ];

        if (
          operacao.tipo ===
            "AEROPORTO_PARA_HOSPEDAGEM" &&
          localAeroporto
        ) {
          setLocalOrigem(
            localAeroporto,
          );
        } else {
          setLocalOrigem(
            operacao.localOrigem,
          );
        }

        if (
          operacao.tipo ===
            "HOSPEDAGEM_PARA_AEROPORTO" &&
          localAeroporto
        ) {
          setLocalDestino(
            localAeroporto,
          );
        } else {
          setLocalDestino(
            operacao.localDestino,
          );
        }

        setMotoristaId(
          operacao.motoristaId
            ? String(
                operacao.motoristaId,
              )
            : "",
        );

        setVeiculoId(
          operacao.veiculoId
            ? String(
                operacao.veiculoId,
              )
            : "",
        );

        setObservacao(
          operacao.observacao ??
            "",
        );

        setMotoristas(
          dadosMotoristas.filter(
            (motorista) =>
              motorista.ativo ||
              motorista.id ===
                operacao.motoristaId,
          ),
        );

        setVeiculos(
          dadosVeiculos.filter(
            (veiculo) =>
              veiculo.ativo ||
              veiculo.id ===
                operacao.veiculoId,
          ),
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar a operação.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarDados();
  }, [
    operacaoId,
    operacaoIdValido,
  ]);

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

    setTipo(
      novoTipo,
    );

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

      await atualizarOperacaoTraslado(
        operacaoId,
        {
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
            Number(
              motoristaId,
            ),

          veiculoId:
            Number(
              veiculoId,
            ),

          observacao:
            observacao.trim() ===
            ""
              ? null
              : observacao.trim(),
        },
      );

      router.push(
        "/traslados",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar a operação.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (!operacaoIdValido) {
    return (
      <p
        role="alert"
        className="text-sm text-red-400"
      >
        Identificador da operação inválido.
      </p>
    );
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando operação...
      </p>
    );
  }

  return (
    <div className="space-y-8">
      <PageHeader
        title="Editar operação de traslado"
        description="Atualize o trajeto e os recursos responsáveis pela operação."
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

          <input
            id="viagem"
            type="text"
            value={
              viagemNome
            }
            disabled
            className="h-12 w-full cursor-not-allowed rounded-xl border border-border bg-background px-4 text-sm text-muted opacity-70"
          />
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
              GRU - Guarulhos
            </option>

            <option value="CGH">
              CGH - Congonhas
            </option>

            <option value="VCP">
              VCP - Viracopos
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
                  }
                  {" | "}
                  {
                    veiculo.placa
                  }
                  {" | "}
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
              ? "Salvando..."
              : "Salvar alterações"}
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