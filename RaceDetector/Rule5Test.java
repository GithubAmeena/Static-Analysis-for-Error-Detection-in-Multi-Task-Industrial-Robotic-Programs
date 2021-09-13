package RaceDetector;

import Main.Variable;
import Main.Parser;
import Main.SyncInstruction;
import java.util.*;
import java.io.*;


public class Rule5Test {
    private static Integer lineNumber = 0;
    private static ArrayList<Parser> parsedTasks = new ArrayList<>();
    public static ArrayList<ArrayList<String>> sharedVariables = new ArrayList<>();
    public static ArrayList<ArrayList<String>> sharedRobTargetVariables = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader("C:\\Users\\Ameena\\Downloads\\task analyzer1\\Rapid-program-parser\\src\\Examples\\rule5.txt"); // path to source rapid file
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

    private static boolean c3() throws IOException {
        FileReader fr1 = new FileReader("C:\\Users\\Ameena\\Downloads\\task analyzer1\\Rapid-program-parser\\src\\Examples\\rule5.txt");

        BufferedReader br1 = new BufferedReader(fr1);
        String lbl = ""; String lbl2 = ""; String lbl2last = "";

        ArrayList<String> globalVariables = new ArrayList<>();
        ArrayList<String> localVariables = new ArrayList<>();

        int taskCount = 0, mainCount = 0;
        boolean task1 = false, task2 = false, start = false;
        String SetDOVariable = "";
        for (int m = 0; m < parsedTasks.size() - 1; m++) {
            Parser p1 = parsedTasks.get(m);
            ArrayList<SyncInstruction> syncInstructionsList1 = new ArrayList<>();
            ArrayList<SyncInstruction> syncInstructionsList2 = new ArrayList<>();
            for (int n = m + 1; n < parsedTasks.size(); n++) {
                Parser p2 = parsedTasks.get(n);
                System.out.println("Sync instructions in task1 :" + p1.syncInstructionsList);
                System.out.println("Sync instructions in task2 :" + p2.syncInstructionsList);

                // not enough waitSyncTask combinations present to synchronize the 2 tasks
                if (p1.syncInstructionsList.size() != p2.syncInstructionsList.size()) {
                    System.out.println("Unequal Sync instructions in the 2 tasks and tasks are potentially data racy");
                    return false;
                }
                if ((p1.syncInstructionsList.size()==0) &&(p2.syncInstructionsList.size()==0)) {
                    System.out.println("No Sync instructions in any of the tasks and there is a chance of potential data race");
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


                    // System.out.println("---------------------------------New Wait Instruction----------------------------------------");
                }
                while ((lbl = br1.readLine()) != null)  {
                    lineNumber++;
                    lbl = lbl.replaceFirst("^\\s*", "");

                    if (lbl.startsWith("BEGINTASK")) {
                        taskCount++;
                    }

                    if(lbl.startsWith("CONST")) {
                        String[] line = lbl.split("\\s");
                        localVariables.add(line[2]);
                    }


                    if(lbl.startsWith("PROC MAIN()") && taskCount==1)
                    {
                        ArrayList<String> track = new ArrayList<>(localVariables);
                        while((lbl2 = br1.readLine()) != null){
                            lbl2 = lbl2.replaceFirst("^\\s*", "");
                            String[] line = lbl2.split("\\s");
                            String temp = line[0];

                            if(temp.equals("ENDPROC")){
                                String[] line2 = lbl2last.split("\\s");
                                String first = line2[0];
                                if(first.equals("SetDO"))
                                {
                                    task1 = true;
                                    if(line2.length>=1){
                                        SetDOVariable = line2[1];
                                    }
                                    break;
                                }
                                else{
                                    break;
                                }
                            }
                            lbl2last = lbl2;
                        }
                    }
                    start = false;
                    lbl2last = "";

                    if(lbl.startsWith("PROC MAIN()") && taskCount==2)
                    {
                        ArrayList<String> track = new ArrayList<>(localVariables);
                        while((lbl2 = br1.readLine()) != null){
                            lbl2 = lbl2.replaceFirst("^\\s*", "");
                            String[] line = lbl2.split("\\s");
                            String temp = line[0];

                            if(track.size()==localVariables.size())
                            {
                                if(temp!="" && line.length>1 && line[0].equals("WaitDO") && line[1].equals(SetDOVariable))
                                {
                                    task2 = true;
                                }
                                if(track.size()>0) track.remove(0);
                            }
                            else if(line[0].equals("ENDPROC"))
                            {
                                break;
                            }
                            else{
                                if(track.size()>0) track.remove(0);
                            }
                        }
                    }
                }
                System.out.println("localVariables Vars : " + localVariables);
                System.out.println("SetDOVariable "+ SetDOVariable);
                System.out.println("task1 "+task1);
                System.out.println("task2 "+task2);
                if((task1==true)&&(task2==true))
                {
                    System.out.println("Tasks are free from dataraces");
                }
                else
                {
                    System.out.println("There is a chance of potential ddata race among tasks");
                }
                //  System.out.println("------------------------New Pair of Tasks with p1 and other task-------------------------------------------------");
            }
            //  System.out.println("-------------------------------------p1 is changing now------------------------------------");
        }
        return true;
    }
}


