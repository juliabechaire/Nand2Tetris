import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    private final ArrayList<String> instructions = new ArrayList<>();
    private int currentInstructionIndex = -1;

    public Parser(String filepath) {
        try {
            File file = new File(filepath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String cleanLine = clean(line);
                
                if (!cleanLine.isEmpty()) {
                    instructions.add(cleanLine);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Arquivo não encontrado -> " + filepath);
        }
    }

    private String clean(String line) {
        line = line.replaceAll("\\s+", ""); // Remove todos os espaços em branco e tabs
        if (line.contains("//")) {
            line = line.split("//")[0]; // Corta tudo o que vem depois dos comentários
        }
        return line;
    }

    public boolean hasMoreLines() {
        return currentInstructionIndex < instructions.size() - 1; //retorna true se houver mais instruções a serem processadas
    }

    public void advance() {
        currentInstructionIndex++;
    }

    public String currentInstruction() {
        return instructions.get(currentInstructionIndex);
    }

    public String instructionType() {
        String inst = currentInstruction();
        if (inst.startsWith("@")) {
            return "A_INSTRUCTION"; // @2, @i, @LOOP
        } else if (inst.startsWith("(") && inst.endsWith(")")) {
            return "L_INSTRUCTION"; //(LOOP), (END) - Rótulos lógicos
        } else {
            return "C_INSTRUCTION"; // D=A, 0;JMP - Cálculos e saltos
        }
    }

    // Pedaço extraído apenas se for Instrução A ou L 
    public String symbol() {
        String inst = currentInstruction();
        if (instructionType().equals("A_INSTRUCTION")) {
            return inst.substring(1); // Remove o '@' e pega o resto
        } else if (instructionType().equals("L_INSTRUCTION")) {
            return inst.substring(1, inst.length() - 1); // Remove os parênteses '(' e ')'
        }
        return "";
    }


    public String dest() {
        String inst = currentInstruction();
        if (inst.contains("=")) {
            return inst.split("=")[0]; // Pega tudo o que está antes do '='
        }
        return ""; 
    }

    public String comp() {
        String inst = currentInstruction();
        if (inst.contains("=")) {
            inst = inst.split("=")[1]; // Descarta o destino e mantém o resto
        }
        if (inst.contains(";")) {
            return inst.split(";")[0]; // Pega o que ficou antes do ';'
        }
        return inst; // (Ex: D)
    }

    public String jump() {
        String inst = currentInstruction();
        if (inst.contains(";")) {
            return inst.split(";")[1]; // Pega tudo o que está depois do ';'
        }
        return ""; 
    }
}