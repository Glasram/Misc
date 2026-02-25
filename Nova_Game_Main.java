import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


class Main 
{
    public static void main(String[] args){
        Game game1 = new Game();
        game1.menu();
    }

    public static void tester(){
        Game game1 = new Game();

        game1.createGame();
        
        //game1.saveGame();
        //game1.loadGame();

        game1.player1.displayGrid();
        System.out.println("");
        game1.player2.displayGrid();

        game1.playGame();
    }
}

class Game{
    public boolean player1Turn;
    public Player player1;
    public Player player2;

    public static int numOfCreatures;

    public boolean gameInProgress;
    private boolean gameExists;

    // for checking to see if it fails to place the creature too much
    int failCounter = 0;

    Scanner s = new Scanner(System.in);
    
    // constructor
    public Game(){
        player1Turn = true;
        gameInProgress = true;
        gameExists = false;
        player1 = new Player();
        player2 = new Player();
    }

    // show all stats and player grids
    private void displayGameState(){
        System.out.println("-_-_-_-_-_-_-_-_-_-_-_- PLAYER 1 -_-_-_-_-_-_-_-_-_-_-_-");
        player1.displayInGameGrid();
        System.out.println("SCORE: " + player1.score + "\nHEALTH: " + player1.health);

        System.out.println("-_-_-_-_-_-_-_-_-_-_-_- PLAYER 2 -_-_-_-_-_-_-_-_-_-_-_-");
        player2.displayInGameGrid();
        System.out.println("SCORE: " + player2.score + "\nHEALTH: " + player2.health);
    }

    public void menu(){
        // Guiding first time players
        System.out.println("Please read the help before starting your first game\n");

        while (true) {
            System.out.println("Please choose an option and write the number below:\n");
            System.out.println("1 - Start or resume the game\n2 - Create a new game\n3 - Load the saved game\n4 - Help\n5 - Save the game\n6 - exit the game\n");
            
            int userChoice = s.nextInt();

            userChoice = validateInt(userChoice, 1, 6);

            switch (userChoice) {
                case 1: // play the game
                    if (gameExists){
                        playGame();
                    }
                    else{
                        System.out.println("You must either create or load a game first");
                    }
                    break;

                case 2: // create a new instance of the game
                    createGame();
                    gameExists = true;
                    break;
                    
                case 3: // load a save
                    loadGame();
                    gameExists = true;
                    break;

                case 4: // get help
                    System.out.println("\nThis game is played with 2 players.\r\n" + //
                                                "Players will take turns choosing both an x and a y coordinate corresponding to a grid.\r\n" + //
                                                "This grid will have both creatures and traps on it (different places for each player), if the player choses coordinates with either they are either rewarded or punished.\r\n" + //
                                                "Animals are typically in distinct shaped and take up multiple cells on the grid.\r\n" + //
                                                "-1 can be written when inputting the coordinates to return to the menu.\r\n" + //
                                                "The game can be saved and loaded from the menu. Saving will overwrite the previous save.\r\n" + //
                                                "A Player can win from finding all the creatures on their grid or their opponent running out of health\r\n" + //
                                                "Creature codes are as follows:\r\n" + //
                                                "m = Minnow\r\n" + //
                                                "s = Shark\r\n" + //
                                                "g = Gulper Eel\r\n" + //
                                                "o = Octopus\r\n" + //
                                                "r = Manta Ray\r\n" + //
                                                "t = trap (not an animal but its a code!)\n");
                    break;

                case 5: // save the game
                    saveGame();
                    break;
                
                case 6: // exit the game
                    System.exit(0);
                    break;

                default:
                    System.out.println("Please choose from the options given");
                    break;
            }
        }
    }

    // 
    // GAME PLAYING START
    //

