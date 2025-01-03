package tla;

import java.util.List;

public class AnalyseSyntaxique {

    private int pos;
    private List<Token> tokens;

    public Noeud analyse(List<Token> tokens) throws Exception {
        pos = 0;
        this.tokens = tokens;
        Noeud expr = Expr();
        if (pos != tokens.size()) {
            throw new IncompleteParsingException();
        }
        return expr;
    }

    private Noeud Expr() throws UnexpectedTokenException {
        if (getTypeDeToken() == TypeDeToken.INTV || getTypeDeToken() == TypeDeToken.K_POW ||
            getTypeDeToken() == TypeDeToken.IDENT || getTypeDeToken() == TypeDeToken.LEFT_PAR ||
            getTypeDeToken() == TypeDeToken.ABS || getTypeDeToken() == TypeDeToken.SIN ||
            getTypeDeToken() == TypeDeToken.COS || getTypeDeToken() == TypeDeToken.TAN ||
            getTypeDeToken() == TypeDeToken.EXP) {
            Noeud a = A();
            return Expr_prime(a);
        }
        throw new UnexpectedTokenException("intv, (, abs, sin, cos, tan, exp ou ident attendu");
    }

    private Noeud Expr_prime(Noeud i) throws UnexpectedTokenException {
        if (getTypeDeToken() == TypeDeToken.ADD) {
            lireToken();
            Noeud n = new Noeud(TypeDeNoeud.ADD);
            n.ajout(i);
            n.ajout(Expr());
            return n;
        }
        if (getTypeDeToken() == TypeDeToken.RIGHT_PAR || getTypeDeToken() == TypeDeToken.K_INPUT ||
            getTypeDeToken() == TypeDeToken.K_PRINT || getTypeDeToken() == TypeDeToken.COMMA || finAtteinte()) {
            return i;
        }
        throw new UnexpectedTokenException("+ ou ) attendu");
    }

    private Noeud A() throws UnexpectedTokenException {
        Noeud n = B();
        return A_prime(n);
    }

    private Noeud A_prime(Noeud i) throws UnexpectedTokenException {
        if (getTypeDeToken() == TypeDeToken.MULTIPLY) {
            lireToken();
            Noeud n = new Noeud(TypeDeNoeud.MULTIPLY);
            n.ajout(i);
            n.ajout(A());
            return n;
        }
        if (getTypeDeToken() == TypeDeToken.ADD || getTypeDeToken() == TypeDeToken.RIGHT_PAR ||
            getTypeDeToken() == TypeDeToken.K_INPUT || getTypeDeToken() == TypeDeToken.K_PRINT ||
            getTypeDeToken() == TypeDeToken.COMMA || finAtteinte()) {
            return i;
        }
        throw new UnexpectedTokenException("* + ou ) attendu");
    }

    private Noeud B() throws UnexpectedTokenException {
        Noeud n = C();
        return B_prime(n);
    }

    private Noeud B_prime(Noeud i) throws UnexpectedTokenException {
        if (getTypeDeToken() == TypeDeToken.K_POW) {
            lireToken();
            Noeud n = new Noeud(TypeDeNoeud.K_POW);
            n.ajout(i);
            n.ajout(C());
            return n;
        }
        if (getTypeDeToken() == TypeDeToken.ADD || getTypeDeToken() == TypeDeToken.MULTIPLY ||
            getTypeDeToken() == TypeDeToken.RIGHT_PAR || getTypeDeToken() == TypeDeToken.K_INPUT ||
            getTypeDeToken() == TypeDeToken.K_PRINT || getTypeDeToken() == TypeDeToken.COMMA || finAtteinte()) {
            return i;
        }
        throw new UnexpectedTokenException("^ * + ou ) attendu");
    }

    private Noeud C() throws UnexpectedTokenException {
        if (getTypeDeToken() == TypeDeToken.LEFT_PAR) {
            lireToken();
            Noeud s = Expr();
            if (getTypeDeToken() == TypeDeToken.RIGHT_PAR) {
                lireToken();
                return s;
            }
            throw new UnexpectedTokenException(") attendu");
        }
        if (getTypeDeToken() == TypeDeToken.INTV) {
            Token t = lireToken();
            return new Noeud(TypeDeNoeud.INTV, t.getValeur());
        }
        if (getTypeDeToken() == TypeDeToken.IDENT) {
            Token t = lireToken();
            return new Noeud(TypeDeNoeud.IDENT, t.getValeur());
        }
        if (getTypeDeToken() == TypeDeToken.ABS || getTypeDeToken() == TypeDeToken.SIN ||
            getTypeDeToken() == TypeDeToken.COS || getTypeDeToken() == TypeDeToken.TAN ||
            getTypeDeToken() == TypeDeToken.EXP) {
            Token t = lireToken();
            Noeud n = new Noeud(TypeDeNoeud.valueOf(t.getTypeDeToken().name()));
            n.ajout(C());
            return n;
        }
        throw new UnexpectedTokenException("intv, (, abs, sin, cos, tan, exp ou ident attendu");
    }

    private boolean finAtteinte() {
        return pos >= tokens.size();
    }

    private TypeDeToken getTypeDeToken() {
        if (pos >= tokens.size()) {
            return null;
        } else {
            return tokens.get(pos).getTypeDeToken();
        }
    }

    private Token lireToken() {
        if (pos >= tokens.size()) {
            return null;
        } else {
            Token t = tokens.get(pos);
            pos++;
            return t;
        }
    }
}
