package com.ufrn.frootmart.controller;

import com.ufrn.frootmart.model.Fruit;
import com.ufrn.frootmart.repository.FruitRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@SessionAttributes("cart")
public class FruitController {

    private static final String UPLOADED_FOLDER = "C:/Users/Brito/IdeaProjects/FrootMart/src/main/resources/static/uploaded";

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
    @GetMapping("/admin")
    public String admin(Model model) {
        List<Fruit> fruits = fruitRepository.findByIsDeletedNull();
        model.addAttribute("fruits", fruits);
        return "admin";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam("id") Long id, Model model) {
        Fruit fruit = fruitRepository.findById(id).orElse(null);
        if (fruit != null) {
            model.addAttribute("fruit", fruit);
            return "editar";
        }
        return "redirect:/admin";
    }

    @GetMapping("/deletar")
    public String deletar(@RequestParam("id") Long id) {
        Fruit fruit = fruitRepository.findById(id).orElse(null);
        if (fruit != null) {
            fruit.setIsDeleted(new Date());
            fruitRepository.save(fruit);
        }
        return "redirect:/admin";
    }
    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("fruit", new Fruit());
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("fruit") Fruit fruit, BindingResult result, @RequestParam("file") MultipartFile file, Model model) {
        if (result.hasErrors()) {
            return "editar";
        }

        if (!file.isEmpty()) {
            try {
                // Definindo o caminho do diretório de upload
                String uploadDir = "/images/";
                Path uploadPath = Paths.get(uploadDir);

                // Criar o diretório se não existir
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Salvando o arquivo
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());

                // Definindo a URI da imagem
                fruit.setImage_uri("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "Falha ao salvar o arquivo");
                return "editar";
            }
        }

        fruitRepository.save(fruit);
        model.addAttribute("msg", "Atualização realizada com sucesso");
        model.addAttribute("fruits", fruitRepository.findByIsDeletedNull());
        return "redirect:/admin";
    }

}

