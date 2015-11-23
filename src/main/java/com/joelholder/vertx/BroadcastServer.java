package com.joelholder.vertx;



import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

// todo: finish - source: https://raw.githubusercontent.com/johanhaleby/perfect-storm/master/src/main/java/com/jayway/perfectstorm/vertx/VertxServer.java

public class BroadcastServer {

    private final ScheduledExecutorService executorService;
    private final HazelcastInstance hazelcast;
    private final IQueue<Object> tweetWordQueue;

    public BroadcastServer() {
        hazelcast = Hazelcast.newHazelcastInstance();
        BlockingQueue<Map<String, Object>> tpsQueue = hazelcast.getQueue("tweets-per-second");
        BlockingQueue<Map<String, Object>> countryAndTweetFrequencyQueue = hazelcast.getQueue("country-frequency");
        BlockingQueue<Map<String, Object>> foundTweetsQueue = hazelcast.getQueue("found-tweets");
        tweetWordQueue = hazelcast.getQueue("tweet-word");
        executorService = Executors.newScheduledThreadPool(2);
        executorService.scheduleAtFixedRate(new QueueBroadcaster(tpsQueue), 100, 50, MILLISECONDS);
        executorService.scheduleAtFixedRate(new QueueBroadcaster(countryAndTweetFrequencyQueue), 150, 50, MILLISECONDS);
        executorService.scheduleAtFixedRate(new QueueBroadcaster(foundTweetsQueue), 200, 50, MILLISECONDS);
    }

    private List<ServerWebSocket> connections = new CopyOnWriteArrayList<>();
    private Vertx vertx;

    public void start() {
       
        HttpServer server = vertx.createHttpServer();

        server.websocketHandler(new Handler<ServerWebSocket>() {
            public void handle(final ServerWebSocket ws) {
                // A WebSocket has connected!
                connections.add(ws);
            
                if (ws.path().equals("/subscribe")) {
                    ws.handler(new Handler<Buffer>() {
                        public void handle(Buffer data) {
                            final String message = data.toString();
                            tweetWordQueue.offer(message);
                        }
                    });
                    ws.endHandler(new Handler<Void>() {
                        @Override
                        public void handle(Void aVoid) {
                            connections.remove(ws);
                        }
                    });
                } else {
                    ws.reject();
                }
            }
        }).requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                if (req.path().equals("/")) {
                    req.response().sendFile("src/web/html/ws.html");
                } else if (req.path().endsWith(".css")) {
                    req.response().sendFile("src/web/css" + req.path());
                } else if (req.path().endsWith(".js")) {
                    req.response().sendFile("src/web/js" + req.path());
                } else if (req.path().endsWith(".png")) {
                    req.response().sendFile("src/web/css" + req.path());
                }
            }
        }).listen(8080);
    }

    public void stop() {
        vertx.close();
        hazelcast.getLifecycleService().shutdown();
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcast(String message) {
        for (ServerWebSocket connection : connections) {
            connection.writeFinalTextFrame(message);
        }
    }

    private class QueueBroadcaster implements Runnable {
        private final BlockingQueue<Map<String, Object>> inputQueue;

        public QueueBroadcaster(BlockingQueue<Map<String, Object>> inputQueue) {
            this.inputQueue = inputQueue;
        }

        @Override
        public void run() {
            final Map<String, Object> object = inputQueue.poll();
            if (object == null) {
                return;
            }

            final String json = new JsonObject(object).encode();
            broadcast(json);
        }
    }
}