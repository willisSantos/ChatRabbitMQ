package br.ufs.dcomp.ChatRabbitMQ.comandos;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.estados.Estado;
import br.ufs.dcomp.ChatRabbitMQ.estados.MensagemAmigo;
import br.ufs.dcomp.ChatRabbitMQ.estados.MensagemGrupo;
import br.ufs.dcomp.ChatRabbitMQ.utils.MessageHandler;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public class Upload implements Runnable {

    private final Estado currentState;
    private final Rabbit rabbit;
    private final String path;

    public void exec() {
        if (currentState.getClass() == MensagemAmigo.class) {
            try {
                Path source = Paths.get(path);
                FileInputStream fis = null;
                fis = new FileInputStream(path);
                byte[] message = fis.readAllBytes();
                String mimeType = Files.probeContentType(source);
                String fileName = (new File(path)).getName();
                String loggedUsername = currentState.getLoggedUser().getName();
                String friendsName = ((MensagemAmigo) currentState).getFriend().getName();
                System.out.println("Enviando \"" + path + "\" para @" + friendsName + ".");
                rabbit.uploadArquivoToFriend(MessageHandler.createBinMessage(loggedUsername, mimeType, fileName, message)
                        , friendsName);
                System.out.println("Arquivo \"" + path + "\" foi enviado para @" + friendsName + " !");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (currentState.getClass() == MensagemGrupo.class) {
            try {
                Path source = Paths.get(path);
                FileInputStream fis = null;
                fis = new FileInputStream(path);
                byte[] message = fis.readAllBytes();
                String mimeType = Files.probeContentType(source);
                String fileName = (new File(path)).getName();
                String loggedUsername = currentState.getLoggedUser().getName();
                String groupName = ((MensagemGrupo) currentState).getGroup().getName();
                System.out.println("Enviando \"" + path + "\" para #" + groupName + ".");
                rabbit.uploadArquivoToGroup(MessageHandler.createBinMessage(loggedUsername, groupName, mimeType, fileName,
                        message), groupName);
                System.out.println("Arquivo \"" + path + "\" foi enviado para #" + groupName + " !");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        exec();
    }
}
