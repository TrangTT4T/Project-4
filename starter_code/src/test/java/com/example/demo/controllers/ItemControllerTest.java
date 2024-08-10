package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        itemController = new ItemController();
        item1 = TestUtils.createItem(9L, "Item Name 1", new BigDecimal("9.99"), "Item Description 1");
        item2 = TestUtils.createItem(99L, "Item Name 2", new BigDecimal("99.99"), "Item Description 2");
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void happy_case_get_all_items_successfully() {
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).size());
    }

    @Test
    public void happy_case_get_an_item_by_id_successfully() {
        when(itemRepository.findById(9L)).thenReturn(Optional.of(item1));
        ResponseEntity<Item> responseEntity = itemController.getItemById(9L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(item1.getId(), Objects.requireNonNull(responseEntity.getBody()).getId());
    }

    @Test
    public void negative_case_get_an_item_unsuccessfully_because_id_not_found() {
        when(itemRepository.findById(9L)).thenReturn(Optional.empty());
        ResponseEntity<Item> responseEntity = itemController.getItemById(9L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void happy_case_get_items_by_name_successfully() {
        when(itemRepository.findByName("Item Name 1")).thenReturn(List.of(item1));
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("Item Name 1");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(item1.getName(), Objects.requireNonNull(responseEntity.getBody()).get(0).getName());
    }

    @Test
    public void negative_case_get_items_unsuccessfully_because_name_not_found() {
        when(itemRepository.findByName("Item Not Found")).thenReturn(new ArrayList<>());
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("Item Not Found");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}
