package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class CreateGroup extends Command {

    private final Rabbit rabbit;

    public CreateGroup(Rabbit rabbit) {
        super("!addGroup", 1, "digite \"!addGroup nome_do_grupo\" que você deseja criar.");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            String groupName = arguments.get(0);
            if (!rabbit.hasGroup(groupName)) {
                rabbit.createGroup(groupName);
                rabbit.addUser(currentState.getLoggedUser().getName(), groupName);
            } else {
                System.err.println("Já existe um grupo com esse nome");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível criar o grupo. Tente novamente!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
