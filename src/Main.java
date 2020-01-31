import lexer.Lexer;
import parser.Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Lexer lex=new Lexer("D:\\java2\\Parser\\out\\production\\Parser\\test.txt");
        Parser parser=new Parser(lex);
        parser.program();
    }
}
