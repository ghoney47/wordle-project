/**
 * @author Gavin Honey
 * */
package wordle;

import java.io.*;
import java.util.*;

public class Wordle {

/* Private instance variables */

    //used for key coloring (in reset)
    private static final String[] ALLLETTERS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    //used for key coloring helper method
    public static final int CORRECT = 1;
    public static final int PRESENT = 0;
    public static final int MISSING = -1;

    //used for cases for scoring
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;

    //used for endGame helper method
    public static final int WIN = 1;
    public static final int LOSE = 0;

    private WordleGWindow gw; //current window being used
    private static final String[] DIC = WordleDictionary.FIVE_LETTER_WORDS; //allows for dictionary access
    private int round = 0; //to track the round currently on/row number
    private String word; //random word (in uppercase)
    private boolean gameDone = false; //keeps track of game start and stop

    public void run() {
        gameDone = false;
        gw = new WordleGWindow();

        /*WORD ANSWER HERE*/
        word = randomWord().toUpperCase();
        System.out.println(word);
        
        gw.addEnterListener((s) -> {
            //if game is done, restarts, otherwise plays game
            if (gameDone){
                restart();
            } else {
                enterAction(s);
            }
    
        });
    }

    /**
     * @return String of random word in 5 letter word dictionary
     * */
    private static String randomWord() {
        Random random = new Random();
        //gets a random number within the index of the array
        int i = random.nextInt((DIC.length - 1));
        //gets random 5 letter word
        return DIC[i];
    }

    /**
     * @param s string input that is to be assigned
     * Assigns the letters of inputted string to the display
     * */
    public void setDisplayChars(String s){
        for(int i = 0; i < s.length(); i++){
            //uses round for row number and iterates through each box to display the word
            gw.setSquareLetter(round, i, s.substring(i, i+1));
        }
    }

    /**
     * @param s string input of intputted word
     * @returns true if word is found in five letter word dictionary
     * */
    private static boolean validWord(String s){
        //searches list of valid words and returns true if the word exists in the dictionary
        for (int i = 0; i < DIC.length; i++){
            if (s.toLowerCase().equals(DIC[i])){
                return true;
            } 
        }
        return false;
    }

/*
 * Called when the user hits the RETURN key or clicks the ENTER button,
 * passing in the string of characters on the current row.
 */
    public void enterAction(String s) {


        //if the input is valid, performs checks on the word
        if (validWord(s)){
            if(s.equals(word)){
                //sets squares and keys green
                for (int i = 0; i < WordleGWindow.N_COLS; i++){
                    gw.setSquareColor(round, i, WordleGWindow.CORRECT_COLOR);
                }
                for (int i = 0; i < WordleGWindow.N_COLS; i++){
                    gw.setKeyColor(String.valueOf(s.charAt(i)), WordleGWindow.CORRECT_COLOR);
                }
                endGame(WIN);
                //stops game loop
                gameDone = true;
            } else { 
                gw.showMessage("In word list");
                checkInput(s);
            }
            
            //increments round
            round++;
        } else {                

            //otherwise terminates word and displays message (does not increment round)
            gw.showMessage("Not in word list");
                
        }
        //ends game if round is maxed out
        if (round == WordleGWindow.N_ROWS){
            endGame(LOSE);
            //stops game loop
            gameDone = true;
        }
        if (!gameDone){
            //sets the current row to the round # if game loop is active
             gw.setCurrentRow(round);
        }
    }
        
    

    //method checks the letter at each value and sets the color of the boxes after user input
    public void checkInput(String s){
        for (int i = 0; i < word.length(); i++){

            //sets character to check as current iteration
            char c = s.charAt(i);
            
            //holds the number of times a character shows up
            int num = contains(c);

            //sets color of square based on if the letter is contained or not, or in the right place
            if (compareIndex(c, i) && shouldColorBox(c, num, i)){
                gw.setSquareColor(round, i, WordleGWindow.CORRECT_COLOR);
                //add coloring keys portion!!!
                colorKey(num, i, CORRECT, c);

            //if letter is contained 
            } else if (num > 0){

                //checks if the letter has been accounted for
                if (shouldColorBox(c, num, i)){
                    gw.setSquareColor(round, i, WordleGWindow.PRESENT_COLOR);
                    colorKey(num, i, PRESENT, c);
                } else {
                    gw.setSquareColor(round, i, WordleGWindow.MISSING_COLOR);
                }
            } else {
                gw.setSquareColor(round, i, WordleGWindow.MISSING_COLOR);
                colorKey(num, i, MISSING, c);
            }


        }
    }

    //helper method to check if string at a given index is the same
    /**
     * @param c char currently on
     * @param index int of the index of the word
     * @return Boolean if they are the same 
     * */
    private boolean compareIndex(char c, int index){
        return c == word.charAt(index);
    }

    //helper method to check if the character is contained in the mystery word
    /**
     * @param char is the letter being checked
     * @return int how many times character shows up
     * */
    private int contains(char c){
        //holds amout of cases 
        int out = 0;
        for (int i = 0; i < word.length(); i++){
            //increments out if char matches
            if (c == word.charAt(i)){
                out++;
            }
        }
        //returns false if the character is not found within the word
        return out;
    }

