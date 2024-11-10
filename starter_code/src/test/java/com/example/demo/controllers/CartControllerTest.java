package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Cart cart;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        // Create mock User
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        // Create mock Cart
        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);

        // Create mock Item
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.0));
    }

    @Test
    void testAddToCart_UserNotFound() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        // When
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testAddToCart_ItemNotFound() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        // When
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testAddToCart_Success() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        // When
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getItems().size());
        assertTrue(response.getBody().getItems().containsAll(
                IntStream.range(0, 2).mapToObj(i -> item).collect(Collectors.toList())
        ));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testRemoveFromCart_UserNotFound() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        // When
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testRemoveFromCart_ItemNotFound() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        // When
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testRemoveFromCart_Success() {
        // Given
        cart.addItem(item);
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        // When
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }
}