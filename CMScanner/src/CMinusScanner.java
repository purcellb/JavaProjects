/**
 * Scans input for recognizeable cminus source code
 * returns tokens of the types of code it finds
 * <p>
 * Created by Bobby on 2/13/2016.
 */

import java.io.BufferedReader;
import java.io.IOException;

public class CMinusScanner {
    private final char[] line;
    private final char[] tokenChrAr;
    private int lIndex = 0;
    private int bufsize = 0;
    private final BufferedReader br;
    //private PrintWriter pw;
    private int lineno = 1;
    public String tokenString = "DEFAULT";

    //constructor for file input
    CMinusScanner(BufferedReader input) {
        this.br = input;
        this.line = new char[256];
        this.tokenChrAr = new char[256];
//        this.pw = null;
    }

    public int getLineno() {
        return this.lineno;
    }

    private char getNextChar() throws IOException {
        if (this.lIndex >= this.bufsize) {
            if ((bufsize = this.br.read(this.line, 0, 256)) > 0) {
                this.lIndex = 0;
                return this.line[this.lIndex++];
            } else {
                return '\u0000';
            }
        } else {
            return this.line[this.lIndex++];
        }
    }

    //backtrack utility to move the index of the current character back one
    private void backOneChar() {
        --this.lIndex;
    }

    /**
     * Takes a given string identifier given to it after state/token logic
     * and determines if its a reserved word.
     * If its a reserved word return that words token and if its not then its simply an ID.
     **/
    private CMinusToken tokenizeID(String ID) {
        return ID.equalsIgnoreCase("else") ? CMinusToken.ELSE
                : (ID.equalsIgnoreCase("void") ? CMinusToken.VOID
                : (ID.equalsIgnoreCase("int") ? CMinusToken.INT
                : (ID.equalsIgnoreCase("return") ? CMinusToken.RETURN
                : (ID.equalsIgnoreCase("if") ? CMinusToken.IF
                : (ID.equalsIgnoreCase("while") ? CMinusToken.WHILE
                : CMinusToken.ID)))));
    }

