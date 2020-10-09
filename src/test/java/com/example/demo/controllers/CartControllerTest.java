package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.security.SaltGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void init(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.createUser(userRepository);

        createItem();
    }

    @Test
    public void addToCart(){
        // Add item for valid user and existing item
        ModifyCartRequest modifyCartRequest = ModifyCartRequest
                .builder()
                .itemId(1L)
                .quantity(20)
                .username("user2")
                .build();
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(OK.value(), cartResponseEntity.getStatusCodeValue());

        // Add item for invalid user and existing item
        modifyCartRequest = ModifyCartRequest
                .builder()
                .itemId(1L)
                .quantity(20)
                .username("user23")
                .build();
        cartResponseEntity = cartController.addTocart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND.value(), cartResponseEntity.getStatusCodeValue());

        // Add item for valid user and non-existing item
        modifyCartRequest = ModifyCartRequest
                .builder()
                .itemId(20L)
                .quantity(20)
                .username("user2")
                .build();
        cartResponseEntity = cartController.addTocart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND.value(), cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeItemFromCart() {
        // remove item for valid user and existing item
        ModifyCartRequest modifyCartRequest = ModifyCartRequest
                .builder()
                .itemId(1L)
                .quantity(20)
                .username("user2")
                .build();
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(OK.value(), cartResponseEntity.getStatusCodeValue());

        // remove item for valid user and non-existing item
        modifyCartRequest = ModifyCartRequest
                .builder()
                .itemId(10L)
                .quantity(20)
                .username("user2")
                .build();
        cartResponseEntity = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND.value(), cartResponseEntity.getStatusCodeValue());
    }

    private void createItem(){
        Item item = Item.builder()
                .id(1L)
                .name("Books")
                .description("LongMan Enterprise")
                .price(new BigDecimal("230.22"))
                .build();
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findByName("Books")).thenReturn(Collections.singletonList(item));
        itemRepository.save(item);
    }
}
