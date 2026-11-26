import { authFetch } from "@/features/auth/services/authFetch";

import {
  buscarCsrfTokenDoCookie,
  inicializarCsrf,
} from "@/features/auth/services/csrfService";

import type {
  UsuarioAtualizarRequest,
  UsuarioCadastroRequest,
  UsuarioEquipe,
} from "../types/usuario";

const API_URL = process.env.NEXT_PUBLIC_API_URL;
const USUARIOS_URL = `${API_URL}/api/usuarios`;


async function lerErro(
  response: Response,
  mensagemPadrao: string,
): Promise<never> {
  const erro = await response.json().catch(() => null);

  throw new Error(erro?.mensagem ?? mensagemPadrao);
}

async function enviar(
  url: string,
  metodo: "POST" | "PUT" | "PATCH",
  corpo?: unknown,
): Promise<Response> {
  await inicializarCsrf();

  const csrfToken = buscarCsrfTokenDoCookie();

  return authFetch(url, {
    method: metodo,
    headers: {
      "Content-Type": "application/json",
      "X-XSRF-TOKEN": csrfToken,
    },
    body: corpo === undefined ? undefined : JSON.stringify(corpo),
  });
}


export async function listarUsuarios(): Promise<UsuarioEquipe[]> {
  const response = await authFetch(USUARIOS_URL);

  if (!response.ok) {
    return lerErro(response, "Não foi possível carregar os usuários.");
  }

  return response.json();
}

export async function buscarUsuarioPorId(id: string): Promise<UsuarioEquipe> {
  const response = await authFetch(`${USUARIOS_URL}/${id}`);

  if (!response.ok) {
    return lerErro(response, "Não foi possível carregar o usuário.");
  }

  return response.json();
}


export async function cadastrarUsuario(
  dados: UsuarioCadastroRequest,
): Promise<UsuarioEquipe> {
  const response = await enviar(USUARIOS_URL, "POST", dados);

  if (!response.ok) {
    return lerErro(response, "Não foi possível cadastrar o usuário.");
  }

  return response.json();
}

export async function atualizarUsuario(
  id: string,
  dados: UsuarioAtualizarRequest,
): Promise<UsuarioEquipe> {
  const response = await enviar(`${USUARIOS_URL}/${id}`, "PUT", dados);

  if (!response.ok) {
    return lerErro(response, "Não foi possível atualizar o usuário.");
  }

  return response.json();
}

export async function desativarUsuario(id: string): Promise<UsuarioEquipe> {
  const response = await enviar(`${USUARIOS_URL}/${id}/desativar`, "PATCH");

  if (!response.ok) {
    return lerErro(response, "Não foi possível desativar o usuário.");
  }

  return response.json();
}

export async function reativarUsuario(id: string): Promise<UsuarioEquipe> {
  const response = await enviar(`${USUARIOS_URL}/${id}/reativar`, "PATCH");

  if (!response.ok) {
    return lerErro(response, "Não foi possível reativar o usuário.");
  }

  return response.json();
}

export async function redefinirSenhaUsuario(
  id: string,
  novaSenha: string,
): Promise<void> {
  const response = await enviar(`${USUARIOS_URL}/${id}/senha`, "PUT", { novaSenha });

  if (!response.ok) {
    return lerErro(response, "Não foi possível redefinir a senha.");
  }
}

