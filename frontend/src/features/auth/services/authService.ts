import type {
  LoginRequest,
  UsuarioAutenticado,
} from "../types/auth";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

async function inicializarCsrf(): Promise<void> {
  const response = await fetch(`${API_URL}/api/auth/csrf`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(
      "Não foi possível obter o token de segurança.",
    );
  }
}

function buscarCsrfTokenDoCookie(): string {
  const cookie = document.cookie
    .split("; ")
    .find((item) => item.startsWith("XSRF-TOKEN="));

  if (!cookie) {
    throw new Error(
      "Token de segurança não encontrado.",
    );
  }

  return decodeURIComponent(
    cookie.substring("XSRF-TOKEN=".length),
  );
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

  if (!response.ok) {
    throw new Error("Não foi possível realizar o login.");
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