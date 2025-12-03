package ui;

import chess.ChessGame;
import client.ServerError;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class LoggedInMenu {
    private final String username;
    private final String token;
    private final String prompt;
    private final ServerFacade facade;
    private HashMap<Integer, Integer> clientIdsToServerIds = null;
    private HashMap<Integer, GameData> serverIdsToGames = null;
    public LoggedInMenu(String username, String token, ServerFacade facade) {
        this.username = username;
        this.token = token;
        this.prompt = "[CHESS (" + username + ")]";
        this.facade = facade;
        System.out.println("Logged in " + username);
    }

    public void loop() {
        while(true) {
            String action = Menu.getInput(this.prompt);
            int i = action.indexOf(" ");
            String command;
            String arguments = "";
            if (i == -1) {
                command = action;
            } else {
                command = action.substring(0, i);
                arguments = action.substring(i+1);
            }
            if (Objects.equals(command, "help")) {
                help();
            } else if (Objects.equals(command, "logout")) {
                break;
            } else if (Objects.equals(command, "create")) {
                create(arguments);
            } else if (Objects.equals(command, "list")) {
                list();
            } else if (Objects.equals(command, "play")) {
                play(arguments);
            } else if (Objects.equals(command, "observe")) {
                observe(arguments);
            } else{
                System.out.println("Enter 'help' for instructions on how to use the program");
            }
        }
    }
    private void create(String arguments) {
        String[] patterns = new String[] {"[a-zA-Z][^\s]*"};
        String[] args = Menu.parseArgs(arguments, patterns, "Usage: create <NAME>");
        if (args == null) {
            return;
        }
        String gameName = args[0];

        int gameId;
        try {
            gameId = facade.createGame(token, gameName);
        } catch (ServerError e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Game successfully created!");
    }
    private void list() {
        ArrayList<GameData> games;
        try {
            games = (ArrayList<GameData>) facade.listGames(token);
        } catch (ServerError e) {
            System.out.println(e.getMessage());
            return;
        }
        HashMap<Integer, Integer> clientToServer = new HashMap<>();
        HashMap<Integer, GameData> serverToGame = new HashMap<>();
        for (int i = 0; i< games.size(); i++) {
            GameData game = games.get(i);
            clientToServer.put(i+1,game.gameID());
            serverToGame.put(game.gameID(), game);
            System.out.println((i+1) + ": " + game.gameName() + "| WHITE: " + game.whiteUsername() + ", BLACK: " + game.blackUsername());
        }
        this.serverIdsToGames = serverToGame;
        this.clientIdsToServerIds = clientToServer;
    }
    private void play(String arguments) {
        String[] patterns = new String[] {"[1-9][0-9]*", "WHITE|BLACK"};
        String[] args = Menu.parseArgs(arguments, patterns, "Usage: play <ID> [WHITE|BLACK]");
        if (args == null) {
            return;
        }
        String id = args[0];
        if (clientIdsToServerIds == null) {
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        Integer gameID = clientIdsToServerIds.get(Integer.parseInt(id));
        if (gameID == null) {
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        String playerColor = args[1];
        startGame(playerColor, gameID);
    }
    private void observe(String arguments) {
        String[] patterns = new String[] {"[1-9][0-9]*"};
        String[] args = Menu.parseArgs(arguments, patterns, "Usage: observe <GameId>");
        if (args == null) {
            return;
        }
        String id = args[0];
        if (clientIdsToServerIds == null) {
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        Integer gameID = clientIdsToServerIds.get(Integer.parseInt(id));
        if (gameID == null) {
            System.out.println("Game id not found (use list to get games)");
            return;
        }

        if (serverIdsToGames == null) {
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        GameData game =serverIdsToGames.get(gameID);
        if (game == null){
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        Menu.printBoard(game.game().getBoard(), ChessGame.TeamColor.WHITE);

        //TODO: enter observe mode

    }

    private void startGame(String playerColor, int gameId) {
        try {
            facade.joinGame(token, playerColor, gameId);
        } catch (ServerError e) {
            System.out.println(e.getMessage());
            return;
        }
        if (serverIdsToGames == null) {
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        GameData game =serverIdsToGames.get(gameId);
        if (game == null){
            System.out.println("Game id not found (use list to get games)");
            return;
        }
        Menu.printBoard(game.game().getBoard(), ChessGame.TeamColor.valueOf(playerColor));
        //TODO: enter game mode


    }

    private void help() {
        System.out.println("create <NAME> — Create a game");
        System.out.println("list — List all ongoing games");
        System.out.println("play <ID> [WHITE|BLACK] — Join a game by game number");
        System.out.println("observe <ID> — Watch a game by game number");
        System.out.println("logout — Log out");
        System.out.println("help — Print this menu");
    }
}
