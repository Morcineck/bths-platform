export type PerfilUsuario = "ADMIN" | "STAFF" | "HOSPEDE";

export type LoginRequest = {
  email: string;
  senha: string;
};

export type UsuarioAutenticado = {
  id: string;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
};