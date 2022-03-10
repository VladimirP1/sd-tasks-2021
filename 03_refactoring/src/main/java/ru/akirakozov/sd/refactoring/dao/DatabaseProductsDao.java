package ru.akirakozov.sd.refactoring.dao;

import ru.akirakozov.sd.refactoring.connection.ConnectionProvider;
import ru.akirakozov.sd.refactoring.model.Product;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProductsDao implements ProductsDao {
    private final ConnectionProvider connectionProvider;

    public DatabaseProductsDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public int count() throws SQLException {
        try (Connection c = connectionProvider.getConnection()) {
            try (Statement stmt = c.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PRODUCT")) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public List<Product> get() throws SQLException {
        return getImpl("SELECT * FROM PRODUCT");
    }

    @Override
    public Product getWithMinPrice() throws SQLException {
        List<Product> result = getImpl("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1");
        return result.size() > 0 ? result.get(0) : null;
    }

    @Override
    public Product getWithMaxPrice() throws SQLException {
        List<Product> result = getImpl("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");
        return result.size() > 0 ? result.get(0) : null;
    }


    @Override
    public long getSumPrices() throws SQLException {
        try (Connection c = connectionProvider.getConnection()) {
            try (Statement stmt = c.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT SUM(price) FROM PRODUCT")) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public void createTable() throws SQLException {
        try (Connection c = connectionProvider.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(sql);
            }
        }
    }

    @Override
    public void addProduct(Product p) throws SQLException {
        try (Connection c = connectionProvider.getConnection()) {
            String sql = "INSERT INTO PRODUCT " +
                    "(NAME, PRICE) VALUES (\"" + p.name + "\"," + p.price + ")";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(sql);
            }
        }
    }


    private List<Product> getImpl(String query) throws SQLException {
        List<Product> products = new ArrayList<Product>();
        try (Connection c = connectionProvider.getConnection()) {
            try (Statement stmt = c.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        int price = rs.getInt("price");
                        products.add(new Product(name, price));
                    }
                }
            }
        }
        return products;
    }
}
