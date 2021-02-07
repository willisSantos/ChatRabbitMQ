package br.ufs.dcomp.ChatRabbitMQ.states;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entities.LoggedUser;

import java.io.IOException;

public class Login extends State {

    public Login(Rabbit rabbit) {
        super("User: ", false, null, rabbit);
    }

    @Override
    public State action(String message) {
        try {
            rabbit.login(message);
            return new Idle(new LoggedUser(message), rabbit);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problema ao logar. Verifique a sua conexão de internet!");
            return this;
        }
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }
}
