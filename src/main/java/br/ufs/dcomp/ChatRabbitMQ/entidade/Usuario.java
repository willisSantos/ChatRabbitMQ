package br.ufs.dcomp.ChatRabbitMQ.entidade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Usuario {
    private final String name;
}
