import java.io.FileNotFoundException;
import java.util.*;
import java.lang.Math;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.ArrayList;

//all initialization/
public class MemoryDesign {
    private final byte toBeInserted = 50;
    private ByteStack newStack = new ByteStack(toBeInserted);
    private PCB[] allPCB = new PCB[6];
    private short[] generalPurposeRegister = new short[16];
    SpecialPurposeRegister SPR = new SpecialPurposeRegister();
    public final byte[] Memory = new byte[65536];
    public short progamCounter;
    boolean[] freeFrameList = new boolean[512];
    int[][] processPages = new int[12][12];
    //-----------------------------------------
    Queue<PCB> highPriorityQueue;
    Queue<PCB> lowPriorityQueue;
    Queue<PCB> runningQueue;

    public MemoryDesign(ArrayList<ArrayList> instructionSet) throws FileNotFoundException {
        // Generating PCB For All Processes
        //------------------------------------------------
        for (int i = 0; i < instructionSet.size(); i++) {
            generatePCB(instructionSet.get(i), i);
            fillpages(instructionSet.get(i), (int) allPCB[i].getProcessDataSize(), (int) allPCB[i].getProcessCodeSize(), i);
            populateQueue(i, allPCB[i].getProcessPriority());
        }

        //------------------------------------------------
        //initialising spr labels
        SPR.intializeSpecialPurposeRegister();
        short InstructionRegister = 0;
        SPR.newSPR[2].value = (short) (instructionSet.size() - 1);
        SPR.newSPR[9].value = progamCounter;
        SPR.newSPR[10].value = InstructionRegister;
        //rollTheDice();
    }


    public void generatePCB(ArrayList<String> instructionSet, int pcbNumber) throws FileNotFoundException {
        String[] pcbKit = new String[8];
        for (int processByte = 0; processByte < 8; processByte++)
            pcbKit[processByte] = instructionSet.get(processByte);
        allPCB[pcbNumber] = new PCB(pcbKit, instructionSet.size());
    }

    public void fillpages(ArrayList instructionset, int datasize, int codesize, int processnum) {

        byte[] data = new byte[datasize];
        byte[] code = new byte[codesize];
        System.out.println();


        for (int i = 8; i < datasize + 8; i++) {
            data[i - 8] = (byte) Integer.parseInt(instructionset.get(i).toString(), 16);
        }
        for (int i = datasize + 8; i < 8 + datasize + codesize; i++) {
            code[i - datasize - 8] = (byte) Integer.parseInt(instructionset.get(i).toString(), 16);
        }


     /*   System.out.println(Arrays.toString(data));
        System.out.println(Arrays.toString(code));*/


        int d = Math.floorDiv(data.length, 128) + 1;
        int c = Math.floorDiv(code.length, 128) + 1;

        int dx = Math.floorMod(data.length, 128);
        int cx = Math.floorMod(data.length, 128);

        for (int i = 0; i < d; i++) {
            int z = checknextfreepage();
            freeFrameList[z] = true;
            processPages[processnum * 2][i] = z + 1;


            if (i < d - 1) {
                for (int x = 0; x < 128; x++) {
                    Memory[z * 128 + x] = data[i * 128 + x];
                }
            }
            else {

                for (int x = 0; x < dx; x++) {

                    Memory[z * 128 + x] = data[i * 128 + x];
                }
            }
        }

        for (int i = 0; i < c; i++) {
            int z = checknextfreepage();

            freeFrameList[z] = true;
            processPages[2 * processnum + 1][i] = z + 1;


            checknextfreepage();
            if (i < c - 1) {
                for (int x = 0; x < 128; x++) {

                    Memory[z * 128 + x] = code[i * 128 + x];
                }
            }
            else {

                for (int x = 0; x < cx; x++) {

                    Memory[z * 128 + x] = code[i * 128 + x];
                }
            }
        }


    }

