"use client";

import { FormEvent, useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";

import {
  atualizarVeiculo,
  buscarVeiculoPorId,
} from "@/features/veiculo/services/veiculoService";

export default function EditarVeiculoPage() {
  const params = useParams();
  const router = useRouter();

  const id = Number(params.id);
  const idInvalido = Number.isNaN(id);

  const [modelo, setModelo] = useState("");
  const [placa, setPlaca] = useState("");
  const [capacidadePassageiros, setCapacidadePassageiros] =
    useState("");
  const [observacao, setObservacao] = useState("");

  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    if (idInvalido) {
      return;
    }

    async function carregar() {
      try {
        setCarregando(true);
        setErro(null);

        const veiculo = await buscarVeiculoPorId(id);

        setModelo(veiculo.modelo);
        setPlaca(veiculo.placa);
        setCapacidadePassageiros(
          String(veiculo.capacidadePassageiros),
        );
        setObservacao(veiculo.observacao ?? "");
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar o veículo.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, [id, idInvalido]);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    try {
      setSalvando(true);
      setErro(null);

      await atualizarVeiculo(id, {
        modelo: modelo.trim(),
        placa: placa.trim().toUpperCase(),
        capacidadePassageiros: Number(
          capacidadePassageiros,
        ),
        observacao:
          observacao.trim() === ""
            ? null
            : observacao.trim(),
      });

      router.push("/veiculos");
      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar o veículo.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (idInvalido) {
    return (
      <div className="p-6">
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          Identificador do veículo inválido.
        </div>
      </div>
    );
  }

  if (carregando) {
    return (
      <div className="p-6">
        <p className="text-sm text-zinc-400">
          Carregando veículo...
        </p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6 p-6">
      <div>
        <Link
          href="/veiculos"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          ← Voltar para veículos
        </Link>

        <h1 className="mt-4 text-2xl font-semibold text-white">
          Editar veículo
        </h1>

        <p className="mt-1 text-sm text-zinc-400">
          Atualize os dados operacionais do veículo.
        </p>
      </div>

      {erro && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {erro}
        </div>
      )}

      {!erro && (
        <form
          onSubmit={handleSubmit}
          className="space-y-6 rounded-xl border border-zinc-800 bg-zinc-950 p-6"
        >
          <div className="space-y-2">
            <label
              htmlFor="modelo"
              className="text-sm font-medium text-zinc-200"
            >
              Modelo
            </label>

            <input
              id="modelo"
              type="text"
              value={modelo}
              onChange={(event) =>
                setModelo(event.target.value)
              }
              required
              disabled={salvando}
              className="w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="grid gap-6 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="placa"
                className="text-sm font-medium text-zinc-200"
              >
                Placa
              </label>

              <input
                id="placa"
                type="text"
                value={placa}
                onChange={(event) =>
                  setPlaca(
                    event.target.value.toUpperCase(),
                  )
                }
                required
                disabled={salvando}
                maxLength={7}
                className="w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm uppercase text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="capacidadePassageiros"
                className="text-sm font-medium text-zinc-200"
              >
                Capacidade de passageiros
              </label>

              <input
                id="capacidadePassageiros"
                type="number"
                min={1}
                value={capacidadePassageiros}
                onChange={(event) =>
                  setCapacidadePassageiros(
                    event.target.value,
                  )
                }
                required
                disabled={salvando}
                className="w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="observacao"
              className="text-sm font-medium text-zinc-200"
            >
              Observação
            </label>

            <textarea
              id="observacao"
              value={observacao}
              onChange={(event) =>
                setObservacao(event.target.value)
              }
              disabled={salvando}
              rows={4}
              className="w-full resize-none rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
            <Link
              href="/veiculos"
              className="inline-flex items-center justify-center rounded-lg border border-zinc-700 px-4 py-2 text-sm text-zinc-200 transition hover:bg-zinc-900"
            >
              Cancelar
            </Link>

            <button
              type="submit"
              disabled={salvando}
              className="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2 text-sm font-medium text-black transition hover:bg-zinc-200 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {salvando
                ? "Salvando..."
                : "Salvar alterações"}
            </button>
          </div>
        </form>
      )}
    </div>
  );
}