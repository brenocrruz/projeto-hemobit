package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.TipoSanguineo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CadastroPageController {
    @GetMapping("/cadastros/unidades-saude")
    public String unidadesSaude(Model model) {
        model.addAttribute("paginaAtual", "unidades-saude");
        return "unidades-saude";
    }

    @GetMapping("/cadastros/doadores")
    public String doadores(Model model) {
        model.addAttribute("paginaAtual", "doadores");
        model.addAttribute("tiposSanguineos", TipoSanguineo.values());
        return "doadores";
    }
    @GetMapping("/cadastros/unidades-coleta")
    public String unidades(Model model) {
        model.addAttribute("paginaAtual", "unidades-coleta");
        return "unidades-coleta";
    }
    @GetMapping("/cadastros/doacoes")
    public String doacoes(Model model) {
        model.addAttribute("paginaAtual", "doacoes");
        return "doacoes";
    }
}
