package ru.auth;

import ru.model.Role;
import ru.repository.CrudRepository;
import ru.service.TokenService;

public class CommonAuthenticator extends AbstractAuthenticator {
    public CommonAuthenticator(TokenService tokenService, CrudRepository repository) {
        super(tokenService, repository);
    }

    @Override
    protected boolean validateRole(Role role) {
        return true;
    }
}
