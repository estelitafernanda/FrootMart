package com.ufrn.frootmart.model;

import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class Fruit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank(message = "Por favor, preencha o campo nome.")
    //@Size(max = 100)
    private String name;

    //@NotBlank(message = "Por favor, preencha o campo descrição.")
    private String description;

    //@NotNull(message = "Por favor, preencha o campo preço.")
    private Double price;

    //@NotBlank(message = "Por favor, preencha o campo imagem.")
    private String image_uri;

    @Column
    private Date isDeleted;

    //@NotBlank(message = "Por favor, preencha o campo categoria.")
    private String category;
}
