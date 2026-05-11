package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import models.Auth;
import models.User;
import services.RegisterService;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.post;

public class Server {

    private final Javalin javalin;
    private final UserDAO users = new MemoryUserDAO();
    private final AuthDAO auths = new MemoryAuthDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        .post("/user", this::register)
//                .post("/session", this::login)
//                .delete("/session", this::logout)
//                .get("/game", this::list)
//                .post("/game", this::create)
//                .put("/game", this::join)
//                .delete("/db", this::clear)
                ;

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String email = (String) body.get("email");

        RegisterService registerService = new RegisterService(users, auths);
        try{
            Auth newAuth = registerService.register(username, password, email);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username",newAuth.username(),"authToken",newAuth.authToken())));
        }
        catch (DataAccessException e) {
            ctx.status(403);
        }
    }
}
