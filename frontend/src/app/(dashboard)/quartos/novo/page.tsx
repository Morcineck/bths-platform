"use client";

import Link from "next/link";

import {
  FormEvent,
  useEffect,
  useState,
} from "react";

import {
  useRouter,
  useSearchParams,
} from "next/navigation";

import {
  ArrowLeft,
  Save,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  cadastrarQuarto,
} from "@/features/quarto/services/quartoService";

import type {
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

export default function NovoQuartoPage() {
  const router = useRouter();
  const searchParams =
    useSearchParams();

  const viagemIdInicial =
    searchParams.get("viagemId");

  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    hospedagens,
    setHospedagens,
  ] = useState<Hospedagem[]>([]);

  const [
    viagemId,
    setViagemId,
  ] = useState(
    viagemIdInicial ?? "",
  );

  const [
    hospedagemId,
    setHospedagemId,
  ] = useState("");

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
    carregandoViagens,
    setCarregandoViagens,
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

  useEffect(() => {
    async function carregarViagens() {
      try {
        setErro(null);

        const dados =
          await listarViagens();

        setViagens(
          dados,
        );

        if (
          !viagemIdInicial &&
          dados.length > 0
        ) {
          const viagemPreferencial =
            dados.find(
              (viagem) =>
                viagem.status ===
                "EM_ANDAMENTO",
            ) ??
            dados.find(
              (viagem) =>
                viagem.status ===
                "PLANEJADA",
            ) ??
            dados[0];

          setViagemId(
            String(
              viagemPreferencial.id,
            ),
          );
        }
      } catch {
        setErro(
          "Não foi possível carregar as viagens.",
        );
      } finally {
        setCarregandoViagens(
          false,
        );
      }
    }

    carregarViagens();
  }, [viagemIdInicial]);

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

        setHospedagemId("");
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

      await cadastrarQuarto({
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
      });

      router.push(
        "/quartos",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar o quarto.",
      );
    } finally {
      setSalvando(false);
    }
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
          title="Novo quarto"
          description="Cadastre uma suíte ou alojamento para a viagem."
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
              Informações principais do quarto.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
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
                disabled={
                  salvando ||
                  carregandoViagens
                }
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
                placeholder="Ex.: Suíte 03"
                className={inputClass}
              />
            </div>
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Configuração
            </h2>

            <p className="mt-1 text-xs text-muted">
              Tipo, capacidade e disponibilidade do quarto.
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
                placeholder="Ex.: 6"
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
              Vincule o quarto ao local onde ele está fisicamente.
            </p>
          </div>

          <div>
            <label
              htmlFor="hospedagem"
              className={labelClass}
            >
              Hospedagem
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
            disabled={
              salvando ||
              carregandoViagens
            }
            className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Save
              size={17}
              strokeWidth={1.9}
            />

            {salvando
              ? "Cadastrando..."
              : "Cadastrar quarto"}
          </button>
        </div>
      </form>
    </div>
  );
}