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
  cadastrarAviso,
} from "@/features/aviso/services/avisoService";

import type {
  TipoAviso,
} from "@/features/aviso/types/aviso";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

export default function NovoAvisoPage() {
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
    mensagem,
    setMensagem,
  ] = useState("");

  const [
    tipo,
    setTipo,
  ] =
    useState<TipoAviso>(
      "INFORMATIVO",
    );

  const [
    dataPublicacao,
    setDataPublicacao,
  ] = useState("");

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

        const agora =
          new Date();

        agora.setMinutes(
          agora.getMinutes() -
            agora.getTimezoneOffset(),
        );

        setDataPublicacao(
          agora
            .toISOString()
            .slice(0, 16),
        );
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
        "Informe o título do aviso.",
      );
      return;
    }

    if (!mensagem.trim()) {
      setErro(
        "Informe a mensagem do aviso.",
      );
      return;
    }

    if (!dataPublicacao) {
      setErro(
        "Informe a data e hora de publicação.",
      );
      return;
    }

    try {
      setSalvando(true);
      setErro(null);

      await cadastrarAviso({
        titulo:
          titulo.trim(),

        mensagem:
          mensagem.trim(),

        tipo,

        dataPublicacao,

        ativo,

        viagemId:
          Number(
            viagemId,
          ),
      });

      router.push(
        "/avisos",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar o aviso.",
      );
    } finally {
      setSalvando(
        false,
      );
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
          href="/avisos"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para avisos
        </Link>

        <PageHeader
          title="Novo aviso"
          description="Publique um comunicado para os hóspedes de uma viagem."
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
              Comunicado
            </h2>

            <p className="mt-1 text-xs text-muted">
              Defina o conteúdo e a categoria do aviso.
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
                      .value as TipoAviso,
                  )
                }
                disabled={salvando}
                className={inputClass}
              >
                <option value="INFORMATIVO">
                  Informativo
                </option>

                <option value="IMPORTANTE">
                  Importante
                </option>

                <option value="TRANSPORTE">
                  Transporte
                </option>

                <option value="HOSPEDAGEM">
                  Hospedagem
                </option>

                <option value="EVENTO">
                  Evento
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
              placeholder="Ex.: Horário do transporte atualizado"
              className={inputClass}
            />
          </div>

          <div>
            <label
              htmlFor="mensagem"
              className={labelClass}
            >
              Mensagem
            </label>

            <textarea
              id="mensagem"
              value={mensagem}
              onChange={(event) =>
                setMensagem(
                  event.target.value,
                )
              }
              required
              disabled={salvando}
              rows={5}
              placeholder="Escreva o comunicado que será exibido aos hóspedes."
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Publicação
            </h2>

            <p className="mt-1 text-xs text-muted">
              Controle quando o aviso foi publicado e se ele ficará visível no App Hóspede.
            </p>
          </div>

          <div>
            <label
              htmlFor="dataPublicacao"
              className={labelClass}
            >
              Data e hora de publicação
            </label>

            <input
              id="dataPublicacao"
              type="datetime-local"
              value={dataPublicacao}
              onChange={(event) =>
                setDataPublicacao(
                  event.target.value,
                )
              }
              required
              disabled={salvando}
              className={inputClass}
            />
          </div>

          <label className="flex cursor-pointer items-center justify-between gap-4 rounded-xl border border-border bg-background/25 p-3">
            <div>
              <p className="text-sm font-medium text-foreground">
                Aviso publicado
              </p>

              <p className="mt-1 text-[11px] leading-5 text-muted">
                Quando ativo, o aviso ficará disponível no App Hóspede.
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
        </section>

        <div className="flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <Link
            href="/avisos"
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
              ? "Publicando..."
              : "Publicar aviso"}
          </button>
        </div>
      </form>
    </div>
  );
}