package Optimize;

import MidCode.MidCodeTerm;
import Type.OpType;

import java.util.ArrayList;
import java.util.HashMap;

public class Optimizer {
    private static Optimizer optimizer = new Optimizer();
    private ArrayList<FuncBlock> funcBlockList = new ArrayList<>();
    private BaseBlock nowBlock;
    private ArrayList<MidCodeTerm> midCodeList = new ArrayList<>();
    private ArrayList<MidCodeTerm> globalList = new ArrayList<>();
    private ArrayList<MidCodeTerm> AllList;
    private FuncBlock nowFuncBlock;
    private int nowIndex;
    private final HashMap<String, BaseBlock> label2Block = new HashMap<>();
    private boolean isOptimized;

    public void setOptimizer(boolean isOptimized) {
        this.isOptimized = isOptimized;
    }

    public boolean getOptimizer() {
        return isOptimized;
    }

    public static Optimizer getInstance() {
        return optimizer;
    }

    public void setMidCodeList(ArrayList<MidCodeTerm> midCodeList) {
        this.AllList = midCodeList;
        for (MidCodeTerm midCodeTerm : midCodeList) {
            if (midCodeTerm.getOp().equals(OpType.GLOBAL_DEC)) {
                globalList.add(midCodeTerm);
            } else {
                this.midCodeList.add(midCodeTerm);
            }
        }
    }

    public void beginOptimize() {
        //生成基本块
        buildBlock();
        //优化基本块
        deleteDeadBlock();
        //块内的常量传播
        constantPropagation();
        //块内删除公共子表达式
        deleteCommonExpression();
        //块内常量传播
        constantPropagation();
        //计算基本块的活跃变量
        calculateInAndOut();
        //删除死代码
        deleteDeadCode();
        //图着色
        graphColoring();
    }

    private void deleteCommonExpression() {
        for (FuncBlock funcBlock : funcBlockList) {
            funcBlock.deleteCommonExpression();
        }
    }

    public void buildBlock() {
        //找到入口点
        int i;
        for (i = 0; i < midCodeList.size(); i++) {
            MidCodeTerm temp = midCodeList.get(i);
            if (temp.getOp().equals(OpType.PUT_LABEL) && (i - 1 < 0 || !midCodeList.get(i - 1).getOp().equals(OpType.PUT_LABEL))) {
                temp.setIsEntrance(true);
            } else {
                OpType op = temp.getOp();
                if (op.equals(OpType.GOTO) || op.equals(OpType.BEQZ) || op.equals(OpType.BNEZ) || op.equals(OpType.RET_VOID) || op.equals(OpType.RET_VALUE) || op.equals(OpType.EXIT)) {
                    if (i + 1 < midCodeList.size()) {
                        midCodeList.get(i + 1).setIsEntrance(true);
                    }
                }
            }
        }
        //开始生成基本块
        for (i = 0; i < midCodeList.size(); i++) {
            MidCodeTerm temp = midCodeList.get(i);
            if (temp.getOp().equals(OpType.FUNC_DEC)) {
                FuncBlock funcBlock = new FuncBlock(temp);
                funcBlock.setFuncString(temp.getResult());
                funcBlockList.add(funcBlock);
                nowFuncBlock = funcBlock;
                nowIndex = 0;
                if (i + 1 < midCodeList.size() && !midCodeList.get(i + 1).getOp().equals(OpType.FUNC_DEC)) {
                    midCodeList.get(i + 1).setIsEntrance(true);
                }
            }
            else if (temp.getIsEntrance()) {
                BaseBlock baseBlock = new BaseBlock(nowIndex++);
                baseBlock.setFunc(nowFuncBlock);
                nowFuncBlock.addBaseBlock(baseBlock);

                nowBlock = baseBlock;
                if (!temp.getOp().equals(OpType.PUT_LABEL)) {
                    nowBlock.addMidCode(temp);
                } else {
                    //是label
                    label2Block.put(temp.getResult(), nowBlock);
                }
            }
            else if (temp.getOp().equals(OpType.PUT_LABEL)) {
                label2Block.put(temp.getResult(), nowBlock);
            } else {
                nowBlock.addMidCode(temp);
            }
        }
        //生成基本块之间的关系
        connectBlock();
    }

    public void connectBlock() {
        for (FuncBlock funcBlock : funcBlockList) {
            ArrayList<BaseBlock> baseBlockList = funcBlock.getBaseBlockList();
            funcBlock.getTopBlock().connectBlock(baseBlockList.get(0));
            for (int i = 0; i < baseBlockList.size(); i++) {
                BaseBlock baseBlock = baseBlockList.get(i);
                ArrayList<MidCodeTerm> midCodeList = baseBlock.getMidCodeList();
                MidCodeTerm lastMidCode = midCodeList.get(midCodeList.size() - 1);
                OpType op = lastMidCode.getOp();
                if (op.equals(OpType.GOTO)) {
                    baseBlock.connectBlock(label2Block.get(lastMidCode.getResult()));
                } else if (op.equals(OpType.BEQZ) || op.equals(OpType.BNEZ)) {
                    baseBlock.connectBlock(label2Block.get(lastMidCode.getResult()));
                    if (i + 1 < baseBlockList.size()) {
                        baseBlock.connectBlock(baseBlockList.get(i + 1));
                    }
                } else if (op.equals(OpType.RET_VOID) || op.equals(OpType.RET_VALUE) || op.equals(OpType.EXIT)) {
                    baseBlock.connectBlock(funcBlock.getBottomBlock());
                } else {
                    if (i + 1 < baseBlockList.size()) {
                        baseBlock.connectBlock(baseBlockList.get(i + 1));
                    }
                }
            }
        }
    }

    public void deleteDeadBlock() {
        for (FuncBlock funcBlock : funcBlockList) {
            ArrayList<BaseBlock> baseBlocks = funcBlock.getBaseBlockList();
            baseBlocks.removeIf(e->e.getBeforeBlock().size() == 0);
            int i;
            for (i = 0; i < baseBlocks.size(); i++) {
                BaseBlock baseBlock = baseBlocks.get(i);
                baseBlock.setIndex(i);
            }
        }
    }

    public void constantPropagation() {
        for (FuncBlock funcBlock : funcBlockList) {
            ArrayList<BaseBlock> baseBlocks = funcBlock.getBaseBlockList();
            for (BaseBlock baseBlock : baseBlocks) {
                baseBlock.propagate();
            }
        }
    }

    public void calculateInAndOut() {
        for (FuncBlock funcBlock : funcBlockList) {
            funcBlock.produceInAndOut();
        }
    }

    public void deleteDeadCode() {
        for (FuncBlock funcBlock : funcBlockList) {
            funcBlock.deleteDeadCode();
        }
    }

    public ArrayList<MidCodeTerm> getMidCode() {
        ArrayList<MidCodeTerm> midCodeList = new ArrayList<>(globalList);
        for (String label: label2Block.keySet()) {
            label2Block.get(label).addLabel(label);
        }
        for (FuncBlock funcBlock : funcBlockList) {
            midCodeList.addAll(funcBlock.getMidCode());
        }
        return midCodeList;
    }

    public void graphColoring() {
        for (FuncBlock funcBlock : funcBlockList) {
            funcBlock.graphColoring();
        }
    }
}
