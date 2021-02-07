package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.util.List;

public class Exit extends Command {

    public Exit() {
        super("!exit", 0, "não faço ideia 3");
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        System.exit(0);
        return currentState;
    }
}
