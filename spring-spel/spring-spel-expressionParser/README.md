## ExpressionParser


- [ExpressionParser](#expressionparser)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Sequence Diagram](#8-sequence-diagram)
  - [9. Source Code Analysis](#9-source-code-analysis)
  - [10. Relationship with Other Components](#10-relationship-with-other-components)
  - [11. FAQ](#11-faq)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **XML and/or annotation configuration**

   + You should know how to configure Spring applications with XML or annotations, because SpEL is often used in these configurations.

2. **SpEL syntax**

   + SpEL has its own syntax rules. You need to know them to write valid SpEL expressions.

3. **AOP and event handling**

   + They are not required. But knowing AOP and event handling in Spring helps you understand how SpEL is used in complex cases.

### 3. Description

The `ExpressionParser` interface is a key component in the Spring Framework. It parses and runs expressions of the Spring Expression Language (SpEL). SpEL is a rich expression language. It lets you work with and query the object graph at runtime. With the `ExpressionParser` interface, we can turn an expression string into an `Expression` object. Then we can do many things with it, such as getting a value, setting a value, or running a complex expression.

### 4. Main Features

1. **Parse expressions**

   + It parses a text expression string and turns it into an `Expression` object. This is its core feature. It lets you embed dynamic expressions in an application.

2. **Evaluate expressions**

   + With the parsed `Expression` object, you can evaluate the expression to get its runtime value. This can mean accessing object properties, calling methods, using logical operators and more.

3. **Set expression values**

   + Besides getting the value of an expression, `ExpressionParser` can also change properties of a target object. With an expression, you can set the value of a property of an object directly.

4. **Type conversion**

   + It also supports type conversion. During evaluation, a value is converted from one type to another automatically.

5. **Functions and operators**

   + `ExpressionParser` supports many functions and operators, such as math operations, string operations and logical operations.

6. **Collection operations**

   + It supports operations on collections and arrays, including selection, projection and aggregation.

7. **Conditional expressions**

   + It can handle conditional expressions, such as an if-then-else structure. This gives stronger decision making.

8. **Template parsing**

   + It can handle expressions with template text, for example joining a string with a dynamic expression.

### 5. Interface Source

The `ExpressionParser` interface turns an expression string into an `Expression` object that can be evaluated. It supports parsing standard expressions and templates. It has two methods to parse expressions. One is basic parsing. The other allows more flexible parsing through a context.

```java
/**
 * Parses expression strings into compiled expressions that can be evaluated.
 * Supports parsing templates as well as standard expression strings.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
public interface ExpressionParser {

	/**
	 * Parse the expression string and return an Expression object you can use for repeated evaluation.
	 * <p>Some examples:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString the raw expression string to parse
	 * @return an evaluator for the parsed expression
	 * @throws ParseException an exception occurred during parsing
	 */
	Expression parseExpression(String expressionString) throws ParseException;

	/**
	 * Parse the expression string and return an Expression object you can use for repeated evaluation.
	 * <p>Some examples:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString the raw expression string to parse
	 * @param context a context for influencing this expression parsing routine (optional)
	 * @return an evaluator for the parsed expression
	 * @throws ParseException an exception occurred during parsing
	 */
	Expression parseExpression(String expressionString, ParserContext context) throws ParseException;

}
```

### 6. Main Implementations

1. **SpelExpressionParser**

   + This is the most used implementation. It fully supports the Spring Expression Language (SpEL). It can handle many complex expressions, such as method calls, property access and relational operators.

### 7. Best Practices

First, this example creates a SpEL expression parser instance, `SpelExpressionParser`. Then it uses the parser to parse a simple math expression `"100 * 2 + 10"`. Finally, it prints the `Expression`.

```java
public class ExpressionParserDemo {

    public static void main(String[] args) {
        // Create the parser instance
        ExpressionParser parser = new SpelExpressionParser();
        // Parse a basic expression
        Expression expression = parser.parseExpression("100 * 2 + 10");

        System.out.println("expression = " + expression);
    }
}
```

Output: `SpelExpressionParser` parses the given expression and returns a `SpelExpression` object. This object holds the internal form of the expression. It can be used later to evaluate the expression.

```java
expression = org.springframework.expression.spel.standard.SpelExpression@754ba872
```

### 8. Sequence Diagram

~~~mermaid
sequenceDiagram
    autonumber
    title: ExpressionParser Sequence Diagram

    ExpressionParserDemo->>TemplateAwareExpressionParser: parseExpression(expressionString)
    TemplateAwareExpressionParser->>TemplateAwareExpressionParser: parseExpression(expressionString, context)
    TemplateAwareExpressionParser->>SpelExpressionParser: doParseExpression(expressionString, context)
    SpelExpressionParser->>InternalSpelExpressionParser: new InternalSpelExpressionParser(this.configuration)
    InternalSpelExpressionParser->>SpelExpressionParser: Return parser
    SpelExpressionParser->>InternalSpelExpressionParser: doParseExpression(expressionString, context)
    InternalSpelExpressionParser->>Tokenizer: new Tokenizer(expressionString)
    Note over InternalSpelExpressionParser,Tokenizer: Turn the character stream into a token stream
    Tokenizer->>InternalSpelExpressionParser: Return tokenizer
    InternalSpelExpressionParser->>Tokenizer: process()
    Note over InternalSpelExpressionParser,Tokenizer: Lexical analysis
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatExpression()
    Note over InternalSpelExpressionParser: Build the abstract syntax tree
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatLogicalOrExpression
    Note over InternalSpelExpressionParser: Parse the logical OR expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatLogicalAndExpression
    Note over InternalSpelExpressionParser: Parse the logical AND expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatRelationalExpression
    Note over InternalSpelExpressionParser: Parse the relational expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatSumExpression
    Note over InternalSpelExpressionParser: Parse the addition and subtraction expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatProductExpression
    Note over InternalSpelExpressionParser: Parse the multiplication and division expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatPowerIncDecExpression
    Note over InternalSpelExpressionParser: Parse the power, increment and decrement expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatUnaryExpression
    Note over InternalSpelExpressionParser: Parse the unary expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatPrimaryExpression
    Note over InternalSpelExpressionParser: Parse the primary expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatStartNode
    Note over InternalSpelExpressionParser: Parse the start node
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatLiteral
    Note over InternalSpelExpressionParser: Try to parse a literal
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatParenExpression
    Note over InternalSpelExpressionParser: Try to parse a parenthesized expression
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatTypeReference
    Note over InternalSpelExpressionParser: Try to parse a type reference
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatNullReference
    Note over InternalSpelExpressionParser: Try to parse a null reference
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatConstructorReference
    Note over InternalSpelExpressionParser: Try to parse a constructor reference
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatBeanReference
    Note over InternalSpelExpressionParser: Try to parse a bean reference
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatProjection
    Note over InternalSpelExpressionParser: Try to parse a projection
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatSelection
    Note over InternalSpelExpressionParser: Try to parse a selection
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatIndexer
    Note over InternalSpelExpressionParser: Try to parse an indexer
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatInlineListOrMap
    Note over InternalSpelExpressionParser: Try to parse an inline list or map
    InternalSpelExpressionParser->>SpelExpressionParser: Return the expression object
    SpelExpressionParser->>TemplateAwareExpressionParser: Return the expression object
    TemplateAwareExpressionParser->>ExpressionParserDemo: Return the expression object

~~~



### 9. Source Code Analysis

The `org.springframework.expression.common.TemplateAwareExpressionParser#parseExpression(expressionString)` method takes an expression string as input. It calls the overloaded `parseExpression` method to do the real parsing.

```java
@Override
public Expression parseExpression(String expressionString) throws ParseException {
    return parseExpression(expressionString, null);
}
```

In the `org.springframework.expression.common.TemplateAwareExpressionParser#parseExpression(expressionString, context)` method, it first checks whether the given `ParserContext` is in template mode. If it is, it calls `parseTemplate` to parse the template expression. Otherwise, it calls `doParseExpression` to parse a normal expression.

```java
@Override
public Expression parseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
    if (context != null && context.isTemplate()) {
       return parseTemplate(expressionString, context);
    }
    else {
       return doParseExpression(expressionString, context);
    }
}
```

The `org.springframework.expression.spel.standard.SpelExpressionParser#doParseExpression` method overrides `doParseExpression`. It creates an `InternalSpelExpressionParser` object and calls its `doParseExpression` method to do the real SpEL expression parsing. Then it returns the `SpelExpression` object.

```java
@Override
protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
    return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#doParseExpression` method, it first splits the given expression string into tokens. Then it parses the expression by building an abstract syntax tree (AST). During parsing, it makes sure the AST is not null and all tokens are processed. Finally, it creates a new `SpelExpression` object from the AST and returns it.

```java
@Override
protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context)
        throws ParseException {

    try {
        // Set the current expression string
        this.expressionString = expressionString;

        // Split the expression string into tokens
        Tokenizer tokenizer = new Tokenizer(expressionString);
        this.tokenStream = tokenizer.process();
        this.tokenStreamLength = this.tokenStream.size();
        this.tokenStreamPointer = 0;

        // Clear the collection of constructed nodes
        this.constructedNodes.clear();

        // Build the abstract syntax tree (AST)
        SpelNodeImpl ast = eatExpression();

        // Make sure the AST is not null
        Assert.state(ast != null, "No node");

        // Check whether there are unprocessed tokens. If so, throw an exception
        Token t = peekToken();
        if (t != null) {
            throw new SpelParseException(t.startPos, SpelMessage.MORE_INPUT, toString(nextToken()));
        }

        // Make sure the collection of constructed nodes is empty
        Assert.isTrue(this.constructedNodes.isEmpty(), "At least one node expected");

        // Create and return the SpEL expression object
        return new SpelExpression(expressionString, ast, this.configuration);
    } catch (InternalParseException ex) {
        // Throw the cause of the internal parse exception
        throw ex.getCause();
    }
}
```

The `org.springframework.expression.spel.standard.Tokenizer#Tokenizer` constructor takes a string as input data and creates a `Tokenizer` instance. The input data is stored in the field `expressionString`. A null character is added to the end of the input, so the end of the input can be recognized correctly during processing. Then the input is turned into a character array and assigned to `charsToProcess`. The length of the array is stored, and the position pointer `pos` is set to 0, the start of the input.

```java
public Tokenizer(String inputData) {
    this.expressionString = inputData;
    this.charsToProcess = (inputData + "\0").toCharArray();
    this.max = this.charsToProcess.length;
    this.pos = 0;
}
```

The `org.springframework.expression.spel.standard.Tokenizer#process` method processes the input string and produces a list of tokens. It loops over each character of the input and acts by the kind of character. It reads identifiers from letters. It makes tokens from operators and separators. It parses number literals. It skips whitespace. It handles string literals in quotes. And it handles reaching the end of the string. At the end, it returns the list of tokens.

```java
public List<Token> process() {
    // Loop over the input characters until the end of the input string
    while (this.pos < this.max) {
        // Get the character at the current position
        char ch = this.charsToProcess[this.pos];
        // If it is a letter, parse an identifier
        if (isAlphabetic(ch)) {
            lexIdentifier();
        } else {
            // Handle operators and separators
            switch (ch) {
                case '+':
                    // Handle the "+" sign
                    if (isTwoCharToken(TokenKind.INC)) {
                        pushPairToken(TokenKind.INC);
                    } else {
                        pushCharToken(TokenKind.PLUS);
                    }
                    break;
                // Other cases are omitted...
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    // Parse a number literal
                    lexNumericLiteral(ch == '0');
                    break;
                // Handle space, tab, carriage return and line feed
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    // Ignore these characters
                    this.pos++;
                    break;
                case '\'':
                    // Parse a string literal in single quotes
                    lexQuotedStringLiteral();
                    break;
                case '"':
                    // Parse a string literal in double quotes
                    lexDoubleQuotedStringLiteral();
                    break;
                case 0:
                    // Reached the end of the input string
                    this.pos++; // Move the pointer to the end of the string
                    break;
                case '\\':
                    // Throw an exception: unsupported escape character
                    raiseParseException(this.pos, SpelMessage.UNEXPECTED_ESCAPE_CHAR);
                    break;
                default:
                    // Throw an exception: a character that cannot be handled
                    throw new IllegalStateException("Cannot handle (" + (int) ch + ") '" + ch + "'");
            }
        }
    }
    // Return the list of tokens
    return this.tokens;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatExpression` method, it first calls `eatLogicalOrExpression` to parse the logical OR expression. Then it checks the type of the next token. If the next token is the assignment operator `=`, it builds an assignment node `Assign`. If it is the Elvis operator `?:`, it builds an Elvis node. If it is the conditional operator `?`, it builds a ternary expression node `Ternary`.

```java
@Nullable
private SpelNodeImpl eatExpression() {
    // Parse the logical OR expression
    SpelNodeImpl expr = eatLogicalOrExpression();
    // Get the next token
    Token t = peekToken();
    if (t != null) {
        // If the next token is the assignment operator "="
        if (t.kind == TokenKind.ASSIGN) {  // a=b
            // If the logical OR expression is null, build a null literal node
            if (expr == null) {
                expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
            }
            // Consume the assignment operator
            nextToken();
            // Parse the logical OR expression on the right of the assignment operator
            SpelNodeImpl assignedValue = eatLogicalOrExpression();
            // Build the assignment node
            return new Assign(t.startPos, t.endPos, expr, assignedValue);
        }
        // If the next token is the Elvis operator "?:"
        if (t.kind == TokenKind.ELVIS) {  // a?:b (a if it isn't null, otherwise b)
            // If the logical OR expression is null, build a null literal node
            if (expr == null) {
                expr = new NullLiteral(t.startPos - 1, t.endPos - 2);
            }
            // Consume the Elvis operator
            nextToken();  // elvis has left the building
            // Parse the expression on the right of the Elvis operator
            SpelNodeImpl valueIfNull = eatExpression();
            // If the right expression is null, build a null literal node
            if (valueIfNull == null) {
                valueIfNull = new NullLiteral(t.startPos + 1, t.endPos + 1);
            }
            // Build the Elvis node
            return new Elvis(t.startPos, t.endPos, expr, valueIfNull);
        }
        // If the next token is the conditional operator "?"
        if (t.kind == TokenKind.QMARK) {  // a?b:c
            // If the logical OR expression is null, build a null literal node
            if (expr == null) {
                expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
            }
            // Consume the conditional operator
            nextToken();
            // Parse the expression on the right of the conditional operator
            SpelNodeImpl ifTrueExprValue = eatExpression();
            // Consume the colon separator
            eatToken(TokenKind.COLON);
            // Parse the third operand of the conditional operator
            SpelNodeImpl ifFalseExprValue = eatExpression();
            // Build the ternary expression node
            return new Ternary(t.startPos, t.endPos, expr, ifTrueExprValue, ifFalseExprValue);
        }
    }
    // Return the parsed expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatLogicalOrExpression` method, it first calls `eatLogicalAndExpression` to parse the logical AND expression. Then in a loop it checks whether the next token is the keyword "or" or the symbol "||". If so, it consumes the token, parses the right operand as a logical AND expression, and builds a logical OR node.

```java
@Nullable
private SpelNodeImpl eatLogicalOrExpression() {
    // Parse the logical AND expression
    SpelNodeImpl expr = eatLogicalAndExpression();
    // Loop to parse the logical OR operations
    while (peekIdentifierToken("or") || peekToken(TokenKind.SYMBOLIC_OR)) {
        // Get the current operator token and consume it
        Token t = takeToken();  //consume OR
        // Parse the right operand of the logical AND expression
        SpelNodeImpl rhExpr = eatLogicalAndExpression();
        // Check that the operands are valid
        checkOperands(t, expr, rhExpr);
        // Build the logical OR node
        expr = new OpOr(t.startPos, t.endPos, expr, rhExpr);
    }
    // Return the parsed logical OR expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatLogicalAndExpression` method, it first calls `eatRelationalExpression` to parse the relational expression. Then in a loop it checks whether the next token is the keyword "and" or the symbol "&&". If so, it consumes the token, parses the right operand as a relational expression, and builds a logical AND node.

```java
@Nullable
private SpelNodeImpl eatLogicalAndExpression() {
    // Parse the relational expression
    SpelNodeImpl expr = eatRelationalExpression();
    // Loop to parse the logical AND operations
    while (peekIdentifierToken("and") || peekToken(TokenKind.SYMBOLIC_AND)) {
        // Get the current operator token and consume it
        Token t = takeToken();  // consume 'AND'
        // Parse the right operand of the relational expression
        SpelNodeImpl rhExpr = eatRelationalExpression();
        // Check that the operands are valid
        checkOperands(t, expr, rhExpr);
        // Build the logical AND node
        expr = new OpAnd(t.startPos, t.endPos, expr, rhExpr);
    }
    // Return the parsed logical AND expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatRelationalExpression` method, it first calls `eatSumExpression` to parse the sum expression. Then it tries to consume a relational operator. If there is one, it parses the right operand and builds the matching node for the operator type. The operators are greater than, less than, greater than or equal, less than or equal, equal, not equal, instanceof, matches and between.

```java
@Nullable
private SpelNodeImpl eatRelationalExpression() {
    // Parse the sum expression
    SpelNodeImpl expr = eatSumExpression();
    // Try to consume a relational operator if there is one
    Token relationalOperatorToken = maybeEatRelationalOperator();
    if (relationalOperatorToken != null) {
       // Consume the relational operator
       Token t = takeToken();  // Consume the relational operator token
       // Parse the right operand
       SpelNodeImpl rhExpr = eatSumExpression();
       // Check that the operands are valid
       checkOperands(t, expr, rhExpr);
       TokenKind tk = relationalOperatorToken.kind;

       // Build the matching node for the operator type
       if (relationalOperatorToken.isNumericRelationalOperator()) {
          if (tk == TokenKind.GT) {
             return new OpGT(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.LT) {
             return new OpLT(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.LE) {
             return new OpLE(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.GE) {
             return new OpGE(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.EQ) {
             return new OpEQ(t.startPos, t.endPos, expr, rhExpr);
          }
          Assert.isTrue(tk == TokenKind.NE, "Not-equals token expected");
          return new OpNE(t.startPos, t.endPos, expr, rhExpr);
       }
       // Handle the instanceof operator
       if (tk == TokenKind.INSTANCEOF) {
          return new OperatorInstanceof(t.startPos, t.endPos, expr, rhExpr);
       }

       // Handle the matches operator
       if (tk == TokenKind.MATCHES) {
          return new OperatorMatches(t.startPos, t.endPos, expr, rhExpr);
       }

       // Handle the between operator
       Assert.isTrue(tk == TokenKind.BETWEEN, "Between token expected");
       return new OperatorBetween(t.startPos, t.endPos, expr, rhExpr);
    }
    // Return the parsed relational expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatSumExpression` method, it first calls `eatProductExpression` to parse the product expression. Then in a loop it checks whether the next token is an addition, subtraction or increment operator. If so, it consumes the token, parses the right operand as a product expression, and builds the matching node for the operator type (an addition or subtraction node).

```java
@Nullable
private SpelNodeImpl eatSumExpression() {
    // Parse the product expression
    SpelNodeImpl expr = eatProductExpression();
    // Loop to parse the sum expressions
    while (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.INC)) {
       // Get the current operator token and consume it
       Token t = takeToken();  // Consume the PLUS, MINUS or INC token
       // Parse the right operand of the product expression
       SpelNodeImpl rhExpr = eatProductExpression();
       // Check that the right operand is valid
       checkRightOperand(t, rhExpr);
       // Build the matching node for the operator type
       if (t.kind == TokenKind.PLUS) {
          // Build the addition node
          expr = new OpPlus(t.startPos, t.endPos, expr, rhExpr);
       }
       else if (t.kind == TokenKind.MINUS) {
          // Build the subtraction node
          expr = new OpMinus(t.startPos, t.endPos, expr, rhExpr);
       }
    }
    // Return the parsed sum expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatProductExpression` method, it first calls `eatPowerIncDecExpression` to parse the power or increment/decrement expression. Then in a loop it checks whether the next token is a multiplication, division or modulus operator. If so, it consumes the token, parses the right operand as a power or increment/decrement expression, and builds the matching node for the operator type (a multiplication, division or modulus node).

```java
@Nullable
private SpelNodeImpl eatProductExpression() {
    // Parse the power or increment/decrement expression
    SpelNodeImpl expr = eatPowerIncDecExpression();
    // Loop to parse the product expressions
    while (peekToken(TokenKind.STAR, TokenKind.DIV, TokenKind.MOD)) {
       // Get the current operator token and consume it
       Token t = takeToken();  // Consume the STAR, DIV or MOD token
       // Parse the right operand of the power or increment/decrement expression
       SpelNodeImpl rhExpr = eatPowerIncDecExpression();
       // Check that the operands are valid
       checkOperands(t, expr, rhExpr);
       // Build the matching node for the operator type
       if (t.kind == TokenKind.STAR) {
          // Build the multiplication node
          expr = new OpMultiply(t.startPos, t.endPos, expr, rhExpr);
       }
       else if (t.kind == TokenKind.DIV) {
          // Build the division node
          expr = new OpDivide(t.startPos, t.endPos, expr, rhExpr);
       }
       else {
          Assert.isTrue(t.kind == TokenKind.MOD, "Mod token expected");
          // Build the modulus node
          expr = new OpModulus(t.startPos, t.endPos, expr, rhExpr);
       }
    }
    // Return the parsed product expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatPowerIncDecExpression` method, it first calls `eatUnaryExpression` to parse the unary expression. Then it checks whether there is a power operator or an increment/decrement operator. If there is a power operator, it parses the unary expression on its right and builds a power node. If there is an increment/decrement operator, it builds the matching increment or decrement node by the operator type.

```java
@Nullable
private SpelNodeImpl eatPowerIncDecExpression() {
    // Parse the unary expression
    SpelNodeImpl expr = eatUnaryExpression();
    // Check whether there is a power operator
    if (peekToken(TokenKind.POWER)) {
       // Get and consume the power operator token
       Token t = takeToken();  // Consume POWER
       // Parse the unary expression on the right of the power operator
       SpelNodeImpl rhExpr = eatUnaryExpression();
       // Check that the right operand is valid
       checkRightOperand(t, rhExpr);
       // Build the power node
       return new OperatorPower(t.startPos, t.endPos, expr, rhExpr);
    }
    // If there is no power operator, but there is an increment/decrement operator
    if (expr != null && peekToken(TokenKind.INC, TokenKind.DEC)) {
       // Get and consume the increment/decrement operator token
       Token t = takeToken();  // Consume INC/DEC
       // Build the matching increment/decrement node by the operator type
       if (t.getKind() == TokenKind.INC) {
          return new OpInc(t.startPos, t.endPos, true, expr);
       }
       return new OpDec(t.startPos, t.endPos, true, expr);
    }
    // If there is neither a power operator nor an increment/decrement operator, return the parsed unary expression node
    return expr;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatUnaryExpression` method, it first checks whether the next token is a plus, minus or logical NOT operator. If so, it consumes the token, calls `eatUnaryExpression` recursively to parse the body of the expression, and builds the matching node for the operator type (plus, minus or logical NOT node). If the next token is an increment or decrement operator, it also consumes the token, calls `eatUnaryExpression` recursively to parse the body, and builds the matching increment or decrement node. If none of these apply, it calls `eatPrimaryExpression` to parse the primary expression.

```java
@Nullable
private SpelNodeImpl eatUnaryExpression() {
    // Check whether the next token is a plus, minus or logical NOT operator
    if (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.NOT)) {
        // Get and consume the operator token
        Token t = takeToken();
        // Parse the body of the expression
        SpelNodeImpl expr = eatUnaryExpression();
        // Assert that the expression is not null
        Assert.state(expr != null, "No node");
        // Build the matching node for the operator type
        if (t.kind == TokenKind.NOT) {
            // Build the logical NOT node
            return new OperatorNot(t.startPos, t.endPos, expr);
        }
        if (t.kind == TokenKind.PLUS) {
            // Build the plus node
            return new OpPlus(t.startPos, t.endPos, expr);
        }
        // If it is not a plus, it is a minus
        Assert.isTrue(t.kind == TokenKind.MINUS, "Minus token expected");
        // Build the minus node
        return new OpMinus(t.startPos, t.endPos, expr);
    }
    // If the next token is an increment or decrement operator
    if (peekToken(TokenKind.INC, TokenKind.DEC)) {
        // Get and consume the operator token
        Token t = takeToken();
        // Parse the body of the expression
        SpelNodeImpl expr = eatUnaryExpression();
        // Build the matching increment or decrement node by the operator type
        if (t.getKind() == TokenKind.INC) {
            // Build the increment node
            return new OpInc(t.startPos, t.endPos, false, expr);
        }
        // Otherwise it is a decrement node
        return new OpDec(t.startPos, t.endPos, false, expr);
    }
    // Parse the primary expression
    return eatPrimaryExpression();
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatPrimaryExpression` method, it first calls `eatStartNode` to parse the start node, and creates a list to store the nodes that follow. Then it loops and keeps calling `eatNode` to parse the following nodes, until no new node can be parsed. In the loop, if the node list is still empty, the start node is added to the list first. Then the parsed node is added to the list. At the end, if the start node or the node list is null, it returns the start node. Otherwise, it builds a compound expression node. Its range goes from the start position of the start node to the end position of the last node. It holds all the parsed nodes, and is returned.

```java
@Nullable
private SpelNodeImpl eatPrimaryExpression() {
    // Parse the start node, usually a single node
    SpelNodeImpl start = eatStartNode();  
    // The node list, used to store the following nodes
    List<SpelNodeImpl> nodes = null;
    // Parse a node
    SpelNodeImpl node = eatNode();
    // Loop to parse the following nodes until no new node can be parsed
    while (node != null) {
        // If the node list is empty, add the start node to the list
        if (nodes == null) {
            nodes = new ArrayList<>(4);
            nodes.add(start);
        }
        // Add the parsed node to the list
        nodes.add(node);
        // Keep parsing the next node
        node = eatNode();
    }
    // If the start node or the node list is null, return the start node
    if (start == null || nodes == null) {
        return start;
    }
    // Build a compound expression node that holds all the parsed nodes
    return new CompoundExpression(start.getStartPosition(), nodes.get(nodes.size() - 1).getEndPosition(),
          nodes.toArray(new SpelNodeImpl[0]));
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatStartNode` method, it tries to parse different kinds of nodes: a literal, a parenthesized expression, a type reference, a null reference, a constructor reference, a method or property, a function or variable, a bean reference, a projection, a selection, an indexer, and an inline list or map.

```java
@Nullable
private SpelNodeImpl eatStartNode() {
    // Try to parse a literal
    if (maybeEatLiteral()) {
       // Return the parsed literal node
       return pop();
    }
    // Try to parse a parenthesized expression
    else if (maybeEatParenExpression()) {
       // Return the parsed parenthesized expression node
       return pop();
    }
    // Try to parse a type reference, null reference, constructor reference, method or property, function or variable
    else if (maybeEatTypeReference() || maybeEatNullReference() || maybeEatConstructorReference() ||
          maybeEatMethodOrProperty(false) || maybeEatFunctionOrVar()) {
       // Return the parsed node
       return pop();
    }
    // Try to parse a bean reference
    else if (maybeEatBeanReference()) {
       // Return the parsed bean reference node
       return pop();
    }
    // Try to parse a projection, selection or indexer node
    else if (maybeEatProjection(false) || maybeEatSelection(false) || maybeEatIndexer()) {
       // Return the parsed node
       return pop();
    }
    // Try to parse an inline list or map node
    else if (maybeEatInlineListOrMap()) {
       // Return the parsed node
       return pop();
    }
    // If no node can be parsed, return null
    else {
       return null;
    }
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatLiteral` method, it first checks whether the next token exists. If not, it returns false. Then it acts by the token type. An integer literal becomes an integer literal node and is pushed on the stack. A long literal becomes a long literal node and is pushed. A hexadecimal integer literal becomes an integer or long literal node and is pushed. A real or float literal becomes a real or float literal node and is pushed. The boolean literal true pushes a boolean node for true. The boolean literal false pushes a boolean node for false. A string literal pushes a string literal node. If none of these apply, it returns false. At the end, it consumes the current token and returns true. This means the literal was parsed.

```java
private boolean maybeEatLiteral() {
    // Get the next token
    Token t = peekToken();
    // If the token is null, return that parsing failed
    if (t == null) {
        return false;
    }
    // Act by the token type
    if (t.kind == TokenKind.LITERAL_INT) {
        // Turn the integer literal into an integer literal node and push it
        push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 10));
    }
    else if (t.kind == TokenKind.LITERAL_LONG) {
        // Turn the long literal into a long literal node and push it
        push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 10));
    }
    else if (t.kind == TokenKind.LITERAL_HEXINT) {
        // Turn the hexadecimal integer literal into an integer literal node and push it
        push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 16));
    }
    else if (t.kind == TokenKind.LITERAL_HEXLONG) {
        // Turn the hexadecimal long literal into a long literal node and push it
        push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 16));
    }
    else if (t.kind == TokenKind.LITERAL_REAL) {
        // Turn the real literal into a real literal node and push it
        push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, false));
    }
    else if (t.kind == TokenKind.LITERAL_REAL_FLOAT) {
        // Turn the float literal into a float literal node and push it
        push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, true));
    }
    else if (peekIdentifierToken("true")) {
        // Push a boolean node for true
        push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, true));
    }
    else if (peekIdentifierToken("false")) {
        // Push a boolean node for false
        push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, false));
    }
    else if (t.kind == TokenKind.LITERAL_STRING) {
        // Push a string literal node
        push(new StringLiteral(t.stringValue(), t.startPos, t.endPos, t.stringValue()));
    }
    else {
        // If the token type is not a literal, return that parsing failed
        return false;
    }
    // Consume the current token and return that parsing succeeded
    nextToken();
    return true;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatParenExpression` method, it first checks whether the next token is a left parenthesis. If so, it consumes the token and parses the expression inside the parentheses. Then it checks that an expression node was parsed. If so, it consumes the right parenthesis, pushes the expression node on the stack, and returns true. If the next token is not a left parenthesis, it returns false directly.

```java
private boolean maybeEatParenExpression() {
    // If the next token is a left parenthesis
    if (peekToken(TokenKind.LPAREN)) {
        // Consume the left parenthesis token
        nextToken();
        // Parse the expression inside the parentheses
        SpelNodeImpl expr = eatExpression();
        // Assert that the expression node is not null
        Assert.state(expr != null, "No node");
        // Consume the right parenthesis token
        eatToken(TokenKind.RPAREN);
        // Push the expression node on the stack
        push(expr);
        // Return that parsing succeeded
        return true;
    }
    else {
        // If the next token is not a left parenthesis, return that parsing failed
        return false;
    }
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatTypeReference` method, it first checks whether the next token is an identifier. If so, it checks whether the identifier is "T". If not, it returns false. If the identifier is "T", it checks whether the next token is a right square bracket. If so, "T" is treated as a map key: it creates a property or field reference node, pushes it, and returns true. If the next token is not a right square bracket, it parses the content inside the parentheses, including a possibly qualified identifier and array dimensions. It builds a type reference node, pushes it, and returns true. If the next token is not an identifier, it returns false directly.

```java
private boolean maybeEatTypeReference() {
    // If the next token is an identifier
    if (peekToken(TokenKind.IDENTIFIER)) {
        // Get the next token
        Token typeName = peekToken();
        // Assert that the token is not null
        Assert.state(typeName != null, "Expected token");
        // If the identifier is not "T", return that parsing failed
        if (!"T".equals(typeName.stringValue())) {
            return false;
        }
        // Take the next token, to check further whether it is a map key
        Token t = takeToken();
        // If the next token is a right square bracket, treat "T" as a map key, create a property or field reference node, push it, and return that parsing succeeded
        if (peekToken(TokenKind.RSQUARE)) {
            // Create a property or field reference node for "T" as a map key
            push(new PropertyOrFieldReference(false, t.stringValue(), t.startPos, t.endPos));
            return true;
        }
        // Otherwise, keep parsing the content inside the parentheses
        // Consume the left parenthesis token
        eatToken(TokenKind.LPAREN);
        // Parse a possibly qualified identifier
        SpelNodeImpl node = eatPossiblyQualifiedId();
        // Check whether there are array dimensions
        int dims = 0;
        while (peekToken(TokenKind.LSQUARE, true)) {
            // Consume the left and right square bracket tokens, and count the array dimensions
            eatToken(TokenKind.RSQUARE);
            dims++;
        }
        // Consume the right parenthesis token
        eatToken(TokenKind.RPAREN);
        // Create the type reference node, push it, and return that parsing succeeded
        this.constructedNodes.push(new TypeReference(typeName.startPos, typeName.endPos, node, dims));
        return true;
    }
    // If the next token is not an identifier, return that parsing failed
    return false;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatNullReference` method, it first checks whether the next token is an identifier. If so, it checks whether the identifier is "null". If not, it returns false. If the identifier is "null", it parses it as a null literal node, pushes it on the stack, and returns true. If the next token is not an identifier, it returns false directly.

```java
private boolean maybeEatNullReference() {
    // If the next token is an identifier
    if (peekToken(TokenKind.IDENTIFIER)) {
        // Get the next token
        Token nullToken = peekToken();
        // Assert that the token is not null
        Assert.state(nullToken != null, "Expected token");
        // If the identifier is not "null", return that parsing failed
        if (!"null".equalsIgnoreCase(nullToken.stringValue())) {
            return false;
        }
        // Consume the "null" identifier token
        nextToken();
        // Create the null literal node, push it, and return that parsing succeeded
        this.constructedNodes.push(new NullLiteral(nullToken.startPos, nullToken.endPos));
        return true;
    }
    // If the next token is not an identifier, return that parsing failed
    return false;
}
```

In the `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatConstructorReference` method, it first checks whether the next token is the keyword "new". If so, it keeps parsing the possible forms of a constructor reference. If "new" is followed by a right square bracket, "new" is treated as a map key: it creates a property or field reference node, pushes it, and returns true. If "new" is followed by other tokens, it parses the constructor reference, including a possibly qualified constructor name and the constructor argument list. If the arguments contain an array initializer, it parses the array dimensions and the inline list or map, creates a constructor reference node, pushes it, and returns true. If they do not contain an array initializer, it parses the constructor argument list, creates a constructor reference node, pushes it, and returns true. If the next token is not the keyword "new", it returns false directly.

```java
private boolean maybeEatConstructorReference() {
    // If the next token is the keyword "new"
    if (peekIdentifierToken("new")) {
        // Get the "new" token
        Token newToken = takeToken();
        // If "new" is followed by a right square bracket, treat "new" as a map key
        if (peekToken(TokenKind.RSQUARE)) {
            // Create a property or field reference node, push it, and return that parsing succeeded
            push(new PropertyOrFieldReference(false, newToken.stringValue(), newToken.startPos, newToken.endPos));
            return true;
        }
        // Keep parsing the possible forms of a constructor reference
        SpelNodeImpl possiblyQualifiedConstructorName = eatPossiblyQualifiedId();
        List<SpelNodeImpl> nodes = new ArrayList<>();
        nodes.add(possiblyQualifiedConstructorName);
        // If the constructor arguments contain an array initializer
        if (peekToken(TokenKind.LSQUARE)) {
            // Parse the array dimensions and the inline list or map, create a constructor reference node, push it, and return that parsing succeeded
            eatConstructorArgs(nodes);
            push(new ConstructorReference(newToken.startPos, newToken.endPos, nodes.toArray(new SpelNodeImpl[0])));
        } else {
            // Keep parsing the constructor argument list, create a constructor reference node, push it, and return that parsing succeeded
            eatConstructorArgs(nodes);
            // Create the constructor reference node, push it, and return that parsing succeeded
            push(new ConstructorReference(newToken.startPos, newToken.endPos, nodes.toArray(new SpelNodeImpl[0])));
        }
        return true;
    }
    // If the next token is not the keyword "new", return that parsing failed
    return false;
}
```

The `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatBeanReference` method tries to parse a bean reference. If the next token is `TokenKind.BEAN_REF` or `TokenKind.FACTORY_BEAN_REF`, it is treated as a bean reference. Then it tries to get the bean name, which can be an identifier or a string literal. After parsing, it creates a `BeanReference` instance by the bean reference type and pushes it on the stack. Finally, it returns the parse result.

```java
private boolean maybeEatBeanReference() {
    // If the next token is BEAN_REF or FACTORY_BEAN_REF
    if (peekToken(TokenKind.BEAN_REF) || peekToken(TokenKind.FACTORY_BEAN_REF)) {
        // Get the BEAN_REF or FACTORY_BEAN_REF token
        Token beanRefToken = takeToken();
        Token beanNameToken = null;
        String beanName = null;
        // If the next token is an identifier, it is the bean name
        if (peekToken(TokenKind.IDENTIFIER)) {
            // Take the identifier token as the bean name
            beanNameToken = eatToken(TokenKind.IDENTIFIER);
            beanName = beanNameToken.stringValue();
        }
        // If the next token is a string literal, it is the bean name
        else if (peekToken(TokenKind.LITERAL_STRING)) {
            // Take the string literal token as the bean name
            beanNameToken = eatToken(TokenKind.LITERAL_STRING);
            beanName = beanNameToken.stringValue();
            // Remove the quotes on both sides of the string to get the real bean name
            beanName = beanName.substring(1, beanName.length() - 1);
        }
        // If the bean name is neither an identifier nor a string literal, throw an exception
        else {
            throw internalException(beanRefToken.startPos, SpelMessage.INVALID_BEAN_REFERENCE);
        }
        BeanReference beanReference;
        // Create the BeanReference instance by the bean reference type
        if (beanRefToken.getKind() == TokenKind.FACTORY_BEAN_REF) {
            // For FACTORY_BEAN_REF, add the prefix string before the bean name
            String beanNameString = String.valueOf(TokenKind.FACTORY_BEAN_REF.tokenChars) + beanName;
            beanReference = new BeanReference(beanRefToken.startPos, beanNameToken.endPos, beanNameString);
        } else {
            // For BEAN_REF, use the bean name directly
            beanReference = new BeanReference(beanNameToken.startPos, beanNameToken.endPos, beanName);
        }
        // Push the BeanReference instance on the stack: parsing succeeded
        this.constructedNodes.push(beanReference);
        return true;
    }
    // If the next token is not BEAN_REF or FACTORY_BEAN_REF, return that parsing failed
    return false;
}
```

The `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatProjection` method tries to parse the projection operator. The projection operator projects a collection and returns a new collection. Each element of the new collection is a transformation of an element of the original collection. If the current token is PROJECT, it parses the expression and creates a Projection node. This means parsing succeeded.

```java
private boolean maybeEatProjection(boolean nullSafeNavigation) {
    Token t = peekToken(); // Get the current token
    // Check whether the current token is the projection operator. If not, return false
    if (!peekToken(TokenKind.PROJECT, true)) {
        return false;
    }
    // Assert that the current token is not null
    Assert.state(t != null, "No token"); 
    // Parse the expression after the projection operator
    SpelNodeImpl expr = eatExpression(); 
    // Assert that the expression node is not null
    Assert.state(expr != null, "No node"); 
    // Parse the right square bracket
    eatToken(TokenKind.RSQUARE); 
    // Create the Projection node and push it on the node stack
    this.constructedNodes.push(new Projection(nullSafeNavigation, t.startPos, t.endPos, expr));
    return true; // The projection operator was parsed, return true
}

```

The `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatSelection` method checks whether a selection operator (such as "![...]") is present. If it is, it parses the expression of the selection operator. Then it creates the matching Selection node by the type of the selection operator (FIRST, LAST or ALL) and pushes it on the node stack. It returns true if the selection operator was parsed, and false otherwise.

```java
private boolean maybeEatSelection(boolean nullSafeNavigation) {
    Token t = peekToken();
    // If the current token is not a selection operator, return false
    if (!peekSelectToken()) {
        return false;
    }
    Assert.state(t != null, "No token");
    // Consume the selection operator token
    nextToken();
    // Parse the selection expression
    SpelNodeImpl expr = eatExpression();
    // If the expression is null, throw an exception
    if (expr == null) {
        throw internalException(t.startPos, SpelMessage.MISSING_SELECTION_EXPRESSION);
    }
    // Consume the right square bracket token
    eatToken(TokenKind.RSQUARE);
    // Create the matching selection node by the selection operator and push it on the node stack
    if (t.kind == TokenKind.SELECT_FIRST) {
        this.constructedNodes.push(new Selection(nullSafeNavigation, Selection.FIRST, t.startPos, t.endPos, expr));
    }
    else if (t.kind == TokenKind.SELECT_LAST) {
        this.constructedNodes.push(new Selection(nullSafeNavigation, Selection.LAST, t.startPos, t.endPos, expr));
    }
    else {
        this.constructedNodes.push(new Selection(nullSafeNavigation, Selection.ALL, t.startPos, t.endPos, expr));
    }
    return true;
}
```

The `org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatIndexer` method tries to parse an indexer expression. If the current token is a left square bracket, it parses the expression as the index of the indexer and pushes it on the node stack.

```java
private boolean maybeEatIndexer() {
    // Get the current token
    Token t = peekToken();
    // If the current token is not '[', return false
    if (!peekToken(TokenKind.LSQUARE, true)) { 
        return false;
    }
    // Assert that the current token is not null
    Assert.state(t != null, "No token"); 
    // Parse the index expression
    SpelNodeImpl expr = eatExpression();
    // Assert that the index expression is not null
    Assert.state(expr != null, "No node"); 
    // Parse ']'
    eatToken(TokenKind.RSQUARE);
    // Create the indexer node and push it on the stack
    this.constructedNodes.push(new Indexer(t.startPos, t.endPos, expr));
    return true;
}
```

This method tries to parse an inline list or map literal. By the current token, it can parse an empty list, an empty map, or a list or map with one or more expressions. It parses the expressions one by one and checks the tokens that follow. Then it builds the matching inline list or map node and pushes it on the node stack.

```java
private boolean maybeEatInlineListOrMap() {
    // Get the current token
    Token t = peekToken(); 
    // If the current token is not '{', return false
    if (!peekToken(TokenKind.LCURLY, true)) { 
       return false;
    }
    // Assert that the current token is not null
    Assert.state(t != null, "No token"); 
    // Initialize the node
    SpelNodeImpl expr = null; 
    // Get the next token
    Token closingCurly = peekToken(); 
    // If the next token is '}', it is an empty list '{}'
    if (peekToken(TokenKind.RCURLY, true)) { 
       // Assert that the next token is not null
       Assert.state(closingCurly != null, "No token"); 
       // Create an empty inline list
       expr = new InlineList(t.startPos, closingCurly.endPos);
    }
    // If the next token is ':', it is an empty map '{:}'
    else if (peekToken(TokenKind.COLON, true)) { 
       // Get '}'
       closingCurly = eatToken(TokenKind.RCURLY);
       // Create an empty inline map
       expr = new InlineMap(t.startPos, closingCurly.endPos);
    }
    // If it is not an empty list or an empty map, keep parsing the expressions
    else { 
       // Parse the first expression
       SpelNodeImpl firstExpression = eatExpression();
       // The next token may be '}'
       if (peekToken(TokenKind.RCURLY)) {  
          // Create a list with only one element
          List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          closingCurly = eatToken(TokenKind.RCURLY);
          expr = new InlineList(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
       }
       // The next token may be a comma ',', which means a list with several elements
       else if (peekToken(TokenKind.COMMA, true)) {  
          // Create a list with several elements
          List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          do {
             elements.add(eatExpression());
          }
          while (peekToken(TokenKind.COMMA, true));
          closingCurly = eatToken(TokenKind.RCURLY);
          expr = new InlineList(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
       }
       // The next token may be a colon ':', which means a map
       else if (peekToken(TokenKind.COLON, true)) { 
          // Create a map
          List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          elements.add(eatExpression());
          while (peekToken(TokenKind.COMMA, true)) {
             elements.add(eatExpression());
             eatToken(TokenKind.COLON);
             elements.add(eatExpression());
          }
          closingCurly = eatToken(TokenKind.RCURLY);
          expr = new InlineMap(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
       }
       // If none of the above applies, throw an exception
       else {
          throw internalException(t.startPos, SpelMessage.OOD);
       }
    }
    // Push the parsed node on the stack
    this.constructedNodes.push(expr);
    return true;
}
```

### 10. Relationship with Other Components

1. **Expression**
   + When an expression string is parsed, `SpelExpressionParser` returns an object that implements the `Expression` interface. These objects represent the parsed expression. You can use them to get the value of the expression, set a value, or run the expression.
   
2. **EvaluationContext**

   + When an expression is evaluated, you can use an object that implements the `EvaluationContext` interface, such as `StandardEvaluationContext`, to give the runtime context information of the expression (for example variable definitions). This makes the expression more dynamic and flexible.

3. **ParseException&EvaluationException**

   + These classes handle the exceptions that may happen when an expression is parsed and evaluated. If an error happens while parsing or evaluating an expression, these exceptions are thrown.

4. **PropertyAccessor&MethodResolver**

   + These classes and interfaces resolve object properties and method calls when an expression is evaluated. They let `SpelExpressionParser` work with the properties and methods of Java objects, to build complex logic.

5. **TypeConverter**

   + It converts types while an expression is evaluated, so values can be converted freely between different types.

### 11. FAQ

1. **Expression syntax errors**

   + Common errors when you write a SpEL expression are typos, and wrong use of symbols or operators. These errors usually throw a `ParseException` when the expression is parsed.

2. **Performance problems**

   + Parsing and evaluating complex SpEL expressions often can hurt application performance. Caching the parsed expression objects helps with this problem.

3. **Context variable not found**

   + If an expression uses a variable that is not defined in the context, an exception is thrown. Make sure all variables used in the expression are defined in the context.

4. **Type conversion problems**

   + During evaluation, types may not match or may not convert correctly. This causes an `EvaluationException`.

5. **Property or method access problems**

   + Accessing a property that does not exist, or calling a method that does not exist, throws an exception. This may be because of a typo or a wrong object type.
