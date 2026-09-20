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

  const csrfToken = buscarCsrfTokenDoCookie();

  const response = await fetch(
    `${API_URL}/api/alocacoes-quartos`,
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
    throw new Error(
      "Não foi possível alocar o hóspede no quarto.",
    );
  }

  return response.json();
}