package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Cart cart;
    private UserOrder order;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(19.99));
        item.setDescription("A test item");

        cart = new Cart();
        cart.setId(1L);
        cart.setItems(Collections.singletonList(item)); // Thêm danh sách items
        cart.setTotal(BigDecimal.valueOf(19.99));

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setCart(cart);

        order = UserOrder.createFromCart(cart);
        order.setId(1L);
    }

    @Test
    void testSubmitOrder_UserExists() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // When
        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        // Then
        assertEquals(OK, response.getStatusCode());
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    void testSubmitOrder_UserNotFound() {
        // Given
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        // When
        ResponseEntity<UserOrder> response = orderController.submit("unknownUser");

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testGetOrdersForUser_UserExists() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(order));

        // When
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(order, response.getBody().get(0));
    }

    @Test
    void testGetOrdersForUser_UserNotFound() {
        // Given
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        // When
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("unknownUser");

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}