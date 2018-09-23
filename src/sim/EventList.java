/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

/**
 *
 * @author Chirag
 */
import java.util.LinkedList;

class EventList extends LinkedList {

    public EventList() {
        super();
    }

    public Object getMin() {
        return getFirst();
    }

    public void enqueue(Object _o) {
        add(_o);
    }

    public void dequeue() {
        removeFirst();
    }
}
