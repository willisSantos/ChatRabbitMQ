package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class ListUsersFromGroup extends Command {

    private final Rabbit rabbit;

    public ListUsersFromGroup(Rabbit rabbit) {
        super("!listUsers", 1, "não faço ideia");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        correctNumberOfArguments(arguments);
        String groupName = arguments.get(0);
        try {
            System.out.println(String.join(", ",
                    rabbit.listUsersFromGroup(groupName)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
