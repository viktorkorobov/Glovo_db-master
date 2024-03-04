package org.glovo.glovo_db;

import org.glovo.glovo_db.exception.ProductNotFoundException;
import org.glovo.glovo_db.repository.ProductRepository;
import org.glovo.glovo_db.service.ProductService;
import org.glovo.glovo_db.model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testGetProductById() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product result = productService.getProductById(productId);
        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    public void testAddProduct() {
        Product product = new Product();
        productService.addProduct(product);
        verify(productRepository, times(1)).save(product);
    }
}
