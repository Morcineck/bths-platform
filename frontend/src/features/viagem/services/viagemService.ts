import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Viagem,
  ViagemRequest,
} from "../types/viagem";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarViagens(): Promise<Viagem[]> {
  const response = await authFetch(
    `${API_URL}/api/viagens`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar as viagens.",
    );
  }

  return response.json();
}

export async function buscarViagemPorId(
  id: number,
): Promise<Viagem> {
  const response = await authFetch(
    `${API_URL}/api/viagens/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar a viagem.",
    );
  }

  return response.json();
}

export async function cadastrarViagem(
  dados: ViagemRequest,
): Promise<Viagem> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/viagens`,
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
        "Não foi possível cadastrar a viagem.",
    );
  }

  return response.json();
}

export async function atualizarViagem(
  id: number,
  dados: ViagemRequest,
): Promise<Viagem> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/viagens/${id}`,
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
        "Não foi possível atualizar a viagem.",
    );
  }

  return response.json();
}

export async function excluirViagem(
  id: number,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/viagens/${id}`,
    {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível excluir a viagem. Verifique se ela tem hóspedes, quartos ou outros cadastros vinculados.",
    );
  }
}