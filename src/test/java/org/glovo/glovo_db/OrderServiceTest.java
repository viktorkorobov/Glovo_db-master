package org.glovo.glovo_db;

import org.glovo.glovo_db.model.Order;
import org.glovo.glovo_db.model.Product;
import org.glovo.glovo_db.repository.OrderRepository;
import org.glovo.glovo_db.service.OrderService;
import org.glovo.glovo_db.service.ProductService;
import org.glovo.glovo_db.model.OrderUpdateRequest;
import org.glovo.glovo_db.exception.OrderNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;


@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testGetAllOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        when(orderRepository.findAll()).thenReturn(orders);
        List<Order> result = orderService.getAllOrders();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetOrderById() {
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Order result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test
    public void testGetOrderById_OrderNotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

   @Test
    public void testCreateOrder() {
       List<Product> products = new ArrayList<>();
       products.add(new Product());
       Order order = Order.builder()
               .id(1L)
               .mobile("0501122334")
               .creationDateTime(new Date())
               .products(products)
               .build();
        when(orderRepository.save(order)).thenReturn(order);
        Order createdOrder = orderService.createOrder(order);
        assertEquals(order, createdOrder);
    }


    @Test
    public void testUpdateOrder() {
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L,"Product 1", 50.0, 2));
        existingOrder.setProducts(products);
        OrderUpdateRequest orderUpdate = new OrderUpdateRequest();
        orderUpdate.setMobile("1234567890");
        orderUpdate.setTotalAmount(100.0);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        orderService.updateOrder(orderId, orderUpdate);
        verify(orderRepository, times(1)).findById(orderId);
        assertEquals("1234567890", existingOrder.getMobile());
        assertEquals(100.0, existingOrder.getTotalAmount());
    }
    @Test
    public void testDeleteOrder() {
        Long orderId = 1L;
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L,"Product 1", 10.0, 1));
        Order order = new Order();
        order.setId(orderId);
        order.setProducts(products);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        orderService.deleteOrder(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    public void testAddProductToOrder() {
        Long orderId = 1L;
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        Order order = new Order();
        order.setId(orderId);
        order.setProducts(products);
        Product product = new Product();
        product.setId(1L);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        orderService.addProductToOrder(orderId, product);
        assertTrue(order.getProducts().contains(product));
        verify(productService, times(1)).addProduct(product);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void testAddProductToOrder_OrderNotFound() {
        Long orderId = 1L;
        Product product = new Product();
        product.setId(1L);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.addProductToOrder(orderId, product));
        verify(productService, never()).addProduct(product);
        verify(orderRepository, never()).save(any());
    }

        @Test
        public void testRemoveProductFromOrder() {
            Long orderId = 1L;
            Long productId = 1L;
            List<Product> products = new ArrayList<>();
            Product product = new Product(productId,"Product 1", 10.0, 1);
            products.add(product);
            Order order = new Order();
            order.setId(orderId);
            order.setProducts(products);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            orderService.removeProductFromOrder(orderId, productId);
            assertFalse(order.getProducts().contains(product));
            verify(productService, times(1)).addProduct(product);
            verify(orderRepository, times(1)).save(order);
        }

        @Test
        public void testRemoveProductFromOrder_ProductNotFound() {
            Long productId = 2L;
            Long orderId = 1L;
            List<Product> products = new ArrayList<>();
            products.add(new Product(2L,"Product 1", 10.0, 1));
            Order order = new Order();
            order.setId(orderId);
            order.setProducts(products);
            Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            orderService.removeProductFromOrder(orderId, productId);
        }

    @Test
    public void testRemoveProductFromOrder_OrderNotFound() {
        Long orderId = 1L;
        Long productId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.removeProductFromOrder(orderId, productId));
        verify(productService, never()).addProduct(any());
        verify(orderRepository, never()).save(any());
    }

}
