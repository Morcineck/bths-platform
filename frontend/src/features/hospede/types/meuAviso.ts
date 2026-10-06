export type TipoMeuAviso =
  | "INFORMATIVO"
  | "IMPORTANTE"
  | "TRANSPORTE"
  | "HOSPEDAGEM"
  | "EVENTO";

export type MeuAviso = {
  id: number;
  titulo: string;
  mensagem: string;
  tipo: TipoMeuAviso;
  dataPublicacao: string;
};