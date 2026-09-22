import { authFetch } from "@/features/auth/services/authFetch";

import type { Traslado } from "../types/traslado";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarTrasladosPorHospede(
  hospedeId: number,
): Promise<Traslado[]> {
  const response = await authFetch(
    `${API_URL}/api/traslados/hospede/${hospedeId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os traslados do hóspede.",
    );
  }

  return response.json();
}