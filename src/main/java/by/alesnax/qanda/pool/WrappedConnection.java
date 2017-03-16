package by.alesnax.qanda.pool;

import by.alesnax.qanda.dao.DAOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * <P>A wrapped version of connection (session) with a specific
 * database. Prevents using of 'wild' connection.
 * SQL statements are executed and results are returned
 * within the context of a connection.
 * <p>
 * A <code>Connection</code> object's database is able to provide information
 * describing its tables, its supported SQL grammar, its stored
 * procedures, the capabilities of this connection, and so on.
 *
 * @see DriverManager#getConnection
 * @see Statement
 * @see ResultSet
 * @author Aliaksandr Nakhankou
 */
public class WrappedConnection {
    private static Logger logger = LogManager.getLogger(WrappedConnection.class);

    /*
     * Static block registers the given driver with the {@code DriverManager}.
     * A newly-loaded driver class should call
     * the method {@code registerDriver} to make itself
     * known to the {@code DriverManager}.
     *
     * @param driver the new JDBC Driver that is to be registered with the
     *               {@code DriverManager}
     * @exception SQLException if a database access error occurs
     */
    static {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            logger.log(Level.FATAL, e + " DriverManager wasn't found");
            throw new RuntimeException(e);
        }
    }

    /**
     * Keys of a database url of the form
     * <code>jdbc:<em>subprotocol</em>:<em>subname</em></code> located in db.properties file
     */
    private static final String JDBC_URL = "db.jdbc_url";

    /**
     * key of the database user on whose behalf the connection is being
     * located in db.properties file
     */
    private static final String DB_LOGIN = "db.login";

    /**
     * key of the user's password located in db.properties file
     */
    private static final String DB_PASSWORD = "db.password";

    /**
     * Name of .properties that contains init params of connection pool
     */
    private static final String DB_PROPERTIES_FILE = "db";

    /**
     * Connection to the URL
     */
    private Connection connection;

    /**
     * constructs connection calling creating method
     *
     * @throws DAOException if a database access error occurs
     *                      or constructor is called on a closed connection
     */
    WrappedConnection() throws DAOException {
        try {
            connection = createConnection();
        } catch (SQLException e) {
            throw new DAOException("Not obtained connection ", e);
        }
    }

    /**
     * returns WrappedConnection
     *
     * @return a Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Reads parameters of initialisation and constructs connection by
     * method of DriverManager.
     * Attempts to establish a connection to the given database URL.
     * The <code>DriverManager</code> attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     *
     * @return a Connection to the URL
     * @throws SQLException if a database access error occurs or the url is
     *                      {@code null}
     */
    private static Connection createConnection() throws SQLException {
        ResourceBundle resource = ResourceBundle.getBundle(DB_PROPERTIES_FILE);
        String url = resource.getString(JDBC_URL);
        String login = resource.getString(DB_LOGIN);
        String password = resource.getString(DB_PASSWORD);

        return DriverManager.getConnection(url, login, password);
    }

    /**
     * Wrapped version of Connection class method.
     * Sets this connection's auto-commit mode to the given state.
     * If a connection is in auto-commit mode, then all its SQL
     * statements will be executed and committed as individual
     * transactions.  Otherwise, its SQL statements are grouped into
     * transactions that are terminated by a call to either
     * the method <code>commit</code> or the method <code>rollback</code>.
     * By default, new connections are in auto-commit
     * mode.
     *
     * @param autoCommit <code>true</code> to enable auto-commit mode;
     *                   <code>false</code> to disable it
     * @throws SQLException if a database access error occurs,
     *                      setAutoCommit(true) is called while participating in a distributed transaction,
     *                      or this method is called on a closed connection
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    /**
     * Wrapped version of Connection class method.
     * Retrieves the current auto-commit mode for this <code>Connection</code>
     * object.
     *
     * @return the current state of this <code>Connection</code> object's
     * auto-commit mode
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     * @see #setAutoCommit
     */
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    /**
     * Wrapped version of Connection class method.
     * Creates a <code>Statement</code> object for sending
     * SQL statements to the database.
     *
     * @return a new default <code>Statement</code> object
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    public Statement getStatement() throws SQLException {
        if (connection != null) {
            Statement statement = connection.createStatement();
            if (statement != null) {
                return statement;
            }
        }
        throw new SQLException("connection or statement is null");
    }

    /**
     * Wrapped version of method 'close()' in Statement class.
     * Releases this <code>Statement</code> object's database
     * and JDBC resources immediately instead of waiting for
     * this to happen when it is automatically closed.
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database
     * resources.
     *
     * @param statement closing Statement
     * @throws DAOException if exception while closing statement was occured
     */
    public void closeStatement(Statement statement) throws DAOException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Statement closing exception ", e);
            }
        }
    }

    /**
     * Wrapped version of Connection class method.
     * Releases this <code>Connection</code> object's database and JDBC resources
     * immediately instead of waiting for them to be automatically released.
     *
     * @throws SQLException SQLException if a database access error occurs
     */
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Wrapped version of Connection class method.
     * Retrieves whether this <code>Connection</code> object has been
     * closed.
     *
     * @return <code>true</code> if this <code>Connection</code> object
     * is closed; <code>false</code> if it is still open
     * @throws SQLException if a database access error occurs
     */
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }


    /**
     * Wrapped version of Connection class method.
     * checks if connecion equals null
     *
     * @return true if connection equals null, false otherwise
     */
    public boolean isNull() {
        return connection == null;
    }

    /**
     * Wrapped version of Connection class method.
     * Creates a <code>PreparedStatement</code> object for sending
     * parametrised SQL statements to the database.
     *
     * @param sql an SQL statement that may contain one or more '?' IN
     *            parameter placeholders
     * @return a new default <code>PreparedStatement</code> object containing the
     * pre-compiled SQL statement
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * Wrapped version of Connection class method.
     * Makes all changes made since the previous
     * commit/rollback permanent and releases any database locks
     * currently held by this <code>Connection</code> object.
     * This method should be
     * used only when auto-commit mode has been disabled.
     *
     * @throws SQLException if a database access error occurs,
     *                      this method is called while participating in a distributed transaction,
     *                      if this method is called on a closed connection or this
     *                      <code>Connection</code> object is in auto-commit mode
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Wrapped version of Connection class method.
     * Undoes all changes made in the current transaction
     * and releases any database locks currently held
     * by this <code>Connection</code> object. This method should be
     * used only when auto-commit mode has been disabled.
     *
     * @throws SQLException if a database access error occurs,
     *                      this method is called while participating in a distributed transaction,
     *                      this method is called on a closed connection or this
     *                      <code>Connection</code> object is in auto-commit mode
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

}