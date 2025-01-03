package tla;

import java.util.List;
import java.util.Stack;

public class ExpressionEvaluator {

    private Stack<Double> stack;
    private String postfixExpression;
    private AnalyseLexicale lex;

    public ExpressionEvaluator() {
        stack = new Stack<>();
        lex = new AnalyseLexicale();
    }

    public void parse(String expression) throws Exception {
        // Replace commas with dots to support both decimal notations
        expression = expression.replace(',', '.');
        List<Token> tokens = lex.analyse(expression);
        postfixExpression = tokensToPostfix(tokens);
    }

    public double evaluate(double x) throws Exception {
        if (postfixExpression == null) {
            throw new Exception("Expression not parsed. Call parse() before evaluate.");
        }
        stack.clear();
        for (String token : postfixExpression.split(" ")) {
            System.out.println("Processing token: " + token); // Debugging line
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("x")) {
                stack.push(x);
            } else {
                if (token.equals("abs") || token.equals("sin") || token.equals("cos") || token.equals("tan") || token.equals("exp")) {
                    if (stack.size() < 1) {
                        throw new Exception("Invalid expression: not enough operands for operator " + token);
                    }
                    double a = stack.pop();
                    switch (token) {
                        case "abs":
                            stack.push(Math.abs(a));
                            break;
                        case "sin":
                            stack.push(Math.sin(a));
                            break;
                        case "cos":
                            stack.push(Math.cos(a));
                            break;
                        case "tan":
                            stack.push(Math.tan(a));
                            break;
                        case "exp":
                            stack.push(Math.exp(a));
                            break;
                        default:
                            throw new Exception("Unknown operator: " + token);
                    }
                } else {
                    if (stack.size() < 2) {
                        throw new Exception("Invalid expression: not enough operands for operator " + token);
                    }
                    double b = stack.pop();
                    double a = stack.pop();
                    switch (token) {
                        case "+":
                        case "add": // Add support for "add"
                            stack.push(a + b);
                            break;
                        case "*":
                        case "multiply": // Add support for "multiply"
                            stack.push(a * b);
                            break;
                        case "/":
                        case "divide": // Add support for "divide"
                            stack.push(a / b);
                            break;
                        case "-":
                        case "subtract": // Add support for "subtract"
                            stack.push(a - b);
                            break;
                        case "^":
                        case "k_pow": // Add support for "k_pow"
                            stack.push(Math.pow(a, b));
                            break;
                        default:
                            throw new Exception("Unknown operator: " + token);
                    }
                }
            }
            System.out.println("Stack after processing token: " + stack); // Debugging line
        }
        if (stack.size() != 1) {
            throw new Exception("Invalid expression: stack size is not 1 after evaluation");
        }
        return stack.pop();
    }

    private String tokensToPostfix(List<Token> tokens) {
        StringBuilder result = new StringBuilder();
        Stack<TypeDeToken> stack = new Stack<>();
        for (Token token : tokens) {
            switch (token.getTypeDeToken()) {
                case INTV:
                case IDENT:
                    result.append(token.getValeur()).append(' ');
                    break;
                case ADD:
                case SUBTRACT:
                case MULTIPLY:
                case DIVIDE:
                case K_POW:
                case ABS:
                case SIN:
                case COS:
                case TAN:
                case EXP:
                    while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token.getTypeDeToken())) {
                        result.append(stack.pop().toString().toLowerCase()).append(' ');
                    }
                    stack.push(token.getTypeDeToken());
                    break;
                case LEFT_PAR:
                    stack.push(token.getTypeDeToken());
                    break;
                case RIGHT_PAR:
                    while (!stack.isEmpty() && stack.peek() != TypeDeToken.LEFT_PAR) {
                        result.append(stack.pop().toString().toLowerCase()).append(' ');
                    }
                    stack.pop(); // Remove the left parenthesis
                    break;
                default:
                    throw new IllegalArgumentException("Unknown token type: " + token.getTypeDeToken());
            }
        }
        while (!stack.isEmpty()) {
            result.append(stack.pop().toString().toLowerCase()).append(' ');
        }
        return result.toString().trim();
    }

    private int precedence(TypeDeToken token) {
        switch (token) {
            case ADD:
            case SUBTRACT:
                return 1;
            case MULTIPLY:
            case DIVIDE:
                return 2;
            case K_POW:
                return 3;
            case ABS:
            case SIN:
            case COS:
            case TAN:
            case EXP:
                return 4;
            default:
                return 0;
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
