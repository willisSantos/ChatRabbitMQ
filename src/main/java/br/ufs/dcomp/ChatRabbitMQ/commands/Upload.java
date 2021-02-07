package br.ufs.dcomp.ChatRabbitMQ.commands;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.FriendState;
import br.ufs.dcomp.ChatRabbitMQ.states.GroupState;
import br.ufs.dcomp.ChatRabbitMQ.states.State;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Upload extends Command implements Runnable {

    private final Rabbit rabbit;

    private State currentState;

    private List<String> arguments;

    public Upload(Rabbit rabbit) {
        super("!upload", 1, "para enviar um arquivo, digite \"!upload caminho_absoluto" +
                "do arquivo\".");
        this.rabbit = rabbit;
    }

    private void threadExec() {
        if (currentState.getClass() == FriendState.class) {
            try {
                correctNumberOfArguments(this.arguments);
                String path = this.arguments.get(0);
                Path source = Paths.get(path);
                FileInputStream fis = new FileInputStream(path);
                byte[] message = fis.readAllBytes();
                String mimeType = Files.probeContentType(source);
                String fileName = (new File(path)).getName();
                String loggedUsername = currentState.getLoggedUser().getName();
                String friendsName = ((FriendState) currentState).getFriend().getName();
                System.out.println("Enviando \"" + path + "\" para @" + friendsName + ".");
                rabbit.uploadArquivoToFriend(MessageHandler.createBinMessage(loggedUsername, mimeType, fileName, message)
                        , friendsName);
                System.out.println("Arquivo \"" + path + "\" foi enviado para @" + friendsName + " !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (currentState.getClass() == GroupState.class) {
            try {
                correctNumberOfArguments(arguments);
                String path = arguments.get(0);
                Path source = Paths.get(path);
                FileInputStream fis = new FileInputStream(path);
                byte[] message = fis.readAllBytes();
                String mimeType = Files.probeContentType(source);
                String fileName = (new File(path)).getName();
                String loggedUsername = currentState.getLoggedUser().getName();
                String groupName = ((GroupState) currentState).getGroup().getName();
                System.out.println("Enviando \"" + path + "\" para #" + groupName + ".");
                rabbit.uploadArquivoToGroup(MessageHandler.createBinMessage(loggedUsername, groupName, mimeType, fileName,
                        message), groupName);
                System.out.println("Arquivo \"" + path + "\" foi enviado para #" + groupName + " !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public State exec(State currentState, List<String> arguments) {
        this.currentState = currentState;
        this.arguments = arguments;
        new Thread(this).start();
        return currentState;

    }

    @Override
    public void run() {
        threadExec();
    }

}
