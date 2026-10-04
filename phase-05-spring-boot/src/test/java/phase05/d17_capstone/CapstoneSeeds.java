package phase05.d17_capstone;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.sql.DataSource;

/** Canonical capstone-local committed JDBC seeds. Migration must run before use. */
public final class CapstoneSeeds {
    private final DataSource dataSource;
    public CapstoneSeeds(DataSource dataSource) { this.dataSource = java.util.Objects.requireNonNull(dataSource); }

    public void seedCustomer(long id, String subject) throws SQLException {
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("insert into customers(id,subject) values(?,?)")) {
            connection.setAutoCommit(true);
            statement.setLong(1, id);
            statement.setString(2, subject);
            statement.executeUpdate();
        }
    }

    public void seedProduct(long id, String name, BigDecimal usdPrice, int stock) throws SQLException {
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("insert into products(id,name,price,stock) values(?,?,?,?)")) {
            connection.setAutoCommit(true);
            statement.setLong(1, id);
            statement.setString(2, name);
            statement.setBigDecimal(3, usdPrice);
            statement.setInt(4, stock);
            statement.executeUpdate();
        }
    }
}
