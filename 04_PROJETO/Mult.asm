// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

//SOBRE A LINGUAGEM
// D: registrador de armazenamento interno da CPU, Guardar valores temporários para cálculos.
// M: representa o valor que está na RAM, O M é totalmente dependente do A. Você não consegue mexer no M sem antes usar o @.
// @: registrador de "apontamento"

// Zera o resultado final
    @R2
    M=0

// CONT começa em 0
    @cont
    M=0

//VERIFICAÇÃO: se um dos termos == 0 pula pro final
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
    D=M //D vai armazenar o valor de i
    @R0
    D=D-M //D agora vai armazenar a diferença
    @END
    D;JEQ    // Se (CONT-R1) == 0 pula para o fim  

// Soma R1 ao valor atual de R2
    @R1
    D=M //D guarda valor de R1
    @R2
    M=M+D //R2 guarda valor da soma parcial

// i++
    @cont
    M=M+1

// Volta para o início do loop para testar de novo
    @LOOP
    0;JMP //pula pra onde esta a etiqueta

(END)
    @END
    0;JMP

