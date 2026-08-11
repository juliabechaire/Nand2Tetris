import java.io.*;

public class VMTranslator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java VMTranslator <arquivo.vm ou pasta>");
            return;
        }

        File input = new File(args[0]);
        try {
            if (input.isDirectory()) {
                translateDirectory(input);
            } else {
                translateSingleFile(input);
            }
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void translateSingleFile(File input) throws IOException {
        String outputPath = input.getPath().replace(".vm", ".asm");
        CodeWriter writer = new CodeWriter(outputPath);
        processFile(input, writer);
        writer.close();
    }

    private static void translateDirectory(File dir) throws IOException {
        File[] vmFiles = dir.listFiles((d, name) -> name.endsWith(".vm"));
        if (vmFiles == null || vmFiles.length == 0) {
            System.out.println("Nenhum arquivo .vm encontrado em " + dir.getPath());
            return;
        }
        String outputPath = dir.getPath() + File.separator + dir.getName() + ".asm";
        CodeWriter writer = new CodeWriter(outputPath);
        for (File vmFile : vmFiles) {
            processFile(vmFile, writer);
        }
        writer.close();
    }

    private static void processFile(File vmFile, CodeWriter writer) throws IOException {
        String fileName = vmFile.getName().replace(".vm", "");
        writer.setFileName(fileName);

        VMParser parser = new VMParser(vmFile.getPath());
        while (parser.hasMoreLines()) {
            parser.advance();
            VMParser.CommandType type = parser.commandType();

            if (type == VMParser.CommandType.C_ARITHMETIC) {
                writer.writeArithmetic(parser.arg1());
            } else if (type == VMParser.CommandType.C_PUSH || type == VMParser.CommandType.C_POP) {
                writer.writePushPop(type, parser.arg1(), parser.arg2());
            }
            // labels/goto/function/call/return entram no Projeto 08
        }
        parser.close();
    }
}