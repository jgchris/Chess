package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {
    /**
     * Static methods to make menus work seamlessly.
     */
    public static String getInput(String prompt) {
        System.out.println();
        System.out.printf(prompt + " >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static String[] parseArgs(String args, String[] patterns, String errorMessage) {
        if (args == null) {
            return null;
        }
        String[] arr = args.split(" ");
        if (arr.length != patterns.length) {
            System.out.println(errorMessage);
            return null;
        }
        for(int i = 0; i< arr.length; i++) {
            String a = arr[i];
            String reg = patterns[i];
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(a);
            boolean matchFound = matcher.find();
            if (!matchFound) {
                System.out.println("Invalid arguments");
                System.out.println(errorMessage);
                return null;
            }
        }
        return arr;

    }
    public static void printBoard(ChessBoard board, ChessGame.TeamColor color) {
        System.out.println("Hello I am a chessboard");

    }
}
