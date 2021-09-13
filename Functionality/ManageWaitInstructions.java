package Functionality;

import Main.Parser;
import Main.Variable;
import Main.WaitInstruction;

public class ManageWaitInstructions {

    public static void mainfunction(Parser p, String lbl, Integer lineNumber) {
        String[] tokens = lbl.split(" |;|,|=|AND|OR|[(]|[)]");
        String instruction = tokens[0].replaceAll("\\s", "");

        WaitInstruction w = new WaitInstruction();
        w.setInstructionName(instruction);
        w.setLineNumber(lineNumber);

        switch (instruction) {
            case "WaitSyncTask":
                process1(p, w, lineNumber, tokens, instruction);
                break;
            case "SyncMoveOn":
                process1(p, w, lineNumber, tokens, instruction);
                break;
            case "WaitTime": {
                for (int i = 1; i < tokens.length; i++) {
                    if (!tokens[i].equals("")) {
                        Float fval = Float.parseFloat(tokens[i]);
                        w.setWaitTime(fval);
                        break;
                    }
                }
            }
            break;
            case "WaitUntil":
                process2(p, w, lineNumber, tokens, instruction);
                break;
            case "SyncMoveOff":
                process2(p, w, lineNumber, tokens, instruction);
                break;
            case "WaitUntil_TestAndSet": {
                int count = 0;
                for (int i = 1; i < tokens.length; i++) {
                    if (!tokens[i].equals("")) {
                        if (count % 2 == 0) {
                            if (p.varMap.containsKey(tokens[i])) {
                                Variable v = p.varMap.get(tokens[i]);
                                if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                                {
                                    p.procMap.get(p.currentProcName).addReadVar(tokens[i]);
                                }
                                else{
                                    v.addReadLocation(lineNumber);
                                }
                                w.addVariable(p.varMap.get(tokens[i]));
                            }
                        }
                        count++;
                    }
                }
            }
        }
        p.waitInstructionsList.add(w);
    }

    private static void process1(Parser p, WaitInstruction w, Integer lineNumber, String[] tokens, String instruction) {
        if (instruction.equals("SyncMoveOn")) {
            p.syncMoveOn = true;
            p.syncMoveOff = false;
        }
        int count = 0;
        for (int i = 1; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                if (count < 2) {
                    if (p.varMap.containsKey(tokens[i])) {
                        if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN")){
                            p.procMap.get(p.currentProcName).addReadVar(tokens[i]);
                        }
                        else
                        {
                            p.varMap.get(tokens[i]).addReadLocation(lineNumber);
                        }

                        w.addVariable(p.varMap.get(tokens[i]));
                    }
                }
                count++;
            }
        }
    }

    private static void process2(Parser p, WaitInstruction w, Integer lineNumber, String[] tokens, String instruction) {
        if (instruction.equals("SyncMoveOff")) {
            p.syncMoveOn = false;
            p.syncMoveOff = true;
        }

        for (int i = 1; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                if (p.varMap.containsKey(tokens[i])) {
                    Variable v = p.varMap.get(tokens[i]);
                    if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                    {
                        p.procMap.get(p.currentProcName).addReadVar(tokens[i]);
                    }
                    else{
                        v.addReadLocation(lineNumber);
                    }
                    w.addVariable(p.varMap.get(tokens[i]));
                    break;
                }
            }
        }
    }
}