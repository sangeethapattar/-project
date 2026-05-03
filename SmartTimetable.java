// SmartTimetable.java
// Smart Timetable Generator using Backtracking
// User enters subjects, teachers, rooms and time slots
// System finds valid schedules with zero conflicts

import java.util.Scanner;

// ============================================================
//  Subject — stores info for one subject
// ============================================================
class Subject {

    String name;
    String teacher;
    String room;

    public Subject(String name, String teacher, String room) {
        this.name    = name;
        this.teacher = teacher;
        this.room    = room;
    }

    @Override
    public String toString() {
        return name + " | Teacher: " + teacher + " | Room: " + room;
    }
}


// ============================================================
//  Scheduler — the backtracking engine
// ============================================================
class Scheduler {

    private Subject[] subjects;
    private String[]  slots;
    private int[]     assignment;    // assignment[i] = slot index for subject i
    private int       solutionCount;
    private boolean   stopAfterFirst;

    public Scheduler(Subject[] subjects, String[] slots) {
        this.subjects       = subjects;
        this.slots          = slots;
        this.assignment     = new int[subjects.length];
        this.solutionCount  = 0;
        this.stopAfterFirst = false;

        for (int i = 0; i < assignment.length; i++) {
            assignment[i] = -1;   // -1 means not yet assigned
        }
    }

    // ----------------------------------------------------------
    // isSafe — checks if putting subject[idx] in slot[slotIdx]
    //          conflicts with any previously assigned subject
    // ----------------------------------------------------------
    public boolean isSafe(int idx, int slotIdx) {
        Subject curr = subjects[idx];

        for (int i = 0; i < idx; i++) {
            if (assignment[i] == -1) continue;

            // only check subjects in the SAME slot
            if (assignment[i] == slotIdx) {
                Subject other = subjects[i];

                // same teacher in same slot = conflict
                if (other.teacher.equalsIgnoreCase(curr.teacher)) {
                    return false;
                }

                // same room in same slot = conflict
                if (other.room.equalsIgnoreCase(curr.room)) {
                    return false;
                }
            }
        }
        return true;
    }

    // ----------------------------------------------------------
    // assignSlot — tries each slot for subjects[idx], recurses
    // ----------------------------------------------------------
    public void assignSlot(int idx, boolean findAll) {

        // all subjects assigned — we found a valid timetable
        if (idx == subjects.length) {
            solutionCount++;
            System.out.println("\n  ===== Valid Timetable #" + solutionCount + " =====");
            printTimetable();
            if (!findAll) stopAfterFirst = true;
            return;
        }

        for (int s = 0; s < slots.length; s++) {

            if (stopAfterFirst) return;   // bail early if needed

            if (isSafe(idx, s)) {
                assignment[idx] = s;             // try this slot
                assignSlot(idx + 1, findAll);    // go deeper

                // backtrack — undo if path didn't work out
                if (!stopAfterFirst) {
                    assignment[idx] = -1;
                }
            }
        }
    }

    // ----------------------------------------------------------
    // backtrack — resets state and kicks off the search
    // ----------------------------------------------------------
    public void backtrack(boolean findAll) {
        solutionCount  = 0;
        stopAfterFirst = false;

        for (int i = 0; i < assignment.length; i++) {
            assignment[i] = -1;
        }

        System.out.println("\n  Searching for valid timetable(s)...\n");
        assignSlot(0, findAll);

        if (solutionCount == 0) {
            System.out.println("  No valid timetable found with given constraints.");
            System.out.println("  Tip: Add more time slots or reduce teacher/room overlaps.");
        } else {
            System.out.println("\n  Total valid timetable(s) found: " + solutionCount);
        }
    }

    // ----------------------------------------------------------
    // generateTimetable — entry point called from Main
    // ----------------------------------------------------------
    public void generateTimetable(boolean findAll) {
        if (subjects.length == 0 || slots.length == 0) {
            System.out.println("  Cannot generate: subjects or slots are missing.");
            return;
        }
        backtrack(findAll);
    }

