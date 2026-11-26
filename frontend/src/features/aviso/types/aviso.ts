export type TipoAviso =
  | "INFORMATIVO"
  | "IMPORTANTE"
  | "TRANSPORTE"
  | "HOSPEDAGEM"
  | "EVENTO";

export type Aviso = {
  id: number;
  titulo: string;
  mensagem: string;
  tipo: TipoAviso;
  dataPublicacao: string;
  ativo: boolean;
  viagemId: number;
  viagemNome: string;
};

export type AvisoRequest = {
  titulo: string;
  mensagem: string;
  tipo: TipoAviso;
  dataPublicacao: string;
  ativo: boolean;
  viagemId: number;
};