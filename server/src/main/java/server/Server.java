package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import models.*;
import services.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.post;

public class Server {

    private final Javalin javalin;
    private final UserDAO users = new MemoryUserDAO();
    private final AuthDAO auths = new MemoryAuthDAO();
    private final GameDAO games = new MemoryGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::list)
                .post("/game", this::create)
                .put("/game", this::join)
                .delete("/db", this::clear)
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
        RegisterRequest registerObject = new RegisterRequest(username, password, email);
        try{
            Auth newAuth = registerService.register(registerObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username",newAuth.username(),"authToken",newAuth.authToken())));
        }
        catch (DataAccessException e) {
            ctx.status(403);
        }
    }

    private void login(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        LoginService loginService = new LoginService(users, auths);
        LoginRequest loginObject = new LoginRequest(username, password);
        try{
            Auth newAuth = loginService.login(loginObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username",newAuth.username(),"authToken",newAuth.authToken())));
        }
        catch (DataAccessException e) {
            ctx.status(401);
        }
    }

    private void logout(Context ctx) {
        String authToken = ctx.header("authorization");

        LogoutService logoutService = new LogoutService(auths);
        LogoutRequest logoutObject = new LogoutRequest(authToken);
        try{
            logoutService.logout(logoutObject);
            ctx.status(200);
            ctx.result("{}");
        }
        catch (DataAccessException e) {
            ctx.status(401);
        }
    }

    private void list(Context ctx) {
        String authToken = ctx.header("authorization");

        ListService listService = new ListService(games, auths);
        ListRequest listObject = new ListRequest(authToken);
        try{
            var listOfGames = listService.list(listObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("games",listOfGames)));
        }
        catch (DataAccessException e) {
            ctx.status(401);
        }
    }

    private void create(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("authorization");
        String gameName = (String) body.get("gameName");

        CreateService createService = new CreateService(games, auths);
        CreateRequest createObject = new CreateRequest(authToken, gameName);
        try{
            int gameID = createService.create(createObject);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("gameID",gameID)));
        }
        catch (DataAccessException e) {
            ctx.status(401);
        }
    }

    private void join(Context ctx) {
        var body = new Gson().fromJson(ctx.body(), Map.class);
        String authToken = ctx.header("authorization");
        String playerColor = (String) body.get("playerColor");
        int gameID = ((Number) body.get("gameID")).intValue();

        JoinService joinService = new JoinService(games, auths);
        JoinRequest joinObject = new JoinRequest(authToken, playerColor,gameID);
        try{
            joinService.join(joinObject);
            ctx.status(200);
            ctx.result("{}");
        }
        catch (DataAccessException e) {
            ctx.status(401);
        }
    }


    private void clear(Context ctx) {

        ClearService clearService = new ClearService(games, auths, users);
        clearService.clear();
        ctx.status(200);
        ctx.result("{}");
    }
}
