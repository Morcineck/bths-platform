export type MinhaViagem = {
  hospedeId: number;
  hospedeNome: string;

  viagemId: number;
  viagemNome: string;
  evento: string;

  dataInicio: string;
  dataFim: string;

  endereco: string;
  cidade: string;
  estado: string;

  status: string;

  quartoId: number | null;
  quartoNome: string | null;
};