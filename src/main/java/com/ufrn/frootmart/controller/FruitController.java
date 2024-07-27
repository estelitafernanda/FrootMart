package com.ufrn.frootmart.controller;

import com.ufrn.frootmart.model.Fruit;
import com.ufrn.frootmart.repository.FruitRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@SessionAttributes("cart")
public class FruitController {

    @Autowired
    private FruitRepository fruitRepository;

    @ModelAttribute("cart")
    public List<Fruit> cart() {
        return new ArrayList<>();
    }

    @GetMapping("/index")
    public String index(Model model, HttpServletResponse response, @CookieValue(value = "visita", defaultValue = "") String visita) {
        List<Fruit> fruits = fruitRepository.findByIsDeletedNull();
        model.addAttribute("fruits", fruits);
        model.addAttribute("pageTitle", "Lista de Frutas");

        // Adiciona o cookie "visita" com a data e hora do acesso ao site
        if (visita.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Cookie cookie = new Cookie("visita", sdf.format(new Date()));
            cookie.setMaxAge(24 * 60 * 60); // 24 horas
            response.addCookie(cookie);
        }

        return "index";
    }

    @GetMapping("/adicionarCarrinho")
    public ModelAndView adicionarCarrinho(@RequestParam("id") Long id, @ModelAttribute("cart") List<Fruit> cart) {
        Fruit fruit = fruitRepository.findById(id).orElse(null);
        if (fruit != null) {
            cart.add(fruit);
        }
        return new ModelAndView("redirect:/index");
    }

    @GetMapping("/verCarrinho")
    public String verCarrinho(@ModelAttribute("cart") List<Fruit> cart, Model model) {
        model.addAttribute("cart", cart);
        if (cart.isEmpty()) {
            model.addAttribute("message", "Não existem itens no carrinho.");
            return "redirect:/index";
        }
        return "verCarrinho";
    }
}
