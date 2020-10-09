package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @BeforeEach
    public void init(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        TestUtils.createUser(userRepository);
        creatCart();
    }

    private void creatCart() {
        User user = userRepository.findByUsername("user2");

        Item item = Item.builder()
                .id(1L)
                .name("LongMan Dictionary")
                .description("A very good dictionary")
                .price(new BigDecimal("230.33"))
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Hand Bowl")
                .description("Blue color hand bowl with sticker")
                .price(new BigDecimal("3.077"))
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        cart.addItem(item);
        cart.addItem(item2);

        user.setCart(cart);

        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByUsername("user2")).thenReturn(user);
        when(orderRepository.findByUser(any())).thenReturn(Collections.singletonList(
                UserOrder.builder()
                        .items(cart.getItems())
                        .user(user)
                        .id(1L)
                        .total(cart.getTotal())
                        .build()
        ));

        userRepository.save(user);
    }

    @Test
    public void submitUser(){
        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit("user2");
        assertNotNull(userOrderResponseEntity);

        UserOrder userOrder = userOrderResponseEntity.getBody();
        assertNotNull(userOrder);

        assertEquals(2, userOrder.getItems().size());
        assertEquals(new BigDecimal("233.407"), userOrder.getTotal());
        assertEquals(1L, userOrder.getUser().getId());
    }

    @Test
    public void getUserOrders() {
        ResponseEntity<List<UserOrder>> userOrderResponseEntity = orderController.getOrdersForUser("user2");
        assertNotNull(userOrderResponseEntity);

        List<UserOrder> userOrders = userOrderResponseEntity.getBody();
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());

        UserOrder userOrder = userOrders.get(0);
        assertEquals(2, userOrder.getItems().size());
        assertEquals(new BigDecimal("233.407"), userOrder.getTotal());
        assertEquals(1L, userOrder.getUser().getId());

    }
}
