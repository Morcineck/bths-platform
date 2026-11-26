import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function EditarEventoAgendaLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para editar eventos da agenda.">
      {children}
    </SomenteAdmin>
  );
}