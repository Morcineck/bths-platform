import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function VeiculosLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para acessar veículos.">
      {children}
    </SomenteAdmin>
  );
}