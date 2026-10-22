package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.CadastrosDTO.*;
import com.hemobit.hemobit.service.CadastroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cadastros")
public class CadastroApiController {
    private final CadastroService service;
    public CadastroApiController(CadastroService service) { this.service = service; }

    @GetMapping("/doadores")
    public List<DoadorResumo> doadores() { return service.listarDoadores(); }
    @PostMapping("/doadores")
    @ResponseStatus(HttpStatus.CREATED)
    public DoadorResumo criarDoador(@RequestBody NovoDoador dados) { return service.criarDoador(dados); }
    @GetMapping("/unidades-coleta")
    public List<UnidadeResumo> unidades() { return service.listarUnidades(); }
    @PostMapping("/unidades-coleta")
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadeResumo criarUnidade(@RequestBody NovaUnidade dados) { return service.criarUnidade(dados); }
    @GetMapping("/doacoes")
    public List<DoacaoResumo> doacoes() { return service.listarDoacoes(); }
    @PostMapping("/doacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public DoacaoResumo criarDoacao(@RequestBody NovaDoacao dados) { return service.criarDoacao(dados); }
    @GetMapping("/doacoes/opcoes")
    public OpcoesDoacao opcoes() { return service.opcoesDoacao(); }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalido(IllegalArgumentException erro) {
        return Map.of("mensagem", erro.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> formatoInvalido() {
        return Map.of("mensagem", "Dados inválidos. Verifique os campos, as datas e o tipo sanguíneo.");
    }
}
