"use client";

import Link from "next/link";
import {
  FormEvent,
  use,
  useEffect,
  useState,
} from "react";

import {
  useRouter,
} from "next/navigation";

import {
  atualizarAviso,
  buscarAvisoPorId,
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

type EditarAvisoPageProps = {
  params: Promise<{
    id: string;
  }>;
};

function paraDateTimeLocal(
  valor: string,
) {
  const data =
    new Date(valor);

  data.setMinutes(
    data.getMinutes() -
      data.getTimezoneOffset(),
  );

  return data
    .toISOString()
    .slice(0, 16);
}

export default function EditarAvisoPage({
  params,
}: EditarAvisoPageProps) {
  const { id } = use(params);

  const router = useRouter();

  const [
    viagens,
    setViagens,
  ] = useState<Viagem[]>([]);

  const [
    viagemId,
    setViagemId,
  ] = useState("");

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
    async function carregar() {
      try {
        setErro(null);

        const [
          dadosAviso,
          dadosViagens,
        ] =
          await Promise.all([
            buscarAvisoPorId(
              Number(id),
            ),
            listarViagens(),
          ]);

        setViagens(
          dadosViagens,
        );

        setViagemId(
          String(
            dadosAviso.viagemId,
          ),
        );

        setTitulo(
          dadosAviso.titulo,
        );

        setMensagem(
          dadosAviso.mensagem,
        );

        setTipo(
          dadosAviso.tipo,
        );

        setDataPublicacao(
          paraDateTimeLocal(
            dadosAviso.dataPublicacao,
          ),
        );

        setAtivo(
          dadosAviso.ativo,
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar o aviso.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregar();
  }, [id]);

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

      await atualizarAviso(
        Number(id),
        {
          titulo:
            titulo.trim(),

          mensagem:
            mensagem.trim(),

          tipo,

          dataPublicacao,

          ativo,

          viagemId:
            Number(viagemId),
        },
      );

      router.push(
        "/avisos",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar o aviso.",
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
        Carregando aviso...
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
          Editar aviso
        </h1>

        <p className="mt-1 text-sm text-muted">
          Atualize o comunicado exibido aos hóspedes.
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
              Edite o conteúdo e a categoria do aviso.
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
              className="w-full rounded-xl border border-border bg-background px-3 py-3 text-sm text-foreground outline-none focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-5 border-t border-border pt-6">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Publicação
            </h2>

            <p className="mt-1 text-sm text-muted">
              Atualize a data e controle se o aviso permanece disponível para o hóspede.
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
                Desative para ocultar o comunicado sem excluí-lo.
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
              ? "Salvando..."
              : "Salvar alterações"}
          </button>
        </div>
      </form>
    </div>
  );
}