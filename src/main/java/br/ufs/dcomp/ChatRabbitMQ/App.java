package br.ufs.dcomp.ChatRabbitMQ;

import br.ufs.dcomp.ChatRabbitMQ.commands.*;
import br.ufs.dcomp.ChatRabbitMQ.config.Rabbit;
import br.ufs.dcomp.ChatRabbitMQ.states.Login;
import br.ufs.dcomp.ChatRabbitMQ.states.State;
import br.ufs.dcomp.ChatRabbitMQ.utils.Input;

public class App {
    public static void main(String[] args) {
        Rabbit rabbit = new Rabbit(args[0], args[1], args[2], args[3]);
        Input input = new Input();
        State currentState = new Login(rabbit);
        CommandList commands = new CommandList(
                new ChangeToFriendState(rabbit),
                new ChangeToGroupState(rabbit),
                new CreateGroup(rabbit),
                new DeleteGroup(rabbit),
                new AddUserToGroup(rabbit),
                new RemoveUserFromGroup(rabbit),
                new Upload(rabbit),
                new ListUsersFromGroup(rabbit),
                new ListGroups(rabbit),
                new Exit()
        );
        while (true) {
            currentState.print();
            input.nextLine();
            if (currentState.hasCommand() && input.isCommand()) {
                currentState = commands.execCommand(input.whichCommand(), input.getArguments(), currentState);
            } else {
                currentState = currentState.action(input.getMessage());
            }
        }
    }
}
