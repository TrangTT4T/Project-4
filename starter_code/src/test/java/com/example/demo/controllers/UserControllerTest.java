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
    public void create_user_happy_path() {
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
    public void create_user_password_and_confirm_password_mismatch() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setConfirmPassword("differentpassword");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void create_user_existing_username() {
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
    public void find_user_by_id_not_found() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_username_not_found() {
        final ResponseEntity<User> response = userController.findByUserName("nonexistentUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_username_found() {
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
    public void create_user_short_password_length_less_than_7() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("short");
        r.setConfirmPassword("short");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

}
