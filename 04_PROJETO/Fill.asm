(RESTART)
    @SCREEN
    D=A
    @address
    M=D

(KBD_CHECK)
    @KBD
    D=M
    @SET_BLACK
    D;JGT
    
    @color
    M=0
    @DRAW_LOOP
    0;JMP

(SET_BLACK)
    @color
    M=-1

(DRAW_LOOP)
    @color
    D=M
    
    @address
    A=M
    M=D

    @address
    M=M+1

    @24576
    D=A
    @address
    D=D-M
    
    @RESTART
    D;JEQ

    @DRAW_LOOP
    0;JMP