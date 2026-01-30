import java.util.Scanner;

public class RPNStackCalculator {
    private static Calculator calc;

    public static void main(String[] args) {
        calc = new Calculator();
        while (true) {
            calc.mainMenu();
        }
    }
}

class Calculator {
    static Scanner s = new Scanner(System.in);
    private Stack stack;
    
    public Calculator(){
        // initialise fields
        stack = new Stack();
    }

    //open the command menu for the calculator
    public void mainMenu(){
        System.out.println("\nThe Calculator uses infix notation");
        System.out.println("Please Input You Command:\n1 - Push (Add a Node)\n2 - Pop (Remove a Node)\n3 - Display All Current Nodes\n4 - Calculate");
        int commandAnswer = s.nextInt();

        switch (commandAnswer) {
            case 1:
                // push
                System.out.println("what do you want to push?");
                s.nextLine();
                String pushed = s.nextLine();
                stack.push(pushed);
                break;
                
            case 2:
                // pop
                stack.pop();
                break;

            case 3:
                // display
                stack.display();
                break;
    
            case 4:
                // Calculate and Display final result
                System.out.println("\n" + calculate());
                break;

            default:
                System.out.println("invalid Choice, Please Try Again");
                break;
        }
    }

    public String calculate(){
        // reverse the stack input by the user so the user can input the formula in a logical way, but its still easy for the program to read
        Stack calcStack = stack.reverse();

        // create small stack with a maximum of 3 nodes to keep information on formula done and the current node values selected
        // without altering the main or calculation stacks
        Stack subCalcStack = new Stack();
        StackNode currentNode = calcStack.headNode;

        // while there is still more than one node left in the calculation stack, that means the calculation has not been finished
        // or that the syntax the user entered was wrong and you will get an error or and infinite loop
        while (calcStack.length() > 1){
            // filling the subStack with values from the calculation stack
            subCalcStack.push(currentNode.value);
            // move to the next node for the next iteration of the loop
            currentNode = currentNode.nextNode;

            // check to see if there are 3 nodes in the substack, if there is there may be the correct values to do an equation
            if (subCalcStack.length() == 3){
                // check to see if the final node value in the substack is an operator instead of a number, 
                // if it is then proceed with completing the operation
                if (isAnOperator(subCalcStack.headNode.value)){
                    // convert the strings into floats so the calculation can be done and then get the result as a float
                    float result = calculateSmall(Float.parseFloat(subCalcStack.headNode.nextNode.nextNode.value), Float.parseFloat(subCalcStack.headNode.nextNode.value), subCalcStack.headNode.value);
                    // remove nodes with values that were used to prevent repetition
                    calcStack.searchAndDeleteNode(calcStack, subCalcStack.headNode.value);
                    calcStack.searchAndDeleteNode(calcStack, subCalcStack.headNode.nextNode.value);
                    calcStack.searchAndDeleteNode(calcStack, subCalcStack.headNode.nextNode.nextNode.value);
                    // reset the substack to empty
                    subCalcStack.pop();
                    subCalcStack.pop();
                    subCalcStack.pop();
                    calcStack.push(Float.toString(result));
                    currentNode = calcStack.headNode;
                }
                else{
                    // if the final node is not an operator, remove the earliest node added by reversing the stack and
                    // then popping the top value then reversing the stack back
                    subCalcStack = subCalcStack.reverse();
                    subCalcStack.pop();
                    subCalcStack = subCalcStack.reverse();
                }
            }
        }
        // return the final value of the calculation
        return calcStack.headNode.value;
    }

    public boolean isAnOperator(String possibleOperator){
        if (possibleOperator.equals("+") || possibleOperator.equals("-") || possibleOperator.equals("x") || possibleOperator.equals("*") || possibleOperator.equals("/")){
            return true;
        }
        else{
            return false;
        }
    }

    public float calculateSmall(float value1, float value2, String operator){
        switch (operator) {
            case "+":
                return value1 + value2;

            case "-":
                return value1 - value2;

            case "x":
            case "*":
                return value1 * value2;

            case "/":
                // catch error caused by 0/0
                if (value1 == 0 && value2 == 0){
                    System.out.println("You cannot divide 0 by 0");
                    return 0;
                }
                return value1 / value2;
        
            default:
                System.err.println("an invalid operator was chosen");
                return 0;
        }
    }
}

class Stack{
    // first node in the stack
    public StackNode headNode;
    
    // constructor to initialize variables
    public Stack(){
        headNode = null;
    }

    // search through the stack to delete the first node that has a specific value
    public void searchAndDeleteNode(Stack stack, String targetValue){
        StackNode previousNode = null;
        StackNode currentNode = stack.headNode;
        boolean found = false;

        while (currentNode != null && !found) {
            if (currentNode.value == targetValue){
                found = true;
                if (previousNode == null){
                    stack.headNode = currentNode.nextNode;
                }
                else{
                    previousNode.nextNode = currentNode.nextNode;
                }
            }
            previousNode = currentNode;
            currentNode = currentNode.nextNode;
        }

        if (found == false){
            System.out.println("no node with the criteria of \"" + targetValue + "\" was found");
        }
    }

    // remove the head node
    public void pop() {
        if (headNode != null){
            headNode = headNode.nextNode;
        }
        else{
            System.err.println("stack already empty!");
        }
    }

    // add another node onto the top of the stack
    public void push(String newNodeValue) {
        StackNode newNode = new StackNode();
        newNode.value = newNodeValue;
        newNode.nextNode = headNode;
        headNode = newNode;
    }

    // Display all the node values with a correlating number for how close to the top of the stack they are
    public void display(){
        StackNode currentnode = headNode;
        int counter = 1;
        while (currentnode != null) {
            System.out.println("Node " + counter + ": " + currentnode.value);
            currentnode = currentnode.nextNode;
            counter++;
        }
        System.out.println("Stack Finished\n");
    }

    // reverse
    public Stack reverse(){
        // make a new stack and put in all the nodes of the stack but in the opposite order
        Stack reverseStack = new Stack();
        StackNode currentnode = headNode;
        while (currentnode != null) {
            reverseStack.push(currentnode.value);
            currentnode = currentnode.nextNode;
        }
        return reverseStack;
    }

    // get the length of the stack
    public int length(){
        StackNode currentnode = headNode;
        int counter = 0;
        while (currentnode != null) {
            counter++;
            currentnode = currentnode.nextNode;
        }
        return counter;
    }
}

class StackNode{
    public String value;
    public StackNode nextNode;
}