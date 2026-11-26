export type StatusCheckIn =
  | "PENDENTE"
  | "REALIZADO"
  | "NAO_COMPARECEU";

export interface MeuCheckIn {
  hospedeNome: string;
  statusCheckIn: StatusCheckIn;
  dataHoraCheckIn: string | null;
}