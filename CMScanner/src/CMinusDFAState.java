/**
 * Created by Bobby on 2/13/2016.
 * Enumerated Type of all the various states the scanner can be in.
 */
public enum CMinusDFAState {
    START,
    ASSIGN_OR_EQUAL,
    NEQ,
    LT_OR_LE,
    GT_OR_GE,
    DIV_OR_COMMENT,
    START_COMMENT,
    NESTED_COMMENT,
    CLOSING_COMMENT,
    INNUM,
    INID,
    DONE
}