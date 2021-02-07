package br.ufs.dcomp.ChatRabbitMQ.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Input {

    private final Scanner input;
    private Scanner line;
    private List<String> args;

    public Input() {
        input = new Scanner(System.in);
    }

    /**
     * Usado para carregar a próxima linha do terminal. As próximas operações irão
     * consultar essa entidade
     */
    public void nextLine() {
        line = new Scanner(input.nextLine());
        args = new ArrayList<>();
        while (line.hasNext()) {
            args.add(line.next());
        }
    }

    /**
     * Comando usado para verificar se a linha que foi escrito na tele é uma
     * mensagem ou se é um comando
     *
     * @return É um comando ou não?
     */
    public boolean isCommand() {
        return args.get(0).startsWith("@") || args.get(0).startsWith("#") || args.get(0).startsWith("!");
    }

    public boolean isChangeAmigo() {
        return args.get(0).startsWith("@");
    }

    public boolean isChangeGrupo() {
        return args.get(0).startsWith("#");
    }

    public boolean isExclamationCommand() {
        return args.get(0).startsWith("!");
    }

    /**
     * Retorna o comando que o usuário logado está querendo executar
     *
     * @return nome do comando executado pelo usuário
     */
    public String whichCommand() {
        if (isCommand()) {
            if (isChangeAmigo()) {
                return "@";
            } else if (isChangeGrupo()) {
                return "#";
            } else {
                return args.get(0);
            }
        } else {
            System.err.println("Não deveria ser possível chegar nessa linha. O programa será parado!");
            System.exit(1);
            return null;
        }
    }

    /**
     * Retorna lista com os argumentos usados para executar o programa
     *
     * @return Se o comando for !, retorne todos os valores seguintes ao comando. O Resto só deve retornar o valor
     * junto do simbolo de comando
     */
    public List<String> getArguments() {
        List<String> arguments = new ArrayList<>(args);
        if (isExclamationCommand()) {
            arguments.remove(0);
        } else {
            arguments.set(0, arguments.get(0).substring(1));
        }
        return arguments;
    }

    public String getMessage() {
        return String.join(" ", args);
    }

}
