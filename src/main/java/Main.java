import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        
        Scanner sc = new Scanner(System.in);
        String path = System.getenv("PATH");
        String[] pathDirs = path.split(";");

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
            if(command.startsWith("type echo") || command.startsWith("type exit") || command.startsWith("type type")){
                System.out.println(command.substring(5) + " is a shell builtin");
            }
            else{
                System.out.println(command.substring(5) + ": not found");
            }
        }
        else{
            System.out.println(command + ": command not found");
        }
        }
    }
}
