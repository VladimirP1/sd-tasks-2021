package ru.akirakozov.sd.refactoring.model;

public class Product {
    public Product(String name, long price) {
        this.name = name;
        this.price = price;
    }

    public final String name;
    public final long price;
}
