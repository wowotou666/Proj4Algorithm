//wowotou666~wew92@pitt.edu
//Before Part1 they are just easy and necessary initial preparations.

//TO-DO Add necessary imports
import java.util.*;
import java.io.FileInputStream;

public class AutoComplete{
  //TO-DO: Add instance variable: you should have at least the tree root
  private DLBNode root;

  public AutoComplete(String dictFile) throws java.io.IOException {
    //TO-DO Initialize the instance variables
    root = null;
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      //TO-DO call the public add method or the private helper method if you have one
      this.add(word);
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add word to the tree
  public void add(StringBuilder word){
    //TO-DO Implement this method
    root = add(root,word,0); //add is a helper method implemented below.
  }

  private DLBNode add(DLBNode curNode,StringBuilder word,int pos){
    //node is null and create a new one.
    if(curNode == null){
      curNode = new DLBNode(word.charAt(pos),0);
      if(pos < word.length()-1){
        curNode.child = add(curNode.child,word,pos+1);
      }
      else{
        curNode.isWord = true; //isWord is used to check whether the position is at the end of the word, and it is same logic for following cases.
      }
    }
    //node exists and its characters correspond to word's position. Go to child node.
    else if(curNode.data == word.charAt(pos)){
      if(pos < word.length()-1){
        curNode.child = add(curNode.child,word,pos+1);
      }
      else{
        curNode.isWord = true;
      }
    }
    //under other conditions, search characters at siblings of same levels.
    else{
      curNode.sibling = add(curNode.sibling,word,pos);
    }
    return curNode;
  }


  //increment the score of word
  public void notifyWordSelected(StringBuilder word){
    //TO-DO Implement this method
    updateScore(root,word,0); //updateScore is a helper method implemented below.
  }

  //workflow is the same as add.
  private void updateScore(DLBNode curNode,StringBuilder word,int pos){
    //node is null so directly return.
    if(curNode == null){
      return;
    }
    //current node matches character, so recursive search child nodes.
    if(curNode.data == word.charAt(pos)){
      if(pos < word.length()-1){
        updateScore(curNode.child,word,pos+1);
      }
      //there exists a complete word so increase the score
      else{
        if(curNode.isWord){
          curNode.score += 1;
        }
      }
    }
    //current node does not match character, so search the siblings.
    else{
      updateScore(curNode.sibling,word,pos);
    }
  }


  //get the score of word
  public int getScore(StringBuilder word){
    //TO-DO Implement this method
    return helpGetScore(root, word, 0); //helpGetScore is a helper method implemented below.
  }


  private int helpGetScore(DLBNode curNode,StringBuilder word, int pos){
    int score;
    if (curNode == null){
      return -1;
    }
    //get score under match condition using recursive call
    if(curNode.data == word.charAt(pos)){
      if(pos < word.length()-1){
        score = helpGetScore(curNode.child,word,pos+1);
      }
      else{
        return curNode.score;
      }
    }
    //go to sibling node
    else{
      score = helpGetScore(curNode.sibling,word,pos);
    }
    return score;
  }

  /**
   * Part 2: retrieve word suggestions in sorted order.
   */
  
  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
  public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    //TO-DO Implement this method
    if (word.length() == 0 ){
      return null;
    }
    if (root == null){
      return null;
    }
    ArrayList<Suggestion> sugList = new ArrayList<>();
    collect(root,word,sugList,new StringBuilder(""),0); //collect is a helper method implemented below.
    Collections.sort(sugList); //sort list
    Collections.reverse(sugList); //in descending order
    return sugList;
  }


  private void collect(DLBNode curNode,StringBuilder word,ArrayList<Suggestion> sugList,StringBuilder curWord,int pos){
    //at the end of word
    if (pos == word.length()-1){
      if(curNode.data == word.charAt(pos)){
        //it is a complete word so add it to suglist, then go to the next child node
        if (curNode.isWord){
          sugList.add(new Suggestion(new StringBuilder(curWord).append(curNode.data),curNode.score));
        }
        if(curNode.child != null){
          collect(curNode.child,word,sugList,new StringBuilder(curWord).append(curNode.data),pos+1);
        }
      }
      //if not, go to the sibling to search
      else{
        if(curNode.sibling != null){
          collect(curNode.sibling,word,sugList,new StringBuilder(curWord),pos);
        }
      }
    }
    //when position is larger than length
    else if(pos > word.length()-1){
      //when it is a complete word, add it to sugList
      if (curNode.isWord) {
        sugList.add(new Suggestion(new StringBuilder(curWord).append(curNode.data), curNode.score));
      }
      // after possible adding, go to the child nodes
      if(curNode.child != null){
        collect(curNode.child,word,sugList,new StringBuilder(curWord).append(curNode.data),pos+1);
      }
      // after possible adding, go to the sibling nodes
      if(curNode.sibling != null) {
        collect(curNode.sibling, word, sugList, new StringBuilder(curWord), pos);
      }
    }
    //when position is less than length, recursive call on child&sibling nodes to get position of prefix
    else{
      if(curNode.data == word.charAt(pos)){
        if(curNode.child != null){
          collect(curNode.child,word,sugList,new StringBuilder(curWord).append(curNode.data),pos+1);
        }
      }
      else{
        if(curNode.sibling != null){
          collect(curNode.sibling, word, sugList, new StringBuilder(curWord), pos);
        }
      }
    }
  }


  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair. 
  //This class should be Comparable to itself.
  public class Suggestion implements Comparable<Suggestion> /*.....*/
  {
    //TO-DO Fill in the fields and methods for this class. Make sure to have them public as they will be accessed from the test program A2Test.java.
    //creation and initialization
    public StringBuilder word;
    public int score;
    public Suggestion(StringBuilder word,int score){
      this.word = new StringBuilder(word);
      this.score = score;
    }
    //implementation of compare order: higher socre should be in earlier order, while put smaller word earlier when scores are equal
    public int compareTo(Suggestion o){
      int score1 = this.score;
      int score2 = o.score;
      int order = score1 - score2;
      if(order == 0){
        order = o.word.toString().compareTo(this.word.toString());
      }
      return order;
    }
  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
