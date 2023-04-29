package CheckAllRules;

import Main.Variable;
import Main.Parser;
import Functionality.ManageIEnableIDisable;
import Functionality.ManageSetDOISignalDO;
import Functionality.ManageSetDOWaitDO;
import Functionality.ManageSharedVarRule;
import Functionality.ManageWaitSyncTaskInstructions;
import Functionality.ManageWaitUntil;
import Functionality.ManageWaitUntilTestAndSet;

import java.util.*;
import java.io.*;


public class TaskAnalyzer {
    private static Integer lineNumber = 0;
    private static ArrayList<Parser> parsedTasks = new ArrayList<>();
    public static ArrayList<ArrayList<String>> sharedVariables = new ArrayList<>();
    public static ArrayList<ArrayList<String>> sharedRobTargetVariables = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //  FileReader fr = new FileReader("flex loader.txt");
        FileReader fr = new FileReader("/home/opt1musenpai/Darpan/Meenakshi mam summer project/task analyzer/Rapid-program-parser/src/InputRules/flex loader.txt"); // path to source rapid file
        // FileReader fr = new FileReader("/home/opt1musenpai/Darpan/Practice/task analyzer/Rapid-program-parser/src/InputRules/a.txt"); // path to source rapid file

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
                // System.out.println(p);
                createGlobalVarTable(p);
                parsedTasks.add(p);
                lineNumber = p.lineNumber + 1;
            }
            System.out.println();
        }
        // System.out.println("Shared Vars : " + sharedVariables);
        // System.out.println("Shared RobTarget Vars: " + sharedRobTargetVariables);
        // System.out.println(c3());
        Parser.check_PERS_common();

        System.out.println("VAR syncident list ROB1 :");
        System.out.println(Parser.syncident_VAR_list1);
        System.out.println("VAR syncident list ROB2 :");
        System.out.println(Parser.syncident_VAR_list2);
        System.out.println("VAR syncident list ROB3 :");
        System.out.println(Parser.syncident_VAR_list3);
        System.out.println("VAR syncident list ROB4 :");
        System.out.println(Parser.syncident_VAR_list4);
        System.out.println();
        System.out.println();

        System.out.println("          ***RULE TESTING STARTS***         ");
        Parser.check_wait_sync_at_end();
        Parser.check_sync_move_at_end();
        ManageWaitUntilTestAndSet.main(new String[] { "a", "b" });
        ManageWaitUntil.main(new String[] { "a", "b" });
        ManageSetDOISignalDO.main(new String[] { "a", "b" });
        ManageSetDOWaitDO.main(new String[] { "a", "b" });
        ManageSharedVarRule.main(new String[] { "a", "b" });
        ManageIEnableIDisable.main(new String[] { "a", "b" });
    }

    private static void createGlobalVarTable(Parser p) {
        ArrayList<String> globalVars = new ArrayList<>();
        ArrayList<String> globalRobTargetVars = new ArrayList<>();
        for (String v : p.varMap.keySet()) {
            if (p.varMap.get(v).isGlobal()) {
                globalVars.add(v);
                // System.out.println("______"+v+"______");
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
            for (int n = m + 1; n < parsedTasks.size(); n++) {
                Parser p2 = parsedTasks.get(n);

                // not enough waitSyncTask combinations present to synchronize the 2 tasks
                if (p1.waitInstructionsList.size() != p2.waitInstructionsList.size()) {
                    // System.out.println("Unequal Wait instructions in the 2 tasks");
                    // flag_global_waitsyncTask = true;
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
                    // System.out.println("Accessed Vars1 :" + accessedVars1);

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
                    // System.out.println("Accessed Vars2 :" + accessedVars2);
                    ArrayList<String> args1 = new ArrayList<>();
                    ArrayList<String> args2 = new ArrayList<>();

                    for (Variable v : p1.waitInstructionsList.get(i).getVars()) {
                        args1.add(v.getName());
                    }
                    for (Variable v : p2.waitInstructionsList.get(i).getVars()) {
                        args2.add(v.getName());
                    }

                    // System.out.println(args1);
                    // System.out.println(args2);

                    if (!args1.equals(args2) || !accessedVars1.equals(accessedVars2)) {
                        return false;
                    }
                    // System.out.println("---------------------------------New Wait Instruction----------------------------------------");
                }
                // System.out.println("------------------------New Pair of Tasks with p1 and other task-------------------------------------------------");
            }
            // System.out.println("-------------------------------------p1 is changing now------------------------------------");
        }
        return true;
    }
}


