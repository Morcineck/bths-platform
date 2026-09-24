"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

import {
  ativarVeiculo,
  inativarVeiculo,
  listarVeiculos,
} from "@/features/veiculo/services/veiculoService";

import type { Veiculo } from "@/features/veiculo/types/veiculo";

export default function VeiculosPage() {
  const [veiculos, setVeiculos] = useState<Veiculo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [mensagem, setMensagem] = useState<string | null>(null);
  const [alterandoId, setAlterandoId] = useState<number | null>(null);

  useEffect(() => {
    async function carregar() {
      try {
        setCarregando(true);
        setErro(null);

        const dados = await listarVeiculos();

        setVeiculos(dados);
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os veículos.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, []);

  async function alterarStatus(veiculo: Veiculo) {
    try {
      setAlterandoId(veiculo.id);
      setErro(null);
      setMensagem(null);

      const atualizado = veiculo.ativo
        ? await inativarVeiculo(veiculo.id)
        : await ativarVeiculo(veiculo.id);

      setVeiculos((atuais) =>
        atuais.map((item) =>
          item.id === atualizado.id
            ? atualizado
            : item,
        ),
      );

      setMensagem(
        atualizado.ativo
          ? "Veículo ativado com sucesso."
          : "Veículo inativado com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível alterar o status do veículo.",
      );
    } finally {
      setAlterandoId(null);
    }
  }

  if (carregando) {
    return (
      <div className="p-6">
        <p className="text-sm text-zinc-400">
          Carregando veículos...
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6 p-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-white">
            Veículos
          </h1>

          <p className="mt-1 text-sm text-zinc-400">
            Gerencie os veículos disponíveis para a operação de transporte.
          </p>
        </div>

        <Link
          href="/veiculos/novo"
          className="inline-flex items-center justify-center rounded-xl bg-primary px-4 py-3 text-sm font-medium text-white transition-colors hover:opacity-90"
        >
          Novo veículo
        </Link>
      </div>

      {mensagem && (
        <div className="rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">
          {mensagem}
        </div>
      )}

      {erro && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {erro}
        </div>
      )}

      {veiculos.length === 0 ? (
        <div className="rounded-xl border border-zinc-800 bg-zinc-950 p-6">
          <p className="text-sm text-zinc-400">
            Nenhum veículo cadastrado.
          </p>
        </div>
      ) : (
        <div className="grid gap-4">
          {veiculos.map((veiculo) => (
            <div
              key={veiculo.id}
              className="rounded-xl border border-zinc-800 bg-zinc-950 p-5"
            >
              <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div className="space-y-2">
                  <div className="flex flex-wrap items-center gap-3">
                    <h2 className="text-lg font-medium text-white">
                      {veiculo.modelo}
                    </h2>

                    <span
                      className={`rounded-full px-2.5 py-1 text-xs font-medium ${
                        veiculo.ativo
                          ? "bg-emerald-500/10 text-emerald-300"
                          : "bg-zinc-800 text-zinc-400"
                      }`}
                    >
                      {veiculo.ativo
                        ? "Ativo"
                        : "Inativo"}
                    </span>
                  </div>

                  <div className="flex flex-wrap gap-x-6 gap-y-1 text-sm text-zinc-400">
                    <span>
                      Placa:{" "}
                      <strong className="font-medium text-zinc-200">
                        {veiculo.placa}
                      </strong>
                    </span>

                    <span>
                      Capacidade:{" "}
                      <strong className="font-medium text-zinc-200">
                        {veiculo.capacidadePassageiros} passageiros
                      </strong>
                    </span>
                  </div>

                  {veiculo.observacao && (
                    <p className="text-sm text-zinc-500">
                      {veiculo.observacao}
                    </p>
                  )}
                </div>

                <div className="flex flex-wrap gap-2">
                  <Link
                    href={`/veiculos/${veiculo.id}/editar`}
                    className="rounded-lg border border-zinc-700 px-3 py-2 text-sm text-zinc-200 transition hover:bg-zinc-900"
                  >
                    Editar
                  </Link>

                  <button
                    type="button"
                    disabled={alterandoId === veiculo.id}
                    onClick={() => alterarStatus(veiculo)}
                    className="rounded-lg border border-zinc-700 px-3 py-2 text-sm text-zinc-200 transition hover:bg-zinc-900 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    {alterandoId === veiculo.id
                      ? "Salvando..."
                      : veiculo.ativo
                        ? "Inativar"
                        : "Ativar"}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}