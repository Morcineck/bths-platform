"use client";

import { FormEvent, useState } from "react";

import { KeyRound } from "lucide-react";

import { redefinirSenhaUsuario } from "../services/usuarioService";

type RedefinirSenhaFormProps = {
  usuarioId: string;
};

export function RedefinirSenhaForm({ usuarioId }: RedefinirSenhaFormProps) {
  const [novaSenha, setNovaSenha] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setErro("");
    setMensagem("");

    if (novaSenha.length < 8) {
      setErro("A nova senha deve possuir pelo menos 8 caracteres!");
      return;
    }

    try {
      setSalvando(true);

      await redefinirSenhaUsuario(usuarioId, novaSenha);

      setNovaSenha("");
      setMensagem("Senha redefinida. Informe a nova senha ao usuário.");
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : "Não foi possível redefinir a senha.",
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
      <p className="text-sm font-semibold text-foreground">Redefinir senha</p>
      <p className="mt-1 text-xs leading-5 text-muted">
        Defina uma senha temporária e passe para o usuário.
      </p>

      {erro && (
        <div
          role="alert"
          className="mt-4 rounded-2xl border border-danger/25 bg-danger/10 px-4 py-3 text-sm text-danger"
        >
          {erro}
        </div>
      )}

      {mensagem && (
        <div className="mt-4 rounded-2xl border border-success/25 bg-success/10 px-4 py-3 text-sm text-success">
          {mensagem}
        </div>
      )}

      <div className="mt-4 flex flex-col gap-2 sm:flex-row">
        <input
          type="password"
          autoComplete="new-password"
          placeholder="Nova senha (mínimo 8 caracteres)"
          value={novaSenha}
          onChange={(event) => setNovaSenha(event.target.value)}
          className="h-11 w-full rounded-xl border border-border bg-background/30 px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary"
        />

        <button
          type="submit"
          disabled={salvando}
          className="inline-flex h-11 shrink-0 items-center justify-center gap-2 rounded-xl border border-primary/30 px-5 text-sm font-medium text-primary transition-colors hover:bg-primary/5 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <KeyRound size={16} strokeWidth={1.9} />
          {salvando ? "Salvando..." : "Redefinir senha"}
        </button>
      </div>
    </form>
  );
}