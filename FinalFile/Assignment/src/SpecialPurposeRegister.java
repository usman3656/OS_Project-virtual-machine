public class SpecialPurposeRegister {
    public String name;
    public short value;
    public SpecialPurposeRegister[] newSPR;
    public boolean[] flag;

    public SpecialPurposeRegister(){

    }

    public SpecialPurposeRegister(String name){
        this.name = name;
        value = 0;
    }

    public SpecialPurposeRegister(boolean[] flagRegister){
        this.flag = flagRegister;
    }

    public void intializeSpecialPurposeRegister(){
        newSPR = new SpecialPurposeRegister[16];
        newSPR[0] = new SpecialPurposeRegister("Code Base");
        newSPR[1] = new SpecialPurposeRegister("Code Counter");
        newSPR[2] = new SpecialPurposeRegister("Code Limit");
        newSPR[3] = new SpecialPurposeRegister("Data Base");
        newSPR[4] = new SpecialPurposeRegister("Data Limit");
        newSPR[5] = new SpecialPurposeRegister("Data Counter");
        newSPR[6] = new SpecialPurposeRegister("Stack Base");
        newSPR[7] = new SpecialPurposeRegister("Stack Counter");
        newSPR[8] = new SpecialPurposeRegister("Stack Limit");
        newSPR[9] = new SpecialPurposeRegister("Program Counter");
        newSPR[10] = new SpecialPurposeRegister("Instruction Register");
        boolean[] flagRegister = new boolean[16];
        newSPR[11] = new SpecialPurposeRegister(flagRegister) ;
    }
}
