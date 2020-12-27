package com.saimon.entity;

import com.saimon.validator.EmailList;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "product_info")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

//    @NotBlank
//    @EmailList(message = "Provide a valid mail")
//    private String email;

    @NotBlank
    private String brand;

    private String madeIn;

    @NotNull
    private double price;


}
