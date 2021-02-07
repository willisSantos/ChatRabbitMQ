package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class ListGroups extends Command {

    private final Rabbit rabbit;

    public ListGroups(Rabbit rabbit) {
        super("!listGroups", 0, "n faço ideia");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            System.out.println(String.join(", ", rabbit.listAllGroups()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
