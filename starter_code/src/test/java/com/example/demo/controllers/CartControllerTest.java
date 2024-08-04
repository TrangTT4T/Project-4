package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_happy_path() {
        User user = createUser();
        Item item = createItem();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart updatedCart = response.getBody();
        assertEquals(user.getCart().getItems().size(), updatedCart.getItems().size());
        assertEquals(user.getCart().getTotal(), updatedCart.getTotal());
    }

    @Test
    public void add_to_cart_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistent_user");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = createUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart_happy_path() {
        User user = createUser();
        Item item = createItem();

        user.getCart().addItem(item);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart updatedCart = response.getBody();
        assertEquals(user.getCart().getItems().size(), updatedCart.getItems().size());
        assertEquals(user.getCart().getTotal(), updatedCart.getTotal());
    }

    @Test
    public void remove_from_cart_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistent_user");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart_item_not_found() {
        User user = createUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Helper methods to create User and Item objects
    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");
        user.setCart(new Cart());
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("Test Description");
        return item;
    }
}
