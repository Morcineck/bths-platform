type PageHeaderProps = {
  title: string;
  description?: string;
};

export function PageHeader({
  title,
  description,
}: PageHeaderProps) {
  return (
    <header>
      <h1 className="text-2xl font-semibold tracking-tight text-foreground">
        {title}
      </h1>

      {description && (
        <p className="mt-2 text-sm leading-6 text-muted">
          {description}
        </p>
      )}
    </header>
  );
}