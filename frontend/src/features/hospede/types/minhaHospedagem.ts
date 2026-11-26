export type MinhaHospedagem = {
  hospedagemId: number;
  nome: string;

  endereco: string | null;
  cidade: string | null;
  estado: string | null;

  localizacaoUrl: string | null;
  imagemUrl: string | null;

  wifiNome: string | null;
  wifiSenha: string | null;

  horarioCheckIn: string | null;
  horarioCheckOut: string | null;

  contatoNome: string | null;
  contatoTelefone: string | null;

  observacaoPublica: string | null;

  viagemId: number;
  viagemNome: string;
};