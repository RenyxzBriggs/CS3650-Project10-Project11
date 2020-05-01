//Author: Renzo Fabrig
//Class: CS 3650 - Computer Architecture
//Assignment: nand2tetris project 10 - Syntax Analyzer

import java.util.*;
import java.io.*;

//Recursive top-down parser
public class CompilationEngine {

    private JackTokenizer tokenizer;
    private PrintWriter pw; //initialize PrintWriter object

    //----------------Constructor----------------
    //Creates new compilation engine with given input/output
    //  next routine called must be compileClass()
    public CompilationEngine(JackTokenizer tokenizer, PrintWriter outputWriter) {

        this.tokenizer = tokenizer;
        this.pw = outputWriter;
    }

    //----------------compileClass----------------
    //Compile a complete class
    public void compileClass() {

        //get next token
        tokenizer.advance();

        //XML begins with class 
        pw.println("<class>");
        writeTerminalAdvance(); 
        writeTerminalAdvance(); //print class name
        writeTerminalAdvance(); //print opening {   

        //Print class var dec
        while (sameToken(NonTerminals.CLASS_VAR_DEC)){

            compileClassVarDec();
        }

        //Print subroutine dec
        while (sameToken(NonTerminals.SUBROUTINE_DEC)){

            compileSubroutine();
        }

        writeTerminal();    //print closing }

        //end XML file
        pw.println("</class>");
    }

    //----------------compileClassVarDec()----------------
    //Compiles a static declaration of a field declaration
    public void compileClassVarDec() {

        pw.println("<classVarDec>");

        writeTerminalAdvance(); //Print static or field
        writeTerminalAdvance(); //Print type of var
        writeTerminalAdvance(); //Print name of var

        //separate variables
        while (tokenizer.symbol() == ',') {

            writeTerminalAdvance();
            writeTerminalAdvance();
        }

        writeTerminalAdvance(); //Print semi-colon ;

        //end classVarDec
        pw.println("</classVarDec>");
    }
    
    //----------------compileSubroutine()----------------
    //Compiles a complete method, function, or constructor
    public void compileSubroutine() {

        pw.println("<subroutineDec>");
        writeTerminalAdvance(); //Print constructor or function or method
        writeTerminalAdvance(); //Print return type
        writeTerminalAdvance(); //Print subroutine

        //compile Parameter list
        compileParameterList();

        //opening XML subroutine body
        pw.println("<subroutineBody>");
        writeTerminalAdvance();

        //print var dec
        while (sameToken(NonTerminals.VAR_DEC)) {

            compileVarDec();
        }

        compileStatements();

        writeTerminalAdvance(); //Print closing }

        //end subroutine 
        pw.println("</subroutineBody>");
        pw.println("</subroutineDec>");
    }

    //----------------compileParameterList()----------------
    //Compiles a (possibly empty) parameter list, not including the enclosing "()"
    public void compileParameterList() {

        writeTerminalAdvance();
        pw.println("<parameterList>");

        //Print parameters
        while (sameToken(NonTerminals.TYPE)) {

            writeTerminalAdvance(); //Print var type
            writeTerminalAdvance(); //Print var name

            if (sameToken(JackTokenizer.TokenType.SYMBOL, ",")) {
                writeTerminalAdvance(); //Print comma

            } 
            else {

                break;
            }
        }

        pw.println("</parameterList>");
        //end Parameter List
        writeTerminalAdvance();
    }

    //----------------compileVarDec()----------------
    //Compiles a var declaration
    public void compileVarDec() {

        pw.println("<varDec>");

        writeTerminalAdvance(); //Print var
        writeTerminalAdvance(); //Print type

        while (true) {

            writeTerminalAdvance();
            if (sameToken(JackTokenizer.TokenType.SYMBOL, ",")) {

                writeTerminalAdvance();
            } 
            else {

                writeTerminalAdvance();
                break;
            }
        }

        //end var dec
        pw.println("</varDec>");
    }

    //----------------compileStatements()----------------
    // Compiles a sequence of statements, not including the enclosing "{}"
    public void compileStatements() {

        pw.println("<statements>");

        while(sameToken(NonTerminals.STATEMENT)) {

            if (sameToken(JackTokenizer.TokenType.KEYWORD, "let")) {

                compileLet();
            } 
            else if (sameToken(JackTokenizer.TokenType.KEYWORD, "if")) {

                compileIf();
            } 
            else if (sameToken(JackTokenizer.TokenType.KEYWORD, "while")) {

                compileWhile();
            } 
            else if (sameToken(JackTokenizer.TokenType.KEYWORD, "do")) {

                compileDo();
            } 
            else {

                compileReturn();
            }
        }

        //end XML statement
        pw.println("</statements>");
    }

    //----------------compileDo()----------------
    //Compile a do statement
    public void compileDo() {

        pw.println("<doStatement>");

        writeTerminalAdvance(); //Print "do"

        compileSubroutineCall();

        writeTerminalAdvance(); //Print semi-colon ;

        //end do statement
        pw.println("</doStatement>");
    }

