import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type { CheckInResponse } from "../types/checkin";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

type CheckInRequest = {
  observacao?: string;
};

export async function consultarCheckIn(
  hospedeId: number,
): Promise<CheckInResponse> {
  const response = await fetch(
    `${API_URL}/api/hospedes/${hospedeId}/check-in`,
    {
      credentials: "include",
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os dados de check-in.",
    );
  }

  return response.json();
}

export async function realizarCheckIn(
  hospedeId: number,
  dados: CheckInRequest,
): Promise<CheckInResponse> {
  await inicializarCsrf();

  const csrfToken = buscarCsrfTokenDoCookie();

  const response = await fetch(
    `${API_URL}/api/hospedes/${hospedeId}/check-in`,
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
      "Não foi possível realizar o check-in.",
    );
  }

  return response.json();
}