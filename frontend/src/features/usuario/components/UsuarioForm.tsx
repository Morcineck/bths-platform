"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";

import { Save } from "lucide-react";

import type { PerfilEquipe, UsuarioEquipe } from "../types/usuario";

export type UsuarioFormDados = {
  nome: string;
  email: string;
  perfil: PerfilEquipe;
  senha: string;
};

type UsuarioFormProps = {
  usuarioInicial?: UsuarioEquipe;
  textoBotao: string;
  textoSalvando: string;
  onSalvar: (dados: UsuarioFormDados) => Promise<void>;
};

const CLASSE_INPUT =
  "h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary";

const CLASSE_LABEL = "mb-2 block text-xs font-medium text-muted";

export function UsuarioForm({
  usuarioInicial,
  textoBotao,
  textoSalvando,
  onSalvar,
}: UsuarioFormProps) {
  const ehCadastro = !usuarioInicial;

  const [nome, setNome] = useState(usuarioInicial?.nome ?? "");
  const [email, setEmail] = useState(usuarioInicial?.email ?? "");
  const [perfil, setPerfil] = useState<PerfilEquipe>(
    usuarioInicial?.perfil ?? "STAFF",
  );
  const [senha, setSenha] = useState("");

  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!nome.trim() || !email.trim()) {
      setErro("Informe o nome e o e-mail.");
      return;
    }

    if (ehCadastro && senha.length < 8) {
      setErro("A senha deve possuir pelo menos 8 caracteres!");
      return;
    }

    try {
      setSalvando(true);
      setErro("");

      await onSalvar({
        nome: nome.trim(),
        email: email.trim(),
        perfil,
        senha,
      });
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível salvar o usuário.",
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="rounded-2xl border border-border bg-surface/50 p-4 shadow-soft sm:p-5"
    >
      {erro && (
        <div
          role="alert"
          className="mb-5 rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      <div className="grid gap-4">
        <div>
          <label htmlFor="nome" className={CLASSE_LABEL}>
            Nome
          </label>
          <input
            id="nome"
            value={nome}
            onChange={(event) => setNome(event.target.value)}
            className={CLASSE_INPUT}
          />
        </div>

        <div>
          <label htmlFor="email" className={CLASSE_LABEL}>
            E-mail
          </label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            className={CLASSE_INPUT}
          />
        </div>

        <div>
          <label htmlFor="perfil" className={CLASSE_LABEL}>
            Perfil
          </label>
          <select
            id="perfil"
            value={perfil}
            onChange={(event) => setPerfil(event.target.value as PerfilEquipe)}
            className={CLASSE_INPUT}
          >
            <option value="STAFF">STAFF</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>

        {ehCadastro && (
          <div>
            <label htmlFor="senha" className={CLASSE_LABEL}>
              Senha inicial (mínimo 8 caracteres)
            </label>
            <input
              id="senha"
              type="password"
              autoComplete="new-password"
              value={senha}
              onChange={(event) => setSenha(event.target.value)}
              className={CLASSE_INPUT}
            />
          </div>
        )}
      </div>

      <div className="mt-5 flex flex-col-reverse gap-2 border-t border-border pt-4 sm:flex-row sm:justify-end">
        <Link
          href="/usuarios"
          className="inline-flex h-11 items-center justify-center rounded-xl border border-border px-5 text-sm font-medium text-muted transition-colors hover:border-primary/30 hover:bg-primary/5 hover:text-primary"
        >
          Cancelar
        </Link>

        <button
          type="submit"
          disabled={salvando}
          className="gradient-brand inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-semibold text-white transition-all hover:opacity-90 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-50"
        >
          <Save size={17} strokeWidth={1.9} />
          {salvando ? textoSalvando : textoBotao}
        </button>
      </div>
    </form>
  );
}