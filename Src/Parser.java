package Main;


import Functionality.*;

import java.io.*;
import java.util.*;


public class Parser {

    public int x = 0;

    // public static String input = "D:\\Rapid-program-parser\\src\\inputFiles\\";

    public HashMap<String, Variable> varMap = new HashMap<String, Variable>();
    public HashMap<String, RobTarget> robTargetMap = new HashMap<>();

    public Stack<ConditionalStatements> stk = new Stack<>();
    public ArrayList<ConditionalStatements> cs = new ArrayList<ConditionalStatements>();

    public boolean syncMoveOn = false;
    public boolean syncMoveOff = false;

    public ArrayList<MoveInstruction> moveInstructionsList = new ArrayList<>();
    public ArrayList<SyncInstruction> syncInstructionsList = new ArrayList<>();
    public ArrayList<WaitInstruction> waitInstructionsList = new ArrayList<>();
    public HashMap<String, Procedure> procMap = new HashMap<>();
    public Integer lineNumber;

    public String taskName, foregroundTaskName;

    public String currentProcName = "";


    public void processTask(BufferedReader br, Integer startLineNumber, Integer taskNumber) throws IOException {

//        FileReader fr = new FileReader("input.txt"); // path to source rapid file

//        BufferedReader br = new BufferedReader(fr);
        String lbl = "";
        lineNumber = startLineNumber;
        while (!(lbl = br.readLine()).startsWith("ENDTASK"))    //read rapid file line by line
        {
            lbl = lbl.replaceFirst("^\\s*", "");
//            System.out.println(lbl);
            lineNumber++;
            // take necessary action depending on type of statement
            process(lbl);
        }

        for(Map.Entry<String,Procedure> p : procMap.entrySet())
        {
//            System.out.println(p.getValue().getReadRobtargetvars());
            p.getValue().setReadVars(new ArrayList<>(new LinkedHashSet<>(p.getValue().getReadVars())));
            p.getValue().setWriteVars(new ArrayList<>(new LinkedHashSet<>(p.getValue().getWriteVars())));
            p.getValue().setReadRobtargetvars(new ArrayList<>(new LinkedHashSet<>(p.getValue().getReadRobtargetvars())));
            p.getValue().setWriteRobtargetvars(new ArrayList<>(new LinkedHashSet<>(p.getValue().getWriteRobtargetvars())));

            for(String v : p.getValue().getReadVars())
            {
                for(Integer u : p.getValue().getUsedLocations())
                {
                    varMap.get(v).addReadLocation(u);
                }
            }
            for(String v : p.getValue().getWriteVars())
            {
                for(Integer u : p.getValue().getUsedLocations())
                {
                    varMap.get(v).addWriteLocation(u);
                }
            }
            for(String r : p.getValue().getReadRobtargetvars())
            {
                for(Integer u : p.getValue().getUsedLocations())
                {
                    robTargetMap.get(r).addReadLocation(u);
                }
            }
            for(String r : p.getValue().getWriteRobtargetvars())
            {
                for(Integer u : p.getValue().getUsedLocations())
                {
                    robTargetMap.get(r).addWriteLocation(u);
                }
            }
        }

        for (String v : varMap.keySet() ) {
            Collections.sort(varMap.get(v).getReadLocations());
            Collections.sort(varMap.get(v).getWriteLocations());
        }
        for (String r : robTargetMap.keySet()){
            Collections.sort(robTargetMap.get(r).getReadLocations());
            Collections.sort(robTargetMap.get(r).getWriteLocations());
        }

        FileOutputStream fos = new FileOutputStream("parser-output-task-" + taskNumber + ".csv", false);

        PrintWriter pw = new PrintWriter(fos);

        for (Map.Entry<String, Variable> entry : varMap.entrySet()) {
            pw.println(entry.getKey() + "->" + entry.getValue());
        }

        for (Map.Entry<String, RobTarget> entry : robTargetMap.entrySet()) {
            pw.println(entry.getKey() + "->" + entry.getValue());
        }

        for (ConditionalStatements c : cs) {
            pw.println(c);
        }

        for (MoveInstruction mv : moveInstructionsList) {
            pw.println(mv);
        }

        for (SyncInstruction s : syncInstructionsList) {
            pw.println(s);
        }

        for (WaitInstruction w : waitInstructionsList) {
            pw.println(w);
        }

        for (Map.Entry<String,Procedure> entry : procMap.entrySet()){
            pw.println(entry.getKey() + "->" + entry.getValue());
        }

        pw.close();
        fos.close();

    }


