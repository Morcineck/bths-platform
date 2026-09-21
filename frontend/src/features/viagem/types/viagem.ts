export type StatusViagem =
  | "PLANEJADA"
  | "EM_ANDAMENTO"
  | "FINALIZADA"
  | "CANCELADA";

export type Viagem = {
  id: number;
  nome: string;
  evento: string;
  dataInicio: string;
  dataFim: string;
  endereco: string;
  cidade: string;
  estado: string;
  status: StatusViagem;
};