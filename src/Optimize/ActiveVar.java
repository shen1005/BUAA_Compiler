package Optimize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ActiveVar {
    private String name;
    private final ArrayList<ActiveVar> conflictVar = new ArrayList<>();
    private boolean isHaveReg = false;
    private String reg;
    private boolean isDeleted = false;
    private boolean isRemoved = false;
    private final HashSet<String> conflictName = new HashSet<>();


    public ActiveVar(String name) {
        this.name = name;
    }

    public boolean addConflictVar(ActiveVar var) {
        //if (!conflictName.contains(var.getName())) {
        if (conflictVar.size() >= 1750) {
            return false;
        }
        if (!conflictName.contains(var.getName())) {
            conflictVar.add(var);
            conflictName.add(var.getName());
            return var.addSingleConflictVar(this);
        }
        //}
        return true;
    }

    public boolean addSingleConflictVar(ActiveVar var) {
        //if ( !conflictName.contains(var.getName())) {
        if (conflictVar.size() >= 1750) {
            return false;
        }
        if (!conflictName.contains(var.getName())) {
            conflictVar.add(var);
            conflictName.add(var.getName());
        }
        return true;
        //}
    }

    public int getSideNum() {
        int num = 0;
//        for (ActiveVar var : conflictVar) {
//            if (!var.isDeleted && !var.isRemoved) {
//                num++;
//            }
//        }
        Iterator<ActiveVar> iterator = conflictVar.iterator();
        while (iterator.hasNext()) {
            ActiveVar var = iterator.next();
            if (!var.isDeleted && !var.isRemoved) {
                num++;
            }
        }
        return num;
    }

    public String getName() {
        return name;
    }

    public void remove() {
        isRemoved = true;
        isDeleted = false;
        isHaveReg = false;
    }

    public void delete() {
        isDeleted = true;
        isRemoved = false;
        isHaveReg = false;
    }

    public void revert() {
        isDeleted = false;
        isRemoved = false;
        isHaveReg = true;
    }

    public HashSet<String> getConflictRegs() {
        HashSet<String> conflictRegs = new HashSet<>();
        for (ActiveVar var : conflictVar) {
            if (var.isHaveReg) {
                conflictRegs.add(var.reg);
            }
        }
        return conflictRegs;
    }

    public void setReg(String reg) {
        this.reg = reg;
        isHaveReg = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ActiveVar) {
            return name.equals(((ActiveVar) obj).name);
        }
        return false;
    }

}
