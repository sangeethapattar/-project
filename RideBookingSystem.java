// RideBookingSystem.java
// Interactive Ride Booking History System
// User can add, delete, view, search and reverse rides through a menu

import java.util.Scanner;

// ============================================================
//  Ride — holds details of one booking
// ============================================================
class Ride {

    int    rideId;
    String pickup;
    String drop;
    double fare;

    public Ride(int rideId, String pickup, String drop, double fare) {
        this.rideId  = rideId;
        this.pickup  = pickup;
        this.drop    = drop;
        this.fare    = fare;
    }

    @Override
    public String toString() {
        return "[ID: " + rideId
                + " | From: " + pickup
                + " -> To: "  + drop
                + " | Fare: Rs." + fare + "]";
    }
}


// ============================================================
//  Node — one element in the linked list
// ============================================================
class Node {

    Ride ride;
    Node next;

    public Node(Ride ride) {
        this.ride = ride;
        this.next = null;
    }
}


// ============================================================
//  RideHistory — linked list with all operations
// ============================================================
class RideHistory {

    private Node head;
    private int  rideCounter;   // auto-generates ride IDs

    public RideHistory() {
        head        = null;
        rideCounter = 1;
    }

    // adds a new ride at the end
    public void addRide(String pickup, String drop, double fare) {
        Ride r       = new Ride(rideCounter++, pickup, drop, fare);
        Node newNode = new Node(r);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
        System.out.println("\n  Ride booked successfully! -> " + r);
    }

    // removes the last ride
    public void deleteLastRide() {
        if (head == null) {
            System.out.println("\n  No rides to cancel. History is empty.");
            return;
        }

        if (head.next == null) {
            System.out.println("\n  Cancelled ride: " + head.ride);
            head = null;
            return;
        }

        Node temp = head;
        while (temp.next.next != null) {
            temp = temp.next;
        }
        System.out.println("\n  Cancelled last ride: " + temp.next.ride);
        temp.next = null;
    }

    // prints all rides
    public void displayRides() {
        if (head == null) {
            System.out.println("\n  No rides in history yet.");
            return;
        }

        System.out.println("\n  ========== Your Ride History ==========");
        Node temp  = head;
        int  count = 1;
        while (temp != null) {
            System.out.println("  " + count + ". " + temp.ride);
            temp = temp.next;
            count++;
        }
        System.out.println("  ========================================");
    }

    // searches by pickup or drop location
    public void searchRide(String location) {
        if (head == null) {
            System.out.println("\n  History is empty. Nothing to search.");
            return;
        }

        System.out.println("\n  Results for location: \"" + location + "\"");
        boolean found = false;
        Node    temp  = head;

        while (temp != null) {
            if (temp.ride.pickup.equalsIgnoreCase(location)
                    || temp.ride.drop.equalsIgnoreCase(location)) {
                System.out.println("  -> " + temp.ride);
                found = true;
            }
            temp = temp.next;
        }

        if (!found) {
            System.out.println("  No rides found for \"" + location + "\".");
        }
    }

    // reverses the list so latest ride shows first
    public void reverseHistory() {
        if (head == null || head.next == null) {
            System.out.println("\n  Not enough rides to reverse.");
            return;
        }

        Node prev    = null;
        Node current = head;
        Node nextNode;

        while (current != null) {
            nextNode     = current.next;
            current.next = prev;
            prev         = current;
            current      = nextNode;
        }

        head = prev;
        System.out.println("\n  History reversed! Latest ride is now at the top.");
    }
}


// ============================================================
//  Main — interactive menu loop
// ============================================================
public class RideBookingSystem {

    public static void main(String[] args) {

        Scanner     sc      = new Scanner(System.in);
        RideHistory history = new RideHistory();
        int         choice  = 0;


        System.out.println("║     Ride Booking History System      ║");


        while (choice != 6) {

            System.out.println("\n  ---- MENU ----");
            System.out.println("  1. Book a new ride");
            System.out.println("  2. Cancel last ride");
            System.out.println("  3. View all rides");
            System.out.println("  4. Search ride by location");
            System.out.println("  5. Reverse ride history");
            System.out.println("  6. Exit");
            System.out.print("\n  Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("  Invalid input. Please enter a number between 1 and 6.");
                sc.next();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("\n  Enter pickup location : ");
                    String pickup = sc.nextLine().trim();

                    System.out.print("  Enter drop location   : ");
                    String drop = sc.nextLine().trim();

                    System.out.print("  Enter fare (Rs.)      : ");

                    double fare = 0;
                    if (sc.hasNextDouble()) {
                        fare = sc.nextDouble();
                        sc.nextLine();
                    } else {
                        System.out.println("  Invalid fare. Ride not added.");
                        sc.nextLine();
                        break;
                    }

                    if (pickup.isEmpty() || drop.isEmpty()) {
                        System.out.println("  Pickup and drop locations cannot be empty.");
                        break;
                    }
                    if (fare <= 0) {
                        System.out.println("  Fare must be greater than 0.");
                        break;
                    }

                    history.addRide(pickup, drop, fare);
                    break;

                case 2:
                    history.deleteLastRide();
                    break;

                case 3:
                    history.displayRides();
                    break;

                case 4:
                    System.out.print("\n  Enter location to search: ");
                    String location = sc.nextLine().trim();
                    if (location.isEmpty()) {
                        System.out.println("  Location cannot be empty.");
                        break;
                    }
                    history.searchRide(location);
                    break;

                case 5:
                    history.reverseHistory();
                    break;

                case 6:
                    System.out.println("\n  Thank you for using the Ride Booking System. Goodbye!");
                    break;

                default:
                    System.out.println("  Invalid choice. Please enter a number between 1 and 6.");
            }
        }

        sc.close();
    }
}