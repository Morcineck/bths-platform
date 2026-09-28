export default function PerfilPage() {
  return (
    <section className="space-y-4">
      <div>
        <p className="text-sm text-muted">Sua conta</p>
        <h1 className="text-2xl font-semibold text-foreground">
          Perfil
        </h1>
      </div>

      <div className="rounded-2xl border border-border bg-surface p-5">
        <p className="text-sm text-muted">
          Seus dados e configurações da conta aparecerão aqui.
        </p>
      </div>
    </section>
  );
}