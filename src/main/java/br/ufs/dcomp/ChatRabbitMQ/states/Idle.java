package br.ufs.dcomp.ChatRabbitMQ.states;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.LoggedUser;

public class Idle extends State {

    public Idle(LoggedUser loggedUser, Rabbit rabbit) {
        super(">> ", true, loggedUser, rabbit);
    }

    @Override
    public State action(String message) {
        return this;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }

}
