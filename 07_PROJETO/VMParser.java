import java.io.*;
import java.util.*;

public class VMParser {
    private BufferedReader reader;
    private String currentLine;
    private String currentCommand;

    public enum CommandType {
        C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO,
        C_IF, C_FUNCTION, C_RETURN, C_CALL
    }

    private static final Set<String> ARITHMETIC_COMMANDS = new HashSet<>(Arrays.asList(
            "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"
    ));

    public VMParser(String filePath) throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
    }

    public boolean hasMoreLines() throws IOException {
        reader.mark(100000);
        String line = readNextValidLine();
        reader.reset();
        return line != null;
    }

    public void advance() throws IOException {
        currentCommand = readNextValidLine();
    }

    private String readNextValidLine() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = stripComment(line).trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    private String stripComment(String line) {
        int idx = line.indexOf("//");
        if (idx >= 0) {
            return line.substring(0, idx);
        }
        return line;
    }

    public CommandType commandType() {
        String[] parts = currentCommand.split("\\s+");
        String cmd = parts[0];

        if (ARITHMETIC_COMMANDS.contains(cmd)) return CommandType.C_ARITHMETIC;
        switch (cmd) {
            case "push": return CommandType.C_PUSH;
            case "pop": return CommandType.C_POP;
            case "label": return CommandType.C_LABEL;
            case "goto": return CommandType.C_GOTO;
            case "if-goto": return CommandType.C_IF;
            case "function": return CommandType.C_FUNCTION;
            case "return": return CommandType.C_RETURN;
            case "call": return CommandType.C_CALL;
            default:
                throw new RuntimeException("Comando desconhecido: " + cmd);
        }
    }

    public String arg1() {
        CommandType type = commandType();
        if (type == CommandType.C_ARITHMETIC) {
            return currentCommand.split("\\s+")[0];
        }
        if (type == CommandType.C_RETURN) {
            throw new RuntimeException("arg1 não deve ser chamado para C_RETURN");
        }
        return currentCommand.split("\\s+")[1];
    }

    public int arg2() {
        CommandType type = commandType();
        if (type != CommandType.C_PUSH && type != CommandType.C_POP
                && type != CommandType.C_FUNCTION && type != CommandType.C_CALL) {
            throw new RuntimeException("arg2 não se aplica a este comando");
        }
        return Integer.parseInt(currentCommand.split("\\s+")[2]);
    }

    public void close() throws IOException {
        reader.close();
    }
}