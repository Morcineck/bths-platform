import { authFetch } from "@/features/auth/services/authFetch";

import type { Quarto } from "../types/quarto";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarQuartosPorViagem(
  viagemId: number,
): Promise<Quarto[]> {
  const response = await authFetch(
    `${API_URL}/api/quartos/viagem/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar os quartos.",
    );
  }

  return response.json();
}