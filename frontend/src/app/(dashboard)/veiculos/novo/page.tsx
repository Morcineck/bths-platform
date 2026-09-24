"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

import { cadastrarVeiculo } from "@/features/veiculo/services/veiculoService";

export default function NovoVeiculoPage() {
  const router = useRouter();

  const [modelo, setModelo] = useState("");
  const [placa, setPlaca] = useState("");
  const [capacidadePassageiros, setCapacidadePassageiros] =
    useState("");
  const [observacao, setObservacao] = useState("");

  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    try {
      setSalvando(true);
      setErro(null);

      await cadastrarVeiculo({
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
          : "Não foi possível cadastrar o veículo.",
      );
    } finally {
      setSalvando(false);
    }
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
          Novo veículo
        </h1>

        <p className="mt-1 text-sm text-zinc-400">
          Cadastre um veículo disponível para a operação
          de transporte.
        </p>
      </div>

      {erro && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {erro}
        </div>
      )}

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
            placeholder="Ex.: Renault Duster"
            className="w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
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
              placeholder="ABC1D23"
              className="w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm uppercase text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />

            <p className="text-xs text-zinc-500">
              Formato antigo ou Mercosul.
            </p>
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
              placeholder="4"
              className="w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
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
            placeholder="Informações adicionais sobre o veículo..."
            className="w-full resize-none rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
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
              : "Cadastrar veículo"}
          </button>
        </div>
      </form>
    </div>
  );
}