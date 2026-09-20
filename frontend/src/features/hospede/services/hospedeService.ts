import type { Hospede } from "../types/hospede";

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