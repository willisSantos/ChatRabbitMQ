package br.ufs.dcomp.ChatRabbitMQ;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.ufs.dcomp.ChatRabbitMQ.entidade.Usuario;

public class App {
	public static void main(String[] args) throws IOException, TimeoutException {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Scanner line;
		String whoToSend = "";
		String currentMode = "User: ";
		List<String> arguments;
		String message;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("willis");
		factory.setPassword("bancodemoto");
		factory.setHost("ec2-54-226-117-72.compute-1.amazonaws.com");
		factory.setVirtualHost("/");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		Consumer consumer = new DefaultConsumer(channel) {
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				String message = new String(body, "UTF-8");
				System.out.println(message);
			}
		};

		System.out.print(currentMode);
		String username = input.nextLine();
		Usuario user = new Usuario(username);
		channel.queueDeclare(user.getNome(), false, false, false, null);
		channel.basicConsume(user.getNome(), true, consumer);
		currentMode = ">> ";
		while (true) {
			System.out.print(currentMode);
			arguments = new ArrayList<String>();
			line = new Scanner(input.nextLine());
			while (line.hasNext()) {
				arguments.add(line.next());
			}
			if (arguments.get(0).startsWith("@")) {
				whoToSend = arguments.get(0).substring(1);
				currentMode = "@" + whoToSend + ">> ";
			} else {
				if (!whoToSend.isEmpty()) {
					message = "(" + LocalDate.now().format(DateTimeFormatter.ofPattern("d/MM/uuuu")) + " às "
							+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + ") " + user.getNome()
							+ " diz: " + String.join(" ", arguments);
					channel.basicPublish("", whoToSend, null, message.getBytes("UTF-8"));
				}
			}
		}
	}
}
