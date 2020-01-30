package lexer;


import symbols.Type;

import java.io.*;
import java.util.Hashtable;
import java.util.Map;

public class Lexer {
    public static int line = 1;
    private char peek = ' ';
    Map<String, Word> words = new Hashtable<>();

    void reverse(Word w) {
        words.put(w.lexeme, w);
    }

    private BufferedReader reader;

    public Lexer(String path) throws FileNotFoundException {
        reverse(new Word("if", Tag.IF));
        reverse(new Word("else", Tag.ELSE));
        reverse(new Word("while", Tag.WHILE));
        reverse(new Word("do", Tag.DO));
        reverse(new Word("break", Tag.BREAK));
        reverse(new Word("extern", Tag.EXTERN));
        reverse(Word.True);
        reverse(Word.False);
        reverse(Type.Int);
        reverse(Type.Char);
        reverse(Type.Bool);
        reverse(Type.Float);
        reader = new BufferedReader(new FileReader(path));
    }

    void read() throws IOException {
        if (reader.ready()) {
            peek = (char) reader.read();
        }
        if (peek == '#') {
            reader.close();
        }
    }

    boolean read(char c) throws IOException {
        read();
        if (peek != c) {
            return false;
        }
        peek = ' ';
        return true;
    }


    public Token scan() throws IOException {
        for (; ; read()) {
            if (peek == ' ' || peek == '\t' || peek == '\r') {
                continue;
            } else if (peek == '\n') {
                line++;
            } else {
                break;
            }
        }
        switch (peek) {
            case '&':
                if (read('&')) {
                    return Word.and;
                } else {
                    return new Token('&');
                }
            case '|':
                if (read('|')) {
                    return Word.or;
                } else {
                    return new Token('|');
                }
            case '=':
                if (read('=')) {
                    return Word.eq;
                } else {
                    return new Token('=');
                }
            case '!':
                if (read('=')) {
                    return Word.ne;
                } else {
                    return new Token('!');
                }
            case '<':
                if (read('<')) {
                    return Word.le;
                } else {
                    return new Token('<');
                }
            case '>':
                if (read('>')) {
                    return Word.ge;
                } else {
                    return new Token('>');
                }
        }
        //解析数字
        if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                read();
            } while (Character.isDigit(peek));
            if (peek != '.') {
                return new Numeric(v);
            }
            float x = v;
            float d = 10;
            for (; ; ) {
                read();
                if (!Character.isDigit(peek)) {
                    break;
                }
                x = x + Character.digit(peek, 10) / d;
                d = d * 10;
            }
            return new Real(x);
        }
        //解析字符
        if (Character.isLetter(peek)) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                read();
            } while (Character.isLetterOrDigit(peek));
            String s = b.toString();
            Word w = words.get(s);
            if (w != null) {
                return w;
            }
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }
        Token tok = new Token(peek);
        peek = ' ';
        return tok;
    }
}