    //----------------compileLet()----------------
    // Compiles a let statement
    public void compileLet() {

        pw.println("<letStatement>");

        writeTerminalAdvance(); //Print "let"
        writeTerminalAdvance(); //Print var name

        if (sameToken(JackTokenizer.TokenType.SYMBOL, "[")) { 

            writeTerminalAdvance();
            compileExpression();
            writeTerminalAdvance(); //Print closing ]
        }
        writeTerminalAdvance(); //Print "="

        compileExpression();

        writeTerminalAdvance(); //Print closing ;

        //end letStatement
        pw.println("</letStatement>");
    }

    //----------------compileWhile()----------------
    //Compile a while statement
    public void compileWhile() {

        pw.println("<whileStatement>");

        writeTerminalAdvance(); //Print "while"

        writeTerminalAdvance(); //Print opening parenthesis (

        compileExpression();

        writeTerminalAdvance(); //Print cosing parenthesis )

        writeTerminalAdvance(); //Print opening {

        compileStatements();

        writeTerminalAdvance(); // print closing }

        //end while statement
        pw.println("</whileStatement>");
    }

    //----------------compileReturn()----------------
    //Compile a return statement
    public void compileReturn() {

        pw.println("<returnStatement>");

        writeTerminalAdvance(); //Print "return"

        while (sameToken(NonTerminals.EXPRESSION)) {

            compileExpression();
        }

        writeTerminalAdvance(); // print semi-colon ;

        //end return statement
        pw.println("</returnStatement>");
    }

    //----------------compileIf()----------------
    //Compiles an if statement, possibly with a trailing else clause
    public void compileIf() {

        pw.println("<ifStatement>");

        writeTerminalAdvance(); //Print "if"

        writeTerminalAdvance(); //Print opening (

        compileExpression();

        writeTerminalAdvance(); //Print closing )

        writeTerminalAdvance(); //Print opening {

        compileStatements();

        writeTerminalAdvance(); //Print closing }

        if (sameToken(JackTokenizer.TokenType.KEYWORD, "else")) {

            writeTerminalAdvance();
            writeTerminalAdvance(); //Print opening {
            compileStatements();
            writeTerminalAdvance(); //Print closing }
        }

        //end if statement 
        pw.println("</ifStatement>");
    }

    //----------------compileSubroutineCall()----------------
    //Compile a subroutine call
    public void compileSubroutineCall() {

        boolean isSubroutine;
        writeTerminalAdvance(); 

        if (sameToken(JackTokenizer.TokenType.SYMBOL, "(")) {

            isSubroutine = true;
        } 
        else if (sameToken(JackTokenizer.TokenType.SYMBOL, ".")) {

            isSubroutine = false;
        }
        else {

            throw new IllegalArgumentException("error");
        }

        writeTerminalAdvance();

        if (isSubroutine) {

            compileExpressionList();
        } 
        else {

            writeTerminalAdvance();
            writeTerminalAdvance();
            compileExpressionList();
        }

        writeTerminalAdvance();
    }

    //----------------compileExpression()----------------
    //Compiles an expression
    public void compileExpression() {

        pw.println("<expression>");

        compileTerm(); //Print expression

        while (sameToken(NonTerminals.OP)) {

            writeTerminalAdvance();
            compileTerm();
        }

        //end expression
        pw.println("</expression>");
    }

    //----------------compileTerm()----------------
    //Compiles a term. Distinguishes between var, array entrry, and subroutine call.
    public void compileTerm() {

        pw.println("<term>");

        if (tokenizer.tokenType() == JackTokenizer.TokenType.INTEGER_CONSTANT ||
                tokenizer.tokenType() == JackTokenizer.TokenType.STRING_CONSTANT ||
                sameToken(NonTerminals.KEYWORD_CONSTANT)) {

            writeTerminalAdvance();
        } 
        else if (sameToken(NonTerminals.UNARY_OP)) {

            writeTerminalAdvance(); //unary operator
            compileTerm();

        } 
        else if (sameToken(JackTokenizer.TokenType.SYMBOL, "(")) {

            writeTerminalAdvance(); //parentheses
            compileExpression();
            writeTerminalAdvance();

        } 
        else if (tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {

            tokenizer.advance();
            char sym = tokenizer.symbol();
            tokenizer.decrement();
            if (sym == '[') { 

                writeTerminalAdvance(); //Print var name
                writeTerminalAdvance(); //Print opening [

                compileExpression();

                writeTerminalAdvance(); //Print closing ]

            } 
            else if (sym == '(' || sym == '.') { 

                compileSubroutineCall();
            } 
            else {

                writeTerminalAdvance(); //Print var name
            }
        }

        //end term
        pw.println("</term>");
    }

    //----------------compileExpressionList()----------------
    //Compile a (possibly empty) comma-separated list of expressions
    public void compileExpressionList() {

        pw.println("<expressionList>");

        while (sameToken(NonTerminals.EXPRESSION)) {

            compileExpression();

            if (sameToken(JackTokenizer.TokenType.SYMBOL, ",")) {

                writeTerminalAdvance(); //Print comma
            } 
            else {

                break;
            }
        }

        //end expression list
        pw.println("</expressionList>");
    }

    //----------------sameToken()----------------
    //check if the tokens match
    public boolean sameToken(NonTerminals category) {

        switch (category) {
            case CLASS:

                return checkToken(JackTokenizer.TokenType.KEYWORD, "class");

            case CLASS_VAR_DEC:

                return (checkToken(JackTokenizer.TokenType.KEYWORD, "static") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "field"));

            case SUBROUTINE_DEC:

                return (checkToken(JackTokenizer.TokenType.KEYWORD, "constructor") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "function") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "method"));

