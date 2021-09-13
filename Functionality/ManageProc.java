package Functionality;

import Main.Parser;
import Main.Procedure;

public class ManageProc {

//    private static String currentProcName;

    public static void mainfunction(Parser p, String lbl, Integer lineNumber) {
        if (lbl.startsWith("ENDPROC")) {
            p.procMap.get(p.currentProcName).setEndDefLoc(lineNumber);
//            System.out.println(currentProcName);
            p.currentProcName = "";
        } else if (lbl.startsWith("PROC")) {
            String[] tokens = lbl.split(" |[)]|[(]|,");
            for (int i = 1; i < tokens.length; i++) {
                if (!tokens[i].equals("")) {
                    //func definition start and end location,
                    String procName = tokens[i];
                    p.currentProcName = procName;
                    if (p.procMap.containsKey(procName)) {
                        p.procMap.get(procName).setStartDefLoc(lineNumber);
                    } else {
                        Procedure proc = new Procedure();
                        proc.setName(procName);
                        proc.setStartDefLoc(lineNumber);
                        p.procMap.put(procName, proc);
                    }
                    break;
                }
            }
        }

        //Function usage in a statement
        else {
            String[] tokens = lbl.split(";| ");
            String procName = tokens[0];
            if (!p.procMap.containsKey(procName)) {
                Procedure proc = new Procedure();
                proc.setName(procName);
                proc.addUsedLoc(lineNumber);
                p.procMap.put(procName, proc);
            } else {
                p.procMap.get(procName).addUsedLoc(lineNumber);
            }
        }
    }
}
