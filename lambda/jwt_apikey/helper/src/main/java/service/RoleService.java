package service;

public class RoleService {
    private static RoleService INSTANCE;
    public static RoleService getInstance() {
        if(INSTANCE == null)
            INSTANCE = new RoleService();
        return INSTANCE;
    }

}
