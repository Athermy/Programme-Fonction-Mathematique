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

    // Évalue l'AST avec une valeur donnée pour x
    public double evaluate(Noeud ast, double x) throws Exception {
        if (ast == null) {
            throw new Exception("AST non défini. Appelez parse() avant evaluate.");
        }
        System.out.println("Evaluer AST avec x = " + x);
        return evaluerNoeud(ast, x);
    }

    // Évalue un noeud de l'AST
    private double evaluerNoeud(Noeud noeud, double x) throws Exception {
        System.out.println("Evaluer noeud: " + noeud);
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
                    throw new IllegalArgumentException("Type de token inconnu: " + token.getTypeDeToken());
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
