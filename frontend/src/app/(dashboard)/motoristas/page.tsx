"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  CheckCircle2,
  Pencil,
  Phone,
  Plus,
  Power,
  Users,
  XCircle,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import {
  ativarMotorista,
  inativarMotorista,
  listarMotoristas,
} from "@/features/motorista/services/motoristaService";

import type { Motorista } from "@/features/motorista/types/motorista";

export default function MotoristasPage() {
  const [
    motoristas,
    setMotoristas,
  ] = useState<Motorista[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    acessoPermitido,
    setAcessoPermitido,
  ] = useState(false);

  const [
    erro,
    setErro,
  ] = useState("");

  const [
    processandoId,
    setProcessandoId,
  ] = useState<number | null>(
    null,
  );

  useEffect(() => {
    async function carregarPagina() {
      try {
        setErro("");

        const usuario =
          await buscarUsuarioAutenticado();

        if (
          usuario.perfil !==
          "ADMIN"
        ) {
          setAcessoPermitido(
            false,
          );

          return;
        }

        setAcessoPermitido(
          true,
        );

        const dados =
          await listarMotoristas();

        setMotoristas(
          dados,
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os motoristas.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarPagina();
  }, []);

  async function handleAlternarStatus(
    motorista: Motorista,
  ) {
    try {
      setProcessandoId(
        motorista.id,
      );

      setErro("");

      const atualizado =
        motorista.ativo
          ? await inativarMotorista(
              motorista.id,
            )
          : await ativarMotorista(
              motorista.id,
            );

      setMotoristas(
        (atuais) =>
          atuais.map(
            (item) =>
              item.id ===
              atualizado.id
                ? atualizado
                : item,
          ),
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível alterar o status do motorista.",
      );
    } finally {
      setProcessandoId(
        null,
      );
    }
  }

  function obterIniciais(
    nome: string,
  ) {
    return nome
      .trim()
      .split(/\s+/)
      .map((parte) => parte[0])
      .slice(0, 2)
      .join("")
      .toUpperCase();
  }

  function formatarTelefone(
    telefone?: string | null,
  ) {
    if (!telefone) {
      return "Telefone não informado";
    }

    const numeros =
      telefone.replace(/\D/g, "");

    if (
      numeros.length === 11
    ) {
      return numeros.replace(
        /(\d{2})(\d{5})(\d{4})/,
        "($1) $2-$3",
      );
    }

    if (
      numeros.length === 10
    ) {
      return numeros.replace(
        /(\d{2})(\d{4})(\d{4})/,
        "($1) $2-$3",
      );
    }

    return telefone;
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando motoristas...
      </p>
    );
  }

  if (!acessoPermitido) {
    return (
      <div className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger">
        Você não possui permissão para acessar motoristas.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Motoristas"
          description="Gerencie os motoristas disponíveis para a operação."
        />

        <Link
          href="/motoristas/novo"
          className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
        >
          <Plus
            size={18}
            strokeWidth={1.9}
          />

          Cadastrar motorista
        </Link>
      </div>

      {erro && (
        <div
          role="alert"
          className="rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {motoristas.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <Users
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhum motorista cadastrado
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Cadastre motoristas para associá-los às operações de traslado.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Motoristas cadastrados
              </h2>

              <p className="mt-1 text-xs text-muted">
                Profissionais disponíveis para a operação.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {motoristas.length}{" "}
              {motoristas.length ===
              1
                ? "motorista"
                : "motoristas"}
            </span>
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            {motoristas.map(
              (motorista) => (
                <article
                  key={motorista.id}
                  className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex min-w-0 items-start gap-3">
                      <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-sm font-semibold text-primary">
                        {obterIniciais(
                          motorista.nomeCompleto,
                        )}
                      </span>

                      <div className="min-w-0">
                        <p className="truncate text-sm font-semibold text-foreground">
                          {
                            motorista.nomeCompleto
                          }
                        </p>

                        <div className="mt-1.5 flex items-center gap-2 text-xs text-muted">
                          <Phone
                            size={14}
                            strokeWidth={1.9}
                            className="shrink-0"
                          />

                          <span className="truncate">
                            {formatarTelefone(
                              motorista.telefone,
                            )}
                          </span>
                        </div>
                      </div>
                    </div>

                    <span
                      className={
                        motorista.ativo
                          ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                          : "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                      }
                    >
                      {motorista.ativo ? (
                        <CheckCircle2
                          size={13}
                          strokeWidth={2}
                        />
                      ) : (
                        <XCircle
                          size={13}
                          strokeWidth={2}
                        />
                      )}

                      {motorista.ativo
                        ? "Ativo"
                        : "Inativo"}
                    </span>
                  </div>

                  {motorista.observacao && (
                    <div className="mt-3 rounded-xl border border-border bg-background/25 px-3 py-2.5">
                      <p className="text-[11px] text-muted">
                        Observação
                      </p>

                      <p className="mt-1 text-xs leading-5 text-foreground">
                        {
                          motorista.observacao
                        }
                      </p>
                    </div>
                  )}

                  <div className="mt-3 grid grid-cols-2 gap-2 border-t border-border pt-3">
                    <Link
                      href={`/motoristas/${motorista.id}/editar`}
                      className="inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-border px-3 text-xs font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
                    >
                      <Pencil
                        size={14}
                        strokeWidth={1.9}
                      />

                      Editar
                    </Link>

                    <button
                      type="button"
                      onClick={() =>
                        handleAlternarStatus(
                          motorista,
                        )
                      }
                      disabled={
                        processandoId ===
                        motorista.id
                      }
                      className={
                        motorista.ativo
                          ? "inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                          : "inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-success/30 px-3 text-xs font-medium text-success transition-colors hover:bg-success/5 disabled:cursor-not-allowed disabled:opacity-50"
                      }
                    >
                      <Power
                        size={14}
                        strokeWidth={1.9}
                      />

                      {processandoId ===
                      motorista.id
                        ? "Processando..."
                        : motorista.ativo
                          ? "Inativar"
                          : "Ativar"}
                    </button>
                  </div>
                </article>
              ),
            )}
          </div>
        </section>
      )}
    </div>
  );
}