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
  endereco?: string | null;
  cidade?: string | null;
  estado?: string | null;
  status: StatusViagem;
};

export type ViagemRequest = {
  nome: string;
  evento: string;
  dataInicio: string;
  dataFim: string;
  endereco?: string | null;
  cidade?: string | null;
  estado?: string | null;
  status: StatusViagem;
};