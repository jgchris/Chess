package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
            System.out.println(errorMessage);
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
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawBoard(out, board, color);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
    private static void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor color) {
        final var lightColor = SET_BG_COLOR_LIGHT_GREY;
        final var darkColor = SET_BG_COLOR_DARK_GREEN;
        final var whitePieceColor = SET_TEXT_COLOR_WHITE;
        final var blackPieceColor = SET_TEXT_COLOR_BLACK;

        boolean white = color == ChessGame.TeamColor.WHITE;
        String topHeader = white ? " A  B  C  D  E  F  G  H " : " H  G  F  E  D  C  B  A";
        out.println("   " + topHeader);
        var currentColor = lightColor;
        for (int i = 0; i< 8; i++) {
            int row = white ? 8-i : i+1;
            out.print(" " + row + " ");
            for (int j = 0; j< 8; j++) {
                int col = white ? j+1 : 8-j;
                out.print(currentColor);
                currentColor = currentColor == lightColor ? darkColor : lightColor;
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    out.print(EMPTY);
                    continue;
                }
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    out.print(whitePieceColor);
                    switch (piece.getPieceType()) {
                        case KING: out.print(WHITE_KING); break;
                        case QUEEN: out.print(WHITE_QUEEN); break;
                        case BISHOP: out.print(WHITE_BISHOP); break;
                        case KNIGHT: out.print(WHITE_KNIGHT); break;
                        case ROOK: out.print(WHITE_ROOK); break;
                        case PAWN: out.print(WHITE_PAWN); break;
                    }
                    out.print(RESET_TEXT_COLOR);
                } else {
                    out.print(blackPieceColor);
                    switch (piece.getPieceType()) {
                        case KING: out.print(BLACK_KING); break;
                        case QUEEN: out.print(BLACK_QUEEN); break;
                        case BISHOP: out.print(BLACK_BISHOP); break;
                        case KNIGHT: out.print(BLACK_KNIGHT); break;
                        case ROOK: out.print(BLACK_ROOK); break;
                        case PAWN: out.print(BLACK_PAWN); break;
                    }
                    out.print(RESET_TEXT_COLOR);

                }


            }
            out.print(RESET_BG_COLOR);
            out.println();
            currentColor = currentColor == lightColor ? darkColor : lightColor;
        }

    }
}
