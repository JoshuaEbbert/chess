import chess.*;
import ui.GameplayUI;
import ui.PreloginUI;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        PreloginUI pre = new PreloginUI();

        PreloginUI.run(out);
    }
}