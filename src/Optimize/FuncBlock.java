package Optimize;

import MidCode.MidCodeTerm;

import java.util.ArrayList;

public class FuncBlock {
    private TopBlock topBlock;
    private BottomBlock bottomBlock;
    private ArrayList<BaseBlock> baseBlockList = new ArrayList<>();
    private String funcString;
    private final ConflictGraph conflictGraph = new ConflictGraph();
    private boolean isTLE = false;

    public FuncBlock(MidCodeTerm midCodeTerm) {
        topBlock = new TopBlock(midCodeTerm);
        bottomBlock = new BottomBlock();
        midCodeTerm.setConflictGraph(conflictGraph);
    }

    public void addBaseBlock(BaseBlock baseBlock) {
        baseBlockList.add(baseBlock);
    }


    public void setFuncString(String func) {
        topBlock.setFuncString(func);
        bottomBlock.setFuncString(func);
        this.funcString = func;
    }

    public String getFunc() {
        return funcString;
    }

    public ArrayList<BaseBlock> getBaseBlockList() {
        return baseBlockList;
    }

    public TopBlock getTopBlock() {
        return topBlock;
    }

    public BottomBlock getBottomBlock() {
        return bottomBlock;
    }

    public void propagate() {
        for (BaseBlock baseBlock : baseBlockList) {
            baseBlock.propagate();
        }
    }

    public void produceInAndOut() {
        for (BaseBlock baseBlock : baseBlockList) {
            baseBlock.produceDefUseSet();
        }
        boolean changed = true;
        do {
            changed = false;
            for (int i = baseBlockList.size() - 1; i >= 0; i--) {
                BaseBlock baseBlock = baseBlockList.get(i);
                if (baseBlock.produceOutSet()) {
                    changed = true;
                }
                baseBlock.produceInSet();
            }
        } while (changed);
        for (BaseBlock baseBlock : baseBlockList) {
            baseBlock.produceInAndOutForMid();
        }
    }

    public void deleteDeadCode() {
        for (BaseBlock baseBlock : baseBlockList) {
            baseBlock.deleteDeadCode();
        }
    }

    public ArrayList<MidCodeTerm> getMidCode() {
        ArrayList<MidCodeTerm> midCode = new ArrayList<>();
        midCode.add(topBlock.getMidCodeTerm());
        for (BaseBlock baseBlock : baseBlockList) {
            midCode.addAll(baseBlock.getMidCode());
        }
        return midCode;
    }

    public void graphColoring() {
        for (BaseBlock baseBlock : baseBlockList) {
            baseBlock.graphColoring(conflictGraph);
            if (conflictGraph.isTLE()) {
                return;
            }
        }
        conflictGraph.graphColoring();
    }

    public ConflictGraph getConflictGraph() {
        return conflictGraph;
    }

    public void deleteCommonExpression() {
        for (BaseBlock baseBlock : baseBlockList) {
            baseBlock.deleteCommonExpression();
        }
    }
}
