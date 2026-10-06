import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Aviso,
  AvisoRequest,
} from "../types/aviso";

const API_URL =
  process.env.NEXT_PUBLIC_API_URL;

export async function listarAvisosPorViagem(
  viagemId: number,
): Promise<Aviso[]> {
  const response = await authFetch(
    `${API_URL}/api/avisos/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os avisos da viagem.",
    );
  }

  return response.json();
}

export async function buscarAvisoPorId(
  id: number,
): Promise<Aviso> {
  const response = await authFetch(
    `${API_URL}/api/avisos/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o aviso.",
    );
  }

  return response.json();
}

export async function cadastrarAviso(
  dados: AvisoRequest,
): Promise<Aviso> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/avisos`,
    {
      method: "POST",
      headers: {
        "Content-Type":
          "application/json",
        "X-XSRF-TOKEN":
          csrfToken,
      },
      body: JSON.stringify(
        dados,
      ),
    },
  );

  if (!response.ok) {
    const erro =
      await response
        .json()
        .catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível cadastrar o aviso.",
    );
  }

  return response.json();
}

export async function atualizarAviso(
  id: number,
  dados: AvisoRequest,
): Promise<Aviso> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/avisos/${id}`,
    {
      method: "PUT",
      headers: {
        "Content-Type":
          "application/json",
        "X-XSRF-TOKEN":
          csrfToken,
      },
      body: JSON.stringify(
        dados,
      ),
    },
  );

  if (!response.ok) {
    const erro =
      await response
        .json()
        .catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível atualizar o aviso.",
    );
  }

  return response.json();
}

export async function deletarAviso(
  id: number,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/avisos/${id}`,
    {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN":
          csrfToken,
      },
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível remover o aviso.",
    );
  }
}