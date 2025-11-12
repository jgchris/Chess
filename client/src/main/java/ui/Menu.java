package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Scanner;

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
    public static void printBoard(ChessBoard board, ChessGame.TeamColor color) {

    }
}
