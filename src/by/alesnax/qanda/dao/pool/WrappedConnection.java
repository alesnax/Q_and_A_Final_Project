package by.alesnax.qanda.dao.pool;

import by.alesnax.qanda.dao.impl.DAOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ResourceBundle;

public class WrappedConnection {
    private static Logger logger = LogManager.getLogger(WrappedConnection.class);

    static {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            logger.log(Level.FATAL, e + " DriverManager wasn't found");
            throw new RuntimeException(e);
        }
    }

    private static final String JDBC_URL = "db.jdbc_url";
    private static final String DB_LOGIN = "db.login";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_PROPERTIES_FILE = "resources.db";

    private Connection connection;

    WrappedConnection() throws DAOException {
        try {
            connection = createConnection();
        } catch (SQLException e) {
            throw new DAOException("Not obtained connection ", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private static Connection createConnection() throws SQLException {
        ResourceBundle resource = ResourceBundle.getBundle(DB_PROPERTIES_FILE);
        String url = resource.getString(JDBC_URL);
        String login = resource.getString(DB_LOGIN);
        String password = resource.getString(DB_PASSWORD);

        return DriverManager.getConnection(url, login, password);
    }


    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    public Statement getStatement() throws SQLException {
        if (connection != null) {
            Statement statement = connection.createStatement();
            if (statement != null) {
                return statement;
            }
        }
        throw new SQLException("connection or statement is null");
    }

    public void closeStatement(Statement statement) throws DAOException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new DAOException("Statement closing exception ", e);
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public void closeResultSet(ResultSet rs) throws DAOException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DAOException("ResultSet closing exception ", e);
            }
        }
    }


    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    public boolean isNull() {
        return connection == null ? true : false;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

// другие необходимые делегированные методы интерфейса Connection

}