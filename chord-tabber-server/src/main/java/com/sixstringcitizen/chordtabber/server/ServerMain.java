package com.sixstringcitizen.chordtabber.server;

import com.sixstringcitizen.chordtabber.ChordTabber;
import com.sixstringcitizen.chordtabber.api.ChordJson;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;

import java.nio.file.Path;

public final class ServerMain {

    public static void main(String[] args) {
        int port = 7070;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Path webRoot = Path.of(System.getProperty("chordtabber.web", "../")).toAbsolutePath().normalize();
        ChordTabber tabber = new ChordTabber();

        Javalin app = Javalin.create(config -> config.staticFiles.add(webRoot.toString(), Location.EXTERNAL));

        app.get("/api/describe", ctx -> {
            String chord = ctx.queryParam("chord");
            if (chord == null || chord.isBlank()) {
                ctx.status(HttpStatus.BAD_REQUEST).result("{\"error\":\"Missing chord query parameter\"}");
                return;
            }
            try {
                ctx.contentType("application/json");
                ctx.result(ChordJson.toJson(tabber.describe(chord.trim())));
            } catch (IllegalArgumentException ex) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.result(ChordJson.error(ex.getMessage()));
            }
        });

        app.start(port);
        System.out.println("Chord Tabber server running at http://localhost:" + port + "/");
    }
}
