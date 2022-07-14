package com.vitalyr.lox;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private boolean isAtEnd() {
        return current == source.length();
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    public void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(TokenType.LeftParen);
            case ')' -> addToken(TokenType.RightParen);
            case '{' -> addToken(TokenType.LeftBrace);
            case '}' -> addToken(TokenType.RightBrace);
            case '[' -> addToken(TokenType.LeftBracket);
            case ']' -> addToken(TokenType.RightBracket);
            case ',' -> addToken(TokenType.Comma);
            case '.' -> addToken(TokenType.Dot);
            case '-' -> addToken(TokenType.Minus);
            case '+' -> addToken(TokenType.Plus);
            case ';' -> addToken(TokenType.Semicolon);
            case '*' -> addToken(TokenType.Star);

            // Not on the list above
            default -> Lox.error(line, "Unexpected character.");
        }
    }

    private char advance(){
        current++;
        return source.charAt(current);
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
