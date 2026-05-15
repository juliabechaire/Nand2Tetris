// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

//SOBRE A LINGUAGEM
// D: registrador de armazenamento interno da CPU, Guardar valores temporários para cálculos.
// M: representa o valor que está na RAM, O M é totalmente dependente do A. Você não consegue mexer no M sem antes usar o @.
// @: registrador de "apontamento"

    @R2
    M=0

    @cont
    M=0

    @R0
    D=M
    @END
    D;JEQ

    @R1
    D=M
    @END
    D;JEQ

(LOOP)
    @cont
    D=M
    @R0
    D=D-M
    @END
    D;JEQ

    @R1
    D=M
    @R2
    M=D+M

    @cont
    M=M+1

    @LOOP
    0;JMP

(END)
    @END
    0;JMP

