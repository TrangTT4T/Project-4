package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private ItemRepository itemRepository;

	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		List<Item> items = itemRepository.findAll();
		if (items.isEmpty()) {
			logger.info("No items found in the database.");
			return ResponseEntity.notFound().build();
		}
		logger.info("Retrieved all items from the database.");
		return ResponseEntity.ok(items);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Optional<Item> item = itemRepository.findById(id);
		if (item.isEmpty()) {
			logger.info("Item not found for ID: {}", id);
			return ResponseEntity.notFound().build();
		}
		logger.info("Retrieved item with ID: {}", id);
		return ResponseEntity.of(item);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);
		if (items.isEmpty()) {
			logger.info("No items found with name: {}", name);
			return ResponseEntity.notFound().build();
		}
		logger.info("Retrieved {} items with name: {}", items.size(), name);
		return ResponseEntity.ok(items);
	}
	
}
