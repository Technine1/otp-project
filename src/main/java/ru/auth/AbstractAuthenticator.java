package ru.auth;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import ru.model.Role;
import ru.model.User;
import ru.repository.CrudRepository;
import ru.service.TokenService;

public abstract class AbstractAuthenticator extends Authenticator {

    private final CrudRepository repository;

    private final TokenService tokenService;

    public AbstractAuthenticator(TokenService tokenService, CrudRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @Override
    public Result authenticate(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Failure(401);
        }

        String token = authHeader.substring(7);
        String username = tokenService.getUsernameFromToken(token);
        if (repository.getById(username, User.class) == null) {
            return new Failure(403);
        }
        if (!tokenService.validateToken(token)) {
            return new Failure(403);
        }

        Role role = tokenService.getRoleFromToken(token);
        if (!validateRole(role)) {
            return new Failure(403);
        }

        return new Success(new Principal(username, "jwt", role));
    }

    protected abstract boolean validateRole(Role role);
}
