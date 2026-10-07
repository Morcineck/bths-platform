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
  Save,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  atualizarQuarto,
  buscarQuartoPorId,
} from "@/features/quarto/services/quartoService";

import type {
  Quarto,
  StatusQuarto,
  TipoQuarto,
} from "@/features/quarto/types/quarto";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

import {
  listarHospedagensPorViagem,
} from "@/features/hospedagem/services/hospedagemService";

import type {
  Hospedagem,
} from "@/features/hospedagem/types/hospedagem";

export default function EditarQuartoPage() {
  const params = useParams();
  const router = useRouter();

  const quartoId =
    Number(params.id);

  const [
    quarto,
    setQuarto,
  ] = useState<Quarto | null>(
    null,
  );

  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    hospedagens,
    setHospedagens,
  ] = useState<Hospedagem[]>([]);

  const [
    nome,
    setNome,
  ] = useState("");

  const [
    tipo,
    setTipo,
  ] = useState<TipoQuarto>(
    "SUITE",
  );

  const [
    capacidade,
    setCapacidade,
  ] = useState("");

  const [
    status,
    setStatus,
  ] = useState<StatusQuarto>(
    "DISPONIVEL",
  );

  const [
    viagemId,
    setViagemId,
  ] = useState("");

  const [
    hospedagemId,
    setHospedagemId,
  ] = useState("");

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    carregandoHospedagens,
    setCarregandoHospedagens,
  ] = useState(false);

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

  const [
    sucesso,
    setSucesso,
  ] = useState<string | null>(
    null,
  );

  useEffect(() => {
    async function carregarDados() {
      try {
        setErro(null);

        const [
          quartoResponse,
          viagensResponse,
        ] =
          await Promise.all([
            buscarQuartoPorId(
              quartoId,
            ),

            listarViagens(),
          ]);

        setQuarto(
          quartoResponse,
        );

        setViagens(
          viagensResponse,
        );

        setNome(
          quartoResponse.nome,
        );

        setTipo(
          quartoResponse.tipo,
        );

        setCapacidade(
          String(
            quartoResponse.capacidade,
          ),
        );

        setStatus(
          quartoResponse.status,
        );

        setViagemId(
          String(
            quartoResponse.viagemId,
          ),
        );

        setHospedagemId(
          quartoResponse.hospedagemId
            ? String(
                quartoResponse.hospedagemId,
              )
            : "",
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar o quarto.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarDados();
  }, [quartoId]);

  useEffect(() => {
    if (!viagemId) {
      return;
    }

    async function carregarHospedagens() {
      try {
        setCarregandoHospedagens(
          true,
        );

        const dados =
          await listarHospedagensPorViagem(
            Number(
              viagemId,
            ),
          );

        setHospedagens(
          dados,
        );

        setHospedagemId(
          (atual) => {
            if (!atual) {
              return "";
            }

            const existe =
              dados.some(
                (hospedagem) =>
                  String(
                    hospedagem.id,
                  ) === atual,
              );

            return existe
              ? atual
              : "";
          },
        );
      } catch {
        setHospedagens([]);

        setErro(
          "Não foi possível carregar as hospedagens da viagem.",
        );
      } finally {
        setCarregandoHospedagens(
          false,
        );
      }
    }

    carregarHospedagens();
  }, [viagemId]);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (!quarto) {
      return;
    }

    if (!viagemId) {
      setErro(
        "Selecione uma viagem.",
      );

      return;
    }

    const capacidadeNumerica =
      Number(
        capacidade,
      );

    if (
      !Number.isInteger(
        capacidadeNumerica,
      ) ||
      capacidadeNumerica <= 0
    ) {
      setErro(
        "Informe uma capacidade válida.",
      );

      return;
    }

    try {
      setSalvando(true);
      setErro(null);
      setSucesso(null);

      const response =
        await atualizarQuarto(
          quarto.id,
          {
            nome:
              nome.trim(),

            tipo,

            capacidade:
              capacidadeNumerica,

            status,

            viagemId:
              Number(
                viagemId,
              ),

            hospedagemId:
              hospedagemId
                ? Number(
                    hospedagemId,
                  )
                : null,
          },
        );

      setQuarto(
        response,
      );

      setSucesso(
        "Quarto atualizado com sucesso.",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar o quarto.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando quarto...
      </p>
    );
  }

  if (!quarto) {
    return (
      <div className="space-y-4">
        <Link
          href="/quartos"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para quartos
        </Link>

        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Quarto não encontrado.
        </div>
      </div>
    );
  }

  const inputClass =
    "h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50";

  const labelClass =
    "mb-2 block text-xs font-medium text-muted";

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/quartos"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para quartos
        </Link>

        <PageHeader
          title="Editar quarto"
          description="Atualize os dados operacionais e a hospedagem vinculada."
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

      {sucesso && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {sucesso}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="space-y-6 rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
      >
        <section className="space-y-4">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Identificação
            </h2>

            <p className="mt-1 text-xs text-muted">
              Dados principais do quarto.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="nome"
                className={labelClass}
              >
                Nome
              </label>

              <input
                id="nome"
                type="text"
                value={nome}
                onChange={(event) =>
                  setNome(
                    event.target.value,
                  )
                }
                required
                disabled={salvando}
                className={inputClass}
              />
            </div>

            <div>
              <label
                htmlFor="viagem"
                className={labelClass}
              >
                Viagem
              </label>

              <select
                id="viagem"
                value={viagemId}
                onChange={(event) => {
                  setViagemId(
                    event.target.value,
                  );

                  setHospedagens([]);

                  setHospedagemId("");
                }}
                required
                disabled={salvando}
                className={inputClass}
              >
                <option value="">
                  Selecione uma viagem
                </option>

                {viagens.map(
                  (viagem) => (
                    <option
                      key={viagem.id}
                      value={viagem.id}
                    >
                      {viagem.nome}
                    </option>
                  ),
                )}
              </select>
            </div>
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Configuração
            </h2>

            <p className="mt-1 text-xs text-muted">
              Tipo, capacidade e disponibilidade.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-3">
            <div>
              <label
                htmlFor="tipo"
                className={labelClass}
              >
                Tipo
              </label>

              <select
                id="tipo"
                value={tipo}
                onChange={(event) =>
                  setTipo(
                    event.target
                      .value as TipoQuarto,
                  )
                }
                disabled={salvando}
                className={inputClass}
              >
                <option value="SUITE">
                  Suíte
                </option>

                <option value="ALOJAMENTO">
                  Alojamento
                </option>
              </select>
            </div>

            <div>
              <label
                htmlFor="capacidade"
                className={labelClass}
              >
                Capacidade
              </label>

              <input
                id="capacidade"
                type="number"
                min={1}
                step={1}
                value={capacidade}
                onChange={(event) =>
                  setCapacidade(
                    event.target.value,
                  )
                }
                required
                disabled={salvando}
                className={inputClass}
              />
            </div>

            <div>
              <label
                htmlFor="status"
                className={labelClass}
              >
                Status
              </label>

              <select
                id="status"
                value={status}
                onChange={(event) =>
                  setStatus(
                    event.target
                      .value as StatusQuarto,
                  )
                }
                disabled={salvando}
                className={inputClass}
              >
                <option value="DISPONIVEL">
                  Disponível
                </option>

                <option value="INDISPONIVEL">
                  Indisponível
                </option>
              </select>
            </div>
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Hospedagem
            </h2>

            <p className="mt-1 text-xs text-muted">
              Altere, mova ou remova o vínculo do quarto com uma hospedagem.
            </p>
          </div>

          <div>
            <label
              htmlFor="hospedagem"
              className={labelClass}
            >
              Hospedagem vinculada
            </label>

            <select
              id="hospedagem"
              value={hospedagemId}
              onChange={(event) =>
                setHospedagemId(
                  event.target.value,
                )
              }
              disabled={
                salvando ||
                carregandoHospedagens ||
                !viagemId
              }
              className={inputClass}
            >
              <option value="">
                Sem hospedagem definida
              </option>

              {hospedagens.map(
                (hospedagem) => (
                  <option
                    key={hospedagem.id}
                    value={hospedagem.id}
                  >
                    {hospedagem.nome}
                  </option>
                ),
              )}
            </select>

            {viagemId &&
              !carregandoHospedagens &&
              hospedagens.length ===
                0 && (
                <p className="mt-2 text-[11px] text-muted">
                  Esta viagem ainda não possui hospedagens cadastradas.
                </p>
              )}
          </div>
        </section>

        <div className="flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <Link
            href="/quartos"
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
    </div>
  );
}