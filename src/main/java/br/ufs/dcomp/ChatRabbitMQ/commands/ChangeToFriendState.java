package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.Friend;
import br.ufs.dcomp.ChatRabbitMQ.states.FriendState;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class ChangeToFriendState extends Command {

    private final Rabbit rabbit;

    public ChangeToFriendState(Rabbit rabbit) {
        super("@", 1, "Digite @nome_do_usuario para falar" +
                " com o usuário desejado!");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            String username = arguments.get(0);
            if (rabbit.hasUser(username)) {
                if (!currentState.getLoggedUser().getName().equals(username)) {
                    return new FriendState(new Friend(username), currentState.getLoggedUser(), rabbit);
                } else {
                    System.err.println("Não é possível mandar mensagem para si mesmo!");
                }
            } else {
                System.err.println("Não existe usuário com esse nome. Tente outro usuário.");
            }
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
