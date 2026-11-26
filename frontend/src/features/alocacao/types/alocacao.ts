export type TipoCama =
  | "CASAL"
  | "BELICHE";

export type AlocacaoQuarto = {
  id: number;

  hospedeId: number;
  hospedeNome: string;

  quartoId: number;
  quartoNome: string;

  tipoCama: TipoCama | null;

  viagemId: number;
  viagemNome: string;

  dataAlocacao: string;
};

export type AlocacaoQuartoRequest = {
  hospedeId: number;
  quartoId: number;
  tipoCama: TipoCama;
};