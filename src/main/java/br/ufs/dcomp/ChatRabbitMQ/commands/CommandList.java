package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.util.Arrays;
import java.util.List;

public class CommandList {

    private final List<Command> commands;

    public CommandList(Command... commands) {
        this.commands = Arrays.asList(commands);
    }

    public State execCommand(String command, List<String> arguments, State currentState) {
        return findCommand(command).exec(currentState, arguments);
    }

    private Command findCommand(String name) {
        return this.commands.stream()
                .filter(command -> command.getName().equals(name))
                .findFirst()
                .orElse(new Default());
    }
}
