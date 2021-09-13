package Main;


public class RobTarget extends Variable {
    private Float x, y, z;
    private Float q1, q2, q3, q4;
    private Float cf1, cf4, cf6, cfx;

    public RobTarget() {
        x = 0f;
        y = 0f;
        z = 0f;
        q1 = 0f;
        q2 = 0f;
        q3 = 0f;
        q4 = 0f;
        cf1 = 0f;
        cf4 = 0f;
        cf6 = 0f;
        cfx = 0f;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public Float getQ1() {
        return q1;
    }

    public void setQ1(Float q1) {
        this.q1 = q1;
    }

    public Float getQ2() {
        return q2;
    }

    public void setQ2(Float q2) {
        this.q2 = q2;
    }

    public Float getQ3() {
        return q3;
    }

    public void setQ3(Float q3) {
        this.q3 = q3;
    }

    public Float getQ4() {
        return q4;
    }

    public void setQ4(Float q4) {
        this.q4 = q4;
    }

    public Float getCf1() {
        return cf1;
    }

    public void setCf1(Float cf1) {
        this.cf1 = cf1;
    }

    public Float getCf4() {
        return cf4;
    }

    public void setCf4(Float cf4) {
        this.cf4 = cf4;
    }

    public Float getCf6() {
        return cf6;
    }

    public void setCf6(Float cf6) {
        this.cf6 = cf6;
    }

    public Float getCfx() {
        return cfx;
    }

    public void setCfx(Float cfx) {
        this.cfx = cfx;
    }

    public void assignValue(String lbl) {
        String[] tokens = lbl.split(":=|;");

        String rightSide = "";
        for (int i = 1; i < tokens.length; i++) {
            rightSide += tokens[i];
        }
//        System.out.println(rightSide);
        rightSide = rightSide.replaceAll("\\s", "");
        String[] t = rightSide.split("\\[|\\]|,| ");
        int count = 0;
        for (String v : t) {
            if (!v.equals("")) {
//                System.out.println('[' + v + ']');
                float val = Float.parseFloat(v);
                switch (count) {

                    case 0 : x = val;
                    case 1 : y = val;
                    case 2 : z = val;
                    case 3 : q1 = val;
                    case 4 : q2 = val;
                    case 5 : q3 = val;
                    case 6 : q4 = val;
                    case 7 : cf1 = val;
                    case 8 : cf4 = val;
                    case 9 : cf6 = val;
                    case 10 : cfx = val;  
                            
                }
                count++;
            }
        }
    }

    public void extractRobTargetOperands(Parser p,String expr, Integer lineNumber) {
        String[] tokens = expr.split(":=|;");
        String rightSide = "";
        for (int i = 1; i < tokens.length; i++) {
            rightSide += tokens[i];
        }
        rightSide = rightSide.replaceAll("\\s", "");
        String[] operands = rightSide.split("[+]|[*]|[/]|[-]|[(]|[)]|[|]|,");
        for (String s : operands) {
//            System.out.println('[' + s + ']');
            if (p.robTargetMap.containsKey(s)) {
                if(!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN"))
                {
                    p.procMap.get(p.currentProcName).addReadRobTargetVar(s);
                }
                else{
                    p.robTargetMap.get(s).addReadLocation(lineNumber);
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + ",[" + x.toString() + ',' + y.toString() + ',' + z.toString() + ']' + ",[" + q1.toString() + ',' + q2.toString() + ',' + q3.toString() + ',' + q4.toString() + ']' + ",[" + cf1.toString() + ',' + cf4.toString() + ',' + cf6.toString() + ',' + cfx.toString() + ']';
    }
}