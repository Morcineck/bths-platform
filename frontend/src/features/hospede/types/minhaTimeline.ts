export type TipoTimeline =
  | "CHEGADA"
  | "CHECK_IN"
  | "WELCOME_BEAT"
  | "FESTIVAL"
  | "CHECK_OUT"
  | "OUTRO";

export type MinhaTimeline = {
  id: number;
  titulo: string;
  descricao: string | null;

  dataHoraInicio: string;
  dataHoraFim: string | null;

  tipo: TipoTimeline;
};