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
  RedefinirSenhaAcessoBthsRequest,
} from "../types/hospede";

import type {
  MinhaViagem,
} from "../types/minhaViagem";

import type {
  MeuQuarto,
} from "../types/meuQuarto";

import type {
  MinhaHospedagem,
} from "../types/minhaHospedagem";

import type {
  MinhaTimeline,
} from "../types/minhaTimeline";

import type {
  MeuAviso,
} from "../types/meuAviso";

import type { MeuTraslado } from "../types/meuTraslado";
import type { MeuCheckIn } from "../types/meuCheckIn";

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

export async function redefinirSenhaAcessoBths(
  hospedeId: number,
  dados: RedefinirSenhaAcessoBthsRequest,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/hospedes/${hospedeId}/acesso-bths/senha`,
    {
      method: "PUT",
      headers: {
        "Content-Type":
          "application/json",
        "X-XSRF-TOKEN":
          csrfToken,
      },
      body: JSON.stringify(
        dados,
      ),
    },
  );

  if (!response.ok) {
    const erro =
      await response
        .json()
        .catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível redefinir a senha do acesso BTHS.",
    );
  }
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

export async function buscarMeuQuarto(): Promise<MeuQuarto | null> {
  const response = await authFetch(
    `${API_URL}/api/app/quarto`,
  );

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar seu quarto.",
    );
  }

  return response.json();
}

export async function buscarMinhaHospedagem(): Promise<MinhaHospedagem | null> {
  const response = await authFetch(
    `${API_URL}/api/app/hospedagem`,
  );

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar sua hospedagem.",
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

export async function buscarMeuCheckIn(): Promise<MeuCheckIn | null> {
  const response = await authFetch(
    `${API_URL}/api/app/check-in`,
  );

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar as informações de check-in.",
    );
  }

  return response.json();
}

export async function buscarMeuQrCode(): Promise<Blob | null> {
  const response = await authFetch(
    `${API_URL}/api/app/check-in/qr`,
  );

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o QR Code.",
    );
  }

  return response.blob();
}

export async function buscarMinhaTimeline(): Promise<MinhaTimeline[]> {
  const response = await authFetch(
    `${API_URL}/api/app/timeline`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar sua timeline.",
    );
  }

  return response.json();
}

export async function buscarMeusAvisos(): Promise<MeuAviso[]> {
  const response = await authFetch(
    `${API_URL}/api/app/avisos`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar seus avisos.",
    );
  }

  return response.json();
}

