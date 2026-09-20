import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "./csrfService";

import type {
  LoginRequest,
  UsuarioAutenticado,
} from "../types/auth";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

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

  if (!response.ok) {
    throw new Error(
      "Não foi possível realizar o login.",
    );
  }
}

export async function buscarUsuarioAutenticado(): Promise<UsuarioAutenticado> {
  const response = await fetch(`${API_URL}/api/auth/me`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(
      "Não foi possível carregar o usuário autenticado.",
    );
  }

  return response.json();
}