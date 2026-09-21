export type AlocacaoQuarto = {
  id: number;

  hospedeId: number;
  hospedeNome: string;

  quartoId: number;
  quartoNome: string;

  viagemId: number;
  viagemNome: string;

  dataAlocacao: string;
};

export type AlocacaoQuartoRequest = {
  hospedeId: number;
  quartoId: number;
};