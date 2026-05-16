package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessSQLException;
import io.javalin.http.Context;
import models.Auth;
import models.RegisterRequest;
import services.RegisterService;

import java.util.Map;

public class RegisterHandler {
private final RegisterService registerService;

public RegisterHandler(RegisterService registerService) {
    this.registerService = registerService;
}

public void register(Context ctx) {
    var body = new Gson().fromJson(ctx.body(), Map.class);
    String username = (String) body.get("username");
    String password = (String) body.get("password");
    String email = (String) body.get("email");

    RegisterRequest registerObject = new RegisterRequest(username, password, email);
    try{
        Auth newAuth = registerService.register(registerObject);
        ctx.status(200);
        ctx.result(new Gson().toJson(Map.of("username",newAuth.username(),"authToken",newAuth.authToken())));
    }
    catch (DataAccessException e) {
        ctx.status(e.getStatusCode());
        ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
    }
    catch (DataAccessSQLException e) {
        ctx.status(e.getStatusCode());
        ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
    }
}}