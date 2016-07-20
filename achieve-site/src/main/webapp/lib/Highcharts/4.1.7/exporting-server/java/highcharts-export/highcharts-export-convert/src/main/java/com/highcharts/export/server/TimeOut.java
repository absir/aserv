package com.highcharts.export.server;

import org.apache.log4j.Logger;

import java.util.TimerTask;


public class TimeOut extends TimerTask {
    protected static Logger logger = Logger.getLogger("utils");
    private final Server server;

    public TimeOut(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        logger.debug("Timed out while downloading.");
        server.setState(ServerState.TIMEDOUT);
    }
};
