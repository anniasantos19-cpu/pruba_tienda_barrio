package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioMvcController {
    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