    public void playGame(){
        while (gameInProgress) {
            // for if returning to the menu is wanted
            int menuReturnValue = -1;

            if (player1Turn){
                System.out.println("Player 1's Turn");
                menuReturnValue = player1.takeTurn();

                if  (menuReturnValue == -1){
                    return;
                }
                player1Turn = false;
            }
            else {
                System.out.println("Player 2's Turn");
                menuReturnValue = player2.takeTurn();
                
                if  (menuReturnValue == -1){
                    return;
                }
                player1Turn = true;

                // show the stats for players after a turn
                displayGameState();
            }
            // check to seei f the round that was just played makes them a winner
            // if they both win or lose in the same aspect at the same time then player1 will win
            checkIfWon(player1, player2);  
        }
    }

    public void checkIfWon(Player player1stats, Player player2stats){
        // player 1
        // System.exit(0) quits the game

        // if player 1 dies
        if (player1.health <= 0){ 
            System.out.println("\nPlayer 1 has lost!\nPlayer1 had " + player1stats.score + " score and " + player1stats.health + " health left");
            System.out.println("Player2 had " + player2stats.score + " score and " + player2stats.health + " health left");
            System.exit(0);
        }
        
        // check to see if all creatures have been fully found
        boolean passed1 = true;
        for (int i = 0; i < player1stats.creatureCellsFound.length; i++){
            if (player1stats.creatureCellsFound[i] != 0){
                passed1 = false;
                return;
            }
        }
    
        // player 2

        // if player 2 dies
        if (player2.health <= 0){ 
            System.out.println("\nPlayer 2 has lost!\nPlayer 2 had " + player2stats.score + " score and " + player2stats.health + " health left");
            System.out.println("Player 1 had " + player1stats.score + " score and " + player1stats.health + " health left");
            System.exit(0);
        }

        // check if all the creatures have been fully found
        boolean passed2 = true;
        for (int i = 0; i < player2stats.creatureCellsFound.length; i++){
            if (player2stats.creatureCellsFound[i] != 0){
                passed2 = false;
                return;
            }
        }

        // both

        if (passed1 || passed2){
            if (passed1){
                System.out.println("\nPlayer 1 has completed the board!");
            }
            else{
                System.out.println("\nPlayer 2 has completed the board!");
            }

            if (player1stats.score > player2stats.score){
                System.out.println("\nPlayer 1 won!\nPlayer1 had " + player1stats.score + " score and " + player1stats.health + " health left");
                System.out.println("Player2 had " + player2stats.score + " score and " + player2stats.health + " health left");
            }
            else{
                System.out.println("\nPlayer 2 won!\nPlayer 2 had " + player2stats.score + " score and " + player2stats.health + " health left");
                System.out.println("Player 1 had " + player1stats.score + " score and " + player1stats.health + " health left");
            }

            System.exit(0);
        }
   }
    // 
    // GAME PLAYING END
    //

    // 
    // GAME CREATION START
    //

    public void createGame(){
        // Initialize new players
        player1 = new Player();
        player2 = new Player();

        // initialize Game variables
        numOfCreatures = 0;
        player1Turn = true;
        gameExists = false;
        gameInProgress = true;

        // decide on the amount of creatures that will be placed on the grids
        int creatureAmount = (int)((Math.random() * (3)) + 6);
        //System.out.println(creatureAmount + " creatures will be placed");
        
        // set some array lengths
        player1.creatureCellsFound = new int[creatureAmount];
        player2.creatureCellsFound = new int[creatureAmount];

        // place the creatures
        for (int i = 0; i < creatureAmount; i++){
            // select a random number to get a random type of creature
            int randInt = (int)(Math.random() * 5) + 1; // the constant (not 1) is how many types of creature there are

            numOfCreatures++;
            
            placeCreature("trap", player1);
            placeCreature("trap", player2);

            switch (randInt) {
                case 1:
                    placeCreature("minnow", player1);
                    placeCreature("minnow", player2);
                    break;

                case 2:
                    placeCreature("shark", player1);
                    placeCreature("shark", player2);
                    break;

                case 3:
                    placeCreature("manta", player1);
                    placeCreature("manta", player2);
                    break;

                case 4:
                    placeCreature("octopus", player1);
                    placeCreature("octopus", player2);
                    break;

                case 5:
                    placeCreature("gulper", player1);
                    placeCreature("gulper", player2);
                    break;

                default:
                    System.out.println("error: value " + randInt + "was an invalid choice for a creature");
                    break;
            }
            
            // if the system has failed to place a creature > 100 times 
            if (failCounter > 100){
                failCounter = 0;
                createGame();
                return;
            }
        }
    }
    
