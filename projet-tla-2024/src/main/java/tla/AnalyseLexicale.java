package tla;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnalyseLexicale {

    private static final Integer[][] TRANSITIONS = {
        //            espace    +    *    /    -    ^    abs  sin  cos  tan  exp    (    )    ,  chiffre  lettre    x    .
        /*  0 */    {      0, 101, 102, 109, 110, 108, 111, 112, 113, 114, 115, 103, 104, 105,       1,      2,   116,  1  },
        /*  1 */    {    106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106, 106,       1,    106,   106,  1  },
        /*  2 */    {    107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107,       2,      2,   107, 107  }
        // 101 acceptation d'un +
        // 102 acceptation d'un *
        // 103 acceptation d'un (
        // 104 acceptation d'un )
        // 105 acceptation d'un ,
        // 106 acceptation d'un entier ou nombre décimal (retourArriere)
        // 107 acceptation d'un identifiant ou mot clé   (retourArriere)
        // 108 acceptation d'un ^
        // 109 acceptation d'un /
        // 110 acceptation d'un -
        // 111 acceptation d'un abs
        // 112 acceptation d'un sin
        // 113 acceptation d'un cos
        // 114 acceptation d'un tan
        // 115 acceptation d'un exp
        // 116 acceptation d'un x
    };

    private String entree;
    private int pos;

    private static final int ETAT_INITIAL = 0;

    public List<Token> analyse(String entree) throws Exception {
        this.entree = entree;
        pos = 0;

        List<Token> tokens = new ArrayList<>();
        String buf = "";
        Integer etat = ETAT_INITIAL;
        Character c;
        boolean lastWasOperator = true; // Track if the last token was an operator
        Stack<Character> parenthesesStack = new Stack<>(); // Stack to track parentheses

        do {
            c = lireCaractere();
            Integer e = TRANSITIONS[etat][indiceSymbole(c)];
            if (e == null) {
                throw new LexicalErrorException("pas de transition depuis état " + etat + " avec symbole " + c);
            }
            if (e >= 100) {
                switch (e) {
                    case 101:
                        tokens.add(new Token(TypeDeToken.ADD));
                        break;
                    case 102:
                        tokens.add(new Token(TypeDeToken.MULTIPLY));
                        break;
                    case 103:
                        tokens.add(new Token(TypeDeToken.LEFT_PAR));
                        parenthesesStack.push('('); // Push to stack
                        break;
                    case 104:
                        tokens.add(new Token(TypeDeToken.RIGHT_PAR));
                        if (parenthesesStack.isEmpty() || parenthesesStack.pop() != '(') {
                            throw new LexicalErrorException("Parenthèse fermante sans correspondance");
                        }
                        break;
                    case 105:
                        tokens.add(new Token(TypeDeToken.COMMA));
                        break;
                    case 106:
                        tokens.add(new Token(TypeDeToken.INTV, buf));
                        retourArriere();
                        break;
                    case 107:
                        switch (buf) {
                            case "input":
                                tokens.add(new Token(TypeDeToken.K_INPUT));
                                break;
                            case "print":
                                tokens.add(new Token(TypeDeToken.K_PRINT));
                                break;
                            case "pow":
                                tokens.add(new Token(TypeDeToken.K_POW));
                                break;
                            case "abs":
                                tokens.add(new Token(TypeDeToken.ABS));
                                break;
                            case "sin":
                                tokens.add(new Token(TypeDeToken.SIN));
                                break;
                            case "cos":
                                tokens.add(new Token(TypeDeToken.COS));
                                break;
                            case "tan":
                                tokens.add(new Token(TypeDeToken.TAN));
                                break;
                            case "exp":
                                tokens.add(new Token(TypeDeToken.EXP));
                                break;
                            default:
                                tokens.add(new Token(TypeDeToken.IDENT, buf));
                                break;
                        }
                        retourArriere();
                        break;
                    case 108:
                        tokens.add(new Token(TypeDeToken.K_POW));
                        break;
                    case 109:
                        tokens.add(new Token(TypeDeToken.DIVIDE));
                        break;
                    case 110:
                        if (lastWasOperator) {
                            tokens.add(new Token(TypeDeToken.INTV, "0")); // Add 0 before negative number
                        }
                        tokens.add(new Token(TypeDeToken.SUBTRACT));
                        break;
                    case 111:
                        tokens.add(new Token(TypeDeToken.ABS));
                        break;
                    case 112:
                        tokens.add(new Token(TypeDeToken.SIN));
                        break;
                    case 113:
                        tokens.add(new Token(TypeDeToken.COS));
                        break;
                    case 114:
                        tokens.add(new Token(TypeDeToken.TAN));
                        break;
                    case 115:
                        tokens.add(new Token(TypeDeToken.EXP));
                        break;
                    case 116:
                        tokens.add(new Token(TypeDeToken.IDENT, "x"));
                        break;
                }
                etat = 0;
                buf = "";
                lastWasOperator = (e != 106 && e != 107 && e != 116); // Update lastWasOperator
            } else {
                etat = e;
                if (etat > 0) buf = buf + c;
            }
        } while (c != null);

        if (!parenthesesStack.isEmpty()) {
            throw new LexicalErrorException("Parenthèse ouvrante sans fermeture correspondante");
        }

        return tokens;
    }

    private Character lireCaractere() {
        Character c;
        try {
            c = entree.charAt(pos);
            pos = pos + 1;
        } catch (StringIndexOutOfBoundsException ex) {
            c = null;
        }
        return c;
    }

    private void retourArriere() {
        pos = pos - 1;
    }

    public static int indiceSymbole(Character c) throws IllegalCharacterException {
        if (c == null) return 0;
        if (Character.isWhitespace(c)) return 0;
        if (c == '+') return 1;
        if (c == '*') return 2;
        if (c == '/') return 3;
        if (c == '-') return 4;
        if (c == '^') return 5;
        if (c == '(') return 11;
        if (c == ')') return 12;
        if (c == ',') return 13;
        if (Character.isDigit(c)) return 14;
        if (Character.isLetter(c)) return 15;
        if (c == 'x') return 16;
        if (c == '.') return 17;
        throw new IllegalCharacterException(c.toString());
    }
}
