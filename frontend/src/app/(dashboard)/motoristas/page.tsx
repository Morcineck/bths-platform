"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";

import {
  ativarMotorista,
  inativarMotorista,
  listarMotoristas,
} from "@/features/motorista/services/motoristaService";

import type { Motorista } from "@/features/motorista/types/motorista";

export default function MotoristasPage() {
  const [motoristas, setMotoristas] = useState<Motorista[]>([]);

  const [carregando, setCarregando] = useState(true);

  const [acessoPermitido, setAcessoPermitido] =
    useState(false);

  const [erro, setErro] = useState("");

  const [processandoId, setProcessandoId] =
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

        const dados =
          await listarMotoristas();

        setMotoristas(dados);
      } catch (error) {
        setErro(
          error instanceof Error
            ? error.message
            : "Não foi possível carregar os motoristas.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, []);

  async function handleAlternarStatus(
    motorista: Motorista,
  ) {
    try {
      setProcessandoId(motorista.id);
      setErro("");

      const atualizado = motorista.ativo
        ? await inativarMotorista(motorista.id)
        : await ativarMotorista(motorista.id);

      setMotoristas((atuais) =>
        atuais.map((item) =>
          item.id === atualizado.id
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
      setProcessandoId(null);
    }
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
      <div className="space-y-4">
        <p className="text-sm text-red-400">
          Você não possui permissão para acessar motoristas.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <PageHeader
        title="Motoristas"
        description="Gerencie os motoristas disponíveis para a operação."
      />

      <div>
        <Link
          href="/motoristas/novo"
          className="inline-flex rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white"
        >
          Cadastrar motorista
        </Link>
      </div>

      {erro && (
        <p
          role="alert"
          className="text-sm text-red-400"
        >
          {erro}
        </p>
      )}

      {motoristas.length === 0 ? (
        <Card>
          <p className="text-sm text-muted">
            Nenhum motorista cadastrado.
          </p>
        </Card>
      ) : (
        <section className="space-y-3">
          {motoristas.map((motorista) => (
            <Card key={motorista.id}>
              <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                  <p className="font-semibold text-foreground">
                    {motorista.nomeCompleto}
                  </p>

                  <p className="mt-1 text-sm text-muted">
                    {motorista.telefone ||
                      "Telefone não informado"}
                  </p>

                  {motorista.observacao && (
                    <p className="mt-2 text-sm text-muted">
                      {motorista.observacao}
                    </p>
                  )}
                </div>

                <div className="flex flex-col gap-3 md:items-end">
                  <span
                    className={
                      motorista.ativo
                        ? "text-sm font-medium text-green-400"
                        : "text-sm font-medium text-red-400"
                    }
                  >
                    {motorista.ativo
                      ? "Ativo"
                      : "Inativo"}
                  </span>

                  <div className="flex flex-wrap gap-2">
                    <Link
                      href={`/motoristas/${motorista.id}/editar`}
                      className="rounded-xl border border-border px-4 py-2 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary"
                    >
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
                      className="rounded-xl border border-border px-4 py-2 text-sm font-medium text-foreground transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      {processandoId ===
                      motorista.id
                        ? "Processando..."
                        : motorista.ativo
                          ? "Inativar"
                          : "Ativar"}
                    </button>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </section>
      )}
    </div>
  );
}