    public void placeCreature(String type, Player player){ 
        String code = "e";
        int width = 0;
        int height = 0;

        switch (type) {
            case "minnow":
                code = "m";
                width = 1;
                height = 1;
                break;

            case "shark":
                code = "s";
                width = 3;
                height = 2;
                break;

            case "manta":
                code = "r"; // ray for manta ray
                width = 5;
                height = 3;
                break;

            case "octopus":
                code = "o";
                width = 5;
                height = 5;
                break;

            case "gulper":
                code = "g";
                width = 3;
                height = 2;
                break;

            case "trap":
                code = "t";
                width = 1;
                height = 1;
                break;
        
            default:
                System.out.println("error: There was an error with the animal selected to be on the grid");
                break;
        }

        boolean passed = false;
        int x = 0;
        int y = 0;

        while (!passed){
             // get random position
            x = (int)((Math.random() * (player.grid.length)));
            y = (int)((Math.random() * (player.grid[0].length)));

            // check if space valid
            passed = checkIfSpaceClear(width, height, x, y, player.grid);

            // if the creature has failed to be placed > 100 times
            if (failCounter > 100){
                return;
            }
        }

        switch (code) {
            // place minnow
            case "m":
                player.grid[x][y] = "m" + numOfCreatures;
                player.creatureCellsFound[numOfCreatures - 1] = 1;
                break;

            // place shark
            case "s":
                player.grid[x][y] = "s" + numOfCreatures;
                player.grid[x][y - 1] = "s" + numOfCreatures;
                player.grid[x - 1][y] = "s" + numOfCreatures;
                player.grid[x + 1][y] = "s" + numOfCreatures;
                player.creatureCellsFound[numOfCreatures - 1] = 4;
                break;

            case "r":
                player.grid[x][y + 1] = "r" + numOfCreatures;
                player.grid[x - 2][y - 1] = "r" + numOfCreatures;
                player.grid[x + 2][y - 1] = "r" + numOfCreatures;
                player.grid[x - 1][y] = "r" + numOfCreatures;
                player.grid[x + 1][y] = "r" + numOfCreatures;
                player.creatureCellsFound[numOfCreatures - 1] = 5;
                break;
            
            case "o":
                player.grid[x + 2][y] = "o" + numOfCreatures;
                player.grid[x + 1][y - 1] = "o" + numOfCreatures;
                player.grid[x][y - 2] = "o" + numOfCreatures;
                player.grid[x - 1][y - 1] = "o" + numOfCreatures;
                player.grid[x - 2][y] = "o" + numOfCreatures;
                player.grid[x - 1][y + 1] = "o" + numOfCreatures;
                player.grid[x][y + 2] = "o" + numOfCreatures;
                player.grid[x + 1][y + 1] = "o" + numOfCreatures;
                player.creatureCellsFound[numOfCreatures - 1] = 8;
                break;

            case "g":
                player.grid[x][y] = "g" + numOfCreatures;
                player.grid[x + 1][y] = "g" + numOfCreatures;
                player.grid[x - 1][y] = "g" + numOfCreatures;
                player.grid[x + 1][y + 1] = "g" + numOfCreatures;
                player.creatureCellsFound[numOfCreatures - 1] = 4;
                break;

            case "t":
                player.grid[x][y] = "t" + numOfCreatures;
                break;
            default:
                System.out.println("error: trying to place a creature with an unknown code");
                break;
        }
    }
    
