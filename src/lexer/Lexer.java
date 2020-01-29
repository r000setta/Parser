package lexer;


import symbols.Type;

import java.io.*;
import java.util.Hashtable;

public class Lexer {
    public static int line = 1;
    char peek = ' ';
    Hashtable<String, Word> words = new Hashtable<>();

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
        reverse(Word.True);
        reverse(Word.False);
        reverse(Type.Int);
        reverse(Type.Char);
        reverse(Type.Bool);
        reverse(Type.Float);
        reader=new BufferedReader(new FileReader(path));
    }

    void readch() throws IOException {
        if (reader.ready()){
            peek=(char)reader.read();
        }
        if (peek=='#'){
            reader.close();
        }
    }

    boolean readch(char c) throws IOException {
        readch();
        if (peek != c) {
            return false;
        }
        peek = ' ';
        return true;
    }


    public Token scan() throws IOException {
        for (; ; readch()) {
            if (peek == ' ' || peek == '\t'||peek=='\r') {
                continue;
            } else if (peek=='\n') {
                line++;
            } else {
                break;
            }
        }
        switch (peek) {
            case '&':
                if (readch('&')) {
                    return Word.and;
                } else {
                    return new Token('&');
                }
            case '|':
                if (readch('|')) {
                    return Word.or;
                } else {
                    return new Token('|');
                }
            case '=':
                if (readch('=')) {
                    return Word.eq;
                } else {
                    return new Token('=');
                }
            case '!':
                if (readch('=')) {
                    return Word.ne;
                } else {
                    return new Token('!');
                }
            case '<':
                if (readch('<')) {
                    return Word.le;
                } else {
                    return new Token('<');
                }
            case '>':
                if (readch('>')) {
                    return Word.ge;
                } else {
                    return new Token('>');
                }
        }
        if (Character.isDigit(peek)){
            int v=0;
            do{
                v=10*v+Character.digit(peek,10);
                readch();
            }while (Character.isDigit(peek));
            if (peek!='.'){
                return new Num(v);
            }
            float x=v;
            float d=10;
            for (;;){
                readch();
                if (!Character.isDigit(peek)){
                    break;
                }
                x=x+Character.digit(peek,10)/d;
                d=d*10;
            }
            return new Real(x);
        }
        if (Character.isLetter(peek)){
            StringBuffer b=new StringBuffer();
            do {
                b.append(peek);
                readch();
            }while (Character.isLetterOrDigit(peek));
            String s=b.toString();
            Word w=words.get(s);
            if (w!=null){
                return w;
            }
            w=new Word(s,Tag.ID);
            words.put(s,w);
            return w;
        }
        Token tok=new Token(peek);
        peek=' ';
        return tok;
    }
}
