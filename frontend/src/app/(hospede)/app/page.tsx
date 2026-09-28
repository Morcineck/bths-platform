"use client";

import { useEffect, useState } from "react";

import { Card } from "@/components/ui/Card";

import {
  buscarUsuarioAutenticado,
} from "@/features/auth/services/authService";

import type {
  UsuarioAutenticado,
} from "@/features/auth/types/auth";

import {
  buscarHospedesDoUsuario,
} from "@/features/hospede/services/hospedeService";

import type {
  Hospede,
} from "@/features/hospede/types/hospede";

export default function AppHospedePage() {
  const [
    usuario,
    setUsuario,
  ] = useState<UsuarioAutenticado | null>(
    null,
  );

  const [
    hospedes,
    setHospedes,
  ] = useState<Hospede[]>([]);

  const [
    carregando,
    setCarregando,
  ] = useState(true);

  const [
    erro,
    setErro,
  ] = useState("");

  useEffect(() => {
    async function carregarApp() {
      try {
        setErro("");

        const [
          usuarioAutenticado,
          hospedesDoUsuario,
        ] = await Promise.all([
          buscarUsuarioAutenticado(),
          buscarHospedesDoUsuario(),
        ]);

        setUsuario(
          usuarioAutenticado,
        );

        setHospedes(
          hospedesDoUsuario,
        );
      } catch {
        setErro(
          "Não foi possível carregar suas informações.",
        );
      } finally {
        setCarregando(
          false,
        );
      }
    }

    carregarApp();
  }, []);

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Carregando suas informações...
      </p>
    );
  }

  if (erro) {
    return (
      <p
        role="alert"
        className="text-sm text-red-400"
      >
        {erro}
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <p className="text-sm text-muted">
          Olá,
        </p>

        <h1 className="mt-1 text-2xl font-semibold text-foreground">
          {usuario?.nome}
        </h1>

        <p className="mt-2 text-sm text-muted">
          Bem-vindo ao seu espaço Beat Trips.
        </p>
      </div>

      {hospedes.length === 0 ? (
        <Card>
          <p className="font-medium text-foreground">
            Nenhuma viagem vinculada à sua conta.
          </p>

          <p className="mt-2 text-sm text-muted">
            Assim que sua hospedagem for vinculada ao acesso BTHS, ela aparecerá aqui.
          </p>
        </Card>
      ) : (
        <div className="space-y-4">
          {hospedes.map(
            (hospede) => (
              <Card
                key={hospede.id}
              >
                <p className="text-sm text-muted">
                  Sua viagem
                </p>

                <p className="mt-2 text-lg font-semibold text-foreground">
                  {hospede.viagemNome}
                </p>

                <p className="mt-3 text-sm text-muted">
                  Hóspede
                </p>

                <p className="mt-1 font-medium text-foreground">
                  {hospede.nomeCompleto}
                </p>
              </Card>
            ),
          )}
        </div>
      )}
    </div>
  );
}