    /**
     * Updates the state of the scanner based on the input starting at START.
     * Based on state of the scanner at the end of the switch on state
     * check next character input and if buffer isn't overflowing
     * attempt to classify the input as one of many types of tokens
     **/
    public CMinusToken getTok() throws IOException {
        int index = 0;
        //default setting error in case somethign falls through all the logic
        CMinusToken currentToken = CMinusToken.ERROR;
        CMinusDFAState state = CMinusDFAState.START;

        while (state != CMinusDFAState.DONE) {
            boolean keep;
            char c;
            c = this.getNextChar();
            keep = true;
            switch (state) {
                case START:
                    index = 0;
                    if (Character.isDigit(c)) {
                        state = CMinusDFAState.INNUM;
                    } else if (Character.isLetter(c)) {
                        state = CMinusDFAState.INID;
                    } else if (c == '=') {
                        state = CMinusDFAState.ASSIGN_OR_EQUAL;
                    } else if (c == '!') {
                        state = CMinusDFAState.NEQ;
                    } else if (c == '<') {
                        state = CMinusDFAState.LT_OR_LE;
                    } else if (c == '>') {
                        state = CMinusDFAState.GT_OR_GE;
                    } else if (c == '/') {
                        state = CMinusDFAState.DIV_OR_COMMENT;
                    } else {
                        //checking for stuff I should ignore, execute newline to avoid inf looping
                        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                            keep = false;
                            if (c == '\n') {
                                ++this.lineno;
                            }
                            break;
                        }
                        state = CMinusDFAState.DONE;
                        switch (c) {
                            case '\u0000':
                                keep = false;
                                currentToken = CMinusToken.ENDFILE;
                                break;
                            case '(':
                                currentToken = CMinusToken.LPAREN;
                                break;
                            case ')':
                                currentToken = CMinusToken.RPAREN;
                                break;
                            case '*':
                                currentToken = CMinusToken.TIMES;
                                break;
                            case '+':
                                currentToken = CMinusToken.PLUS;
                                break;
                            case ',':
                                currentToken = CMinusToken.COMMA;
                                break;
                            case '-':
                                currentToken = CMinusToken.MINUS;
                                break;
                            case ';':
                                currentToken = CMinusToken.SEMI;
                                break;
                            case '[':
                                currentToken = CMinusToken.LCB;
                                break;
                            case ']':
                                currentToken = CMinusToken.RCB;
                                break;
                            case '{':
                                currentToken = CMinusToken.LBRACE;
                                break;
                            case '}':
                                currentToken = CMinusToken.RBRACE;
                                break;
                            default:
                                currentToken = CMinusToken.ERROR;

                                System.err.printf("HIT DEFAULT ERROR TOKEN AT LINE: %d\n ON CHARACTER: %c\n", lineno, c);

                                break;
                        }
                    }
                    break;
                case DIV_OR_COMMENT: {
                    if (c == '*') {
                        //its a comment, dont save it,
                        keep = false;
                        state = CMinusDFAState.START_COMMENT;
                    } else {
                        //its div, save and done
                        this.backOneChar();
                        currentToken = CMinusToken.DIV;
                        state = CMinusDFAState.DONE;
                    }
                    break;
                }
                case START_COMMENT: {//TODO Retain comment text?
                    //its a comment make sure were not keeping its contents
                    keep = false;
                    if (c == '/') {
                        //potentially trying to comment inside a comment, that's disallowed so check
                        keep = false;
                        state = CMinusDFAState.NESTED_COMMENT;
                    } else if (c == '*') {
                        //might be ending comment
                        keep = false;
                        state = CMinusDFAState.CLOSING_COMMENT;
                    } else if (c == '\n') {
                        //newlines are allowed, keep executing them
                        ++this.lineno;
                    } else if (c == '\u0000') {
                        currentToken = CMinusToken.ENDFILE;
                        state = CMinusDFAState.DONE;
                        System.err.printf("INPUT FORMAT ERROR AT LINE: %d\nTYPE: UNEXPECTED EOF\n", lineno);
                        System.exit(1);
                    } else {
                        keep = false;
                    }
                    break;
                }
                case NESTED_COMMENT: {
                    keep = false;
                    if (c == '*') {
                        //nested comment = disallowed
                        keep = false;
                        currentToken = CMinusToken.ERROR;
                        System.err.printf("INPUT FORMAT ERROR AT LINE: %d\nTYPE: NESTED COMMENT\n", lineno);
                        System.exit(1);
                    } else if (c == '/') {
                        //false alarm its just a few slashes
                        keep = false;
                    } else if (c == '\n') {
                        //newlines are allowed, keep executing them
                        ++this.lineno;
                    } else {
                        // false alarm its a single slash, still in a comment though
                        state = CMinusDFAState.START_COMMENT;
                    }
                    break;
                }
                case CLOSING_COMMENT: {
                    keep = false;
                    if (c == '/') {
                        //comment end, back to start
                        state = CMinusDFAState.START;
                        break;
                    } else if (c == '*') {
                        //JUST A COUPLE OF STARS, nothing to see here
                        //still possibly closing comment
                        state = CMinusDFAState.CLOSING_COMMENT;
                    } else {
                        if (c == '\n') {
                            //newline
                            ++this.lineno;
                        }
                        //star not followed by star or slash, still in comment
                        state = CMinusDFAState.START_COMMENT;
                    }
                    break;
                }
                case ASSIGN_OR_EQUAL: {
                    //determine if assignment or equals comparison
                    if (c == '=') {
                        currentToken = CMinusToken.EQ;
                        this.backOneChar();
                        state = CMinusDFAState.DONE;
                    } else {
                        this.backOneChar();
                        currentToken = CMinusToken.ASSIGN;
                        state = CMinusDFAState.DONE;
                    }
                    break;
                }
                case NEQ: {
                    //the only acceptable case for '!' is when its followed immediately by '='
                    if (c == '=') {
                        currentToken = CMinusToken.NE;
                        state = CMinusDFAState.DONE;
                    } else {
                        keep = false;
                        this.backOneChar();
                        currentToken = CMinusToken.ERROR;
                        state = CMinusDFAState.DONE;
                    }
                    break;
                }
                case LT_OR_LE: {
                    //check next char to see if its =
                    if (c == '=') {
                        currentToken = CMinusToken.LE;
                        state = CMinusDFAState.DONE;
                    } else {
                        this.backOneChar();
                        currentToken = CMinusToken.LT;
                        state = CMinusDFAState.DONE;
                    }
                    break;
                }
                case GT_OR_GE: {
                    //check next char to see if its =
                    if (c == '=') {
                        currentToken = CMinusToken.GE;
                        state = CMinusDFAState.DONE;
                    } else {
                        this.backOneChar();
                        currentToken = CMinusToken.GT;
                        state = CMinusDFAState.DONE;
                    }
                    break;
                }
                case INNUM: {
                    //check if still number, if not num then done
                    if (!Character.isDigit(c)) {
                        this.backOneChar();
                        keep = false;
                        currentToken = CMinusToken.NUM;
                        state = CMinusDFAState.DONE;
                    }
                    break;
                }
                /**TODO: Possibly adjust scanner to auto correct input to allow for this
                 * Numbers are extremely commonly added to identifiers.
                 * CMinus only alows letter identifiers (letter letter*), this could
                 * create some frequent errors in labeling as in "int var1 = 5;"
                 * the ID would only be tokenized to var followed by a number token then assignment
                 * so the compiler may misread this as tokens INT ID NUM ASSIGN NUM SEMI
                 * may cause frequent errors later trying to compile "NUM ASSIGN NUM"
                 **/
                case INID: {
                    //check if still letter, if not letter then done
                    if (!Character.isLetter(c)) {
                        this.backOneChar();
                        keep = false;
                        state = CMinusDFAState.DONE;
                        currentToken = CMinusToken.ID;
                    }
                    break;
                }
                case DONE:
                    //and so the adventure through the longest switch ive ever written ends
                    break;
                default: {
                    System.err.printf("HIT DEFAULT STATE AT LINE: %d\n", lineno);
                    state = CMinusDFAState.DONE;
                    currentToken = CMinusToken.ERROR;
                }
            }

            if (keep && index <= 256) {
                this.tokenChrAr[index++] = c;
            }
            /** if state logic is done and ive got an id, check if the id is a reserved word
             *  if it is a reserved word, tokenize it and
             */
            //make tokenChrAr a string for comparison in tokenizeid and print out elsewhere
            tokenString = new String(tokenChrAr, 0, index);
            if (state == CMinusDFAState.DONE && currentToken == CMinusToken.ID) {
                currentToken = this.tokenizeID(tokenString);
            }
        }
        return currentToken;
    }
}