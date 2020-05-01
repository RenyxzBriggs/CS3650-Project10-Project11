//Author: Renzo Fabrig
//Class: CS 3650 - Computer Architecture
//Assignment: nand2tetris project 11 - Code Generation

import java.util.*;
import java.io.*;

//Top-level driver that sets up and invokes the other modules
public class JackCompiler {

    public static void main(String[] args) {

        //Create arraylist for .jack files
        ArrayList<File> jackFiles = new ArrayList<>();

        //Take the first argument in command line
        File inputFile = new File(args[0]);

        boolean isFile = inputFile.isFile();
        boolean isJackFile = args[0].endsWith(".jack");

        //---------------------------------------------------------------------------------------------
        //Check if file exists
        if (!inputFile.exists()) {

            System.out.println(args[0] + " does not exist");
            System.exit(1);
        } 
        //Check if user passed a single file in command line
        else if (isFile && isJackFile) {
            
            jackFiles.add(inputFile);

        } 
        //Check if user passed a directory file in command line
        else if (inputFile.isDirectory()) {
            
            File[] files = inputFile.listFiles();
            for (File file : files) {

                if (file.getName().endsWith(".jack")) {

                    jackFiles.add(file);
                }
            }
        } 
        else {

            System.out.println("Program use: java JackAnalyzer file.jack/directory");
        }

        //---------------------------------------------------------------------------------------------
        //Tokenize each file in jackFiles ArrayList
        for (File jackFile : jackFiles) {

            //split the file name
            String inputFilename = jackFile.getName().substring(0, jackFile.getName().indexOf(".jack"));
            String outputFile = inputFilename + ".vm";

            //Create JackTokenizer object
            JackTokenizer tokenizer;

            try {

                //tokenize each .jack file
                tokenizer = new JackTokenizer(new Scanner(new FileReader(jackFile)));
            } 
            catch (IOException e) {

                System.out.println("file error");
                continue;
            }

            PrintWriter pw; //initialize PrintWriter object

            try {

                pw = new PrintWriter(outputFile);
            } 
            catch (IOException e) {

                System.out.println("output error");
                continue;
            }

            //Create CompilationEngine object
            CompilationEngine compilationEngine = new CompilationEngine(tokenizer, pw);
            compilationEngine.compileClass();

            if (pw != null) {

                System.out.println("Generating .vm file for " + outputFile);
                pw.close(); //close PrintWriter object
            }

        }
    }

}