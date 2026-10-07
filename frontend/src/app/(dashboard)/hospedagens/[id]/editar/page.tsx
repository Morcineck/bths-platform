"use client";

import Link from "next/link";

import {
  FormEvent,
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  useParams,
  useRouter,
} from "next/navigation";

import {
  ArrowLeft,
  BedDouble,
  Link2,
  Save,
  Unlink,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  atualizarHospedagem,
  buscarHospedagemPorId,
} from "@/features/hospedagem/services/hospedagemService";

import type {
  Hospedagem,
} from "@/features/hospedagem/types/hospedagem";

import {
  atualizarQuarto,
  listarQuartosPorViagem,
} from "@/features/quarto/services/quartoService";

import type {
  Quarto,
} from "@/features/quarto/types/quarto";

function normalizarOpcional(
  valor: string,
) {
  const texto = valor.trim();

  return texto === ""
    ? null
    : texto;
}

export default function EditarHospedagemPage() {
  const params = useParams();
  const router = useRouter();

  const hospedagemId =
    Number(params.id);

  const [
    hospedagem,
    setHospedagem,
  ] = useState<Hospedagem | null>(
    null,
  );

  const [
    quartos,
    setQuartos,
  ] = useState<Quarto[]>([]);

  const [
    nome,
    setNome,
  ] = useState("");

  const [
    endereco,
    setEndereco,
  ] = useState("");

  const [
    cidade,
    setCidade,
  ] = useState("");

  const [
    estado,
    setEstado,
  ] = useState("");

  const [
    localizacaoUrl,
    setLocalizacaoUrl,
  ] = useState("");

  const [
    imagemUrl,
    setImagemUrl,
  ] = useState("");

  const [
    wifiNome,
    setWifiNome,
  ] = useState("");

  const [
    wifiSenha,
    setWifiSenha,
  ] = useState("");

  const [
    horarioCheckIn,
    setHorarioCheckIn,
  ] = useState("");

  const [
    horarioCheckOut,
    setHorarioCheckOut,
  ] = useState("");

  const [
    contatoNome,
    setContatoNome,
  ] = useState("");

  const [
    contatoTelefone,
    setContatoTelefone,
  ] = useState("");

  const [
    observacaoPublica,
    setObservacaoPublica,
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
    quartoAtualizandoId,
    setQuartoAtualizandoId,
  ] = useState<number | null>(
    null,
  );

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

        const hospedagemResponse =
          await buscarHospedagemPorId(
            hospedagemId,
          );

        setHospedagem(
          hospedagemResponse,
        );

        setNome(
          hospedagemResponse.nome,
        );

        setEndereco(
          hospedagemResponse.endereco ??
            "",
        );

        setCidade(
          hospedagemResponse.cidade ??
            "",
        );

        setEstado(
          hospedagemResponse.estado ??
            "",
        );

        setLocalizacaoUrl(
          hospedagemResponse.localizacaoUrl ??
            "",
        );

        setImagemUrl(
          hospedagemResponse.imagemUrl ??
            "",
        );

        setWifiNome(
          hospedagemResponse.wifiNome ??
            "",
        );

        setWifiSenha(
          hospedagemResponse.wifiSenha ??
            "",
        );

        setHorarioCheckIn(
          hospedagemResponse.horarioCheckIn
            ? hospedagemResponse.horarioCheckIn.slice(
                0,
                5,
              )
            : "",
        );

        setHorarioCheckOut(
          hospedagemResponse.horarioCheckOut
            ? hospedagemResponse.horarioCheckOut.slice(
                0,
                5,
              )
            : "",
        );

        setContatoNome(
          hospedagemResponse.contatoNome ??
            "",
        );

        setContatoTelefone(
          hospedagemResponse.contatoTelefone ??
            "",
        );

        setObservacaoPublica(
          hospedagemResponse.observacaoPublica ??
            "",
        );

        const quartosResponse =
          await listarQuartosPorViagem(
            hospedagemResponse.viagemId,
          );

        setQuartos(
          quartosResponse,
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar a hospedagem.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarDados();
  }, [hospedagemId]);

  const quartosDaHospedagem =
    useMemo(
      () =>
        quartos.filter(
          (quarto) =>
            quarto.hospedagemId ===
            hospedagemId,
        ),
      [
        quartos,
        hospedagemId,
      ],
    );

  const quartosDisponiveis =
    useMemo(
      () =>
        quartos.filter(
          (quarto) =>
            quarto.hospedagemId ===
              null ||
            quarto.hospedagemId !==
              hospedagemId,
        ),
      [
        quartos,
        hospedagemId,
      ],
    );

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (!hospedagem) {
      return;
    }

    try {
      setSalvando(true);
      setErro(null);
      setSucesso(null);

      const response =
        await atualizarHospedagem(
          hospedagem.id,
          {
            nome:
              nome.trim(),

            endereco:
              normalizarOpcional(
                endereco,
              ),

            cidade:
              normalizarOpcional(
                cidade,
              ),

            estado:
              normalizarOpcional(
                estado,
              ),

            localizacaoUrl:
              normalizarOpcional(
                localizacaoUrl,
              ),

            imagemUrl:
              normalizarOpcional(
                imagemUrl,
              ),

            wifiNome:
              normalizarOpcional(
                wifiNome,
              ),

            wifiSenha:
              normalizarOpcional(
                wifiSenha,
              ),

            horarioCheckIn:
              normalizarOpcional(
                horarioCheckIn,
              ),

            horarioCheckOut:
              normalizarOpcional(
                horarioCheckOut,
              ),

            contatoNome:
              normalizarOpcional(
                contatoNome,
              ),

            contatoTelefone:
              normalizarOpcional(
                contatoTelefone,
              ),

            observacaoPublica:
              normalizarOpcional(
                observacaoPublica,
              ),

            viagemId:
              hospedagem.viagemId,
          },
        );

      setHospedagem(
        response,
      );

      setSucesso(
        "Hospedagem atualizada com sucesso.",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível atualizar a hospedagem.",
      );
    } finally {
      setSalvando(false);
    }
  }

  async function vincularQuarto(
    quarto: Quarto,
  ) {
    try {
      setQuartoAtualizandoId(
        quarto.id,
      );

      setErro(null);
      setSucesso(null);

      const response =
        await atualizarQuarto(
          quarto.id,
          {
            nome:
              quarto.nome,

            tipo:
              quarto.tipo,

            capacidade:
              quarto.capacidade,

            status:
              quarto.status,

            viagemId:
              quarto.viagemId,

            hospedagemId:
              hospedagemId,
          },
        );

      setQuartos(
        (atuais) =>
          atuais.map(
            (item) =>
              item.id ===
              response.id
                ? response
                : item,
          ),
      );

      setSucesso(
        "Quarto vinculado com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível vincular o quarto.",
      );
    } finally {
      setQuartoAtualizandoId(
        null,
      );
    }
  }

  async function removerVinculoQuarto(
    quarto: Quarto,
  ) {
    try {
      setQuartoAtualizandoId(
        quarto.id,
      );

      setErro(null);
      setSucesso(null);

      const response =
        await atualizarQuarto(
          quarto.id,
          {
            nome:
              quarto.nome,

            tipo:
              quarto.tipo,

            capacidade:
              quarto.capacidade,

            status:
              quarto.status,

            viagemId:
              quarto.viagemId,

            hospedagemId:
              null,
          },
        );

      setQuartos(
        (atuais) =>
          atuais.map(
            (item) =>
              item.id ===
              response.id
                ? response
                : item,
          ),
      );

      setSucesso(
        "Vínculo do quarto removido com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível remover o vínculo do quarto.",
      );
    } finally {
      setQuartoAtualizandoId(
        null,
      );
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando hospedagem...
      </p>
    );
  }

  if (!hospedagem) {
    return (
      <div className="space-y-4">
        <Link
          href="/hospedagens"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para hospedagens
        </Link>

        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          Hospedagem não encontrada.
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6">
      <div className="space-y-4">
        <Link
          href="/hospedagens"
          className="inline-flex items-center gap-2 text-xs font-medium text-muted transition-colors hover:text-primary"
        >
          <ArrowLeft
            size={15}
            strokeWidth={1.9}
          />

          Voltar para hospedagens
        </Link>

        <PageHeader
          title="Editar hospedagem"
          description={hospedagem.viagemNome}
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
        noValidate
        className="space-y-6 rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
      >
        <section className="space-y-4">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Identificação
            </h2>

            <p className="mt-1 text-xs text-muted">
              Informações principais da hospedagem.
            </p>
          </div>

          <div>
            <label
              htmlFor="nome"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Nome
            </label>

            <input
              id="nome"
              value={nome}
              onChange={(event) =>
                setNome(
                  event.target.value,
                )
              }
              required
              disabled={salvando}
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Localização
            </h2>

            <p className="mt-1 text-xs text-muted">
              Endereço e localização da hospedagem.
            </p>
          </div>

          <div>
            <label
              htmlFor="endereco"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Endereço
            </label>

            <input
              id="endereco"
              value={endereco}
              onChange={(event) =>
                setEndereco(
                  event.target.value,
                )
              }
              disabled={salvando}
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="cidade"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Cidade
              </label>

              <input
                id="cidade"
                value={cidade}
                onChange={(event) =>
                  setCidade(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div>
              <label
                htmlFor="estado"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Estado
              </label>

              <input
                id="estado"
                value={estado}
                onChange={(event) =>
                  setEstado(
                    event.target.value.toUpperCase(),
                  )
                }
                maxLength={2}
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm uppercase text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="localizacaoUrl"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Link da localização
            </label>

            <input
              id="localizacaoUrl"
              type="url"
              value={localizacaoUrl}
              onChange={(event) =>
                setLocalizacaoUrl(
                  event.target.value,
                )
              }
              disabled={salvando}
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-4 border-t border-border pt-5">
          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Experiência do hóspede
            </h2>

            <p className="mt-1 text-xs text-muted">
              Dados exibidos no App Hóspede.
            </p>
          </div>

          <div>
            <label
              htmlFor="imagemUrl"
              className="mb-2 block text-xs font-medium text-muted"
            >
              URL da imagem
            </label>

            <input
              id="imagemUrl"
              type="url"
              value={imagemUrl}
              onChange={(event) =>
                setImagemUrl(
                  event.target.value,
                )
              }
              disabled={salvando}
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="wifiNome"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Rede Wi-Fi
              </label>

              <input
                id="wifiNome"
                value={wifiNome}
                onChange={(event) =>
                  setWifiNome(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div>
              <label
                htmlFor="wifiSenha"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Senha Wi-Fi
              </label>

              <input
                id="wifiSenha"
                value={wifiSenha}
                onChange={(event) =>
                  setWifiSenha(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="horarioCheckIn"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Check-in
              </label>

              <input
                id="horarioCheckIn"
                type="time"
                value={horarioCheckIn}
                onChange={(event) =>
                  setHorarioCheckIn(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div>
              <label
                htmlFor="horarioCheckOut"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Check-out
              </label>

              <input
                id="horarioCheckOut"
                type="time"
                value={horarioCheckOut}
                onChange={(event) =>
                  setHorarioCheckOut(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="contatoNome"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Nome do contato
              </label>

              <input
                id="contatoNome"
                value={contatoNome}
                onChange={(event) =>
                  setContatoNome(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div>
              <label
                htmlFor="contatoTelefone"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Telefone
              </label>

              <input
                id="contatoTelefone"
                value={contatoTelefone}
                onChange={(event) =>
                  setContatoTelefone(
                    event.target.value,
                  )
                }
                disabled={salvando}
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="observacaoPublica"
              className="mb-2 block text-xs font-medium text-muted"
            >
              Informação pública
            </label>

            <textarea
              id="observacaoPublica"
              value={observacaoPublica}
              onChange={(event) =>
                setObservacaoPublica(
                  event.target.value,
                )
              }
              disabled={salvando}
              rows={3}
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <div className="flex justify-end border-t border-border pt-4">
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

      <section className="space-y-4 rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5">
        <div className="flex items-start gap-3">
          <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
            <BedDouble
              size={19}
              strokeWidth={1.9}
            />
          </span>

          <div>
            <h2 className="text-sm font-semibold text-foreground">
              Quartos desta hospedagem
            </h2>

            <p className="mt-1 text-xs leading-5 text-muted">
              Gerencie os quartos vinculados a este local de hospedagem.
            </p>
          </div>
        </div>

        {quartosDaHospedagem.length ===
        0 ? (
          <div className="rounded-xl border border-border bg-background/25 px-3 py-3">
            <p className="text-xs text-muted">
              Nenhum quarto vinculado a esta hospedagem.
            </p>
          </div>
        ) : (
          <div className="space-y-2">
            {quartosDaHospedagem.map(
              (quarto) => (
                <div
                  key={quarto.id}
                  className="flex flex-col gap-3 rounded-xl border border-border bg-background/25 p-3 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div className="min-w-0">
                    <p className="truncate text-sm font-medium text-foreground">
                      {quarto.nome}
                    </p>

                    <p className="mt-1 text-[11px] text-muted">
                      {quarto.tipo ===
                      "SUITE"
                        ? "Suíte"
                        : "Alojamento"}
                      {" · "}
                      {quarto.capacidade}{" "}
                      {quarto.capacidade ===
                      1
                        ? "hóspede"
                        : "hóspedes"}
                    </p>
                  </div>

                  <button
                    type="button"
                    disabled={
                      quartoAtualizandoId ===
                      quarto.id
                    }
                    onClick={() =>
                      removerVinculoQuarto(
                        quarto,
                      )
                    }
                    className="inline-flex h-9 shrink-0 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    <Unlink
                      size={14}
                      strokeWidth={1.9}
                    />

                    {quartoAtualizandoId ===
                    quarto.id
                      ? "Atualizando..."
                      : "Remover vínculo"}
                  </button>
                </div>
              ),
            )}
          </div>
        )}

        <div className="border-t border-border pt-4">
          <h3 className="text-sm font-semibold text-foreground">
            Quartos disponíveis
          </h3>

          <p className="mt-1 text-xs leading-5 text-muted">
            Quartos da mesma viagem que podem ser vinculados a esta hospedagem.
          </p>

          {quartosDisponiveis.length ===
          0 ? (
            <div className="mt-3 rounded-xl border border-border bg-background/25 px-3 py-3">
              <p className="text-xs text-muted">
                Nenhum outro quarto disponível.
              </p>
            </div>
          ) : (
            <div className="mt-3 space-y-2">
              {quartosDisponiveis.map(
                (quarto) => (
                  <div
                    key={quarto.id}
                    className="flex flex-col gap-3 rounded-xl border border-border bg-background/25 p-3 sm:flex-row sm:items-center sm:justify-between"
                  >
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-foreground">
                        {quarto.nome}
                      </p>

                      <p className="mt-1 text-[11px] leading-5 text-muted">
                        {quarto.tipo ===
                        "SUITE"
                          ? "Suíte"
                          : "Alojamento"}

                        {quarto.hospedagemNome
                          ? ` · Atualmente em ${quarto.hospedagemNome}`
                          : " · Sem hospedagem"}
                      </p>
                    </div>

                    <button
                      type="button"
                      disabled={
                        quartoAtualizandoId ===
                        quarto.id
                      }
                      onClick={() =>
                        vincularQuarto(
                          quarto,
                        )
                      }
                      className="inline-flex h-9 shrink-0 items-center justify-center gap-2 rounded-xl border border-primary/30 px-3 text-xs font-medium text-primary transition-colors hover:bg-primary/5 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      <Link2
                        size={14}
                        strokeWidth={1.9}
                      />

                      {quartoAtualizandoId ===
                      quarto.id
                        ? "Vinculando..."
                        : quarto.hospedagemId
                          ? "Mover para esta hospedagem"
                          : "Vincular"}
                    </button>
                  </div>
                ),
              )}
            </div>
          )}
        </div>
      </section>
    </div>
  );
}