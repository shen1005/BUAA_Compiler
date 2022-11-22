package Optimize;

import Type.OpType;

import java.util.Objects;

public class CalNode {
    private OpType opType;
    private String para1;
    private String para2;
    private String result;

    public CalNode(OpType opType, String para1, String para2, String result) {
        this.opType = opType;
        this.para1 = para1;
        this.para2 = para2;
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalNode calNode = (CalNode) o;
        if (opType.equals(OpType.ADD) || opType.equals(OpType.MUL)) {
            return opType == calNode.opType &&
                    ((para1.equals(calNode.para1) && para2.equals(calNode.para2)) || (para1.equals(calNode.para2) && para2.equals(calNode.para1)));
        } else {
            return opType == calNode.opType &&
                    para1.equals(calNode.para1) &&
                    para2.equals(calNode.para2);
        }
    }

    public String getResult() {
        return result;
    }

    public boolean isHaveValue(String name) {
        return para1.equals(name) || para2.equals(name) || result.equals(name);
    }

    public OpType getOp() {
        return opType;
    }

    public String getPara1() {
        return para1;
    }

    public String getPara2() {
        return para2;
    }
}
