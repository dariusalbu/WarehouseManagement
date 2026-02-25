package org.example.model;

public class InsufficientStockException extends Exception {
    public InsufficientStockException() {
        super("Error! Insuficient stock!");
    }
}
