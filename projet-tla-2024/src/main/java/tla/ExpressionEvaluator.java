/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

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

    // Analyse l'expression et la convertit en notation postfixée
    public void parse(String expression) throws Exception {
        // Remplacer les virgules par des points pour supporter les deux notations décimales
        expression = expression.replace(',', '.');
        List<Token> tokens = lex.analyse(expression);
        postfixExpression = tokensToPostfix(tokens);
    }

    // Évalue l'expression avec une valeur donnée pour x
    public double evaluate(double x) throws Exception {
        if (postfixExpression == null) {
            throw new Exception("Expression not parsed. Call parse() before evaluate.");
        }
        stack.clear();
        for (String token : postfixExpression.split(" ")) {
            System.out.println("Processing token: " + token); // Ligne de débogage
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token)); // Ajouter le nombre à la pile
            } else if (token.equals("x")) {
                stack.push(x); // Ajouter la valeur de x à la pile
            } else {
                if (token.equals("abs") || token.equals("sin") || token.equals("cos") || token.equals("tan") || token.equals("exp")) {
                    if (stack.size() < 1) {
                        throw new Exception("Invalid expression: not enough operands for operator " + token);
                    }
                    double a = stack.pop();
                    switch (token) {
                        case "abs":
                            stack.push(Math.abs(a)); // Calculer la valeur absolue
                            break;
                        case "sin":
                            stack.push(Math.sin(a)); // Calculer le sinus
                            break;
                        case "cos":
                            stack.push(Math.cos(a)); // Calculer le cosinus
                            break;
                        case "tan":
                            stack.push(Math.tan(a)); // Calculer la tangente
                            break;
                        case "exp":
                            stack.push(Math.exp(a)); // Calculer l'exponentielle
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
                        case "add": // Support pour "add"
                            stack.push(a + b); // Calculer l'addition
                            break;
                        case "*":
                        case "multiply": // Support pour "multiply"
                            stack.push(a * b); // Calculer la multiplication
                            break;
                        case "/":
                        case "divide": // Support pour "divide"
                            stack.push(a / b); // Calculer la division
                            break;
                        case "-":
                        case "subtract": // Support pour "subtract"
                            stack.push(a - b); // Calculer la soustraction
                            break;
                        case "^":
                        case "k_pow": // Support pour "k_pow"
                            stack.push(Math.pow(a, b)); // Calculer la puissance
                            break;
                        default:
                            throw new Exception("Unknown operator: " + token);
                    }
                }
            }
            System.out.println("Stack after processing token: " + stack); // Ligne de débogage
        }
        if (stack.size() != 1) {
            throw new Exception("Invalid expression: stack size is not 1 after evaluation");
        }
        return stack.pop();
    }

    // Évalue l'AST avec une valeur donnée pour x
    public double evaluate(Noeud ast, double x) throws Exception {
        if (ast == null) {
            throw new Exception("AST non défini. Appelez parse() avant evaluate.");
        }
        System.out.println("Evaluating AST with x = " + x);
        return evaluerNoeud(ast, x);
    }

    // Évalue un noeud de l'AST
    private double evaluerNoeud(Noeud noeud, double x) throws Exception {
        System.out.println("Evaluating node: " + noeud);
        switch (noeud.getTypeDeNoeud()) {
            case intv:
                return Double.parseDouble(noeud.getValeur()); // Retourner la valeur entière
            case ident:
                if (noeud.getValeur().equals("x")) {
                    return x; // Retourner la valeur de x
                }
                throw new Exception("Identifiant inconnu: " + noeud.getValeur());
            case add:
                return evaluerNoeud(noeud.enfant(0), x) + evaluerNoeud(noeud.enfant(1), x); // Calculer l'addition
            case multiply:
                return evaluerNoeud(noeud.enfant(0), x) * evaluerNoeud(noeud.enfant(1), x); // Calculer la multiplication
            case kPow:
                return Math.pow(evaluerNoeud(noeud.enfant(0), x), evaluerNoeud(noeud.enfant(1), x)); // Calculer la puissance
            case abs:
                return Math.abs(evaluerNoeud(noeud.enfant(0), x)); // Calculer la valeur absolue
            case sin:
                return Math.sin(evaluerNoeud(noeud.enfant(0), x)); // Calculer le sinus
            case cos:
                return Math.cos(evaluerNoeud(noeud.enfant(0), x)); // Calculer le cosinus
            case tan:
                return Math.tan(evaluerNoeud(noeud.enfant(0), x)); // Calculer la tangente
            case exp:
                return Math.exp(evaluerNoeud(noeud.enfant(0), x)); // Calculer l'exponentielle
            case subtract:
                // Si le noeud droit est une soustraction, évaluer comme une addition
                if (noeud.enfant(1).getTypeDeNoeud() == TypeDeNoeud.subtract) {
                    return evaluerNoeud(noeud.enfant(0), x) + evaluerNoeud(noeud.enfant(1).enfant(1), x);
                }
                return evaluerNoeud(noeud.enfant(0), x) - evaluerNoeud(noeud.enfant(1), x); // Calculer la soustraction
            case divide:
                return evaluerNoeud(noeud.enfant(0), x) / evaluerNoeud(noeud.enfant(1), x); // Calculer la division
            default:
                throw new Exception("Type de noeud inconnu: " + noeud.getTypeDeNoeud());
        }
    }

    // Convertit une liste de tokens en notation postfixée
    private String tokensToPostfix(List<Token> tokens) {
        StringBuilder result = new StringBuilder();
        Stack<TypeDeToken> stack = new Stack<>();
        for (Token token : tokens) {
            switch (token.getTypeDeToken()) {
                case INTV:
                case IDENT:
                    result.append(token.getValeur()).append(' '); // Ajouter les valeurs directement au résultat
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
                        result.append(stack.pop().toString().toLowerCase()).append(' '); // Ajouter les opérateurs de la pile au résultat
                    }
                    stack.push(token.getTypeDeToken()); // Empiler l'opérateur actuel
                    break;
                case LEFT_PAR:
                    stack.push(token.getTypeDeToken()); // Empiler la parenthèse gauche
                    break;
                case RIGHT_PAR:
                    while (!stack.isEmpty() && stack.peek() != TypeDeToken.LEFT_PAR) {
                        result.append(stack.pop().toString().toLowerCase()).append(' '); // Ajouter les opérateurs jusqu'à la parenthèse gauche
                    }
                    stack.pop(); // Supprimer la parenthèse gauche
                    break;
                default:
                    throw new IllegalArgumentException("Unknown token type: " + token.getTypeDeToken());
            }
        }
        while (!stack.isEmpty()) {
            result.append(stack.pop().toString().toLowerCase()).append(' '); // Ajouter les opérateurs restants
        }
        return result.toString().trim();
    }

    // Détermine la priorité des opérateurs
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

    // Vérifie si une chaîne est numérique
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
