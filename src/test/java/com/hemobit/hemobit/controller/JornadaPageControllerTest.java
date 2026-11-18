package com.hemobit.hemobit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(JornadaPageController.class)
class JornadaPageControllerTest {
    @Autowired private MockMvc mvc;

    @Test
    void paginaDeAtendimentoMostraOsEnumsEOMenuComTodaAJornada() throws Exception {
        mvc.perform(get("/atendimento"))
                .andExpect(status().isOk())
                .andExpect(view().name("atendimento"))
                .andExpect(model().attribute("paginaAtual", "atendimento"))
                .andExpect(content().string(containsString("Solicitações e atendimento")))
                .andExpect(content().string(containsString("CONCENTRADO_PLAQUETAS")))
                .andExpect(content().string(containsString("O_NEGATIVO")))
                .andExpect(content().string(containsString("EMERGENCIA")))
                .andExpect(content().string(containsString("/js/atendimento.js")))
                .andExpect(content().string(containsString("href=\"/estoque\"")))
                .andExpect(content().string(containsString("href=\"/logistica/rotas\"")))
                .andExpect(content().string(containsString("href=\"/painel-estatistico.html\"")))
                .andExpect(content().string(containsString("href=\"/rede\"")));
    }

    @Test
    void paginaDeRotasCarregaOScriptEMarcaOItemDoMenu() throws Exception {
        mvc.perform(get("/logistica/rotas"))
                .andExpect(status().isOk())
                .andExpect(view().name("rotas"))
                .andExpect(model().attribute("paginaAtual", "rotas"))
                .andExpect(content().string(containsString("Rotas de entrega")))
                .andExpect(content().string(containsString("/js/rotas.js")))
                .andExpect(content().string(containsString("href=\"/atendimento\"")));
    }
}
