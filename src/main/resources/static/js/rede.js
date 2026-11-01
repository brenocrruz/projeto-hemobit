"use strict";

// Limites usados para interpretar as métricas (mesmos de docs/rsd/benchmarking.md).
const LIMITES = {
    latencia: { bom: 200, atencao: 500 },   // p95 do RTT, ms
    jitter: { bom: 30, atencao: 100 },      // ms
    perda: { bom: 1, atencao: 5 },          // %
    erros5xx: { bom: 0, atencao: 1 },       // % das requisições
    erros4xx: { bom: 2, atencao: 10 },      // % das requisições
    api: { bom: 100, atencao: 300 }         // p95 do tempo de servidor, ms
};

document.addEventListener("DOMContentLoaded", () => {
    const estado = document.getElementById("rede-estado");
    const canal = document.getElementById("canal");
    const reiniciar = document.getElementById("reiniciar");
    const alertas = document.getElementById("alertas");
    const fmt = (v, casas = 1) => v === null || v === undefined ? "—" : Number(v).toLocaleString("pt-BR", { maximumFractionDigits: casas, minimumFractionDigits: casas });

    function nivel(valor, limite) {
        if (valor === null || valor === undefined) return "";
        if (valor <= limite.bom) return "bom";
        if (valor <= limite.atencao) return "atencao";
        return "critico";
    }

    function badge(id, classe) {
        const el = document.getElementById(`b-${id}`);
        el.className = `rede-badge ${classe}`;
        el.textContent = { bom: "BOM", atencao: "ATENÇÃO", critico: "CRÍTICO" }[classe] || "SEM DADOS";
    }

    function texto(id, valor, detalhe) {
        document.getElementById(`m-${id}`).textContent = valor;
        document.getElementById(`d-${id}`).textContent = detalhe;
    }

    function grafico(serie) {
        const svg = document.getElementById("grafico-latencia");
        const ns = "http://www.w3.org/2000/svg";
        const largura = 600, altura = 200;
        const valores = serie.flatMap(p => [p.latenciaMediaMs, p.latenciaP95Ms]).filter(v => v !== null);
        const maximo = Math.max(10, ...valores) * 1.15;
        const x = i => (i / Math.max(1, serie.length - 1)) * largura;
        const y = v => altura - (v / maximo) * altura;
        // Segundos sem amostra interrompem a linha (novo "M") em vez de ligar pontos distantes.
        const caminho = chave => {
            let d = "", aberto = false;
            serie.forEach((p, i) => {
                if (p[chave] === null) { aberto = false; return; }
                d += `${aberto ? " L" : " M"}${x(i).toFixed(1)},${y(p[chave]).toFixed(1)}`;
                aberto = true;
            });
            return d.trim();
        };
        const filhos = [];
        serie.forEach((p, i) => {
            if (p.perdidos > 0) {
                const r = document.createElementNS(ns, "rect");
                r.setAttribute("class", "barra-perda");
                r.setAttribute("x", (x(i) - 4).toFixed(1));
                r.setAttribute("y", "0");
                r.setAttribute("width", "8");
                r.setAttribute("height", String(altura));
                filhos.push(r);
            }
        });
        [["linha-media", "latenciaMediaMs"], ["linha-p95", "latenciaP95Ms"]].forEach(([classe, chave]) => {
            const d = caminho(chave);
            if (d.length > 1) {
                const linha = document.createElementNS(ns, "path");
                linha.setAttribute("class", classe);
                linha.setAttribute("d", d);
                filhos.push(linha);
            }
        });
        const rotulo = document.createElementNS(ns, "text");
        rotulo.setAttribute("x", "6");
        rotulo.setAttribute("y", "14");
        rotulo.textContent = `${fmt(maximo / 1.15, 0)} ms`;
        filhos.push(rotulo);
        svg.replaceChildren(...filhos);
    }

    function diagnostico(t, api) {
        const itens = [];
        const lat = t.latenciaMs.p95;
        if (lat === null) {
            itens.push(["", "Nenhum envio de telemetria entregue na janela. Verifique se o simulador está ativo e se o endpoint de ingestão responde."]);
        } else {
            const n = nivel(lat, LIMITES.latencia);
            itens.push([n, n === "bom"
                ? `Latência p95 de ${fmt(lat)} ms: o canal entrega 95% das leituras bem abaixo do limite de ${LIMITES.latencia.bom} ms, adequado para acompanhar a cadeia fria em tempo quase real.`
                : `Latência p95 de ${fmt(lat)} ms acima de ${LIMITES.latencia.bom} ms: alertas de temperatura e desvio de rota chegam com atraso perceptível ao operador.`]);
            const nj = nivel(t.jitterMs, LIMITES.jitter);
            itens.push([nj, nj === "bom"
                ? `Jitter de ${fmt(t.jitterMs)} ms: os intervalos de chegada são estáveis, o que permite usar timeouts curtos sem falsos alarmes de "veículo sem sinal".`
                : `Jitter de ${fmt(t.jitterMs)} ms indica chegada irregular; leituras podem chegar fora de ordem e o timeout de "sem sinal" precisa de margem maior.`]);
        }
        const perda = Math.max(t.perdaPercentual, t.perdaSequenciaPercentual);
        const np = nivel(perda, LIMITES.perda);
        itens.push([np, np === "bom"
            ? `Perda de ${fmt(perda, 2)}% dentro do limite de ${LIMITES.perda.bom}%: a trilha de temperatura de cada bolsa fica praticamente completa.`
            : `Perda de ${fmt(perda, 2)}% (cliente ${fmt(t.perdaPercentual, 2)}%, lacunas de sequência ${fmt(t.perdaSequenciaPercentual, 2)}%): há buracos na trilha de temperatura; uma excursão térmica curta pode passar despercebida.`]);
        const n5 = nivel(api.taxaErro5xxPercentual, LIMITES.erros5xx);
        const n4 = nivel(api.taxaErro4xxPercentual, LIMITES.erros4xx);
        itens.push([n5 === "critico" || n4 === "critico" ? "critico" : (n5 === "atencao" || n4 === "atencao" ? "atencao" : "bom"),
            `API: ${fmt(api.taxaErro4xxPercentual, 2)}% de 4xx (requisições inválidas, ex.: leitura sem temperatura) e ${fmt(api.taxaErro5xxPercentual, 2)}% de 5xx (falhas do servidor). 5xx acima de zero exige investigação nos logs.`]);
        const lista = document.getElementById("diagnostico");
        lista.replaceChildren(...itens.map(([classe, frase]) => {
            const li = document.createElement("li");
            li.className = classe;
            li.textContent = frase;
            return li;
        }));
    }

    function celula(conteudo, classe) {
        const td = document.createElement("td");
        td.textContent = conteudo;
        if (classe) td.className = classe;
        return td;
    }

    function tabelas(veiculos, rotas) {
        const corpoV = document.getElementById("veiculos");
        corpoV.replaceChildren(...(veiculos.length ? veiculos.map(v => {
            const tr = document.createElement("tr");
            tr.append(celula(v.veiculo), celula(v.tipoHemocomponente),
                celula(`${fmt(v.temperaturaC)} °C`, v.temperaturaConforme ? "temp-ok" : "temp-fora"),
                celula(`${v.faixaC[0]} a ${v.faixaC[1]} °C`),
                celula(`${fmt(v.latitude, 4)}, ${fmt(v.longitude, 4)}`),
                celula(String(v.sequencia)), celula(`${v.atrasoUnidirecionalMs} ms`));
            return tr;
        }) : [linhaVazia(7, "Sem leituras ainda.")]));
        const corpoR = document.getElementById("rotas");
        corpoR.replaceChildren(...(rotas.length ? rotas.slice(0, 8).map(r => {
            const tr = document.createElement("tr");
            tr.append(celula(r.rota), celula(String(r.requisicoes)), celula(`${fmt(r.p95Ms)} ms`),
                celula(String(r.erros4xx), r.erros4xx ? "temp-fora" : ""), celula(String(r.erros5xx), r.erros5xx ? "temp-fora" : ""));
            return tr;
        }) : [linhaVazia(5, "Sem requisições na janela.")]));
    }

    function linhaVazia(colunas, mensagem) {
        const tr = document.createElement("tr");
        const td = celula(mensagem);
        td.colSpan = colunas;
        tr.append(td);
        return tr;
    }

    function renderizar(m) {
        const t = m.telemetria, api = m.api;

        texto("latencia", t.latenciaMs.p95 === null ? "—" : `${fmt(t.latenciaMs.p95)} ms`, `p50 ${fmt(t.latenciaMs.p50)} ms · média ${fmt(t.latenciaMs.media)} ms · máx ${fmt(t.latenciaMs.max)} ms`);
        badge("latencia", nivel(t.latenciaMs.p95, LIMITES.latencia));
        texto("jitter", `${fmt(t.jitterMs)} ms`, "por veículo, entre envios consecutivos");
        badge("jitter", t.amostras ? nivel(t.jitterMs, LIMITES.jitter) : "");
        const perda = Math.max(t.perdaPercentual, t.perdaSequenciaPercentual);
        texto("perda", `${fmt(perda, 2)}%`, `cliente ${fmt(t.perdaPercentual, 2)}% · sequência ${fmt(t.perdaSequenciaPercentual, 2)}% · ${t.perdidos} perdidos`);
        badge("perda", t.amostras ? nivel(perda, LIMITES.perda) : "");
        texto("vazao", `${fmt(t.vazaoMsgPorSegundo, 2)} msg/s`, `${fmt(t.vazaoKbps, 2)} kbit/s de payload HTTP`);
        badge("vazao", t.amostras ? (t.vazaoMsgPorSegundo > 0 ? "bom" : "critico") : "");
        texto("erros", `${api.erros4xx} · ${api.erros5xx}`, `4xx ${fmt(api.taxaErro4xxPercentual, 2)}% · 5xx ${fmt(api.taxaErro5xxPercentual, 2)}%`);
        const n5 = nivel(api.taxaErro5xxPercentual, LIMITES.erros5xx), n4 = nivel(api.taxaErro4xxPercentual, LIMITES.erros4xx);
        badge("erros", api.requisicoes ? (n5 === "critico" || n4 === "critico" ? "critico" : n5 === "atencao" || n4 === "atencao" ? "atencao" : "bom") : "");
        texto("api", api.tempoServidorMs.p95 === null ? "—" : `${fmt(api.tempoServidorMs.p95)} ms`, `${fmt(api.vazaoReqPorSegundo, 2)} req/s · ${api.requisicoes} requisições`);
        badge("api", nivel(api.tempoServidorMs.p95, LIMITES.api));

        document.getElementById("amostras").textContent = `${t.amostras} amostras`;
        grafico(m.serie);
        diagnostico(t, api);
        tabelas(m.veiculos.veiculos, api.rotas);
        reiniciar.hidden = !m.controleHabilitado;

        const sim = m.simulador;
        estado.textContent = sim.habilitado
            ? `Simulador ativo · ${sim.veiculos} veículos → ${sim.destino} · atualizado ${new Date(m.geradoEm).toLocaleTimeString("pt-BR")}`
            : "Simulador desligado neste ambiente; exibindo apenas o tráfego real da API.";
    }

    function alertaTemperatura(a) {
        alertas.querySelector(".rede-alerta-vazio")?.remove();
        const li = document.createElement("li");
        li.textContent = `${new Date(a.timestamp).toLocaleTimeString("pt-BR")} · ${a.veiculo} (${a.tipoHemocomponente}) a ${fmt(a.temperaturaC)} °C, fora da faixa ${a.faixaC[0]} a ${a.faixaC[1]} °C`;
        alertas.prepend(li);
        while (alertas.children.length > 5) alertas.lastElementChild.remove();
    }

    // Canal principal: WebSocket /ws/telemetria (contratos-api.md). Se cair, o painel passa a
    // consultar /api/v1/rede/metricas a cada 2 s e tenta reconectar com espera crescente.
    let polling = null, espera = 1000;

    async function consultar() {
        try {
            const resposta = await fetch("/api/v1/rede/metricas", { headers: { Accept: "application/json" }, cache: "no-store" });
            if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`);
            renderizar(await resposta.json());
        } catch (erro) {
            estado.textContent = `Falha ao consultar métricas (${erro.message}). Nova tentativa em 2 s.`;
        }
    }

    function modoCanal(tipo, rotulo) {
        canal.className = `rede-canal ${tipo}`;
        canal.textContent = rotulo;
    }

    function iniciarPolling() {
        if (polling) return;
        modoCanal("polling", "POLLING HTTP · 2 s");
        consultar();
        polling = setInterval(consultar, 2000);
    }

    function conectar() {
        const protocolo = location.protocol === "https:" ? "wss:" : "ws:";
        const ws = new WebSocket(`${protocolo}//${location.host}/ws/telemetria`);
        ws.addEventListener("open", () => {
            espera = 1000;
            clearInterval(polling);
            polling = null;
            modoCanal("ws", "WEBSOCKET · PUSH 1 s");
        });
        ws.addEventListener("message", evento => {
            const msg = JSON.parse(evento.data);
            if (msg.evento === "METRICAS_REDE") {
                renderizar(msg);
                modoCanal("ws", `WEBSOCKET · atraso ${Math.max(0, Date.now() - msg.geradoEm)} ms`);
            } else if (msg.evento === "TEMPERATURA_FORA_DA_FAIXA") {
                alertaTemperatura(msg);
            }
        });
        ws.addEventListener("close", () => {
            iniciarPolling();
            setTimeout(conectar, espera);
            espera = Math.min(espera * 2, 30000);
        });
    }

    reiniciar.addEventListener("click", async () => {
        await fetch("/api/v1/rede/metricas/reiniciar", { method: "POST" });
        consultar();
    });

    if ("WebSocket" in window) conectar(); else iniciarPolling();
});
