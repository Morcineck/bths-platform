export type Veiculo = {
  id: number;
  modelo: string;
  placa: string;
  capacidadePassageiros: number;
  observacao: string | null;
  ativo: boolean;
};

export type VeiculoRequest = {
  modelo: string;
  placa: string;
  capacidadePassageiros: number;
  observacao?: string | null;
};