    // ----------------------------------------------------------
    // printTimetable — prints the current valid assignment
    // ----------------------------------------------------------
    private void printTimetable() {
        int col1 = 18, col2 = 20, col3 = 16, col4 = 12;

        String line = "  +" + "-".repeat(col1 + 2)
                + "+" + "-".repeat(col2 + 2)
                + "+" + "-".repeat(col3 + 2)
                + "+" + "-".repeat(col4 + 2) + "+";

        System.out.println(line);
        System.out.printf("  | %-" + col1 + "s | %-" + col2 + "s | %-"
                        + col3 + "s | %-" + col4 + "s |%n",
                "Subject", "Teacher", "Room", "Time Slot");
        System.out.println(line);

        for (int i = 0; i < subjects.length; i++) {
            Subject s    = subjects[i];
            String  slot = (assignment[i] >= 0) ? slots[assignment[i]] : "---";
            System.out.printf("  | %-" + col1 + "s | %-" + col2 + "s | %-"
                            + col3 + "s | %-" + col4 + "s |%n",
                    s.name, s.teacher, s.room, slot);
        }
        System.out.println(line);
    }
}


// ============================================================
//  SmartTimetable — main class with interactive input
// ============================================================
public class SmartTimetable {

    static Scanner sc = new Scanner(System.in);

    // small helper to read a positive integer from the user
    static int readPositiveInt(String prompt) {
        int val = 0;
        while (val <= 0) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                val = sc.nextInt();
                sc.nextLine();
                if (val <= 0) System.out.println("  Please enter a number greater than 0.");
            } else {
                System.out.println("  Invalid input. Enter a whole number.");
                sc.nextLine();
            }
        }
        return val;
    }

    public static void main(String[] args) {


        System.out.println("║       Smart Timetable Generator          ║");
        System.out.println("║    Constraint-based Backtracking         ║");


        // ---- Step 1: collect subjects ----
        int subjectCount = readPositiveInt("\n  How many subjects to schedule? ");
        Subject[] subjects = new Subject[subjectCount];

        System.out.println("\n  Enter details for each subject:");
        for (int i = 0; i < subjectCount; i++) {
            System.out.println("\n  -- Subject " + (i + 1) + " --");

            System.out.print("    Subject name : ");
            String name = sc.nextLine().trim();

            System.out.print("    Teacher name : ");
            String teacher = sc.nextLine().trim();

            System.out.print("    Room         : ");
            String room = sc.nextLine().trim();

            // fallback defaults if user leaves blank
            subjects[i] = new Subject(
                    name.isEmpty()    ? "Subject"  + (i + 1) : name,
                    teacher.isEmpty() ? "Teacher"  + (i + 1) : teacher,
                    room.isEmpty()    ? "Room"     + (i + 1) : room
            );
        }

        // ---- Step 2: collect time slots ----
        int slotCount = readPositiveInt("\n  How many time slots are available? ");
        String[] slots = new String[slotCount];

        System.out.println("\n  Enter time slot labels  (e.g. Mon 9-10AM, Tue 11AM, etc.):");
        for (int i = 0; i < slotCount; i++) {
            System.out.print("    Slot " + (i + 1) + " : ");
            String input = sc.nextLine().trim();
            slots[i] = input.isEmpty() ? "Slot-" + (i + 1) : input;
        }

        if (slotCount < subjectCount) {
            System.out.println("\n  Warning: fewer slots than subjects.");
            System.out.println("  Some subjects may share a slot, which could cause conflicts.");
        }

        // ---- Step 3: choose generation mode ----
        System.out.println("\n  What do you want to generate?");
        System.out.println("  1. First valid timetable only (faster)");
        System.out.println("  2. All possible valid timetables");
        System.out.print("\n  Enter choice (1 or 2): ");

        boolean findAll = false;
        if (sc.hasNextInt()) {
            findAll = (sc.nextInt() == 2);
            sc.nextLine();
        } else {
            sc.nextLine();
            System.out.println("  Defaulting to first valid timetable.");
        }

        // ---- Step 4: run the scheduler ----
        Scheduler gen = new Scheduler(subjects, slots);
        gen.generateTimetable(findAll);

        sc.close();
    }
}