public class ByteStack {
    byte stackList[];
    int top = -1;

    public ByteStack(byte BytesToBeInserted) {//stack initialisation
        stackList = new byte[BytesToBeInserted];
        top = -1;
    }

    public void push(byte c) {//push operation
        if (isEmpty()) {
            top++;
            stackList[top] = c;

        }
        else if (!isFull()) {
            top++;
            stackList[top] = c;

        }
    }

    public byte pop() {//pop operation
        byte temp = stackList[top];
        if (!isEmpty())
            top--;
        return temp;
    }

    public byte peek() {
        return stackList[top];
    }//peek operation

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == stackList.length - 1);
    }
}
