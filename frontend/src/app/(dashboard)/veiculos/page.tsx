"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import {
  Car,
  CheckCircle2,
  Pencil,
  Plus,
  Power,
  Users,
  XCircle,
} from "lucide-react";

import { PageHeader } from "@/components/layout/PageHeader";

import {
  ativarVeiculo,
  inativarVeiculo,
  listarVeiculos,
} from "@/features/veiculo/services/veiculoService";

import type {
  Veiculo,
} from "@/features/veiculo/types/veiculo";

export default function VeiculosPage() {
  const [
    veiculos,
    setVeiculos,
  ] = useState<Veiculo[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState<string | null>(
    null,
  );

  const [
    mensagem,
    setMensagem,
  ] = useState<string | null>(
    null,
  );

  const [
    alterandoId,
    setAlterandoId,
  ] = useState<number | null>(
    null,
  );

  useEffect(() => {
    async function carregar() {
      try {
        setCarregando(true);
        setErro(null);

        const dados =
          await listarVeiculos();

        setVeiculos(
          dados,
        );
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os veículos.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, []);

  async function alterarStatus(
    veiculo: Veiculo,
  ) {
    try {
      setAlterandoId(
        veiculo.id,
      );

      setErro(null);
      setMensagem(null);

      const atualizado =
        veiculo.ativo
          ? await inativarVeiculo(
              veiculo.id,
            )
          : await ativarVeiculo(
              veiculo.id,
            );

      setVeiculos(
        (atuais) =>
          atuais.map(
            (item) =>
              item.id ===
              atualizado.id
                ? atualizado
                : item,
          ),
      );

      setMensagem(
        atualizado.ativo
          ? "Veículo ativado com sucesso."
          : "Veículo inativado com sucesso.",
      );
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível alterar o status do veículo.",
      );
    } finally {
      setAlterandoId(
        null,
      );
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando veículos...
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <PageHeader
          title="Veículos"
          description="Gerencie os veículos disponíveis para a operação de transporte."
        />

        <Link
          href="/veiculos/novo"
          className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99]"
        >
          <Plus
            size={18}
            strokeWidth={1.9}
          />

          Novo veículo
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

      {veiculos.length === 0 ? (
        <div className="rounded-2xl border border-border bg-surface/40 p-5 shadow-soft">
          <div className="flex items-start gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-surface-secondary text-muted">
              <Car
                size={19}
                strokeWidth={1.9}
              />
            </span>

            <div>
              <p className="text-sm font-medium text-foreground">
                Nenhum veículo cadastrado
              </p>

              <p className="mt-1 text-xs leading-5 text-muted">
                Cadastre veículos para utilizá-los nas operações de traslado.
              </p>
            </div>
          </div>
        </div>
      ) : (
        <section className="space-y-3">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Veículos cadastrados
              </h2>

              <p className="mt-1 text-xs text-muted">
                Frota disponível para a operação.
              </p>
            </div>

            <span className="rounded-full border border-border bg-surface/50 px-3 py-1 text-xs font-medium text-muted">
              {veiculos.length}{" "}
              {veiculos.length === 1
                ? "veículo"
                : "veículos"}
            </span>
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            {veiculos.map(
              (veiculo) => (
                <article
                  key={veiculo.id}
                  className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex min-w-0 items-start gap-3">
                      <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/15 text-primary">
                        <Car
                          size={20}
                          strokeWidth={1.9}
                        />
                      </span>

                      <div className="min-w-0">
                        <p className="truncate text-sm font-semibold text-foreground">
                          {veiculo.modelo}
                        </p>

                        <p className="mt-1.5 text-xs font-medium tracking-wide text-muted">
                          {veiculo.placa}
                        </p>
                      </div>
                    </div>

                    <span
                      className={
                        veiculo.ativo
                          ? "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-success/30 bg-success/10 px-2.5 py-1 text-[11px] font-medium text-success"
                          : "inline-flex shrink-0 items-center gap-1.5 rounded-full border border-danger/30 bg-danger/10 px-2.5 py-1 text-[11px] font-medium text-danger"
                      }
                    >
                      {veiculo.ativo ? (
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

                      {veiculo.ativo
                        ? "Ativo"
                        : "Inativo"}
                    </span>
                  </div>

                  <div className="mt-3 rounded-xl border border-border bg-background/25 px-3 py-2.5">
                    <div className="flex items-center gap-2">
                      <Users
                        size={15}
                        strokeWidth={1.9}
                        className="text-primary"
                      />

                      <p className="text-[11px] text-muted">
                        Capacidade
                      </p>
                    </div>

                    <p className="mt-1.5 text-sm font-semibold text-foreground">
                      {
                        veiculo.capacidadePassageiros
                      }{" "}
                      {veiculo.capacidadePassageiros ===
                      1
                        ? "passageiro"
                        : "passageiros"}
                    </p>
                  </div>

                  {veiculo.observacao && (
                    <div className="mt-3 rounded-xl border border-border bg-background/25 px-3 py-2.5">
                      <p className="text-[11px] text-muted">
                        Observação
                      </p>

                      <p className="mt-1 text-xs leading-5 text-foreground">
                        {
                          veiculo.observacao
                        }
                      </p>
                    </div>
                  )}

                  <div className="mt-3 grid grid-cols-2 gap-2 border-t border-border pt-3">
                    <Link
                      href={`/veiculos/${veiculo.id}/editar`}
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
                      disabled={
                        alterandoId ===
                        veiculo.id
                      }
                      onClick={() =>
                        alterarStatus(
                          veiculo,
                        )
                      }
                      className={
                        veiculo.ativo
                          ? "inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-danger/30 px-3 text-xs font-medium text-danger transition-colors hover:bg-danger/5 disabled:cursor-not-allowed disabled:opacity-50"
                          : "inline-flex h-9 items-center justify-center gap-2 rounded-xl border border-success/30 px-3 text-xs font-medium text-success transition-colors hover:bg-success/5 disabled:cursor-not-allowed disabled:opacity-50"
                      }
                    >
                      <Power
                        size={14}
                        strokeWidth={1.9}
                      />

                      {alterandoId ===
                      veiculo.id
                        ? "Salvando..."
                        : veiculo.ativo
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