package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.*;
import com.hemobit.hemobit.dto.CadastrosDTO;
import com.hemobit.hemobit.repository.*;
import com.hemobit.hemobit.service.CadastroService;
import com.hemobit.hemobit.service.DoadorService;
import com.hemobit.hemobit.service.HemocomponenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CadastroApiController.class, CadastroPageController.class,
        EstoqueOpcoesController.class, HemocomponenteController.class})
@Import({CadastroService.class, DoadorService.class, HemocomponenteService.class})
class CadastroControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private DoadorRepository doadores;
    @MockitoBean private UnidadeColetaRepository unidades;
    @MockitoBean private DoacaoRepository doacoes;
    @MockitoBean private LocalidadeRepository localidades;
    @MockitoBean private HemocomponenteRepository hemocomponentes;
    private final Map<Long, Doador> doadoresSalvos = new LinkedHashMap<>();
    private final Map<Long, UnidadeColeta> unidadesSalvas = new LinkedHashMap<>();
    private final Map<Long, Doacao> doacoesSalvas = new LinkedHashMap<>();
    private final Map<Long, Hemocomponente> componentesSalvos = new LinkedHashMap<>();

    @BeforeEach
    void repositorios() {
        when(doadores.save(any())).thenAnswer(call -> {
            Doador d = call.getArgument(0);
            long id = doadoresSalvos.size() + 1L;
            ReflectionTestUtils.setField(d, "id", id);
            doadoresSalvos.put(id, d);
            return d;
        });
        when(unidades.save(any())).thenAnswer(call -> {
            UnidadeColeta u = call.getArgument(0);
            long id = unidadesSalvas.size() + 1L;
            ReflectionTestUtils.setField(u, "id", id);
            unidadesSalvas.put(id, u);
            return u;
        });
        when(doacoes.save(any())).thenAnswer(call -> {
            Doacao d = call.getArgument(0);
            long id = doacoesSalvas.size() + 1L;
            ReflectionTestUtils.setField(d, "id", id);
            doacoesSalvas.put(id, d);
            return d;
        });
        when(hemocomponentes.save(any())).thenAnswer(call -> {
            Hemocomponente h = call.getArgument(0);
            long id = componentesSalvos.size() + 1L;
            ReflectionTestUtils.setField(h, "id", id);
            componentesSalvos.put(id, h);
            return h;
        });
        when(doadores.findAll()).thenAnswer(call -> new ArrayList<>(doadoresSalvos.values()));
        when(unidades.findAll(any(Sort.class))).thenAnswer(call -> new ArrayList<>(unidadesSalvas.values()));
        when(doacoes.findAll(any(Sort.class))).thenAnswer(call -> new ArrayList<>(doacoesSalvas.values()));
        when(localidades.findAll(any(Sort.class))).thenAnswer(call -> new ArrayList<>(unidadesSalvas.values()));
        when(hemocomponentes.findAll()).thenAnswer(call -> new ArrayList<>(componentesSalvos.values()));
        when(doadores.findById(anyLong())).thenAnswer(call -> Optional.ofNullable(doadoresSalvos.get(call.getArgument(0))));
        when(unidades.findById(anyLong())).thenAnswer(call -> Optional.ofNullable(unidadesSalvas.get(call.getArgument(0))));
        when(doacoes.findById(anyLong())).thenAnswer(call -> Optional.ofNullable(doacoesSalvas.get(call.getArgument(0))));
        when(localidades.findById(anyLong())).thenAnswer(call -> Optional.ofNullable(unidadesSalvas.get(call.getArgument(0))));
    }

    @Test
    void renderizaTresTelasComFragmentsENavegacao() throws Exception {
        for (String tela : List.of("doadores", "unidades-coleta", "doacoes")) {
            mvc.perform(get("/cadastros/" + tela))
                    .andExpect(status().isOk()).andExpect(view().name(tela))
                    .andExpect(model().attribute("paginaAtual", tela))
                    .andExpect(content().string(containsString("/js/cadastros.js")))
                    .andExpect(content().string(containsString("/estoque#novo-hemocomponente")))
                    .andExpect(content().string(containsString("class=\"page-header\"")))
                    .andExpect(content().string(containsString("aria-current=\"page\"")));
        }
    }

    @Test
    void retornaListasEOpcoesVazias() throws Exception {
        for (String recurso : List.of("doadores", "unidades-coleta", "doacoes")) {
            mvc.perform(get("/api/cadastros/" + recurso)).andExpect(status().isOk()).andExpect(content().json("[]"));
        }
        mvc.perform(get("/api/cadastros/doacoes/opcoes"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.doadores", hasSize(0)))
                .andExpect(jsonPath("$.unidadesColeta", hasSize(0)));
    }

    private void criarDoadorEUnidade() throws Exception {
        mvc.perform(post("/api/cadastros/doadores").contentType("application/json").content("""
                {"nome":"Doador de teste","dataNascimento":"1990-01-01","tipoSanguineo":"O_POSITIVO"}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$", aMapWithSize(3)))
                .andExpect(jsonPath("$.dataNascimento").doesNotExist()).andExpect(jsonPath("$.telefone").doesNotExist());
        mvc.perform(post("/api/cadastros/unidades-coleta").contentType("application/json")
                        .content("{\"nome\":\"Unidade teste\",\"cidade\":\"Recife\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void fluxoCompletoAteHemocomponenteUsaServicosReaisComRepositoriosSimulados() throws Exception {
        criarDoadorEUnidade();
        mvc.perform(get("/api/cadastros/doacoes/opcoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doadores[0]", aMapWithSize(3)))
                .andExpect(jsonPath("$.doadores[0].id").value(1))
                .andExpect(jsonPath("$.doadores[0].nome").value("Doador de teste"))
                .andExpect(jsonPath("$.doadores[0].telefone").doesNotExist())
                .andExpect(jsonPath("$.doadores[0].dataNascimento").doesNotExist());
        mvc.perform(post("/api/cadastros/doacoes").contentType("application/json").content("""
                {"doadorId":1,"unidadeColetaId":1,"dataHora":"2026-10-21T10:00:00","quantidade":450}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doadorId").value(1)).andExpect(jsonPath("$.doador").doesNotExist());
        mvc.perform(get("/api/estoque/opcoes"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.doacoes[0].id").value(1))
                .andExpect(jsonPath("$.doacoes[0].tipoSanguineo").value("O_POSITIVO"))
                .andExpect(jsonPath("$.localidades[0].id").value(1));
        mvc.perform(post("/hemocomponentes").contentType("application/json").content("""
                {"tipo":"CONCENTRADO_HEMACIAS","quantidade":100,"dataProducao":"2026-10-21",
                 "dataValidade":"2026-11-21","doacaoId":1,"localizacaoAtualId":1}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("DISPONIVEL"));
        mvc.perform(get("/hemocomponentes"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doacaoId").value(1));
        mvc.perform(get("/api/cadastros/doacoes")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantidade").value(450));
        mvc.perform(get("/api/cadastros/doadores")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", aMapWithSize(3)));
        assertNull(doadoresSalvos.get(1L).getTelefone());
        assertSame(unidadesSalvas.get(1L), doacoesSalvas.get(1L).getUnidadeColeta());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"nome\":\"  \",\"dataNascimento\":\"1990-01-01\",\"tipoSanguineo\":\"A_POSITIVO\"}",
            "{\"nome\":\"Teste\",\"dataNascimento\":\"2999-01-01\",\"tipoSanguineo\":\"A_POSITIVO\"}",
            "{\"nome\":\"Teste\",\"tipoSanguineo\":\"A_POSITIVO\"}",
            "{\"nome\":\"Teste\",\"dataNascimento\":\"1990-01-01\"}"
    })
    void preservaValidacoesDeDoador(String corpo) throws Exception {
        mvc.perform(post("/api/cadastros/doadores").contentType("application/json").content(corpo))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem", not(emptyString())));
        verify(doadores, never()).save(any());
    }

    @Test
    void payloadReportadoPreservaTipoColetaSemInventarCnpj() throws Exception {
        // Diagnóstico com repositório simulado: não comprova inserção no PostgreSQL.
        mvc.perform(post("/api/cadastros/unidades-coleta").contentType("application/json")
                        .content("{\"nome\":\"Upa\",\"cidade\":\"Imbiribeira\",\"endereco\":\"dsdsds\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Upa"))
                .andExpect(jsonPath("$.cidade").value("Imbiribeira"))
                .andExpect(jsonPath("$.endereco").value("dsdsds"));
        UnidadeColeta unidade = unidadesSalvas.get(1L);
        assertEquals(UnidadeColeta.class, unidade.getClass());
        assertNull(unidade.getTelefone());
        assertThrows(NoSuchMethodException.class, () -> UnidadeColeta.class.getMethod("setCnpj", String.class));
        assertEquals("SINGLE_TABLE", Localidade.class.getAnnotation(jakarta.persistence.Inheritance.class).strategy().name());
        assertTrue(UnidadeSaude.class.getDeclaredField("cnpj").getAnnotation(jakarta.persistence.Column.class).nullable());
        assertNull(Localidade.class.getDeclaredField("telefone").getAnnotation(jakarta.persistence.Column.class));
    }

    @Test
    void rejeitaUnidadeSemNome() throws Exception {
        mvc.perform(post("/api/cadastros/unidades-coleta").contentType("application/json").content("{\"nome\":\" \"}"))
                .andExpect(status().isBadRequest());
        verify(unidades, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"dataHora\":\"2026-10-21T10:00:00\",\"quantidade\":0,\"doadorId\":1,\"unidadeColetaId\":1}",
            "{\"dataHora\":\"2026-10-21T10:00:00\",\"quantidade\":-1,\"doadorId\":1,\"unidadeColetaId\":1}",
            "{\"dataHora\":\"2026-10-21T10:00:00\",\"doadorId\":1,\"unidadeColetaId\":1}",
            "{\"quantidade\":100,\"doadorId\":1,\"unidadeColetaId\":1}",
            "{\"dataHora\":\"2026-10-21T10:00:00\",\"quantidade\":100,\"unidadeColetaId\":1}",
            "{\"dataHora\":\"2026-10-21T10:00:00\",\"quantidade\":100,\"doadorId\":1}"
    })
    void rejeitaDoacaoComCamposInvalidos(String corpo) throws Exception {
        mvc.perform(post("/api/cadastros/doacoes").contentType("application/json").content(corpo))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem", not(emptyString())));
        verify(doacoes, never()).save(any());
    }

    @Test
    void rejeitaVinculosInexistentesSemSalvarDoacao() throws Exception {
        criarDoadorEUnidade();
        for (String ids : List.of("\"doadorId\":999,\"unidadeColetaId\":1", "\"doadorId\":1,\"unidadeColetaId\":999")) {
            mvc.perform(post("/api/cadastros/doacoes").contentType("application/json")
                            .content("{\"quantidade\":100,\"dataHora\":\"2026-10-21T10:00:00\"," + ids + "}"))
                    .andExpect(status().isBadRequest());
        }
        verify(doacoes, never()).save(any());
    }

    @Test
    void representacaoTextualDosDtosOcultaDadosPessoais() {
        var entrada = new CadastrosDTO.NovoDoador("Nome privado teste",
                java.time.LocalDate.of(1990, 1, 1), TipoSanguineo.A_POSITIVO);
        var saida = new CadastrosDTO.DoadorResumo(1L, "Nome privado teste", TipoSanguineo.A_POSITIVO);
        assertFalse(entrada.toString().contains("Nome privado teste"));
        assertFalse(entrada.toString().contains("1990"));
        assertFalse(saida.toString().contains("Nome privado teste"));
        assertFalse(saida.toString().contains("A_POSITIVO"));
    }

    @Test
    void erroDeFormatoNaoEcoaDadosDoCorpo() throws Exception {
        mvc.perform(post("/api/cadastros/doadores").contentType("application/json")
                        .content("{\"nome\":\"Nome privado teste\",\"tipoSanguineo\":\"INVALIDO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("Nome privado teste"))))
                .andExpect(content().string(not(containsString("INVALIDO"))));
    }
}
