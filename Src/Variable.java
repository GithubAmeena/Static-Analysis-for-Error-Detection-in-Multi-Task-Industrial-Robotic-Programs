package Main;

import java.util.ArrayList;


public class Variable {
    private Float value;
    private String name;
    private ArrayList<Integer> readLocations = new ArrayList<>();
    private ArrayList<Integer> writeLocations = new ArrayList<>();
    //    ArrayList<Float> writeValues = new ArrayList<>();
    private String dataType;
    private boolean isGlobal = false;
    private Integer declaredLoc;

    public ArrayList<Integer> getReadLocations() {
        return readLocations;
    }

    public ArrayList<Integer> getWriteLocations() {
        return writeLocations;
    }

    public Integer getDeclaredLoc() {
        return declaredLoc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addReadLocation(Integer lineNumber) {
        readLocations.add(lineNumber);
    }

    public void addWriteLocation(Integer lineNumber) {
        writeLocations.add(lineNumber);
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setGlobal() {
        isGlobal = true;
    }

    public boolean isGlobal(){
        return isGlobal;
    }
    public void setDeclaredLoc(Integer declaredLoc) {
        this.declaredLoc = declaredLoc;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String scope = (isGlobal) ? "Global" : "Local";
        return '{' + name + "," + readLocations + "," + writeLocations + "," + dataType + "," + scope + "," + declaredLoc + '}';
    }

    public String extractVariable(String expr) {
        String[] tokens = expr.split(":=|;");
        String[] left = tokens[0].split(" ");
        String var = "";
        for (int i = 2; i < left.length; i++) {
            var += left[i];
        }
        return var;
    }

    public void extractVarOperands(Parser p,String expr, Integer lineNumber) {
        String[] tokens = expr.split(":=|;");
        String rightSide = "";
        for (int i = 1; i < tokens.length; i++) {
            rightSide += tokens[i];
        }
        rightSide = rightSide.replaceAll("\\s", "");
        String[] operands = rightSide.split("[+]|[*]|[/]|[-]|[(]|[)]|[|]|,");
        for (String s : operands) {
//            System.out.println('[' + s + ']');
            if (p.varMap.containsKey(s)) {
                if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                {
                    p.procMap.get(p.currentProcName).addReadVar(s);
                }
                else{
                    p.varMap.get(s).addReadLocation(lineNumber);
                }
            }
        }
    }
}
