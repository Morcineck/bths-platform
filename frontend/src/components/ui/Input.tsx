import type { InputHTMLAttributes } from "react";

type InputProps = InputHTMLAttributes<HTMLInputElement>;

export function Input({ className = "", ...props }: InputProps) {
  return (
    <input
      className={`h-12 w-full rounded-xl border border-border bg-surface-secondary px-4 text-sm text-foreground outline-none transition-colors placeholder:text-muted focus:border-primary ${className}`}
      {...props}
    />
  );
}