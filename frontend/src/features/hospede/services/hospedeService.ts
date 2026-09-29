import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  AtualizarHospedeRequest,
  CadastrarHospedeRequest,
  CriarAcessoBthsRequest,
  Hospede,
  HospedeAcessoBths,
} from "../types/hospede";

import type {
  MinhaViagem,
} from "../types/minhaViagem";

import type { MeuTraslado } from "../types/meuTraslado";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarHospedesPorViagem(
  viagemId: number,
): Promise<Hospede[]> {
  const response = await authFetch(
    `${API_URL}/api/hospedes/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os hóspedes.",
    );
  }

  return response.json();
}

export async function buscarHospedePorId(
  id: number,
): Promise<Hospede> {
  const response = await authFetch(
    `${API_URL}/api/hospedes/${id}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o hóspede.",
    );
  }

  return response.json();
}

export async function cadastrarHospede(
  dados: CadastrarHospedeRequest,
): Promise<Hospede> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedes`,
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
        "Não foi possível cadastrar o hóspede.",
    );
  }

  return response.json();
}

export async function atualizarHospede(
  hospedeId: number,
  dados: AtualizarHospedeRequest,
): Promise<Hospede> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedes/${hospedeId}`,
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
        "Não foi possível atualizar o hóspede.",
    );
  }

  return response.json();
}

export async function buscarAcessoBths(
  hospedeId: number,
): Promise<HospedeAcessoBths> {
  const response = await authFetch(
    `${API_URL}/api/hospedes/${hospedeId}/acesso-bths`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar a situação de acesso BTHS.",
    );
  }

  return response.json();
}

export async function criarAcessoBths(
  hospedeId: number,
  dados: CriarAcessoBthsRequest,
): Promise<HospedeAcessoBths> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedes/${hospedeId}/acesso-bths`,
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
        "Não foi possível criar o acesso BTHS.",
    );
  }

  return response.json();
}

export async function buscarHospedesDoUsuario(): Promise<Hospede[]> {
  const response = await authFetch(
    `${API_URL}/api/app/hospedes`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os dados do hóspede.",
    );
  }

  return response.json();
}

export async function buscarMinhaViagem(): Promise<MinhaViagem | null> {
  const response = await authFetch(
    `${API_URL}/api/app/viagem`,
  );

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar sua viagem.",
    );
  }

  return response.json();
}

export async function buscarMeusTraslados(): Promise<MeuTraslado[]> {
  const response = await authFetch(
    `${API_URL}/api/app/traslados`
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar seus traslados."
    );
  }

  return response.json();
}