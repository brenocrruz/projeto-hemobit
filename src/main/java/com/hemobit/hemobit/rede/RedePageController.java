package com.hemobit.hemobit.rede;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedePageController {
    @GetMapping("/rede")
    public String rede(Model model) {
        model.addAttribute("paginaAtual", "rede");
        return "rede";
    }
}
