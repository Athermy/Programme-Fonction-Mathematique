/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.util.*;

public class ExpressionEvaluator {

    private Stack<Double> stack;
    private String postfixExpression;

    public ExpressionEvaluator() {
        stack = new Stack<>();
    }

    public void parse(String expression) throws Exception {
        postfixExpression = infixToPostfix(expression);
    }

    public double evaluate(double x) throws Exception {
        stack.clear();
        for (String token : postfixExpression.split(" ")) {
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if ("x".equals(token)) {
                stack.push(x);
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                    case "^": stack.push(Math.pow(a, b)); break;
                    default: throw new Exception("Unknown operator: " + token);
                }
            }
        }
        return stack.pop();
    }

    private String infixToPostfix(String expression) throws Exception {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                result.append(c);
            } else if (c == 'x') {
                result.append('x');
            } else if ("+-*/^".indexOf(c) >= 0) {
                result.append(' ');
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    result.append(stack.pop()).append(' ');
                }
                stack.push(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    result.append(' ').append(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new Exception("Mismatched parentheses");
                }
                stack.pop();
            } else {
                throw new Exception("Invalid character: " + c + "attentue");
            }
        }
        while (!stack.isEmpty()) {
            result.append(' ').append(stack.pop());
        }
        return result.toString();
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            case '^': return 3;
            default: return -1;
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
