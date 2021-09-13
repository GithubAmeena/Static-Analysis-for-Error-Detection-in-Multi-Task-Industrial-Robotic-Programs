package Main;

import java.util.ArrayList;

public class Procedure {
    private String name;
    private ArrayList<Integer> usedLocations = new ArrayList<>();
    private Integer startDefLoc;
    private Integer endDefLoc;
    private ArrayList<String> readVars = new ArrayList<>();
    private ArrayList<String> writeVars = new ArrayList<>();
    private ArrayList<String> readRobtargetvars = new ArrayList<>();
    private ArrayList<String> writeRobtargetvars = new ArrayList<>();

    public void setReadVars(ArrayList<String> readVars) {
        this.readVars = readVars;
    }

    public void setWriteVars(ArrayList<String> writeVars) {
        this.writeVars = writeVars;
    }

    public void setReadRobtargetvars(ArrayList<String> readRobtargetvars) {
        this.readRobtargetvars = readRobtargetvars;
    }

    public void setWriteRobtargetvars(ArrayList<String> writeRobtargetvars) {
        this.writeRobtargetvars = writeRobtargetvars;
    }

    public void addReadVar(String v)
    {
        this.readVars.add(v);
    }

    public void addWriteVars(String  v)
    {
        this.writeVars.add(v);
    }

    public void addReadRobTargetVar(String  r)
    {
        this.readRobtargetvars.add(r);
    }

    public void addWriteRobTargetVar(String r)
    {
        this.writeRobtargetvars.add(r);
    }

    public ArrayList<Integer> getUsedLocations() {
        return usedLocations;
    }

    public ArrayList<String> getReadVars() {
        return readVars;
    }

    public ArrayList<String> getWriteVars() {
        return writeVars;
    }

    public ArrayList<String> getReadRobtargetvars() {
        return readRobtargetvars;
    }

    public ArrayList<String> getWriteRobtargetvars() {
        return writeRobtargetvars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addUsedLoc(Integer loc) {
        this.usedLocations.add(loc);
    }

    public Integer getStartDefLoc() {
        return startDefLoc;
    }

    public void setStartDefLoc(Integer startDefLoc) {
        this.startDefLoc = startDefLoc;
    }

    public Integer getEndDefLoc() {
        return endDefLoc;
    }

    public void setEndDefLoc(Integer endDefLoc) {
        this.endDefLoc = endDefLoc;
    }

    @Override
    public String toString() {
        return "Procedure{" +
                "name='" + name + '\'' +
                ", usedLocations=" + usedLocations +
                ", startDefLoc=" + startDefLoc +
                ", endDefLoc=" + endDefLoc +
                ", readVars=" + readVars +
                ", writeVars=" + writeVars +
                ", readRobtargetvars=" + readRobtargetvars +
                ", writeRobtargetvars=" + writeRobtargetvars +
                '}';
    }
}
