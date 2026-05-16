package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessSQLException;
import io.javalin.http.Context;
import services.ClearService;

import java.util.Map;


public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        }
        catch (DataAccessSQLException e) {
        ctx.status(e.getStatusCode());
        ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}