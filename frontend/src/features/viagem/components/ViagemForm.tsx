"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";

import { Save } from "lucide-react";

import type {
  Viagem,
  ViagemRequest,
} from "../types/viagem";

import { OPCOES_STATUS_VIAGEM } from "../utils/viagemFormatacao";

type ViagemFormProps = {
  viagemInicial?: Viagem;
  textoBotao: string;
  textoSalvando: string;
  onSalvar: (dados: ViagemRequest) => Promise<void>;
};

const CLASSE_INPUT =
  "h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary";

const CLASSE_LABEL =
  "mb-2 block text-xs font-medium text-muted";

export function ViagemForm({
  viagemInicial,
  textoBotao,
  textoSalvando,
  onSalvar,
}: ViagemFormProps) {
  const [nome, setNome] = useState(
    viagemInicial?.nome ?? "",
  );
  const [evento, setEvento] = useState(
    viagemInicial?.evento ?? "",
  );
  const [dataInicio, setDataInicio] = useState(
    viagemInicial?.dataInicio ?? "",
  );
  const [dataFim, setDataFim] = useState(
    viagemInicial?.dataFim ?? "",
  );
  const [endereco, setEndereco] = useState(
    viagemInicial?.endereco ?? "",
  );
  const [cidade, setCidade] = useState(
    viagemInicial?.cidade ?? "",
  );
  const [estado, setEstado] = useState(
    viagemInicial?.estado ?? "",
  );
  const [status, setStatus] = useState<
    ViagemRequest["status"]
  >(viagemInicial?.status ?? "PLANEJADA");

  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (!nome.trim()) {
      setErro("Informe o nome da viagem.");
      return;
    }

    if (!evento.trim()) {
      setErro("Informe o evento.");
      return;
    }

    if (!dataInicio || !dataFim) {
      setErro("Informe a data inicial e a data final.");
      return;
    }

    if (dataFim < dataInicio) {
      setErro(
        "A data final não pode ser anterior à data inicial.",
      );
      return;
    }

    try {
      setSalvando(true);
      setErro("");

      await onSalvar({
        nome: nome.trim(),
        evento: evento.trim(),
        dataInicio,
        dataFim,
        endereco: endereco.trim() || undefined,
        cidade: cidade.trim() || undefined,
        estado: estado.trim().toUpperCase() || undefined,
        status,
      });
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível salvar a viagem.",
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
    >
      {erro && (
        <div
          role="alert"
          className="mb-5 rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      <div className="space-y-5">
        <div>
          <label htmlFor="nome" className={CLASSE_LABEL}>
            Nome da viagem
          </label>

          <input
            id="nome"
            type="text"
            value={nome}
            onChange={(event) => setNome(event.target.value)}
            placeholder="Ex.: Tomorrowland Brasil 2027"
            className={CLASSE_INPUT}
          />
        </div>

        <div>
          <label htmlFor="evento" className={CLASSE_LABEL}>
            Evento
          </label>

          <input
            id="evento"
            type="text"
            value={evento}
            onChange={(event) =>
              setEvento(event.target.value)
            }
            placeholder="Ex.: Tomorrowland Brasil"
            className={CLASSE_INPUT}
          />
        </div>

        <div className="grid gap-5 sm:grid-cols-2">
          <div>
            <label
              htmlFor="data-inicio"
              className={CLASSE_LABEL}
            >
              Data inicial
            </label>

            <input
              id="data-inicio"
              type="date"
              value={dataInicio}
              onChange={(event) =>
                setDataInicio(event.target.value)
              }
              className={CLASSE_INPUT}
            />
          </div>

          <div>
            <label
              htmlFor="data-fim"
              className={CLASSE_LABEL}
            >
              Data final
            </label>

            <input
              id="data-fim"
              type="date"
              value={dataFim}
              onChange={(event) =>
                setDataFim(event.target.value)
              }
              className={CLASSE_INPUT}
            />
          </div>
        </div>

        <div>
          <label htmlFor="endereco" className={CLASSE_LABEL}>
            Endereço
          </label>

          <input
            id="endereco"
            type="text"
            value={endereco}
            onChange={(event) =>
              setEndereco(event.target.value)
            }
            placeholder="Opcional"
            className={CLASSE_INPUT}
          />
        </div>

        <div className="grid gap-5 sm:grid-cols-[1fr_120px]">
          <div>
            <label htmlFor="cidade" className={CLASSE_LABEL}>
              Cidade
            </label>

            <input
              id="cidade"
              type="text"
              value={cidade}
              onChange={(event) =>
                setCidade(event.target.value)
              }
              placeholder="Opcional"
              className={CLASSE_INPUT}
            />
          </div>

          <div>
            <label htmlFor="estado" className={CLASSE_LABEL}>
              Estado
            </label>

            <input
              id="estado"
              type="text"
              maxLength={2}
              value={estado}
              onChange={(event) =>
                setEstado(event.target.value)
              }
              placeholder="SP"
              className={CLASSE_INPUT}
            />
          </div>
        </div>

        <div>
          <label htmlFor="status" className={CLASSE_LABEL}>
            Status
          </label>

          <select
            id="status"
            value={status}
            onChange={(event) =>
              setStatus(
                event.target.value as ViagemRequest["status"],
              )
            }
            className={CLASSE_INPUT}
          >
            {OPCOES_STATUS_VIAGEM.map((opcao) => (
              <option key={opcao.valor} value={opcao.valor}>
                {opcao.rotulo}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="mt-5 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
        <Link
          href="/viagens"
          className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
        >
          Cancelar
        </Link>

        <button
          type="submit"
          disabled={salvando}
          className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-50"
        >
          <Save size={17} strokeWidth={1.9} />

          {salvando ? textoSalvando : textoBotao}
        </button>
      </div>
    </form>
  );
}