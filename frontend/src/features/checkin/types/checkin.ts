import type { StatusCheckIn } from "@/features/hospede/types/hospede";

export type CheckInResponse = {
  hospedeId: number;
  hospedeNome: string;
  statusCheckIn: StatusCheckIn;
  dataHoraCheckIn: string | null;
  responsavel: string | null;
  observacao: string | null;

  viagemId: number;
  viagemNome: string;

  quartoId: number | null;
  quartoNome: string | null;
};

export type QrCodeCheckInResponse = {
  hospedeId: number;
  hospedeNome: string;
  statusCheckIn: StatusCheckIn;

  viagemId: number;
  viagemNome: string;

  quartoId: number | null;
  quartoNome: string | null;
};