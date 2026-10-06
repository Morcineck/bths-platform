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
          Number(viagemId),
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

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div>
        <Link
          href="/avisos"
          className="text-sm text-muted transition-colors hover:text-foreground"
        >
          ← Voltar para avisos
        </Link>

        <h1 className="mt-4 text-2xl font-semibold text-foreground">
          Novo aviso
        </h1>

        <p className="mt-1 text-sm text-muted">
          Publique um comunicado para os hóspedes de uma viagem.
        </p>
      </div>

      {erro && (
        <div
          role="alert"
          className="rounded-xl border border-danger/30 bg-danger/10 px-4 py-3 text-sm text-danger"
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
              Comunicado
            </h2>

            <p className="mt-1 text-sm text-muted">
              Defina o conteúdo e a categoria do aviso.
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
                onChange={(event) =>
                  setViagemId(
                    event.target.value,
                  )
                }
                required
                disabled={salvando}
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
                    event.target
                      .value as TipoAviso,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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

          <div className="space-y-2">
            <label
              htmlFor="titulo"
              className="text-sm font-medium text-foreground"
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="space-y-2">
            <label
              htmlFor="mensagem"
              className="text-sm font-medium text-foreground"
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
              rows={6}
              placeholder="Escreva o comunicado que será exibido aos hóspedes."
              className="w-full rounded-xl border border-border bg-background px-3 py-3 text-sm text-foreground outline-none placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-5 border-t border-border pt-6">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Publicação
            </h2>

            <p className="mt-1 text-sm text-muted">
              Controle quando o aviso foi publicado e se ele ficará visível no App Hóspede.
            </p>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="dataPublicacao"
              className="text-sm font-medium text-foreground"
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <label className="flex cursor-pointer items-center justify-between gap-4 rounded-2xl border border-border bg-background/40 p-4">
            <div>
              <p className="text-sm font-medium text-foreground">
                Aviso publicado
              </p>

              <p className="mt-1 text-xs text-muted">
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

        <div className="flex flex-col-reverse gap-3 border-t border-border pt-6 sm:flex-row sm:justify-end">
          <Link
            href="/avisos"
            className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-4 text-sm font-medium text-foreground transition-colors hover:bg-background"
          >
            Cancelar
          </Link>

          <button
            type="submit"
            disabled={salvando}
            className="inline-flex h-11 items-center justify-center rounded-xl bg-primary px-5 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {salvando
              ? "Publicando..."
              : "Publicar aviso"}
          </button>
        </div>
      </form>
    </div>
  );
}