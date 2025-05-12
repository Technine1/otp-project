package ru.auth;

import ru.model.Role;
import ru.repository.CrudRepository;
import ru.service.TokenService;

public class AdminAuthenticator extends AbstractAuthenticator {
    public AdminAuthenticator(TokenService tokenService, CrudRepository repository) {
        super(tokenService, repository);
    }

    @Override
    protected boolean validateRole(Role role) {
        return role.equals(Role.ADMIN);
    }
}
