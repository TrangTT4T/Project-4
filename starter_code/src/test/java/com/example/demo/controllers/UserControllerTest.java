package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    private UserController userController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BCryptPasswordEncoder encoder;

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void happy_case_create_user_successfully() {
        when(encoder.encode("password")).thenReturn("hasspassword");

        CreateUserRequest request = new CreateUserRequest("test", "password", "password");
        ResponseEntity<User> responseEntity = userController.createUser(request);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("test", Objects.requireNonNull(responseEntity.getBody()).getUsername());
        assertEquals("hasspassword", responseEntity.getBody().getPassword());
    }

    @Test
    public void negative_case_create_user_unsuccessfully_because_mismatch_password_and_confirm_password() {
        CreateUserRequest request = new CreateUserRequest("test", "password", "differentpassword");
        ResponseEntity<User> responseEntity = userController.createUser(request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void negative_case_create_user_unsuccessfully_because_username_exist() {
        when(userRepository.findByUsername("existingUser")).thenReturn(new User());

        CreateUserRequest request = new CreateUserRequest("existingUser", "password", "password");
        ResponseEntity<User> responseEntity = userController.createUser(request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void negative_case_find_user_unsuccessfully_because_id_not_found() {
        ResponseEntity<User> responseEntity = userController.findById(9L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void happy_case_find_user_by_username_exist_successfully() {
        User user = new User(9L, "test");
        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<User> responseEntity = userController.findByUserName("test");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("test", Objects.requireNonNull(responseEntity.getBody()).getUsername());
    }

    @Test
    public void negative_case_create_user_unsuccessfully_because_password_length_less_than_7() {
        CreateUserRequest request = new CreateUserRequest("test", "short", "short");
        ResponseEntity<User> responseEntity = userController.createUser(request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
