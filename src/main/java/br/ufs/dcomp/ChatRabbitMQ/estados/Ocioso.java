package br.ufs.dcomp.ChatRabbitMQ.estados;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.UsuarioLogado;

public class Ocioso extends Estado {

    public Ocioso(UsuarioLogado loggedUser, Rabbit rabbit) {
        super(">> ", true, loggedUser, rabbit);
    }

    @Override
    public Estado action(String message, Estado currentState) {
        return currentState;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }

}
