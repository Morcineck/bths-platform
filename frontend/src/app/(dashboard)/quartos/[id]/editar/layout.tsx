import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function EditarQuartoLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para editar quartos.">
      {children}
    </SomenteAdmin>
  );
}