    // -_-_-_-_- overlap check might be broken -_-_-_-_-
    private boolean checkIfSpaceClear(int width, int height, int x, int y, String[][] grid){ 
        // checking to see if the creature would be off of the grid
        if (x + Math.round((float)width/2 + 1) > grid.length || x - Math.round(((float)width/2 + 1)) < 0){
            return false;
        }
        
        if (y + Math.round((float)height/2 + 1) > grid[0].length || y - Math.round(((float)height/2 + 1)) < 0){
            return false;
        }
        
        // checking to see if the space is occupied
        float baseI = (x - Math.round(((float)width / 2) - 1));
        float baseJ = (y - Math.round(((float)height / 2) - 1));

        for (int i = (int)baseI; i < baseI + width; i++){
            // i is the x coordinate we are at in the grid
            for (int j = (int)baseJ; j < baseJ + height; j++){
                // j is the y coordinate we are at in the grid

                if (grid[i][j] != "e"){
                    //System.out.println("new creature overlapped with existing one");
                    failCounter++;
                    return false;
                }
            }
        }

        // reset failcounter if an animal is successfully placed
        failCounter = 0;
        return true;
    }

    // 
    // GAME CREATION END
    //

    //
    // GAME SAVING START
    //

    public void saveGame(){
        File savefile = new File("saveFile.txt");
        
        if (savefile.exists()){
            try{
                // make save file blank
                FileWriter eraser = new FileWriter(savefile);
                eraser.write("");
                eraser.close();

                // write new data into file
                FileWriter writer = new FileWriter(savefile, true);
                
                // write the player turn status and the number of creatures in the game 
                writer.write(convertBooleanToString(player1Turn) + "," + numOfCreatures +"\n");
                
                // write Player1 Stats
                writer.write(writePlayerStatsToFile(player1));
                writer.write(writePlayerStatsToFile(player2));
                writer.close();
            } catch(IOException e){
                System.err.println("IOException occured");
            }
        }
        else{
            System.out.println("error: file for saving was not found");
        }
    }

    private String writePlayerStatsToFile(Player player){
        // write Player Stats
        String playerDataLine = "";
        // player.grid
        playerDataLine += (player.grid.length + "," + player.grid[0].length + ",");
        for (int i = 0; i < player.grid.length; i++){
            for (int j = 0; j < player.grid[0].length; j++){
                playerDataLine += (player.grid[i][j] + ",");
            }
        }
        // player.foundcells
        int counter = 0;
        for (int i = 0; i < player.foundCells.length; i++){
            if (player.foundCells[i] != null){
                counter++;
            }
            else{
                break;
            }
        }
    
        playerDataLine += (counter +","); // how many notable cells there are

        for (int i = 0; i < counter; i++){
            playerDataLine += (player.foundCells[i].x + "," + player.foundCells[i].y + "," + player.foundCells[i].code + ",");
        }

        // player.creatureCellsFound
        for (int i = 0; i < player.creatureCellsFound.length; i++){
            playerDataLine += (player.creatureCellsFound[i] + ",");
        }
        playerDataLine += (player.score + ",");
        playerDataLine += (player.numOfTurnsTaken + ",");
        playerDataLine += (player.health + "\n");
        return playerDataLine;
    }

    private String convertBooleanToString(boolean bool){
        if (bool == false){
            return "false";
        }
        else{
            return "true";
        }
    }

    private Boolean convertStringToBoolean(String string){
        if (string == "false"){
            return false;
        }
        else{
            return true;
        }
    }

    public void loadGame(){
        File savefile = new File("saveFile.txt");
        
        if (savefile.exists()){
            try{
                // get the save file
                Scanner reader = new Scanner(savefile);
                String[] gameLine = reader.nextLine().split(",");
                String[] player1Line = reader.nextLine().split(",");
                String[] player2Line = reader.nextLine().split(",");

                reader.close();

                player1Turn = convertStringToBoolean(gameLine[0]);
                numOfCreatures = Integer.parseInt(gameLine[1]);

                setPlayerStats(player1Line, player1);
                setPlayerStats(player2Line, player2);
            } catch(IOException e){
                System.err.println("IOException occured");
            }
        }
        else{
            System.out.println("error: file for saving was not found");
        }
    }

