package br.ufs.dcomp.ChatRabbitMQ.estados;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.UsuarioLogado;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Estado {

    protected String promptStatement;

    protected boolean hasCommand;

    @Getter
    protected UsuarioLogado loggedUser;

    protected Rabbit rabbit;

    abstract public Estado action(String message, Estado currentState);

    abstract public void print();

    public boolean hasCommand() {
        return hasCommand;
    }

}
