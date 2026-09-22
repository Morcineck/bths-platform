import { authFetch } from "@/features/auth/services/authFetch";

import type { DashboardResponse } from "../types/dashboard";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function buscarDashboard(
  viagemId: number,
): Promise<DashboardResponse> {
  const response = await authFetch(
    `${API_URL}/api/dashboard/viagens/${viagemId}`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o dashboard.",
    );
  }

  return response.json();
}