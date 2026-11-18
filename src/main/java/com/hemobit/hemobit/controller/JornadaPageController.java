package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Prioridade;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JornadaPageController {
    @GetMapping("/atendimento")
    public String atendimento(Model model) {
        model.addAttribute("paginaAtual", "atendimento");
        model.addAttribute("tipos", TipoHemocomponente.values());
        model.addAttribute("tiposSanguineos", TipoSanguineo.values());
        model.addAttribute("prioridades", Prioridade.values());
        return "atendimento";
    }

    @GetMapping("/logistica/rotas")
    public String rotas(Model model) {
        model.addAttribute("paginaAtual", "rotas");
        return "rotas";
    }
}
