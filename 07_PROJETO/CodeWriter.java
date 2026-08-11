import java.io.*;

public class CodeWriter {
    private BufferedWriter writer;
    private String fileName;
    private int labelCounter = 0;

    public CodeWriter(String outputPath) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputPath));
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeArithmetic(String command) throws IOException {
        switch (command) {
            case "add": writeBinary("+"); break;
            case "sub": writeBinary("-"); break;
            case "and": writeBinary("&"); break;
            case "or":  writeBinary("|"); break;
            case "neg": writeUnary("-"); break;
            case "not": writeUnary("!"); break;
            case "eq":  writeComparison("JEQ"); break;
            case "gt":  writeComparison("JGT"); break;
            case "lt":  writeComparison("JLT"); break;
            default:
                throw new RuntimeException("Comando aritmético desconhecido: " + command);
        }
    }

    private void writeBinary(String op) throws IOException {
        write("@SP");
        write("AM=M-1");
        write("D=M");
        write("A=A-1");
        write("M=M" + op + "D");
    }

    private void writeUnary(String op) throws IOException {
        write("@SP");
        write("A=M-1");
        write("M=" + op + "M");
    }

    private void writeComparison(String jump) throws IOException {
        String labelTrue = "TRUE_" + labelCounter;
        String labelEnd = "END_" + labelCounter;
        labelCounter++;

        write("@SP");
        write("AM=M-1");
        write("D=M");
        write("A=A-1");
        write("D=M-D");
        write("@" + labelTrue);
        write("D;" + jump);
        write("@SP");
        write("A=M-1");
        write("M=0");
        write("@" + labelEnd);
        write("0;JMP");
        write("(" + labelTrue + ")");
        write("@SP");
        write("A=M-1");
        write("M=-1");
        write("(" + labelEnd + ")");
    }

    public void writePushPop(VMParser.CommandType type, String segment, int index) throws IOException {
        if (type == VMParser.CommandType.C_PUSH) {
            switch (segment) {
                case "constant":
                    write("@" + index);
                    write("D=A");
                    break;
                case "local":
                    pushFromSegment("LCL", index);
                    return;
                case "argument":
                    pushFromSegment("ARG", index);
                    return;
                case "this":
                    pushFromSegment("THIS", index);
                    return;
                case "that":
                    pushFromSegment("THAT", index);
                    return;
                case "temp":
                    write("@" + (5 + index));
                    write("D=M");
                    break;
                case "pointer":
                    write("@" + (3 + index));
                    write("D=M");
                    break;
                case "static":
                    write("@" + fileName + "." + index);
                    write("D=M");
                    break;
                default:
                    throw new RuntimeException("Segmento desconhecido: " + segment);
            }
            write("@SP");
            write("A=M");
            write("M=D");
            write("@SP");
            write("M=M+1");

        } else { // C_POP
            switch (segment) {
                case "local":
                    popToSegment("LCL", index);
                    return;
                case "argument":
                    popToSegment("ARG", index);
                    return;
                case "this":
                    popToSegment("THIS", index);
                    return;
                case "that":
                    popToSegment("THAT", index);
                    return;
                case "temp":
                    write("@SP");
                    write("AM=M-1");
                    write("D=M");
                    write("@" + (5 + index));
                    write("M=D");
                    return;
                case "pointer":
                    write("@SP");
                    write("AM=M-1");
                    write("D=M");
                    write("@" + (3 + index));
                    write("M=D");
                    return;
                case "static":
                    write("@SP");
                    write("AM=M-1");
                    write("D=M");
                    write("@" + fileName + "." + index);
                    write("M=D");
                    return;
                default:
                    throw new RuntimeException("Segmento desconhecido: " + segment);
            }
        }
    }

    private void pushFromSegment(String base, int index) throws IOException {
        write("@" + index);
        write("D=A");
        write("@" + base);
        write("A=D+M");
        write("D=M");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    private void popToSegment(String base, int index) throws IOException {
        write("@" + index);
        write("D=A");
        write("@" + base);
        write("D=D+M");
        write("@R13");
        write("M=D");
        write("@SP");
        write("AM=M-1");
        write("D=M");
        write("@R13");
        write("A=M");
        write("M=D");
    }

    private void write(String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}