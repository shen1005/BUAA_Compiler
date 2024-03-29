import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import MidCode.CodeManager;
import MidCode.Visitor;
import Node.ErrorAnalysis;
import Node.GrammarAnalysis;
import Node.NonLeafNode;
import Optimize.Optimizer;
import TargetCode.TargetCode;
import WordAnalysis.WordAnalysis;

public class Compiler {
    public static void main(String[] args) {
        boolean isOptimize = true;

        try {
            String file = new String(Files.readAllBytes(Paths.get("testfile.txt")));
            File output = new File("error.txt");
           File midCode = new File("midCode.txt");
//           File symbolTable = new File("symbolTable.txt");
            File targetCode = new File("mips.txt");

            WordAnalysis wordAnalysis = new WordAnalysis();
            wordAnalysis.Analyze(file);
            GrammarAnalysis grammarAnalysis = new GrammarAnalysis(wordAnalysis.getWords());
            grammarAnalysis.analysis();
            grammarAnalysis.findError();

            FileWriter writer = new FileWriter(output);
          FileWriter midCodeWriter = new FileWriter(midCode);
//            FileWriter symbolTableWriter = new FileWriter(symbolTable);
            FileWriter targetCodeWriter = new FileWriter(targetCode);
 //           System.out.println("Error ok");
            if (!ErrorAnalysis.getInstance().isFindError()) {
                Optimizer.getInstance().setOptimizer(isOptimize);
                Visitor visitor = new Visitor((NonLeafNode) grammarAnalysis.getRoot());
                //String symbolTableStr = ErrorAnalysis.getInstance().getRootTable().toString();
                if (!isOptimize) {
                    String code = new TargetCode().getCode();
                    targetCodeWriter.write(code);
                    midCodeWriter.write(CodeManager.getInstance().toString());
                } else {
                    Optimizer optimizer = Optimizer.getInstance();
                    optimizer.setOptimizer(isOptimize);
                    optimizer.setMidCodeList(CodeManager.getInstance().getMidCodeTerms());
                    optimizer.beginOptimize();
                    CodeManager.getInstance().setMidCodeList(optimizer.getMidCode());
                    //optimizer.setOptimizer(false);
                    //Optimizer.getInstance().setOptimizer(false);
                    String code = new TargetCode().getCode();
                    targetCodeWriter.write(code);
                    midCodeWriter.write(CodeManager.getInstance().toString());
                }
                //symbolTableWriter.write(symbolTableStr);
            }

            writer.write(ErrorAnalysis.getInstance().toString());
            //symbolTableWriter.close();
            midCodeWriter.close();
            writer.close();
            targetCodeWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}