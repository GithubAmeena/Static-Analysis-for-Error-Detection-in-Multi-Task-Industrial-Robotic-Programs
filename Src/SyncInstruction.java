package Main;


public class SyncInstruction {
    private String instructionName;
    private Integer lineNumber;
    private String diName;
    private String doName;
    private Integer diValue;
    private Integer doValue;
    private String InterruptTriggered;
    private String trapRoutineName;

    public String getTrapRoutineName() {
        return trapRoutineName;
    }

    public void setTrapRoutineName(String trapRoutineName) {
        this.trapRoutineName = trapRoutineName;
    }

    @Override
    public String toString() {
        return "SyncInstruction{" +
                "instructionName='" + instructionName + '\'' +
                ", lineNumber=" + lineNumber +
                ", diName='" + diName + '\'' +
                ", doName='" + doName + '\'' +
                ", diValue=" + diValue +
                ", doValue=" + doValue +
                ", InterruptTriggered='" + InterruptTriggered + '\'' +
                ", trapRoutineName='" + trapRoutineName + '\'' +
                '}';
    }

    public Integer getDiValue() {
        return diValue;
    }

    public void setDiValue(Integer diValue) {
        this.diValue = diValue;
    }

    public Integer getDoValue() {
        return doValue;
    }

    public void setDoValue(Integer doValue) {
        this.doValue = doValue;
    }

    public String getInterruptTriggered() {
        return InterruptTriggered;
    }

    public void setInterruptTriggered(String interruptTriggered) {
        InterruptTriggered = interruptTriggered;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDiName() {
        return diName;
    }

    public void setDiName(String diName) {
        this.diName = diName;
    }

    public String getDoName() {
        return doName;
    }

    public void setDoName(String doName) {
        this.doName = doName;
    }
}
