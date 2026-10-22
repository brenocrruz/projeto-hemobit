package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.repository.DoadorRepository;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.UnidadeColetaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({DashboardContagensController.class, DashboardController.class})
class DashboardContagensControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private DoadorRepository doadores;
    @MockitoBean private DoacaoRepository doacoes;
    @MockitoBean private UnidadeColetaRepository unidadesColeta;

    @Test
    void retornaSomenteContagensSemCarregarListas() throws Exception {
        when(doadores.count()).thenReturn(5L);
        when(doacoes.count()).thenReturn(7L);
        when(unidadesColeta.count()).thenReturn(2L);
        mvc.perform(get("/api/dashboard/contagens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(3)))
                .andExpect(jsonPath("$.doadores").value(5))
                .andExpect(jsonPath("$.doacoes").value(7))
                .andExpect(jsonPath("$.unidadesColeta").value(2));
        verify(doadores).count();
        verify(doacoes).count();
        verify(unidadesColeta).count();
        verifyNoMoreInteractions(doadores, doacoes, unidadesColeta);
    }

    @Test
    void bancoVazioRetornaZerosValidos() throws Exception {
        mvc.perform(get("/api/dashboard/contagens"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"doadores\":0,\"doacoes\":0,\"unidadesColeta\":0}"));
    }

    @Test
    void renderizaDashboardSemPaineisFicticiosComQuatroAcessosReais() throws Exception {
        mvc.perform(get("/dashboard"))
                .andExpect(status().isOk()).andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("digital-drop")))
                .andExpect(content().string(containsString("id=\"total-doadores\"")))
                .andExpect(content().string(containsString("id=\"total-doacoes\"")))
                .andExpect(content().string(containsString("id=\"total-unidades-coleta\"")))
                .andExpect(content().string(containsString("href=\"/cadastros/doadores\"")))
                .andExpect(content().string(containsString("href=\"/cadastros/unidades-coleta\"")))
                .andExpect(content().string(containsString("href=\"/cadastros/doacoes\"")))
                .andExpect(content().string(containsString("href=\"/estoque\"")))
                .andExpect(content().string(not(containsString("requests-panel"))))
                .andExpect(content().string(not(containsString("alert-panel"))))
                .andExpect(content().string(not(containsString("Demonstrativo ·"))))
                .andExpect(content().string(not(containsString("bolsas físicas"))));
    }
}
