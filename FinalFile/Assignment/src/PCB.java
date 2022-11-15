import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.LinkedList;
public class PCB {

        //declaring variables for PCB
        //General Purpose Register
        short[] GPR = new short[16];
        //Special Purpose Register
        SpecialPurposeRegister SPRforPCB;
        //Page Table
        int[][] pageTable;
        //Local time spent at waiting
        int waitingLocalTime;
        //Local time spent at execution
        int executionLocalTime;
        //linked list for instructions
        private LinkedList<Short> instructionsList;
        //declaring process ID
        int processID;
        //declaring process size code+data+segment
        double processSize;
        //declaring process priority
        int processPriority;
        //declaring process file
        String processFile;

        //default setters and getters
        public short[] getGPR() {
            return GPR;
        }

        public void setGPR(short[] GPR) {
            this.GPR = GPR;
        }

        public int[][] getPageTable() {
            return pageTable;
        }

        public void setPageTable(int[][] pageTable) {
            this.pageTable = pageTable;
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

        public void setProcessID() {
            this.processID = (int) (Math.random() * 10000);
        }

        public double getProcessSize() {
            return processSize;
        }

        public void setProcessSize(double processSize) {
            this.processSize = processSize;
        }

        public int getProcessPriority() {
            return processPriority;
        }

        public void setProcessPriority(int processPriority) {
            this.processPriority = processPriority;
        }

        public String getProcessFile() {
            return processFile;
        }

        public void setProcessFile(String processFile) {
            this.processFile = processFile;
        }

        //default constructor
        public PCB() {
            setProcessID();
            instructionsList = new LinkedList<Short>();
        }
        //initializing the variables with the variables
        public PCB(int processID, String fileName, int processPriority, double processSize, short dataSize, LinkedList<Short> instructions) throws FileNotFoundException {
            SPRforPCB.intializeSpecialPurposeRegister();
            this.processID = processID;
            this.SPRforPCB.newSPR[1].value = 127 ;
            this.processPriority = processPriority;
            this.processSize = processSize;
            this.processFile = fileName;
            this.instructionsList = instructions;
            this.SPRforPCB.newSPR[9].value = dataSize;

        }
    }



