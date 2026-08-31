import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws Exception {
        
        Scanner sc = new Scanner(System.in);
        File currentDirectory = new File(System.getProperty("user.dir"));

        while(true){
        System.out.print("$ ");

        String command = sc.nextLine();

        if(command.equals("exit")){
            break;
        }
        else if (command.startsWith("echo ")){
            ArrayList<String> commandParts = parseArguments(command);
            int redirectionIndex = findOutputRedirection(commandParts);
            int outputEnd = redirectionIndex == -1 ? commandParts.size() : redirectionIndex;
            String output = String.join(" ", commandParts.subList(1, outputEnd));
            if (redirectionIndex == -1) {
                System.out.println(output);
            } else {
                try (PrintStream outputStream = new PrintStream(
                    new FileOutputStream(resolveFile(commandParts.get(redirectionIndex + 1), currentDirectory)))) {
                    outputStream.println(output);
                }
            }
        }
        else if (command.startsWith("type ")){
            type_command(command);
        }
        else if (command.equals("pwd")){
            System.out.println(currentDirectory.getAbsolutePath());
        }
        else if (command.startsWith("cd ")){
            String directoryName = command.substring(3);
            String resolvedDirectoryName = directoryName.equals("~")
                ? System.getenv("HOME")
                : directoryName;
            File directory = new File(resolvedDirectoryName);
            if (!directory.isAbsolute()) {
                directory = new File(currentDirectory, resolvedDirectoryName);
            }
            if (directory.isDirectory()) {
                currentDirectory = directory.getCanonicalFile();
            } else {
                System.out.println("cd: " + directoryName + ": No such file or directory");
            }
        }
        else{
            ArrayList<String> commandParts = parseArguments(command);
            int redirectionIndex = findOutputRedirection(commandParts);
            File outputFile = null;
            if (redirectionIndex != -1) {
                outputFile = resolveFile(commandParts.get(redirectionIndex + 1), currentDirectory);
                commandParts.subList(redirectionIndex, redirectionIndex + 2).clear();
            }
            if (findExecutable(commandParts.get(0)) != null) {
                ProcessBuilder processBuilder = new ProcessBuilder(commandParts)
                    .directory(currentDirectory)
                    .inheritIO();
                if (outputFile != null) {
                    processBuilder.redirectOutput(outputFile);
                }
                processBuilder.start().waitFor();
            } else {
                System.out.println(commandParts.get(0) + ": command not found");
            }
        }
        }
    }

    private static void type_command(String command){
        
        String commandName = command.substring(5);

        ArrayList<String> shell_builtins = new ArrayList<>();
        shell_builtins.add("echo");
        shell_builtins.add("exit");
        shell_builtins.add("type");
        shell_builtins.add("pwd");
        shell_builtins.add("cd");
        shell_builtins.add("declare");

        if(shell_builtins.contains(commandName)){
            System.out.println(commandName + " is a shell builtin");
        }
        else{
            File executable = findExecutable(commandName);
            if (executable != null) {
                System.out.println(commandName + " is " + executable.getAbsolutePath());
            } else {
                System.out.println(commandName + ": not found");
            }
        }
    }

    private static File findExecutable(String commandName) {
        String path = System.getenv("PATH");
        if (path == null) {
            return null;
        }

        for (String pathDir : path.split(java.util.regex.Pattern.quote(File.pathSeparator))) {
            File executable = new File(pathDir, commandName);
            if (executable.isFile() && executable.canExecute()) {
                return executable;
            }
        }

        return null;
    }

    private static int findOutputRedirection(ArrayList<String> commandParts) {
        for (int index = 0; index < commandParts.size() - 1; index++) {
            if (commandParts.get(index).equals(">") || commandParts.get(index).equals("1>")) {
                return index;
            }
        }

        return -1;
    }

    private static File resolveFile(String fileName, File currentDirectory) {
        File file = new File(fileName);
        return file.isAbsolute() ? file : new File(currentDirectory, fileName);
    }

    private static ArrayList<String> parseArguments(String command) {
        ArrayList<String> arguments = new ArrayList<>();
        StringBuilder argument = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;

        for (int index = 0; index < command.length(); index++) {
            char character = command.charAt(index);
            if (character == '\\' && !inSingleQuotes && !inDoubleQuotes
                && index + 1 < command.length()) {
                argument.append(command.charAt(++index));
            } else if (character == '\\' && inDoubleQuotes
                && index + 1 < command.length()
                && (command.charAt(index + 1) == '"' || command.charAt(index + 1) == '\\')) {
                argument.append(command.charAt(++index));
            } else if (character == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
            } else if (character == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (Character.isWhitespace(character) && !inSingleQuotes && !inDoubleQuotes) {
                if (argument.length() > 0) {
                    arguments.add(argument.toString());
                    argument.setLength(0);
                }
            } else {
                argument.append(character);
            }
        }

        if (argument.length() > 0) {
            arguments.add(argument.toString());
        }

        return arguments;
    }
}