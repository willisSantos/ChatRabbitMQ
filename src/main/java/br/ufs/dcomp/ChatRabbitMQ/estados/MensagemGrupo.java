package br.ufs.dcomp.ChatRabbitMQ.estados;

import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.entidade.Grupo;
import br.ufs.dcomp.ChatRabbitMQ.entidade.UsuarioLogado;
import br.ufs.dcomp.ChatRabbitMQ.protBuff.MensagemProto;
import com.google.protobuf.ByteString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MensagemGrupo extends Estado {

    private final Grupo group;

    public MensagemGrupo(Grupo group, UsuarioLogado loggedUser, Rabbit rabbit) {
        super("#" + group.getName() + ">> ", true, loggedUser, rabbit);
        this.group = group;
    }

    @Override
    public Estado action(String message, Estado currentState) {
        MensagemProto.Conteudo.Builder content = MensagemProto.Conteudo.newBuilder();
        content.setTipo("text/plain");
        content.setNome("Mensagem.txt");
        content.setCorpo(ByteString.copyFromUtf8(message));

        MensagemProto.Mensagem.Builder messageBuilder = MensagemProto.Mensagem.newBuilder();
        messageBuilder.setEmissor(loggedUser.getName());
        messageBuilder.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("d/MM/uuuu")));
        messageBuilder.setHora(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        messageBuilder.setGrupo(this.group.getName());
        messageBuilder.setConteudo(content);

        this.rabbit.sendMessageToGroup(messageBuilder.build().toByteArray(), this.group.getName());
        return currentState;
    }

    @Override
    public void print() {
        System.out.print(promptStatement);
    }
}
