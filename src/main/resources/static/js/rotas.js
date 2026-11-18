"use strict";

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-rota");
    const campos = document.getElementById("campos-rota");
    const lista = document.getElementById("lista-rotas");
    const resumo = document.getElementById("resumo-rotas");
    const contagem = document.getElementById("contagem-rotas");
    const salvar = document.getElementById("salvar-rota");

    function mensagem(id, texto, classe = "") {
        const elemento = document.getElementById(id);
        elemento.textContent = texto;
        elemento.className = `stock-message ${classe}`;
        elemento.hidden = !texto;
    }

    async function lerErro(resposta) {
        const texto = (await resposta.text()).trim();
        return texto && texto.length < 400 && !texto.startsWith("<") && !texto.startsWith("{")
            ? texto
            : `Erro HTTP ${resposta.status}`;
    }

    async function obterJson(url) {
        const resposta = await fetch(url, { headers: { Accept: "application/json" }, cache: "no-store" });
        if (!resposta.ok) throw new Error(await lerErro(resposta));
        return resposta.json();
    }

    function formatarTempo(minutos) {
        if (minutos < 60) return `${minutos} min`;
        const horas = Math.floor(minutos / 60);
        const resto = minutos % 60;
        return resto ? `${horas} h ${resto} min` : `${horas} h`;
    }

    function formatarKm(valor) {
        return `${Number(valor).toLocaleString("pt-BR", { maximumFractionDigits: 1 })} km`;
    }

    function ponto(nome, classe, papel) {
        const div = document.createElement("div");
        div.className = `rota-ponto ${classe}`;
        div.textContent = nome;
        const legenda = document.createElement("small");
        legenda.textContent = papel;
        div.append(legenda);
        return div;
    }

    function cartaoResumo(rotulo, valor) {
        const div = document.createElement("div");
        const span = document.createElement("span");
        span.textContent = rotulo;
        const forte = document.createElement("strong");
        forte.textContent = valor;
        div.append(span, forte);
        return div;
    }

    function renderizar(rotas) {
        contagem.textContent = `${rotas.length.toLocaleString("pt-BR")} rotas cadastradas`;
        if (!rotas.length) {
            resumo.hidden = true;
            const vazio = document.createElement("p");
            vazio.className = "stock-message";
            vazio.textContent = "Nenhuma rota cadastrada. Use o formulário abaixo para começar.";
            lista.replaceChildren(vazio);
            return;
        }
        const maior = Math.max(...rotas.map(r => r.distancia));
        const totalKm = rotas.reduce((soma, r) => soma + r.distancia, 0);
        const maisRapida = rotas.reduce((menor, r) => (r.tempoEstimado < menor.tempoEstimado ? r : menor));
        resumo.replaceChildren(
            cartaoResumo("Rotas", String(rotas.length)),
            cartaoResumo("Distância total", formatarKm(totalKm)),
            cartaoResumo("Rota mais rápida", `${formatarTempo(maisRapida.tempoEstimado)} · ${maisRapida.origemNome} → ${maisRapida.destinoNome}`));
        resumo.hidden = false;
        const fragmento = document.createDocumentFragment();
        rotas.forEach(rota => {
            const item = document.createElement("article");
            item.className = "rota-item";
            const trajeto = document.createElement("div");
            trajeto.className = "rota-trajeto";
            const linha = document.createElement("div");
            linha.className = "rota-linha";
            const barra = document.createElement("span");
            barra.style.width = `${Math.max(8, Math.round((rota.distancia / maior) * 100))}%`;
            linha.append(barra);
            const texto = document.createElement("span");
            texto.textContent = `${formatarKm(rota.distancia)} · ${formatarTempo(rota.tempoEstimado)}`;
            trajeto.append(linha, texto);
            item.append(ponto(rota.origemNome, "origem", "Origem"), trajeto, ponto(rota.destinoNome, "destino", "Destino"));
            fragmento.append(item);
        });
        lista.replaceChildren(fragmento);
    }

    async function carregar() {
        try {
            const rotas = await obterJson("/api/rotas");
            renderizar(rotas.sort((a, b) => a.origemNome.localeCompare(b.origemNome, "pt-BR")
                || a.destinoNome.localeCompare(b.destinoNome, "pt-BR")));
        } catch (erro) {
            resumo.hidden = true;
            contagem.textContent = "Não foi possível consultar as rotas.";
            const aviso = document.createElement("p");
            aviso.className = "stock-message error";
            aviso.textContent = `Não foi possível carregar as rotas. ${erro.message}`;
            lista.replaceChildren(aviso);
        }
    }

    async function carregarLocalidades() {
        try {
            const opcoes = await obterJson("/api/estoque/opcoes");
            const localidades = opcoes.localidades || [];
            [form.elements.origemId, form.elements.destinoId].forEach(select => {
                localidades.forEach(local => {
                    const option = document.createElement("option");
                    option.value = local.id;
                    option.textContent = local.nome;
                    select.append(option);
                });
            });
            if (localidades.length >= 2) {
                campos.disabled = false;
                mensagem("estado-localidades", "");
            } else {
                mensagem("estado-localidades", "Cadastre ao menos duas localidades (unidades de saúde ou de coleta) para criar uma rota.", "error");
            }
        } catch (erro) {
            mensagem("estado-localidades", `Não foi possível carregar as localidades. ${erro.message}`, "error");
        }
    }

    form.addEventListener("submit", async evento => {
        evento.preventDefault();
        if (!form.reportValidity()) return;
        const dados = Object.fromEntries(new FormData(form));
        if (dados.origemId === dados.destinoId) {
            mensagem("mensagem-cadastro", "Origem e destino não podem ser o mesmo local.", "error");
            return;
        }
        const corpo = {
            origemId: Number(dados.origemId),
            destinoId: Number(dados.destinoId),
            distancia: Number(dados.distancia),
            tempoEstimado: Number(dados.tempoEstimado)
        };
        salvar.disabled = true;
        mensagem("mensagem-cadastro", "Cadastrando rota...");
        try {
            const resposta = await fetch("/api/rotas", {
                method: "POST",
                headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify(corpo)
            });
            if (!resposta.ok) throw new Error(await lerErro(resposta));
            form.reset();
            mensagem("mensagem-cadastro", "Rota cadastrada com sucesso.", "success");
            await carregar();
        } catch (erro) {
            mensagem("mensagem-cadastro", erro.message, "error");
        } finally {
            salvar.disabled = false;
        }
    });

    document.getElementById("atualizar-rotas").addEventListener("click", carregar);
    carregarLocalidades();
    carregar();
});
