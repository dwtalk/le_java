package com.logentries.log4j2;

import com.logentries.net.AsyncLogger;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;

/**
 * Responsible for managing the actual connection to Logentries.
 * Created by josh on 11/15/14.
 */
public class LogentriesManager extends AbstractManager
{
    private static LogentriesManagerFactory FACTORY = new LogentriesManagerFactory();

    static LogentriesManager getManager(String name, FactoryData data)
    {
        return getManager(name, FACTORY, data);
    }

    static class LogentriesManagerFactory implements ManagerFactory<LogentriesManager, FactoryData>
    {
        @Override
        public LogentriesManager createManager(String name, FactoryData data)
        {
            return new LogentriesManager(name,data);
        }
    }

    private final AsyncLogger asyncLogger;

    protected LogentriesManager(String name, FactoryData data)
    {
        super(name);
        asyncLogger = new AsyncLogger();
        asyncLogger.setToken(data.getToken());
        asyncLogger.setKey(data.getKey());
        asyncLogger.setLocation(nullToEmpty(data.getLocation()));
        asyncLogger.setHttpPut(data.isHttpPut());
        asyncLogger.setSsl(data.isSsl());
        asyncLogger.setDebug(data.isDebug());
        asyncLogger.setUseDataHub(data.isUseDataHub());
        asyncLogger.setDataHubAddr(data.getDataHubAddr());
        asyncLogger.setDataHubPort(data.getDataHubPort());
        asyncLogger.setLogHostName(data.isLogHostName());
        // AsyncLogger doesn't like it when hostName is null. [jsd]
        asyncLogger.setHostName(nullToEmpty(data.getHostName()));
        // AsyncLogger doesn't like it when logID is null. [jsd]
        asyncLogger.setLogID(nullToEmpty(data.getLogID()));
        LOGGER.debug("AsyncLogger created.");
    }

    private String nullToEmpty(String s)
    {
        return s == null ? "" : s;
    }

    @Override
    protected void releaseSub()
    {
        super.releaseSub();
        asyncLogger.close();
        LOGGER.debug("AsyncLogger closed.");
    }

    public void writeLine(String line)
    {
        asyncLogger.addLineToQueue(line);
    }

}
