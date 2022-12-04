import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class PCB {

        //declaring variables for PCB
        //General Purpose Register
        short[] GPR = new short[16];
        //Special Purpose Register
        SpecialPurposeRegister SPRforPCB = new SpecialPurposeRegister();
        //Local time spent at waiting
        int waitingLocalTime;
        //Local time spent at execution
        int executionLocalTime;
        //linked list for instructions
        private LinkedList<Short> instructionsList;
        //declaring process ID
        int processID;
        //declaring process data
        double processDataSize;
        //declaring process data
        double processCodeSize;
        //declaring Overall Process Size
        double processSize;
        //declaring process priority
        int processPriority;
        //declaring process file
        String processFile;
        //declaring process file
        int[] pagetable = new int[2];
        //declaring page table

        int queuenumber;//declaring queue number 1 or 2
        int codepin;//the point of program counter on code
        int datapin;//the point of program counter on data

        //default setters and getters
        public short[] getGPR() {
            return GPR;
        }

        public void setGPR(short[] GPR) {
            this.GPR = GPR;
        }

        public int getWaitingLocalTime() {
            return waitingLocalTime;
        }

        public void setWaitingLocalTime(int waitingLocalTime) {
            this.waitingLocalTime = waitingLocalTime;
        }

        public int getExecutionLocalTime() {
            return executionLocalTime;
        }

        public void setExecutionLocalTime(int executionLocalTime) {
            this.executionLocalTime = executionLocalTime;
        }

        public LinkedList<Short> getInstructionsList() {
            return instructionsList;
        }

        public void setInstructionsList(LinkedList<Short> instructionsList) {
            this.instructionsList = instructionsList;
        }

        public int getProcessID() {
            return processID;
        }

        public void setProcessID(int processID) {
            this.processID = processID;
        }

        public double getProcessDataSize() {
            return processDataSize;
        }
        public void setProcessDataSize(double processDataSize) {
            this.processDataSize = processDataSize;
        }

        public double getProcessCodeSize() {
            return this.processCodeSize;
        }
        public void setProcessCodeSize() {
            this.processCodeSize = getProcessSize() - getProcessDataSize() - 8;
        }

        public double getProcessSize() {
            return this.processSize;
        }
        public void setProcessSize(int processSize) {
            this.processSize = processSize;
        }
        public int getProcessPriority() {
            if (this.processPriority > 0 || this.processPriority < 31)
                return processPriority;
            else
                return -1;
        }

        public void setProcessPriority(int processPriority) {
            this.processPriority = processPriority;
        }//setting priority


        public String getProcessFile() {
            return processFile;
        }

        public void setProcessFile(String processFile) {
            this.processFile = processFile;
        }

        //default constructor
        public PCB(Object remove) {
            instructionsList = new LinkedList<Short>();
        }
        //initializing the variables with the variables
        public PCB(String[] pcbKit, int instructionSize) throws FileNotFoundException {//pcb class
            SPRforPCB.intializeSpecialPurposeRegister();
            setProcessPriority(Integer.parseInt(pcbKit[0],16));
            setProcessID(Integer.parseInt(pcbKit[1]+pcbKit[2],16));
            setProcessDataSize(Integer.parseInt(pcbKit[3]+pcbKit[4],16));
            setProcessSize(instructionSize);
            setProcessCodeSize();
        }
    }



