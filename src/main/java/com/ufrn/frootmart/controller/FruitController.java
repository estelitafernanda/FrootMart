package com.ufrn.frootmart.controller;

import com.ufrn.frootmart.model.Fruit;
import com.ufrn.frootmart.repository.FruitRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class FruitController {

    @Autowired
    private FruitRepository fruitRepository;

    @GetMapping("/index")
    public String index(Model model, HttpServletResponse response) {
        List<Fruit> fruits = fruitRepository.findByIsDeletedNull();
        model.addAttribute("fruits", fruits);
        model.addAttribute("pageTitle", "Lista de Frutas");

        // Formata a data e hora em um formato seguro para cookies
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String formattedDate = sdf.format(new Date());

        // Adiciona o cookie de visita com data e hora do acesso
        Cookie cookie = new Cookie("visita", formattedDate);
        cookie.setMaxAge(24 * 60 * 60); // 24 horas
        cookie.setPath("/"); // Disponível em toda a aplicação
        response.addCookie(cookie);

        return "index";
    }
}
