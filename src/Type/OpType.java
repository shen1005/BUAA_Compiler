package Type;

public enum OpType {
    ASSIGN,
    SW_1D,
    SW_2D,
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    NEG,
    NOT,
    LOAD_ADDRESS,
    LOAD_ARRAY_1D,
    LOAD_ARRAY_2D,
    PRINT_INT,
    PRINT_STRING,
    GetInt,
    FUNC_DEC,
    PRE_CALL,
    PUSH,
    CALL,
    FIN_CALL,
    SW_RET,
    EXIT,
    RET_VOID,
    RET_VALUE,
    GLOBAL_DEC,
    GOTO,
    PUT_LABEL,
    LE,
    GE,
    LT,
    GT,
    EQ,
    NE,
    BNEZ,   // branch not equal zero
    BEQZ,   // branch equal zero
}
