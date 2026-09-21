import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";
import type {
  CadastrarHospedeRequest,
  Hospede,
} from "../types/hospede";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarHospedesPorViagem(
  viagemId: number,
): Promise<Hospede[]> {
  const response = await fetch(
    `${API_URL}/api/hospedes/viagem/${viagemId}`,
    {
      credentials: "include",
    },
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
  const response = await fetch(
    `${API_URL}/api/hospedes/${id}`,
    {
      credentials: "include",
    },
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

  const csrfToken = buscarCsrfTokenDoCookie();

  const response = await fetch(
    `${API_URL}/api/hospedes`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      credentials: "include",
      body: JSON.stringify(dados),
    },
  );

  if (!response.ok) {
    const erro = await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível cadastrar o hóspede.",
    );
  }

  return response.json();
}