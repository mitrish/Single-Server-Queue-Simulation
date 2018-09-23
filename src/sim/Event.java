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
public class Event {
    private double time;
    private int type;
    public Event(int _type, double _time) {
        type = _type;
        time = _time;
    }
    public int getType() {
        return type;
    }
    public double getTime() {
        return time;
    }

}
