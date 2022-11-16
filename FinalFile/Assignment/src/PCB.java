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
            instructionsList = new LinkedList<Short>();
        }
        //initializing the variables with the variables
        public PCB(String[] pcbKit, int instructionSize) throws FileNotFoundException {
            SPRforPCB.intializeSpecialPurposeRegister();
            setProcessPriority(Integer.parseInt(pcbKit[0],16));
            setProcessID(Integer.parseInt(pcbKit[1]+pcbKit[2],16));
            setProcessDataSize(Integer.parseInt(pcbKit[3]+pcbKit[4],16));
            setProcessSize(instructionSize);
            setProcessCodeSize();
        }
    }



