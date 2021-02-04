package br.ufs.dcomp.ChatRabbitMQ.config;

import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Rabbit {

    private Channel mainChannel;

    private Channel fileChannel;

    public Rabbit(String rabbitUsername, String rabbitPassword, String rabbitHost) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(rabbitUsername);
        factory.setPassword(rabbitPassword);
        factory.setHost(rabbitHost);
        factory.setVirtualHost("/");
        try {
            Connection connection = factory.newConnection();
            this.mainChannel = connection.createChannel();
            this.fileChannel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void login(String username) {
        Consumer textConsumer = new DefaultConsumer(this.mainChannel) {
            public void handleDelivery(String consumerTag, Envelope envelomessagepe, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(MessageHandler.formatTextMessage(body));
            }
        };
        Consumer binConsumer = new DefaultConsumer(this.fileChannel) {
            public void handleDelivery(String consumerTag, Envelope envelomessagepe, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(MessageHandler.formatBinMessage(body));
            }
        };
        try {
            this.mainChannel.queueDeclare(username, false, false, false, null);
            this.mainChannel.queueDeclare(username + "_bin", false, false, false, null);
            this.mainChannel.basicConsume(username, true, textConsumer);
            this.fileChannel.basicConsume(username + "_bin", true, binConsumer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessageToFriend(byte[] message, String friendsname) {
        try {
            this.mainChannel.basicPublish("", friendsname, null, message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessageToGroup(byte[] message, String groupName) {
        try {
            this.mainChannel.basicPublish(groupName, "text", null, message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createGroup(String groupName) {
        try {
            this.mainChannel.exchangeDeclare(groupName, "direct");
        } catch (IOException e) {
            // TODO Tratar exceção
            e.printStackTrace();
        }
    }

    public void deleteGroup(String groupName) {
        try {
            this.mainChannel.exchangeDelete(groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String username, String groupName) {
        try {
            this.mainChannel.queueBind(username, groupName, "text");
            this.mainChannel.queueBind(username + "_bin", groupName, "binary");
        } catch (IOException e) {
            // TODO Tratar exceção
            e.printStackTrace();
        }
    }

    public void removeUser(String username, String groupName) {
        try {
            this.mainChannel.queueUnbind(username, groupName, "text");
            this.mainChannel.queueUnbind(username + "_bin", groupName, "binary");
        } catch (IOException e) {
            // TODO Tratar exceção
            e.printStackTrace();
        }
    }

    public void uploadArquivoToFriend(byte[] message, String friendsname) {
        try {
            this.fileChannel.basicPublish("", friendsname + "_bin", null, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadArquivoToGroup(byte[] message, String groupName) {
        try {
            this.fileChannel.basicPublish(groupName, "binary", null, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
