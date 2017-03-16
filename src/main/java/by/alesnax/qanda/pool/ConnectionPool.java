package by.alesnax.qanda.pool;

import by.alesnax.qanda.dao.DAOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An object that provides hooks for connection pool management.
 * A <code>ConnectionPool</code> object
 * represents a physical connection to a data source.  The connection
 * can be recycled rather than being closed when an application is
 * finished with it, thus reducing the number of connections that
 * need to be made.
 * <p>
 * An application programmer does not use the <code>ConnectionPool</code>
 * interface directly; rather, it is used by a middle tier infrastructure
 * that manages the pooling of connections.
 *
 * @author Aliksandr Nakhankou
 */
public class ConnectionPool {
    private static Logger logger = LogManager.getLogger(ConnectionPool.class);
    /**
     * Lock for using in getInstance() method
     */
    private static Lock lock = new ReentrantLock();

    /**
     * shows if instance was created or not
     */
    private volatile static boolean instanceCreated = false;

    /**
     * instance of ConnectionPool
     */
    private static ConnectionPool instance = null;

    /**
     * Name of .properties that contains init params of connection pool
     */
    private static final String DB_PROPERTIES_FILE = "db";

    /**
     * key of the number of connections located in db.properties file
     */
    private static final String CONNECTION_COUNT = "db.connection_count";

    /**
     * default number of connections
     */
    private static final int MINIMAL_CONNECTION_COUNT = 5;

    /**
     * container for free for using connections
     */
    private BlockingQueue<WrappedConnection> freeConnections;

    /**
     * conteiner of connections in using
     */
    private BlockingQueue<WrappedConnection> givenConnections;

    /**
     * constructs instance
     */
    private ConnectionPool() {
    }

    /**
     * creates ConnectionPool object and returns it, or returns if pool has already created
     *
     * @return ConnectionPool object
     */
    public static ConnectionPool getInstance() {
        if (!instanceCreated) {
            lock.lock();
            try {
                if (!instanceCreated) {
                    instance = new ConnectionPool();
                    instanceCreated = true;
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /**
     * creates connections and put it in freeConnections
     *
     * @throws ConnectionPoolException if exception while creating were caught
     */
    public void init() throws ConnectionPoolException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(DB_PROPERTIES_FILE);
        int connectionCount = 0;
        try {
            connectionCount = Integer.parseInt(resourceBundle.getString(CONNECTION_COUNT));
        } catch (NumberFormatException e) {
            logger.log(Level.ERROR, "Exception while reading connection number, minimal connection count was set");
            connectionCount = MINIMAL_CONNECTION_COUNT;
        }

        freeConnections = new ArrayBlockingQueue<>(connectionCount);
        givenConnections = new ArrayBlockingQueue<>(connectionCount);

        for (int i = 0; i < connectionCount; i++) {
            try {
                WrappedConnection connection = new WrappedConnection();
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                freeConnections.put(connection);
            } catch (DAOException e) {
                throw new ConnectionPoolException("Fatal error, not obtained connection ", e);
            } catch (SQLException e) {
                throw new ConnectionPoolException("Connection SetAutoCommitException", e);
            } catch (InterruptedException e) {
                throw new ConnectionPoolException("pool error", e);
            }
        }
    }

    /**
     * takes connection from freeConnections and put it to givenConnections,
     * then returns it for following using
     *
     * @return connection
     * @throws ConnectionPoolException if exception while taking connection was occurred
     */
    public WrappedConnection takeConnection() throws ConnectionPoolException {
        WrappedConnection connection;
        try {
            connection = freeConnections.take();
            givenConnections.put(connection);
        } catch (InterruptedException e) {
            throw new ConnectionPoolException("take connection error", e);
        }
        return connection;
    }

    /**
     * takes connection from givenConnections and put it to freeConnections,
     * and makes commit status 'true' if needs
     *
     * @throws ConnectionPoolException if exception while taking connection was occurred
     */
    public void returnConnection(WrappedConnection connection) throws ConnectionPoolException {
        try {
            if (connection.isNull() || connection.isClosed()) {
                throw new ConnectionPoolException("ConnectionWasLostWhileReturning Error");
            }
            try {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                givenConnections.remove(connection);
                freeConnections.put(connection);
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Connection SetAutoCommitException", e);
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Interrupted exception while putting connection into freeConnectionPool", e);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "ConnectionIsClosed Error", e);
        }
    }

    /**
     * closes connections when application finishes work
     */
    public void destroyPool() {
        for (int i = 0; i < freeConnections.size(); i++) {
            try {
                WrappedConnection connection = freeConnections.take();
                connection.closeConnection();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "DestroyPoolException in freeConnections", e);
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Interrupted exception while taking connection from freeConnections for close connection and destroying pool", e);
            }
        }
        for (int i = 0; i < givenConnections.size(); i++) {
            try {
                WrappedConnection connection = givenConnections.take();
                connection.closeConnection();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "DestroyPoolException in givenConnections", e);
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Interrupted exception while taking connection from freeConnections for close connection and destroying pool", e);
            }
        }
        try {
            DriverManager.deregisterDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e + " DriverManager wasn't found");
        }
    }
}
