package com.hemobit.hemobit.controller;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("paginaAtual", "dashboard");
        model.addAttribute("estoqueDemo", List.of(
            Map.of("tipo", "O+", "quantidade", 62, "percentual", 89),
            Map.of("tipo", "A+", "quantidade", 54, "percentual", 77),
            Map.of("tipo", "B+", "quantidade", 38, "percentual", 54),
            Map.of("tipo", "AB+", "quantidade", 21, "percentual", 30),
            Map.of("tipo", "O−", "quantidade", 8, "percentual", 11),
            Map.of("tipo", "A−", "quantidade", 29, "percentual", 41),
            Map.of("tipo", "B−", "quantidade", 19, "percentual", 27),
            Map.of("tipo", "AB−", "quantidade", 17, "percentual", 24)
        ));
        return "dashboard";
    }
}