    private void setPlayerStats(String[] data, Player player){
        // player.grid
        int counter = 2;

        for (int i = 0; i < Integer.parseInt(data[0]); i++){
            for (int j = 0; j < Integer.parseInt(data[1]); j++){
                player.grid[i][j] = data[counter];
                counter++;
            }
        }
        
        // player.foundcells
        int amountOfValidCells = Integer.parseInt(data[counter]);
        counter++;
        for (int i = 0; i < amountOfValidCells; i++){
            player.foundCells[i] = new Cell();
            player.foundCells[i].x = Integer.parseInt(data[counter]);
            counter++;
            player.foundCells[i].y = Integer.parseInt(data[counter]);
            counter++;
            player.foundCells[i].code = data[counter];
            counter++;
        }
        // player.creatureCellsFound
        for (int i = 0; i < Game.numOfCreatures; i++){
            player.creatureCellsFound = new int[Game.numOfCreatures];
            player.creatureCellsFound[i] = Integer.parseInt(data[counter]);
            counter++;
        }

        player.score = Integer.parseInt(data[counter]);
        counter++;
        player.numOfTurnsTaken = Integer.parseInt(data[counter]);
        counter++;
        player.health = Integer.parseInt(data[counter]);
    }
    //
    // GAME SAVING END
    //

    // misc
    public int validateInt(int value, int min, int max){
        while (value < min || value > max) {
            System.out.println("That was an invalid number, please try entering a number between " + min + " and " + max);
            value = s.nextInt();
        }
        return value;
   }
}

class Player{
   Scanner s = new Scanner(System.in);

   public String[][] grid;
   public Cell[] foundCells;
   public int[] creatureCellsFound; // the number in a cell - 1 is the index.
   public int score;
   public int health;
   public int numOfTurnsTaken;

   public Player(){
        grid = new String[16][16];
        for (int i = 0; i < grid.length; i++){
            // i is x
            for (int j = 0; j < grid[0].length; j++){
                // j is y
                grid[i][j] = "e";
            }
        }

        // foundCells is the same length as the amount of cells in the grid
        foundCells = new Cell[(grid.length * grid[0].length)];

        health = 25;
   }

   public int takeTurn(){ // returning int just to get a value to check to see if it should return to the menu
        int x = 0;
        int y = 0;
        boolean validCellChosen = false;

        // loop until a valid cell is chosen
        while (!validCellChosen) {
            // get user selected cell coordinated
            System.out.println("Please enter the X value for the coordinate you want to search (max = " + (grid.length - 1)+ ")");
            x = s.nextInt();
            if (x != 7337 && x != -1){ // debug number used to display grid without - and menu number
                x = validateInt(y, 0, grid[0].length - 1);
            }
            if (x == -1){
                return -1;
            }

            System.out.println("Please enter the Y value for the coordinate you want to search (max = " + (grid[0].length - 1)+ ")");
            y = s.nextInt();
            if (y != 7337 && y != -1){ // debug number used to display grid without - and menu number
                y = validateInt(y, 0, grid[0].length - 1);
            }

            // debug to display the grid for the current player
            if (x == 7337 || y == 7337){
                displayGrid();
                return 0;
            }

            if (y == -1){
                return -1;
            }

            // check to see if this cell has been selected before 
            boolean cellAlreadyDone = false;
            for (int i = 0; i < foundCells.length; i++){
                if (foundCells[i] != null){
                    if (x == foundCells[i].x && y == foundCells[i].y){
                        cellAlreadyDone = true;
                        System.out.println("This coordinate has been chosen already, " + foundCells[i].code + " was found here");
                        System.out.println("Please select another set of coordinates");
                    }
                }
            }
            
            // final check to see if the cell is valid 
            if (!cellAlreadyDone) {
                validCellChosen = true;
            }
        }

        
        // put the cell in the list of found ones to stop it from being chosen multiple times
            foundCells[numOfTurnsTaken] = new Cell();
            foundCells[numOfTurnsTaken].x = x;
            foundCells[numOfTurnsTaken].y = y;
            foundCells[numOfTurnsTaken].code = grid[x][y];
            
            // increase the number of turns taken by the player
            numOfTurnsTaken++;

        // check to see if the cell was empty or not
        if (!grid[x][y].equals("e")){
            if (grid[x][y].substring(0, 1).equals("t")){
                System.out.println("You hit a mine! the trap explodes! -10 points -5 health");
                score -= 10;
                health -= 5;
                return 0;
            }
            System.out.println("HIT!\nYou found a creature at that coordinate, creature code: " + grid[x][y]);

            creatureCellsFound[Integer.parseInt(grid[x][y].substring(1, 2)) - 1]--;
            if (creatureCellsFound[Integer.parseInt(grid[x][y].substring(1, 2)) - 1] == 0){
                System.out.println("You found a whole creature! +5 points.");
                score += 5;
            }

            score += 5;
        }
        return 0;
   }

