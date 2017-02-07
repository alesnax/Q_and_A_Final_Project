package by.alesnax.qanda.pool;

import by.alesnax.qanda.dao.DAOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by alesnax on 04.12.2016.
 */
public class ConnectionPool {
    private static Logger logger = LogManager.getLogger(ConnectionPool.class);
    private static ReentrantLock lock = new ReentrantLock();

    private static ConnectionPool instance = null;


    private static final String DB_PROPERTIES_FILE = "resources.db";
    private static final String CONNECTION_COUNT = "db.connection_count";
    private static final int MINIMAL_CONNECTION_COUNT = 5;

    private BlockingQueue<WrappedConnection> freeConnections;
    private BlockingQueue<WrappedConnection> givenConnections;

    private ConnectionPool() {
    }

    public static ConnectionPool getInstance() {
        lock.lock();
        try {
            if(instance == null){
                instance = new ConnectionPool();
            }
        } finally {
            lock.unlock();
        }
        return instance;
    }

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

    public void destroyPool() throws ConnectionPoolException {
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
    }
}
