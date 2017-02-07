package by.alesnax.qanda.listener;

import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Class-Listener that initialise connection pool while application start and destroy pool when
 * application stops.
 *
 * @author Aliaksandr Nakhankou
 * @see javax.servlet.ServletContextListener
 */
@WebListener
public class QAContextCreateListener implements ServletContextListener {
    private static Logger logger = LogManager.getLogger(QAContextCreateListener.class);

    private ConnectionPool pool;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            pool = ConnectionPool.getInstance();
            pool.init();
            logger.log(Level.INFO, "ConnectionPool was initialized");
        } catch (ConnectionPoolException e) {
            logger.log(Level.FATAL, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        pool.destroyPool();
        logger.log(Level.INFO, "ConnectionPool was destroyed");
    }
}

