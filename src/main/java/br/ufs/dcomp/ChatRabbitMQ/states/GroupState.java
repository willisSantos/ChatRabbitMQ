package br.ufs.dcomp.ChatRabbitMQ.states;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.Group;
import br.ufs.dcomp.ChatRabbitMQ.entities.LoggedUser;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import lombok.Getter;

import java.io.IOException;

public class GroupState extends State {

    @Getter
    private final Group group;

    public GroupState(Group group, LoggedUser loggedUser, Rabbit rabbit) {
        super("#" + group.getName() + ">> ", true, loggedUser, rabbit);
        this.group = group;
    }

    @Override
    public State action(String message) {
        try {
            this.rabbit.sendMessageToGroup(MessageHandler.createTextMessage(this.loggedUser.getName(),
                    this.group.getName(), message), this.group.getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível enviar a mensagem para o grupo. Tente novamente!");
        }
        return this;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }
}
