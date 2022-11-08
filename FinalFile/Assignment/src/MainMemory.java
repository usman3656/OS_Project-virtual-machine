import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainMemory {
    public static void main(String[] args) throws IOException {
        ArrayList<String> instructionSet = new ArrayList<String>();
        instructionSet = readFileDecodeFile();
        for (int i = 0; i < instructionSet.size();i++)
            System.out.print(instructionSet.get(i) + " ");
        //MemoryDesign VM = new MemoryDesign(instructionSet);
    }

    public static ArrayList<String> readFileDecodeFile() throws IOException {
        ArrayList<String> instructionSet = new ArrayList<String>();
        int i;
        try (FileInputStream fin = new FileInputStream("C:\\Users\\Hp\\OneDrive - Institute of Business Administration\\Documents\\5th Semester\\Operating Systems\\Muneeb - 22809, Bilal - 2811 , Usman - 22850 phase 1\\FinalFile\\Assignment\\demofiles\\flags")) {
            do {
                i = fin.read();
                if (i != -1)
                    instructionSet.add(Integer.toHexString(i));
            } while (i != -1);
            fin.close();
        }
        catch (IOException e) {
            System.out.println("An I/O Error Occurred");
        }
        return instructionSet;
    }
}
