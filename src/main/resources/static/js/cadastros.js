"use strict";

document.addEventListener("DOMContentLoaded", () => {
    const tipo = document.body.dataset.cadastro;
    if (!["doadores", "unidades-coleta", "unidades-saude", "doacoes"].includes(tipo)) return;
    const url = tipo === "unidades-saude" ? "/unidades-saude" : `/api/cadastros/${tipo}`;
    const form = document.getElementById("form-cadastro");
    const campos = document.getElementById("campos-cadastro");
    const salvar = document.getElementById("salvar-cadastro");
    const atualizarLista = document.getElementById("atualizar-lista");
    const atualizarOpcoes = document.getElementById("atualizar-opcoes");
    const linhas = document.getElementById("cadastro-linhas");
    const tabela = document.getElementById("tabela-cadastros");
    const contagem = document.getElementById("contagem-cadastros");
    const colunas = tipo === "doadores" ? 3 : tipo === "unidades-saude" ? 6 : tipo === "doacoes" ? 5 : 4;
    let doadores = new Set();
    let unidades = new Set();
    let opcoesProntas = tipo !== "doacoes";
    let enviando = false;

    function mensagem(id, texto, classe = "") {
        const el = document.getElementById(id);
        el.textContent = texto;
        el.className = `stock-message ${classe}`;
        el.hidden = !texto;
    }
    function sangue(valor) {
        return valor ? valor.replace("_POSITIVO", "+").replace("_NEGATIVO", "-") : "Não informado";
    }
    function estadoLista(texto) {
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = colunas;
        td.textContent = texto;
        tr.append(td);
        linhas.replaceChildren(tr);
    }
    function dataHora(valor) {
        if (!valor) return "Não informada";
        const [data, hora] = valor.split("T");
        return `${data.split("-").reverse().join("/")} ${hora || ""}`;
    }
    async function getJson(endereco) {
        const response = await fetch(endereco, { headers: { Accept: "application/json" }, cache: "no-store" });
        if (!response.ok) throw new Error("Consulta indisponível");
        return response.json();
    }
    async function carregarLista() {
        atualizarLista.disabled = true;
        tabela.setAttribute("aria-busy", "true");
        contagem.textContent = "Consultando registros...";
        estadoLista("Carregando registros...");
        try {
            const dados = await getJson(url);
            if (!Array.isArray(dados) || dados.some(item => !item || item.id == null)) throw new Error("Formato inválido");
            contagem.textContent = `${dados.length.toLocaleString("pt-BR")} registros`;
            if (!dados.length) {
                estadoLista("Nenhum registro cadastrado. Use o formulário acima para começar.");
                return true;
            }
            const fragment = document.createDocumentFragment();
            dados.forEach(item => {
                const valores = tipo === "doadores" ? [`#${item.id}`, item.nome, sangue(item.tipoSanguineo)]
                    : tipo === "unidades-saude" ? [`#${item.id}`, item.nome, item.cnpj, item.cidade, item.endereco, item.telefone]
                    : tipo === "unidades-coleta" ? [`#${item.id}`, item.nome, item.cidade, item.endereco]
                    : [`#${item.id}`, dataHora(item.dataHora), Number(item.quantidade).toLocaleString("pt-BR", { maximumFractionDigits: 10 }), `#${item.doadorId}`, `#${item.unidadeColetaId}`];
                const tr = document.createElement("tr");
                valores.forEach(valor => {
                    const td = document.createElement("td");
                    td.textContent = valor || "Não informado";
                    tr.append(td);
                });
                fragment.append(tr);
            });
            linhas.replaceChildren(fragment);
            return true;
        } catch {
            // Não registra resposta, formulário ou dados pessoais em logs.
            estadoLista("Não foi possível carregar os registros. Use Atualizar lista para tentar novamente.");
            contagem.textContent = "Listagem indisponível";
            return false;
        } finally {
            tabela.setAttribute("aria-busy", "false");
            atualizarLista.disabled = enviando;
        }
    }
    function preencher(select, itens, placeholder, rotulo) {
        const anterior = select.value;
        select.replaceChildren(new Option(placeholder, ""));
        itens.forEach(item => select.add(new Option(rotulo(item), String(item.id))));
        if (itens.some(item => String(item.id) === anterior)) select.value = anterior;
    }
    async function carregarOpcoes() {
        if (tipo !== "doacoes" || enviando) return;
        campos.disabled = true;
        atualizarOpcoes.disabled = true;
        opcoesProntas = false;
        mensagem("estado-dependencias", "Carregando doadores e unidades de coleta...");
        try {
            const dados = await getJson("/api/cadastros/doacoes/opcoes");
            if (!Array.isArray(dados.doadores) || !Array.isArray(dados.unidadesColeta)
                || [...dados.doadores, ...dados.unidadesColeta].some(item => !item || item.id == null)) throw new Error("Formato inválido");
            doadores = new Set(dados.doadores.map(item => String(item.id)));
            unidades = new Set(dados.unidadesColeta.map(item => String(item.id)));
            preencher(form.elements.doadorId, dados.doadores, "Selecione um doador", d => `Doador #${d.id} · ${d.nome || "Nome não informado"} · ${sangue(d.tipoSanguineo)}`);
            preencher(form.elements.unidadeColetaId, dados.unidadesColeta, "Selecione uma unidade", u => `${u.nome} · #${u.id}`);
            opcoesProntas = doadores.size > 0 && unidades.size > 0;
            mensagem("estado-dependencias", opcoesProntas ? "" : "Para registrar uma doação, cadastre pelo menos um doador e uma unidade de coleta pelos links abaixo. Depois, atualize as opções.");
        } catch {
            doadores.clear();
            unidades.clear();
            mensagem("estado-dependencias", "Não foi possível carregar as opções. O formulário ficará bloqueado. Tente Atualizar opções.", "error");
        } finally {
            campos.disabled = !opcoesProntas;
            atualizarOpcoes.disabled = false;
        }
    }
    if (tipo === "doadores") {
        const hoje = new Date();
        form.elements.dataNascimento.max = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, "0")}-${String(hoje.getDate()).padStart(2, "0")}`;
    }
    form.addEventListener("input", () => {
        Array.from(form.elements).forEach(el => { if (typeof el.setCustomValidity === "function") el.setCustomValidity(""); });
    });
    function cnpjValido(valor) {
        if (!/^(\d{14}|\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2})$/.test(valor)) return false;
        const digitos = valor.replace(/\D/g, "");
        if (/^(\d)\1{13}$/.test(digitos)) return false;
        const calcular = pesos => {
            const soma = pesos.reduce((total, peso, indice) => total + Number(digitos[indice]) * peso, 0);
            const resto = soma % 11;
            return resto < 2 ? 0 : 11 - resto;
        };
        return calcular([5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]) === Number(digitos[12])
            && calcular([6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]) === Number(digitos[13]);
    }
    form.addEventListener("submit", async evento => {
        evento.preventDefault();
        if (enviando || !opcoesProntas) return;
        let payload;
        if (tipo === "doadores") {
            const nome = form.elements.nome.value.trim();
            form.elements.nome.setCustomValidity(nome ? "" : "Informe o nome do doador.");
            payload = { nome, dataNascimento: form.elements.dataNascimento.value, tipoSanguineo: form.elements.tipoSanguineo.value };
        } else if (tipo === "unidades-saude") {
            const nome = form.elements.nome.value.trim();
            const cidade = form.elements.cidade.value.trim();
            const cnpj = form.elements.cnpj.value.trim();
            form.elements.nome.setCustomValidity(nome ? "" : "Informe o nome da unidade de saúde.");
            form.elements.cidade.setCustomValidity(cidade ? "" : "Informe a cidade.");
            form.elements.cnpj.setCustomValidity(cnpjValido(cnpj) ? "" : "Informe um CNPJ válido, com ou sem pontuação.");
            payload = { nome, cidade, cnpj: cnpj.replace(/\D/g, ""),
                endereco: form.elements.endereco.value.trim() || null,
                telefone: form.elements.telefone.value.trim() || null };
        } else if (tipo === "unidades-coleta") {
            const nome = form.elements.nome.value.trim();
            form.elements.nome.setCustomValidity(nome ? "" : "Informe o nome da unidade.");
            payload = { nome, cidade: form.elements.cidade.value.trim() || null, endereco: form.elements.endereco.value.trim() || null };
        } else {
            const quantidade = Number(form.elements.quantidade.value);
            form.elements.quantidade.setCustomValidity(Number.isFinite(quantidade) && quantidade > 0 ? "" : "Informe quantidade maior que zero.");
            form.elements.doadorId.setCustomValidity(doadores.has(form.elements.doadorId.value) ? "" : "Selecione um doador existente.");
            form.elements.unidadeColetaId.setCustomValidity(unidades.has(form.elements.unidadeColetaId.value) ? "" : "Selecione uma unidade existente.");
            payload = { quantidade, dataHora: form.elements.dataHora.value, doadorId: Number(form.elements.doadorId.value), unidadeColetaId: Number(form.elements.unidadeColetaId.value) };
        }
        if (!form.reportValidity()) return;
        enviando = true;
        campos.disabled = true;
        atualizarLista.disabled = true;
        if (atualizarOpcoes) atualizarOpcoes.disabled = true;
        salvar.textContent = "Salvando...";
        mensagem("mensagem-cadastro", "Salvando registro...");
        try {
            const resposta = await fetch(url, { method: "POST", headers: { "Content-Type": "application/json", Accept: "application/json" }, body: JSON.stringify(payload) });
            if (!resposta.ok) {
                let texto = "Não foi possível salvar. Verifique os campos e tente novamente.";
                if (resposta.status === 400) {
                    if (tipo === "unidades-saude") {
                        const detalhe = await resposta.text();
                        if (detalhe && !detalhe.trim().startsWith("{") && !detalhe.trim().startsWith("<")) texto = detalhe;
                    } else {
                        const erro = await resposta.json().catch(() => null);
                        if (typeof erro?.mensagem === "string") texto = erro.mensagem;
                    }
                }
                mensagem("mensagem-cadastro", texto, "error");
                return;
            }
            form.reset();
            mensagem("mensagem-cadastro", "Cadastro realizado com sucesso.", "success");
            if (!await carregarLista()) mensagem("mensagem-cadastro", "Cadastro realizado com sucesso. Não foi possível atualizar a lista. Atualize a listagem sem repetir o cadastro.", "success");
        } catch {
            mensagem("mensagem-cadastro", "Não foi possível confirmar o resultado. Atualize a lista antes de tentar novamente para evitar duplicidade.", "error");
        } finally {
            enviando = false;
            campos.disabled = !opcoesProntas;
            salvar.textContent = "Salvar cadastro";
            atualizarLista.disabled = false;
            if (atualizarOpcoes) atualizarOpcoes.disabled = false;
        }
    });
    atualizarLista.addEventListener("click", carregarLista);
    if (atualizarOpcoes) atualizarOpcoes.addEventListener("click", carregarOpcoes);
    carregarLista();
    carregarOpcoes();
});
