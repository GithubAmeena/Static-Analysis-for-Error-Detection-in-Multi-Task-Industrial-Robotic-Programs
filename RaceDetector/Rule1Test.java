package RaceDetector;

import Main.WaitInstruction;
import Main.Variable;
import Main.Parser;

import java.util.*;
import java.io.*;


public class Rule1Test {
    private static Integer lineNumber = 0;
    private static ArrayList<Parser> parsedTasks = new ArrayList<>();
    public static ArrayList<ArrayList<String>> sharedVariables = new ArrayList<>();
    public static ArrayList<ArrayList<String>> sharedRobTargetVariables = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        FileReader fr = new FileReader("flex loader.txt");
        FileReader fr = new FileReader("C:\\Users\\Ameena\\Downloads\\task analyzer1\\Rapid-program-parser\\src\\Examples\\rule1.txt"); // path to source rapid file
        BufferedReader br = new BufferedReader(fr);
        String lbl = "";
        while ((lbl = br.readLine()) != null) {
            lineNumber++;
            lbl = lbl.replaceFirst("^\\s*", "");
            if (lbl.startsWith("BEGINTASK")) {
                String[] tokens = lbl.replaceAll("\\s", "").split("<|>|,");
                Parser p = new Parser();
                p.taskName = tokens[1];
                p.foregroundTaskName = tokens[2];
                System.out.println(p.taskName);
                p.processTask(br, lineNumber, parsedTasks.size() + 1);
                createGlobalVarTable(p);
                parsedTasks.add(p);
                lineNumber = p.lineNumber + 1;
            }
            System.out.println();
        }
        System.out.println("Shared Vars : " + sharedVariables);
        System.out.println("Shared RobTarget Vars: " + sharedRobTargetVariables);
        System.out.println(c3());
    }

    private static void createGlobalVarTable(Parser p) {
        ArrayList<String> globalVars = new ArrayList<>();
        ArrayList<String> globalRobTargetVars = new ArrayList<>();
        for (String v : p.varMap.keySet()) {
            if (p.varMap.get(v).isGlobal()) {
                globalVars.add(v);
            }
        }
        for (String r : p.robTargetMap.keySet()) {
            if (p.robTargetMap.get(r).isGlobal()) {
                globalRobTargetVars.add(r);
            }
        }
        sharedVariables.add(globalVars);
        sharedRobTargetVariables.add(globalRobTargetVars);
    }

    private static boolean c3() {
        for (int m = 0; m < parsedTasks.size() - 1; m++) {
            Parser p1 = parsedTasks.get(m);
            ArrayList<WaitInstruction> waitInstructionsList1 = new ArrayList<>();
            ArrayList<WaitInstruction> waitInstructionsList2 = new ArrayList<>();
            for (int n = m + 1; n < parsedTasks.size(); n++) {
                Parser p2 = parsedTasks.get(n);
                System.out.println("Wait instructions in task1 :" + p1.waitInstructionsList);
                System.out.println("Wait instructions in task2 :" + p2.waitInstructionsList);


                // not enough waitSyncTask combinations present to synchronize the 2 tasks
                if (p1.waitInstructionsList.size() != p2.waitInstructionsList.size()) {
                    System.out.println("Unequal WaitSyncTask instructions in the 2 tasks and the tasks are potentially data racy");

                    return false;
                }
                if ((p1.waitInstructionsList.size()==0)||  (p2.waitInstructionsList.size()==0)) {
                    System.out.println("No Wait instructions in any of the tasks and there is a chance of potential data race");
                    return false;
                }

                for (int i = 0; i < p1.waitInstructionsList.size(); i++) {
                    ArrayList<String> accessedVars1 = new ArrayList<>();
                    if (i > 0) {
                        int l1 = p1.waitInstructionsList.get(i - 1).getLineNumber();
                        int l2 = p1.waitInstructionsList.get(i).getLineNumber();

                        for (String s : sharedVariables.get(m)) {
                            ArrayList<Integer> readLocs = p1.varMap.get(s).getReadLocations();

                            if (readLocs != null && !readLocs.isEmpty()) {
                                int firstReadLoc = readLocs.get(0);
                                int index = 1;
                                if (firstReadLoc < l2) {
                                    int readLoc = firstReadLoc;
                                    while (index < readLocs.size() && readLoc <= l1) {
                                        readLoc = readLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < readLocs.size() && readLoc < l2) {
                                        accessedVars1.add(s);
                                    }
                                }
                            }
                            ArrayList<Integer> writeLocs = p1.varMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int firstWriteLoc = writeLocs.get(0);
                                int index = 1;
                                if (firstWriteLoc < l2) {
                                    int writeLoc = firstWriteLoc;
                                    while (index < writeLocs.size() && writeLoc <= l1) {
                                        writeLoc = writeLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < writeLocs.size() && writeLoc < l2) {
                                        accessedVars1.add(s);
                                    }
                                }
                            }
                        }
                        for (String s : sharedRobTargetVariables.get(m)) {
                            ArrayList<Integer> readLocs = p1.robTargetMap.get(s).getReadLocations();

                            if (readLocs != null && !readLocs.isEmpty()) {
                                int firstReadLoc = readLocs.get(0);
                                int index = 1;
                                if (firstReadLoc < l2) {
                                    int readLoc = firstReadLoc;
                                    while (index < readLocs.size() && readLoc <= l1) {
                                        readLoc = readLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < readLocs.size() && readLoc < l2) {
                                        accessedVars1.add(s);
                                    }
                                }
                            }
                            ArrayList<Integer> writeLocs = p1.robTargetMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int firstWriteLoc = writeLocs.get(0);
                                int index = 1;
                                if (firstWriteLoc < l2) {
                                    int writeLoc = firstWriteLoc;
                                    while (index < writeLocs.size() && writeLoc <= l1) {
                                        writeLoc = writeLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < writeLocs.size() && writeLoc < l2) {
                                        accessedVars1.add(s);
                                    }
                                }
                            }
                        }
                    } else {
                        int l1 = p1.waitInstructionsList.get(i).getLineNumber();
                        for (String s : sharedVariables.get(m)) {
                            ArrayList<Integer> readLocs = p1.varMap.get(s).getReadLocations();
                            if (readLocs != null && !readLocs.isEmpty()) {
                                int firstReadLoc = readLocs.get(0);
                                if (firstReadLoc < l1) {
                                    accessedVars1.add(s);
                                }
                            }

                            ArrayList<Integer> writeLocs = p1.varMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int firstWriteLoc = writeLocs.get(0);
                                if (firstWriteLoc < l1) {
                                    accessedVars1.add(s);
                                }
                            }
                        }

                        for (String s : sharedRobTargetVariables.get(m)) {
                            ArrayList<Integer> readLocs = p1.robTargetMap.get(s).getReadLocations();
                            if (readLocs != null && !readLocs.isEmpty()) {
                                int firstReadLoc = readLocs.get(0);
                                if (firstReadLoc < l1) {
                                    accessedVars1.add(s);
                                }
                            }

                            ArrayList<Integer> writeLocs = p1.robTargetMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int firstWriteLoc = writeLocs.get(0);
                                if (firstWriteLoc < l1) {
                                    accessedVars1.add(s);
                                }
                            }
                        }

                    }
                    System.out.println("Accessed Vars1 :" + accessedVars1);

                    ArrayList<String> accessedVars2 = new ArrayList<>();
                    if (i < p2.waitInstructionsList.size() - 1) {
                        int l1 = p2.waitInstructionsList.get(i).getLineNumber();
                        int l2 = p2.waitInstructionsList.get(i + 1).getLineNumber();

                        for (String s : sharedVariables.get(n)) {
                            ArrayList<Integer> readLocs = p2.varMap.get(s).getReadLocations();

                            if (readLocs != null && !readLocs.isEmpty()) {
                                int firstReadLoc = readLocs.get(0);
                                int index = 1;
                                if (firstReadLoc < l2) {
                                    int readLoc = firstReadLoc;
                                    while (index < readLocs.size() && readLoc <= l1) {
                                        readLoc = readLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < readLocs.size() && readLoc < l2) {
                                        accessedVars2.add(s);
                                    }
                                }
                            }
                            ArrayList<Integer> writeLocs = p2.varMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int firstWriteLoc = writeLocs.get(0);
                                int index = 1;
                                if (firstWriteLoc < l2) {
                                    int writeLoc = firstWriteLoc;
                                    while (index < writeLocs.size() && writeLoc <= l1) {
                                        writeLoc = writeLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < writeLocs.size() && writeLoc < l2) {
                                        accessedVars2.add(s);
                                    }
                                }
                            }
                        }
                        for (String s : sharedRobTargetVariables.get(n)) {
                            ArrayList<Integer> readLocs = p2.robTargetMap.get(s).getReadLocations();

                            if (readLocs != null && !readLocs.isEmpty()) {
                                int firstReadLoc = readLocs.get(0);
                                int index = 1;
                                if (firstReadLoc < l2) {
                                    int readLoc = firstReadLoc;
                                    while (index < readLocs.size() && readLoc <= l1) {
                                        readLoc = readLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < readLocs.size() && readLoc < l2) {
                                        accessedVars2.add(s);
                                    }
                                }
                            }
                            ArrayList<Integer> writeLocs = p2.robTargetMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int firstWriteLoc = writeLocs.get(0);
                                int index = 1;
                                if (firstWriteLoc < l2) {
                                    int writeLoc = firstWriteLoc;
                                    while (index < writeLocs.size() && writeLoc <= l1) {
                                        writeLoc = writeLocs.get(index);
                                        index += 1;
                                    }
                                    if (index < writeLocs.size() && writeLoc < l2) {
                                        accessedVars2.add(s);
                                    }
                                }
                            }
                        }
                    } else {
                        int l1 = p2.waitInstructionsList.get(i).getLineNumber();
                        for (String s : sharedVariables.get(n)) {
                            ArrayList<Integer> readLocs = p2.varMap.get(s).getReadLocations();
                            if (readLocs != null && !readLocs.isEmpty()) {
                                int LastReadLoc = readLocs.get(readLocs.size() - 1);
                                if (LastReadLoc > l1) {
                                    accessedVars2.add(s);
                                }
                            }

                            ArrayList<Integer> writeLocs = p2.varMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int LastWriteLoc = writeLocs.get(writeLocs.size() - 1);
                                if (LastWriteLoc > l1) {
                                    accessedVars2.add(s);
                                }
                            }
                        }

                        for (String s : sharedRobTargetVariables.get(n)) {
                            ArrayList<Integer> readLocs = p2.robTargetMap.get(s).getReadLocations();
                            if (readLocs != null && !readLocs.isEmpty()) {
                                int LastReadLoc = readLocs.get(readLocs.size() - 1);
                                if (LastReadLoc > l1) {
                                    accessedVars2.add(s);
                                }
                            }

                            ArrayList<Integer> writeLocs = p1.robTargetMap.get(s).getWriteLocations();
                            if (writeLocs != null && !writeLocs.isEmpty()) {
                                int LastWriteLoc = writeLocs.get(writeLocs.size() - 1);
                                if (LastWriteLoc > l1) {
                                    accessedVars2.add(s);
                                }
                            }
                        }

                    }
                    System.out.println("Accessed Vars2 :" + accessedVars2);
                    ArrayList<String> args1 = new ArrayList<>();
                    ArrayList<String> args2 = new ArrayList<>();

                    for (Variable v : p1.waitInstructionsList.get(i).getVars()) {
                        args1.add(v.getName());
                    }
                    for (Variable v : p2.waitInstructionsList.get(i).getVars()) {
                        args2.add(v.getName());
                    }

                    System.out.println(args1);
                    System.out.println(args2);

                    if (args1.equals(args2)) {
                        System.out.println("Tasks are free from data races");
                    }
                  //  System.out.println("---------------------------------New Wait Instruction----------------------------------------");
                }
              //  System.out.println("------------------------New Pair of Tasks with p1 and other task-------------------------------------------------");
            }
           // System.out.println("-------------------------------------p1 is changing now------------------------------------");
        }
        return true;
    }


}


