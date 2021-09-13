package Main;

public class MoveInstruction
{
    private Integer lineNumber;
    private String type;
    private String name;
    private boolean insideSyncmoveOnOff = false;
    private boolean partOfWaitSyncTask = false;
    private String id ;


    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInsideSyncmoveOnOff(boolean insideSyncmoveOnOff) {
        this.insideSyncmoveOnOff = insideSyncmoveOnOff;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getType() {
        return type;
    }

    public boolean isInsideSyncmoveOnOff() {
        return insideSyncmoveOnOff;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPartOfWaitSyncTask() {
        return partOfWaitSyncTask;
    }

    public void setPartOfWaitSyncTask(boolean partOfWaitSyncTask) {
        this.partOfWaitSyncTask = partOfWaitSyncTask;
    }

    @Override
    public String toString() {
        return name + ',' + type + ',' + lineNumber + ',' + insideSyncmoveOnOff + ',' + partOfWaitSyncTask + ',' + id;
    }
}
