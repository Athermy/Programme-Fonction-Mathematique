/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

// Énumération des types de tokens utilisés dans l'analyse lexicale et syntaxique
public enum TypeDeToken {
    ADD,        // Addition
    SUBTRACT,   // Soustraction
    DIVIDE,     // Division
    MULTIPLY,   // Multiplication
    LEFT_PAR,   // Parenthèse gauche
    RIGHT_PAR,  // Parenthèse droite
    COMMA,      // Virgule
    INTV,       // Valeur entière
    IDENT,      // Identifiant
    K_POW,      // Puissance
    ABS,        // Fonction 'abs'
    SIN,        // Fonction 'sin'
    COS,        // Fonction 'cos'
    TAN,        // Fonction 'tan'
    EXP         // Fonction 'exp'
}