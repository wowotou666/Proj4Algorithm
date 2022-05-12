/*wowotou666*/
/*wew92@pitt.edu*/

import java.io.*;
import java.util.*;

public class Crossword
{
    private static DictInterface Dict;
    private static char[][] board;
    private static int boardSize;
    private static StringBuilder[] colStr;
    private static StringBuilder[] rowStr;


    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            System.out.println("please enter correct inputs");
            return;
        }

        Dict = new MyDictionary();

        Scanner dictScan = new Scanner(new FileInputStream(args[0]));
        Scanner testScan = new Scanner(new FileInputStream(args[1]));

        String word;
        while(dictScan.hasNext()){
            word = dictScan.nextLine();
            Dict.add(word);
        }
        dictScan.close();

        boardSize = testScan.nextInt();
        board = new char[boardSize][boardSize];

        String rowWord;
        int row = 0;
        int col = 0;
        String ignore = testScan.nextLine();
        while(testScan.hasNext()){
            rowWord = testScan.nextLine();
            for(int i = 0; i < boardSize; i++){
                board[row][col] = Character.toLowerCase(rowWord.charAt(i));
                col++;
            }
            row++;
            col = 0;
        }
        testScan.close();

        colStr = new StringBuilder[boardSize];
        rowStr = new StringBuilder[boardSize];
        for(int i = 0; i < boardSize; i++){
            colStr[i] = new StringBuilder("");
            rowStr[i] = new StringBuilder("");
        }
        solve(0, 0);
    }


    private static void solve(int row, int col){
        if(board[row][col] == '+'){
            for(char ch = 'a'; ch <= 'z'; ch++){
                if(isValid(row, col, ch)) {
                    rowStr[row].append(ch);
                    colStr[col].append(ch);
                    if(row == boardSize-1 && col == boardSize -1){
                        print();
                    }
                    else if(col < boardSize-1){
                        solve(row, col+1);
                    }
                    else{
                        solve(row+1, 0);
                    }
                    rowStr[row].deleteCharAt(rowStr[row].length() -1);
                    colStr[col].deleteCharAt(colStr[col].length() -1);
                }
            }
        }

        else if(board[row][col] == '-') {
            rowStr[row].append(board[row][col]);
            colStr[col].append(board[row][col]);

            if(row == boardSize-1 && col == boardSize -1){
                print();
            }
            else if(col < boardSize-1){
                solve(row, col+1);
            }
            else{
                solve(row+1, 0);
            }
            rowStr[row].deleteCharAt(rowStr[row].length() -1);
            colStr[col].deleteCharAt(colStr[col].length() -1);
        }

        else if(board[row][col] != '+' && board[row][col] != '-'){
            if(isValid(row, col, board[row][col])) {
                rowStr[row].append(board[row][col]);
                colStr[col].append(board[row][col]);
                if(row == boardSize-1 && col == boardSize -1){
                    print();
                }
                else if(col < boardSize-1){
                    solve(row, col+1);
                }
                else{
                    solve(row+1, 0);
                }
                rowStr[row].deleteCharAt(rowStr[row].length() -1);
                colStr[col].deleteCharAt(colStr[col].length() -1);
            }
        }
    }


    private static boolean isValid(int row, int col, char c){
        boolean valid = true;

        int startRow = 0;
        int startCol = 0;

        if(rowStr[row].toString().contains("-")){
            for(int i = 0; i < rowStr[row].length(); i++){
                if(rowStr[row].toString().charAt(i) == '-'){
                    startRow = i+1;
                }
            }
        }

        if(colStr[col].toString().contains("-")){
            for(int i = 0; i < colStr[col].length(); i++){
                if(colStr[col].toString().charAt(i) == '-'){
                    startCol = i+1;
                }
            }
        }

        if(row < boardSize-1 && board[row+1][col] != '-'){
            int val = Dict.searchPrefix(colStr[col].append(c),startCol,colStr[col].length()-1);
            colStr[col].deleteCharAt(colStr[col].length()-1);
            if(!(val == 1 || val == 3))
                valid = false;
        }

        if(row == boardSize-1 || board[row+1][col] == '-'){
            int val = Dict.searchPrefix(colStr[col].append(c),startCol,colStr[col].length()-1);
            colStr[col].deleteCharAt(colStr[col].length() - 1);
            if (!(val == 2 || val == 3))
                valid = false;
        }

        if(col < boardSize-1 && board[row][col+1] != '-'){
            int val = Dict.searchPrefix(rowStr[row].append(c),startRow,rowStr[row].length()-1);
            rowStr[row].deleteCharAt(rowStr[row].length() - 1);
            if(!(val == 1 || val ==3))
                valid = false;
        }

        if(col == boardSize-1 || board[row][col+1] == '-'){
            int val = Dict.searchPrefix(rowStr[row].append(c),startRow,rowStr[row].length()-1);
            rowStr[row].deleteCharAt(rowStr[row].length() - 1);
            if (!(val == 2 || val == 3))
                valid = false;
        }
        return valid;
    }


    private static void print(){
        for (int row = 0; row < boardSize; row++){
            System.out.println(rowStr[row].toString());
        }
        System.out.println("Score: " + point());
        System.exit(0);
    }


    private static int point(){
        int score = 0;
        int[] letterPoint = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

        for(int row = 0; row < boardSize; row++) {
            for(int col = 0; col < boardSize; col++) {
                char c = rowStr[row].charAt(col);
                if(c == '-'){
                    score += 0;
                }
                else{
                    score += letterPoint[Character.toUpperCase(rowStr[row].charAt(col)) - 'A'];
                }
            }
        }
        return score;
    }

}