    //helper method to check if letter is repeatedly accounted for
    /**
     * @pre letter must be contained (checked within higher function)
     * @param amount is the amount of times the character shows in the word (from contains method)
     * @param index is the current index of the character in the input
     * @return true if letter has already been counted as contained enough times
     * */
    //need to finish edge cases
    private boolean shouldColorBox(char c, int amount, int index){

        //counter to see how many already accounted for 
        int counter = 0;

        //checks previous letters
        for (int i = index - 1; i >= 0; i--){
        
            //checks if letter has already been accouted for
            if ((gw.getSquareColor(round, i).equals(WordleGWindow.CORRECT_COLOR) || gw.getSquareColor(round, i).equals(WordleGWindow.PRESENT_COLOR)) && (gw.getSquareLetter(round, i).charAt(0) == c)){
                
                //increments counter if already accounted for
                counter++;
            }
        }

        return counter < amount;
    }

    /**
     * helper function to determine color of keys
     * @pre word is valid and letter is contained - both checked within higher function
     * @param c is the character currently being delt with
     * @param correctness is the final int variable passed from the higher function
     * @param index is the current index of the word
     * @param amount is the amount of times the character is in the word
     * */

    private void colorKey(int amount, int index, int correctness, char c){
        //converting c to string
        String s = String.valueOf(c);

        //counter to check if accounted for
        int counter = 0;

        if ((gw.getSquareColor(round, index).equals(WordleGWindow.CORRECT_COLOR) || gw.getSquareColor(round, index).equals(WordleGWindow.PRESENT_COLOR)) && (gw.getSquareLetter(round, index).charAt(0) == c)){
                
            //increments counter if already accounted for
            counter++;
        }

        switch (correctness) {
            case CORRECT:
                gw.setKeyColor(s, WordleGWindow.CORRECT_COLOR);
                break;
            case MISSING:
                gw.setKeyColor(s, WordleGWindow.MISSING_COLOR);
                break;
            case PRESENT:
                //if there are some uncounted for cases or all cases have been accounted for
                if (counter <= amount && !gw.getKeyColor(s).equals(WordleGWindow.CORRECT_COLOR)){
                    gw.setKeyColor(s, WordleGWindow.PRESENT_COLOR);
                }
                break;
        }
        
    }

    /**
     * @param result static final WIN or LOSE
     * helper function to determine end game screen
     * */
    private void endGame(int result){
        switch (result){
            case WIN: 
                gw.showMessage("Correct!");
                writeScore();
                break;
            case LOSE:
                gw.showMessage("You Lose");
                break;

        }

        //delay with sleep between end game and scores showing
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e){
            System.out.println("delay failed");
        }
        
        displayScore();
        

    }

    //helper method adds score to text file to be read
    private void writeScore(){
        //creates bufferedwriter writer in append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./memory.txt", true))){
            writer.write((round + 1) + "\n");
            System.out.println("Score " + (round + 1) + " was saved");
            writer.close();
        } catch (IOException e){
            System.out.println("Save unsuccessful");
        }
        
    }

    /**
     * helper method that both reads the memory and displays it on the board
     * */
    private void displayScore(){
        try (BufferedReader reader = new BufferedReader(new FileReader("./memory.txt"))){

            //string that each line is stored in
            String line;

            ArrayList<String> file = new ArrayList<>();

            //reads file and adds all data to arraylist file
            while ((line = reader.readLine()) != null) {
				file.add(line);
			}

            if (file.isEmpty()){
                gw.showMessage("Empty Memory");
            } else {
                clearDisplay();
                for (int i = 0; i < WordleGWindow.N_ROWS; i++)
                {
                    gw.setSquareColor(i, 0, WordleGWindow.CORRECT_COLOR);
                    gw.setSquareLetter(i, 0, String.valueOf(i+1));
                }

                //variables to hold each number of round wins
                int first = 0;
                int second = 0;
                int third = 0;
                int fourth = 0;
                int fifth = 0;
                int sixth = 0;

                for (String s : file){
                    switch(Integer.valueOf(s)){

                        //increments counters
                        case ONE: first++; break;
                        case TWO: second++; break;
                        case THREE: third++; break;
                        case FOUR: fourth++; break;
                        case FIVE: fifth++; break;
                        case SIX: sixth++; break;

                    }
                }
                //stores counters in array 
                int[] scores = {first, second, third, fourth, fifth, sixth};

                //displays counter values on final screen
                for (int i = 0; i < WordleGWindow.N_ROWS; i++){
                    if (scores[i] < 10){
                        gw.setSquareLetter(i, (WordleGWindow.N_COLS - 1), String.valueOf(scores[i]));
                    } else {
                        //if the value is greater than 10, shifts over to fill next box
                        int temp = scores[i];
                        int columnOffset = 0;
                        while (temp > 10){
                            
                            gw.setSquareLetter(i, (WordleGWindow.N_COLS - 1) - columnOffset, String.valueOf(temp%10));
                            columnOffset++;
                            
                            //removes a 'place' from the number
                            temp /= 10;
                        }
                        gw.setSquareLetter(i, (WordleGWindow.N_COLS - 1) - columnOffset, String.valueOf(temp%10));
                    }
                }
                
            }

            gw.showMessage("Press Enter to play again");
            
            reader.close();
        } catch (IOException e){
            System.out.println("Memory not found");
        }
    }

    //helper method to restart play
    private void restart(){
        round = 0;
        gameDone = false;
        word = randomWord().toUpperCase();
        clearDisplay();
        gw.showMessage("New Game Has Begun!");
        gw.setCurrentRow(round);
    }

    //helper method to clear the display
    private void clearDisplay(){
        for (int i = 0; i < WordleGWindow.N_ROWS; i++){
            gw.setCurrentRow(i);
        }

        for (String l : ALLLETTERS){
            gw.setKeyColor(l, WordleGWindow.MISSING_COLOR);
        }
        
    }




/* Startup code */

    public static void main(String[] args) {
        new Wordle().run();
    }


}
