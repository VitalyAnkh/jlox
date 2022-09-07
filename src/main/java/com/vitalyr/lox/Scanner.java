package com.vitalyr.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scanner {

    private static final Map<String, TokenType> KEYWORDS;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("and", TokenType.And);
        KEYWORDS.put("class", TokenType.Class);
        KEYWORDS.put("else", TokenType.Else);
        KEYWORDS.put("for", TokenType.For);
        KEYWORDS.put("fun", TokenType.Fun);
        KEYWORDS.put("if", TokenType.If);
        KEYWORDS.put("nil", TokenType.Nil);
        KEYWORDS.put("or", TokenType.Or);
        KEYWORDS.put("print", TokenType.Print);
        KEYWORDS.put("return", TokenType.Return);
        KEYWORDS.put("super", TokenType.Super);
        KEYWORDS.put("this", TokenType.This);
        KEYWORDS.put("true", TokenType.True);
        KEYWORDS.put("var", TokenType.Var);
        KEYWORDS.put("while", TokenType.While);
    }

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

            case '!' -> addToken(match('=') ? TokenType.BangEqual : TokenType.Bang);
            case '=' -> addToken(match('=') ? TokenType.EqualEqual : TokenType.Equal);
            case '>' -> addToken(match('=') ? TokenType.GreaterEqual : TokenType.Greater);
            case '<' -> addToken(match('=') ? TokenType.LessEqual : TokenType.Less);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.Slash);
                }

                // Not on the list above
            }
            case ' ', '\r', '\t' -> {
            }
            case '\n' -> line++;
            case '"' -> lexString();
            default -> {
                if (isDigit(c)) {
                    lexNumber();
                } else if (isAlpha(c)) {
                    lexIdentifierOrKeyword();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
            }
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z')) || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void lexString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
        }
        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String stringLiteral = source.substring(start + 1, current - 1);
        addToken(TokenType.String, stringLiteral);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void lexIdentifierOrKeyword() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.get(text);
        if (type == null) type = TokenType.Identifier;
        addToken(TokenType.Identifier);
    }

    private void lexNumber() {
        boolean isFloat = false;
        boolean isIntegerWithDot = false;
        while (isDigit(peek()) && !isAtEnd())
            advance();
        // Maybe a floating number, continue lexing
        if (peek() == '.') {
            advance();
            // Handle things like 1234.abc,
            // we should stop parsing for we
            // only support decimal numbers
            if (!isDigit(peek())) {
                // Discard dot
                current--;
                addToken(TokenType.Integer, Integer.parseInt(source.substring(start, current)));
                return;
            }
            isFloat = true;
            while (isDigit(peek()) && !isAtEnd())
                advance();
        }
        String numberLiteral = source.substring(start, current);
        if (isFloat) {
            addToken(TokenType.Double, Double.parseDouble(numberLiteral));
        } else {
            addToken(TokenType.Integer, Integer.parseInt(numberLiteral));
        }
    }
}
