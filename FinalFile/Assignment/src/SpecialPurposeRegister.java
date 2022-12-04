public class SpecialPurposeRegister {//special purpose register for processing
    public String name;
    public int value;
    public SpecialPurposeRegister[] newSPR;
    public boolean[] flag;

    public SpecialPurposeRegister(){//initialising spr
        intializeSpecialPurposeRegister();
    }

    public SpecialPurposeRegister(String name,int valuePassed){
        this.name = name;
        this.value = valuePassed;
    }

    public SpecialPurposeRegister(boolean[] flagRegister){
        this.flag = flagRegister;
    }

    public void intializeSpecialPurposeRegister(){//main function
        newSPR = new SpecialPurposeRegister[16];
        newSPR[0] = new SpecialPurposeRegister("Code Base",0);
        newSPR[1] = new SpecialPurposeRegister("Code Counter",0);
        newSPR[2] = new SpecialPurposeRegister("Code Limit",0);
        newSPR[3] = new SpecialPurposeRegister("Data Base",0);
        newSPR[4] = new SpecialPurposeRegister("Data Limit",0);
        newSPR[5] = new SpecialPurposeRegister("Data Counter",0);
        newSPR[6] = new SpecialPurposeRegister("Stack Base",0);
        newSPR[7] = new SpecialPurposeRegister("Stack Counter",0);
        newSPR[8] = new SpecialPurposeRegister("Stack Limit",0);
        newSPR[9] = new SpecialPurposeRegister("Program Counter",0);
        newSPR[10] = new SpecialPurposeRegister("Instruction Register",0);
        boolean[] flag = new boolean[16];
        newSPR[11] = new SpecialPurposeRegister(flag) ;
    }
}
