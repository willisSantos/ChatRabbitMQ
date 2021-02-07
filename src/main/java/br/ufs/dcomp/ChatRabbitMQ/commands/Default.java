package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.util.List;

public class Default extends Command {
    public Default() {
        super("", 0, "");
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        System.err.println("Comando inválido! Este comando não faz parte da lista de comandos " +
                "disponíveis. Por favor, tente novamente.");
        return currentState;
    }
}
