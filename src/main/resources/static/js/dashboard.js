"use strict";

document.addEventListener("DOMContentLoaded", () => {
    const grid = document.getElementById("estoque-grid");
    const atualizar = document.getElementById("atualizar-dashboard");
    if (!grid || !atualizar) return;
    const grupos = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];
    const idsCadastros = ["doadores", "doacoes", "unidades-coleta"];
    const inteiroValido = valor => Number.isSafeInteger(valor) && valor >= 0;

    function card(id, valor, descricao, carregando = false) {
        const elemento = document.getElementById(`total-${id}`);
        elemento.textContent = valor;
        elemento.setAttribute("aria-busy", String(carregando));
        elemento.classList.toggle("metric-unavailable", valor === "—");
        document.getElementById(`estado-${id}`).textContent = descricao;
    }

    async function consultar(url) {
        const controller = new AbortController();
        const timer = setTimeout(() => controller.abort(), 15000);
        try {
            const resposta = await fetch(url, { headers: { Accept: "application/json" }, cache: "no-store", signal: controller.signal });
            if (!resposta.ok) throw new Error("Consulta indisponível");
            return await resposta.json();
        } finally {
            clearTimeout(timer);
        }
    }

    async function carregarEstoque() {
        card("estoque", "…", "Carregando registros disponíveis...", true);
        grid.setAttribute("aria-busy", "true");
        const carregando = document.createElement("p");
        carregando.textContent = "Carregando dados do estoque...";
        grid.replaceChildren(carregando);
        try {
            const resultado = await consultar("/api/indicadores/estoque-real");
            const estoque = resultado.dados;
            if (!estoque || typeof estoque !== "object" || Array.isArray(estoque)
                || Object.keys(estoque).length !== grupos.length
                || grupos.some(tipo => !inteiroValido(estoque[tipo]))) throw new Error("Formato inválido");
            const total = grupos.reduce((soma, tipo) => soma + estoque[tipo], 0);
            if (!inteiroValido(total)) throw new Error("Total inválido");
            const maior = Math.max(...grupos.map(tipo => estoque[tipo]), 1);
            const fragmento = document.createDocumentFragment();
            if (total === 0) {
                const vazio = document.createElement("p");
                vazio.className = "stock-notice";
                vazio.textContent = "Nenhum hemocomponente disponível com validade vigente.";
                fragmento.append(vazio);
            }
            grupos.forEach(tipo => {
                const item = document.createElement("div");
                item.className = "blood-item";
                const topo = document.createElement("div");
                topo.className = "blood-item-top";
                const rotulo = document.createElement("strong");
                rotulo.textContent = tipo;
                const quantidade = document.createElement("span");
                quantidade.textContent = `${estoque[tipo].toLocaleString("pt-BR")} ${estoque[tipo] === 1 ? "registro" : "registros"}`;
                topo.append(rotulo, quantidade);
                const trilho = document.createElement("div");
                trilho.className = "bar-track";
                trilho.setAttribute("aria-hidden", "true");
                const barra = document.createElement("div");
                barra.className = "bar-fill";
                barra.style.width = `${estoque[tipo] / maior * 100}%`;
                trilho.append(barra);
                item.append(topo, trilho);
                fragmento.append(item);
            });
            grid.replaceChildren(fragmento);
            card("estoque", total.toLocaleString("pt-BR"), "Registros disponíveis e não vencidos");
        } catch {
            card("estoque", "—", "Indisponível. Tente atualizar os dados.");
            const erro = document.createElement("p");
            erro.className = "stock-notice dashboard-error";
            erro.textContent = "Não foi possível carregar o estoque. Use Atualizar dados para tentar novamente.";
            grid.replaceChildren(erro);
        } finally {
            grid.setAttribute("aria-busy", "false");
        }
    }

    async function carregarContagens() {
        idsCadastros.forEach(id => card(id, "…", "Carregando total cadastrado...", true));
        try {
            const dados = await consultar("/api/dashboard/contagens");
            if (!dados || ![dados.doadores, dados.doacoes, dados.unidadesColeta].every(inteiroValido)) throw new Error("Formato inválido");
            card("doadores", dados.doadores.toLocaleString("pt-BR"), "Total de doadores cadastrados");
            card("doacoes", dados.doacoes.toLocaleString("pt-BR"), "Total de doações registradas");
            card("unidades-coleta", dados.unidadesColeta.toLocaleString("pt-BR"), "Somente unidades de coleta");
        } catch {
            idsCadastros.forEach(id => card(id, "—", "Indisponível. Tente atualizar os dados."));
        }
    }

    async function carregarDashboard() {
        atualizar.disabled = true;
        try {
            await Promise.all([carregarEstoque(), carregarContagens()]);
        } finally {
            atualizar.disabled = false;
        }
    }
    atualizar.addEventListener("click", carregarDashboard);
    carregarDashboard();
});