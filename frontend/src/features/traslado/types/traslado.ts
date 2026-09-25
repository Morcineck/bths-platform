export type TipoTraslado =
  | "AEROPORTO_PARA_HOSPEDAGEM"
  | "HOSPEDAGEM_PARA_AEROPORTO"
  | "OUTRO";

export type StatusTraslado =
  | "AGUARDANDO"
  | "EM_ANDAMENTO"
  | "CONCLUIDO"
  | "CANCELADO";

export type Aeroporto =
  | "GRU"
  | "CGH"
  | "VCP";

export type Traslado = {
  id: number;

  hospedeId: number;
  hospedeNome: string;

  viagemId: number;
  viagemNome: string;

  operacaoTrasladoId: number | null;

  motoristaId: number | null;
  motoristaNome: string | null;

  veiculoId: number | null;
  veiculoModelo: string | null;
  veiculoPlaca: string | null;
  veiculoCapacidadePassageiros: number | null;

  tipo: TipoTraslado;
  aeroporto: Aeroporto | null;
  status: StatusTraslado;

  dataHoraPrevista: string;

  numeroVoo: string | null;
  companhiaAerea: string | null;

  localOrigem: string;
  localDestino: string;

  observacoes: string | null;
};

export type TrasladoOperacaoRequest = {
  motoristaId: number;
  veiculoId: number;
};

export type TrasladoUpdateRequest = {
  tipo: TipoTraslado;
  aeroporto: Aeroporto | null;

  dataHoraPrevista: string;

  numeroVoo: string | null;
  companhiaAerea: string | null;

  localOrigem: string;
  localDestino: string;

  observacoes: string | null;
};