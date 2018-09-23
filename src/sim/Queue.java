/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

import java.util.LinkedList;

/**
 *
 * @author Chirag
 */
public class Queue extends LinkedList {

    public void enqueue(Object _o) {
        add(_o);
    }

    public Object dequeue() {
        return removeFirst();
    }

}
