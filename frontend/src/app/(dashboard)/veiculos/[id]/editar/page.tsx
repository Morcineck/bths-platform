"use client";

import Link from "next/link";
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
  ArrowLeft,
  Car,
  Save,
  Users,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  atualizarVeiculo,
  buscarVeiculoPorId,
} from "@/features/veiculo/services/veiculoService";

export default function EditarVeiculoPage() {
  const params =
    useParams();

  const router =
    useRouter();

  const id =
    Number(params.id);

  const idInvalido =
    Number.isNaN(id);

  const [
    modelo,
    setModelo,
  ] = useState("");

  const [
    placa,
    setPlaca,
  ] = useState("");

  const [
    capacidadePassageiros,
    setCapacidadePassageiros,
  ] = useState("");

  const [
    observacao,
    setObservacao,
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
  ] = useState<string | null>(
    null,
  );

  useEffect(() => {
    if (idInvalido) {
      return;
    }

    async function carregar() {
      try {
        setCarregando(true);
        setErro(null);

        const veiculo =
          await buscarVeiculoPorId(
            id,
          );

        setModelo(
          veiculo.modelo,
        );

        setPlaca(
          veiculo.placa,
        );

        setCapacidadePassageiros(
          String(
            veiculo.capacidadePassageiros,
          ),
        );

        setObservacao(
          veiculo.observacao ?? "",
        );
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
  }, [
    id,
    idInvalido,
  ]);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    try {
      setSalvando(true);
      setErro(null);

      await atualizarVeiculo(
        id,
        {
          modelo:
            modelo.trim(),

          placa:
            placa
              .trim()
              .toUpperCase(),

          capacidadePassageiros:
            Number(
              capacidadePassageiros,
            ),

          observacao:
            observacao.trim() === ""
              ? null
              : observacao.trim(),
        },
      );

      router.push(
        "/veiculos",
      );

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
      <div
        role="alert"
        className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
      >
        Identificador do veículo inválido.
      </div>
    );
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando veículo...
      </p>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/veiculos"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para veículos
        </Link>

        <PageHeader
          title="Editar veículo"
          description="Atualize os dados operacionais do veículo."
        />
      </div>

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {!erro && (
        <form
          onSubmit={handleSubmit}
          className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
        >
          <div className="space-y-5">
            <div>
              <label
                htmlFor="modelo"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Modelo
              </label>

              <div className="relative">
                <Car
                  size={17}
                  strokeWidth={1.9}
                  className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-muted"
                />

                <input
                  id="modelo"
                  type="text"
                  value={modelo}
                  onChange={(event) =>
                    setModelo(
                      event.target.value,
                    )
                  }
                  required
                  disabled={salvando}
                  className="h-11 w-full rounded-xl border border-border bg-background/30 pl-11 pr-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
                />
              </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label
                  htmlFor="placa"
                  className="mb-2 block text-xs font-medium text-muted"
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
                  className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm uppercase tracking-wide text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
                />

                <p className="mt-1.5 text-[11px] text-muted">
                  Formato antigo ou Mercosul.
                </p>
              </div>

              <div>
                <label
                  htmlFor="capacidadePassageiros"
                  className="mb-2 block text-xs font-medium text-muted"
                >
                  Capacidade
                </label>

                <div className="relative">
                  <Users
                    size={17}
                    strokeWidth={1.9}
                    className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-muted"
                  />

                  <input
                    id="capacidadePassageiros"
                    type="number"
                    min={1}
                    value={
                      capacidadePassageiros
                    }
                    onChange={(event) =>
                      setCapacidadePassageiros(
                        event.target.value,
                      )
                    }
                    required
                    disabled={salvando}
                    className="h-11 w-full rounded-xl border border-border bg-background/30 pl-11 pr-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
                  />
                </div>
              </div>
            </div>

            <div>
              <label
                htmlFor="observacao"
                className="mb-2 block text-xs font-medium text-muted"
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
                disabled={salvando}
                rows={3}
                className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="mt-5 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
            <Link
              href="/veiculos"
              className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
            >
              Cancelar
            </Link>

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
      )}
    </div>
  );
}