package com.squares.products.controllers;

import com.squares.products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private final ProductRepository productRepository;


}
