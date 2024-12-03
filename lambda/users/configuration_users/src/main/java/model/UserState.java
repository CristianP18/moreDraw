package model;

public class UserState {
    public static final Integer OFFLINE = 0, // não está logado
        IDLE = 1, // está logado mas não tem nenhuma remessa para transportar
        ON_DUTY = 2, // está logado e em transito
        ONLINE = 3; // está logado, possui remessas para transportar, mas ainda não iniciou o transporte
}
