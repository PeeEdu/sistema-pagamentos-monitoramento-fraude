package com.user.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super("Usuário com ID '" + id + "' não encontrado");
    }
}