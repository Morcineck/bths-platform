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
          Number(viagemId),
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
    <div className="mx-auto max-w-4xl space-y-6 p-6">
      <div>
        <Link
          href="/hospedagens"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          ← Voltar para hospedagens
        </Link>

        <h1 className="mt-4 text-2xl font-semibold text-white">
          Nova hospedagem
        </h1>

        <p className="mt-1 text-sm text-zinc-400">
          Cadastre o local onde os hóspedes ficarão durante a viagem.
        </p>
      </div>

      {erro && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {erro}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="space-y-8 rounded-xl border border-zinc-800 bg-zinc-950 p-6"
      >
        <section className="space-y-5">
          <div>
            <h2 className="text-base font-semibold text-white">
              Identificação
            </h2>

            <p className="mt-1 text-sm text-zinc-500">
              Informações principais da hospedagem.
            </p>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="viagem"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
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
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>
        </section>

        <section className="space-y-5 border-t border-zinc-800 pt-6">
          <div>
            <h2 className="text-base font-semibold text-white">
              Localização
            </h2>

            <p className="mt-1 text-sm text-zinc-500">
              Endereço e link para localização da hospedagem.
            </p>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="endereco"
              className="text-sm font-medium text-zinc-200"
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
              className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="cidade"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="estado"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm uppercase text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="localizacaoUrl"
              className="text-sm font-medium text-zinc-200"
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
              className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <section className="space-y-5 border-t border-zinc-800 pt-6">
          <div>
            <h2 className="text-base font-semibold text-white">
              Experiência do hóspede
            </h2>

            <p className="mt-1 text-sm text-zinc-500">
              Dados exibidos no App Hóspede.
            </p>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="imagemUrl"
              className="text-sm font-medium text-zinc-200"
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
              className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="wifiNome"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="wifiSenha"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="horarioCheckIn"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="horarioCheckOut"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="space-y-2">
              <label
                htmlFor="contatoNome"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="space-y-2">
              <label
                htmlFor="contatoTelefone"
                className="text-sm font-medium text-zinc-200"
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
                className="h-11 w-full rounded-lg border border-zinc-800 bg-zinc-900 px-3 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label
              htmlFor="observacaoPublica"
              className="text-sm font-medium text-zinc-200"
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
              placeholder="Informações importantes que podem ser exibidas ao hóspede..."
              className="w-full resize-none rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-white outline-none transition placeholder:text-zinc-600 focus:border-zinc-600 disabled:cursor-not-allowed disabled:opacity-50"
            />
          </div>
        </section>

        <div className="flex flex-col-reverse gap-3 border-t border-zinc-800 pt-6 sm:flex-row sm:justify-end">
          <Link
            href="/hospedagens"
            className="inline-flex items-center justify-center rounded-lg border border-zinc-700 px-4 py-2 text-sm text-zinc-200 transition hover:bg-zinc-900"
          >
            Cancelar
          </Link>

          <button
            type="submit"
            disabled={
              salvando ||
              carregandoViagens
            }
            className="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2 text-sm font-medium text-black transition hover:bg-zinc-200 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {salvando
              ? "Salvando..."
              : "Cadastrar hospedagem"}
          </button>
        </div>
      </form>
    </div>
  );
}