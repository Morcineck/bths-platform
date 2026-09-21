"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

import { buscarUsuarioAutenticado } from "@/features/auth/services/authService";
import { cadastrarHospede } from "@/features/hospede/services/hospedeService";
import { listarViagens } from "@/features/viagem/services/viagemService";

export default function NovoHospedePage() {
  const [carregando, setCarregando] =
    useState(true);

  const [acessoPermitido, setAcessoPermitido] =
    useState(false);

  const [nomeCompleto, setNomeCompleto] =
    useState("");

  const [cpf, setCpf] =
    useState("");

  const [telefone, setTelefone] =
    useState("");

  const [email, setEmail] =
    useState("");

  const [
    dataNascimento,
    setDataNascimento,
  ] = useState("");

  const [
    horarioPrevistoChegada,
    setHorarioPrevistoChegada,
  ] = useState("");

  const [viagemId, setViagemId] =
    useState<number | null>(null);

  const [viagemNome, setViagemNome] =
    useState("");

  const [cadastrando, setCadastrando] =
    useState(false);

  const [erroCadastro, setErroCadastro] =
    useState("");

  const [sucessoCadastro, setSucessoCadastro] =
    useState("");

  useEffect(() => {
    async function carregarPagina() {
      try {
        const usuario =
          await buscarUsuarioAutenticado();

        const permitido =
          usuario.perfil === "ADMIN";

        setAcessoPermitido(permitido);

        if (!permitido) {
          return;
        }

        const viagens =
          await listarViagens();

        const viagem = viagens.find(
          (item) => item.status === "PLANEJADA",
        );

        if (!viagem) {
          throw new Error(
            "Nenhuma viagem planejada foi encontrada.",
          );
        }

        setViagemId(viagem.id);
        setViagemNome(viagem.nome);
      } catch {
        setErroCadastro(
          "Não foi possível carregar os dados necessários para o cadastro.",
        );
      } finally {
        setCarregando(false);
      }
    }

    carregarPagina();
  }, []);

  async function handleCadastrarHospede() {
    if (!viagemId) {
      setErroCadastro(
        "Nenhuma viagem ativa foi encontrada.",
      );
      return;
    }

    if (!nomeCompleto.trim()) {
      setErroCadastro(
        "Informe o nome completo.",
      );
      return;
    }

    if (cpf.length !== 11) {
      setErroCadastro(
        "O CPF deve conter 11 números.",
      );
      return;
    }

    try {
      setCadastrando(true);
      setErroCadastro("");
      setSucessoCadastro("");

      await cadastrarHospede({
        nomeCompleto: nomeCompleto.trim(),
        cpf,
        telefone:
          telefone.trim() || undefined,
        email:
          email.trim() || undefined,
        dataNascimento:
          dataNascimento || undefined,
        horarioPrevistoChegada:
          horarioPrevistoChegada || undefined,
        statusCheckIn: "PENDENTE",
        viagemId,
      });

      setNomeCompleto("");
      setCpf("");
      setTelefone("");
      setEmail("");
      setDataNascimento("");
      setHorarioPrevistoChegada("");

      setSucessoCadastro(
        "Hóspede cadastrado com sucesso.",
      );
    } catch (error) {
      setSucessoCadastro("");

      setErroCadastro(
        error instanceof Error
          ? error.message
          : "Não foi possível cadastrar o hóspede.",
      );
    } finally {
      setCadastrando(false);
    }
  }

  if (carregando) {
    return (
      <p className="text-sm text-muted">
        Verificando permissão...
      </p>
    );
  }

  if (!acessoPermitido) {
    return (
      <div className="space-y-4">
        <p className="text-sm text-red-400">
          Você não possui permissão para cadastrar hóspedes.
        </p>

        <Link
          href="/hospedes"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para hóspedes
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <header>
        <Link
          href="/hospedes"
          className="text-sm font-medium text-primary"
        >
          ← Voltar para hóspedes
        </Link>

        <h1 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
          Novo hóspede
        </h1>

        <p className="mt-2 text-sm text-muted">
          Cadastre um novo hóspede na viagem.
        </p>
      </header>

     <div className="space-y-4">
       <div>
         <label
           htmlFor="nome-completo"
           className="mb-2 block text-sm font-medium text-foreground"
         >
           Nome completo
         </label>

         <input
           id="nome-completo"
           type="text"
           value={nomeCompleto}
           onChange={(event) =>
             setNomeCompleto(event.target.value)
           }
           placeholder="Digite o nome completo"
           className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
         />
       </div>

       <div>
         <label
           htmlFor="cpf"
           className="mb-2 block text-sm font-medium text-foreground"
         >
           CPF
         </label>

         <input
           id="cpf"
           type="text"
           value={cpf}
           onChange={(event) =>
             setCpf(event.target.value)
           }
           placeholder="Digite somente os 11 números"
           maxLength={11}
           className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
         />
       </div>
       <div>
         <label
           htmlFor="telefone"
           className="mb-2 block text-sm font-medium text-foreground"
         >
           Telefone
         </label>

         <input
           id="telefone"
           type="text"
           value={telefone}
           onChange={(event) =>
             setTelefone(event.target.value)
           }
           placeholder="Digite o telefone"
           className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
         />
       </div>

       <div>
         <label
           htmlFor="email"
           className="mb-2 block text-sm font-medium text-foreground"
         >
           E-mail
         </label>

         <input
           id="email"
           type="email"
           value={email}
           onChange={(event) =>
             setEmail(event.target.value)
           }
           placeholder="Digite o e-mail"
           className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
         />
       </div>

       <div>
         <label
           htmlFor="data-nascimento"
           className="mb-2 block text-sm font-medium text-foreground"
         >
           Data de nascimento
         </label>

         <input
           id="data-nascimento"
           type="date"
           value={dataNascimento}
           onChange={(event) =>
             setDataNascimento(event.target.value)
           }
           className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
         />
       </div>

       <div>
         <label
           htmlFor="horario-previsto-chegada"
           className="mb-2 block text-sm font-medium text-foreground"
         >
           Horário previsto de chegada
         </label>

         <input
           id="horario-previsto-chegada"
           type="datetime-local"
           value={horarioPrevistoChegada}
           onChange={(event) =>
             setHorarioPrevistoChegada(
               event.target.value,
             )
           }
           className="h-12 w-full rounded-xl border border-border bg-surface px-4 text-sm text-foreground outline-none transition-colors focus:border-primary"
         />
       </div>
       <div className="rounded-xl border border-border bg-surface p-4">
         <p className="text-sm text-muted">
           Viagem
         </p>

         <p className="mt-1 font-medium text-foreground">
           {viagemNome || "Carregando viagem..."}
         </p>
       </div>

       {erroCadastro && (
         <p
           role="alert"
           className="text-sm text-red-400"
         >
           {erroCadastro}
         </p>
       )}

       {sucessoCadastro && (
         <p className="text-sm text-green-400">
           {sucessoCadastro}
         </p>
       )}

       <button
         type="button"
         onClick={handleCadastrarHospede}
         disabled={cadastrando}
         className="rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
       >
         {cadastrando
           ? "Cadastrando..."
           : "Cadastrar hóspede"}
       </button>
     </div>
    </div>
  );
}