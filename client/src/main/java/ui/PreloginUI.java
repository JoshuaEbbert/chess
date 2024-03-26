package ui;

import model.AuthData;

import java.io.PrintStream;
import java.util.Scanner;

public class PreloginUI {
    private final String EXIT_COMMAND = "quit";
    private final String STATE = "[LOGGED OUT]";
    public void run(PrintStream out, Scanner scanner, ServerFacade server, WebSocketFacade webSocket) {
        out.println("♕ Chess Client: Type help to get started ♕");

        String input = "";
        while (!input.equals(EXIT_COMMAND)) {
            out.print(STATE + " >>> ");
            input = scanner.nextLine();

            String[] input_array = input.split(" ");
            if (input_array[0].equals("register") && input_array.length == 4) { // register
                try {
                    AuthData authorized = server.register(input_array[1], input_array[2], input_array[3]);
                    out.println("Successfully registered!");
                    PostloginUI post = new PostloginUI(authorized.username(), authorized.authToken());
                    post.run(out, scanner, server, webSocket);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if (input_array[0].equals("login") && input_array.length == 3) { // login
                try {
                    AuthData authorized = server.login(input_array[1], input_array[2]);
                    out.println("Successfully logged in!");
                    PostloginUI post = new PostloginUI(authorized.username(), authorized.authToken());
                    post.run(out, scanner, server, webSocket);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if (input_array[0].equals("help")) { // help
                out.println("Commands: \n\tregister <USERNAME> <PASSWORD> <EMAIL>, \n\tlogin <USERNAME> <PASSWORD>, \n\tquit");
            } else if (!input.equals(EXIT_COMMAND)) {
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            }
        };
    }
}
