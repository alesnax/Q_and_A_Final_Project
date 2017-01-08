package by.alesnax.qanda.listener;

import by.alesnax.qanda.dao.pool.ConnectionPool;
import by.alesnax.qanda.dao.pool.ConnectionPoolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by alesnax on 08.12.2016.
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
        try {
            pool.destroyPool();
        } catch (ConnectionPoolException e) {
            logger.log(Level.ERROR, "ConnectionPool exception while destroying.");
        }
        logger.log(Level.INFO, "ConnectionPool was destroyed");
    }
}

