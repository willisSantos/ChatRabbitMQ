package br.ufs.dcomp.ChatRabbitMQ.estados;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.Grupo;
import br.ufs.dcomp.ChatRabbitMQ.entidade.UsuarioLogado;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import lombok.Getter;

public class MensagemGrupo extends Estado {

    @Getter
    private final Grupo group;

    public MensagemGrupo(Grupo group, UsuarioLogado loggedUser, Rabbit rabbit) {
        super("#" + group.getName() + ">> ", true, loggedUser, rabbit);
        this.group = group;
    }

    @Override
    public Estado action(String message, Estado currentState) {
        this.rabbit.sendMessageToGroup(MessageHandler.createTextMessage(this.loggedUser.getName(),
                this.group.getName(), message), this.group.getName());
        return currentState;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }
}
