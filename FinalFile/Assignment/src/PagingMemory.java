public class PagingMemory {
    //Page Table
    PCB table;
    int dataStartPage;
    int codeStartPage;
    int[][] pageTable;


    public PagingMemory(){

    }


    public int[][] getPageTable() {
        return pageTable;
    }

    public void setPageTable(int[][] pageTable) {
        this.pageTable = pageTable;
    }

}