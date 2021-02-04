package br.ufs.dcomp.ChatRabbitMQ.estados;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.Amigo;
import br.ufs.dcomp.ChatRabbitMQ.entidade.UsuarioLogado;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import lombok.Getter;

public class MensagemAmigo extends Estado {

    @Getter
    private final Amigo friend;

    public MensagemAmigo(Amigo friend, UsuarioLogado loggedUser, Rabbit rabbit) {
        super("@" + friend.getName() + ">> ", true, loggedUser, rabbit);
        this.friend = friend;
    }

    @Override
    public Estado action(String message, Estado currentState) {
        this.rabbit.sendMessageToFriend(MessageHandler.createTextMessage(this.loggedUser.getName(), message),
                friend.getName());
        return currentState;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }

}