    public int checknextfreepage() {
        int i = 0;
        do {
            if (freeFrameList[i] == false) {
                return i;


            }
            i++;

        } while (i <= freeFrameList.length);
        return -1;
    }

    public void populateQueue(int processNumber, double processPriority) {
        createQueue();
        if (processPriority >= 0 && processPriority <= 15)
            highPriorityQueue.add(allPCB[(processNumber)]);
        else if (processPriority >= 16 && processPriority <= 31)
            lowPriorityQueue.add(allPCB[processNumber]);
        else
            System.out.println("Invalid Priority");
    }

    public void createQueue() {
        highPriorityQueue = new LinkedList<>();
        lowPriorityQueue = new LinkedList<>();
        runningQueue = new LinkedList();
    }

    public void startTheProcesses() {
        while (highPriorityQueue.peek() != null && lowPriorityQueue.peek() != null) {
            if (highPriorityQueue.peek() != null) {
                highPriorityQueue.remove();
            }
            else if (lowPriorityQueue.peek() != null) {
                lowPriorityQueue.remove();
            }
        }
    }
}
/*

    private void rollTheDice(){
        do {
            String opCode = Integer.toHexString(checkMemoryValue(Memory[this.progamCounter]));
            // extract the opcode
            System.out.println(opCode);
            char opCodeBreakdownOne = opCode.charAt(0);
            char opCodeBreakdownTwo = opCode.charAt(1);
            //break opcode in 2 characters
            progamCounter++;
            // now 2 switchcases will be used to get the instruction based on the value of the first and second characters of the opcode
            switch (opCodeBreakdownOne) {
                case '1' -> {
                    short registerOne = (checkMemoryValue(Memory[this.progamCounter]));
                    short newRegisterOne = generalPurposeRegister[registerOne];
                    progamCounter++;
                    short registerTwo = (checkMemoryValue(Memory[this.progamCounter]));
                    short newRegisterTwo = generalPurposeRegister[registerTwo];
                    // extracting the values of both registers to use further
                    progamCounter++;
                    if (newRegisterOne > 15 && newRegisterTwo > 15)
                        System.out.println("Register Does Not Exist");
                    //exception condition
                    switch (opCodeBreakdownTwo) {
                        case '6':
                            generalPurposeRegister[registerOne] = newRegisterTwo;
                            //move instruction
                        case '7': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne + newRegisterTwo);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,newRegisterTwo,"add");
                            CarryFlagSett(newRegisterOne,newRegisterTwo,"add");
                            break;
                            // add instruction along with setting required flags
                        }
                        case '8': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne - newRegisterTwo);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,newRegisterTwo,"sub");
                            CarryFlagSett(newRegisterOne,newRegisterTwo,"sub");
                            break;
                            // subtract instruction along with setting required flags

                        }
                        case '9': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne * newRegisterTwo);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,newRegisterTwo,"mul");
                            CarryFlagSett(newRegisterOne,newRegisterTwo,"mul");
                            break;
                            // multiply instruction along with setting required flags

                        }
                        case 'A': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne / newRegisterTwo);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,newRegisterTwo,"div");
                            CarryFlagSett(newRegisterOne,newRegisterTwo,"div");
                            break;
                            // division instruction along with setting required flags

                        }
                        case 'B': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne & newRegisterTwo);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            break;
                        }
                        case 'C': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne | newRegisterTwo);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            break;
                            // OR instruction along with setting required flags

                        }
                    }
                    System.out.println("1 done");
                }
                case '3' -> {
                    short registerOne = (checkMemoryValue(Memory[this.progamCounter]));
                    short newRegisterOne = generalPurposeRegister[registerOne];
                    progamCounter++;
                    String immediatevalue = Integer.toHexString(checkMemoryValue(Memory[this.progamCounter])) + Integer.toHexString(checkMemoryValue(Memory[this.progamCounter + 1]));
                    short immediate = (short) Integer.parseInt(immediatevalue,16);
                    //extracting a registor value and an immediate value to be used further
                    switch (opCodeBreakdownTwo) {
                        case '0':
                            generalPurposeRegister[registerOne] = immediate;
                            break;
                            // mov instruction along with setting required flags
                        case '1': {
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne + immediate);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,immediate,"add");
                            CarryFlagSett(newRegisterOne,immediate,"add");
                            break;
                            // add instruction along with setting required flags

                        }
                        case '2':
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne - immediate);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,immediate,"sub");
                            CarryFlagSett(newRegisterOne,immediate,"sub");
                            break;
                        // subtract instruction along with setting required flags

                        case '3':
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne * immediate);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,immediate,"mul");
                            CarryFlagSett(newRegisterOne,immediate,"mul");
                            break;
                        // multiply instruction along with setting required flags

                        case '4':
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne / immediate);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            OverFlowCheck(newRegisterOne,immediate,"div");
                            CarryFlagSett(newRegisterOne,immediate,"div");
                            break;
                        // divide instruction along with setting required flags

                        case '5':
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne & immediate);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            break;
                        // AND instruction along with setting required flags

                        case '6':
                            generalPurposeRegister[registerOne] = (short) (newRegisterOne | immediate);
                            zeroSet(generalPurposeRegister[registerOne]);
                            signSet(generalPurposeRegister[registerOne]);
                            break;
                        // OR instruction along with setting required flags

                        case '7':
                            if (SPR.newSPR[11].flag[1] == true) {
                                if ((this.progamCounter+immediate ) < SPR.newSPR[2].value)
                                    this.progamCounter = (short) (this.progamCounter + immediate);
                                //checks zero flag then jumps to offset
                            }
                            break;
                        case '8':
                            if (SPR.newSPR[11].flag[1] == false) {
                                if ((this.progamCounter+immediate ) < SPR.newSPR[2].value)
                                    this.progamCounter = (short) (this.progamCounter + immediate);
                                //checks zero flag then jumps to offset

                            }
                            break;
                        case '9':
                            if (SPR.newSPR[11].flag[0] == true) {
                                if ((this.progamCounter+immediate ) < SPR.newSPR[2].value)
                                    this.progamCounter = (short) (this.progamCounter + immediate);
                                //checks carry flag then jumps to offset

                            }
                            break;
                        case 'A':
                            if (SPR.newSPR[11].flag[2] == true) {
                                if ((this.progamCounter+immediate ) < SPR.newSPR[2].value)
                                    this.progamCounter = (short) (this.progamCounter + immediate);
                                //checks sign flag then jumps to offset

                            }
                            break;
                        case 'B':
                            if ((this.progamCounter+immediate ) < SPR.newSPR[2].value)
                                this.progamCounter = (short) (this.progamCounter + immediate);
                            break;
                        // jump instruction

                        case 'C':
                            newStack.push((byte) progamCounter);
                            this.progamCounter = (short) (this.progamCounter + immediate);
                            break;
                            // prodecure call instruction along with adding value to stack
                        case 'D':
                            break;
                        // ACT insttruction

                    }
                    progamCounter += 2;
                }
                case '5' -> {
                    short registerOne = (checkMemoryValue(Memory[this.progamCounter]));
                    short newRegisterOne = generalPurposeRegister[registerOne];
                    progamCounter++;
                    String immediatevalue = Integer.toHexString(checkMemoryValue(Memory[this.progamCounter])) + Integer.toHexString(checkMemoryValue(Memory[this.progamCounter + 1]));
                    short immediate = (short) Integer.parseInt(immediatevalue,16);
                    //extracting a registor value and an immediate value to be used further


                    switch (opCodeBreakdownTwo) {
                        case '1' -> generalPurposeRegister[registerOne] = (short) Memory[newRegisterOne + immediate];
                        case '2' -> Memory[newRegisterOne + immediate] = (byte) generalPurposeRegister[(registerOne)];
                        //load and store instructions
                    }
                    progamCounter += 2;
                }
                case '7' -> {
                    short registerOne =checkMemoryValue (Memory[this.progamCounter]);
                    short newRegisterOne = generalPurposeRegister[registerOne];
                    progamCounter++;
                    //extracting a registor value to be used further

                    switch (opCodeBreakdownTwo) {
                        case '1':
                            newRegisterOne = (short) (newRegisterOne << 1);  //left shift
                            generalPurposeRegister[(registerOne)] = newRegisterOne;
                            break;
                        case '2':
                            newRegisterOne = (short) (newRegisterOne >> 1);  //right shift
                            generalPurposeRegister[(registerOne)] = newRegisterOne;
                            break;
                        case '3':
                            StringBuilder binStr = new StringBuilder(Integer.toBinaryString(newRegisterOne));
                            //converts binary string into a 16 bit binary string

                            for (int i = 16 - binStr.length() - 1; i >= 0; i--) {

                                binStr.insert(0, 0);
//
                            }
                            //checks carry flag and append the value of carry bit to the right of the binary string
                            if (SPR.newSPR[11].flag[0]) {
                                binStr.append(1);
                            } else {
                                binStr.append(0);
                            }
                            // moves the msb to carry flag
                            if (binStr.charAt(0) == '1') {
                                SPR.newSPR[11].flag[0] = true;
                            }
                            else {
                                SPR.newSPR[11].flag[0] = false;
                            }

                            String finalStr = binStr.substring(1, binStr.length()); //removes left most bit from the binary string
                            generalPurposeRegister[(registerOne)] = (short) (Integer.parseInt(finalStr, 2)); //converts binary to decimal and saves in the register
                            break;
                        case '4':
                            StringBuilder binStr2 = new StringBuilder(Integer.toBinaryString(newRegisterOne));// converts decimal to binary
                            //converts binary string into a 16 bit binary string
                            for (int i = 16 - binStr2.length() - 1; i >= 0; i--) {

                                binStr2.insert(0, 0);
//
                            }
                            //checks carry flag and append the value of carry bit to the left of the binary string
                            if (SPR.newSPR[11].flag[0]) {
                                binStr2.insert(0, 1);
                            } else {
                                binStr2.insert(0, 0);
                            }
                            // moves the lsb to carry flag
                            if (binStr2.charAt(binStr2.length() - 1) == '1') {
                                SPR.newSPR[11].flag[0] = true;
                            } else {
                                SPR.newSPR[11].flag[0] = false;
                            }
                            String finalStr2 = binStr2.substring(0, binStr2.length() - 1); //removes right most bit from the binary string
                            generalPurposeRegister[(registerOne)] = (short) (Integer.parseInt(finalStr2, 2)); //converts binary to decimal and saves in the register
                            break;
                        case '5':
                            newRegisterOne++; //increment value in register by 1
                            generalPurposeRegister[(registerOne)] = newRegisterOne;
                            break;
                        case '6':
                            newRegisterOne--; //decrement value in register by 1
                            generalPurposeRegister[(registerOne)] = newRegisterOne;
                            break;
                        case '7':
                            break;
                        case '8':
                            break;
                    }

                }
                case 'f' -> {
                    switch (opCodeBreakdownTwo) {
                        case '1' -> progamCounter = newStack.pop();//pop pc from stack
                        case '2' -> progamCounter++;//increment pc
                        case '3' -> System.exit(2);//exit program
                    }
                    progamCounter++;
                }
                default -> System.out.println("Invalid Op-Code");//invalid opcode
            }
        } while (progamCounter < SPR.newSPR[2].value );
    }

    private void zeroSet(short value) {//zeroset function
        if (value == 0) {
            SPR.newSPR[11].flag[1] = true;
        }
        else
            SPR.newSPR[11].flag[1] = false;
    }

    private void signSet(short value) {//signset function
        if (value < 0) {
            SPR.newSPR[11].flag[2] = true;
        }
        else
            SPR.newSPR[11].flag[2] = false;
    }

    private void OverFlowCheck(Short value1,Short value2,String operation){ //sets overflow flag
        switch (operation) {  // checks the operation that was performed, and then moves to the right code to check overflow
            case "add":  // compares the result of short and integer addition
                short addShort =(short)(value1 + value2) ;
                int addInt=value1 + value2;
               // if the sign of the resulting value after short addition is different form
                //the sign of resulting value after integer addition, the overflow flag is set
                if (addShort!=addInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;
            case "sub":  // compares the result of short and integer subtraction
                short subShort =(short)(value1 - value2) ;
                int subInt=value1 - value2;
                // if the sign of the resulting value after short subtraction is different form
                //the sign of resulting value after integer subtraction, the overflow flag is set
                if (subShort!=subInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;


            case "mul":  // compares the result of short and integer multiplication
                short mulShort =(short)(value1*value2) ;
                int mulInt=value1 * value2;
                // if the sign of the resulting value after short multiplication is different form
                //the sign of resulting value after integer multiplication, the overflow flag is set
                if (mulShort!=mulInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;

            case "div":  // compares the result of short and integer division
                short divShort =(short)(value1/value2) ;
                int divInt=value1 / value2;
                // if the sign of the resulting value after short division is different form
                //the sign of resulting value after integer division, the overflow flag is set
                if (divShort!=divInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;
        }


    }

    private void CarryFlagSett(Short value1,Short value2,String operation) { //sets carry flag
        switch (operation) { // checks the operation that was performed, and then moves to the right code to check carry flag
            case "add":
                int AddResult =value1 + value2 ;
                String addBinStr = Integer.toBinaryString(AddResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if(addBinStr.length()>16 && (SPR.newSPR[11].flag[3] = false)){
                    SPR.newSPR[11].flag[0] = true;
                }
                else{
                    SPR.newSPR[11].flag[0] = false;
                }

            case "sub":
                int SubResult =value1 - value2 ;
                String subBinStr = Integer.toBinaryString(SubResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if(subBinStr.length()>16 && (SPR.newSPR[11].flag[3] = false)){
                    SPR.newSPR[11].flag[0] = true;
                }
                else{
                    SPR.newSPR[11].flag[0] = false;
                }
            case "mul":
                int MulResult =value1 * value2 ;
                String mulBinStr = Integer.toBinaryString(MulResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if(mulBinStr.length()>16 && (SPR.newSPR[11].flag[3] = false)){
                    SPR.newSPR[11].flag[0] = true;
                }
                else{
                    SPR.newSPR[11].flag[0] = false;
                }
            case "div":
                int DivResult =value1 / value2 ;
                String DivBinStr = Integer.toBinaryString(DivResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if(DivBinStr.length()>16 && (SPR.newSPR[11].flag[3] = false)){
                    SPR.newSPR[11].flag[0] = true;
                }
                else{
                    SPR.newSPR[11].flag[0] = false;
                }
        }
    }

    private short checkMemoryValue(byte value){//converts byte to short value
        int valueNumber =  Byte.toUnsignedInt(value);
        short newValueNumber = (short) valueNumber;
        return newValueNumber;
    }

    private int stringToInteger(String letter) {//converts string to integer
        if (letter.equals("A") || letter.equals("0A"))
            return 10;
        else if (letter.equals("B") || letter.equals("0B"))
            return 11;
        else if (letter.equals("C") || letter.equals("0C"))
            return 12;
        else if (letter.equals("D"))
            return 13;
        else if (letter.equals("E"))
            return 14;
        else if (letter.equals("F"))
            return 15;
        else
            return Integer.parseInt(letter);
    }
}
*/
