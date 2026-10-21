async function carregarEstoquePorTipo() {
    const totalElemento = document.getElementById("total-estoque");
    const gridElemento = document.getElementById("estoque-grid");

    if (!totalElemento || !gridElemento) {
        console.warn("Elementos de estoque não encontrados.");
        return;
    }

    try {
        const resposta = await fetch("/api/indicadores/estoque-tipo");

        if (!resposta.ok) {
            throw new Error(`Erro HTTP ${resposta.status}`);
        }

        const resultado = await resposta.json();
        const estoque = resultado.dados;

        if (!estoque || typeof estoque !== "object" || Array.isArray(estoque)) {
            throw new Error("Formato de dados inválido.");
        }

        const itens = Object.entries(estoque).map(([tipo, quantidade]) => ({
            tipo,
            quantidade: Number(quantidade)
        }));

        if (itens.length === 0 || itens.some(item =>
            !Number.isFinite(item.quantidade) || item.quantidade < 0
        )) {
            throw new Error("Valores de estoque inválidos.");
        }

        const total = itens.reduce(
            (soma, item) => soma + item.quantidade, 0
        );

        const maiorQuantidade = Math.max(
            ...itens.map(item => item.quantidade), 1
        );

        totalElemento.textContent = total.toLocaleString("pt-BR");
        gridElemento.replaceChildren();

        itens.forEach(item => {
            const bloco = document.createElement("div");
            bloco.className = "blood-item";

            const topo = document.createElement("div");
            topo.className = "blood-item-top";

            const tipo = document.createElement("strong");
            tipo.textContent = item.tipo;

            const quantidade = document.createElement("span");
            quantidade.textContent =
                `${item.quantidade.toLocaleString("pt-BR")} bolsas`;

            topo.append(tipo, quantidade);

            const trilho = document.createElement("div");
            trilho.className = "bar-track";

            const barra = document.createElement("div");
            barra.className = "bar-fill";
            barra.style.width =
                `${(item.quantidade / maiorQuantidade) * 100}%`;

            trilho.append(barra);
            bloco.append(topo, trilho);
            gridElemento.append(bloco);
        });

        console.log("Estoque carregado:", total);

    } catch (erro) {
        console.error("Erro ao carregar estoque:", erro);

        totalElemento.textContent = "—";

        const mensagem = document.createElement("p");
        mensagem.textContent =
            "Não foi possível carregar os dados do estoque.";

        gridElemento.replaceChildren(mensagem);
    }
}

document.addEventListener("DOMContentLoaded", carregarEstoquePorTipo);