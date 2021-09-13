package Functionality;

import Main.Parser;
import Main.SyncInstruction;
import Main.Variable;

public class ManageSyncConstructs {
    public static void mainfunction(Parser p, String lbl, Integer lineNumber) {
        String[] tokens = lbl.split(" |;|,");
        String instruction = tokens[0].replaceAll("\\s", "");

        SyncInstruction s = new SyncInstruction();
        s.setLineNumber(lineNumber);
        s.setInstructionName(instruction);

        switch (instruction) {
            case "IEnable"  : p.syncInstructionsList.add(s);
                                break;
            case "IDisable" : p.syncInstructionsList.add(s);
                                break;
            case "SetDO" : {
                int count = 0;
                for (int i = 1; i < tokens.length; i++) {
                    if (!tokens[i].equals("")) {
                        if (count == 0) {

                            //update write location if already in varMap, else create new entry in p.varMap
                            if (p.varMap.containsKey(tokens[i])) {
                                if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                                {
                                    p.procMap.get(p.currentProcName).addWriteVars(tokens[i]);
                                }
                                else{
                                    p.varMap.get(tokens[i]).addWriteLocation(lineNumber);
                                }
                            } else {
                                Variable v = new Variable();
                                v.setDataType("Signaldo");
                                v.setGlobal();
                                v.setName(tokens[i]);
                                if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                                {
                                    p.procMap.get(p.currentProcName).addWriteVars(tokens[i]);
                                }
                                else{
                                    v.addWriteLocation(lineNumber);
                                }
                                p.varMap.put(tokens[i], v);
                            }
                            s.setDoName(tokens[i]);
                        } else if (count == 1) {
                            int val = Integer.parseInt(tokens[i]);
                            p.varMap.get(s.getDoName()).setValue((float) val);
                            s.setDoValue(val);
                            p.syncInstructionsList.add(s);
//                            System.out.println(syncInstructionsList.get(0));
                            break;
                        }
                        count++;
                    }

                }
            }
            break;
            case "ISignalDO" : processSignal(p,s,lineNumber,tokens,instruction);
                                break;
            case "ISignalDI" :  processSignal(p,s,lineNumber,tokens,instruction);
                                break;
            case "CONNECT" : {
                int count = 0;
                for (int i = 1; i < tokens.length; i++) {
                    if (!tokens[i].equals("") && !tokens[i].equals("WITH")) {
                        if (count == 0) {
                            s.setInterruptTriggered(tokens[i]);
                        } else if (count == 1) {
                            s.setTrapRoutineName(tokens[i]);
                            p.syncInstructionsList.add(s);
                            break;
                        }
                        count++;
                    }
                }

            }
            break;

            case "WaitDI" : processWait(p,s,lineNumber,tokens,instruction);
                            break;
            case "WaitDO" : processWait(p,s,lineNumber,tokens,instruction);
                            break;
        }
    }

    private static void processWait(Parser p, SyncInstruction s, Integer lineNumber, String[] tokens, String instruction) {
        int count = 0;
        int val = 0;
        boolean flag = instruction.equals("WaitDI");
        String varName = "";
        for (int i = 1; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                if (count == 0) {
                    varName = tokens[i];
                } else if (count == 1) {
                    val = Integer.parseInt(tokens[i]);
                    break;
                }
                count++;
            }
        }

        //update read location if already in p.varMap, else create new entry in p.varMap
        if (p.varMap.containsKey(varName)) {
            if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
            {
                p.procMap.get(p.currentProcName).addReadVar(varName);
            }
            else{
                p.varMap.get(varName).addReadLocation(lineNumber);
            }
        } else {
            Variable v = new Variable();
            v.setGlobal();
            if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
            {
                p.procMap.get(p.currentProcName).addReadVar(varName);
            }
            else{
                v.addReadLocation(lineNumber);
            }
            if (flag) {
                v.setDataType("Signaldi");
            } else {
                v.setDataType("Signaldo");
            }
            v.setName(varName);
            p.varMap.put(varName, v);
        }
        if (flag) {
            s.setDiValue(val);
            s.setDiName(varName);
        } else {
            s.setDoValue(val);
            s.setDoName(varName);
        }
        p.syncInstructionsList.add(s);
    }

    private static void processSignal(Parser p, SyncInstruction s, Integer lineNumber, String[] tokens, String instruction)
    {
        int count = 0;
        int val = 0;
        boolean flag = instruction.equals("ISignalDI");
        String varName = "";
        for (int i = 1; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                if (count == 0) {
                    varName = tokens[i];
                } else if (count == 1) {
                    val = Integer.parseInt(tokens[i]);
                } else if (count == 2) {
                    s.setInterruptTriggered(tokens[i]);
                    break;
                }
                count++;
            }
        }

        //update read location if already in p.varMap, else create new entry in p.varMap
        if (p.varMap.containsKey(varName)) {
            if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
            {
                p.procMap.get(p.currentProcName).addReadVar(varName);
            }
            else{
                p.varMap.get(varName).addReadLocation(lineNumber);
            }
        } else {
            Variable v = new Variable();
            v.setGlobal();
            if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
            {
                p.procMap.get(p.currentProcName).addReadVar(varName);
            }
            else{
                v.addReadLocation(lineNumber);
            }
            if (flag) {
                v.setDataType("Signaldi");
            } else {
                v.setDataType("Signaldo");
            }
            v.setName(varName);
            p.varMap.put(varName, v);
        }
        if (flag) {
            s.setDiValue(val);
            s.setDiName(varName);
        } else {
            s.setDoValue(val);
            s.setDoName(varName);
        }
        p.syncInstructionsList.add(s);
    }
}