    public void process(String lbl) throws IOException {

        if (lbl.startsWith("PROC") || lbl.startsWith("ENDPROC")) {
//            System.out.println("before: " + currentProcName);
            ManageProc.mainfunction(this, lbl, lineNumber);
//            System.out.println("after: " + currentProcName);
        }

        else if (lbl.startsWith("VAR") || lbl.startsWith("CONST") || lbl.startsWith("PERS")) {
            ManageVariable.mainfunction(this, lbl, lineNumber);
        }

        // Start Conditional Statements
        else if (lbl.startsWith("FOR") || lbl.startsWith("WHILE") || lbl.startsWith("IF")) {
            ManageConditionalStatements.mainfunction(this, lbl, lineNumber);
            x++;
        }

        // End Conditional Statements
        else if (lbl.startsWith("ENDFOR") || lbl.startsWith("ENDWHILE") || lbl.startsWith("ENDIF")) {
            stk.peek().addExit(lineNumber);
            x--;
            if (x == 0) {
                ConditionalStatements c = stk.pop();
                cs.add(c);
            }
        }

        else if (lbl.startsWith("ELSEIF") || lbl.startsWith("ELSE")) {
            ManageConditionalStatements.mainfunction(this, lbl, lineNumber);
        }

//        else if(lbl.startsWith("TPWrite")) {
//
//        }

        // Move statements
        else if (lbl.startsWith("MoveL") || lbl.startsWith("MoveC") || lbl.startsWith("MoveJ") || lbl.startsWith("MoveAbsJ")) {
            ManageMove.mainfunction(this, lbl, lineNumber);
        }

        else if ( lbl.startsWith("SetDO") || lbl.startsWith("ISignalDO")||lbl.startsWith("IEnable") || lbl.startsWith("IDisable")||lbl.startsWith("ISignalDI") || lbl.startsWith("WaitDO") || lbl.startsWith("WaitDI")) {
            ManageSyncConstructs.mainfunction(this, lbl, lineNumber);
        }

        else if (lbl.startsWith("WaitSyncTask")|| lbl.startsWith("WaitUntil") || lbl.startsWith("WaitTime") || lbl.startsWith("WaitUntil_TestAndSet")) {
            ManageWaitInstructions.mainfunction(this, lbl, lineNumber);
        }

        //Function usage in a statement
        else if (!lbl.contains(":=") && !lbl.startsWith("MODULE") && !lbl.startsWith("ENDMODULE")) {
            ManageProc.mainfunction(this, lbl, lineNumber);
        }

        // Initialization statements
        else {

            String[] words = lbl.split(" |[.x]|[.y]|[.z]|[=]|:|;|>|<");
            String leftSide = words[0].replaceAll("\\s", "");

            if (robTargetMap.containsKey(leftSide)) {
                //robTarget constant initialization
                if (lbl.contains("[")) {
                    robTargetMap.get(leftSide).assignValue(lbl);
                } else {
                    robTargetMap.get(leftSide).extractRobTargetOperands(this, lbl, lineNumber);
                }
                if(!currentProcName.equals("") && !currentProcName.equals("MAIN"))
                {
                    procMap.get(currentProcName).addWriteRobTargetVar(leftSide);
                }
                else{
                    robTargetMap.get(leftSide).addWriteLocation(lineNumber);
                }
            } else if (varMap.containsKey(leftSide)) {
                varMap.get(leftSide).extractVarOperands(this, lbl, lineNumber);
//                System.out.println("Variable name : " + leftSide);
                if(!currentProcName.equals("") && !currentProcName.equals("MAIN"))
                {
                    procMap.get(currentProcName).addWriteVars(leftSide);
                }
                else{
                    varMap.get(leftSide).addWriteLocation(lineNumber);
                }
            }

        }
//        System.out.println(varMap.entrySet());
    }
}