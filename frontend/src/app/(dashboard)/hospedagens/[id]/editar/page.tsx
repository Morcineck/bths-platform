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
  ] = useState<number | null>(null);

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
            nome: nome.trim(),

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
      setErro(null);

      const response =
        await atualizarQuarto(
          quarto.id,
          {
            nome: quarto.nome,
            tipo: quarto.tipo,
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
              item.id === response.id
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
            nome: quarto.nome,
            tipo: quarto.tipo,
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
              item.id === response.id
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
          className="text-sm text-primary"
        >
          ← Voltar para hospedagens
        </Link>

        <p className="text-sm text-red-400">
          Hospedagem não encontrada.
        </p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl space-y-8">
      <div>
        <Link
          href="/hospedagens"
          className="text-sm text-muted transition-colors hover:text-foreground"
        >
          ← Voltar para hospedagens
        </Link>

        <h1 className="mt-4 text-2xl font-semibold text-foreground">
          Editar hospedagem
        </h1>

        <p className="mt-1 text-sm text-muted">
          {hospedagem.viagemNome}
        </p>
      </div>

      {erro && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {erro}
        </div>
      )}

      {sucesso && (
        <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">
          {sucesso}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        noValidate
        className="space-y-8 rounded-2xl border border-border bg-surface p-6"
      >
        <section className="space-y-5">
          <div>
            <h2 className="text-lg font-semibold text-foreground">
              Dados da hospedagem
            </h2>

            <p className="mt-1 text-sm text-muted">
              Informações exibidas no App Hóspede.
            </p>
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
              value={nome}
              onChange={(event) =>
                setNome(
                  event.target.value,
                )
              }
              required
              disabled={salvando}
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
            />
          </div>

          <div className="space-y-2">
            <label
              htmlFor="endereco"
              className="text-sm font-medium text-foreground"
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="cidade"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="estado"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm uppercase text-foreground outline-none focus:border-primary"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="localizacaoUrl"
              className="text-sm font-medium text-foreground"
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
            />
          </div>

          <div className="space-y-2">
            <label
              htmlFor="imagemUrl"
              className="text-sm font-medium text-foreground"
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
              className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="wifiNome"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="wifiSenha"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="horarioCheckIn"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="horarioCheckOut"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="contatoNome"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="contatoTelefone"
                className="text-sm font-medium text-foreground"
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
                className="h-11 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground outline-none focus:border-primary"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="observacaoPublica"
              className="text-sm font-medium text-foreground"
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
              rows={4}
              className="w-full resize-none rounded-xl border border-border bg-background px-3 py-2 text-sm text-foreground outline-none focus:border-primary"
            />
          </div>
        </section>

        <div className="flex justify-end border-t border-border pt-6">
          <button
            type="submit"
            disabled={salvando}
            className="rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {salvando
              ? "Salvando..."
              : "Salvar alterações"}
          </button>
        </div>
      </form>

      <section className="space-y-5 rounded-2xl border border-border bg-surface p-6">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Quartos desta hospedagem
          </h2>

          <p className="mt-1 text-sm text-muted">
            Vincule os quartos da viagem ao local onde eles
            realmente estão.
          </p>
        </div>

        {quartosDaHospedagem.length === 0 ? (
          <div className="rounded-xl border border-border bg-background/40 p-4">
            <p className="text-sm text-muted">
              Nenhum quarto vinculado a esta hospedagem.
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {quartosDaHospedagem.map(
              (quarto) => (
                <div
                  key={quarto.id}
                  className="flex flex-col gap-4 rounded-xl border border-border bg-background/40 p-4 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div>
                    <p className="font-medium text-foreground">
                      {quarto.nome}
                    </p>

                    <p className="mt-1 text-xs text-muted">
                      {quarto.tipo ===
                      "SUITE"
                        ? "Suíte"
                        : "Alojamento"}
                      {" • "}
                      {quarto.capacidade} hóspedes
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
                    className="text-sm font-medium text-red-400 transition-opacity hover:opacity-80 disabled:cursor-not-allowed disabled:opacity-50"
                  >
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

        <div className="border-t border-border pt-5">
          <h3 className="text-sm font-semibold text-foreground">
            Quartos disponíveis para vínculo
          </h3>

          <p className="mt-1 text-sm text-muted">
            São exibidos os quartos da mesma viagem que ainda
            não estão nesta hospedagem.
          </p>

          {quartosDisponiveis.length ===
          0 ? (
            <p className="mt-4 text-sm text-muted">
              Nenhum outro quarto disponível.
            </p>
          ) : (
            <div className="mt-4 space-y-3">
              {quartosDisponiveis.map(
                (quarto) => (
                  <div
                    key={quarto.id}
                    className="flex flex-col gap-4 rounded-xl border border-border bg-background/40 p-4 sm:flex-row sm:items-center sm:justify-between"
                  >
                    <div>
                      <p className="font-medium text-foreground">
                        {quarto.nome}
                      </p>

                      <p className="mt-1 text-xs text-muted">
                        {quarto.tipo ===
                        "SUITE"
                          ? "Suíte"
                          : "Alojamento"}

                        {quarto.hospedagemNome
                          ? ` • Atualmente em ${quarto.hospedagemNome}`
                          : " • Sem hospedagem"}
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
                      className="rounded-xl border border-primary/40 px-4 py-2 text-sm font-medium text-primary transition-colors hover:bg-primary/10 disabled:cursor-not-allowed disabled:opacity-50"
                    >
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