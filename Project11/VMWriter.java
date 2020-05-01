//Author: Renzo Fabrig
//Class: CS 3650 - Computer Architecture
//Assignment: nand2tetris project 11 - Code Generation

import java.io.PrintWriter;

//Output module for generating VM code;
public class VMWriter {

    private PrintWriter pw; 

    //--------Constructor()--------
    //Creates a new file and prepares it for writing
    public VMWriter(PrintWriter writer) {

        pw = writer;
    }

    //--------writePush()--------
    //Writes a VM push command
    public void writePush(Segment segment, int index) {

        pw.println("push " + segment + " " + index);
    }

    //--------writePop()--------
    //Writes a VM pop command
    public void writePop(Segment segment, int index) {

        pw.println("pop " + segment + " " + index);
    }

    //--------writeArithmetic()--------
    //Writes a VM arithmetic command
    public void writeArithmetic(Command command) {

        pw.println(command);
    }

    //--------writeLabel()--------
    //Writes a VM label command
    public void writeLabel(String label) {

        pw.println("label " + label);
    }

    //--------writeGoto()--------
    //Writes a VM goto command
    public void writeGoto(String label) {

        pw.println("goto " + label);
    }

    //--------writeIf()--------
    //Writes a VM if-goto command
    public void writeIf(String label) {

        pw.println("if-goto " + label);
    }

    //--------writeCall()--------
    //Writes a VM call command
    public void writeCall(String name, int nArgs) {

        pw.println("call " + name + " " + nArgs);
    }

    //--------writeFunction()--------
    //Writes a VM function command
    public void writeFunction(String name, int nLocals) {

        pw.println("function " + name + " " + nLocals);
    }

    //--------writeReturn()--------
    //Writes a VM return command
    public void writeReturn() {

        pw.println("return");
    }

    //Enumerate segments
    public enum Segment {

        CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP;

        public String toString() {
            return name().toLowerCase();
        }
    }

    //Enumerate commands
    public enum Command {

        ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT;

        public String toString() {
            return name().toLowerCase();
        }
    }

    //--------writeReturn()--------
    //Closes the output file
    public void close() {

        pw.close();
    }
}