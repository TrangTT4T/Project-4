package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    private User user;
    private List<UserOrder> orders;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        user = TestUtils.createUser(9L, "trangtran", new Cart());
        Cart cart = TestUtils.createCart(user);
        orders = TestUtils.createOrder(9L, user);
    }

    @Test
    public void happy_case_submit_order_successfully() {
        when(userRepository.findByUsername("trangtran")).thenReturn(user);

        ResponseEntity<UserOrder> responseEntity = orderController.submit("trangtran");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user.getId(), Objects.requireNonNull(responseEntity.getBody()).getUser().getId());
    }

    @Test
    public void negative_case_submit_order_unsuccessfully_because_user_not_found() {
        when(userRepository.findByUsername("notfounduser")).thenReturn(null);

        ResponseEntity<UserOrder> responseEntity = orderController.submit("notfounduser");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void happy_case_get_orders_information_for_user_successfully() {
        when(userRepository.findByUsername("trangtran")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("trangtran");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
    }
}
