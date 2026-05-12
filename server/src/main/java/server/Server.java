package server;

import dataaccess.*;
import handlers.*;
import io.javalin.*;
import models.*;
import services.*;



public class Server {

    private final Javalin javalin;
    private final UserDAO users = new MemoryUserDAO();
    private final AuthDAO auths = new MemoryAuthDAO();
    private final GameDAO games = new MemoryGameDAO();
    private final RegisterHandler registerHandler = new RegisterHandler(new RegisterService(users, auths));
    private final LoginHandler loginHandler = new LoginHandler(new LoginService(users, auths));
    private final LogoutHandler logoutHandler = new LogoutHandler(new LogoutService(auths));
    private final ListHandler listHandler = new ListHandler(new ListService(games, auths));
    private final CreateHandler createHandler = new CreateHandler(new CreateService(games, auths));
    private final JoinHandler joinHandler = new JoinHandler(new JoinService(games, auths));
    private final ClearHandler clearHandler = new ClearHandler(new ClearService(games, auths, users));

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        .post("/user", registerHandler::register)
                .post("/session", loginHandler::login)
                .delete("/session", logoutHandler::logout)
                .get("/game", listHandler::list)
                .post("/game", createHandler::create)
                .put("/game", joinHandler::join)
                .delete("/db", clearHandler::clear)
                ;

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
