export type TipoTraslado =
  | "AEROPORTO_PARA_HOSPEDAGEM"
  | "HOSPEDAGEM_PARA_AEROPORTO"
  | "OUTRO";

export type Aeroporto =
  | "GRU"
  | "CGH"
  | "VCP";

export type StatusTraslado =
  | "AGUARDANDO"
  | "EM_ANDAMENTO"
  | "CONCLUIDO"
  | "CANCELADO";

export type OperacaoTraslado = {
  id: number;

  viagemId: number;
  viagemNome: string;

  tipo: TipoTraslado;
  aeroporto: Aeroporto | null;
  status: StatusTraslado;

  dataHoraPrevista: string;

  localOrigem: string;
  localDestino: string;

  motoristaId: number | null;
  motoristaNome: string | null;

  veiculoId: number | null;
  veiculoModelo: string | null;
  veiculoPlaca: string | null;

  capacidadePassageiros: number | null;
  quantidadePassageiros: number;
  vagasDisponiveis: number | null;

  observacao: string | null;
};

export type OperacaoTrasladoRequest = {
  viagemId: number;

  tipo: TipoTraslado;
  aeroporto: Aeroporto | null;

  dataHoraPrevista: string;

  localOrigem: string;
  localDestino: string;

  motoristaId: number;
  veiculoId: number;

  observacao: string | null;
};

export type OperacaoTrasladoPassageiro = {
  trasladoId: number;
  hospedeId: number;
  hospedeNome: string;
};