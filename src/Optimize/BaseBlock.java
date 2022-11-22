package Optimize;

import MidCode.MidCodeTerm;
import Type.OpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BaseBlock {
    protected ArrayList<MidCodeTerm> midCodeList = new ArrayList<>();
    protected final ArrayList<BaseBlock> beforeBlock = new ArrayList<>();
    protected final ArrayList<BaseBlock> afterBlock = new ArrayList<>();
    protected int index;
    protected FuncBlock func;
    private final HashMap<String, String> valueMap = new HashMap<>();
    //TODO 数组名未算入def和use
    private final HashSet<String> defSet = new HashSet<>();
    private final HashSet<String> useSet = new HashSet<>();
    private final HashSet<String> inSet = new HashSet<>();
    private final HashSet<String> outSet = new HashSet<>();
    private final ArrayList<String> labelList = new ArrayList<>();

    public BaseBlock() {

    }

    public BaseBlock(int index) {
        this.index = index;
    }

    public void addMidCode(MidCodeTerm midCodeTerm) {
        midCodeList.add(midCodeTerm);
        midCodeTerm.setConflictGraph(func.getConflictGraph());
    }

    public void setFunc(FuncBlock func) {
        this.func = func;
    }

    public void addBeforeBlock(BaseBlock baseBlock) {
        beforeBlock.add(baseBlock);
    }

    public void addAfterBlock(BaseBlock baseBlock) {
        afterBlock.add(baseBlock);
    }

    //注意是前驱调用后继
    public void connectBlock(BaseBlock baseBlock) {
        addAfterBlock(baseBlock);
        baseBlock.addBeforeBlock(this);
    }

    public ArrayList<MidCodeTerm> getMidCodeList() {
        return midCodeList;
    }

    public ArrayList<BaseBlock> getBeforeBlock() {
        return beforeBlock;
    }

    public ArrayList<BaseBlock> getAfterBlock() {
        return afterBlock;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    //是不是全局变量
    public boolean isGlobal(String name) {
        if (isNum(name)) {
            return false;
        }
        if (name.charAt(name.length() - 1) == 'A') {
            return false;
        }
        int line = Integer.parseInt(name.substring(name.lastIndexOf('_') + 1, name.length()));
        return line == -1;
    }

    public void propagate() {
        valueMap.clear();
        for (MidCodeTerm midCodeTerm : midCodeList) {
            OpType op = midCodeTerm.getOp();
            String result = midCodeTerm.getResult();
            String para1 = midCodeTerm.getPara1();
            String para2 = midCodeTerm.getPara2();
            String para3 = midCodeTerm.getPara3();
            if (op.equals(OpType.ASSIGN)) {
                if (valueMap.containsKey(result)) {
                    midCodeTerm.setResult(valueMap.get(result));
                    result = valueMap.get(result);
                }
                if (!isGlobal(result)) {
                    valueMap.put(para1, result);
                }
                valueMap.values().remove(para1);
            } else if (isCalculate(op)) {
                dealCalculate(midCodeTerm);
            }
            if (isNewPara1(op)) {
                if (valueMap.containsKey(para1)) {
                    midCodeTerm.setPara1(valueMap.get(para1));
                }
            }
            if (isNewPara2(op) || (op.equals(OpType.LOAD_ADDRESS) && para2 != null)) {
                if (valueMap.containsKey(para2)) {
                    midCodeTerm.setPara2(valueMap.get(para2));
                }
            }
            if (isNewPara3(op)) {
                if (valueMap.containsKey(para3)) {
                    midCodeTerm.setPara3(valueMap.get(para3));
                }
            }
            if (isNewResult(op)) {
                if (valueMap.containsKey(result)) {
                    midCodeTerm.setResult(valueMap.get(result));
                }
            }

        }
    }

    public boolean isNewPara1(OpType op) {
        return op.equals(OpType.PUSH) || op.equals(OpType.BNEZ) ||op.equals(OpType.BEQZ);
    }

    public boolean isNewPara2(OpType op) {
        return op.equals(OpType.SW_1D) ||
                op.equals(OpType.SW_2D) ||
                op.equals(OpType.LOAD_ARRAY_1D) ||
                op.equals(OpType.LOAD_ARRAY_2D);
    }

    public boolean isNewPara3(OpType op) {
        return op.equals(OpType.SW_2D) ||
                op.equals(OpType.LOAD_ARRAY_2D);
    }

    public boolean isNewResult(OpType op) {
        return op.equals(OpType.PRINT_INT) || op.equals(OpType.RET_VALUE) || op.equals(OpType.SW_1D) || op.equals(OpType.SW_2D);
    }

    public boolean isCalculate(OpType op) {
        return op.equals(OpType.ADD) ||
                op.equals(OpType.SUB) ||
                op.equals(OpType.MUL) ||
                op.equals(OpType.DIV) ||
                op.equals(OpType.MOD) ||
                op.equals(OpType.EQ) ||
                op.equals(OpType.NE) ||
                op.equals(OpType.LT) ||
                op.equals(OpType.LE) ||
                op.equals(OpType.GT) ||
                op.equals(OpType.GE);
    }

    //参数1可以传播替换
    public boolean isNeedP1() {
        return true;
    }

    public void dealCalculate(MidCodeTerm midCodeTerm) {
        String result = midCodeTerm.getResult();
        String para1 = midCodeTerm.getPara1();
        String para2 = midCodeTerm.getPara2();
        if (valueMap.containsKey(para1)) {
            midCodeTerm.setPara1(valueMap.get(para1));
            para1 = valueMap.get(para1);
        }
        if (valueMap.containsKey(para2)) {
            midCodeTerm.setPara2(valueMap.get(para2));
            para2 = valueMap.get(para2);
        }
        if (isNum(para1) && isNum(para2)) {
            int num1 = Integer.parseInt(para1);
            int num2 = Integer.parseInt(para2);
            int resultNum = 0;
            switch (midCodeTerm.getOp()) {
                case ADD:
                    resultNum = num1 + num2;
                    break;
                case SUB:
                    resultNum = num1 - num2;
                    break;
                case MUL:
                    resultNum = num1 * num2;
                    break;
                case DIV:
                    resultNum = num1 / num2;
                    break;
                case MOD:
                    resultNum = num1 % num2;
                    break;
                case EQ:
                    resultNum = num1 == num2 ? 1 : 0;
                    break;
                case NE:
                    resultNum = num1 != num2 ? 1 : 0;
                    break;
                case LT:
                    resultNum = num1 < num2 ? 1 : 0;
                    break;
                case LE:
                    resultNum = num1 <= num2 ? 1 : 0;
                    break;
                case GT:
                    resultNum = num1 > num2 ? 1 : 0;
                    break;
                case GE:
                    resultNum = num1 >= num2 ? 1 : 0;
                    break;
            }
            valueMap.put(result, String.valueOf(resultNum));
        } else {
            //如果不是数字，原来的值也不能用了
            valueMap.remove(result);
        }
    }

    public void produceDefUseSet() {
        for (MidCodeTerm midCodeTerm : midCodeList) {
            OpType op = midCodeTerm.getOp();
            String result = midCodeTerm.getResult();
            String para1 = midCodeTerm.getPara1();
            String para2 = midCodeTerm.getPara2();
            String para3 = midCodeTerm.getPara3();
            //只有para1是use
            if (op.equals(OpType.PUSH) || op.equals(OpType.BNEZ) || op.equals(OpType.BEQZ)) {
                if (canPutUse(para1)) {
                    useSet.add(para1);
                }
                if (canPutUseMid(midCodeTerm, para1)) {
                    midCodeTerm.addToUseSet(para1);
                }
            } else if (op.equals(OpType.SW_RET) || op.equals(OpType.GetInt) || (op.equals(OpType.LOAD_ADDRESS) && para2 == null)) {
                //只有result是def
                if (canPutDef(result)) {
                    defSet.add(result);
                }
                if (canPutDefMid(midCodeTerm, result)) {
                    midCodeTerm.addToDefSet(result);
                }
            } else if (op.equals(OpType.LOAD_ARRAY_1D) || (op.equals(OpType.LOAD_ADDRESS) && para2 != null)) {
                //只有result是def, para2是use
                if (canPutUse(para2)) {
                    useSet.add(para2);
                }
                if (canPutUseMid(midCodeTerm, para2)) {
                    midCodeTerm.addToUseSet(para2);
                }
                if (canPutDef(result)) {
                    defSet.add(result);
                }
                if (canPutDefMid(midCodeTerm, result)) {
                    midCodeTerm.addToDefSet(result);
                }
            } else if (op.equals(OpType.LOAD_ARRAY_2D)) {
                //result是def, para2是use, para3是use
                if (canPutUse(para2)) {
                    useSet.add(para2);
                }
                if (canPutUseMid(midCodeTerm, para2)) {
                    midCodeTerm.addToUseSet(para2);
                }
                if (canPutUse(para3)) {
                    useSet.add(para3);
                }
                if (canPutUseMid(midCodeTerm, para3)) {
                    midCodeTerm.addToUseSet(para3);
                }
                if (canPutDef(result)) {
                    defSet.add(result);
                }
                if (canPutDefMid(midCodeTerm, result)) {
                    midCodeTerm.addToDefSet(result);
                }
            } else if (op.equals(OpType.ASSIGN)) {
                //para1是def, result是use
                if (canPutUse(result)) {
                    useSet.add(result);
                }
                if (canPutUseMid(midCodeTerm, result)) {
                    midCodeTerm.addToUseSet(result);
                }
                if (canPutDef(para1)) {
                    defSet.add(para1);
                }
                if (canPutDefMid(midCodeTerm, para1)) {
                    midCodeTerm.addToDefSet(para1);
                }
            } else if (op.equals(OpType.PRINT_INT) || op.equals(OpType.RET_VALUE)) {
                //只有result是use
                if (canPutUse(result)) {
                    useSet.add(result);
                }
                if (canPutUseMid(midCodeTerm, result)) {
                    midCodeTerm.addToUseSet(result);
                }
            } else if (op.equals(OpType.ADD) || op.equals(OpType.SUB) || op.equals(OpType.MUL) || op.equals(OpType.DIV) || op.equals(OpType.MOD) || op.equals(OpType.EQ) || op.equals(OpType.NE) || op.equals(OpType.LT) || op.equals(OpType.LE) || op.equals(OpType.GT) || op.equals(OpType.GE)) {
                //para1是use, para2是use, result是def
                if (canPutUse(para1)) {
                    useSet.add(para1);
                }
                if (canPutUseMid(midCodeTerm, para1)) {
                    midCodeTerm.addToUseSet(para1);
                }
                if (canPutUse(para2)) {
                    useSet.add(para2);
                }
                if (canPutUseMid(midCodeTerm, para2)) {
                    midCodeTerm.addToUseSet(para2);
                }
                if (canPutDef(result)) {
                    defSet.add(result);
                }
                if (canPutDefMid(midCodeTerm, result)) {
                    midCodeTerm.addToDefSet(result);
                }
            } else if (op.equals(OpType.NEG) || op.equals(OpType.NOT)) {
                //只有para1是use, result是def
                if (canPutUse(para1)) {
                    useSet.add(para1);
                }
                if (canPutUseMid(midCodeTerm, para1)) {
                    midCodeTerm.addToUseSet(para1);
                }
                if (canPutDef(result)) {
                    defSet.add(result);
                }
                if (canPutDefMid(midCodeTerm, result)) {
                    midCodeTerm.addToDefSet(result);
                }
            } else if (op.equals(OpType.SW_1D)) {
                //para2和result都是use
                if (canPutUse(para2)) {
                    useSet.add(para2);
                }
                if (canPutUseMid(midCodeTerm, para2)) {
                    midCodeTerm.addToUseSet(para2);
                }
                if (canPutUse(result)) {
                    useSet.add(result);
                }
                if (canPutUseMid(midCodeTerm, result)) {
                    midCodeTerm.addToUseSet(result);
                }
            } else if (op.equals(OpType.SW_2D)) {
                //para2, para3和result都是use
                if (canPutUse(para2)) {
                    useSet.add(para2);
                }
                if (canPutUseMid(midCodeTerm, para2)) {
                    midCodeTerm.addToUseSet(para2);
                }
                if (canPutUse(para3)) {
                    useSet.add(para3);
                }
                if (canPutUseMid(midCodeTerm, para3)) {
                    midCodeTerm.addToUseSet(para3);
                }
                if (canPutUse(result)) {
                    useSet.add(result);
                }
                if (canPutUseMid(midCodeTerm, result)) {
                    midCodeTerm.addToUseSet(result);
                }
            }
        }
    }

    //对表示变化size
    public boolean produceOutSet() {
        int size = outSet.size();
        for (BaseBlock block : afterBlock) {
            outSet.addAll(block.inSet);
        }
        return outSet.size() != size;
    }

    public void produceInSet() {
        inSet.clear();
        inSet.addAll(outSet);
        inSet.removeAll(defSet);
        inSet.addAll(useSet);
    }

    public void produceInAndOutForMid() {
        HashSet<String> temp = new HashSet<>(outSet);
        for (int i = midCodeList.size() - 1; i >= 0; i--) {
            MidCodeTerm midCodeTerm = midCodeList.get(i);
            midCodeTerm.getOutSet().addAll(temp);
            HashSet<String> temp2 = new HashSet<>(temp);
            temp2.removeAll(midCodeTerm.getDefSet());
            temp2.addAll(midCodeTerm.getUseSet());
            midCodeTerm.getInSet().addAll(temp2);
            temp = temp2;
        }

    }

    public boolean isLocalVar(String var) {
        if (var.charAt(var.length() - 1) == 'A') {
            return true;
        } else if (isNum(var)) {
            return false;
        } else {
            return Integer.parseInt(var.substring(var.lastIndexOf('_') + 1)) != -1;
        }
    }

    public boolean canPutUse(String var) {
        return var != null && isLocalVar(var) && !defSet.contains(var);
    }

    public boolean canPutDef(String var) {
        return var != null && isLocalVar(var) && !useSet.contains(var);
    }

    public boolean canPutDefMid(MidCodeTerm midCodeTerm, String var) {
        return var != null && isLocalVar(var) && !midCodeTerm.getUseSet().contains(var);
    }

    public boolean canPutUseMid(MidCodeTerm midCodeTerm, String var) {
        return var != null && isLocalVar(var) && !midCodeTerm.getDefSet().contains(var);
    }

    public boolean isNum(String str) {
        return str.matches("[-+]*[0-9]+");
    }

    public HashSet<String> getInSet() {
        return inSet;
    }

    public HashSet<String> getOutSet() {
        return outSet;
    }

    public void deleteDeadCode() {
        ArrayList<MidCodeTerm> temp = new ArrayList<>();
        for (MidCodeTerm midCodeTerm : midCodeList) {
            String result = midCodeTerm.getResult();
            String para1 = midCodeTerm.getPara1();
            OpType op = midCodeTerm.getOp();
            //def出现在para1中
            if (op.equals(OpType.ASSIGN)) {
                //不是全局变量
                if (isLocalVar(para1)) {
                    if (midCodeTerm.getOutSet().contains(para1)) {
                        temp.add(midCodeTerm);
                    }
                } else {
                    temp.add(midCodeTerm);
                }
            } else if (op.equals(OpType.SW_RET) ||
                    op.equals(OpType.LOAD_ADDRESS) ||
                    op.equals(OpType.ADD) ||
                    op.equals(OpType.SUB) ||
                    op.equals(OpType.MUL) ||
                    op.equals(OpType.DIV) ||
                    op.equals(OpType.MOD) ||
                    op.equals(OpType.LT) ||
                    op.equals(OpType.LE) ||
                    op.equals(OpType.GT) ||
                    op.equals(OpType.GE) ||
                    op.equals(OpType.EQ) ||
                    op.equals(OpType.NE) ||
                    op.equals(OpType.NEG) ||
                    op.equals(OpType.NOT)) {
                if (isLocalVar(result)) {
                    if (midCodeTerm.getOutSet().contains(result)) {
                        temp.add(midCodeTerm);
                    }
                } else {
                    temp.add(midCodeTerm);
                }
            } else {
                temp.add(midCodeTerm);
            }
        }
        midCodeList = temp;
    }

    public void addLabel(String label) {
        labelList.add(label);
    }

    public ArrayList<MidCodeTerm> getMidCode() {
        ArrayList<MidCodeTerm> temp = new ArrayList<>();
        for (String label : labelList) {
            temp.add(new MidCodeTerm(OpType.PUT_LABEL, null, null, label));
        }
        temp.addAll(midCodeList);
        return temp;
    }

    public void graphColoring(ConflictGraph conflictGraph) {
        //TODO 函数的参数可以不用分配寄存器
        for (MidCodeTerm midCodeTerm : midCodeList) {
            HashSet<String> outSet = midCodeTerm.getOutSet();
            if (!conflictGraph.isTLE()) {
                conflictGraph.addActiveVarSet(outSet);
            }
            else {
                return;
            }
        }
    }

//    public void deleteCommonExpression() {
//        ArrayList<CalNode> spreadList = new ArrayList<>();
//        for (MidCodeTerm midCodeTerm : midCodeList) {
//            OpType op = midCodeTerm.getOp();
//            String para1 = midCodeTerm.getPara1();
//            String para2 = midCodeTerm.getPara2();
//            String result = midCodeTerm.getResult();
//            if (op == OpType.ADD || op == OpType.SUB || op == OpType.MUL || op == OpType.DIV || op == OpType.MOD) {
//                boolean flag = false;
//                CalNode calNode = haveTerm(midCodeTerm, spreadList);
//                if (calNode != null) {
//                    midCodeTerm.setOp(OpType.ASSIGN);
//                    midCodeTerm.setPara1(result);
//                    midCodeTerm.setResult(calNode.getResult());
//                    flag = true;
//                }
//                spreadList.removeIf(temp -> temp.isHaveValue(result));
//                if (!isGlobal(para1) && !isGlobal(para2) && !isGlobal(result) && !flag) {
//                    spreadList.add(new CalNode(op, para1, para2, result));
//                }
//            } else if (op == OpType.ASSIGN) {
//                spreadList.removeIf(temp -> temp.isHaveValue(para1));
//            }
//             else if (isChange(op)) {
//                spreadList.removeIf(temp -> temp.isHaveValue(result));
//            }
//        }
//    }
//
//    public boolean isCanChange(OpType op) {
//        return  op == OpType.LT ||
//                op == OpType.LE ||
//                op == OpType.GT ||
//                op == OpType.GE ||
//                op == OpType.EQ ||
//                op == OpType.NE ||
//                op == OpType.NEG ||
//                op == OpType.NOT;
//    }
//    public boolean isChange(OpType op) {
//        return op == OpType.SW_RET ||
//                op == OpType.LOAD_ADDRESS ||
//                op == OpType.LOAD_ARRAY_1D ||
//                op == OpType.LOAD_ARRAY_2D ||
//                op == OpType.GetInt ||
//                op == OpType.LT ||
//                op == OpType.LE ||
//                op == OpType.GT ||
//                op == OpType.GE ||
//                op == OpType.EQ ||
//                op == OpType.NE ||
//                op == OpType.NEG ||
//                op == OpType.NOT;
//    }
//
//    public CalNode haveTerm(MidCodeTerm midCodeTerm, ArrayList<CalNode> spreadList) {
//        for (CalNode calNode : spreadList) {
//            if (calNode.getOp().equals(midCodeTerm.getOp()) &&
//                    calNode.getPara1().equals(midCodeTerm.getPara1()) &&
//                    calNode.getPara2().equals(midCodeTerm.getPara2())) {
//                return calNode;
//            }
//        }
//        return null;
//    }

      public void deleteCommonExpression() {
        ArrayList<CalNode> spreadList = new ArrayList<>();
        for (MidCodeTerm midCodeTerm : midCodeList) {
            OpType op = midCodeTerm.getOp();
            String para1 = midCodeTerm.getPara1();
            String para2 = midCodeTerm.getPara2();
            String result = midCodeTerm.getResult();
            if (op == OpType.ADD || op == OpType.SUB || op == OpType.MUL || op == OpType.DIV || op == OpType.MOD) {
                boolean flag = false;
                CalNode calNode = new CalNode(op, para1, para2, result);
                //TODO 小心全局变量
                if (spreadList.size() == 0) {
                    spreadList.add(calNode);
                } else {
                for (CalNode calNode1 : spreadList) {
                    if (calNode1.equals(calNode)) {
                        midCodeTerm.setOp(OpType.ASSIGN);
                        midCodeTerm.setPara1(result);
                        midCodeTerm.setResult(calNode1.getResult());
                        flag = true;
                        break;
                    }
                }
                spreadList.removeIf(temp -> temp.isHaveValue(result));
                if (!flag & !isGlobal(para1) && !isGlobal(para2) && !isGlobal(result)) {
                    spreadList.add(calNode);
                }
            }
            } else if (op == OpType.ASSIGN) {
                spreadList.removeIf(temp -> temp.isHaveValue(para1));
            }
        }
      }
}
