package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessSQLException;
import io.javalin.http.Context;
import models.LogoutRequest;
import services.LogoutService;

import java.util.Map;

public class LogoutHandler {
    private final LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public void logout(Context ctx) {
        String authToken = ctx.header("authorization");

        LogoutRequest logoutObject = new LogoutRequest(authToken);
        try{
            logoutService.logout(logoutObject);
            ctx.status(200);
            ctx.result("{}");
        }
        catch (DataAccessException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch (DataAccessSQLException e) {
            ctx.status(e.getStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}