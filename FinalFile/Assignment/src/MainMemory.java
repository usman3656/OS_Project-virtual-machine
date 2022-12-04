import java.io.*;
import java.util.ArrayList;


public class MainMemory {//everything starts from here. the main function
    public static void main(String[] args) throws IOException {
        ArrayList<ArrayList> allProcessInstructionSet =readFileProcess();//reads the file into an array list

        for (int i = 0; i < allProcessInstructionSet.size();i++) {// loop outputs each file
            System.out.print("File " + i + ": ");
            for (int j = 0; j < allProcessInstructionSet.get(i).size(); j++)
                System.out.print(allProcessInstructionSet.get(i).get(j) + " ");
            System.out.println();
        }
        System.out.println("-----------------------------------------");
        MemoryDesign VM = new MemoryDesign(allProcessInstructionSet);//the memory design is called which will be manipulating the files used
    }

    //Reads All Process
    public static ArrayList readFileProcess() throws IOException {//reads all files into thier respective location
        ArrayList<ArrayList<String>> allProcesses = new ArrayList<>();
        try {
            FileInputStream flagFile = new FileInputStream("FinalFile/Assignment/demofiles/flags");
            allProcesses.add(readFileDecodeFile(flagFile));
            FileInputStream largeFile = new FileInputStream("FinalFile/Assignment/demofiles/large0");
            allProcesses.add(readFileDecodeFile(largeFile));
            FileInputStream noopFile = new FileInputStream("FinalFile/Assignment/demofiles/noop");
            allProcesses.add(readFileDecodeFile(noopFile));
            FileInputStream p5File = new FileInputStream("FinalFile/Assignment/demofiles/p5");
            allProcesses.add(readFileDecodeFile(p5File));
            FileInputStream powerFile = new FileInputStream("FinalFile/Assignment/demofiles/power");
            allProcesses.add(readFileDecodeFile(powerFile));
            FileInputStream sfullFile = new FileInputStream("FinalFile/Assignment/demofiles/sfull");
            allProcesses.add(readFileDecodeFile(sfullFile));
        }

        catch (IOException error) {//for error handling
            System.out.println("File Directory Not Found ! Re-Specify, It Based On Your Computer's Location");
        }
        return allProcesses;
    }
    
    /*
    --- Read File Decode File Function ---
    - File Is Read From The Location
    - Specify Location Of The File Based on your Directory
    - File Read In Ascii Format
    - Converted To Int
    - Converted To Hex
    - Stored In A String Array
    - Return to Main Function
     */

    public static ArrayList<String> readFileDecodeFile(FileInputStream readFile) throws IOException {//converts file to hex and reads whole file and returns to parent
        ArrayList<String> instructionSet = new ArrayList<String>();
        int i;
        try {
            do {
                i = readFile.read();
                if (i != -1)
                    instructionSet.add(Integer.toHexString(i));
            } while (i != -1);
            readFile.close();
        }
        catch (IOException e) {
            System.out.println("An I/O Error Occurred");
        }
        return instructionSet;
    }
}
