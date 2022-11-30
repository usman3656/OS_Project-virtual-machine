import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.*;
import java.lang.Math;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;
import java.io.File;
//all initialization/
public class MemoryDesign {
    private final byte toBeInserted = 50;
    private ByteStack newStack = new ByteStack(toBeInserted);
    private PCB[] allPCB = new PCB[6];
    public final byte[] Memory = new byte[65536];
    public short progamCounter;
    boolean[] freeFrameList = new boolean[512];
    int[][] processPages = new int[12][13];
    ArrayList[] infoAboutPCB = new ArrayList[2];
    //-----------------------------------------
    Queue<PCB> highPriorityQueue;
    Queue<PCB> lowPriorityQueue;
    Queue<PCB> runningQueue;

    public MemoryDesign(ArrayList<ArrayList> instructionSet) throws FileNotFoundException {
        // Generating PCB For All Processes
        //------------------------------------------------
        System.out.println();
        createQueue();
        for (int i = 0; i < instructionSet.size(); i++) {
            generatePCB(instructionSet.get(i), i);
            fillpages(instructionSet.get(i), (int) allPCB[i].getProcessDataSize(), (int) allPCB[i].getProcessCodeSize(), i, allPCB[i].getProcessID());
            populateQueue(i, allPCB[i].processPriority);
        }

        System.out.println(Arrays.toString(Memory));
        int pcbnum = 0;
        PCB currentPCB = null;
        do {

            currentPCB = findMyPCB();
            if (currentPCB != null) {
                System.out.println();
                System.out.println(currentPCB.getProcessID());
                runningQueue.add(currentPCB);
                rollTheDice(runningQueue, currentPCB.GPR, currentPCB.SPRforPCB);
                pcbnum++;
            }

        } while (currentPCB != null);
        System.out.println(Arrays.toString(Memory));
    }

    public ArrayList[] extractInfo(PCB currentPCB) {
        int idToMatch = currentPCB.getProcessID();
        int datasize = (int) currentPCB.getProcessDataSize();

        int i;
        for (i = 0; i < processPages.length; i = i + 2) {
            if (idToMatch == processPages[i][0]) {
                break;
            }
        }
        int d = 1;
        int pagenum;
        ArrayList datalist = new ArrayList();
        while (processPages[i][d] != 0) {
            pagenum = processPages[i][d];

            for (int j = 0; j < 128; j++) {
                datalist.add(Memory[(pagenum - 1) * 128 + j]);
            }
            d++;

        }

        int codesize = (int) currentPCB.getProcessDataSize();


        int c = 1;
        int pagenumcode;
        ArrayList codelist = new ArrayList();
        while (processPages[i + 1][c] != 0) {
            pagenumcode = processPages[i + 1][c];
            for (int j = 0; j < 128; j++) {
                codelist.add(Memory[(pagenumcode - 1) * 128 + j]);
            }
            c++;

        }

        infoAboutPCB[0] = datalist;
        infoAboutPCB[1] = codelist;

        return infoAboutPCB;

    }


    public void generatePCB(ArrayList<String> instructionSet, int pcbNumber) throws FileNotFoundException {
        String[] pcbKit = new String[8];
        for (int processByte = 0; processByte < 8; processByte++)
            pcbKit[processByte] = instructionSet.get(processByte);
        allPCB[pcbNumber] = new PCB(pcbKit, instructionSet.size());
    }

