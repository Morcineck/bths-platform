import { SomenteAdmin } from "@/components/auth/SomenteAdmin";

export default function NovaOperacaoLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <SomenteAdmin mensagem="Você não possui permissão para criar operações de traslado.">
      {children}
    </SomenteAdmin>
  );
}