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
  cadastrarAgenda,
} from "@/features/agenda/services/agendaViagemService";

import type {
  TipoAgendaViagem,
} from "@/features/agenda/types/agendaViagem";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

export default function NovaAgendaViagemPage() {
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
    viagemId,
    setViagemId,
  ] = useState(
    viagemIdInicial ?? "",
  );

  const [
    titulo,
    setTitulo,
  ] = useState("");

  const [
    descricao,
    setDescricao,
  ] = useState("");

  const [
    dataHoraInicio,
    setDataHoraInicio,
  ] = useState("");

  const [
    dataHoraFim,
    setDataHoraFim,
  ] = useState("");

  const [
    tipo,
    setTipo,
  ] =
    useState<TipoAgendaViagem>(
      "FESTIVAL",
    );

  const [
    ordem,
    setOrdem,
  ] = useState("1");

  const [
    visivelHospede,
    setVisivelHospede,
  ] = useState(true);

  const [
    ativo,
    setAtivo,
  ] = useState(true);

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
        setCarregando(
          false,
        );
      }
    }

    carregarViagens();
  }, [viagemIdInicial]);

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

    if (!titulo.trim()) {
      setErro(
        "Informe o título do evento.",
      );

      return;
    }

    if (!dataHoraInicio) {
      setErro(
        "Informe a data e hora de início.",
      );

      return;
    }

    const ordemNumerica =
      Number(
        ordem,
      );

    if (
      !Number.isInteger(
        ordemNumerica,
      ) ||
      ordemNumerica < 0
    ) {
      setErro(
        "Informe uma ordem válida.",
      );

      return;
    }

    try {
      setSalvando(true);
      setErro(null);

      await cadastrarAgenda({
        titulo:
          titulo.trim(),

        descricao:
          descricao.trim()
            ? descricao.trim()
            : null,

        dataHoraInicio,

        dataHoraFim:
          dataHoraFim || null,

        tipo,

        ordem:
          ordemNumerica,

        visivelHospede,

        ativo,

        viagemId:
          Number(
            viagemId,
          ),
      });

      router.push(
        "/agenda-viagem",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar o evento.",
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando...
      </p>
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
          href="/agenda-viagem"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para agenda
        </Link>

        <PageHeader
          title="Novo evento"
          description="Cadastre um marco da experiência que poderá aparecer na timeline do hóspede."
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
              Informações principais do evento.
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
                onChange={(event) =>
                  setViagemId(
                    event.target.value,
                  )
                }
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
                      .value as TipoAgendaViagem,
                  )
                }
                disabled={salvando}
                className={inputClass}
              >
                <option value="CHEGADA">
                  Chegada
                </option>

                <option value="CHECK_IN">
                  Check-in
                </option>

                <option value="WELCOME_BEAT">
                  Welcome Beat
                </option>

                <option value="FESTIVAL">
                  Festival
                </option>

                <option value="CHECK_OUT">
                  Check-out
                </option>

                <option value="OUTRO">
                  Outro
                </option>
              </select>
            </div>
          </div>

          <div>
            <label
              htmlFor="titulo"
              className={labelClass}
            >
              Título
            </label>

            <input
              id="titulo"
              type="text"
              value={titulo}
              onChange={(event) =>
                setTitulo(
                  event.target.value,
                )
              }
              required
              disabled={salvando}
              placeholder="Ex.: Festival — Dia 1"
              className={inputClass}
            />
          </div>

          <div>
            <label
              htmlFor="descricao"
              className={labelClass}
            >
              Descrição
            </label>

            <textarea
              id="descricao"
              value={descricao}
              onChange={(event) =>
                setDescricao(
                  event.target.value,
                )
              }
              disabled={salvando}
              rows={3}
              placeholder="Informação opcional exibida ao hóspede."
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Data e horário
            </h2>

            <p className="mt-1 text-xs text-muted">
              Defina quando esse marco acontece.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="dataHoraInicio"
                className={labelClass}
              >
                Início
              </label>

              <input
                id="dataHoraInicio"
                type="datetime-local"
                value={
                  dataHoraInicio
                }
                onChange={(event) =>
                  setDataHoraInicio(
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
                htmlFor="dataHoraFim"
                className={labelClass}
              >
                Fim
              </label>

              <input
                id="dataHoraFim"
                type="datetime-local"
                value={
                  dataHoraFim
                }
                onChange={(event) =>
                  setDataHoraFim(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className={inputClass}
              />

              <p className="mt-2 text-[11px] text-muted">
                Opcional.
              </p>
            </div>
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Exibição
            </h2>

            <p className="mt-1 text-xs text-muted">
              Controle a posição e a visibilidade do evento.
            </p>
          </div>

          <div>
            <label
              htmlFor="ordem"
              className={labelClass}
            >
              Ordem
            </label>

            <input
              id="ordem"
              type="number"
              min={0}
              step={1}
              value={ordem}
              onChange={(event) =>
                setOrdem(
                  event.target.value,
                )
              }
              required
              disabled={salvando}
              className={inputClass}
            />

            <p className="mt-2 text-[11px] text-muted">
              Números menores aparecem primeiro na timeline.
            </p>
          </div>

          <div className="grid gap-2 md:grid-cols-2">
            <label className="flex cursor-pointer items-center justify-between gap-4 rounded-xl border border-border bg-background/25 p-3">
              <div>
                <p className="text-sm font-medium text-foreground">
                  Visível ao hóspede
                </p>

                <p className="mt-1 text-[11px] leading-5 text-muted">
                  Exibe este evento no App Hóspede.
                </p>
              </div>

              <input
                type="checkbox"
                checked={
                  visivelHospede
                }
                onChange={(event) =>
                  setVisivelHospede(
                    event.target.checked,
                  )
                }
                disabled={salvando}
                className="h-4 w-4 accent-primary"
              />
            </label>

            <label className="flex cursor-pointer items-center justify-between gap-4 rounded-xl border border-border bg-background/25 p-3">
              <div>
                <p className="text-sm font-medium text-foreground">
                  Evento ativo
                </p>

                <p className="mt-1 text-[11px] leading-5 text-muted">
                  Desative sem precisar excluir o registro.
                </p>
              </div>

              <input
                type="checkbox"
                checked={ativo}
                onChange={(event) =>
                  setAtivo(
                    event.target.checked,
                  )
                }
                disabled={salvando}
                className="h-4 w-4 accent-primary"
              />
            </label>
          </div>
        </section>

        <div className="flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <Link
            href="/agenda-viagem"
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
              ? "Cadastrando..."
              : "Cadastrar evento"}
          </button>
        </div>
      </form>
    </div>
  );
}