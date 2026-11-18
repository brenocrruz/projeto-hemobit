package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.dto.TipoHemocomponenteDTO;
import com.hemobit.hemobit.service.HemocomponenteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HemocomponenteController.class)
class HemocomponenteAcervoControllerTest {
    private static final String CORPO_TIPO_SANGUINEO_INVALIDO = """
            {"codigo":"B1","tipo":"PLASMA_FRESCO_CONGELADO","tipoSanguineo":"X_POSITIVO","quantidade":300,
             "dataProducao":"2026-10-20","dataValidade":"2026-12-20","doacaoId":1}
            """;

    @Autowired private MockMvc mvc;
    @MockitoBean private HemocomponenteService hemocomponentes;

    @Test
    void catalogoRetornaOsTiposComNomeEPrazoDeReferencia() throws Exception {
        when(hemocomponentes.listarTipos()).thenReturn(List.of(
                new TipoHemocomponenteDTO(TipoHemocomponente.CONCENTRADO_HEMACIAS),
                new TipoHemocomponenteDTO(TipoHemocomponente.CONCENTRADO_PLAQUETAS)));

        mvc.perform(get("/hemocomponentes/tipos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("CONCENTRADO_HEMACIAS"))
                .andExpect(jsonPath("$[0].nome").value("Concentrado de hemácias"))
                .andExpect(jsonPath("$[0].validadeReferenciaDias").value(42));
    }

    @Test
    void cadastroComTipoSanguineoInvalidoRetornaBadRequestComMensagemClara() throws Exception {
        mvc.perform(post("/hemocomponentes").contentType("application/json").content(CORPO_TIPO_SANGUINEO_INVALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("O_NEGATIVO")));
    }

    @Test
    void edicaoComTipoSanguineoInvalidoRetornaBadRequest() throws Exception {
        mvc.perform(put("/hemocomponentes/1").contentType("application/json").content(CORPO_TIPO_SANGUINEO_INVALIDO))
                .andExpect(status().isBadRequest());
    }
}
