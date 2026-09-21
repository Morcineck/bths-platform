const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function inicializarCsrf(): Promise<void> {
  const response = await fetch(`${API_URL}/api/auth/csrf`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(
      "Não foi possível obter o token de segurança.",
    );
  }
}

export function buscarCsrfTokenDoCookie(): string {
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