import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Veiculo,
  VeiculoRequest,
} from "../types/veiculo";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarVeiculos(): Promise<Veiculo[]> {
  const response = await authFetch(
    `${API_URL}/api/veiculos`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os veículos.",
    );
  }

  return response.json();
}

export async function buscarVeiculoPorId(
  id: number,
): Promise<Veiculo> {
  const response = await authFetch(
    `${API_URL}/api/veiculos/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o veículo.",
    );
  }

  return response.json();
}

export async function cadastrarVeiculo(
  dados: VeiculoRequest,
): Promise<Veiculo> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/veiculos`,
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
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível cadastrar o veículo.",
    );
  }

  return response.json();
}

export async function atualizarVeiculo(
  id: number,
  dados: VeiculoRequest,
): Promise<Veiculo> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/veiculos/${id}`,
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
        "Não foi possível atualizar o veículo.",
    );
  }

  return response.json();
}

export async function inativarVeiculo(
  id: number,
): Promise<Veiculo> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/veiculos/${id}/inativar`,
    {
      method: "PATCH",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível inativar o veículo.",
    );
  }

  return response.json();
}

export async function ativarVeiculo(
  id: number,
): Promise<Veiculo> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/veiculos/${id}/ativar`,
    {
      method: "PATCH",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível ativar o veículo.",
    );
  }

  return response.json();
}