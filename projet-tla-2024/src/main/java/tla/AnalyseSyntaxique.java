/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.util.List;

public class AnalyseSyntaxique {

    private int pos;
    private List<Token> tokens;

    /*
    effectue l'analyse syntaxique à partir de la liste de tokens
    et retourne le noeud racine de l'arbre syntaxique abstrait
    */
    public Noeud analyse(List<Token> tokens) throws Exception {
        pos = 0;
        this.tokens = tokens;
        Noeud expr = Expr();
        if (pos != tokens.size()) {
            System.out.println("L'analyse syntaxique s'est terminé avant l'examen de tous les tokens");
            throw new IncompleteParsingException();
        }
        System.out.println("AST généré : ");
        Noeud.afficheNoeud(expr, 0);
        return expr;
    }

    // Signaler une erreur avec un message
    private void signalerErreur(String message) throws Exception {
        throw new Exception("Erreur à la position " + pos + ": " + message);
    }

    // Analyse une expression
    private Noeud Expr() throws Exception {
        Noeud a = A();
        return Expr_prime(a);
    }

    // Analyse la suite d'une expression
    private Noeud Expr_prime(Noeud i) throws Exception {
        if (getTypeDeToken() == TypeDeToken.ADD || getTypeDeToken() == TypeDeToken.SUBTRACT) {
            TypeDeToken operator = getTypeDeToken();
            lireToken(); // Lire le token opérateur
            Noeud n = new Noeud(operator == TypeDeToken.ADD ? TypeDeNoeud.add : TypeDeNoeud.subtract);
            n.ajout(i); // Ajouter le noeud gauche
            n.ajout(A()); // Ajouter le noeud droit
            System.out.println("Expr_prime : Analyse après ajout de " + operator);
            return Expr_prime(n); // Continue l'analyse pour permettre plusieurs opérations en série
        }
        System.out.println("Expr_prime : Token actuel après analyse = " + getTypeDeToken());

        return i;
    }

    // Analyse un terme
    private Noeud A() throws Exception {
        Noeud n = B();
        return A_prime(n);
    }

    // Analyse la suite d'un terme
    private Noeud A_prime(Noeud i) throws Exception {
        if (getTypeDeToken() == TypeDeToken.MULTIPLY || getTypeDeToken() == TypeDeToken.DIVIDE) {
            TypeDeToken operator = getTypeDeToken();
            lireToken(); // Lire le token opérateur
            Noeud n = new Noeud(operator == TypeDeToken.MULTIPLY ? TypeDeNoeud.multiply : TypeDeNoeud.divide);
            n.ajout(i); // Ajouter le noeud gauche
            n.ajout(B()); // Ajouter le noeud droit
            System.out.println("A_prime : Analyse après ajout de " + operator);
            return A_prime(n);
        }
        System.out.println("A_prime : Token actuel après analyse = " + getTypeDeToken());
        return i;
    }

    // Analyse un facteur
    private Noeud B() throws Exception {
        Noeud n = C();
        return B_prime(n);
    }

    // Analyse la suite d'un facteur
    private Noeud B_prime(Noeud i) throws Exception {
        if (getTypeDeToken() == TypeDeToken.K_POW) {
            lireToken(); // Lire le token opérateur
            Noeud n = new Noeud(TypeDeNoeud.kPow);
            n.ajout(i); // Ajouter le noeud gauche
            n.ajout(C()); // Ajouter le noeud droit
            return n;
        }
        return i;
    }

    // Analyse une valeur ou une expression entre parenthèses
    private Noeud C() throws Exception {
        System.out.println("Analyzing token at position " + pos + ": " + getTypeDeToken());
        if (getTypeDeToken() == TypeDeToken.LEFT_PAR) {
            lireToken(); // Lire la parenthèse gauche
            Noeud s = Expr(); // Analyser l'expression entre parenthèses
            if (getTypeDeToken() == TypeDeToken.RIGHT_PAR) {
                lireToken(); // Lire la parenthèse droite
                return s;
            }
            signalerErreur(") attendu");
        }
        if (getTypeDeToken() == TypeDeToken.INTV) {
            Token t = lireToken(); // Lire le token entier
            return new Noeud(TypeDeNoeud.intv, t.getValeur());
        }
        if (getTypeDeToken() == TypeDeToken.IDENT) {
            Token t = lireToken(); // Lire le token identifiant
            return new Noeud(TypeDeNoeud.ident, t.getValeur());
        }
        if (isFunction(getTypeDeToken())) {
            TypeDeToken functionType = getTypeDeToken();
            lireToken(); // Lire le token fonction
            if (getTypeDeToken() == TypeDeToken.LEFT_PAR) {
                lireToken(); // Lire la parenthèse gauche
                Noeud argument = Expr(); // Analyser l'argument de la fonction
                if (getTypeDeToken() == TypeDeToken.RIGHT_PAR) {
                    lireToken(); // Lire la parenthèse droite
                    Noeud functionNode = new Noeud(mapFunctionTypeToNodeType(functionType));
                    functionNode.ajout(argument); // Ajouter l'argument au noeud fonction
                    return functionNode;
                }
                signalerErreur(") attendu");
            }
            signalerErreur("( attendu après la fonction");
        }
        if (getTypeDeToken() == TypeDeToken.SUBTRACT) {
            lireToken(); // Lire le token soustraction
            Noeud n = new Noeud(TypeDeNoeud.subtract);
            n.ajout(new Noeud(TypeDeNoeud.intv, "0")); // Ajouter un noeud 0 pour gérer les nombres négatifs
            n.ajout(C()); // Ajouter le noeud droit
            return n;
        }
        signalerErreur("intv, ( ou ident attendu");
        return null; // Inaccessible, mais nécessaire pour la compilation
    }

    // Vérifie si le token est une fonction
    private boolean isFunction(TypeDeToken tokenType) {
        return tokenType == TypeDeToken.ABS || tokenType == TypeDeToken.SIN || tokenType == TypeDeToken.COS ||
               tokenType == TypeDeToken.TAN || tokenType == TypeDeToken.EXP;
    }

    // Mappe le type de token de fonction au type de noeud correspondant
    private TypeDeNoeud mapFunctionTypeToNodeType(TypeDeToken tokenType) {
        switch (tokenType) {
            case ABS:
                return TypeDeNoeud.abs;
            case SIN:
                return TypeDeNoeud.sin;
            case COS:
                return TypeDeNoeud.cos;
            case TAN:
                return TypeDeNoeud.tan;
            case EXP:
                return TypeDeNoeud.exp;
            default:
                throw new IllegalArgumentException("Unknown function type: " + tokenType);
        }
    }

    // Vérifie si la fin de l'analyse est atteinte
    private boolean finAtteinte() {
        return pos >= tokens.size();
    }

    // Obtient le type de token actuel
    private TypeDeToken getTypeDeToken() {
        if (pos >= tokens.size()) {
            return null;
        } else {
            System.out.println("Type de token actuel : " + tokens.get(pos).getTypeDeToken());
            return tokens.get(pos).getTypeDeToken();
        }
    }

    // Lit le prochain token
    private Token lireToken() {
        if (pos >= tokens.size()) {
            return null;
        } else {
            Token t = tokens.get(pos);
            pos++;
            System.out.println("Token consommé : " + t);
            return t;
        }
    }
}
