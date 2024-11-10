package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        // Mock User
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPassword");
    }

    @Test
    void testFindById_UserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<User> response = userController.findById(1L);

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.findById(1L);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFindByUserName_UserExists() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // When
        ResponseEntity<User> response = userController.findByUserName("testUser");

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testFindByUserName_UserNotFound() {
        // Given
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        // When
        ResponseEntity<User> response = userController.findByUserName("unknownUser");

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateUser_InvalidPasswordLength() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("123");
        request.setConfirmPassword("123");

        // When
        ResponseEntity<User> response = userController.createUser(request);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testCreateUser_PasswordsDoNotMatch() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("password123");
        request.setConfirmPassword("password456");

        // When
        ResponseEntity<User> response = userController.createUser(request);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testCreateUser_Success() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPassword");

        // When
        ResponseEntity<User> response = userController.createUser(request);

        // Then
        assertEquals(OK, response.getStatusCode());
        User createdUser = response.getBody();
        assertNotNull(createdUser);
        assertEquals("testUser", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
}