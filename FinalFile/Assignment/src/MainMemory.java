import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class MainMemory {
    public static void main(String[] args) throws FileNotFoundException {

        // pass the path to the file as a parameter
        File file = new File("Assignment/p0.txt");
        Scanner input = new Scanner(file);
        // file being read using scanner

        String instructionSet = input.nextLine();
        String[] newInstructionSet = instructionSet.split(" ");
        //array made of file input

        MemoryDesign VM = new MemoryDesign(newInstructionSet);
    }
}