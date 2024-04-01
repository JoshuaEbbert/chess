package ui;

import model.AuthData;
import serverLogic.ServerFacade;
import serverLogic.WebSocketFacade;

import java.io.PrintStream;
import java.util.Scanner;

public class PreloginUI {
    private static final String EXIT_COMMAND = "quit";
    private static final String STATE = "[LOGGED OUT]";
    public void run(PrintStream out, Scanner scanner, ServerFacade server) {
        out.println("♕ Chess Client: Type help to get started ♕");

        String input = "";
        while (!input.equals(EXIT_COMMAND)) {
            out.print(STATE + " >>> ");
            input = scanner.nextLine();

            String[] inputArray = input.split(" ");
            if (inputArray[0].equals("register") && inputArray.length == 4) { // register
                try {
                    AuthData authorized = server.register(inputArray[1], inputArray[2], inputArray[3]);
                    out.println("Successfully registered!");
                    PostloginUI post = new PostloginUI(authorized.username(), authorized.authToken());
                    post.run(out, scanner, server);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if (inputArray[0].equals("login") && inputArray.length == 3) { // login
                try {
                    AuthData authorized = server.login(inputArray[1], inputArray[2]);
                    out.println("Successfully logged in!");
                    PostloginUI post = new PostloginUI(authorized.username(), authorized.authToken());
                    post.run(out, scanner, server);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if (inputArray[0].equals("help")) { // help
                out.println("Commands: \n\tregister <USERNAME> <PASSWORD> <EMAIL>, \n\tlogin <USERNAME> <PASSWORD>, \n\tquit");
            } else if (!input.equals(EXIT_COMMAND)) {
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            }
        };
    }
}
