package Main;

import java.util.ArrayList;

public class WaitInstruction {
    private String instructionName;
    private ArrayList<Variable> vars = new ArrayList<Variable>();
    private Integer lineNumber;
    private Float waitTime;
    private String condition;

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    public void addVariable(Variable v) {
        vars.add(v);
    }

    public void removeVariable(Variable v) {
        vars.remove(v);
    }

    public ArrayList<Variable> getVars() {
        return vars;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Float getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Float waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public String toString() {
        return "WaitInstruction{" +
                "instructionName='" + instructionName + '\'' +
                ", vars=" + vars +
                ", lineNumber=" + lineNumber +
                ", waitTime=" + waitTime +
                ", condition='" + condition + '\'' +
                '}';
    }
}
