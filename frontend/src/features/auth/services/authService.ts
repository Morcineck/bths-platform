import { authFetch } from "./authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "./csrfService";

import type {
  LoginRequest,
  UsuarioAutenticado,
} from "../types/auth";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export class LoginBloqueadoError extends Error {
  readonly segundos: number | null;

  constructor(segundos: number | null) {
    super("Muitas tentativas de login.");
    this.name = "LoginBloqueadoError";
    this.segundos = segundos;
  }

  mensagemParaUsuario(): string {
    if (this.segundos === null) {
      return "Muitas tentativas de login. Aguarde alguns minutos e tente novamente.";
    }

    const minutos = Math.max(1, Math.ceil(this.segundos / 60));

    return `Muitas tentativas de login. Tente novamente em ${minutos} ${
      minutos === 1 ? "minuto" : "minutos"
    }.`;
  }
}

export async function login(
  dados: LoginRequest,
): Promise<void> {
  await inicializarCsrf();

  const csrfToken = buscarCsrfTokenDoCookie();

  const response = await fetch(`${API_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-XSRF-TOKEN": csrfToken,
    },
    credentials: "include",
    body: JSON.stringify(dados),
  });

  if (response.status === 429) {
    const retryAfter = Number(response.headers.get("Retry-After"));

    throw new LoginBloqueadoError(
      Number.isFinite(retryAfter) && retryAfter > 0 ? retryAfter : null,
    );
  }

  if (!response.ok) {
    throw new Error(
      "Não foi possível realizar o login.",
    );
  }
}

export async function buscarUsuarioAutenticado(): Promise<UsuarioAutenticado> {
  const response = await authFetch(
    `${API_URL}/api/auth/me`,
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o usuário autenticado.",
    );
  }

  return response.json();
}

export async function logout(): Promise<void> {
  await inicializarCsrf();

  const csrfToken =
    buscarCsrfTokenDoCookie();

  const response = await fetch(
    `${API_URL}/api/auth/logout`,
    {
      method: "POST",
      headers: {
        "X-XSRF-TOKEN": csrfToken,
      },
      credentials: "include",
    },
  );

  if (!response.ok) {
    throw new Error(
      "Não foi possível encerrar a sessão.",
    );
  }
}