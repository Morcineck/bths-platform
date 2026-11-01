"use client";

import { useState } from "react";

import { UserPlus } from "lucide-react";

import { listarHospedesPorViagem } from "@/features/hospede/services/hospedeService";

import type { Hospede } from "@/features/hospede/types/hospede";

import {
  criarTraslado,
  listarTrasladosPorViagem,
} from "@/features/traslado/services/trasladoService";

import type { Traslado } from "@/features/traslado/types/traslado";

import { vincularTrasladoOperacao } from "../services/operacaoTrasladoService";

import type {
  OperacaoTraslado,
  OperacaoTrasladoPassageiro,
} from "../types/operacaoTraslado";

type AdicionarPassageiroOperacaoProps = {
  operacao: OperacaoTraslado;
  onAdicionado: (
    operacaoAtualizada: OperacaoTraslado,
    passageiro: OperacaoTrasladoPassageiro,
  ) => void;
};

function trasladoCompativel(
  traslado: Traslado,
  operacao: OperacaoTraslado,
): boolean {
  return (
    traslado.tipo === operacao.tipo &&
    (traslado.tipo === "OUTRO" ||
      traslado.aeroporto === operacao.aeroporto)
  );
}

export function AdicionarPassageiroOperacao({
  operacao,
  onAdicionado,
}: AdicionarPassageiroOperacaoProps) {
  const [aberto, setAberto] = useState(false);
  const [carregando, setCarregando] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  const [hospedesDisponiveis, setHospedesDisponiveis] =
    useState<Hospede[]>([]);
  const [trasladosDaViagem, setTrasladosDaViagem] =
    useState<Traslado[]>([]);
  const [hospedeSelecionadoId, setHospedeSelecionadoId] =
    useState("");

  const operacaoEncerrada =
    operacao.status === "CONCLUIDO" ||
    operacao.status === "CANCELADO";

  const operacaoLotada =
    operacao.vagasDisponiveis !== null &&
    operacao.vagasDisponiveis <= 0;

  async function handleAbrir() {
    try {
      setAberto(true);
      setCarregando(true);
      setErro("");
      setHospedeSelecionadoId("");

      const [hospedes, traslados] = await Promise.all([
        listarHospedesPorViagem(operacao.viagemId),
        listarTrasladosPorViagem(operacao.viagemId),
      ]);

      // Fica de fora quem já está nesta operação e, nos
      // traslados de aeroporto, quem já está em outra
      // operação do mesmo tipo (para isso existe a troca
      // de operação no detalhe do hóspede).
      const hospedesIndisponiveis = new Set(
        traslados
          .filter(
            (traslado) =>
              traslado.operacaoTrasladoId === operacao.id ||
              (traslado.operacaoTrasladoId !== null &&
                operacao.tipo !== "OUTRO" &&
                traslado.tipo === operacao.tipo),
          )
          .map((traslado) => traslado.hospedeId),
      );

      setTrasladosDaViagem(traslados);

      setHospedesDisponiveis(
        hospedes
          .filter(
            (hospede) =>
              !hospedesIndisponiveis.has(hospede.id),
          )
          .sort((a, b) =>
            a.nomeCompleto.localeCompare(b.nomeCompleto),
          ),
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível carregar os hóspedes.",
      );
    } finally {
      setCarregando(false);
    }
  }

  async function handleAdicionar() {
    const hospede = hospedesDisponiveis.find(
      (item) => item.id === Number(hospedeSelecionadoId),
    );

    if (!hospede) {
      setErro("Selecione o hóspede.");
      return;
    }

    try {
      setSalvando(true);
      setErro("");

      // Reaproveita um traslado compatível sem operação,
      // para não duplicar o registro do hóspede.
      const existente = trasladosDaViagem.find(
        (traslado) =>
          traslado.hospedeId === hospede.id &&
          traslado.operacaoTrasladoId === null &&
          trasladoCompativel(traslado, operacao),
      );

      const trasladoId = existente
        ? existente.id
        : (
            await criarTraslado({
              hospedeId: hospede.id,
              viagemId: operacao.viagemId,
              tipo: operacao.tipo,
              aeroporto: operacao.aeroporto,
              dataHoraPrevista: operacao.dataHoraPrevista,
              localOrigem: operacao.localOrigem,
              localDestino: operacao.localDestino,
            })
          ).id;

      const operacaoAtualizada =
        await vincularTrasladoOperacao(
          operacao.id,
          trasladoId,
        );

      onAdicionado(operacaoAtualizada, {
        trasladoId,
        hospedeId: hospede.id,
        hospedeNome: hospede.nomeCompleto,
      });

      setAberto(false);
      setHospedeSelecionadoId("");
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível adicionar o passageiro.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (operacaoEncerrada) {
    return null;
  }

  if (operacaoLotada) {
    return (
      <p className="mt-2 text-xs text-muted">
        Operação lotada: não há vagas para novos passageiros.
      </p>
    );
  }

  if (!aberto) {
    return (
      <button
        type="button"
        onClick={handleAbrir}
        className="mt-2 inline-flex h-9 w-full items-center justify-center gap-2 rounded-xl border border-primary/30 px-3 text-xs font-medium text-primary transition-colors hover:bg-primary/5"
      >
        <UserPlus size={14} strokeWidth={1.9} />
        Adicionar passageiro
      </button>
    );
  }

  return (
    <div className="mt-2 space-y-2 rounded-xl border border-border bg-surface/40 p-2.5">
      {erro && (
        <p role="alert" className="text-xs leading-5 text-danger">
          {erro}
        </p>
      )}

      {carregando ? (
        <p className="text-xs text-muted">
          Carregando hóspedes...
        </p>
      ) : hospedesDisponiveis.length === 0 ? (
        <p className="text-xs leading-5 text-muted">
          Nenhum hóspede disponível para esta operação.
        </p>
      ) : (
        <select
          aria-label="Hóspede"
          value={hospedeSelecionadoId}
          onChange={(event) =>
            setHospedeSelecionadoId(event.target.value)
          }
          className="h-10 w-full rounded-xl border border-border bg-background/30 px-3 text-sm text-foreground outline-none transition-colors focus:border-primary"
        >
          <option value="">Selecione o hóspede</option>

          {hospedesDisponiveis.map((hospede) => (
            <option key={hospede.id} value={hospede.id}>
              {hospede.nomeCompleto}
            </option>
          ))}
        </select>
      )}

      <div className="grid grid-cols-2 gap-2">
        <button
          type="button"
          disabled={
            salvando ||
            carregando ||
            !hospedeSelecionadoId
          }
          onClick={handleAdicionar}
          className="inline-flex h-9 items-center justify-center rounded-xl border border-primary/30 bg-primary/10 px-3 text-xs font-medium text-primary transition-colors hover:bg-primary/20 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {salvando ? "Adicionando..." : "Adicionar"}
        </button>

        <button
          type="button"
          disabled={salvando}
          onClick={() => {
            setAberto(false);
            setErro("");
          }}
          className="inline-flex h-9 items-center justify-center rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:bg-surface/50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Cancelar
        </button>
      </div>
    </div>
  );
}