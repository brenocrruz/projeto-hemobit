"use strict";

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-hemocomponente");
    const campos = document.getElementById("campos-cadastro");
    const linhas = document.getElementById("estoque-linhas");
    const tabela = document.getElementById("tabela-estoque");
    const contagem = document.getElementById("contagem-estoque");
    const atualizar = document.getElementById("atualizar-estoque");
    const tentarOpcoes = document.getElementById("recarregar-opcoes");
    const salvar = document.getElementById("salvar-hemocomponente");
    const pesquisa = document.getElementById("pesquisa-estoque");
    const filtroTipo = document.getElementById("filtro-tipo");
    const filtroStatus = document.getElementById("filtro-status");
    const filtroValidade = document.getElementById("filtro-validade");
    const tipos = {
        CONCENTRADO_HEMACIAS: "Concentrado de hemácias",
        CONCENTRADO_PLAQUETAS: "Concentrado de plaquetas",
        PLASMA_FRESCO_CONGELADO: "Plasma fresco congelado"
    };
    const status = {
        DISPONIVEL: "Disponível", RESERVADO: "Reservado", TRANSPORTANDO: "Transportando",
        ENTREGUE: "Entregue", UTILIZADO: "Utilizado", VENCIDO: "Vencido", DESCARTADO: "Descartado"
    };
    let registros = [];
    let doacoes = new Map();
    let localidades = new Map();
    let listaDisponivel = false;
    let opcoesDisponiveis = false;
    let enviando = false;

    // Os valores dos selects vêm dos enums Java; aqui apenas traduzimos os rótulos.
    [form.elements.tipo, filtroTipo].forEach(select => {
        Array.from(select.options).forEach(option => {
            if (option.value) option.textContent = tipos[option.value] || option.value;
        });
    });
    Array.from(filtroStatus.options).forEach(option => {
        if (option.value) option.textContent = status[option.value] || option.value;
    });

    function mensagem(id, texto, classe = "") {
        const elemento = document.getElementById(id);
        elemento.textContent = texto;
        elemento.className = `stock-message ${classe}`;
        elemento.hidden = !texto;
    }

    function dataFormatada(data) {
        if (!data) return "Não informada";
        const partes = data.slice(0, 10).split("-");
        return partes.length === 3 ? partes.reverse().join("/") : "Não informada";
    }

    function hojeLocal() {
        const hoje = new Date();
        return `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, "0")}-${String(hoje.getDate()).padStart(2, "0")}`;
    }

    function sangue(tipo) {
        return tipo ? tipo.replace("_POSITIVO", "+").replace("_NEGATIVO", "-") : "Não informado";
    }

    function nomeLocal(item) {
        if (item.localizacaoAtualId == null) return "Sem localização";
        return localidades.get(String(item.localizacaoAtualId))?.nome || `Localidade #${item.localizacaoAtualId}`;
    }

    function tipoSanguineo(item) {
        return sangue(doacoes.get(String(item.doacaoId))?.tipoSanguineo);
    }

    function normalizar(valor) {
        return String(valor).normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
    }

    function estadoTabela(texto) {
        const linha = document.createElement("tr");
        const celula = document.createElement("td");
        celula.colSpan = 7;
        celula.textContent = texto;
        linha.append(celula);
        linhas.replaceChildren(linha);
    }

    function renderizar() {
        if (!listaDisponivel) return;
        const termo = normalizar(pesquisa.value.trim());
        const hoje = hojeLocal();
        const filtrados = registros.filter(item => {
            const texto = [item.id, tipos[item.tipo] || item.tipo, tipoSanguineo(item),
                status[item.status] || item.status, nomeLocal(item)].join(" ");
            return (!termo || normalizar(texto).includes(termo))
                && (!filtroTipo.value || item.tipo === filtroTipo.value)
                && (!filtroStatus.value || item.status === filtroStatus.value)
                && (!filtroValidade.value || (item.dataValidade &&
                    (filtroValidade.value === "vencido" ? item.dataValidade < hoje : item.dataValidade >= hoje)));
        });
        contagem.textContent = `${filtrados.length.toLocaleString("pt-BR")} de ${registros.length.toLocaleString("pt-BR")} registros`;
        if (!filtrados.length) {
            estadoTabela(registros.length ? "Nenhum registro corresponde aos filtros." : "Nenhum hemocomponente cadastrado. Use o formulário abaixo para começar.");
            return;
        }
        const fragmento = document.createDocumentFragment();
        filtrados.forEach(item => {
            const linha = document.createElement("tr");
            function celula(texto, classe = "") {
                const td = document.createElement("td");
                td.textContent = texto;
                if (classe) td.className = classe;
                linha.append(td);
                return td;
            }
            celula(`#${item.id}`);
            celula(tipos[item.tipo] || item.tipo);
            const tag = document.createElement("span");
            tag.className = "blood-tag";
            tag.textContent = tipoSanguineo(item);
            celula("").append(tag);
            celula(Number(item.quantidade).toLocaleString("pt-BR", { maximumFractionDigits: 10 }));
            celula(dataFormatada(item.dataValidade), `stock-date ${item.dataValidade < hoje ? "expired" : ""}`);
            const badge = document.createElement("span");
            badge.className = `status-tag ${item.status === "DISPONIVEL" ? "available" : item.status === "VENCIDO" ? "expired" : ""}`;
            badge.textContent = status[item.status] || item.status;
            celula("").append(badge);
            celula(nomeLocal(item));
            fragmento.append(linha);
        });
        linhas.replaceChildren(fragmento);
    }

    async function obterJson(url) {
        const resposta = await fetch(url, { headers: { Accept: "application/json" }, cache: "no-store" });
        if (!resposta.ok) throw new Error(`Erro HTTP ${resposta.status}`);
        return resposta.json();
    }

    async function carregarLista() {
        listaDisponivel = false;
        tabela.setAttribute("aria-busy", "true");
        contagem.textContent = "Consultando registros...";
        estadoTabela("Carregando hemocomponentes...");
        try {
            const dados = await obterJson("/hemocomponentes");
            if (!Array.isArray(dados) || dados.some(item => !item || item.id == null || !Number.isFinite(item.quantidade))) {
                throw new Error("Formato de estoque inválido");
            }
            registros = dados;
            listaDisponivel = true;
            renderizar();
            return true;
        } catch (erro) {
            console.error("Erro ao carregar estoque:", erro);
            contagem.textContent = "Estoque indisponível";
            estadoTabela("Não foi possível carregar o estoque. Use Atualizar dados para tentar novamente.");
            return false;
        } finally {
            tabela.setAttribute("aria-busy", "false");
        }
    }

    function preencherSelect(select, itens, placeholder, rotulo) {
        const selecionado = select.value;
        select.replaceChildren(new Option(placeholder, ""));
        itens.forEach(item => select.add(new Option(rotulo(item), String(item.id))));
        if (Array.from(select.options).some(option => option.value === selecionado)) select.value = selecionado;
    }

    async function carregarOpcoes() {
        campos.disabled = true;
        tentarOpcoes.hidden = true;
        opcoesDisponiveis = false;
        mensagem("estado-opcoes", "Carregando doações e localidades...");
        try {
            const dados = await obterJson("/api/estoque/opcoes");
            if (!Array.isArray(dados.doacoes) || !Array.isArray(dados.localidades)
                || [...dados.doacoes, ...dados.localidades].some(item => !item || item.id == null)) {
                throw new Error("Formato de opções inválido");
            }
            doacoes = new Map(dados.doacoes.map(item => [String(item.id), item]));
            localidades = new Map(dados.localidades.map(item => [String(item.id), item]));
            preencherSelect(form.elements.doacaoId, dados.doacoes, "Selecione uma doação", item =>
                `Doação #${item.id} · ${dataFormatada(item.dataHora)} · ${sangue(item.tipoSanguineo)}`);
            preencherSelect(form.elements.localizacaoAtualId, dados.localidades, "Sem localização", item => item.nome || `Localidade #${item.id}`);
            opcoesDisponiveis = true;
            campos.disabled = enviando || doacoes.size === 0;
            mensagem("estado-opcoes", doacoes.size ? "" : "Não há doações cadastradas. É necessário existir uma doação no sistema para cadastrar um hemocomponente. Após o cadastro da doação, atualize os dados.");
            mensagem("aviso-opcoes", "");
        } catch (erro) {
            doacoes.clear();
            localidades.clear();
            console.error("Erro ao carregar opções:", erro);
            mensagem("estado-opcoes", "Não foi possível carregar doações e localidades. O cadastro está bloqueado até que as opções sejam carregadas.", "error");
            mensagem("aviso-opcoes", "Doações e localidades indisponíveis: tipo sanguíneo não informado e localização exibida por ID.");
            tentarOpcoes.hidden = false;
        }
        renderizar();
    }

    async function atualizarTudo() {
        atualizar.disabled = true;
        try {
            await Promise.all([carregarLista(), carregarOpcoes()]);
        } finally {
            atualizar.disabled = enviando;
        }
    }

    function validarDatas() {
        const producao = form.elements.dataProducao;
        const validade = form.elements.dataValidade;
        validade.min = producao.value;
        validade.setCustomValidity(producao.value && validade.value && validade.value < producao.value
            ? "A validade deve ser igual ou posterior à produção." : "");
    }

    form.addEventListener("input", () => {
        form.elements.quantidade.setCustomValidity("");
        form.elements.doacaoId.setCustomValidity("");
        form.elements.localizacaoAtualId.setCustomValidity("");
        validarDatas();
    });
    form.addEventListener("submit", async evento => {
        evento.preventDefault();
        if (enviando || !opcoesDisponiveis || campos.disabled) return;
        const quantidade = Number(form.elements.quantidade.value);
        form.elements.quantidade.setCustomValidity(Number.isFinite(quantidade) && quantidade > 0 ? "" : "Informe uma quantidade maior que zero.");
        const doacaoId = form.elements.doacaoId.value;
        const localId = form.elements.localizacaoAtualId.value;
        form.elements.doacaoId.setCustomValidity(doacoes.has(doacaoId) ? "" : "Selecione uma doação existente.");
        form.elements.localizacaoAtualId.setCustomValidity(!localId || localidades.has(localId) ? "" : "Selecione uma localização existente.");
        validarDatas();
        if (!form.reportValidity()) return;
        const payload = {
            tipo: form.elements.tipo.value, quantidade,
            dataProducao: form.elements.dataProducao.value,
            dataValidade: form.elements.dataValidade.value,
            doacaoId: Number(doacaoId), localizacaoAtualId: localId ? Number(localId) : null
        };
        enviando = true;
        campos.disabled = true;
        atualizar.disabled = true;
        salvar.textContent = "Cadastrando...";
        mensagem("mensagem-cadastro", "Salvando hemocomponente...");
        try {
            const resposta = await fetch("/hemocomponentes", {
                method: "POST", headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify(payload)
            });
            if (!resposta.ok) {
                const detalhe = await resposta.text();
                // O contrato atual retorna texto para validações de negócio (400).
                mensagem("mensagem-cadastro", resposta.status === 400 && detalhe && !detalhe.trim().startsWith("{") && !detalhe.trim().startsWith("<")
                    ? detalhe : `Não foi possível cadastrar (HTTP ${resposta.status}). Verifique os dados e tente novamente.`, "error");
                return;
            }
            form.reset();
            validarDatas();
            mensagem("mensagem-cadastro", "Hemocomponente cadastrado com sucesso.", "success");
            const atualizada = await carregarLista();
            if (!atualizada) mensagem("mensagem-cadastro", "Cadastro realizado com sucesso. A lista não pôde ser atualizada; use Atualizar dados. Não repita o cadastro.", "success");
        } catch (erro) {
            console.error("Erro no cadastro:", erro);
            mensagem("mensagem-cadastro", "Não foi possível confirmar o cadastro. Atualize a lista antes de tentar novamente para evitar duplicidade.", "error");
        } finally {
            enviando = false;
            campos.disabled = !opcoesDisponiveis || doacoes.size === 0;
            atualizar.disabled = false;
            salvar.textContent = "Cadastrar hemocomponente";
        }
    });

    pesquisa.addEventListener("input", renderizar);
    [filtroTipo, filtroStatus, filtroValidade].forEach(select => select.addEventListener("change", renderizar));
    document.getElementById("limpar-filtros").addEventListener("click", () => {
        [pesquisa, filtroTipo, filtroStatus, filtroValidade].forEach(input => { input.value = ""; });
        renderizar();
    });
    atualizar.addEventListener("click", atualizarTudo);
    tentarOpcoes.addEventListener("click", atualizarTudo);
    atualizarTudo();
});
