export type TipoQuarto =
  | "SUITE"
  | "ALOJAMENTO";

export type TipoCama =
  | "CASAL"
  | "BELICHE";

export type MeuQuarto = {
  quartoId: number;
  quartoNome: string;
  quartoTipo: TipoQuarto;
  tipoCama: TipoCama | null;
  capacidade: number;

  viagemId: number;
  viagemNome: string;
};