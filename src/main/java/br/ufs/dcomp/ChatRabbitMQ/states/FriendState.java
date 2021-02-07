package br.ufs.dcomp.ChatRabbitMQ.states;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.Friend;
import br.ufs.dcomp.ChatRabbitMQ.entities.LoggedUser;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import lombok.Getter;

import java.io.IOException;

public class FriendState extends State {

    @Getter
    private final Friend friend;

    public FriendState(Friend friend, LoggedUser loggedUser, Rabbit rabbit) {
        super("@" + friend.getName() + ">> ", true, loggedUser, rabbit);
        this.friend = friend;
    }

    @Override
    public State action(String message) {
        try {
            this.rabbit.sendMessageToFriend(MessageHandler.createTextMessage(this.loggedUser.getName(), message),
                    friend.getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível enviar a mensagem para o amigo. Tente novamente!");
        }
        return this;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }

}
