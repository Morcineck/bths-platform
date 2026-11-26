import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  Hospedagem,
  HospedagemRequest,
} from "../types/hospedagem";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarHospedagens(): Promise<Hospedagem[]> {
  const response = await authFetch(
    `${API_URL}/api/hospedagens`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar as hospedagens.",
    );
  }

  return response.json();
}

export async function listarHospedagensPorViagem(
  viagemId: number,
): Promise<Hospedagem[]> {
  const response = await authFetch(
    `${API_URL}/api/hospedagens/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar as hospedagens da viagem.",
    );
  }

  return response.json();
}

export async function buscarHospedagemPorId(
  id: number,
): Promise<Hospedagem> {
  const response = await authFetch(
    `${API_URL}/api/hospedagens/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar a hospedagem.",
    );
  }

  return response.json();
}

export async function cadastrarHospedagem(
  dados: HospedagemRequest,
): Promise<Hospedagem> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedagens`,
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
      await response
        .json()
        .catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível cadastrar a hospedagem.",
    );
  }

  return response.json();
}

export async function atualizarHospedagem(
  id: number,
  dados: HospedagemRequest,
): Promise<Hospedagem> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedagens/${id}`,
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
        "Não foi possível atualizar a hospedagem.",
    );
  }

  return response.json();
}

export async function deletarHospedagem(
  id: number,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedagens/${id}`,
    {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    const erro =
      await response
        .json()
        .catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível excluir a hospedagem.",
    );
  }
}