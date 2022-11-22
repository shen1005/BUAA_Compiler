package Optimize;

import MidCode.MidCodeTerm;

public class TopBlock extends BaseBlock{
    private MidCodeTerm midCodeTerm;
    private FuncBlock fatherBlock;
    private String funcString;

    public TopBlock(MidCodeTerm midCodeTerm) {
        this.midCodeTerm = midCodeTerm;
    }

    public void setFatherBlock(FuncBlock fatherBlock) {
        this.fatherBlock = fatherBlock;
    }

    public MidCodeTerm getMidCodeTerm() {
        return midCodeTerm;
    }

    public void setFuncString(String funcString) {
        this.funcString = funcString;
    }
}
