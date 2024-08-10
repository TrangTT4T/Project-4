package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static void injectObjects(Object target, String fieldName, Object toInject){

        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if (!f.canAccess(target)){
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if (wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createUser(Long id, String username, Cart cart) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setCart(new Cart());
        return user;
    }

    public static Item createItem(Long id, String name, BigDecimal price, String description) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        return item;
    }

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        user.setCart(cart);
        return cart;
    }

    public static List<UserOrder> createOrder(Long id, User user) {
        List<UserOrder> orders = new ArrayList<>();
        UserOrder order = new UserOrder();
        order.setId(id);
        order.setUser(user);
        order.setItems(new ArrayList<>());
        orders.add(order);
        return orders;
    }
}
