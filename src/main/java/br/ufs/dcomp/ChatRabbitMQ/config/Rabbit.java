package br.ufs.dcomp.ChatRabbitMQ.config;

import br.ufs.dcomp.ChatRabbitMQ.protBuff.MensagemProto;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Rabbit {

    private Channel channel;

    public Rabbit() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("willis");
        factory.setPassword("bancodemoto");
        factory.setHost("ec2-54-87-140-216.compute-1.amazonaws.com");
        factory.setVirtualHost("/");

        try {
            Connection connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void login(String username) {
        Consumer consumer = new DefaultConsumer(this.channel) {
            public void handleDelivery(String consumerTag, Envelope envelomessagepe, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(formatMessage(body));
            }
        };
        try {
            this.channel.queueDeclare(username, false, false, false, null);
            this.channel.basicConsume(username, true, consumer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessageToFriend(byte[] message, String friendsname) {
        try {
            this.channel.basicPublish("", friendsname, null, message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessageToGroup(byte[] message, String groupName) {
        try {
            this.channel.basicPublish(groupName, "", null, message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createGroup(String groupName) {
        try {
            this.channel.exchangeDeclare(groupName, "fanout");
        } catch (IOException e) {
            // TODO Tratar exceção
            e.printStackTrace();
        }
    }

    public void deleteGroup(String groupName) {
        try {
            this.channel.exchangeDelete(groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String username, String groupName) {
        try {
            this.channel.queueBind(username, groupName, "");
        } catch (IOException e) {
            // TODO Tratar exceção
            e.printStackTrace();
        }
    }

    public void removeUser(String username, String groupName) {
        try {
            channel.queueUnbind(username, groupName, "");
        } catch (IOException e) {
            // TODO Tratar exceção
            e.printStackTrace();
        }
    }

    private String formatMessage(byte[] buffer) throws IOException {
        MensagemProto.Mensagem message = MensagemProto.Mensagem.parseFrom(buffer);
        String groupName = message.getGrupo().isEmpty() ? "" : "#" + message.getGrupo();
        return "(" + message.getData() + " às " + message.getHora() + ") " + message.getEmissor() + groupName
                + " diz: " + new String(message.getConteudo().getCorpo().toByteArray(), StandardCharsets.UTF_8);
    }

}
