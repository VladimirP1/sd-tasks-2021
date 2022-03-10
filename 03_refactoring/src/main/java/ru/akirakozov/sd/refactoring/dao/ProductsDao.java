package ru.akirakozov.sd.refactoring.dao;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductsDao {
    int count() throws SQLException;

    List<Product> get() throws SQLException;

    Product getWithMinPrice() throws SQLException;

    Product getWithMaxPrice() throws SQLException;

    long getSumPrices() throws SQLException;

    void createTable() throws SQLException;

    void addProduct(Product p) throws SQLException;
}
