package Functionality;

import Main.Parser;
import Main.RobTarget;
import Main.Variable;

import java.io.IOException;



public class ManageVariable {
    public static void mainfunction(Parser p,String lbl, Integer lineNumber) throws IOException {
        if (lbl.contains("robtarget")) {
            RobTarget r = new RobTarget();
            r.setDataType("robtarget");
            r.setDeclaredLoc(lineNumber);
            if (lbl.startsWith("PERS")) {
                r.setGlobal();
            }
            String robvar = r.extractVariable(lbl);
            //assigning value during declaration
            if (lbl.contains(":=")) {
                //const value assignment
                if (lbl.contains("[")) {
                    r.assignValue(lbl);
                }
                else    //assignment using existing variables
                {
                    r.extractRobTargetOperands(p,lbl, lineNumber);
                }
            }
            r.setName(robvar);
            if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
            {
                p.procMap.get(p.currentProcName).addWriteRobTargetVar(robvar);
            }
            else{
                r.addWriteLocation(lineNumber);
            }
            p.robTargetMap.put(robvar, r);
//            System.out.println(r);
        }
        else {
            Variable v = new Variable();
            v.setDeclaredLoc(lineNumber);

            if (lbl.startsWith("PERS")) {
                v.setGlobal();

                // tasks types lists have to be global
                if (lbl.contains("tasks")) {
                    String taskvar = manageTasks(lbl);
                    v.setDataType("tasks");
                    v.setName(taskvar);
                    if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                    {
                        p.procMap.get(p.currentProcName).addWriteVars(taskvar);
                    }
                    else{
                        v.addWriteLocation(lineNumber);
                    }
                    p.varMap.put(taskvar, v);
                    return;
                }
            }

            // num
            if (lbl.contains("num")) {
                v.setDataType("num");
            }

            // bool
            else if (lbl.contains("bool")) {
                v.setDataType("bool");
            }

            // string
            else if (lbl.contains("string")) {
                v.setDataType("string");
            }

            //syncident
            else if (lbl.contains("syncident")) {
                v.setDataType("syncident");
            }

            String var = v.extractVariable(lbl);
            v.extractVarOperands(p,lbl, lineNumber);
            v.setName(var);
            if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
            {
                p.procMap.get(p.currentProcName).addWriteVars(var);
            }
            else{
                v.addWriteLocation(lineNumber);
            }
            p.varMap.put(var, v);
//            System.out.println(v);
        }
    }


    private static String manageTasks(String lbl) {
        String[] tokens = lbl.split(":=|;");
        String[] left = tokens[0].split(" ");
        String var = "";
        for (int i = 2; i < left.length; i++) {
            var += left[i];
        }
        String[] words = var.split("[{]|[}]");
        return words[0];
    }
}