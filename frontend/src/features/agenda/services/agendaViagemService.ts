import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  AgendaViagem,
  AgendaViagemRequest,
} from "../types/agendaViagem";

const API_URL =
  process.env.NEXT_PUBLIC_API_URL;

export async function listarAgendaPorViagem(
  viagemId: number,
): Promise<AgendaViagem[]> {
  const response = await authFetch(
    `${API_URL}/api/agenda-viagem/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar a agenda da viagem.",
    );
  }

  return response.json();
}

export async function buscarAgendaPorId(
  id: number,
): Promise<AgendaViagem> {
  const response = await authFetch(
    `${API_URL}/api/agenda-viagem/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o item da agenda.",
    );
  }

  return response.json();
}

export async function cadastrarAgenda(
  dados: AgendaViagemRequest,
): Promise<AgendaViagem> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/agenda-viagem`,
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
        "Não foi possível cadastrar o item da agenda.",
    );
  }

  return response.json();
}

export async function atualizarAgenda(
  id: number,
  dados: AgendaViagemRequest,
): Promise<AgendaViagem> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/agenda-viagem/${id}`,
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
        "Não foi possível atualizar o item da agenda.",
    );
  }

  return response.json();
}

export async function deletarAgenda(
  id: number,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/agenda-viagem/${id}`,
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
      "Não foi possível remover o item da agenda.",
    );
  }
}