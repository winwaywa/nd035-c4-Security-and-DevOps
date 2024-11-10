package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(19.99));
        item.setDescription("A test item");
    }

    @Test
    void testGetItems_Success() {
        // Given
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item));

        // When
        ResponseEntity<List<Item>> response = itemController.getItems();

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(item, response.getBody().get(0));
    }

    @Test
    void testGetItemById_ItemExists() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // When
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(item, response.getBody());
    }

    @Test
    void testGetItemById_ItemNotFound() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetItemsByName_ItemsFound() {
        // Given
        when(itemRepository.findByName("Test Item")).thenReturn(Arrays.asList(item));

        // When
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Test Item");

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(item, response.getBody().get(0));
    }

    @Test
    void testGetItemsByName_ItemsNotFound() {
        // Given
        when(itemRepository.findByName("Unknown Item")).thenReturn(null);

        // When
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Unknown Item");

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}