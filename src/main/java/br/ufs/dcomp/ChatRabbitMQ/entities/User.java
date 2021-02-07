package br.ufs.dcomp.ChatRabbitMQ.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class User {
    private final String name;
}
