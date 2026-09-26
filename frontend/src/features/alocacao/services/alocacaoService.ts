import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  AlocacaoQuarto,
  AlocacaoQuartoRequest,
} from "../types/alocacao";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function alocarHospedeEmQuarto(
  dados: AlocacaoQuartoRequest,
): Promise<AlocacaoQuarto> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/alocacoes-quartos`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify(dados),
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível alocar o hóspede no quarto.",
    );
  }

  return response.json();
}

export async function buscarAlocacaoPorHospedeEViagem(
  hospedeId: number,
  viagemId: number,
): Promise<AlocacaoQuarto> {
  const response = await authFetch(
    `${API_URL}/api/alocacoes-quartos/hospede/${hospedeId}/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar a alocação do hóspede.",
    );
  }

  return response.json();
}

export async function listarAlocacoesPorQuarto(
  quartoId: number,
): Promise<AlocacaoQuarto[]> {
  const response = await authFetch(
    `${API_URL}/api/alocacoes-quartos/quarto/${quartoId}`,
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível carregar os hóspedes do quarto.",
    );
  }

  return response.json();
}

export async function trocarQuarto(
  alocacaoId: number,
  novoQuartoId: number,
): Promise<AlocacaoQuarto> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/alocacoes-quartos/${alocacaoId}/quarto`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify({
        novoQuartoId,
      }),
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível trocar o quarto do hóspede.",
    );
  }

  return response.json();
}