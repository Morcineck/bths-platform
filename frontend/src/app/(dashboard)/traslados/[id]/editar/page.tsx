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

import {
  Save,
} from "lucide-react";

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
    !Number.isNaN(
      operacaoId,
    );

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
  ] = useState<TipoTraslado | "">("");

  const [
    aeroporto,
    setAeroporto,
  ] = useState<Aeroporto | "">("");

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
            observacao.trim() === ""
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
      <div
        role="alert"
        className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
      >
        Identificador da operação inválido.
      </div>
    );
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando operação...
      </p>
    );
  }

  const inputClass =
    "h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-60";

  const labelClass =
    "mb-2 block text-xs font-medium text-muted";

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <PageHeader
        title="Editar operação de traslado"
        description="Atualize o trajeto e os recursos responsáveis pela operação."
      />

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
      >
        <div className="space-y-5">
          <div>
            <label
              htmlFor="viagem"
              className={labelClass}
            >
              Viagem
            </label>

            <input
              id="viagem"
              type="text"
              value={viagemNome}
              disabled
              className={inputClass}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="tipo"
                className={labelClass}
              >
                Tipo de traslado
              </label>

              <select
                id="tipo"
                value={tipo}
                onChange={(event) =>
                  handleTipoChange(
                    event.target.value as
                      | TipoTraslado
                      | "",
                  )
                }
                className={inputClass}
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
                className={labelClass}
              >
                Aeroporto
              </label>

              <select
                id="aeroporto"
                value={aeroporto}
                onChange={(event) =>
                  handleAeroportoChange(
                    event.target.value as
                      | Aeroporto
                      | "",
                  )
                }
                disabled={
                  tipo === "" ||
                  tipo === "OUTRO"
                }
                className={inputClass}
              >
                <option value="">
                  {tipo === ""
                    ? "Selecione o tipo primeiro"
                    : tipo === "OUTRO"
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
            </div>
          </div>

          {tipo !== "" &&
            tipo !== "OUTRO" && (
              <p className="-mt-2 text-[11px] leading-5 text-muted">
                O aeroporto selecionado define automaticamente o local correspondente do trajeto.
              </p>
            )}

          <div>
            <label
              htmlFor="dataHoraPrevista"
              className={labelClass}
            >
              Data e hora prevista
            </label>

            <input
              id="dataHoraPrevista"
              type="datetime-local"
              value={dataHoraPrevista}
              onChange={(event) =>
                setDataHoraPrevista(
                  event.target.value,
                )
              }
              className={inputClass}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="localOrigem"
                className={labelClass}
              >
                Origem
              </label>

              <input
                id="localOrigem"
                type="text"
                value={localOrigem}
                onChange={(event) =>
                  setLocalOrigem(
                    event.target.value,
                  )
                }
                readOnly={
                  tipo ===
                  "AEROPORTO_PARA_HOSPEDAGEM"
                }
                className={`${inputClass} read-only:cursor-not-allowed read-only:opacity-70`}
              />
            </div>

            <div>
              <label
                htmlFor="localDestino"
                className={labelClass}
              >
                Destino
              </label>

              <input
                id="localDestino"
                type="text"
                value={localDestino}
                onChange={(event) =>
                  setLocalDestino(
                    event.target.value,
                  )
                }
                readOnly={
                  tipo ===
                  "HOSPEDAGEM_PARA_AEROPORTO"
                }
                className={`${inputClass} read-only:cursor-not-allowed read-only:opacity-70`}
              />
            </div>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="motorista"
                className={labelClass}
              >
                Motorista
              </label>

              <select
                id="motorista"
                value={motoristaId}
                onChange={(event) =>
                  setMotoristaId(
                    event.target.value,
                  )
                }
                className={inputClass}
              >
                <option value="">
                  Selecione o motorista
                </option>

                {motoristas.map(
                  (motorista) => (
                    <option
                      key={motorista.id}
                      value={motorista.id}
                    >
                      {motorista.nomeCompleto}
                    </option>
                  ),
                )}
              </select>
            </div>

            <div>
              <label
                htmlFor="veiculo"
                className={labelClass}
              >
                Veículo
              </label>

              <select
                id="veiculo"
                value={veiculoId}
                onChange={(event) =>
                  setVeiculoId(
                    event.target.value,
                  )
                }
                className={inputClass}
              >
                <option value="">
                  Selecione o veículo
                </option>

                {veiculos.map(
                  (veiculo) => (
                    <option
                      key={veiculo.id}
                      value={veiculo.id}
                    >
                      {veiculo.modelo} | {veiculo.placa} |{" "}
                      {veiculo.capacidadePassageiros} passageiros
                    </option>
                  ),
                )}
              </select>
            </div>
          </div>

          <div>
            <label
              htmlFor="observacao"
              className={labelClass}
            >
              Observação
            </label>

            <textarea
              id="observacao"
              value={observacao}
              onChange={(event) =>
                setObservacao(
                  event.target.value,
                )
              }
              rows={3}
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
            />
          </div>
        </div>

        <div className="mt-5 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <button
            type="button"
            onClick={() =>
              router.push(
                "/traslados",
              )
            }
            disabled={salvando}
            className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
          >
            Cancelar
          </button>

          <button
            type="submit"
            disabled={salvando}
            className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Save
              size={17}
              strokeWidth={1.9}
            />

            {salvando
              ? "Salvando..."
              : "Salvar alterações"}
          </button>
        </div>
      </form>
    </div>
  );
}