export type MeuTraslado = {
  id: number;
  tipo: string;
  dataHoraPrevista: string;
  localOrigem: string;
  localDestino: string;
  aeroporto: string | null;
  numeroVoo: string | null;
  companhiaAerea: string | null;
  status: string;
};