/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

/**
 *
 * @author Gerg
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int min = 1;
        int max = 1;
        int multiple = 1;
        
        String[] displayedValues = new String[max-min+1];
        for(int i = 0; i<displayedValues.length; i++) {
            displayedValues[i] = String.valueOf((i + min) * multiple);
        }
        for(int i = 0; i<displayedValues.length; i++) {
            System.out.println(i + ":" + displayedValues[i]);
        }    
    }
    
}
