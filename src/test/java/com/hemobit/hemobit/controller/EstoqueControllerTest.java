package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.Localidade;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import com.hemobit.hemobit.service.HemocomponenteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EstoqueController.class, EstoqueOpcoesController.class, HemocomponenteController.class})
class EstoqueControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private DoacaoRepository doacoes;
    @MockitoBean private LocalidadeRepository localidades;
    @MockitoBean private HemocomponenteService hemocomponentes;

    @Test
    void renderizaPaginaComFragmentsEnumsEEstoqueAtivo() throws Exception {
        mvc.perform(get("/estoque"))
                .andExpect(status().isOk())
                .andExpect(view().name("estoque"))
                .andExpect(model().attribute("paginaAtual", "estoque"))
                .andExpect(content().string(containsString("CONCENTRADO_HEMACIAS")))
                .andExpect(content().string(containsString("PLASMA_FRESCO_CONGELADO")))
                .andExpect(content().string(containsString("Gestão de estoque")))
                .andExpect(content().string(containsString("href=\"/estoque\" class=\" active\"")))
                .andExpect(content().string(containsString("/js/estoque.js")));
    }

    @Test
    void listaOpcoesSemDadosPessoaisNemEntidadesAninhadas() throws Exception {
        Doacao doacao = mock(Doacao.class);
        Doador doador = mock(Doador.class);
        Localidade local = mock(Localidade.class);
        when(doacao.getId()).thenReturn(7L);
        when(doacao.getDataHora()).thenReturn(LocalDateTime.of(2026, 10, 20, 10, 0));
        when(doacao.getDoador()).thenReturn(doador);
        when(doador.getTipoSanguineo()).thenReturn(TipoSanguineo.O_NEGATIVO);
        when(local.getId()).thenReturn(2L);
        when(local.getNome()).thenReturn("Unidade Central");
        when(doacoes.findAll(any(Sort.class))).thenReturn(List.of(doacao));
        when(localidades.findAll(any(Sort.class))).thenReturn(List.of(local));
        mvc.perform(get("/api/estoque/opcoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doacoes[0].id").value(7))
                .andExpect(jsonPath("$.doacoes[0].tipoSanguineo").value("O_NEGATIVO"))
                .andExpect(jsonPath("$.doacoes[0]", aMapWithSize(3)))
                .andExpect(jsonPath("$.doacoes[0].doador").doesNotExist())
                .andExpect(jsonPath("$.localidades[0].nome").value("Unidade Central"))
                .andExpect(jsonPath("$.localidades[0]", aMapWithSize(2)));
        verify(doador, never()).getNome();
        verify(doador, never()).getTelefone();
    }

    @Test
    void retornaListasVaziasQuandoNaoHaOpcoes() throws Exception {
        when(doacoes.findAll(any(Sort.class))).thenReturn(List.of());
        when(localidades.findAll(any(Sort.class))).thenReturn(List.of());
        mvc.perform(get("/api/estoque/opcoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doacoes", hasSize(0)))
                .andExpect(jsonPath("$.localidades", hasSize(0)));
    }

    @Test
    void preservaListagemEValidacaoDoContratoDeHemocomponentes() throws Exception {
        when(hemocomponentes.listarTodos()).thenReturn(List.of());
        mvc.perform(get("/hemocomponentes"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        when(hemocomponentes.criar(any())).thenThrow(new IllegalArgumentException("Doação não encontrada."));
        mvc.perform(post("/hemocomponentes").contentType("application/json")
                        .content("""
                                {"tipo":"CONCENTRADO_HEMACIAS","quantidade":100,
                                 "dataProducao":"2026-10-20","dataValidade":"2026-11-20",
                                 "doacaoId":999,"localizacaoAtualId":null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doação não encontrada."));
    }
}
