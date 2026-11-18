package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Localidade;
import com.hemobit.hemobit.domain.Rota;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import com.hemobit.hemobit.service.RotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RotaApiController.class)
class RotaApiControllerTest {
    private static final String CORPO = """
            {"origemId":1,"destinoId":2,"distancia":12.5,"tempoEstimado":25}
            """;

    @Autowired private MockMvc mvc;
    @MockitoBean private RotaService rotas;
    @MockitoBean private LocalidadeRepository localidades;

    @Test
    void listaAsRotasSemExporAsEntidadesAninhadas() throws Exception {
        Rota existente = rota(7L, local(1L, "Hemocentro Central"), local(2L, "Hospital São Marcos"), 12.5, 25);
        when(rotas.listarTodas()).thenReturn(List.of(existente));

        mvc.perform(get("/api/rotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].origemNome").value("Hemocentro Central"))
                .andExpect(jsonPath("$[0].destinoNome").value("Hospital São Marcos"))
                .andExpect(jsonPath("$[0].distancia").value(12.5))
                .andExpect(jsonPath("$[0].tempoEstimado").value(25))
                .andExpect(jsonPath("$[0].origem").doesNotExist());
    }

    @Test
    void cadastraRotaValidaERetornaCreated() throws Exception {
        Localidade origem = local(1L, "Hemocentro Central");
        Localidade destino = local(2L, "Hospital São Marcos");
        when(localidades.findById(1L)).thenReturn(Optional.of(origem));
        when(localidades.findById(2L)).thenReturn(Optional.of(destino));
        Rota salva = rota(9L, origem, destino, 12.5, 25);
        when(rotas.salvar(any())).thenReturn(salva);

        mvc.perform(post("/api/rotas").contentType("application/json").content(CORPO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.origemId").value(1))
                .andExpect(jsonPath("$.destinoNome").value("Hospital São Marcos"));
    }

    @Test
    void cadastroSemOrigemRetornaBadRequest() throws Exception {
        mvc.perform(post("/api/rotas").contentType("application/json")
                        .content("{\"destinoId\":2,\"distancia\":10,\"tempoEstimado\":20}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Informe a localidade de origem."));
        verify(rotas, never()).salvar(any());
    }

    @Test
    void cadastroComLocalidadeInexistenteRetornaBadRequest() throws Exception {
        Localidade origem = local(1L, "Hemocentro Central");
        when(localidades.findById(1L)).thenReturn(Optional.of(origem));
        when(localidades.findById(2L)).thenReturn(Optional.empty());

        mvc.perform(post("/api/rotas").contentType("application/json").content(CORPO))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Localidade de destino não encontrada."));
        verify(rotas, never()).salvar(any());
    }

    @Test
    void cadastroComTempoEstimadoInvalidoRetornaBadRequest() throws Exception {
        Localidade origem = local(1L, "Hemocentro Central");
        Localidade destino = local(2L, "Hospital São Marcos");
        when(localidades.findById(1L)).thenReturn(Optional.of(origem));
        when(localidades.findById(2L)).thenReturn(Optional.of(destino));

        mvc.perform(post("/api/rotas").contentType("application/json")
                        .content("{\"origemId\":1,\"destinoId\":2,\"distancia\":12.5,\"tempoEstimado\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O tempo estimado deve ser maior que zero."));
    }

    @Test
    void repassaARegraDeNegocioDoServicoComoBadRequest() throws Exception {
        Localidade origem = local(1L, "Hemocentro Central");
        Localidade destino = local(2L, "Hospital São Marcos");
        when(localidades.findById(1L)).thenReturn(Optional.of(origem));
        when(localidades.findById(2L)).thenReturn(Optional.of(destino));
        when(rotas.salvar(any())).thenThrow(new IllegalArgumentException("Distância deve ser maior que zero."));

        mvc.perform(post("/api/rotas").contentType("application/json").content(CORPO))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Distância deve ser maior que zero."));
    }

    private Localidade local(Long id, String nome) {
        Localidade local = mock(Localidade.class);
        when(local.getId()).thenReturn(id);
        when(local.getNome()).thenReturn(nome);
        return local;
    }

    private Rota rota(Long id, Localidade origem, Localidade destino, double distancia, int tempo) {
        Rota rota = mock(Rota.class);
        when(rota.getId()).thenReturn(id);
        when(rota.getOrigem()).thenReturn(origem);
        when(rota.getDestino()).thenReturn(destino);
        when(rota.getDistancia()).thenReturn(distancia);
        when(rota.getTempoEstimado()).thenReturn(tempo);
        return rota;
    }
}
