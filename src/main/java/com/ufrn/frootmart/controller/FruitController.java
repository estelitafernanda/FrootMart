package com.ufrn.frootmart.controller;

import com.ufrn.frootmart.model.Fruit;
import com.ufrn.frootmart.repository.FruitRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@SessionAttributes("cart")
public class FruitController {

    private static final String UPLOADED_FOLDER = "/src/main/webapp/WEB-INF/images/";

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
    public String adicionarCarrinho(@RequestParam("id") Long id, @ModelAttribute("cart") List<Fruit> cart, RedirectAttributes redirectAttributes) {
        Fruit fruit = fruitRepository.findById(id).orElse(null);
        if (fruit != null) {
            cart.add(fruit);
            redirectAttributes.addFlashAttribute("msg", "Item adicionado ao carrinho");
        }
        return "redirect:/index";
    }


    @GetMapping("/verCarrinho")
    public String verCarrinho(@ModelAttribute("cart") List<Fruit> cart, Model model, RedirectAttributes redirectAttributes) {
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "Não existem itens no carrinho.");
            return "redirect:/index";
        }
        model.addAttribute("cart", cart);
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
    public String deletar(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Fruit fruit = fruitRepository.findById(id).orElse(null);
        if (fruit != null) {
            fruit.setIsDeleted(new Date());
            fruitRepository.save(fruit);
            redirectAttributes.addFlashAttribute("msg", "Remoção realizada com sucesso");
        }
        return "redirect:/admin";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("fruit", new Fruit());
        return "cadastro";
    }

    @PostMapping("/salvar/{editar_ou_cadastrar}")
    public ModelAndView processSave(
            @ModelAttribute  Fruit fruit, BindingResult result,
            @RequestParam("file") MultipartFile file, Errors errors,
            @PathVariable String editar_ou_cadastrar,
            RedirectAttributes redirectAttributes) {

        // Verifica se há erros após o upload do arquivo
        if (result.hasErrors()) {
            System.out.println("Erros de validação: " + result.getAllErrors());
            return new ModelAndView(editar_ou_cadastrar.equals("edit") ? "editar" : "cadastro").addObject("fruit", fruit);
        }

        if (Objects.equals(editar_ou_cadastrar, "edit")) {
            // Pega o id do fruit que foi passado por parâmetro na URL
            Optional<Fruit> fruit_b = fruitRepository.findById(fruit.getId());

            // Modifica com as novas informações pegas do HTML caso o fruit exista
            if (fruit_b.isPresent()) {
                fruitRepository.save(fruit);
                System.out.println("Editou");
            }
        }

        if (Objects.equals(editar_ou_cadastrar, "cad")) {
            // Gerar um UUID para o nome da imagem
            String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // Salvando a imagem com um nome único
            fruit.setImage_uri(uniqueFilename);
            saveFile(file, uniqueFilename);

            // Salva o objeto no banco de dados com as informações pegas do HTML
            fruitRepository.save(fruit);
            System.out.println("Cadastrou");
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/admin");
        redirectAttributes.addFlashAttribute("msg", "Cadastro/Atualização realizado(a) com sucesso");
        modelAndView.addObject("fruits", fruitRepository.findByIsDeletedNull());
        return modelAndView;
    }

    @GetMapping("/produtoDetails/{id}")
    public String getProdutoDetails(@PathVariable("id") Long id, Model model) {
        Optional<Fruit> fruitOptional = fruitRepository.findById(id);
        if (fruitOptional.isPresent()) {
            Fruit fruit = fruitOptional.get();
            model.addAttribute("fruit", fruit);
            return "produtoDetails";
        } else {
            // Retorna uma página de erro se o produto não for encontrado
            return "error/404";
        }
    }

    private void saveFile(MultipartFile file, String uniqueFilename) {
        try {
            Path uploadPath = Paths.get(UPLOADED_FOLDER);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
        sessionStatus.setComplete();
        redirectAttributes.addFlashAttribute("msg", "Compra finalizada com sucesso");
        return "redirect:/index";
    }

}

