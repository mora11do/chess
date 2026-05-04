package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.post;

public class Server {

    private final Javalin javalin;
    private final HashMap<String, String> users = new HashMap<>();
    private final HashMap<String, String> auths = new HashMap<>();

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
        var notSureYet = new Gson().fromJson(ctx.body(), Map.class);
        String username = (String) notSureYet.get("username");
        String password = (String) notSureYet.get("password");
        String email = (String) notSureYet.get("email");
        if (users.containsKey(username)){
            ctx.status(403);
        }
        else{
            users.put(username, password);
            String authToken = UUID.randomUUID().toString();
            auths.put(username, authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("username",username,"authToken",authToken)));
        }
    }
}
