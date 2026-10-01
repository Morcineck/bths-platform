import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Quarto,
  QuartoOcupacao,
  QuartoRequest,
} from "../types/quarto";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarQuartosPorViagem(
  viagemId: number,
): Promise<Quarto[]> {
  const response = await authFetch(
    `${API_URL}/api/quartos/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os quartos.",
    );
  }

  return response.json();
}

export async function atualizarQuarto(
  quartoId: number,
  dados: QuartoRequest,
): Promise<Quarto> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/quartos/${quartoId}`,
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
      await response
        .json()
        .catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível atualizar o quarto.",
    );
  }

  return response.json();
}

export async function listarOcupacaoQuartosPorViagem(
  viagemId: number,
): Promise<QuartoOcupacao[]> {
  const response = await authFetch(
    `${API_URL}/api/quartos/viagem/${viagemId}/ocupacao`,
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível carregar a ocupação dos quartos.",
    );
  }

  return response.json();
}