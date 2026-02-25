public class Binary_Trees {
    public static void main(String[] args) {
        test();
    }

    private static void test(){
        BinaryTree tree = new BinaryTree();
        tree.root = new treeNode(10);
        tree.root.left = new treeNode(4);
        tree.root.right = new treeNode(16);
        tree.root.right.left = new treeNode(13);
        tree.root.right.right = new treeNode(18);
        tree.root.left.left = new treeNode(7);
        tree.root.left.right = new treeNode(2);
        tree.display();
    }

    public static class BinaryTree {
        treeNode root;

        public BinaryTree() {
            this.root = null;
        }

        public void display() {
            
        }


    }

    public static class treeNode {
        int value;
        treeNode left;
        treeNode right;

        public treeNode(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }
}