    public void fillpages(ArrayList instructionset, int datasize, int codesize, int processnum, int processid) {
        byte[] data = new byte[datasize];
        byte[] code = new byte[codesize];
        System.out.println();

        allPCB[processnum].pagetable[0] = processnum * 2;
        allPCB[processnum].pagetable[1] = processnum * 2 + 1;


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

        processPages[processnum * 2][0] = processid;
        processPages[processnum * 2 + 1][0] = processid;


        for (int i = 0; i < d; i++) {
            int z = checknextfreepage();
            freeFrameList[z] = true;
            processPages[processnum * 2][i + 1] = z + 1;


            if (i < d - 1) {
                for (int x = 0; x < 128; x++) {
                    Memory[z * 128 + x] = data[i * 128 + x];
                }
            } else {

                for (int x = 0; x < dx; x++) {
                    Memory[z * 128 + x] = data[i * 128 + x];
                }
            }
        }

        for (int i = 0; i < c; i++) {
            int z = checknextfreepage();
            freeFrameList[z] = true;
            processPages[2 * processnum + 1][i + 1] = z + 1;

            checknextfreepage();
            if (i < c - 1) {
                for (int x = 0; x < 128; x++) {

                    Memory[z * 128 + x] = code[i * 128 + x];
                }
            } else {

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

        if (processPriority >= 0 && processPriority <= 15)
            highPriorityQueue.add(allPCB[(processNumber)]);
        else if (processPriority >= 16 && processPriority <= 31)
            lowPriorityQueue.add(allPCB[processNumber]);
        else
            System.out.println("Invalid Priority");

        if (!highPriorityQueue.isEmpty()) {
        }
    }

    public void createQueue() {
        highPriorityQueue = new LinkedList<>();
        lowPriorityQueue = new LinkedList<>();
        runningQueue = new LinkedList<>();
    }

    public PCB findMyPCB() {
        while (lowPriorityQueue.peek() != null || highPriorityQueue.peek() != null) {
            if (highPriorityQueue.peek() != null) {
                return highPriorityQueue.remove();
            } else {
                return lowPriorityQueue.remove();
            }
        }
        return null;
    }

    public PCB getRunningPCB() {
        return runningQueue.remove();
    }

    private void rollTheDice(Queue runningQueue, short[] generalPurposeRegister, SpecialPurposeRegister SPR) {
        PCB currentpcb = null;


        System.out.println("start execution of process");


        ArrayList datalist;
        ArrayList codelist;

        currentpcb = getRunningPCB();
        ArrayList[] bothlist;
        bothlist = extractInfo(currentpcb);
        datalist = bothlist[0];
        codelist = bothlist[1];
        System.out.println("Print Data And Code");
        System.out.println(datalist);
        System.out.println(codelist);

        int datapoint = 0;
        int codepoint = 0;


        do {
            String opCode = Integer.toHexString(checkMemoryValue((Byte) codelist.get(codepoint)));


            //System.out.print(opCode+"  "+checkMemoryValue((Byte) codelist.get(codepoint))+"  ");
            // extract the opcode

            if (checkopcodevalue(Integer.parseInt(opCode, 16))) {
                char opCodeBreakdownOne = opCode.charAt(0);
                char opCodeBreakdownTwo = opCode.charAt(1);
                //break opcode in 2 characters
                codepoint++;
                // now 2 switchcases will be used to get the instruction based on the value of the first and second characters of the opcode
                switch (opCodeBreakdownOne) {
                    case '1' -> {
                        short registerOne = (checkMemoryValue((Byte) codelist.get(codepoint)));
                        short newRegisterOne;
                        if (checkregistervalue(registerOne)) {
                            newRegisterOne = generalPurposeRegister[registerOne];
                        } else
                            break;
                        codepoint++;
                        short registerTwo = (checkMemoryValue((Byte) codelist.get(codepoint)));
                        short newRegisterTwo = generalPurposeRegister[registerTwo];
                        // extracting the values of both registers to use further
                        codepoint++;
                        if (newRegisterOne > 15 && newRegisterTwo > 15)
                            System.out.println("Register Does Not Exist");
                        //exception condition
                        switch (opCodeBreakdownTwo) {
                            case '6':
                                generalPurposeRegister[registerOne] = newRegisterTwo;
                                //move instruction
                            case '7': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne + newRegisterTwo);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, newRegisterTwo, "add", SPR);
                                CarryFlagSett(newRegisterOne, newRegisterTwo, "add", SPR);
                                break;
                                // add instruction along with setting required flags
                            }
                            case '8': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne - newRegisterTwo);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, newRegisterTwo, "sub", SPR);
                                CarryFlagSett(newRegisterOne, newRegisterTwo, "sub", SPR);
                                break;
                                // subtract instruction along with setting required flags

                            }
                            case '9': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne * newRegisterTwo);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, newRegisterTwo, "mul", SPR);
                                CarryFlagSett(newRegisterOne, newRegisterTwo, "mul", SPR);
                                break;
                                // multiply instruction along with setting required flags

                            }
                            case 'A': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne / newRegisterTwo);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, newRegisterTwo, "div", SPR);
                                CarryFlagSett(newRegisterOne, newRegisterTwo, "div", SPR);
                                break;
                                // division instruction along with setting required flags

                            }
                            case 'B': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne & newRegisterTwo);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                break;
                            }
                            case 'C': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne | newRegisterTwo);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                break;
                                // OR instruction along with setting required flags

                            }
                        }
                        //System.out.println("1 done");
                    }
                    case '3' -> {
                        short registerOne = (checkMemoryValue((Byte) codelist.get(codepoint)));
                        short newRegisterOne;
                        if (checkregistervalue(registerOne)) {
                            newRegisterOne = generalPurposeRegister[registerOne];
                        } else
                            break;
                        codepoint++;
                        String immediatevalue = Integer.toHexString(checkMemoryValue((Byte) codelist.get(codepoint))) + Integer.toHexString(checkMemoryValue((Byte) codelist.get(codepoint + 1)));
                        ;
                        short immediate = (short) Integer.parseInt(immediatevalue, 16);
                        //extracting a registor value and an immediate value to be used further
                        switch (opCodeBreakdownTwo) {
                            case '0':
                                generalPurposeRegister[registerOne] = immediate;
                                break;
                            // mov instruction along with setting required flags
                            case '1': {
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne + immediate);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, immediate, "add", SPR);
                                CarryFlagSett(newRegisterOne, immediate, "add", SPR);
                                break;
                                // add instruction along with setting required flags

                            }
                            case '2':
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne - immediate);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, immediate, "sub", SPR);
                                CarryFlagSett(newRegisterOne, immediate, "sub", SPR);
                                break;
                            // subtract instruction along with setting required flags

                            case '3':
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne * immediate);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, immediate, "mul", SPR);
                                CarryFlagSett(newRegisterOne, immediate, "mul", SPR);
                                break;
                            // multiply instruction along with setting required flags

                            case '4':
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne / immediate);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                OverFlowCheck(newRegisterOne, immediate, "div", SPR);
                                CarryFlagSett(newRegisterOne, immediate, "div", SPR);
                                break;
                            // divide instruction along with setting required flags

                            case '5':
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne & immediate);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                break;
                            // AND instruction along with setting required flags

                            case '6':
                                generalPurposeRegister[registerOne] = (short) (newRegisterOne | immediate);
                                zeroSet(generalPurposeRegister[registerOne], SPR);
                                signSet(generalPurposeRegister[registerOne], SPR);
                                break;
                            // OR instruction along with setting required flags

                            case '7':
                                if (SPR.newSPR[11].flag[1] == true) {
                                    if ((codepoint + immediate) < SPR.newSPR[2].value)
                                        codepoint = (short) (codepoint + immediate);
                                    //checks zero flag then jumps to offset
                                }
                                break;
                            case '8':
                                if (SPR.newSPR[11].flag[1] == false) {
                                    if ((codepoint + immediate) < SPR.newSPR[2].value)
                                        codepoint = (short) (codepoint + immediate);
                                    //checks zero flag then jumps to offset

                                }
                                break;
                            case '9':
                                if (SPR.newSPR[11].flag[0] == true) {
                                    if ((codepoint + immediate) < SPR.newSPR[2].value)
                                        codepoint = (short) (codepoint + immediate);
                                    //checks carry flag then jumps to offset

                                }
                                break;
                            case 'A':
                                if (SPR.newSPR[11].flag[2] == true) {
                                    if ((codepoint + immediate) < SPR.newSPR[2].value)
                                        codepoint = (short) (codepoint + immediate);
                                    //checks sign flag then jumps to offset

                                }
                                break;
                            case 'B':
                                if ((codepoint + immediate) < SPR.newSPR[2].value)
                                    codepoint = (short) (codepoint + immediate);
                                break;
                            // jump instruction

                            case 'C':
                                newStack.push((byte) codepoint);
                                codepoint = (short) (codepoint + immediate);
                                break;
                            // prodecure call instruction along with adding value to stack
                            case 'D':
                                break;
                            // ACT insttruction

                        }
                        codepoint += 2;
                    }
                    case '5' -> {
                        short registerOne = (checkMemoryValue((Byte) codelist.get(codepoint)));
                        short newRegisterOne;
                        if (checkregistervalue(registerOne)) {
                            newRegisterOne = generalPurposeRegister[registerOne];
                        } else
                            break;
                        codepoint++;
                        String immediatevalue = Integer.toHexString(checkMemoryValue((Byte) codelist.get(codepoint))) + Integer.toHexString(checkMemoryValue((Byte) codelist.get(codepoint + 1)));
                        short immediate = (short) Integer.parseInt(immediatevalue, 16);
                        //extracting a registor value and an immediate value to be used further


                        switch (opCodeBreakdownTwo) {
                            case '1' -> generalPurposeRegister[registerOne] = checkMemoryValue((Byte) datalist.get(datapoint + immediate));
                            case '2' -> datalist.set(datapoint + immediate, generalPurposeRegister[(registerOne)]);
                            //load and store instructions
                        }
                        codepoint += 2;
                    }

                    case '7' -> {
                        short registerOne = (checkMemoryValue((Byte) codelist.get(codepoint)));
                        short newRegisterOne;
                        if (checkregistervalue(registerOne)) {
                            newRegisterOne = generalPurposeRegister[registerOne];
                        } else
                            break;
                        codepoint++;
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
                                } else {
                                    SPR.newSPR[11].flag[0] = false;
                                }

                                String finalStr = binStr.substring(1, binStr.length()); //removes left most bit from the binary string
                                // generalPurposeRegister[(registerOne)] = (short) (Integer.parseInt(finalStr, 2)); //converts binary to decimal and saves in the register
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
                                //generalPurposeRegister[(registerOne)] = (short) (Integer.parseInt(finalStr2, 2)); //converts binary to decimal and saves in the register
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
                            case '1' -> codepoint = codepoint;//pop pc from stack
                            case '2' -> codepoint++;//increment pc
                            case '3' -> System.exit(2);//exit program
                        }

                    }
                    //default -> System.out.println("Invalid Op-Code");//invalid opcode
                }
            } else {
                //System.out.println("invalid opcode");
                codepoint++;
            }


        } while (codepoint < codelist.size());

        finishExecution(datalist, codelist, currentpcb);
        printFile(datalist,codelist);
    }

    public void printFile(ArrayList datalist, ArrayList codelist){

        try {
            File myObj = new File("D:\\IBA\\Semester 5\\OS_Project1\\file2.txt");
            FileWriter myWriter = new FileWriter("D:\\IBA\\Semester 5\\OS_Project1\\file2.txt");
            for(int i=0;i<datalist.size();i++){
                myWriter.write(datalist.get(i)+" ");
            }
            myWriter.write("\n");
            myWriter.write("\n");
            for(int j=0;j<codelist.size();j++){
                myWriter.write(""+codelist.get(j)+" ");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void finishExecution(ArrayList datalist, ArrayList codelist, PCB currentpcb) {;
        int dataCount = 1;
        while (processPages[currentpcb.pagetable[0]][dataCount] != 0) {
            for (int x = 0; x < 128; x++) {
                Memory[processPages[currentpcb.pagetable[0]][dataCount-1] * 128 + x] = (byte) datalist.get((dataCount - 1) * 128 + x);
            }
            dataCount++;
        }

        int codeCount = 1;
        while (processPages[currentpcb.pagetable[1]][codeCount] != 0) {
            for (int x = 0; x < 128; x++) {
                Memory[processPages[currentpcb.pagetable[1]][codeCount-1] * 128 + x] = (byte) codelist.get((codeCount - 1) * 128 + x);
            }
            System.out.println(codeCount);
            codeCount++;

        }

    }

    private void zeroSet(short value, SpecialPurposeRegister SPR) {//zeroset function
        if (value == 0) {
            SPR.newSPR[11].flag[1] = true;
        } else
            SPR.newSPR[11].flag[1] = false;
    }

    private void signSet(short value, SpecialPurposeRegister SPR) {//signset function
        if (value < 0) {
            SPR.newSPR[11].flag[2] = true;
        } else
            SPR.newSPR[11].flag[2] = false;
    }

    private void OverFlowCheck(Short value1, Short value2, String operation, SpecialPurposeRegister SPR) { //sets overflow flag
        switch (operation) {  // checks the operation that was performed, and then moves to the right code to check overflow
            case "add":  // compares the result of short and integer addition
                short addShort = (short) (value1 + value2);
                int addInt = value1 + value2;
                // if the sign of the resulting value after short addition is different form
                //the sign of resulting value after integer addition, the overflow flag is set
                if (addShort != addInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;
            case "sub":  // compares the result of short and integer subtraction
                short subShort = (short) (value1 - value2);
                int subInt = value1 - value2;
                // if the sign of the resulting value after short subtraction is different form
                //the sign of resulting value after integer subtraction, the overflow flag is set
                if (subShort != subInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;


            case "mul":  // compares the result of short and integer multiplication
                short mulShort = (short) (value1 * value2);
                int mulInt = value1 * value2;
                // if the sign of the resulting value after short multiplication is different form
                //the sign of resulting value after integer multiplication, the overflow flag is set
                if (mulShort != mulInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;

            case "div":  // compares the result of short and integer division
                short divShort = (short) (value1 / value2);
                int divInt = value1 / value2;
                // if the sign of the resulting value after short division is different form
                //the sign of resulting value after integer division, the overflow flag is set
                if (divShort != divInt) {
                    SPR.newSPR[11].flag[3] = true;
                } else {
                    SPR.newSPR[11].flag[3] = false;
                }
                break;
        }


    }

    private void CarryFlagSett(Short value1, Short value2, String operation, SpecialPurposeRegister SPR) { //sets carry flag
        switch (operation) { // checks the operation that was performed, and then moves to the right code to check carry flag
            case "add":
                int AddResult = value1 + value2;
                String addBinStr = Integer.toBinaryString(AddResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if (addBinStr.length() > 16 && (SPR.newSPR[11].flag[3] = false)) {
                    SPR.newSPR[11].flag[0] = true;
                } else {
                    SPR.newSPR[11].flag[0] = false;
                }

            case "sub":
                int SubResult = value1 - value2;
                String subBinStr = Integer.toBinaryString(SubResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if (subBinStr.length() > 16 && (SPR.newSPR[11].flag[3] = false)) {
                    SPR.newSPR[11].flag[0] = true;
                } else {
                    SPR.newSPR[11].flag[0] = false;
                }
            case "mul":
                int MulResult = value1 * value2;
                String mulBinStr = Integer.toBinaryString(MulResult); //convert decimal to binary
                //when there is no overflow and result is greater than 16 bits, carry is set
                if (mulBinStr.length() > 16 && (SPR.newSPR[11].flag[3] = false)) {
                    SPR.newSPR[11].flag[0] = true;
                } else {
                    SPR.newSPR[11].flag[0] = false;
                }
            case "div":
                if (value2 != 0) {
                    int DivResult = value1 / value2;
                    String DivBinStr = Integer.toBinaryString(DivResult); //convert decimal to binary
                    //when there is no overflow and result is greater than 16 bits, carry is set
                    if (DivBinStr.length() > 16 && (SPR.newSPR[11].flag[3] = false)) {
                        SPR.newSPR[11].flag[0] = true;
                    } else {
                        SPR.newSPR[11].flag[0] = false;
                    }
                }
        }
    }

    private short checkMemoryValue(byte value) {//converts byte to short value
        int valueNumber = Byte.toUnsignedInt(value);
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

    public boolean checkregistervalue(int value) {
        if (value > 16 || value < 0)
            return false;
        else
            return true;
    }

    public boolean checkopcodevalue(int value) {
        if (value < 16)
            return false;
        else
            return true;
    }


}


