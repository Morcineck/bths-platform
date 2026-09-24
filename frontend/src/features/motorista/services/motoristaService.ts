import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Motorista,
  MotoristaRequest,
} from "../types/motorista";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarMotoristas(): Promise<Motorista[]> {
  const response = await authFetch(
    `${API_URL}/api/motoristas`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os motoristas.",
    );
  }

  return response.json();
}

export async function buscarMotoristaPorId(
  id: number,
): Promise<Motorista> {
  const response = await authFetch(
    `${API_URL}/api/motoristas/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o motorista.",
    );
  }

  return response.json();
}

export async function cadastrarMotorista(
  dados: MotoristaRequest,
): Promise<Motorista> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/motoristas`,
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
        "Não foi possível cadastrar o motorista.",
    );
  }

  return response.json();
}

export async function atualizarMotorista(
  id: number,
  dados: MotoristaRequest,
): Promise<Motorista> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/motoristas/${id}`,
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
        "Não foi possível atualizar o motorista.",
    );
  }

  return response.json();
}

export async function inativarMotorista(
  id: number,
): Promise<Motorista> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/motoristas/${id}/inativar`,
    {
      method: "PATCH",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível inativar o motorista.",
    );
  }

  return response.json();
}

export async function ativarMotorista(
  id: number,
): Promise<Motorista> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/motoristas/${id}/ativar`,
    {
      method: "PATCH",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível ativar o motorista.",
    );
  }

  return response.json();
}