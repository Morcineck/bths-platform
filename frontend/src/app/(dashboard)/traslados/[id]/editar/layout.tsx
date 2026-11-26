import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function EditarOperacaoLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para editar operações de traslado.">
      {children}
    </SomenteAdmin>
  );
}