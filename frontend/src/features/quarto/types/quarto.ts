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

  hospedagemId: number | null;
  hospedagemNome: string | null;
};

export type QuartoOcupacao = {
  quartoId: number;
  nome: string;
  tipo: TipoQuarto;
  status: StatusQuarto;
  capacidade: number;
  ocupacao: number;
  vagasDisponiveis: number;
};

export type QuartoRequest = {
  nome: string;
  tipo: TipoQuarto;
  capacidade: number;
  status: StatusQuarto;

  viagemId: number;
  hospedagemId: number | null;
};