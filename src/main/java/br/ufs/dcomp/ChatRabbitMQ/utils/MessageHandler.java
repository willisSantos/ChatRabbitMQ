package br.ufs.dcomp.ChatRabbitMQ.utils;

import br.ufs.dcomp.ChatRabbitMQ.protBuff.MensagemProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MessageHandler {
    private static byte[] createMessage(String loggedUsername, String groupName, String mimeType, String fileName,
                                        ByteString message) {
        MensagemProto.Conteudo.Builder content = MensagemProto.Conteudo.newBuilder();
        content.setTipo(mimeType);
        content.setNome(fileName);
        content.setCorpo(message);

        MensagemProto.Mensagem.Builder messageBuilder = MensagemProto.Mensagem.newBuilder();
        messageBuilder.setEmissor(loggedUsername);
        messageBuilder.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("d/MM/uuuu")));
        messageBuilder.setHora(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        messageBuilder.setGrupo(groupName);
        messageBuilder.setConteudo(content);

        return messageBuilder.build().toByteArray();
    }

    public static byte[] createTextMessage(String loggedUsername, String groupName, String message) {
        return createMessage(loggedUsername, groupName, "text/plain", "", ByteString.copyFromUtf8(message));
    }

    public static byte[] createTextMessage(String loggedUsername, String message) {
        return createTextMessage(loggedUsername, "", message);
    }

    public static byte[] createBinMessage(String loggedUsername, String groupName, String mimeType,
                                          String fileName, byte[] message) {
        return createMessage(loggedUsername, groupName, mimeType, fileName, ByteString.copyFrom(message));
    }

    public static byte[] createBinMessage(String loggedUsername, String mimeType,
                                          String fileName, byte[] message) {
        return createMessage(loggedUsername, "", mimeType, fileName, ByteString.copyFrom(message));
    }

    public static String formatTextMessage(byte[] buffer) throws InvalidProtocolBufferException {
        MensagemProto.Mensagem message = MensagemProto.Mensagem.parseFrom(buffer);
        String groupName = message.getGrupo().isEmpty() ? "" : "#" + message.getGrupo();
        return "(" + message.getData() + " às " + message.getHora() + ") " + message.getEmissor() + groupName
                + " diz: " + new String(message.getConteudo().getCorpo().toByteArray(), StandardCharsets.UTF_8);
    }

    public static String formatBinMessage(byte[] buffer) throws InvalidProtocolBufferException {
        MensagemProto.Mensagem message = MensagemProto.Mensagem.parseFrom(buffer);
        try {
            FileOutputStream fos =
                    new FileOutputStream(message.getConteudo().getNome());
            fos.write(message.getConteudo().getCorpo().toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String groupName = message.getGrupo().isEmpty() ? "" : "#" + message.getGrupo();
        return "(" + message.getData() + " às " + message.getHora() + ") Arquivo \"" + message.getConteudo().getNome()
                + "\" recebido de " + message.getEmissor() + groupName;
    }

}
