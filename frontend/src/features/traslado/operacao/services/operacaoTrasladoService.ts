import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  OperacaoTraslado,
  OperacaoTrasladoPassageiro,
  OperacaoTrasladoRequest,
  StatusTraslado,
} from "@/features/traslado/operacao/types/operacaoTraslado";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function criarOperacaoTraslado(
  dados: OperacaoTrasladoRequest,
): Promise<OperacaoTraslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes`,
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
        "Não foi possível criar a operação de traslado.",
    );
  }

  return response.json();
}

export async function vincularTrasladoOperacao(
  operacaoId: number,
  trasladoId: number,
): Promise<OperacaoTraslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}/traslados/${trasladoId}`,
    {
      method: "PATCH",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível vincular o traslado à operação.",
    );
  }

  return response.json();
}

export async function alterarVeiculoOperacao(
  operacaoId: number,
  veiculoId: number,
): Promise<OperacaoTraslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}/veiculo/${veiculoId}`,
    {
      method: "PATCH",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível alterar o veículo da operação.",
    );
  }

  return response.json();
}

export async function listarOperacoesPorViagem(
  viagemId: number,
): Promise<OperacaoTraslado[]> {
  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/viagem/${viagemId}`,
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível carregar as operações de traslado.",
    );
  }

  return response.json();
}

export async function atualizarOperacaoTraslado(
  operacaoId: number,
  dados: Omit<
    OperacaoTrasladoRequest,
    "viagemId"
  >,
): Promise<OperacaoTraslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}`,
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
        "Não foi possível atualizar a operação de traslado.",
    );
  }

  return response.json();
}

export async function buscarOperacaoTrasladoPorId(
  operacaoId: number,
): Promise<OperacaoTraslado> {
  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}`,
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível carregar a operação de traslado.",
    );
  }

  return response.json();
}

export async function excluirOperacaoTraslado(
  operacaoId: number,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}`,
    {
      method: "DELETE",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível excluir a operação de traslado.",
    );
  }
}

export async function listarPassageirosDaOperacao(
  operacaoId: number,
): Promise<OperacaoTrasladoPassageiro[]> {
  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}/passageiros`,
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível carregar os passageiros da operação.",
    );
  }

  return response.json();
}

export async function atualizarStatusOperacao(
  operacaoId: number,
  status: StatusTraslado,
): Promise<OperacaoTraslado> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await authFetch(
    `${API_URL}/api/traslados/operacoes/${operacaoId}/status`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify({
        status,
      }),
    },
  );

  if (!response.ok) {
    const erro =
      await response.json().catch(() => null);

    throw new Error(
      erro?.mensagem ??
        "Não foi possível atualizar o status da operação.",
    );
  }

  return response.json();
}