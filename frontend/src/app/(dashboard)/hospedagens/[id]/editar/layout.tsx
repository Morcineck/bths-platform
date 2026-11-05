import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function EditarHospedagemLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para editar hospedagens.">
      {children}
    </SomenteAdmin>
  );
}