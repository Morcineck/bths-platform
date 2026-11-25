export type PerfilEquipe = "ADMIN" | "STAFF";

export type UsuarioEquipe = {
  id: string;
  nome: string;
  email: string;
  perfil: PerfilEquipe;
  ativo: boolean;
};

export type UsuarioCadastroRequest = {
  nome: string;
  email: string;
  senha: string;
  perfil: PerfilEquipe;
};

export type UsuarioAtualizarRequest = {
  nome: string;
  email: string;
  perfil: PerfilEquipe;
};