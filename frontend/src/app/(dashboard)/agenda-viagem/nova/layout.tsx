import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function NovoEventoAgendaLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para cadastrar eventos na agenda.">
      {children}
    </SomenteAdmin>
  );
}