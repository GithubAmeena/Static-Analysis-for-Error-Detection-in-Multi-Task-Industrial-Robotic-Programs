package Functionality;

import Main.MoveInstruction;
import Main.Parser;


public class ManageMove
{
    public static void mainfunction(Parser p, String lbl, Integer lineNumber)
    {
        String[] tokens = lbl.split(";| |,|\\\\");
        String type = tokens[0].replaceAll("\\s","");
        MoveInstruction m = new MoveInstruction();
        m.setLineNumber(lineNumber);
        m.setName(type);
        if(type.equals("MoveL") || type.equals("MoveJ") || type.equals("MoveAbsJ"))
        {
            switch (type) {
                case "MoveL" : m.setType("Linear");
                case "MoveJ" : m.setType("Joint");
                case "MoveAbsJ" : m.setType("AbsJoint");
            }
            for (int i=1;i < tokens.length;i++)
            {
                if(!tokens[i].equals(""))
                {
                      if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                      {
                          p.procMap.get(p.currentProcName).addReadRobTargetVar(tokens[i]);
                      }
                      else{
                          p.robTargetMap.get(tokens[i]).addReadLocation(lineNumber);
                      }
                      break;
                }
            }
        }
        else if(type.equals("MoveC"))
        {
            m.setType("Circular");
            int count = 0;
            for (int i=1;i < tokens.length;i++)
            {
                if(!tokens[i].equals("") && count < 2)
                {
                    if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                    {
                        p.procMap.get(p.currentProcName).addReadRobTargetVar(tokens[i]);
                    }
                    else{
                        p.robTargetMap.get(tokens[i]).addReadLocation(lineNumber);
                    }
                    count++;
                    if(count==2)
                    {
                        break;
                    }
                }
            }
        }
        if(p.syncMoveOn && !p.syncMoveOff)
        {
            m.setInsideSyncmoveOnOff(true);
        }
        p.moveInstructionsList.add(m);
    }
}
