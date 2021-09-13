package Functionality;


import Main.Parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Main.ConditionalStatements;
import Main.Variable;


public class ManageConditionalStatements {

    public static void mainfunction(Parser p, String lbl, Integer lineNumber) throws IOException {

        Map<Boolean, List<String>> map = Arrays
                .stream(lbl.split(" "))
                .collect(Collectors.groupingBy(e -> e.matches("IF|AND|THEN|OR|WHILE|FOR|ELSE")));

        System.out.println(map);

        List<String> tokens = map.get(false);

        if(tokens != null)
        {
            for (String t : tokens) {
                String[] var = t.split(" |[.x]|[.y]|[=]|:|;|>|<");
//            System.out.println(var[0]);
                for (String s : var) {
//                System.out.println("[" + s + "]");
                    if (!s.equals("")) {
                        if (p.robTargetMap.containsKey(s)) {
                            if (!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN")) {
                                p.procMap.get(p.currentProcName).addReadRobTargetVar(s);
                            } else {
                                p.robTargetMap.get(s).addReadLocation(lineNumber);
                            }
                            break;
                        }
                        if (!p.varMap.containsKey(s)) {
                            Variable v = new Variable();
                            v.setDataType("ioSignal");
                            v.setName(s);
                            if (!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN")) {
                                p.procMap.get(p.currentProcName).addReadVar(s);
                            } else {
                                v.addReadLocation(lineNumber);
                            }
                            v.setGlobal();
                            p.varMap.put(s, v);
                        } else {
                            if (!p.currentProcName.equals("") && !p.currentProcName.equals("MAIN")) {
                                p.procMap.get(p.currentProcName).addReadVar(s);
                            } else {
                                p.varMap.get(s).addReadLocation(lineNumber);
                            }
                        }
                        break;
                    }
                }
            }
        }


        if (lbl.startsWith("FOR")) {
            if (p.stk.empty()) {
                ConditionalStatements c = new ConditionalStatements("FOR", lineNumber);
                p.stk.push(c);
            } else {
                p.stk.peek().addType("NESTED FOR");
                p.stk.peek().addNew("FOR");
                p.stk.peek().addEnter(lineNumber);
            }

            String[] words = lbl.split(" ");
            String condition = "";
            String variable = words[1];

            String start_value = words[3];
            String end_value = words[5];

            condition = start_value + " < " + variable + " < " + end_value;
            p.stk.peek().addCondition(condition);
        } else if (lbl.startsWith("IF")) {
            if (p.stk.empty()) {
                ConditionalStatements c = new ConditionalStatements("IF", lineNumber);
                p.stk.push(c);
            } else {
                p.stk.peek().addType("NESTED IF");
                p.stk.peek().addNew("IF");
                p.stk.peek().addEnter(lineNumber);
            }

            String[] words = lbl.split(" ");
            String condition = "";
            for (int i = 1; i < words.length - 1; i++) {
                condition = condition + words[i] + " ";
            }
            p.stk.peek().addCondition(condition);
        } else if (lbl.startsWith("WHILE")) {
            if (p.stk.empty()) {
                ConditionalStatements c = new ConditionalStatements("WHILE", lineNumber);
                p.stk.push(c);
            } else {
                p.stk.peek().addType("NESTED WHILE");
                p.stk.peek().addNew("WHILE");
                p.stk.peek().addEnter(lineNumber);
            }

            String[] words = lbl.split(" ");
            String condition = "";
            for (int i = 1; i < words.length - 1; i++) {
                condition = condition + words[i] + ' ';
            }

            p.stk.peek().addCondition(condition);
        } else if (lbl.startsWith("ELSEIF")) {
            p.stk.peek().addType("NESTED IF ELSEIF ELSE");
            p.stk.peek().addNew("ELSIF");
            p.stk.peek().addEnter(lineNumber);
        } else if (lbl.startsWith("ELSE")) {
            p.stk.peek().addNew("ELSE");
            p.stk.peek().addEnter(lineNumber);
        }

    }
}
