export default function Home() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-6">
      <section className="w-full max-w-sm rounded-2xl border border-border bg-surface p-6">
        <p className="text-sm font-medium text-primary">
          Beat Trips
        </p>

        <h1 className="mt-2 text-3xl font-semibold tracking-tight text-foreground">
          BTHS Platform
        </h1>

        <p className="mt-3 text-sm leading-6 text-muted">
          Plataforma de operação e experiência do hóspede.
        </p>
      </section>
    </main>
  );
}