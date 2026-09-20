export type TipoQuarto =
  | "SUITE"
  | "ALOJAMENTO";

export type StatusQuarto =
  | "DISPONIVEL"
  | "INDISPONIVEL";

export type Quarto = {
  id: number;
  nome: string;
  tipo: TipoQuarto;
  capacidade: number;
  status: StatusQuarto;

  viagemId: number;
  viagemNome: string;
};