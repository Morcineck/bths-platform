import type { StatusViagem } from "../types/viagem";

export const OPCOES_STATUS_VIAGEM: {
  valor: StatusViagem;
  rotulo: string;
}[] = [
  { valor: "PLANEJADA", rotulo: "Planejada" },
  { valor: "EM_ANDAMENTO", rotulo: "Em andamento" },
  { valor: "FINALIZADA", rotulo: "Finalizada" },
  { valor: "CANCELADA", rotulo: "Cancelada" },
];

export function rotuloStatusViagem(
  status: StatusViagem,
): string {
  return (
    OPCOES_STATUS_VIAGEM.find(
      (opcao) => opcao.valor === status,
    )?.rotulo ?? status
  );
}

export function formatarDataViagem(
  data: string,
): string {
  const [ano, mes, dia] = data.split("-");

  if (!ano || !mes || !dia) {
    return data;
  }

  return `${dia}/${mes}/${ano}`;
}