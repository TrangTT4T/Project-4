package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void happy_case_submit_order_successfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        user.setCart(cart);
        UserOrder order = UserOrder.createFromCart(cart);

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(orderRepo.save(order)).thenReturn(order);

        final ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder submittedOrder = response.getBody();
        assertNotNull(submittedOrder);
        assertNotNull(submittedOrder.getUser()); // Additional null check
        assertEquals(user.getId(), submittedOrder.getUser().getId());
        assertEquals(0, submittedOrder.getItems().size());
    }

    @Test
    public void negative_case_submit_order_unsuccessfully_because_user_not_found() {
        when(userRepo.findByUsername("nonexistentUser")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("nonexistentUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void happy_case_get_orders_information_for_user_successfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        List<UserOrder> orders = new ArrayList<>();
        UserOrder order1 = new UserOrder();
        order1.setId(1L);
        order1.setUser(user);
        order1.setItems(new ArrayList<>());
        orders.add(order1);

        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(orders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> retrievedOrders = response.getBody();
        assertNotNull(retrievedOrders);
        assertEquals(1, retrievedOrders.size());
        assertEquals(user.getId(), retrievedOrders.get(0).getUser().getId());
    }
}
