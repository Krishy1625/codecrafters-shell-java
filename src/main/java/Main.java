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
        else{
            System.out.println(command + ": command not found");
        }
        }
    }

    private static void type_command(String command){
    String commandName = command.substring(5);
    
    if(commandName.equals("echo") || commandName.equals("exit") || commandName.equals("type")){
        System.out.println(commandName + " is a shell builtin");
    }
    else{
        String path = System.getenv("PATH");
        boolean found = false;

        if (path != null) {
            for (String pathDir : path.split(java.util.regex.Pattern.quote(File.pathSeparator))) {
                File executable = new File(pathDir, commandName);
                
                if (executable.isFile() && executable.canExecute()) {
                    System.out.println(commandName + " is " + executable.getAbsolutePath());
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            System.out.println(commandName + ": not found");
        }
    }
}
}