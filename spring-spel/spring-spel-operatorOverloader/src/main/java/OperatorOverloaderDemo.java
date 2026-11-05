import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class OperatorOverloaderDemo {

    public static void main(String[] args) {
        // Create the expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Create the evaluation context
        StandardEvaluationContext context = new StandardEvaluationContext();
        // Create a custom OperatorOverloader and register it in the context
        context.setOperatorOverloader(new CustomOperatorOverloader());
        context.setVariable("myBean1", new MyBean(18));
        context.setVariable("myBean2", new MyBean(20));

        // Define a SpEL expression that uses the custom operator
        Expression expression = parser.parseExpression("#myBean1 + #myBean2");

        // Parse and evaluate the expression
        MyBean myBean = expression.getValue(context, MyBean.class);

        System.out.println("myBean1+myBean2 = " + myBean);
    }
}
