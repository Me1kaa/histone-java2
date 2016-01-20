package ru.histone.v2;

import org.junit.Assert;
import org.junit.Test;
import ru.histone.HistoneException;
import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.Evaluator;
import ru.histone.v2.parser.Parser;
import ru.histone.v2.parser.node.ExpAstNode;

/**
 * Created by inv3r on 13/01/16.
 */
public class OldEvaluatorTest {
    @Test
    public void testIf() throws HistoneException {
        String ifStatement = "sdjhfsdjkbfdsksd {{if 1 = 1}}1231231{{else}}aaa{{/if}}sdklbjfsdlkfdsbfsdksfd";
        String expectedResult = "sdjhfsdjkbfdsksd 1231231sdklbjfsdlkfdsbfsdksfd";
        String baseUri = "";

        Parser parser = new Parser();
        ExpAstNode ifNode = parser.process(ifStatement, "");
        Context context = new Context();
        Evaluator evaluator = new Evaluator();
        String result = evaluator.process(baseUri, ifNode, context);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testIf2() throws HistoneException {
        String ifStatement = "sdjhfsdjkbfdsksd {{if 1 = 1 && 2 != \"fdsds4\" || '3' = 2}}1231231{{else}}aaa{{/if}}sdklbjfsdlkfdsbfsdksfd";
        String expectedResult = "sdjhfsdjkbfdsksd 1231231sdklbjfsdlkfdsbfsdksfd";
        String baseUri = "";

        Parser parser = new Parser();
        ExpAstNode ifNode = parser.process(ifStatement, "");
        Context context = new Context();
        Evaluator evaluator = new Evaluator();
        String result = evaluator.process(baseUri, ifNode, context);
        Assert.assertEquals(expectedResult, result);
    }
}
