import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Traslado,
  TrasladoOperacaoRequest,
  TrasladoUpdateRequest,
} from "../types/traslado";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarTrasladosPorHospede(
  hospedeId: number,
): Promise<Traslado[]> {
  const response = await authFetch(
    `${API_URL}/api/traslados/hospede/${hospedeId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os traslados do hóspede.",
    );
  }

  return response.json();
}

export async function associarOperacaoTraslado(
  trasladoId: number,
  dados: TrasladoOperacaoRequest,
): Promise<Traslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/${trasladoId}/operacao`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify(dados),
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível associar motorista e veículo ao traslado.",
    );
  }

  return response.json();
}

export async function atualizarTraslado(
  trasladoId: number,
  dados: TrasladoUpdateRequest,
): Promise<Traslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/${trasladoId}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify(dados),
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível atualizar o traslado.",
    );
  }

  return response.json();
}


