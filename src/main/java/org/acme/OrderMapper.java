package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.input.Order;
import org.acme.model.output.ProcessedOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderMapper {
    
    public ProcessedOrder map(Order order) {
        ProcessedOrder processedOrder = new ProcessedOrder();
        
        // Set order reference
        processedOrder.setOrderReference(order.getOrderId());
        
        // Map customer details
        processedOrder.setCustomerDetails(new ProcessedOrder.CustomerDetails());
        processedOrder.getCustomerDetails().setCustomerName(order.getCustomer().getName());
        processedOrder.getCustomerDetails().setCustomerEmail(order.getCustomer().getEmail());
        
        // Map order items
        processedOrder.setOrderItems(new ProcessedOrder.OrderItems());
        processedOrder.getOrderItems().getOrderItem().addAll(
            order.getItems().getItem().stream()
                .map(item -> {
                    ProcessedOrder.OrderItems.OrderItem orderItem = new ProcessedOrder.OrderItems.OrderItem();
                    orderItem.setSku(item.getProductId());
                    orderItem.setOrderedQuantity(item.getQuantity());
                    
                    // Format decimal numbers with 2 decimal places
                    BigDecimal unitPrice = item.getPrice().setScale(2, RoundingMode.HALF_UP);
                    orderItem.setUnitPrice(unitPrice);
                    
                    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity().intValue()))
                        .setScale(2, RoundingMode.HALF_UP);
                    orderItem.setTotalPrice(totalPrice);
                    
                    return orderItem;
                })
                .collect(Collectors.toList())
        );
        
        // Calculate total order value
        BigDecimal totalOrderValue = processedOrder.getOrderItems().getOrderItem().stream()
            .map(item -> item.getTotalPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
        
        processedOrder.setTotalOrderValue(totalOrderValue);
        
        return processedOrder;
    }
} 