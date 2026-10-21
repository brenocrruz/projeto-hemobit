package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstoqueController {
    @GetMapping("/estoque")
    public String estoque(Model model) {
        model.addAttribute("paginaAtual", "estoque");
        model.addAttribute("tipos", TipoHemocomponente.values());
        model.addAttribute("statusDisponiveis", StatusHemocomponente.values());
        return "estoque";
    }
}
