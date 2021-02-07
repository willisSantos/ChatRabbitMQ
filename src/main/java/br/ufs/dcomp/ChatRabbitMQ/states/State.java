package br.ufs.dcomp.ChatRabbitMQ.states;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.LoggedUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class State {

    protected String promptStatement;

    protected boolean hasCommand;

    @Getter
    protected LoggedUser loggedUser;

    protected Rabbit rabbit;

    abstract public State action(String message);

    abstract public void print();

    public boolean hasCommand() {
        return hasCommand;
    }

}
