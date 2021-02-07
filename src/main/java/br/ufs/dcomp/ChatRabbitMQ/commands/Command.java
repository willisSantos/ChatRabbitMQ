package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.states.State;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public abstract class Command {
    @Getter
    protected final String name;

    protected final int numberOfArguments;

    protected final String helpMessage;

    protected void correctNumberOfArguments(List<String> arguments) {
        if (this.numberOfArguments != arguments.size()) {
            throw new IllegalArgumentException("Exatos" + numberOfArguments + "são necessários");
        }
    }

    public abstract State exec(State currentState, List<String> arguments);
}
