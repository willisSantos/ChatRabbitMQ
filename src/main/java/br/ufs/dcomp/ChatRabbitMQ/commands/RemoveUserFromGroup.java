package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class RemoveUserFromGroup extends Command {

    private final Rabbit rabbit;

    public RemoveUserFromGroup(Rabbit rabbit) {
        super("!delFromGroup", 2, "Não foi possível remover o usuário");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            String username = arguments.get(0);
            String groupName = arguments.get(1);
            if (rabbit.hasUser(username) && rabbit.hasGroup(groupName)) {
                if (rabbit.hasAccessToGroup(currentState.getLoggedUser().getName(), groupName)) {
                    rabbit.removeUser(username, groupName);
                } else {
                    System.err.println("Você não pode remover usuários desse grupo porque não é um " +
                            "usuário dele.");
                }
            } else {
                System.err.println("Não existe usuário ou grupo com esse nome!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível remover o usuário do grupo. Tente novamente!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
