// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

//SOBRE A LINGUAGEM
// D: registrador de armazenamento interno da CPU, Guardar valores temporários para cálculos.
// M: representa o valor que está na RAM, O M é totalmente dependente do A. Você não consegue mexer no M sem antes usar o @.
// @: registrador de "apontamento"

// 1. Zera o resultado final
    @R2
    M=0

// 2. Pega o valor de R1 para usar como contador
    @R1
    D=M
    @count
    M=D

(LOOP)
// 3. Checa se o contador é 0 ou menor. Se for, acaba.
    @count
    D=M
    @END
    D;JLE    // Se count <= 0 pula para o fim 

// 4. Soma R0 ao valor atual de R2
    @R0
    D=M
    @R2
    M=D+M

// 5. Diminui 1 do contador
    @count
    M=M-1

// 6. Volta para o início do loop para testar de novo
    @LOOP
    0;JMP

(END)
    @END
    0;JMP

