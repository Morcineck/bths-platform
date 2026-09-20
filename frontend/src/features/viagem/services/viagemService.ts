import type { Viagem } from "../types/viagem";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function listarViagens(): Promise<Viagem[]> {
  const response = await fetch(`${API_URL}/api/viagens`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar as viagens.",
    );
  }

  return response.json();
}