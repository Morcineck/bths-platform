export type TipoTraslado =
  | "AEROPORTO_PARA_HOSPEDAGEM"
  | "HOSPEDAGEM_PARA_AEROPORTO"
  | "OUTRO";

export type DashboardHospedes = {
  total: number;
  presentes: number;
  pendentes: number;
  taxaCheckIn: number;
};

export type DashboardHospedagem = {
  vagasTotais: number;
  ocupadas: number;
  disponiveis: number;
};

export type DashboardProximoTraslado = {
  id: number;
  hospedeNome: string;
  tipo: TipoTraslado;
  dataHoraPrevista: string;
  localOrigem: string;
  localDestino: string;
};

export type DashboardTraslados = {
  aguardando: number;
  emAndamento: number;
  concluidos: number;
  proximos: DashboardProximoTraslado[];
};

export type DashboardResponse = {
  viagemId: number;
  hospedes: DashboardHospedes;
  hospedagem: DashboardHospedagem;
  traslados: DashboardTraslados;
};