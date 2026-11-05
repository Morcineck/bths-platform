import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function NovaHospedagemLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para cadastrar hospedagens.">
      {children}
    </SomenteAdmin>
  );
}