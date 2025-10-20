package model;

import Chess.chessboard;

record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
