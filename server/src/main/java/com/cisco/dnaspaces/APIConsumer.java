package com.cisco.dnaspaces;

import com.cisco.dnaspaces.client.FireHoseAPIClient;
import com.cisco.dnaspaces.consumers.JsonEventConsumer;
import com.cisco.dnaspaces.exceptions.FireHoseAPIException;
import com.cisco.dnaspaces.server.HTTPVerticle;
import com.cisco.dnaspaces.utils.ConfigUtil;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Main class which invokes Firehose api.
 * Event counts are manipulated from response stream and pushed to client via Web Socket
 */
public class APIConsumer {

    private static Logger log = LogManager.getLogger(APIConsumer.class);

    public static void main(String[] args) {
        final Properties config = ConfigUtil.getConfig();
        final Integer retryCutoff = Integer.parseInt(config.getProperty("api.retrylimit.cutoff"));
        final long fromTimeStamp = (!config.getProperty("api.initialfromtimestamp").equals("-1")) ? Long.getLong(config.getProperty("api.initialfromtimestamp")) : 0;
        final int fromTimeStampAdvanceWindow = Integer.parseInt(config.getProperty("api.initialfromtimestampadvancewindow"));
        // create FireHoseAPIClient to communicate to API and receive stream of events
        FireHoseAPIClient client = new FireHoseAPIClient(config.getProperty("api.url"), config.getProperty("api.key"));
        // consumer to handle the event json objects from API
        JsonEventConsumer consumer = new JsonEventConsumer();
        consumer.setLastSuccessTimeStamp(fromTimeStamp);

        // set consumer in API client
        client.setConsumer(consumer);
        client.setFromTimeStampAdvanceWindow(fromTimeStampAdvanceWindow);


        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HTTPVerticle());

        Integer executionCount = 0;
        // loop indefinitely to reconnect
        while (true) {
            // exponential backoff time to retry
            waitBeforeRetry(executionCount++ % retryCutoff);
            log.trace("Connecting to FireHose API. Attempt : " + executionCount);
            try {
                client.startConsumeEvents();
            } catch (FireHoseAPIException e) {
                log.error("Couldn't connect to API " + e.getMessage());
                if (canRetry(e)) {
                    continue;
                }
                break;
            }
        }

        /*WSServer wsServer = null;
        try {
            // create a web socket server and set it to counter utility which will send messages to client
            wsServer = WSServer.getWsServer(8887);
            Integer executionCount = 0;
            // loop indefinitely to reconnect
            while (true) {
                // exponential backoff time to retry
                waitBeforeRetry(executionCount++ % retryCutoff);
                log.trace("Connecting to FireHose API. Attempt : " + executionCount);
                try {
                    client.startConsumeEvents();
                } catch (FireHoseAPIException e) {
                    log.error("Couldn't connect to API " + e.getMessage());
                    if (canRetry(e)) {
                        continue;
                    }
                    break;
                }
            }
        } catch (UnknownHostException e) {
            log.error("Couldn't create Web Socket server :: " + e.getMessage());
        } finally {
            try {
                if (wsServer != null) {
                    log.debug("Stopping WSServer");
                    wsServer.stop();
                    log.info("WSServer stopped");
                }
            } catch (Exception e) {
                log.error("Couldn't close WSServer properly");
            }
        }*/

    }

    public static void waitBeforeRetry(Integer executionCount) {
        try {
            TimeUnit.MILLISECONDS.sleep(((int) Math.round(Math.pow(2, executionCount)) * 1000));
        } catch (InterruptedException e) {
            log.error("Thread interrupted");
        }
    }

    public static boolean canRetry(FireHoseAPIException e) {
        if ((e.getStatusCode() <= 0) || (e.getStatusCode() <= 599 && e.getStatusCode() >= 500)) {
            return true;
        } else if (e.getStatusCode() <= 499 && e.getStatusCode() >= 400) {
            return false;
        } else {
            return false;
        }
    }


}
