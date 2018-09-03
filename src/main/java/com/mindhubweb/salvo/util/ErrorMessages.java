package com.mindhubweb.salvo.util;

public final class ErrorMessages {

    public static final String ERROR_EMPTY_VALUE = "You can't use empty username or password";

    public static final String ERROR_NAME_TAKEN = "Name is already in use";

    public static final String THAT_IS_NOT_YOUR_PLAYER = "You can't access other players data";

    public static final String NOT_LOGGED_IN = "You are not Logged In";

    public static final String GAME_DOESNT_EXIST = "No such game";

    public static final String GAME_FULL = "Game is full";


    private ErrorMessages(){
        //this prevents even the native class from
        //calling this actor as well :
        throw new AssertionError();
    }
}
