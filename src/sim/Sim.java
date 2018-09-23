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
import java.util.*;

public class Sim {

    /**
     * @param args the command line arguments
     */
    // Class Sim variables
    public static double clock;
    public static double meanInterArrivalTime;
    public static double meanServiceTime;
    public static double totalArrivalTime;
    public static double lastEventTime;
    public static double totalBusy;
    public static double maxQueueLength;
    public static double sumResponseTime;
    public static long numberOfCustomers;
    public static long queueLength;
    public static long numberInService; // either 0 or 1
    public static long totalCustomers;
    public static long numberOfDepartures;
    public static long longService;
    public final static int arrival = 1;
    public final static int departure = 2;
    public static EventList futureEventList;
    public static Queue customers;
    public static Random stream;
    public static double MaximumWaitTime;
    public static double iat[][] = new double[5][5];
    public static double st[][] = new double[5][5];
    public static double ServiceEndTime;
    public static double SumOfWaitTime;
    public static double SumOfIdealTime;

    public static void intializeTables() {
        int[] timep = new int[]{2, 3, 4, 5, 6};
        double[] iatp = new double[]{0.15, 0.25, 0.20, 0.25, 0.15};
        double[] stp = new double[]{0.10, 0.25, 0.30, 0.20, 0.15};
        for (int i = 0; i < 5; i++) {
            iat[i][0] = timep[i];
            st[i][0] = timep[i];
            iat[i][1] = iatp[i];
            st[i][1] = stp[i];
            if (i == 0) {
                iat[i][2] = iatp[i];
                iat[i][3] = 1;
                iat[i][4] = iat[i][2] * 100;
                st[i][2] = stp[i];
                st[i][3] = 1;
                st[i][4] = st[i][2] * 100;

            } else {
                iat[i][2] = iat[i - 1][2] + iat[i][1];
                iat[i][3] = iat[i - 1][4] + 1;
                iat[i][4] = iat[i][2] * 100;
                st[i][2] = st[i - 1][2] + st[i][1];
                st[i][3] = st[i - 1][4] + 1;
                st[i][4] = st[i][2] * 100;
            }
        }
    }

    public static int checkRandomInIAT(int num) {
        for (int i = 0; i < 5; i++) {
            if (iat[i][3] <= num && num <= iat[i][4]) {
                return (int) iat[i][0];
            }
        }
        return 0;
    }

    public static int checkRandomInST(int num) {
        for (int i = 0; i < 5; i++) {
            if (st[i][3] <= num && num <= st[i][4]) {
                return (int) st[i][0];
            }
        }
        return 0;
    }

    public static void initialization() {
        clock = 0.0;
        queueLength = 0;
        numberInService = 0;
        lastEventTime = 0.0;
        totalBusy = 0;
        maxQueueLength = 0;
        sumResponseTime = 0;
        numberOfDepartures = 0;
        longService = 0;
        totalArrivalTime = 0;
        MaximumWaitTime = 0;
        SumOfWaitTime = 0;
        ServiceEndTime = 0;
        SumOfIdealTime = 0;
        // create first arrival event
        int rn = stream.nextInt(100) + 1;
        Event evt = new Event(arrival, checkRandomInIAT(rn));
        futureEventList.enqueue(evt);
    }

    public static void processArrival(Event evt) {
        customers.enqueue(evt);
        queueLength++;
        // if the server is idle, fetch the event, do statistics
        // and put into service
        System.out.println("Current Arrival: " + evt.getTime() + " " + ServiceEndTime);
        if ((ServiceEndTime - evt.getTime()) > 0) {
            SumOfWaitTime += (ServiceEndTime - evt.getTime());

        } else {
            SumOfIdealTime += (evt.getTime() - ServiceEndTime);
        }

        if (numberInService == 0) {
            scheduleDeparture();
        } else {
            totalBusy += (clock - lastEventTime);
        }
        // server is busy
        // adjust max queue length statistics
        if (maxQueueLength < queueLength) {
            maxQueueLength = queueLength;
        }
        // schedule the next arrival – interarrival time is random between 1 to 4 minutes
        int rn = stream.nextInt(100) + 1;
        int arrivalTime = (int) checkRandomInIAT(rn);
        System.out.println("IAT:" + arrivalTime);
        totalArrivalTime += arrivalTime;
        Event nextArrival = new Event(arrival, clock + arrivalTime);
        futureEventList.enqueue(nextArrival);
        lastEventTime = clock;
    }

