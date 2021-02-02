package br.ufs.dcomp.ChatRabbitMQ.estados;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.UsuarioLogado;

public class Logar extends Estado {

    public Logar(Rabbit rabbit) {
        super("User: ", false, null, rabbit);
    }

    @Override
    public Estado action(String message, Estado currentState) {
        rabbit.login(message);
        return new Ocioso(new UsuarioLogado(message), rabbit);
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }
}
