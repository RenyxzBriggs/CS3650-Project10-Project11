//Author: Renzo Fabrig
//Class: CS 3650 - Computer Architecture
//Assignment: nand2tetris project 11 - Code Generation

import java.util.*;

//Symbol table abstraction
//Associates the identifier names found in the program with identifier properties needed for compilation: type, 
//  kind, and running index.

public class SymbolTable {

    private int staticIndex;
    private int fieldIndex;
    private int argIndex;
    private int varIndex;

    HashMap<String, Identifier> classTable; //class Hashmap
    HashMap<String, Identifier> subroutineTable; //subroutine Hashmap

    //--------Constructor()--------
    //Creates a new empty symbol table
    public SymbolTable() {

        classTable = new HashMap<String, Identifier>();
        subroutineTable = new HashMap<String, Identifier>();

        //Initialize indeces
        staticIndex = 0;
        fieldIndex = 0;
        argIndex = 0;
        varIndex = 0;
    }

    //--------startSubroutine()--------
    //Starts a new subroutine scope
    public void startSubroutine() {
        
        subroutineTable = new HashMap<String, Identifier>();
        argIndex = 0;
        varIndex = 0;
    }

    //--------define()--------
    //Defines a new identifier of a given name, type and kind and assigns it a running index
    public void define(String name, String type, Kind kind) {

        HashMap<String, Identifier> scope;

        switch (kind) {
            case STATIC:

                classTable.put(name, new Identifier(type, kind, staticIndex++));
                break;

            case FIELD:

                classTable.put(name, new Identifier(type, kind, fieldIndex++));
                break;

            case ARG:

                subroutineTable.put(name, new Identifier(type, kind, argIndex++));
                break;

            case VAR:

                subroutineTable.put(name, new Identifier(type, kind, varIndex++));
                break;

        }
    }

    //--------varCount()--------
    //Returns the number of variables of the given kind already defined in the current scope
    public int varCount(Kind kind) {

        switch (kind) {
            case STATIC:

                return staticIndex;

            case FIELD:

                return fieldIndex;

            case ARG:

                return argIndex;

            case VAR:

                return varIndex;

            default:

                return 0;
        }

    }

    //--------kindOf()--------
    //Returns the kind of the named identifier in the current scope
    //If the identifier is unknown in the current scope, returns NONE
    public Kind kindOf(String name) {

        Identifier id = identifierName(name);

        return (id == null ? Kind.NONE : id.getKind());
    }

    //--------typeOf()--------
    //Returns the type of the named identifier in the current scope
    public String typeOf(String name) {

        Identifier id = identifierName(name);

        return (id == null ? null : id.getType());
    }

    //--------indexOf()--------
    //Returns the index assigned to the named identifier
    public int indexOf(String name) {

        Identifier identifier = identifierName(name);

        return (identifier == null ? - 1 : identifier.getIndex());
    }

    //Separate class for identifiers
    class Identifier {

        private String type;
        private Kind kind;
        private int index;

        public Identifier(String type, Kind kind, int index) {

            this.type = type;
            this.kind = kind;
            this.index = index;
        }

        public String getType() {

            return type;
        }

        public Kind getKind() {

            return kind;
        }

        public int getIndex() {

            return index;
        }
    }

    //Enumerate different identifiers
    public enum Kind {STATIC, FIELD, ARG, VAR, NONE}

    private Identifier identifierName(String name) {

        Identifier id = subroutineTable.get(name);
        if (id == null) id = classTable.get(name);
        return id;
    }
}