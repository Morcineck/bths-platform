export type Motorista = {
  id: number;
  nomeCompleto: string;
  telefone?: string;
  observacao?: string;
  ativo: boolean;
};

export type MotoristaRequest = {
  nomeCompleto: string;
  telefone?: string;
  observacao?: string;
};