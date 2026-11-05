import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function NovoQuartoLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para cadastrar quartos.">
      {children}
    </SomenteAdmin>
  );
}