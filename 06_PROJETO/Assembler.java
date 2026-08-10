import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Assembler {

    public static void main(String[] args) {
        // Altere o nome abaixo para o arquivo .asm que você deseja testar (ex: Add.asm, Max.asm)
        String inputFile = "Add.asm"; 
        String outputFile = inputFile.replace(".asm", ".hack");

        SymbolTable symbolTable = new SymbolTable();
        
        Parser firstPassParser = new Parser(inputFile);
        int romAddress = 0;

        while (firstPassParser.hasMoreLines()) {
            firstPassParser.advance();
            String type = firstPassParser.instructionType();

            if (type.equals("A_INSTRUCTION") || type.equals("C_INSTRUCTION")) {
                // Instruções reais ocupam espaço na memória ROM do Hack
                romAddress++; 
            } else if (type.equals("L_INSTRUCTION")) {
                // Achamos um rótulo ex: (LOOP). Guardamos ele apontando para a PRÓXIMA instrução real
                String label = firstPassParser.symbol();
                symbolTable.addEntry(label, romAddress);
            }
        }

        Parser secondPassParser = new Parser(inputFile);
        int nextAvailableRamAddress = 16; // Variáveis criadas pelo usuário começam na RAM[16]

        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            while (secondPassParser.hasMoreLines()) {
                secondPassParser.advance();
                String type = secondPassParser.instructionType();

                if (type.equals("A_INSTRUCTION")) {
                    String symbol = secondPassParser.symbol();
                    int address;

                    // Verifica se o símbolo é um número puro (ex: @2) ou um texto (ex: @i ou @LOOP)
                    if (symbol.matches("\\d+")) { 
                        address = Integer.parseInt(symbol);
                    } else {
                        // Se for texto, consulta a nossa tabela de símbolos
                        if (!symbolTable.contains(symbol)) {
                            // Se for uma variável nova, aloca na próxima gaveta livre da RAM
                            symbolTable.addEntry(symbol, nextAvailableRamAddress);
                            nextAvailableRamAddress++;
                        }
                        address = symbolTable.getAddress(symbol);
                    }

                    // Conversão de Decimal para String Binária de 15 bits
                    String binary15 = String.format("%15s", Integer.toBinaryString(address)).replace(' ', '0');
                    // A instrução A sempre começa com o bit '0' na frente
                    printWriter.println("0" + binary15);

                } else if (type.equals("C_INSTRUCTION")) {
                    // Extrai as partes textuais da Instrução C
                    String destMnemonic = secondPassParser.dest();
                    String compMnemonic = secondPassParser.comp();
                    String jumpMnemonic = secondPassParser.jump();

                    // Traduz as partes usando a nossa classe Code.java
                    String destBits = code.dest(destMnemonic);
                    String compBits = code.comp(compMnemonic);
                    String jumpBits = code.jump(jumpMnemonic);

                    // A instrução C sempre começa com os bits de controle '111' na frente
                    printWriter.println("111" + compBits + destBits + jumpBits);
                }
                // Se for L_INSTRUCTION (rótulo), o segundo passo ignora completamente!
            }

            printWriter.close();
            System.out.println("Sucesso! Arquivo binário gerado com sucesso em: " + outputFile);

        } catch (IOException e) {
            System.out.println("Erro ao tentar gravar o arquivo de saída.");
        }
    }
}