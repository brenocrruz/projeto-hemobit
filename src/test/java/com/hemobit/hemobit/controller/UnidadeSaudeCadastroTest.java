package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.UnidadeSaude;
import com.hemobit.hemobit.repository.UnidadeSaudeRepository;
import com.hemobit.hemobit.service.UnidadeSaudeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CadastroPageController.class, UnidadeSaudeController.class})
@Import(UnidadeSaudeService.class)
class UnidadeSaudeCadastroTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private UnidadeSaudeRepository repository;

    @Test
    void renderizaFormularioComCnpjCidadeETelefone() throws Exception {
        mvc.perform(get("/cadastros/unidades-saude"))
                .andExpect(status().isOk()).andExpect(view().name("unidades-saude"))
                .andExpect(model().attribute("paginaAtual", "unidades-saude"))
                .andExpect(content().string(containsString("data-cadastro=\"unidades-saude\"")))
                .andExpect(content().string(containsString("name=\"cnpj\"")))
                .andExpect(content().string(containsString("required")))
                .andExpect(content().string(containsString("name=\"cidade\" required")))
                .andExpect(content().string(containsString("name=\"telefone\"")));
    }

    @Test
    void contratoExistenteRecebeEPersisteCnpjNoSubtipoSaude() throws Exception {
        when(repository.save(any())).thenAnswer(call -> {
            UnidadeSaude unidade = call.getArgument(0);
            ReflectionTestUtils.setField(unidade, "id", 1L);
            return unidade;
        });
        // CNPJ usado exclusivamente como fixture de teste; a interface exige entrada real do usuário.
        mvc.perform(post("/unidades-saude").contentType("application/json").content("""
                {"nome":"Unidade teste","cidade":"Recife","endereco":"Endereço teste",
                 "cnpj":"11222333000181","telefone":"81999999999"}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cnpj").value("11222333000181"))
                .andExpect(jsonPath("$.telefone").value("81999999999"));
        verify(repository).save(argThat(unidade -> unidade.getClass() == UnidadeSaude.class
                && "11222333000181".equals(unidade.getCnpj())));
    }

    @Test
    void rejeitaPayloadAnteriorSemCnpj() throws Exception {
        mvc.perform(post("/unidades-saude").contentType("application/json")
                        .content("{\"nome\":\"Upa\",\"cidade\":\"Imbiribeira\",\"endereco\":\"dsdsds\"}"))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("CNPJ")));
        verify(repository, never()).save(any());
    }

    @Test
    void listaUnidadesPeloEndpointOriginal() throws Exception {
        when(repository.findAll()).thenReturn(List.of());
        mvc.perform(get("/unidades-saude")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
}
