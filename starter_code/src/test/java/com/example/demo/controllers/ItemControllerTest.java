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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void happy_case_get_all_items_successfully() {
        List<Item> items = new ArrayList<>();
        items.add(createItem(1L, "Item 1", new BigDecimal("10.00"), "Description of Item 1"));
        items.add(createItem(2L, "Item 2", new BigDecimal("20.00"), "Description of Item 2"));

        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    public void happy_case_get_an_item_by_id_successfully() {
        Long itemId = 1L;
        Item item = createItem(itemId, "Item 1", new BigDecimal("10.00"), "Description of Item 1");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ResponseEntity<Item> responseEntity = itemController.getItemById(itemId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(item.getId(), responseEntity.getBody().getId());
    }

    @Test
    public void negative_case_get_an_item_unsuccessfully_because_id_not_found() {
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        ResponseEntity<Item> responseEntity = itemController.getItemById(itemId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void happy_case_get_items_by_name_successfully() {
        String itemName = "Item";
        List<Item> items = new ArrayList<>();
        items.add(createItem(1L, "Item 1", new BigDecimal("10.00"), "Description of Item 1"));
        items.add(createItem(2L, "Item 2", new BigDecimal("20.00"), "Description of Item 2"));

        when(itemRepository.findByName(itemName)).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(itemName);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    public void negative_case_get_items_unsuccessfully_because_name_not_found() {
        String itemName = "Item";

        when(itemRepository.findByName(itemName)).thenReturn(new ArrayList<>());
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(itemName);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    private Item createItem(Long id, String name, BigDecimal price, String description) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        return item;
    }
}
