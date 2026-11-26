"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  CalendarDays,
  MapPin,
  MapPinned,
  Pencil,
  Plus,
  Trash2,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import {
  excluirViagem,
  listarViagens,
} from "@/features/viagem/services/viagemService";

import type {
  StatusViagem,
  Viagem,
} from "@/features/viagem/types/viagem";

import {
  formatarDataViagem,
  rotuloStatusViagem,
} from "@/features/viagem/utils/viagemFormatacao";

const CLASSES_STATUS: Record<StatusViagem, string> = {
  PLANEJADA:
    "border-primary/30 bg-primary/10 text-primary",
  EM_ANDAMENTO:
    "border-success/30 bg-success/10 text-success",
  FINALIZADA:
    "border-border bg-surface/50 text-muted",
  CANCELADA:
    "border-danger/30 bg-danger/10 text-danger",
};

export default function ViagensPage() {
  const [viagens, setViagens] = useState<Viagem[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [acessoPermitido, setAcessoPermitido] =
    useState(false);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [confirmandoId, setConfirmandoId] =
    useState<number | null>(null);
  const [excluindoId, setExcluindoId] =
    useState<number | null>(null);

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        if (usuario.perfil !== "ADMIN") {
          setAcessoPermitido(false);

          return;
        }

        setAcessoPermitido(true);

        setViagens(await listarViagens());
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar as viagens.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, []);

  async function handleExcluir(viagem: Viagem) {
    try {
      setExcluindoId(viagem.id);
      setErro("");
      setMensagem("");

      await excluirViagem(viagem.id);

      setViagens((atuais) =>
        atuais.filter((item) => item.id !== viagem.id),
      );

      setConfirmandoId(null);

      setMensagem("Viagem excluída com sucesso.");
    } catch (error) {
      setConfirmandoId(null);

      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível excluir a viagem.",
      );
    } finally {
      setExcluindoId(null);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando viagens...
      </p>
    );
  }

  if (!acessoPermitido) {
    return (
      <div className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger">
        Você não possui permissão para acessar viagens.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Viagens"
          description="Cadastre e mantenha as viagens da Beat Trips."
        />

        <Link
          href="/viagens/nova"
          className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
        >
          <Plus size={18} strokeWidth={1.9} />

          Nova viagem
        </Link>
      </div>

      {mensagem && (
        <div className="rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {mensagem}
        </div>
      )}

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {viagens.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <MapPinned size={19} strokeWidth={1.9} />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhuma viagem cadastrada
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Cadastre a primeira viagem para começar a registrar hóspedes, quartos e traslados.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          {viagens.map((viagem) => {
            const local = [viagem.cidade, viagem.estado]
              .filter(Boolean)
              .join(" / ");

            return (
              <article
                key={viagem.id}
                className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex min-w-0 items-start gap-3">
                    <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                      <MapPinned size={20} strokeWidth={1.9} />
                    </span>

                    <div className="min-w-0">
                      <p className="truncate text-sm font-semibold text-foreground">
                        {viagem.nome}
                      </p>

                      <p className="mt-1 truncate text-xs text-muted">
                        {viagem.evento}
                      </p>
                    </div>
                  </div>

                  <span
                    className={`inline-flex shrink-0 items-center rounded-full border px-2.5 py-1 text-[11px] font-medium ${CLASSES_STATUS[viagem.status]}`}
                  >
                    {rotuloStatusViagem(viagem.status)}
                  </span>
                </div>

                <div className="mt-3 space-y-2 rounded-xl border border-border bg-background/25 px-3 py-2.5">
                  <div className="flex items-center gap-2 text-xs text-foreground">
                    <CalendarDays
                      size={15}
                      strokeWidth={1.9}
                      className="text-primary"
                    />

                    {formatarDataViagem(viagem.dataInicio)} a{" "}
                    {formatarDataViagem(viagem.dataFim)}
                  </div>

                  {(local || viagem.endereco) && (
                    <div className="flex items-start gap-2 text-xs text-foreground">
                      <MapPin
                        size={15}
                        strokeWidth={1.9}
                        className="mt-0.5 shrink-0 text-primary"
                      />

                      <span>
                        {[viagem.endereco, local]
                          .filter(Boolean)
                          .join(" - ")}
                      </span>
                    </div>
                  )}
                </div>

                {confirmandoId === viagem.id ? (
                  <div className="mt-3 space-y-2 border-t border-border pt-3">
                    <p className="text-xs leading-5 text-danger">
                      Excluir a viagem {viagem.nome}? Essa ação não pode ser desfeita.
                    </p>

                    <div className="grid grid-cols-2 gap-2">
                      <button
                        type="button"
                        disabled={excluindoId === viagem.id}
                        onClick={() => handleExcluir(viagem)}
                        className="inline-flex h-9 items-center justify-center rounded-xl border border-danger/30 bg-danger/10 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/20 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        {excluindoId === viagem.id
                          ? "Excluindo..."
                          : "Confirmar exclusão"}
                      </button>

                      <button
                        type="button"
                        disabled={excluindoId === viagem.id}
                        onClick={() => setConfirmandoId(null)}
                        className="inline-flex h-9 items-center justify-center rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:bg-surface/50 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        Cancelar
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="mt-3 grid grid-cols-2 gap-2 border-t border-border pt-3">
                    <Link
                      href={`/viagens/${viagem.id}/editar`}
                      className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
                    >
                      <Pencil size={14} strokeWidth={1.9} />

                      Editar
                    </Link>

                    <button
                      type="button"
                      onClick={() => {
                        setErro("");
                        setMensagem("");
                        setConfirmandoId(viagem.id);
                      }}
                      className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5"
                    >
                      <Trash2 size={14} strokeWidth={1.9} />

                      Excluir
                    </button>
                  </div>
                )}
              </article>
            );
          })}
        </div>
      )}
    </div>
  );
}