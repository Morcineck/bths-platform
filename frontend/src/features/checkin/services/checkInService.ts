import type { CheckInResponse } from "../types/checkin";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

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