   public void displayInGameGrid(){   
        String numbLine = "  ";
        for (int i = 0; i < grid.length; i++){
            if (i < 10){
                numbLine += "| " + i + " |";
            }
            else{
                numbLine += "|" + i + " |";
            }
        }
        System.out.println(numbLine);

        for (int i = 0; i < grid.length; i++){
            // i is y
            String gridLine = "";
            
            if (i > 9){
                gridLine = i + "";
            }
            else{
                gridLine = i + " ";
            }
            for (int j = 0; j < grid[0].length; j++){
                // j is x
                if (!cellAlreadyFound(j, i)){
                    gridLine += "| - |";
                }
                else if (grid[j][i].equals("e")){
                    gridLine += "| # |";
                        
                }
                else{
                    gridLine += "| " + grid[j][i].substring(0,1) + " |";
                }
            }
            System.out.println(gridLine);
        }
   }

   private boolean cellAlreadyFound(int x, int y){
        int numOfCellsFound = 0;

        for (int i = 0; i < foundCells.length; i++){
            if (foundCells[i] != null){
                numOfCellsFound++;
            }
        }

        for (int i = 0; i < numOfCellsFound; i++){
            if (foundCells[i].x == x && foundCells[i].y == y){
                return true;
            }
        }
        return false;
   }

    public void displayGrid(){   
        String numbLine = "  ";
        for (int i = 0; i < grid.length; i++){
            if (i < 10){
                numbLine += "| " + i + " |";
            }
            else{
                numbLine += "|" + i + " |";
            }
        }
        System.out.println(numbLine);

        for (int i = 0; i < grid.length; i++){
            // i is y
            String gridLine = "";

            if (i > 9){
                gridLine = i + "";
            }
            else{
                gridLine = i + " ";
            }
            for (int j = 0; j < grid[0].length; j++){
                // j is x
                if (grid[j][i].length() == 1){
                    gridLine += "| " + grid[j][i] + " |";
                }
                else if (grid[j][i].length() == 2){
                    gridLine += "|" + grid[j][i] + " |";
                }
                else{
                    gridLine += "|" + grid[j][i] + "|";
                }
            }
            System.out.println(gridLine);
        }
   }

   public int validateInt(int value, int min, int max){
        while (value < min || value > max) {
            System.out.println("That was an invalid number, please try entering a number between " + min + " and " + max);
            value = s.nextInt();
        }
        return value;
   }
}

class Cell {
    int x;
    int y;
    String code;

    public Cell(){
        x = 0;
        y = 0;
        code = "e";
    }
}

