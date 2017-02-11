/**
 * Created by Bobby on 2/13/2016.
 * Enumerated Type of all possible types of expressions that can be found in C-
 * represented as tokens
 */
public enum CMinusToken {
    ELSE,
    IF,
    RETURN,
    VOID,
    WHILE,
    ERROR,
    ASSIGN,
    ID,
    NUM,
    EQ, NE, LE, LT, GE, GT,
    PLUS, MINUS, TIMES, DIV,
    SEMI,
    COMMA,
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    LCB, RCB,
    INT,
    ENDFILE
}