package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class DeleteGroup extends Command {

    private final Rabbit rabbit;

    public DeleteGroup(Rabbit rabbit) {
        super("!removeGroup", 1, "Não é possível deletar o grupo");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            String groupName = arguments.get(0);
            if (rabbit.hasGroup(groupName)) {
                if (rabbit.listUsersFromGroup(groupName).contains(currentState.getLoggedUser().getName())) {
                    rabbit.deleteGroup(groupName);
                }
                else {
                    System.err.println("Você não tem autorização para excluir grupos");
                }
            }
            else {
                System.err.println("Grupo não existe!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível deletar o grupo. Tente novamente!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
