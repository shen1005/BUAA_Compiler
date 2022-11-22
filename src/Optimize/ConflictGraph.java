package Optimize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ConflictGraph {
    private final ArrayList<ActiveVar> activeVarList = new ArrayList<>();
    private final ArrayList<ActiveVar> removedVarList = new ArrayList<>();
    private final ArrayList<ActiveVar> deletedVarList = new ArrayList<>();
    private final HashMap<String, String> name2Reg = new HashMap<>();
    private final ArrayList<String> regs = new ArrayList<>();
    private final HashMap<String, String> saveRegs = new HashMap<>();
    private boolean isTLE = false;


    public ConflictGraph() {
        //只保留t0-t3, $gp, $sp, $ra, $v0, $a0, $0, $at
        for (int i = 4; i < 10; i++) {
            regs.add("$t" + i);
        }
        for (int i = 0; i < 8; i++) {
            regs.add("$s" + i);
        }
        regs.add("$v1");
        regs.add("$a1");
        regs.add("$a2");
        regs.add("$a3");
        regs.add("$fp");
        regs.add("$k0");
        regs.add("$k1");
    }

    public void graphColoring() {
        int varNum = activeVarList.size();
        while (removedVarList.size() + deletedVarList.size() != varNum) {
            boolean isRemoved = false;
//            for (ActiveVar activeVar : activeVarList) {
//                //TODO 迭代器？
//
//                if (activeVar.getSideNum() < regs.size()) {
//                    isRemoved = true;
//                    activeVar.remove();
//                    removedVarList.add(activeVar);
//                    activeVarList.remove(activeVar);
//                    break;
//                }
//            }
            Iterator<ActiveVar> iterator = activeVarList.iterator();
            while (iterator.hasNext()) {
                ActiveVar activeVar = iterator.next();
                if (activeVar.getSideNum() < regs.size()) {
                    isRemoved = true;
                    activeVar.remove();
                    removedVarList.add(activeVar);
                    iterator.remove();
                    break;
                }
            }
            if (!isRemoved) {
                ActiveVar maxSideVar = activeVarList.get(0);
                for (ActiveVar activeVar : activeVarList) {
                    if (activeVar.getSideNum() > maxSideVar.getSideNum()) {
                        maxSideVar = activeVar;
                    }
                }
                maxSideVar.delete();
                deletedVarList.add(maxSideVar);
                activeVarList.remove(maxSideVar);
            }
        }
        for (ActiveVar activeVar : removedVarList) {
            HashSet<String> conflictRegs = activeVar.getConflictRegs();
            for (String reg : regs) {
                if (!conflictRegs.contains(reg)) {
                    activeVar.setReg(reg);
                    name2Reg.put(activeVar.getName(), reg);
                    break;
                }
            }
        }
        //TODO函数的栈
//        for (String var : name2Reg.keySet()) {
//            String reg = name2Reg.get(var);
//            if (!saveRegs.values().contains(reg)) {
//                saveRegs.put(var, reg);
//            }
//        }
        Iterator<String> iterator = name2Reg.keySet().iterator();
        while (iterator.hasNext()) {
            String var = iterator.next();
            String reg = name2Reg.get(var);
            if (!saveRegs.values().contains(reg)) {
                saveRegs.put(var, reg);
            }
        }
    }

    public void addActiveVarSet(HashSet<String> activeVarSet) {
        ArrayList<ActiveVar> temp = new ArrayList<>();
        for (String name : activeVarSet) {
            ActiveVar activeVar = new ActiveVar(name);
            temp.add(getRealActiveVar(activeVar));
        }
        for (int i = 0; i < temp.size() - 1; i++) {
            for (int j = i + 1; j < temp.size(); j++) {
                if (!temp.get(i).addConflictVar(temp.get(j))) {
                    isTLE = true;
                    return;
                };
            }
        }
        for (ActiveVar activeVar : temp) {
            if (!activeVarList.contains(activeVar)) {
                activeVarList.add(activeVar);
            }
        }

    }


    public ActiveVar getRealActiveVar(ActiveVar activeVar) {
        for (ActiveVar var : activeVarList) {
            if (var.getName().equals(activeVar.getName())) {
                return var;
            }
        }
        //activeVarList.add(activeVar);
        return activeVar;
    }

    public boolean isHaveReg(String name) {
        return name2Reg.containsKey(name);
    }

    public String getReg(String name) {
        return name2Reg.get(name);
    }

    public HashMap<String, String> getSaveRegs() {
        return saveRegs;
    }

    public boolean isTLE() {
        return isTLE;
    }

}
