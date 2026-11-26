import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function UsuariosLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para acessar usuários.">
      {children}
    </SomenteAdmin>
  );
}