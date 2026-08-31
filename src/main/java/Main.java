import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        
        Scanner sc = new Scanner(System.in);

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
            String userDirectory = System.getProperty("user.dir");
            System.out.println(userDirectory);
        }
        else{
            String[] commandParts = command.split("\\s+");
            if (findExecutable(commandParts[0]) != null) {
                new ProcessBuilder(commandParts).inheritIO().start().waitFor();
            } else {
                System.out.println(commandParts[0] + ": command not found");
            }
        }
        }
    }

    private static void type_command(String command){
        String commandName = command.substring(5);

        if(commandName.equals("echo") || commandName.equals("exit") || commandName.equals("type")){
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