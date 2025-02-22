package via.sep4gr2.sep4websocketstest.loriotconnection;

import via.sep4gr2.sep4websocketstest.util.PropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LoriotWebSocketClient implements WebSocket.Listener, PropertyChangeSubject {
    private WebSocket server = null;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private static final String SERVER_URL = "wss://iotnet.cibicom.dk/app?token=vnoTuQAAABFpb3RuZXQuY2liaWNvbS5kaxJcKw-s3YsQp99sxC0vwhU=";

    public void sendDownLink(String jsonTelegram) {
        server.sendText(jsonTelegram,true);
    }

    public LoriotWebSocketClient() {
        System.out.println("Here1");
        HttpClient client = HttpClient.newHttpClient();
        System.out.println("Here2");
        CompletableFuture<WebSocket> ws = client.newWebSocketBuilder()
                .buildAsync(URI.create(SERVER_URL), this);
        System.out.println("Here3");
        boolean succeededToJoin = false;
        int attemptCounter = 0;
        while (!succeededToJoin && attemptCounter < 20) {
            try{
                System.out.println("\tAttempting to join");
                attemptCounter++;
                server = ws.join();
                System.out.println("\t\t\tJoined successfully");
                succeededToJoin = true;
            } catch (Exception e) {
                System.out.println("Caught exception " + e);
            }
        }
        System.out.println("Here4");
    }

    //onOpen()
    public void onOpen(WebSocket webSocket) {
        // This WebSocket will invoke onText, onBinary, onPing, onPong or onClose methods on the associated listener (i.e. receive methods) up to n more times
        webSocket.request(1);
        System.out.println("WebSocket Listener has been opened for requests.");
    }

    //onError()
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("A " + error.getCause() + " exception was thrown.");
        System.out.println("Message: " + error.getLocalizedMessage());
        webSocket.abort();
    };
    //onClose()
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("WebSocket closed!");
        System.out.println("Status:" + statusCode + " Reason: " + reason);
        return new CompletableFuture().completedFuture("onClose() completed.").thenAccept(System.out::println);
    };
    //onPing()
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        System.out.println("Ping: Client ---> Server");
        System.out.println(message.asCharBuffer().toString());
        return new CompletableFuture().completedFuture("Ping completed.").thenAccept(System.out::println);
    };
    //onPong()
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        System.out.println("Pong: Client ---> Server");
        System.out.println(message.asCharBuffer().toString());
        return new CompletableFuture().completedFuture("Pong completed.").thenAccept(System.out::println);
    };
    //onText()
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        String indented = data.toString();
        System.out.println("Received text" + indented);
        support.firePropertyChange("RECEIVED_DATA", null, indented);
        webSocket.request(1);
        return new CompletableFuture().completedFuture("onText() completed.").thenAccept(System.out::println);
    };

    @Override
    public void addPropertyChangeListener(String eventName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(eventName, listener);
    }
}
