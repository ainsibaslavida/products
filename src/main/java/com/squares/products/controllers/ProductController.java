package com.squares.products.controllers;

import com.squares.products.dtos.ProductRecordDTO;
import com.squares.products.models.ProductModel;
import com.squares.products.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private final ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDTO productRecordDTO) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productsList = this.productRepository.findAll();

        if (!productsList.isEmpty()) {
            for (ProductModel product : productsList) {
                String id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id))
                		.withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") String productId) {
        Optional<ProductModel> product = productRepository.findById(productId);

        if (product.isPresent()) {
            ProductModel productsList = product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products list"));
        }

        return product.<ResponseEntity<Object>>map(productModel -> ResponseEntity.status(HttpStatus.OK).body(productModel)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found."));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(name = "id") String id, @RequestBody @Valid ProductRecordDTO productRecordDTO) {
        Optional<ProductModel> product = this.productRepository.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        var productModel = product.get();

        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(name = "id") String productId) {
        Optional<ProductModel> product = this.productRepository.findById(productId);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        this.productRepository.deleteById(productId);

        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
    }
}
