package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository  itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void init(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        saveItems();
    }

    private void saveItems() {
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
        List<Item> itemList = new ArrayList<>();
        Collections.addAll(itemList, item, item2);
        when(itemRepository.findAll()).thenReturn(itemList);
        when(itemRepository.findByName("LongMan Dictionary")).thenReturn(Collections.singletonList(item));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        itemRepository.saveAll(itemList);
    }

    @Test
    public void getItems(){
        ResponseEntity<List<Item>> listResponseEntity = itemController.getItems();
        assertNotNull(listResponseEntity);
        assertEquals(OK.value(), listResponseEntity.getStatusCodeValue());

        List<Item> list = listResponseEntity.getBody();
        assertNotNull(list);
        assertEquals(2, list.size());

        Item item  = list.get(0);
        assertEquals(1L, item.getId().longValue());
        assertEquals("LongMan Dictionary", item.getName());
    }

    @Test
    public void getItemByName(){
        ResponseEntity<List<Item>> listResponseEntity = itemController.getItemsByName("LongMan Dictionary");
        assertNotNull(listResponseEntity);
        assertEquals(OK.value(), listResponseEntity.getStatusCodeValue());

        List<Item> list = listResponseEntity.getBody();
        assertNotNull(list);

        Item item  = list.get(0);
        assertEquals(1L, item.getId().longValue());
        assertEquals("LongMan Dictionary", item.getName());
    }

    @Test
    public void getItemById(){
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());

        Item item  = responseEntity.getBody();
        assertNotNull(item);
        assertEquals(1L, item.getId().longValue());
        assertEquals("LongMan Dictionary", item.getName());

        responseEntity = itemController.getItemById(2L);
        assertNotNull(responseEntity);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());

        item  = responseEntity.getBody();
        assertNotNull(item);
        assertEquals(2L, item.getId().longValue());
        assertEquals("Blue color hand bowl with sticker", item.getDescription());
    }
}
