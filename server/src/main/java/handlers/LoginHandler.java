package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessSQLException;
import io.javalin.http.Context;
import models.Auth;
import models.LoginRequest;
import services.LoginService;

import java.util.Map;

public class LoginHandler {
    private final LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public void login(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        LoginRequest loginObject = new LoginRequest(username, password);
        try {
            Auth newAuth = loginService.login(loginObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username", newAuth.username(), "authToken", newAuth.authToken())));
        } catch (DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch (DataAccessSQLException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}