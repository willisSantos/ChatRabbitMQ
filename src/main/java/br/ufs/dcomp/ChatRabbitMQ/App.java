package br.ufs.dcomp.ChatRabbitMQ;

import br.ufs.dcomp.ChatRabbitMQ.comandos.Upload;
import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.Amigo;
import br.ufs.dcomp.ChatRabbitMQ.entidade.Entrada;
import br.ufs.dcomp.ChatRabbitMQ.entidade.Grupo;
import br.ufs.dcomp.ChatRabbitMQ.estados.Estado;
import br.ufs.dcomp.ChatRabbitMQ.estados.Logar;
import br.ufs.dcomp.ChatRabbitMQ.estados.MensagemAmigo;
import br.ufs.dcomp.ChatRabbitMQ.estados.MensagemGrupo;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        Rabbit rabbit = new Rabbit();
        Entrada input = new Entrada();
        Estado currentState = new Logar(rabbit);
        while (true) {
            currentState.print();
            input.nextLine();
            if (currentState.hasCommand() && input.isCommand()) {
                switch (input.whichCommand()) {
                    case "@":
                        // Muda o estado para MensagemAmigo
                        currentState = new MensagemAmigo(new Amigo(input.getArguments().get(0)),
                                currentState.getLoggedUser(), rabbit);
                        break;
                    case "#":
                        // Muda o estado para MensagemGrupo
                        currentState = new MensagemGrupo(new Grupo(input.getArguments().get(0)),
                                currentState.getLoggedUser(), rabbit);
                        break;
                    case "!addGroup":
                        rabbit.createGroup(input.getArguments().get(0));
                        rabbit.addUser(currentState.getLoggedUser().getName(), input.getArguments().get(0));
                        break;
                    case "!delFromGroup":
                        rabbit.removeUser(input.getArguments().get(0), input.getArguments().get(1));
                        break;
                    case "!addUser":
                        rabbit.addUser(input.getArguments().get(0), input.getArguments().get(1));
                        break;
                    case "!removeGroup":
                        rabbit.deleteGroup(input.getArguments().get(0));
                        break;
                    case "!upload":
                        new Thread(new Upload(currentState, rabbit, input.getArguments().get(0))).start();
                        break;
                    default:
                        // TODO O que fazer quando o usuário não colocar um comando válido?
                        break;
                }
            } else {
                // Realiza a ação do estado
                currentState = currentState.action(input.getMessage(), currentState);
            }
        }
    }
}
