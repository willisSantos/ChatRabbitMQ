package br.ufs.dcomp.ChatRabbitMQ.config;

import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Rabbit {

    private static final String queueSuffix = "6e92d4e9a428f44a4e1f97e1da8680610e3b049d41656699e61770e4ac7e457f";
    private static final String binaryRoutingKey = "bd9ea23bc60b356063cd21987b02c374c59dd385d9949332c00cd27aad94802c";
    private static final String textRoutingKey = "d9a43d8ebabaaf1e8a09b0cdac4b1ef96c87e6b3df3d94395890e234d2b96a1e";
    @Getter
    private final String username;
    private final String password;
    private final String host;
    private final String httpHost;
    private final HttpClient httpClient;
    private Channel mainChannel;
    private Channel fileChannel;

    public Rabbit(String rabbitUsername, String rabbitPassword, String rabbitHost, String rabbitHttpHost) {
        this.username = rabbitUsername;
        this.password = rabbitPassword;
        this.host = rabbitHost;
        this.httpHost = "http://" + rabbitHttpHost + ":15672/";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(rabbitUsername);
        factory.setPassword(rabbitPassword);
        factory.setHost(rabbitHost);
        factory.setVirtualHost("/");
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        try {
            Connection connection = factory.newConnection();
            this.mainChannel = connection.createChannel();
            this.fileChannel = connection.createChannel();
        } catch (TimeoutException e) {
            e.printStackTrace();
            System.err.println("Não foi possível se conectar aos servidores. Execute o programa novamente conectado a" +
                    " internet e com os servidores funcionando");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Verifique os valores de usuario, senha e host passados para o código. Verifique " +
                    "também qual o virtual host executando no servidor.");
            System.exit(1);
        }
    }

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public void login(String username) throws IOException {
        this.mainChannel.queueDeclare(username, false, false, false, null);
        this.mainChannel.queueDeclare(username + queueSuffix, false, false, false, null);
        this.mainChannel.basicConsume(username, true, new DefaultConsumer(this.mainChannel) {
            public void handleDelivery(String consumerTag, Envelope envelomessagepe, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(MessageHandler.formatTextMessage(body));
            }
        });
        this.fileChannel.basicConsume(username + queueSuffix, true, new DefaultConsumer(this.fileChannel) {
            public void handleDelivery(String consumerTag, Envelope envelomessagepe, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(MessageHandler.formatBinMessage(body));
            }
        });
    }

    public void sendMessageToFriend(byte[] message, String friendsname) throws IOException {
        this.mainChannel.basicPublish("", friendsname, null, message);
    }

    public void sendMessageToGroup(byte[] message, String groupName) throws IOException {
        this.mainChannel.basicPublish(groupName, textRoutingKey, null, message);
    }

    public void createGroup(String groupName) throws IOException {
        this.mainChannel.exchangeDeclare(groupName, "direct");
    }

    public void deleteGroup(String groupName) throws IOException {
        this.mainChannel.exchangeDelete(groupName);
    }

    public void addUser(String username, String groupName) throws IOException {
        this.mainChannel.queueBind(username, groupName, textRoutingKey);
        this.mainChannel.queueBind(username + queueSuffix, groupName, binaryRoutingKey);
    }

    public void removeUser(String username, String groupName) throws IOException {
        this.mainChannel.queueUnbind(username, groupName, textRoutingKey);
        this.mainChannel.queueUnbind(username + queueSuffix, groupName, binaryRoutingKey);
    }

    public void uploadArquivoToFriend(byte[] message, String friendsname) {
        try {
            this.fileChannel.basicPublish("", friendsname + queueSuffix, null, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadArquivoToGroup(byte[] message, String groupName) {
        try {
            this.fileChannel.basicPublish(groupName, binaryRoutingKey, null, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendHttpRequest(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpHost + path))
                .header("Authorization", basicAuth(this.username, this.password))
                .timeout(Duration.ofMinutes(1))
                .GET()
                .build();
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public List<String> listAllUsers() throws IOException, InterruptedException {
        List<String> users = new ArrayList<>();
        JsonNode jsonNode = (new ObjectMapper()).readTree(sendHttpRequest("api/queues/%2F/"));
        jsonNode.forEach(node -> {
            if (!node.get("name").asText().endsWith(queueSuffix)) {
                users.add(node.get("name").asText());
            }
        });
        return users;
    }

    public List<String> listAllGroups() throws IOException, InterruptedException {
        List<String> groups = new ArrayList<>();
        JsonNode jsonNode = (new ObjectMapper()).readTree(sendHttpRequest("api/exchanges/%2F/"));
        jsonNode.forEach(node -> {
            if (node.get("user_who_performed_action").asText().equals(this.username)) {
                groups.add(node.get("name").asText());
            }
        });
        return groups;
    }

    public List<String> listUsersFromGroup(String groupName) throws IOException, InterruptedException {
        List<String> users = new ArrayList<>();
        JsonNode jsonNode =
                (new ObjectMapper()).readTree(sendHttpRequest("api/exchanges/%2F/" + groupName + "/bindings/source"));
        jsonNode.forEach(node -> {
            if (node.get("routing_key").asText().equals(textRoutingKey)) {
                users.add(node.get("destination").asText());
            }
        });
        return users;
    }

    public boolean hasUser(String username) throws IOException, InterruptedException {
        List<String> groups = listAllUsers();
        return groups.contains(username);
    }

    public boolean hasGroup(String groupName) throws IOException, InterruptedException {
        List<String> groups = listAllGroups();
        return groups.contains(groupName);
    }

    public boolean hasAccessToGroup(String loggedUser, String groupName) throws IOException, InterruptedException {
        List<String> users = listUsersFromGroup(groupName);
        return users.contains(loggedUser);
    }

}
