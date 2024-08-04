package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void happy_case_create_user_successfully() {
        when(encoder.encode("password")).thenReturn("hasspassword");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setConfirmPassword("password");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("hasspassword", u.getPassword());
    }

    @Test
    public void negative_case_create_user_unsuccessfully_because_mismatch_password_and_confirm_password() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setConfirmPassword("differentpassword");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void negative_case_create_user_unsuccessfully_because_username_exist() {
        when(userRepo.findByUsername("existingUser")).thenReturn(new User());
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("existingUser");
        r.setPassword("password");
        r.setConfirmPassword("password");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void negative_case_find_user_unsuccessfully_because_id_not_found() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void happy_case_find_user_by_username_exist_successfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(userRepo.findByUsername("testUser")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("testUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    public void negative_case_create_user_unsuccessfully_because_password_length_less_than_7() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("short");
        r.setConfirmPassword("short");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

}
