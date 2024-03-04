package org.glovo.glovo_db.service;

import lombok.AllArgsConstructor;
import org.glovo.glovo_db.exception.OrderNotFoundException;
import org.glovo.glovo_db.exception.ProductNotFoundException;
import org.glovo.glovo_db.model.Order;
import org.glovo.glovo_db.model.OrderUpdateRequest;
import org.glovo.glovo_db.model.Product;
import org.glovo.glovo_db.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    public Order createOrder(Order order) {
        for (Product product : order.getProducts()) {
            productService.addProduct(product);
        }
        return orderRepository.save(order);
    }

    public void updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) {
        Order existingOrder = getOrderById(id);

        existingOrder.setModificationDateTime(new Date());
        existingOrder.setTotalAmount(orderUpdateRequest.getTotalAmount());
        existingOrder.setMobile(orderUpdateRequest.getMobile());

        recalculateOrderTotal(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);

        for (Product product : order.getProducts()) {
            productService.addProduct(product);
        }
        orderRepository.delete(order);
    }

    public void addProductToOrder(Long id, Product product) {
        Order order = getOrderById(id);
        productService.addProduct(product);
        order.getProducts().add(product);
        orderRepository.save(order);
    }

    public void removeProductFromOrder(Long orderId, Long productId) {
        Order order = getOrderById(orderId);
        Product productToRemove = order.getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found in order with ID: " + productId));

        productService.addProduct(productToRemove);
        order.getProducts().remove(productToRemove);

        orderRepository.save(order);
    }

    public void recalculateOrderTotal(Order order) {
        double totalAmount = order.getProducts().stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

}
