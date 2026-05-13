// Inicialização: define o ponteiro para o início da tela
(RESTART)
    @SCREEN
    D=A
    @address
    M=D         // address = 16384 (base da tela)

// Loop de verificação do teclado (Polling)
(KBD_CHECK)
    @KBD
    D=M         // Lê o valor do teclado
    @SET_BLACK
    D;JGT       // Se D > 0 (tecla pressionada), vai para SET_BLACK
    
    @color
    M=0         // Caso contrário, cor = branco (0)
    @DRAW_LOOP
    0;JMP

(SET_BLACK)
    @color
    M=-1        // Cor = preto (-1 são 16 bits em 1)

// Loop que percorre toda a memória da tela
(DRAW_LOOP)
    @color
    D=M         // Pega a cor atual (0 ou -1)
    
    @address
    A=M         // Aponta para o endereço atual da tela
    M=D         // Pinta 16 pixels com a cor

    @address
    M=M+1       // Move o ponteiro para a próxima palavra (16 pixels)
    
    // Verifica se já pintamos até o fim da tela
    @24576      // Endereço do Teclado (fim da tela + 1)
    D=A
    @address
    D=D-M       // D = 24576 - endereço_atual
    
    @RESTART
    D;JEQ       // Se D == 0, a tela acabou. Volta para o início.

    @DRAW_LOOP
    0;JMP       // Senão, continua pintando o próximo bloco