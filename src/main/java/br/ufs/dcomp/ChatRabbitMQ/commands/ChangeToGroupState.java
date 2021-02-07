package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.Group;
import br.ufs.dcomp.ChatRabbitMQ.states.GroupState;
import br.ufs.dcomp.ChatRabbitMQ.states.State;

import java.io.IOException;
import java.util.List;

public class ChangeToGroupState extends Command {

    private final Rabbit rabbit;

    public ChangeToGroupState(Rabbit rabbit) {
        super("#", 1, "Digite #nome_do_grupo para falar com os membros do grupo que" +
                "você faz parte");
        this.rabbit = rabbit;
    }

    public State exec(State currentState, List<String> arguments) {
        try {
            correctNumberOfArguments(arguments);
            String groupName = arguments.get(0);
            if (rabbit.hasGroup(groupName)) {
                if (rabbit.hasAccessToGroup(currentState.getLoggedUser().getName(), groupName)) {
                    return new GroupState(new Group(groupName), currentState.getLoggedUser(), rabbit);
                } else {
                    System.err.println("Você não pode postar mensagens nesse grupo porque não é um " +
                            "usuário dele. Contate alguém do grupo e peça pra ele adicioná-lo");
                }
            } else {
                System.err.println("Não existe Grupo com esse nome. Tente outro grupo.");
            }
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return currentState;
    }
}
