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
  cadastrarHospedagem,
} from "@/features/hospedagem/services/hospedagemService";

import {
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  Viagem,
} from "@/features/viagem/types/viagem";

function normalizarOpcional(
  valor: string,
) {
  const texto = valor.trim();

  return texto === ""
    ? null
    : texto;
}

export default function NovaHospedagemPage() {
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
    carregandoViagens,
    setCarregandoViagens,
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

    try {
      setSalvando(true);
      setErro(null);

      await cadastrarHospedagem({
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
          Number(
            viagemId,
          ),
      });

      router.push(
        "/hospedagens",
      );

      router.refresh();
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar a hospedagem.",
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6">
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
          title="Nova hospedagem"
          description="Cadastre o local onde os hóspedes ficarão durante a viagem."
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
              Informações principais da hospedagem.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="viagem"
                className="mb-2 block text-xs font-medium text-muted"
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
                disabled={
                  salvando ||
                  carregandoViagens
                }
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
                className="mb-2 block text-xs font-medium text-muted"
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
                placeholder="Ex.: Villa Parateí — Beat House"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
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
              type="text"
              value={endereco}
              onChange={(event) =>
                setEndereco(
                  event.target.value,
                )
              }
              disabled={salvando}
              placeholder="Rua, número, bairro..."
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
                type="text"
                value={cidade}
                onChange={(event) =>
                  setCidade(
                    event.target.value,
                  )
                }
                disabled={salvando}
                placeholder="Ex.: Alumínio"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
                type="text"
                value={estado}
                onChange={(event) =>
                  setEstado(
                    event.target.value.toUpperCase(),
                  )
                }
                disabled={salvando}
                maxLength={2}
                placeholder="SP"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm uppercase text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
              placeholder="https://maps.google.com/..."
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
              placeholder="https://..."
              className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
                type="text"
                value={wifiNome}
                onChange={(event) =>
                  setWifiNome(
                    event.target.value,
                  )
                }
                disabled={salvando}
                placeholder="Nome da rede"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
                type="text"
                value={wifiSenha}
                onChange={(event) =>
                  setWifiSenha(
                    event.target.value,
                  )
                }
                disabled={salvando}
                placeholder="Senha da rede"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label
                htmlFor="horarioCheckIn"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Horário de check-in
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
                Horário de check-out
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
                type="text"
                value={contatoNome}
                onChange={(event) =>
                  setContatoNome(
                    event.target.value,
                  )
                }
                disabled={salvando}
                placeholder="Equipe Beat Trips"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div>
              <label
                htmlFor="contatoTelefone"
                className="mb-2 block text-xs font-medium text-muted"
              >
                Telefone do contato
              </label>

              <input
                id="contatoTelefone"
                type="text"
                value={contatoTelefone}
                onChange={(event) =>
                  setContatoTelefone(
                    event.target.value,
                  )
                }
                disabled={salvando}
                placeholder="(11) 99999-9999"
                className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
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
              placeholder="Informações importantes que podem ser exibidas ao hóspede..."
              className="w-full resize-none rounded-xl border border-border bg-background/30 px-4 py-3 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <div className="flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
          <Link
            href="/hospedagens"
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
              ? "Salvando..."
              : "Cadastrar hospedagem"}
          </button>
        </div>
      </form>
    </div>
  );
}