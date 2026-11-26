"use client";

import { createContext, useContext } from "react";

import type { UsuarioAutenticado } from "../types/auth";

const UsuarioAutenticadoContext =
  createContext<UsuarioAutenticado | null>(null);

export const UsuarioAutenticadoProvider =
  UsuarioAutenticadoContext.Provider;

export function useIsAdmin(): boolean {
  return (
    useContext(UsuarioAutenticadoContext)?.perfil ===
    "ADMIN"
  );
}