export type StatusCheckIn =
  | "PENDENTE"
  | "REALIZADO"
  | "NAO_COMPARECEU";

export type Hospede = {
  id: number;
  nomeCompleto: string;
  cpf: string;
  telefone: string;
  email: string;
  dataNascimento: string;
  horarioPrevistoChegada: string;
  statusCheckIn: StatusCheckIn;
  viagemId: number;
  viagemNome: string;
};

export type CadastrarHospedeRequest = {
  nomeCompleto: string;
  cpf: string;
  telefone?: string;
  email?: string;
  dataNascimento?: string;
  horarioPrevistoChegada?: string;
  statusCheckIn: "PENDENTE";
  viagemId: number;
};

export type AtualizarHospedeRequest = {
  nomeCompleto: string;
  cpf: string;
  telefone?: string;
  email?: string;
  dataNascimento?: string;
  horarioPrevistoChegada?: string;
  statusCheckIn:
    | "PENDENTE"
    | "REALIZADO"
    | "NAO_COMPARECEU";
  viagemId: number;
};