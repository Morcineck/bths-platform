export class SessaoExpiradaError extends Error {
  constructor() {
    super("Sua sessão expirou. Faça login novamente.");
    this.name = "SessaoExpiradaError";
  }
}

export async function authFetch(
  input: RequestInfo | URL,
  init?: RequestInit,
): Promise<Response> {
  const response = await fetch(input, {
    ...init,
    credentials: "include",
  });

  if (response.status === 401) {
    if (typeof window !== "undefined") {
      window.location.replace(
        "/login?motivo=sessao-expirada",
      );
    }

    throw new SessaoExpiradaError();
  }

  return response;
}