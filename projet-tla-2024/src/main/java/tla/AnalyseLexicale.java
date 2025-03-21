/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnalyseLexicale {

    // Table de transition pour l'analyse lexicale
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

    // Analyse lexicale de l'entrée et retourne une liste de tokens correspondants
    public List<Token> analyse(String entree) throws Exception {
        this.entree = entree;
        pos = 0;

        List<Token> tokens = new ArrayList<>();
        String buf = "";
        Integer etat = ETAT_INITIAL;
        Character c;
        boolean lastWasOperator = true; // Suivre si le dernier token était un opérateur ou non
        Stack<Character> parenthesesStack = new Stack<>(); // Pile pour suivre les parenthèses ouvrantes non fermées et les parenthèses fermantes non ouvertes

        do {
            c = lireCaractere(); // Lire le prochain caractère de l'entrée
            Integer e = TRANSITIONS[etat][indiceSymbole(c)]; // Obtenir le prochain état à partir de la table de transition
            if (e == null) {
                throw new LexicalErrorException("pas de transition depuis état " + etat + " avec symbole " + c);
            }
            if (e >= 100) { // Si l'état est un état d'acceptation
                switch (e) {
                    case 101:
                        tokens.add(new Token(TypeDeToken.ADD)); // Ajouter un token ADD
                        break;
                    case 102:
                        tokens.add(new Token(TypeDeToken.MULTIPLY)); // Ajouter un token MULTIPLY
                        break;
                    case 103:
                        tokens.add(new Token(TypeDeToken.LEFT_PAR)); // Ajouter un token LEFT_PAR
                        parenthesesStack.push('('); // Empiler la parenthèse ouvrante pour vérification ultérieure
                        break;
                    case 104:
                        tokens.add(new Token(TypeDeToken.RIGHT_PAR)); // Ajouter un token RIGHT_PAR
                        if (parenthesesStack.isEmpty() || parenthesesStack.pop() != '(') {
                            throw new LexicalErrorException("Parenthèse fermante sans correspondance");
                        }
                        break;
                    case 105:
                        tokens.add(new Token(TypeDeToken.COMMA)); // Ajouter un token COMMA
                        break;
                    case 106:
                        tokens.add(new Token(TypeDeToken.INTV, buf)); // Ajouter un token INTV avec la valeur du buffer
                        retourArriere(); // Revenir en arrière d'un caractère
                        break;
                    case 107:
                        switch (buf) {
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
                                tokens.add(new Token(TypeDeToken.IDENT, buf)); // Ajouter un token IDENT avec la valeur du buffer
                                break;
                        }
                        retourArriere(); // Revenir en arrière d'un caractère
                        break;
                    case 108:
                        tokens.add(new Token(TypeDeToken.K_POW)); // Ajouter un token K_POW
                        break;
                    case 109:
                        tokens.add(new Token(TypeDeToken.DIVIDE)); // Ajouter un token DIVIDE
                        break;
                    case 110:
                        // Vérifier si le prochain caractère est un espace suivi d'un autre '-'
                        Character nextChar = lireCaractere();
                        while (nextChar != null && Character.isWhitespace(nextChar)) {
                            nextChar = lireCaractere();
                        }
                        if (nextChar != null && nextChar == '-') {
                            tokens.add(new Token(TypeDeToken.ADD)); // Ajouter un token ADD
                            lastWasOperator = false;
                        } else {
                            retourArriere(); // Revenir en arrière d'un caractère
                            tokens.add(new Token(TypeDeToken.SUBTRACT)); // Ajouter un token SUBTRACT
                        }
                        break;
                    case 111:
                        tokens.add(new Token(TypeDeToken.ABS)); // Ajouter un token ABS
                        break;
                    case 112:
                        tokens.add(new Token(TypeDeToken.SIN)); // Ajouter un token SIN
                        break;
                    case 113:
                        tokens.add(new Token(TypeDeToken.COS)); // Ajouter un token COS
                        break;
                    case 114:
                        tokens.add(new Token(TypeDeToken.TAN)); // Ajouter un token TAN
                        break;
                    case 115:
                        tokens.add(new Token(TypeDeToken.EXP)); // Ajouter un token EXP
                        break;
                    case 116:
                        tokens.add(new Token(TypeDeToken.IDENT, "x")); // Ajouter un token IDENT avec la valeur "x"
                        break;
                }
                etat = 0; // Réinitialiser l'état
                buf = ""; // Réinitialiser le buffer
                lastWasOperator = (e != 106 && e != 107 && e != 116); // Mettre à jour lastWasOperator
            } else {
                etat = e; // Mettre à jour l'état
                if (etat > 0) buf = buf + c; // Ajouter le caractère au buffer
            }
        } while (c != null);

        // Vérifier s'il reste des parenthèses ouvrantes non fermées
        if (!parenthesesStack.isEmpty()) {
            throw new LexicalErrorException("Parenthèse ouvrante sans fermeture correspondante");
        }
        
        return tokens;
    }

    // Lire le prochain caractère de l'entrée
    private Character lireCaractere() {
        Character c;
        try {
            c = entree.charAt(pos); // Lire le caractère à la position actuelle
            pos = pos + 1; // Avancer la position
        } catch (StringIndexOutOfBoundsException ex) {
            c = null; // Retourner null si la position dépasse la longueur de l'entrée
        }
        return c;
    }

    // Revenir en arrière d'un caractère
    private void retourArriere() {
        pos = pos - 1; // Reculer la position d'un caractère
    }

    // Obtenir l'indice du symbole dans la table de transition
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