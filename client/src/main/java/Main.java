import chess.*;
import ui.GameplayUI;
import ui.PreloginUI;
import ui.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(System.in);
        ServerFacade server = new ServerFacade(8080);

        out.print(ERASE_SCREEN);
        PreloginUI pre = new PreloginUI();

        pre.run(out, scanner, server);
    }
}