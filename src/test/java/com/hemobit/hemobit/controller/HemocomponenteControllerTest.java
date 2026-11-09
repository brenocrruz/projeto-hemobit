package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.service.HemocomponenteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HemocomponenteController.class)
class HemocomponenteControllerTest {
    private static final String CORPO = """
            {"codigo":"B102030","tipo":"PLASMA_FRESCO_CONGELADO","quantidade":300,
             "dataProducao":"2026-10-20","dataValidade":"2026-12-20","doacaoId":1}
            """;

    @Autowired private MockMvc mvc;
    @MockitoBean private HemocomponenteService hemocomponentes;

    @Test
    void repassaOsFiltrosDaConsultaParaOServico() throws Exception {
        when(hemocomponentes.listar(any(), any(), any())).thenReturn(List.of());

        mvc.perform(get("/hemocomponentes")
                        .param("tipoSanguineo", "O_NEGATIVO")
                        .param("status", "DISPONIVEL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(hemocomponentes).listar(TipoSanguineo.O_NEGATIVO, StatusHemocomponente.DISPONIVEL, null);
    }

    @Test
    void filtroComValorInvalidoRetornaBadRequest() throws Exception {
        mvc.perform(get("/hemocomponentes").param("status", "INEXISTENTE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void entradaComValidadeVencidaRetornaBadRequestComAMensagemDaRegra() throws Exception {
        when(hemocomponentes.criar(any())).thenThrow(
                new IllegalArgumentException("Não é permitido cadastrar bolsas com data de validade vencida!"));

        mvc.perform(post("/hemocomponentes").contentType("application/json").content(CORPO))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Não é permitido cadastrar bolsas com data de validade vencida!"));
    }

    @Test
    void entradaComCodigoRepetidoRetornaConflito() throws Exception {
        when(hemocomponentes.criar(any())).thenThrow(
                new IllegalStateException("Já existe uma bolsa cadastrada com o código B102030."));

        mvc.perform(post("/hemocomponentes").contentType("application/json").content(CORPO))
                .andExpect(status().isConflict())
                .andExpect(content().string("Já existe uma bolsa cadastrada com o código B102030."));
    }

    @Test
    void resumoDoEstoqueRetornaAContagemPorTipoSanguineo() throws Exception {
        Map<TipoSanguineo, Long> resumo = new EnumMap<>(TipoSanguineo.class);
        resumo.put(TipoSanguineo.O_POSITIVO, 2L);
        resumo.put(TipoSanguineo.AB_NEGATIVO, 0L);
        when(hemocomponentes.resumoEstoque()).thenReturn(resumo);

        mvc.perform(get("/hemocomponentes/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.O_POSITIVO").value(2))
                .andExpect(jsonPath("$.AB_NEGATIVO").value(0));
    }
}
