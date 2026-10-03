export type TipoAgendaViagem =
  | "CHEGADA"
  | "CHECK_IN"
  | "WELCOME_BEAT"
  | "FESTIVAL"
  | "CHECK_OUT"
  | "OUTRO";

export type AgendaViagem = {
  id: number;
  titulo: string;
  descricao: string | null;

  dataHoraInicio: string;
  dataHoraFim: string | null;

  tipo: TipoAgendaViagem;

  ordem: number;
  visivelHospede: boolean;
  ativo: boolean;

  viagemId: number;
  viagemNome: string;
};

export type AgendaViagemRequest = {
  titulo: string;
  descricao: string | null;

  dataHoraInicio: string;
  dataHoraFim: string | null;

  tipo: TipoAgendaViagem;

  ordem: number;
  visivelHospede: boolean;
  ativo: boolean;

  viagemId: number;
};