            case TYPE:

                return (checkToken(JackTokenizer.TokenType.KEYWORD, "int") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "char") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "boolean") ||
                        sameToken(NonTerminals.CLASS_NAME));

            case VAR_DEC:

                return (checkToken(JackTokenizer.TokenType.KEYWORD, "var"));

            case SUBROUTINE_NAME:

                return (checkToken(JackTokenizer.TokenType.IDENTIFIER));

            case SUBROUTINE_CALL:

                return checkToken(JackTokenizer.TokenType.IDENTIFIER);

            case STATEMENT:

                return(checkToken(JackTokenizer.TokenType.KEYWORD, "let") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "if") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "while") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "do") ||
                        checkToken(JackTokenizer.TokenType.KEYWORD, "return"));

            case EXPRESSION:

                return sameToken(NonTerminals.TERM);

            case TERM:

                return(checkToken(JackTokenizer.TokenType.INTEGER_CONSTANT) ||
                        checkToken(JackTokenizer.TokenType.STRING_CONSTANT) ||
                        sameToken(NonTerminals.KEYWORD_CONSTANT) ||
                        sameToken(NonTerminals.VAR_NAME) ||
                        sameToken(NonTerminals.SUBROUTINE_CALL) ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "(") ||
                        sameToken(NonTerminals.UNARY_OP));

            case UNARY_OP:

                return (sameToken(JackTokenizer.TokenType.SYMBOL, "-") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "~"));

            case OP:

                return (sameToken(JackTokenizer.TokenType.SYMBOL, "+") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "-") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "*") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "/") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "&") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "|") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "<") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, ">") ||
                        sameToken(JackTokenizer.TokenType.SYMBOL, "="));

            case KEYWORD_CONSTANT:

                return (sameToken(JackTokenizer.TokenType.KEYWORD, "true") ||
                        sameToken(JackTokenizer.TokenType.KEYWORD, "false") ||
                        sameToken(JackTokenizer.TokenType.KEYWORD, "null") ||
                        sameToken(JackTokenizer.TokenType.KEYWORD, "this"));

            default:
                return false;
        }
    }

    public boolean sameToken(JackTokenizer.TokenType type, String value) {

        return (tokenizer.tokenType() == type && tokenizer.getToken().equals(value));
    }

    //Enumerates NonTerminals
    private static enum NonTerminals {CLASS, CLASS_VAR_DEC, TYPE, SUBROUTINE_DEC, PARAMETER_LIST, SUBROUTINE_BODY, VAR_DEC, CLASS_NAME, SUBROUTINE_NAME, VAR_NAME, STATEMENT, SUBROUTINE_CALL,EXPRESSION, TERM, OP, UNARY_OP, KEYWORD_CONSTANT}

    //Writes terminal and gets the next token
    public void writeTerminalAdvance() {

        //write Terminals to output file
        writeTerminal();
        //get next token
        tokenizer.advance();
    }
    //----------------writeTerminal()----------------
    public void writeTerminal() {

        switch (tokenizer.tokenType()) {

            case IDENTIFIER:

                pw.println("<identifier> " + tokenizer.identifier() + " </identifier>");
                break;

            case KEYWORD:

                pw.println("<keyword> " + tokenizer.keyWord() + " </keyword>");
                break;
                
            case SYMBOL:

                char sym = tokenizer.symbol();
                String printedValue = null;

                switch (sym) {
                    case '<':

                        printedValue = "&lt;";
                        break;

                    case '>':

                        printedValue = "&gt;";
                        break;

                    case '&':

                        printedValue = "&amp;";
                        break;

                    case '"':

                        printedValue = "&quot;";
                        break;

                    default:

                        printedValue = String.valueOf(sym);
                        break;
                }

                pw.println("<symbol> " + printedValue + " </symbol>");
                break;

            case STRING_CONSTANT:

                pw.println("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>");
                break;

            case INTEGER_CONSTANT:

                pw.println("<integerConstant> " + tokenizer.intVal() + " </integerConstant>");
                break;

        }
    }

    //----------------checkToken()----------------
    //Checks the token's validity
    public boolean checkToken(JackTokenizer.TokenType type, String value) {

        return (tokenizer.tokenType() == type && tokenizer.getToken().equals(value));
    }

    public boolean checkToken(JackTokenizer.TokenType type) {

        return (tokenizer.tokenType() == type);
    }

}