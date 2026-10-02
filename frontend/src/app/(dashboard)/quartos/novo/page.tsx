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
            Number(viagemId),
          );

        setHospedagens(
          dados,
        );

        setHospedagemId(
          "",
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

    if (!viagemId) {
      setErro(
        "Selecione uma viagem.",
      );

      return;
    }

    const capacidadeNumerica =
      Number(capacidade);

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
        nome: nome.trim(),
        tipo,
        capacidade:
          capacidadeNumerica,
        status,
        viagemId:
          Number(viagemId),
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

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div>
        <Link
          href="/quartos"
          className="text-sm text-muted transition-colors hover:text-foreground"
        >
          ← Voltar para quartos
        </Link>

        <h1 className="mt-4 text-2xl font-semibold text-foreground">
          Novo quarto
        </h1>

        <p className="mt-1 text-sm text-muted">
          Cadastre uma suíte ou alojamento para a viagem.
        </p>
      </div>

      {erro && (
        <div
          role="alert"
          className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300"
        >
          {erro}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="space-y-8 rounded-2xl border border-border bg-surface p-6"
      >
        <section className="space-y-5">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Identificação
            </h2>

            <p className="mt-1 text-sm text-muted">
              Informações principais do quarto.
            </p>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="viagem"
                className="text-sm font-medium text-foreground"
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

                  setHospedagens(
                    [],
                  );

                  setHospedagemId(
                    "",
                  );
                }}
                required
                disabled={
                  salvando ||
                  carregandoViagens
                }
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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

            <div className="space-y-2">
              <label
                htmlFor="nome"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>
        </section>

        <section className="space-y-5 border-t border-border pt-6">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Configuração
            </h2>

            <p className="mt-1 text-sm text-muted">
              Tipo, capacidade e disponibilidade do quarto.
            </p>
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            <div className="space-y-2">
              <label
                htmlFor="tipo"
                className="text-sm font-medium text-foreground"
              >
                Tipo
              </label>

              <select
                id="tipo"
                value={tipo}
                onChange={(event) =>
                  setTipo(
                    event.target.value as TipoQuarto,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              >
                <option value="SUITE">
                  Suíte
                </option>

                <option value="ALOJAMENTO">
                  Alojamento
                </option>
              </select>
            </div>

            <div className="space-y-2">
              <label
                htmlFor="capacidade"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="status"
                className="text-sm font-medium text-foreground"
              >
                Status
              </label>

              <select
                id="status"
                value={status}
                onChange={(event) =>
                  setStatus(
                    event.target.value as StatusQuarto,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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

        <section className="space-y-5 border-t border-border pt-6">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Hospedagem
            </h2>

            <p className="mt-1 text-sm text-muted">
              Vincule o quarto ao local onde ele está fisicamente.
            </p>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="hospedagem"
              className="text-sm font-medium text-foreground"
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
              hospedagens.length === 0 && (
                <p className="text-xs text-muted">
                  Esta viagem ainda não possui hospedagens cadastradas.
                </p>
              )}
          </div>
        </section>

        <div className="flex flex-col-reverse gap-3 border-t border-border pt-6 sm:flex-row sm:justify-end">
          <Link
            href="/quartos"
            className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-4 text-sm font-medium text-foreground transition-colors hover:bg-background"
          >
            Cancelar
          </Link>

          <button
            type="submit"
            disabled={
              salvando ||
              carregandoViagens
            }
            className="inline-flex h-11 items-center justify-center rounded-xl bg-primary px-5 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {salvando
              ? "Cadastrando..."
              : "Cadastrar quarto"}
          </button>
        </div>
      </form>
    </div>
  );
}