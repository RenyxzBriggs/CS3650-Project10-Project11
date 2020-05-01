//Author: Renzo Fabrig
//Class: CS 3650 - Computer Architecture
//Assignment: nand2tetris project 11 - Code Generation

import java.util.*;
import java.util.regex.*;

//Tokenizer
public class JackTokenizer {

    //Define each terminal for the Jack language: Keyword, Symbol, integerConstant, StringConstant, identifier.
    //Jack language keywords
    private final String KEYWORD = "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    //Jack language symbols
    private final String SYMBOL = "([{}()\\[\\].,;+-/*&|<>=~])";
    //Decmal number in range of 0...32767
    private final String INTEGER_CONSTANT = "(\\d+)";
    //Sequence of Unicode chars not including double quote or newline
    private final String STRING_CONSTANT = "(\"(?:\\\\\"|[^\"])*\")";
    //Sequence of letters, digits, underscores not starting with a digit
    private final String IDENTIFIER = "([a-zA-Z][a-zA-Z0-9_]*|[_][a-zA-Z0-9_]*)";

    //Use Pattern class from regex package to compile representation of Lexical elements for the Jack language
    private final Pattern PATTERN = Pattern.compile(KEYWORD + "|" + SYMBOL + "|" + INTEGER_CONSTANT + "|" + STRING_CONSTANT + "|" + IDENTIFIER);

    private ArrayList<Token> tokens;    //Create the arraylist to hold the tokens
    private Token currentToken; //"Initially no current token"
    private Matcher matcher;    //create Matcher object
    private int index;          //track the index for the currentToken
 
    //-------------------------------------------------------------------------------
    class Token {

        private String value;
        private TokenType type;

        public Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        //--------Getters--------
        public String getValue() {

            return value;
        }

        public TokenType getType() {

            return type;
        }
    }

    //Enumerate the 5 type of terminals(tokens)
    public enum TokenType {KEYWORD, SYMBOL, IDENTIFIER, INTEGER_CONSTANT, STRING_CONSTANT}

    //-------------------------------------------------------------------------------

    //--------Constructor--------
    //Opens the input file/stream and tokenizes it
    public JackTokenizer(Scanner inputFile) {

        tokens = new ArrayList<>();
        this.matcher = PATTERN.matcher(matchComments(inputFile));
       
        //add respective token type to the arraylist of Tokens
        while (matcher.find()) {

            //1 - KEYWORD
            if (matcher.group(1) != null) {

                tokens.add(new Token(matcher.group(1), TokenType.KEYWORD));
            } 
            //2 - SYMBOL
            else if (matcher.group(2) != null) {

                tokens.add(new Token(matcher.group(2), TokenType.SYMBOL));
            } 
            //3 - INTEGER_CONSTANT
            else if (matcher.group(3) != null) {

                tokens.add(new Token(matcher.group(3), TokenType.INTEGER_CONSTANT));
            } 
            //4 - STRING_CONSTANT
            else if (matcher.group(4) != null) {

                tokens.add(new Token(matcher.group(4), TokenType.STRING_CONSTANT));
            } 
            //5 - IDENTIFIER
            else if (matcher.group(5) != null) {

                tokens.add(new Token(matcher.group(5), TokenType.IDENTIFIER));
            }
        }
        index = -1;
    }

    private String matchComments(Scanner inputFile) {

        String s = "";

        while (inputFile.hasNextLine()) {

            String line = inputFile.nextLine();
            if (line.isEmpty() || line.startsWith("//")) {
                
                continue;
            }
            if (line.contains("//")) {

                line = line.substring(0, line.indexOf("//"));
            }
            if (line.trim().length() == 0){

                continue;
            }
            s += line + " ";
        }

        s = s.replaceAll("/\\*(.|[\\r\\n])*?\\*/", "");

        return s;
    }

    //--------tokenType--------
    //Returns type of the current token
    public TokenType tokenType() {

        return currentToken.getType();
    }

    //--------Getter--------
    //returns the value of the Token
    public String getToken() {

        return currentToken.getValue();
    }

    //-------------------------------------------------------------------------------

    //--------hasMoreTokens--------
    //more tokens in input?
    public boolean hasMoreTokens() {

        return index < tokens.size() - 1;
    }

    //--------advance--------
    //Gets next token from input and makes it currentToken
    //Only called if hasMoreTokens() is true. Initiallly there is no current token.
    public void advance() {

        index++;
        currentToken = tokens.get(index);
    }

    //Decrement the index for the currentToken
    public void decrement() {

        index--;
        currentToken = tokens.get(index);
    }

    //--------keyword--------
    //Returns keyword which is the currentToken
    //Only called if tokenType() is KEYWORD
    public String keyWord() {

        return currentToken.getValue();
    }

    //--------symbol--------
    //Returns character which is the currentToken 
    //Only be called if tokenType() is SYMBOL
    public char symbol() {

        return currentToken.getValue().charAt(0);
    }

    //--------identifier--------
    //Returns the identifier which is the currentToken
    //Only be called if tokenType() is IDENTIFIER
    public String identifier() {

        return currentToken.getValue();
    }

    //--------intVal--------
    //Returns the integer value of the currentToken
    //Only be called if tokenType() is INTEGER_CONSTANT
    public int intVal() {

        return Integer.parseInt(currentToken.getValue());
    }

    //--------stringVal--------
    //Returns the string value of the currentToken without the double quotes
    //Only be called if tokenType() is STRING_CONSTANT
    public String stringVal() {

        String tokenValue = currentToken.getValue();
        return tokenValue.substring(1, tokenValue.length() - 1);
    }

}