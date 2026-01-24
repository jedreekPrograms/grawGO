package pl.edu.go.server.commandInterfaces;

import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.persistence.PersistenceApplication;
import pl.edu.go.server.replay.GameReplayService;

public class PlayStoreCommand implements GameCommand {

    @Override
    public boolean execute(String[] args, GameSession session, ClientConnection sender) {

        if (args.length != 1) {
            sender.send("ERROR Usage: PLAYSTORE <id>");
            return false;
        }

        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            sender.send("ERROR Invalid replay id");
            return false;
        }

        GameReplayService replayService =
                PersistenceApplication.getBean(GameReplayService.class);

        var replay = replayService.buildReplay(id);

        if (replay == null) {
            sender.send("ERROR Replay not found");
            return false;
        }

        for (String line : replay) {
            sender.send(line);
            try {
                Thread.sleep(500); // 0.5 sekundy
            } catch (InterruptedException ignored) {}
        }

        return true;
    }
}
