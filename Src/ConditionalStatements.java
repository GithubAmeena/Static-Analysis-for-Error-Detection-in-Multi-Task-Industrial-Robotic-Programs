package Main;

import java.util.ArrayList;


public class ConditionalStatements {
	private String type, condition;
	private ArrayList<String> s = new ArrayList<String>();
	private ArrayList<Integer>  enter = new ArrayList<Integer>();
	private ArrayList<Integer>  exit = new ArrayList<Integer>();
	private ArrayList<Integer> branchings = new ArrayList<Integer>();

	public ConditionalStatements(String t, int e) {
		type = t;
		enter.add(e);
	}

	ConditionalStatements(ConditionalStatements another) {
		this.type = another.type;
		this.enter = another.enter;
	}

	public void addType(String s) {
		type = s;
	}

	public void addNew(String str) {
		s.add(str);
	}

	public void addEnter(Integer e) {
		enter.add(e);
	}

	public void addExit(Integer e) {
		exit.add(e);
	}

	public void addBranch(int line) {
		branchings.add(line);
	}

	public void addCondition(String c) {
		condition = c;
	}

	@Override
    public String toString() {
        return "{" +
                "type = " + type +
                ", nested types = " + s + 
                ", enter = " + enter +
                ", exit = '" + exit + '\'' +
                ", condition = " + condition + 
                '}';
    }
}