    public static void scheduleDeparture() {
        int ServiceTime;
        // service time is random between 1 to 4 minutes
        int rn = stream.nextInt(100) + 1;

        ServiceTime = checkRandomInST(rn);

        System.out.println("ST:" + ServiceTime);
        Event depart = new Event(departure, clock + ServiceTime);

        futureEventList.enqueue(depart);
        numberInService = 1;
        queueLength--;
    }

    public static void processDeparture(Event e) {
        // get the customer description
        Event finished = (Event) customers.dequeue();
        //System.out.println("d " + e.time);
        // measure the response time and add to the sum

        double response = (clock - finished.getTime());

        sumResponseTime += response;
        ServiceEndTime = e.getTime();
        if (MaximumWaitTime <= (ServiceEndTime - finished.getTime())) {
            MaximumWaitTime = (ServiceEndTime - finished.getTime());
        }
        if (response > 2.0) {
            longService++;
        }
        // record long service
        totalBusy += (clock - lastEventTime);
        numberOfDepartures++;
        lastEventTime = clock;
        // if there are customers in the queue then schedule
        // the departure of the next one
        if (queueLength > 0) {
            scheduleDeparture();
        } else {
            numberInService = 0;
        }
    }

    public static void reportGeneration() {
        double rho = totalBusy / clock;
        double avgr = sumResponseTime / totalCustomers;
        double pc4 = ((double) longService) / totalCustomers;
        System.out.println("totalArrivalTime" + totalArrivalTime);
        meanInterArrivalTime = totalArrivalTime / totalCustomers;
        System.out.println("SINGLE SERVER QUEUE SIMULATION ‐ DATABASE REQUEST ");
        System.out.println("\tMEAN INTERARRIVAL TIME "
                + meanInterArrivalTime + " minutes");
        System.out.println("\tNUMBER OF CUSTOMERS SERVED " + totalCustomers);
        System.out.println();
        System.out.println("\tAVERAGE WAIT TIME " + SumOfWaitTime / totalCustomers + " minutes");
        System.out.println("\tSERVER UTILIZATION TIME " + rho + " minutes");
        System.out.println("\tAVERAGE SERVICE TIME " + (avgr) + " minutes");
        System.out.println("\tAVERAGE IDEAL TIME " + (SumOfIdealTime / totalCustomers) + " minutes");
        System.out.println("\tMAXIMUM A REQUEST SPENT IN THE SYSTEM " + MaximumWaitTime + " minutes");
        System.out.println("\tMAXIMUM LINE LENGTH " + maxQueueLength);
        System.out.println("\tPROPORTION WHO SPEND TWO ");
        System.out.println("\t MINUTES OR MORE IN SYSTEM " + pc4 + " minutes");
        System.out.println("\tSIMULATION RUNLENGTH " + clock + " minutes");
        System.out.println("\tNUMBER OF DEPARTURES " + totalCustomers);
    }

    public static void main(String argv[]) {
        totalCustomers = 0;
        // Set the seed as current system time, so that a different sequence of random numbers is obtained
        // each time
        long seed = System.currentTimeMillis();
        // initialize rng stream
        stream = new Random(seed);
        futureEventList = new EventList();
        customers = new Queue();
        int count = 0;
        initialization();
        intializeTables();
// Loop until 240 minutes have passed
        while (clock < 240) {
            Event evt = (Event) futureEventList.getMin();
            // get imminent event
            futureEventList.dequeue();
            // be rid of it
            clock = evt.getTime();
            // advance simulation time
            if (evt.getType() == arrival) {
                processArrival(evt);
            } else {
                processDeparture(evt);
            }
            totalCustomers++;
        }
        reportGeneration();
    }

}
