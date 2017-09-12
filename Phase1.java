import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bhushan
 */
public class Phase1 {

    /**
     * @param args the command line arguments
     */
    // static int[] accepted_states=new int[]{1,2};
    static int[][] transition_table=new int[][]{{0,1,2,3,4,5,6,8,9},
                                                {1,1,1,99,4,5,6,99,99},
                                                {2,99,2,3,4,5,6,99,99},
                                                {3,99,99,3,99,99,99,99,99},
                                                {4,1,2,3,4,5,6,8,9},
                                                {5,5,5,5,5,99,99,8,5},
                                                {6,6,6,6,6,6,6,8,5},
                                                 {7,1,1,99,4,99,99,99,9},
                                                 {8,1,2,3,4,5,6,8,9},
                                                 };
    static String input;
    static int index = -1;
    static Stack st = new Stack();
    static Stack pos = new Stack();
    static char NextChar = '~';

    static FileWriter fw;
    static BufferedWriter bw;

    static List < String > reserved_words = Arrays.asList("int", "void", "if", "while", "return", "read", "write", "print", "continue", "break", "binary", "decimal");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here

        String input_program_name = args[0];
        String output_program_name = input_program_name.replaceAll(".c", "_gen.c");
        String read_input;
        FileReader fileReader = new FileReader(input_program_name);
        BufferedReader bufferedReader = new BufferedReader(fileReader);


        fw = new FileWriter(output_program_name);
        bw = new BufferedWriter(fw);



        while ((read_input = bufferedReader.readLine()) != null) {
            input = read_input;
            input = input.trim();
            index = -1;
            while (true) {
                NextWord();
                if (index == input.length() - 1)
                    break;
            }
            bw.write("\n");

        }

        bw.close();
    }

    public static char getNextChar() {
        // get next character 
        index++;
        char c = input.charAt(index);
        return c;
    }

    public static int CharCat(char c) {
        // categorize the characters into different categories.
        List < Character > symbol = Arrays.asList('(', ')', '{', '}', '[', ']', ',', '+', '-', '*', '/', '=', '!', '<', '>', '|', '.', '%', '\\', '&');

        int cat = 8;
        boolean flag = true; // Used for conflict in / and //

        if (c == ';')
            cat = 7;

        if (Character.isLetter(c) || c == '_')
            cat = 1;

        if (Character.isDigit(c))
            cat = 2;

        if (symbol.contains(c)) {
            cat = 3;
            flag = false;
        }

        if (c == ' ')
            cat = 4;

        if (c == '"')
            cat = 5;

        if (index != input.length() - 1)
            if (c == '/' && peekChar() == '/') // check for value of index
        {
            cat = 6;
        }


        if (flag)
            if (c == '#' || c == '\n' || c == '/')
                cat = 6;



        return cat;
    }

    public static char peekChar() {
        // peel the next character in input stream
        index++;
        char c = input.charAt(index);
        index--;
        return c;
    }

    public static String getType(int state) {
        String state_type = null;
        switch (state) {
            case 0:
                state_type = "Initial state"; // initial state
                break;
            case 1:
                state_type = "<identifier>"; // identifier
                break;
            case 2:
                state_type = "<number>"; // number
                break;
            case 3:
                state_type = "<symbol>"; // symbol
                break;
            case 4:
                state_type = "<ws>"; // ws
                break;
            case 5:
                state_type = "<string>"; // string
                break;
            case 6:
                state_type = "<meta>"; //meta
                break;
            case 7:
                state_type = "<reserved word>"; // reserved state
                break;
            case 8:
                state_type = "<statement_end>"; // statement end
                break;

            case 9:
                state_type = "<invalid>"; // invalid       

        }

        return state_type;

    }
    public static void NextWord() throws IOException {
        int state = 0;
        String lexeme = "";
        st.clear();

        while (state != 99 && index < input.length() - 1) {
            NextChar = getNextChar();
            lexeme = lexeme + NextChar + "";
            if (state <= 8) // condition changes when new states are added
            {
                st.clear();
            }

            st.push(state);
            pos.push(index);

            int cat = CharCat(NextChar);

            state = transition_table[state][cat];

            if (state == 9) {
                System.out.println("the input program is illegal");
                break;

            }
            // System.out.println(state);

            if (state == 4) {
                if (!st.empty()) {
                    String temp = getType((int) st.pop());
                    if (temp == "<identifier>") {
                        try {
                            bw.write("cs512" + lexeme);
                        } catch (IOException ex) {
                            Logger.getLogger(Phase1.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else
                        bw.write(lexeme);

                }

                lexeme = "";

            }

            if (!st.empty())
                if ((int) st.peek() == 4)
                    st.pop();


            if (state == 1 && reserved_words.contains(lexeme)) // To check if an identifer is reserved_word or not
            {
                state = 7; // 7 for reserved words
                st.push(state);
                bw.write(lexeme);
                lexeme = "";
            }

            if (NextChar == ' ') //
                bw.write(" ");

            int position;
            boolean flag = false;

            while (state > 8 && !st.empty()) // state not equal to accepted then rollback
            {

                flag = true;
                state = (int) st.pop();
                position = (int) pos.pop();
                index = position - 1;

                lexeme = lexeme.substring(0, lexeme.length() - 1);


                if (getType(state) == "<identifier>") {
                    if (!lexeme.equals("main"))
                        bw.write("cs512" + lexeme);
                    else
                        bw.write(lexeme);
                } else
                    bw.write(lexeme);

                state = 0;


            }
            if (flag == true)
                lexeme = "";
        }


        try {
            if (getType(state) == "<meta>") // If meta then print on newline
            {
                bw.write(lexeme);
                bw.newLine();
            } else
                bw.write(lexeme);
        } catch (IOException ex) {
            Logger.getLogger(Phase1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
