import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

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
            System.out.println(command.substring(5));
        }
        else if (command.startsWith("type ")){
            type_command(command);
        }
        else if (command.equals("pwd")){
            System.out.println(currentDirectory.getAbsolutePath());
        }
        else if (command.startsWith("cd ")){
            String directoryName = command.substring(3);
            File directory = new File(directoryName);
            if (directory.isDirectory()) {
                currentDirectory = directory;
            } else {
                System.out.println("cd: " + directoryName + ": No such file or directory");
            }
        }
        else{
            String[] commandParts = command.split("\\s+");
            if (findExecutable(commandParts[0]) != null) {
                new ProcessBuilder(commandParts)
                    .directory(currentDirectory)
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                System.out.println(commandParts[0] + ": command not found");
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
}