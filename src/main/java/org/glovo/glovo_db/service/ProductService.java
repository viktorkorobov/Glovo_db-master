package org.glovo.glovo_db.service;

import lombok.AllArgsConstructor;
import org.glovo.glovo_db.exception.ProductNotFoundException;
import org.glovo.glovo_db.model.Product;
import org.glovo.glovo_db.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

}
