"use strict";

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-solicitacao");
    const campos = document.getElementById("campos-solicitacao");
    const linhas = document.getElementById("solicitacoes-linhas");
    const tabela = document.getElementById("tabela-solicitacoes");
    const contagem = document.getElementById("contagem-solicitacoes");
    const operador = document.getElementById("operador");
    const filtroStatus = document.getElementById("filtro-status");
    const salvar = document.getElementById("salvar-solicitacao");
    const painel = document.getElementById("detalhes");
    const tipos = {
        CONCENTRADO_HEMACIAS: "Concentrado de hemácias",
        CONCENTRADO_PLAQUETAS: "Concentrado de plaquetas",
        PLASMA_FRESCO_CONGELADO: "Plasma fresco congelado"
    };
    const prioridades = { ROTINA: "Rotina", URGENTE: "Urgente", EMERGENCIA: "Emergência" };
    const status = {
        PENDENTE: ["Pendente", "pendente"], EM_SEPARACAO: ["Em separação", "separacao"],
        ATENDIDA: ["Atendida", "atendida"], CANCELADA: ["Cancelada", "encerrada"], NEGADA: ["Negada", "encerrada"]
    };
    let solicitacoes = [];
    let ocupado = false;

    Array.from(form.elements.tipoHemocomponente.options).forEach(option => {
        if (option.value) option.textContent = tipos[option.value] || option.value;
    });
    Array.from(form.elements.tipoSanguineo.options).forEach(option => {
        if (option.value) option.textContent = sangue(option.value);
    });
    Array.from(form.elements.prioridade.options).forEach(option => {
        if (option.value) option.textContent = prioridades[option.value] || option.value;
    });

    function sangue(tipo) {
        return tipo ? tipo.replace("_POSITIVO", "+").replace("_NEGATIVO", "-") : "Não informado";
    }

    function dataFormatada(data) {
        if (!data) return "—";
        const partes = data.slice(0, 10).split("-");
        return partes.length === 3 ? partes.reverse().join("/") : "—";
    }

    function dataHoraFormatada(data) {
        if (!data) return "—";
        return `${dataFormatada(data)} ${data.slice(11, 16)}`;
    }

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

    function estadoTabela(texto) {
        const linha = document.createElement("tr");
        const celula = document.createElement("td");
        celula.colSpan = 8;
        celula.textContent = texto;
        linha.append(celula);
        linhas.replaceChildren(linha);
    }

    function botao(texto, classe, aoClicar) {
        const el = document.createElement("button");
        el.type = "button";
        el.className = `stock-button ${classe}`;
        el.textContent = texto;
        el.addEventListener("click", aoClicar);
        return el;
    }

    function operadorInformado() {
        const nome = operador.value.trim();
        if (!nome) {
            mensagem("mensagem-acao", "Informe o operador responsável antes de alterar a solicitação.", "error");
            operador.focus();
        }
        return nome;
    }

    async function executar(item, rotulo, requisicao) {
        if (ocupado) return;
        const nome = operadorInformado();
        if (!nome) return;
        ocupado = true;
        mensagem("mensagem-acao", `${rotulo} da solicitação #${item.id}...`);
        try {
            const resposta = await requisicao(nome);
            if (!resposta.ok) throw new Error(await lerErro(resposta));
            mensagem("mensagem-acao", `Solicitação #${item.id}: ${rotulo.toLowerCase()} concluído.`, "success");
            await carregar();
            if (!painel.hidden) await abrirDetalhes(item);
        } catch (erro) {
            mensagem("mensagem-acao", erro.message, "error");
        } finally {
            ocupado = false;
        }
    }

    function mudarStatus(item, novoStatus, rotulo) {
        return executar(item, rotulo, nome => fetch(`/solicitacoes/${item.id}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json", Accept: "application/json" },
            body: JSON.stringify({ status: novoStatus, operador: nome })
        }));
    }

    function atender(item) {
        return executar(item, "Atendimento", nome => fetch(
            `/solicitacoes/${item.id}/atender?operador=${encodeURIComponent(nome)}`,
            { method: "PUT", headers: { Accept: "application/json" } }));
    }

    function acoes(item) {
        const grupo = document.createElement("div");
        grupo.className = "acoes";
        if (item.status === "PENDENTE") {
            grupo.append(botao("Separar", "secondary", () => mudarStatus(item, "EM_SEPARACAO", "Separação")));
        }
        if (item.status === "PENDENTE" || item.status === "EM_SEPARACAO") {
            grupo.append(botao("Atender", "", () => atender(item)));
            grupo.append(botao("Cancelar", "perigo", () => mudarStatus(item, "CANCELADA", "Cancelamento")));
            grupo.append(botao("Negar", "perigo", () => mudarStatus(item, "NEGADA", "Negação")));
        }
        grupo.append(botao("Detalhes", "secondary", () => abrirDetalhes(item)));
        return grupo;
    }

    function renderizar() {
        const filtradas = solicitacoes.filter(item => !filtroStatus.value || item.status === filtroStatus.value);
        contagem.textContent = `${filtradas.length.toLocaleString("pt-BR")} de ${solicitacoes.length.toLocaleString("pt-BR")} solicitações`;
        tabela.setAttribute("aria-busy", "false");
        if (!filtradas.length) {
            estadoTabela(filtroStatus.value
                ? "Nenhuma solicitação encontrada para o status selecionado."
                : "Nenhuma solicitação registrada. Use o formulário abaixo para começar.");
            return;
        }
        const fragmento = document.createDocumentFragment();
        filtradas.forEach(item => {
            const linha = document.createElement("tr");
            function celula(texto) {
                const td = document.createElement("td");
                td.textContent = texto;
                linha.append(td);
                return td;
            }
            celula(`#${item.id}`);
            celula(item.unidadeSaudeNome || "—");
            celula(tipos[item.tipoHemocomponente] || item.tipoHemocomponente);
            const sangueTag = document.createElement("span");
            sangueTag.className = "blood-tag";
            sangueTag.textContent = sangue(item.tipoSanguineo);
            celula("").append(sangueTag);
            celula(Number(item.quantidade).toLocaleString("pt-BR", { maximumFractionDigits: 0 }));
            const prioridade = document.createElement("span");
            prioridade.className = `prioridade-tag ${String(item.prioridade || "").toLowerCase()}`;
            prioridade.textContent = prioridades[item.prioridade] || item.prioridade;
            celula("").append(prioridade);
            const [rotulo, classe] = status[item.status] || [item.status, ""];
            const badge = document.createElement("span");
            badge.className = `status-tag ${classe}`;
            badge.textContent = rotulo;
            celula("").append(badge);
            celula("").append(acoes(item));
            fragmento.append(linha);
        });
        linhas.replaceChildren(fragmento);
    }

    async function carregar() {
        try {
            const dados = await obterJson("/solicitacoes");
            solicitacoes = dados.sort((a, b) => b.id - a.id);
            renderizar();
        } catch (erro) {
            solicitacoes = [];
            tabela.setAttribute("aria-busy", "false");
            contagem.textContent = "Não foi possível consultar as solicitações.";
            estadoTabela(`Não foi possível carregar as solicitações. ${erro.message}`);
        }
    }

    async function abrirDetalhes(item) {
        document.getElementById("detalhes-titulo").textContent =
            `Solicitação #${item.id} · ${item.unidadeSaudeNome || "sem unidade"}`;
        const historico = document.getElementById("historico");
        const bolsas = document.getElementById("bolsas-reservadas");
        historico.replaceChildren();
        bolsas.replaceChildren();
        painel.hidden = false;
        try {
            const [eventos, reservadas] = await Promise.all([
                obterJson(`/solicitacoes/${item.id}/historico`),
                obterJson(`/solicitacoes/${item.id}/bolsas`)
            ]);
            eventos.forEach(evento => {
                const li = document.createElement("li");
                const anterior = evento.statusAnterior ? (status[evento.statusAnterior] || [evento.statusAnterior])[0] : "Criada";
                li.textContent = `${anterior} → ${(status[evento.statusNovo] || [evento.statusNovo])[0]}`;
                const detalhe = document.createElement("small");
                detalhe.textContent = `${dataHoraFormatada(evento.dataHora)} · ${evento.operador || "—"}`;
                li.append(detalhe);
                historico.append(li);
            });
            if (!reservadas.length) {
                const linha = document.createElement("tr");
                const td = document.createElement("td");
                td.colSpan = 4;
                td.textContent = "Nenhuma bolsa reservada ainda. As bolsas são reservadas no atendimento.";
                linha.append(td);
                bolsas.append(linha);
            }
            reservadas.forEach(bolsa => {
                const linha = document.createElement("tr");
                [bolsa.codigo || `#${bolsa.id}`, tipos[bolsa.tipo] || bolsa.tipo, sangue(bolsa.tipoSanguineo),
                    dataFormatada(bolsa.dataValidade)].forEach(texto => {
                    const td = document.createElement("td");
                    td.textContent = texto;
                    linha.append(td);
                });
                bolsas.append(linha);
            });
            painel.scrollIntoView({ behavior: "smooth", block: "start" });
        } catch (erro) {
            mensagem("mensagem-acao", `Não foi possível carregar os detalhes. ${erro.message}`, "error");
        }
    }

    async function carregarUnidades() {
        try {
            const unidades = await obterJson("/unidades-saude");
            const select = form.elements.unidadeSaudeId;
            unidades.sort((a, b) => String(a.nome).localeCompare(String(b.nome), "pt-BR")).forEach(unidade => {
                const option = document.createElement("option");
                option.value = unidade.id;
                option.textContent = unidade.nome;
                select.append(option);
            });
            if (unidades.length) {
                campos.disabled = false;
                mensagem("estado-unidades", "");
            } else {
                mensagem("estado-unidades", "Nenhuma unidade de saúde cadastrada. Cadastre uma para registrar solicitações.", "error");
            }
        } catch (erro) {
            mensagem("estado-unidades", `Não foi possível carregar as unidades de saúde. ${erro.message}`, "error");
        }
    }

    form.addEventListener("submit", async evento => {
        evento.preventDefault();
        if (!form.reportValidity()) return;
        const dados = Object.fromEntries(new FormData(form));
        const corpo = {
            unidadeSaudeId: Number(dados.unidadeSaudeId),
            tipoHemocomponente: dados.tipoHemocomponente,
            tipoSanguineo: dados.tipoSanguineo,
            quantidade: Number(dados.quantidade),
            prioridade: dados.prioridade
        };
        salvar.disabled = true;
        mensagem("mensagem-cadastro", "Registrando solicitação...");
        try {
            const resposta = await fetch("/solicitacoes", {
                method: "POST",
                headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify(corpo)
            });
            if (!resposta.ok) throw new Error(await lerErro(resposta));
            const criada = await resposta.json();
            form.reset();
            mensagem("mensagem-cadastro", `Solicitação #${criada.id} registrada como pendente.`, "success");
            await carregar();
        } catch (erro) {
            mensagem("mensagem-cadastro", erro.message, "error");
        } finally {
            salvar.disabled = false;
        }
    });

    filtroStatus.addEventListener("change", renderizar);
    document.getElementById("atualizar-solicitacoes").addEventListener("click", carregar);
    document.getElementById("fechar-detalhes").addEventListener("click", () => { painel.hidden = true; });
    carregarUnidades();
    carregar();
});
