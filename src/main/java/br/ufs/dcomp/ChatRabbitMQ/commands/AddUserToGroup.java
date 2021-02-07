package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class AddUserToGroup extends Command {

    private final Rabbit rabbit;

    public AddUserToGroup(Rabbit rabbit) {
        super("!addUser", 2, "digite \"!addUser nome_do_usuario nome_do_grupo\" que você deseja criar.");
        this.rabbit = rabbit;
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            String username = arguments.get(0);
            String groupName = arguments.get(1);
            List<String> usersFromGroup = rabbit.listUsersFromGroup(groupName);
            if (rabbit.hasUser(username)) {
                if (usersFromGroup.contains(currentState.getLoggedUser().getName())){
                    if (!usersFromGroup.contains(username)) {
                        rabbit.addUser(username, groupName);
                    }
                    else {
                        System.err.println("Usuário já adicionado ao grupo!");
                    }
                } else {
                    System.err.println("Você não tem permissão para adicionar usuários nesse grupo!");
                }
            } else {
                System.err.println("Usuário não existe no sistema!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível adicionar usuário ao grupo. Tente novamente!");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Problema de conexão. Tente novamente!");
        }
        return currentState;
    }
}
