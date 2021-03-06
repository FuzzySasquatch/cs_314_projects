/**
 * this class Cons implements a Lisp-like Cons cell
 * 
 * @author  Gordon S. Novak Jr.
 * @version 29 Nov 01; 25 Aug 08; 05 Sep 08; 08 Sep 08; 12 Sep 08; 24 Sep 08
 *          06 Oct 08; 07 Oct 08; 09 Oct 08; 23 Oct 08; 30 Oct 08; 07 Apr 09
 *          10 Apr 09; 02 Aug 10; 06 Aug 10
 */

import java.util.StringTokenizer;

interface Functor { Object fn(Object x); }

interface Predicate { boolean pred(Object x); }

public class Cons
{
    // instance variables
    private Object car;
    private Cons cdr;
    private Cons(Object first, Cons rest)
       { car = first;
         cdr = rest; }
    public static Cons cons(Object first, Cons rest)
      { return new Cons(first, rest); }
    public static boolean consp (Object x)
       { return ( (x != null) && (x instanceof Cons) ); }
// safe car, returns null if lst is null
    public static Object first(Cons lst) {
        return ( (lst == null) ? null : lst.car  ); }
// safe cdr, returns null if lst is null
    public static Cons rest(Cons lst) {
      return ( (lst == null) ? null : lst.cdr  ); }
    public static Object second (Cons x) { return first(rest(x)); }
    public static Object third (Cons x) { return first(rest(rest(x))); }
    public static void setfirst (Cons x, Object i) { x.car = i; }
    public static void setrest  (Cons x, Cons y) { x.cdr = y; }
   public static Cons list(Object ... elements) {
       Cons list = null;
       for (int i = elements.length-1; i >= 0; i--) {
           list = cons(elements[i], list);
       }
       return list;
   }
    // access functions for expression representation
    public static Object op  (Cons x) { return first(x); }
    public static Object lhs (Cons x) { return first(rest(x)); }
    public static Object rhs (Cons x) { return first(rest(rest(x))); }
    public static boolean numberp (Object x)
       { return ( (x != null) &&
                  (x instanceof Integer || x instanceof Double) ); }
    public static boolean integerp (Object x)
       { return ( (x != null) && (x instanceof Integer ) ); }
    public static boolean floatp (Object x)
       { return ( (x != null) && (x instanceof Double ) ); }
    public static boolean stringp (Object x)
       { return ( (x != null) && (x instanceof String ) ); }

    public boolean equals(Object other) { return equal(this,other); }

    // tree equality
public static boolean equal(Object tree, Object other) {
    if ( tree == other ) return true;
    if ( consp(tree) )
        return ( consp(other) &&
                 equal(first((Cons) tree), first((Cons) other)) &&
                 equal(rest((Cons) tree), rest((Cons) other)) );
    return eql(tree, other); }

    // simple equality test
public static boolean eql(Object tree, Object other) {
    return ( (tree == other) ||
             ( (tree != null) && (other != null) &&
               tree.equals(other) ) ); }

    // convert a list to a string for printing
    public String toString() {
       return ( "(" + toStringb(this) ); }
    public static String toString(Cons lst) {
       return ( "(" + toStringb(lst) ); }
    private static String toStringb(Cons lst) {
       return ( (lst == null) ?  ")"
                : ( first(lst) == null ? "()" : first(lst).toString() )
                  + ((rest(lst) == null) ? ")" 
                     : " " + toStringb(rest(lst)) ) ); }

    // iterative version of length
public static int length (Cons lst) {
  int n = 0;
  while ( lst != null ) {
    n++;
    lst = rest(lst); }
  return n; }

// member returns null if requested item not found
public static Cons member (Object item, Cons lst) {
  if ( lst == null )
     return null;
   else if ( item.equals(first(lst)) )
           return lst;
         else return member(item, rest(lst)); }

public static Cons union (Cons x, Cons y) {
  if ( x == null ) return y;
  if ( member(first(x), y) != null )
       return union(rest(x), y);
  else return cons(first(x), union(rest(x), y)); }

public static boolean subsetp (Cons x, Cons y) {
    return ( (x == null) ? true
             : ( ( member(first(x), y) != null ) &&
                 subsetp(rest(x), y) ) ); }

public static boolean setEqual (Cons x, Cons y) {
    return ( subsetp(x, y) && subsetp(y, x) ); }

    // combine two lists: (append '(a b) '(c d e))  =  (a b c d e)
public static Cons append (Cons x, Cons y) {
  if (x == null)
     return y;
   else return cons(first(x),
                    append(rest(x), y)); }

    // look up key in an association list
    // (assoc 'two '((one 1) (two 2) (three 3)))  =  (two 2)
public static Cons assoc(Object key, Cons lst) {
  if ( lst == null )
     return null;
  else if ( key.equals(first((Cons) first(lst))) )
      return ((Cons) first(lst));
          else return assoc(key, rest(lst)); }

    public static int square(int x) { return x*x; }
    public static int pow (int x, int n) {
        if ( n <= 0 ) return 1;
        if ( (n & 1) == 0 )
            return square( pow(x, n / 2) );
        else return x * pow(x, n - 1); }

public static Object copy_tree(Object tree) {
    if ( consp(tree) )
        return cons(copy_tree(first((Cons) tree)),
                    (Cons) copy_tree(rest((Cons) tree)));
    return tree; }

public static Object subst(Object gnew, String old, Object tree) {
    if ( consp(tree) )
        return cons(subst(gnew, old, first((Cons) tree)),
                    (Cons) subst(gnew, old, rest((Cons) tree)));
    return (old.equals(tree)) ? gnew : tree; }

public static Object sublis(Cons alist, Object tree) {
    if ( consp(tree) )
        return cons(sublis(alist, first((Cons) tree)),
                    (Cons) sublis(alist, rest((Cons) tree)));
    if ( tree == null ) return null;
    Cons pair = assoc(tree, alist);
    return ( pair == null ) ? tree : second(pair); }

public static Cons dummysub = list(list("t", "t"));

public static Cons match(Object pattern, Object input) {
    return matchb(pattern, input, dummysub); }

public static Cons matchb(Object pattern, Object input, Cons bindings) {
    if ( bindings == null ) return null;
    if ( consp(pattern) )
        if ( consp(input) )
            return matchb( rest( (Cons) pattern),
                           rest( (Cons) input),
                           matchb( first( (Cons) pattern),
                                   first( (Cons) input),
                                   bindings) );
        else return null;
    if ( varp(pattern) ) {
        Cons binding = assoc(pattern, bindings);
        if ( binding != null )
            if ( equal(input, second(binding)) )
                return bindings;
            else return null;
        else return cons(list(pattern, input), bindings); }
    if ( eql(pattern, input) )
        return bindings;
    return null; }

public static Object reader(String str) {
    return readerb(new StringTokenizer(str, " \t\n\r\f()'", true)); }

public static Object readerb( StringTokenizer st ) {
    if ( st.hasMoreTokens() ) {
        String nexttok = st.nextToken();
        if ( nexttok.charAt(0) == ' ' ||
             nexttok.charAt(0) == '\t' ||
             nexttok.charAt(0) == '\n' ||
             nexttok.charAt(0) == '\r' ||
             nexttok.charAt(0) == '\f' )
            return readerb(st);
        if ( nexttok.charAt(0) == '(' )
            return readerlist(st);
        if ( nexttok.charAt(0) == '\'' )
            return list("QUOTE", readerb(st));
        return readtoken(nexttok); }
    return null; }

    public static Object readtoken( String tok ) {
        if ( (tok.charAt(0) >= '0' && tok.charAt(0) <= '9') ||
             ((tok.length() > 1) &&
              (tok.charAt(0) == '+' || tok.charAt(0) == '-' ||
               tok.charAt(0) == '.') &&
              (tok.charAt(1) >= '0' && tok.charAt(1) <= '9') ) ||
             ((tok.length() > 2) &&
              (tok.charAt(0) == '+' || tok.charAt(0) == '-') &&
              (tok.charAt(1) == '.') &&
              (tok.charAt(2) >= '0' && tok.charAt(2) <= '9') )  ) {
            boolean dot = false;
            for ( int i = 0; i < tok.length(); i++ )
                if ( tok.charAt(i) == '.' ) dot = true;
            if ( dot )
                return Double.parseDouble(tok);
            else return Integer.parseInt(tok); }
        return tok; }

public static Cons readerlist( StringTokenizer st ) {
    if ( st.hasMoreTokens() ) {
        String nexttok = st.nextToken();
        if ( nexttok.charAt(0) == ' ' ||
             nexttok.charAt(0) == '\t' ||
             nexttok.charAt(0) == '\n' ||
             nexttok.charAt(0) == '\r' ||
             nexttok.charAt(0) == '\f' )
            return readerlist(st);
        if ( nexttok.charAt(0) == ')' )
            return null;
        if ( nexttok.charAt(0) == '(' ) {
            Cons temp = readerlist(st);
            return cons(temp, readerlist(st)); }
        if ( nexttok.charAt(0) == '\'' ) {
            Cons temp = list("QUOTE", readerb(st));
            return cons(temp, readerlist(st)); }
        return cons( readtoken(nexttok),
                     readerlist(st) ); }
    return null; }

    // read a list of strings, producing a list of results.
public static Cons readlist( Cons lst ) {
    if ( lst == null )
        return null;
    return cons( reader( (String) first(lst) ),
                 readlist( rest(lst) ) ); }

public static Object transform(Cons patpair, Cons input) {
    Cons bindings = match(first(patpair), input);
    if ( bindings == null ) return null;
    return sublis(bindings, second(patpair)); }

    // Transform a list of arguments.  If no change, returns original.
public static Cons transformlst(Cons allpats, Cons input) {
    if ( input == null ) return null;
    Cons restt = transformlst(allpats, rest(input));
    Object thist = transformr(allpats, first(input));
    if ( thist == first(input) && restt == rest(input) )
        return input;
    return cons(thist, restt); }

    // Transform a single item.  If no change, returns original.
public static Object transformr(Cons allpats, Object input) {
    //    System.out.println("transformr:  " + input.toString());
    if ( consp(input) ) {
        Cons listt = transformlst(allpats, (Cons) input);
        //       System.out.println("   lst =  " + listt.toString());
        return transformrb(allpats, allpats,
                           transformlst(allpats, listt)); }
    Object res = transformrb(allpats, allpats, input);
    //    System.out.println("   result =  " + res.toString());
    return res; }

    // Transform a single item.  If no change, returns original.
public static Object transformrb(Cons pats, Cons allpats, Object input) {
    if ( pats == null ) return input;
    if ( input == null ) return null;
    Cons bindings = match(first((Cons)first(pats)), input);
    if ( bindings == null ) return transformrb(rest(pats), allpats, input);
    return sublis(bindings, second((Cons)first(pats))); }

    // Transform a single item repeatedly, until fixpoint (no change).
public static Object transformfp(Cons allpats, Object input) {
    //    System.out.println("transformfp: " + input.toString());
    Object trans = transformr(allpats, input);
    if ( trans == input ) return input;
    //    System.out.println("    result = " + trans.toString());
    return transformfp(allpats, trans); }          // potential loop!

public static boolean varp(Object x) {
    return ( stringp(x) &&
             ( ((String) x).charAt(0) == '?' ) ); }

    // Note: this list will handle most, but not all, cases.
    // The binary operators - and / have special cases.
public static Cons opposites = 
    list( list( "+", "-"), list( "-", "+"), list( "*", "/"),
          list( "/", "*"), list( "sqrt", "expt"), list( "expt", "sqrt"),
          list( "log", "exp"), list( "exp", "log") );

public static String opposite(String op) {
    Cons pair = assoc(op, opposites);
    if ( pair != null ) return (String) second(pair);
    return "error"; }

public static void javaprint(Object item, int tabs) {
    if ( item == null ) System.out.print("null");
    else if ( consp(item) ) javaprintlist((Cons) item, tabs);
    else if ( stringp(item) )
        if ( item.equals("zlparen") ) System.out.print("(");
        else if ( item.equals("zrparen") ) System.out.print(")");
        else if ( item.equals("zspace") ) System.out.print(" ");
        else if ( item.equals("znothing") ) ;
        else if ( item.equals("ztab") ) System.out.print("\t");
        else if ( item.equals("zreturn") ) System.out.println();
        else System.out.print((String)item);
    else System.out.print(item.toString()); }

public static void javaprintlist(Cons lst, int tabs) {
    if ( lst != null ) {
        if ( stringp(first(lst)) )
            if ( ((String)first(lst)).equals("ztab" ) ) tabs++;
            else if ( ((String)first(lst)).equals("zuntab" ) ) tabs--;
            else if ( ((String)first(lst)).equals("zreturn" ) ) {
                System.out.println();
                for (int i = 0; i < tabs; i++) System.out.print("\t"); }
            else javaprint(first(lst), tabs);
        else javaprint(first(lst), tabs);
        javaprintlist(rest(lst), tabs); } }
             
    // ****** your code starts here ******

    // your program from the previous assignments
    // attempts to solve equation e for variable v
public static Cons solve(Cons e, String v) {
  // lhs of e is v
  if (lhs(e).equals(v))
    return e;
  // rhs of e is v
  if (rhs(e).equals(v))
    return list(op(e), rhs(e), lhs(e));
  // rhs of e is an operator
  if (consp(rhs(e))) {
    Object op = op((Cons)rhs(e));
    // unary -
    if (op.equals("-") && rhs((Cons)rhs(e)) == null)
      return solve(list("=", list(op, lhs(e)), lhs((Cons)rhs(e))), v);
    // + and * 
    if (op.equals("+") || op.equals("*")) {
      op = second(assoc(op, opposites));
      if (solve(list("=", list(op, lhs(e), lhs((Cons)rhs(e))), rhs((Cons)rhs(e))), v) != null)
        return solve(list("=", list(op, lhs(e), lhs((Cons)rhs(e))), rhs((Cons)rhs(e))), v);
      return solve(list("=", list(op, lhs(e), rhs((Cons)rhs(e))), lhs((Cons)rhs(e))), v);
    }
    // binary - and /
    if (op.equals("-") || op.equals("/")) {
      if (solve(list("=", list(op, lhs(e), rhs((Cons)rhs(e))), lhs((Cons)rhs(e))), v) != null) {
        op = second(assoc(op, opposites));
        return solve(list("=", list(op, lhs(e), rhs((Cons)rhs(e))), lhs((Cons)rhs(e))), v);
      }
      return solve(list("=", list(op, lhs((Cons)rhs(e)), lhs(e)), rhs((Cons)rhs(e))), v);
    }
    // log and exp
    if (op.equals("log") || op.equals("exp")) {
      op = second(assoc(op, opposites));
      if (lhs((Cons)lhs((Cons)rhs(e))) == null)
        return solve(list("=", list(op, lhs(e)), first((Cons)lhs((Cons)rhs(e)))), v);
      return solve(list("=", list(op, lhs(e)), list( op((Cons)lhs((Cons)rhs(e))), lhs((Cons)lhs((Cons)rhs(e))), rhs((Cons)lhs((Cons)rhs(e))) ) ), v);
    }
    // expt to sqrt
    if (op.equals("expt")) {
      op = second(assoc(op, opposites));
      return solve(list("=", list(op, lhs(e)), lhs((Cons)rhs(e))), v); 
    }
    // sqrts to expt
    if (op.equals("sqrt")) {
      op = second(assoc(op, opposites));
      if (lhs((Cons)lhs((Cons)rhs(e))) == null)
        return solve(list("=", list(op, lhs(e), new Integer(2)), first((Cons)lhs((Cons)rhs(e)))), v); 
      return solve(list("=", list(op, lhs(e), new Integer(2)), list( op((Cons)lhs((Cons)rhs(e))), lhs((Cons)lhs((Cons)rhs(e))), rhs((Cons)lhs((Cons)rhs(e))) ) ), v);
    }
  }
  // rhs is not v or an operator
  return null;
}

    // returns the set of variables in an expression
public static Cons vars (Object expr) {
  if (consp(expr))
    return union(vars(lhs((Cons)expr)), vars(rhs((Cons)expr)));
  else if (stringp(expr))
    return cons(expr, null);
  return null;
}

    // assigns values to variables and evaluates the resulting expression
public static Double eval (Object tree, Cons bindings) {
  // interior
  if (consp(tree)) {
    // +
    if (op((Cons)tree).equals("+"))
      return eval(lhs((Cons)tree), bindings) + eval(rhs((Cons)tree), bindings);
    // - and unary minus
    if (op((Cons)tree).equals("-")) {
      // accounts for unary minus
      if (rest(rest((Cons)tree)) == null) {
        return - eval(lhs((Cons)tree), bindings);
      }
      return eval(lhs((Cons)tree), bindings) - eval(rhs((Cons)tree), bindings);
    }
    // *
    if (op((Cons)tree).equals("*"))
      return eval(lhs((Cons)tree), bindings) * eval(rhs((Cons)tree), bindings);
    // /
    // System.out.println("Cur eq = " + tree);
    // System.out.println("bindings = " + bindings);
    // System.out.println(eval(rhs((Cons)tree), bindings));
    if (op((Cons)tree).equals("/"))
      return eval(lhs((Cons)tree), bindings) / eval(rhs((Cons)tree), bindings);
    // expt
    if (op((Cons)tree).equals("expt"))
      return Math.pow(eval(lhs((Cons)tree), bindings), eval(rhs((Cons)tree), bindings));
    // exp
    if (op((Cons)tree).equals("exp"))
      return Math.exp(eval(lhs((Cons)tree), bindings));
    // sqrt
    if (op((Cons)tree).equals("sqrt")) {
      return Math.sqrt(eval(lhs((Cons)tree), bindings));
    }
    // log
    if (op((Cons)tree).equals("log"))
      return Math.log(eval(lhs((Cons)tree), bindings));
    // (leaves)
    if (stringp(first((Cons)tree)))
      return ((Double)second(assoc(first((Cons)tree), bindings)));
    if (integerp(first((Cons)tree)))
      return ((Integer)first((Cons)tree)).doubleValue();
    if (numberp(first((Cons)tree))) 
      return (Double)first((Cons)tree);
    return null; 
  }
  // leaves
  if (integerp(tree))
    return ((Integer)tree).doubleValue();
  if (numberp(tree)) 
    return (Double) tree;
  // assigns values to variables
  if(stringp(tree)) {
    return ((Double)second(assoc(tree, bindings)));
  }
  return null;
}

    // new for this assignment

// attempts to solve the list of equations eqn for variable v
// given an assoc list of vals
public static Double solveqns(Cons eqns, Cons vals, String v) {
 // test if v is already defined in vals
 if (assoc(v, vals) != null)
    return (Double) second(assoc(v, vals));

  Cons vars = vars(first(eqns)); // get variables from first eqn
  int unknown = 0;
  String var = "";

  // test for number of unknowns in eqn
  while (vars != null) {
    if (assoc(first(vars), vals) == null) {
      unknown++;
      var = (String) first(vars);
    }
    vars = rest(vars);
  }

  // if var can be solved and evaled add it and its val to vals
  if (unknown == 1) {
    Object tree = solve((Cons)first(eqns), var);
    // don't forget to remove the = and variable
    Double val = eval(rhs((Cons)tree), vals);
    if (tree != null && val != null) {
      return solveqns(rest(eqns), cons(list(var, val), vals), v);
    }
  }

  // no equations can be solved
  if (rest(eqns) == null)
    return null;
  return solveqns(rest(eqns), vals, v);  
}

    // Question 2 of Assignment 8
    // Code substitutions to be done in 'binaryfn' below
    // ((?function ...) (?combine ...) (?baseanswer ...))
    //    (defun ?function (tree)
    //       (if (consp tree)
    //           (?combine (?function (first tree))
    //                     (?function (rest tree)))
    //           ?baseanswer))
    public static Cons substitutions = readlist( list(
       "( (?function addnums) (?combine +) (?baseanswer (if (numberp tree) tree 0)))",       
       "( (?function countstrings) (?combine +) (?baseanswer (if (stringp tree) 1 0)))",
       "( (?function copytree) (?combine cons) (?baseanswer tree)",
       "( (?function mintree) (?combine min) (?baseanswer (if (numberp tree) tree 999999)))",
       "( (?function conses) (?combine add1) (?baseanswer 0))"
       ));
  
    // list of patterns to perform optimization of algebraic expressions
    public static Cons optpats = readlist( list(

       // addition
       "( (+ ?x 0)   ?x)",
       "( (+ 0 ?x)   ?x)",

       // simple addition
       "(  (+ 1 1)   2)",
       "(  (+ 2 1)   3)",
       "(  (+ 3 1)   4)",
       "(  (+ 4 1)   5)",
       "(  (+ 5 1)   6)",
       "(  (+ 6 1)   7)",
       "(  (+ 7 1)   8)",
       "(  (+ 8 1)   9)",
       "(  (+ 9 1)   10)",
       "(  (+ 10 1)   11)",
       "(  (+ 11 1)   12)",
       "(  (+ 12 1)   13)",
       "(  (+ 13 1)   14)",
       "(  (+ 14 1)   15)",
       "(  (+ 15 1)   16)",

       // expt
       "( (expt ?x 1) ?x)",
       "( (expt ?x 0) 1)",
       "( (/ (expt ?x ?u) (expt ?x ?v))   (expt ?x (- ?u ?v)))",
       "( (/ (* ?y (expt ?x ?u)) (expt ?x ?v))   (* ?y (expt ?x (- ?u ?v))))",
       "( (expt ?x (- ?y))   (/ 1 (expt ?x ?y)))",

       // division/multiplication
       "( (* (/ 1 ?u) ?v)   (/ ?v ?u))",
       "( (/ (* ?y ?u) (* ?y ?v))   (/ ?u ?v))",
       "( (/ (* ?y ?u) (* ?v ?y))   (/ ?u ?v))",
       "( (* ?y (/ 1 ?x))   (/ ?y ?x))",

       // multiplication
       "( (* ?x 0)   0)",
       "( (* 0 ?x)   0)",
       "( (* ?x 1)   ?x)",
       "( (* 1 ?x)   ?x)",

       // subtraction
       "( (- (- ?x ?y ))   (- ?y ?x))",
       "( (- ?x ?x)   0)",
       "( (- (- ?x))   ?x)",

       // simple subtraction
       "(  (- 2 1)   1)",
       "(  (- 3 1)   2)",
       "(  (- 4 1)   3)",
       "(  (- 5 1)   4)",
       "(  (- 6 1)   5)",
       "(  (- 7 1)   6)",
       "(  (- 8 1)   7)",
       "(  (- 9 1)   8)",
       "(  (- 10 1)   9)",
       "(  (- 11 1)   10)",
       "(  (- 12 1)   11)",
       "(  (- 13 1)   12)",
       "(  (- 14 1)   13)",
       "(  (- 15 1)   14)",
       "(  (- 16 1)   15)",
       "(  (- 2 3)   (- 1))",

       // division
       "( (/ 0 ?x)   0)",
       "( (/ ?x ?x)   1)",
       "( (/ ?x 1)   ?x)",
       "( (/ (* ?y ?x) ?y)   ?x)",

       // ln
       "( (+ (log x?) (log?y))   (log (* ?x ?y)))",
       "( (- (log x?) (log ?y))   (log (/ ?x ?y)))",
       "( (log 1)   0)",

       // exp with base e
       "( (exp 0)   1)",

       // square root
       "( (sqrt (* ?x ?x))   ?x)",
       "( (sqrt (expt ?x 2))   ?x)",

       // simple square root
       "( (sqrt 0)   0)",
       "( (sqrt 1)   1)",
       "( (sqrt 4)   2)",
       "( (sqrt 9)   3)",
       "( (sqrt 16)   4)",
       "( (sqrt 25)   5)",
       "( (sqrt 36)   6)",
       "( (sqrt 49)   7)",
       "( (sqrt 64)   8)",
       "( (sqrt 81)   9)",
       "( (sqrt 100)   10)",
       "( (sqrt 121)   11)",
       "( (sqrt 144)   12)"
       ));
  
    // list of patterns to perform differentiation
    public static Cons derivpats = readlist( list(
       "( (deriv ?x ?x)   1)",

       "( (deriv (+ ?u ?v) ?x)  (+ (deriv ?u ?x) (deriv ?v ?x)))",

       "( (deriv (- ?u ?v) ?x)  (- (deriv ?u ?x) (deriv ?v ?x)))",

       "( (deriv (- ?v) ?x)  (- (deriv ?v ?x))",

       "( (deriv (* ?u ?v) ?x)  (+ (* ?v (deriv ?u ?x)) (* ?u (deriv ?v ?x))))",
       "( (deriv (/ ?u ?v) ?x)  (/ (- (* ?v (deriv ?u ?x)) (* ?u (deriv ?v ?x)))) (expt v 2))",

       "( (deriv (expt ?u ?y) ?x)  (* (* ?y (expt ?u (- ?y 1))) (deriv ?u ?x))",

       "( (deriv (sqrt ?u) ?x)  (* (/ 1 (* 2 (sqrt ?u))) (deriv ?u ?x)))",

       "( (deriv (exp ?u) ?x)  (* (exp ?u) (deriv ?u ?x)))",

       "( (deriv (log ?u) ?x)  (/ (deriv ?u ?x) ?u)",

       "( (deriv (sin ?u) ?x)  (* (cos ?u) (deriv ?u ?x)",

       "( (deriv (cos ?u) ?x)  (* (- (sin ?u)) (deriv ?u ?x)",

       "( (deriv (tan ?u) ?x)  (* (+ (tan (expt ?u 2)) 1) (deriv ?u ?x)",

       "( (deriv ?y ?x)   0)"   // this must be last!
       ));

    // restructuring patterns for Lisp to Java
    public static Cons javarestructpats = readlist( list(
       "( (return (if ?test ?then)) (if ?test (return ?then)) )",
       "( (return (if ?test ?then ?else)) (if ?test (return ?then) (return  ?else)) )",
       "( (defun ?fun ?args ?code) (zdefun ?fun (arglist ?args) (progn (return ?code))) )",
       "( (setq ?x (+ ?x 1)) (incf ?x) )"
       ));

    // patterns for Lisp to Java syntax
    public static Cons javapats = readlist( list(
       "( (if ?test ?then) (if zspace zlparen zspace ?test zspace zrparen ztab zreturn ?then))",
       "( (if ?test ?then ?else) (if zspace zlparen zspace ?test zspace zrparen zspace { ztab zreturn ?then zuntab zreturn } zspace else zspace { zspace ztab zreturn ?else zuntab zreturn }))",
 
       "( (cons ?x ?y) (znothing cons zlparen ?x , zspace ?y zrparen))",
       "( (zdefun ?fun ?args ?code) (public zspace static zspace Object zspace ?fun zspace ?args zreturn ?code zreturn) )",
       "( (arglist (?x))   (zlparen Object zspace ?x zrparen))",
       "( (progn ?x) ({ ztab zreturn ?x zuntab zreturn }) )",
       "( (setq ?x ?y) (?x zspace = zspace ?y ; zreturn) )",
       "( (first ?x) (znothing first zlparen zlparen Cons zrparen ?x zrparen) )",
       "( (rest ?x) (znothing rest zlparen zlparen Cons zrparen ?x zrparen) )",

       "( (incf ?x) (?x + +))",
       "( (* ?x ?y) (?x zspace * zspace ?y))",
       "( (/ ?x ?y) (?x zspace / zspace ?y))",
       "( (+ ?x ?y) (?x zspace + zspace ?y))",
       "( (- ?x ?y) (?x zspace - zspace ?y))",

       "( (min ?x ?y) (Math.min zlparen ?x , zspace ?y zrparen))",
       "( (expt ?x ?y) (Math.pow zlparen ?x , zspace ?y zrparen))",
       "( (add1 ?x ?y) (?x zspace + zspace ?y zspace + zspace 1))",

       "( (> ?x ?y) (?x zspace > zspace ?y))",
       "( (< ?x ?y)  (zlparen ?x zspace < zspace ?y zrparen))",

       "( (or ?x ?y) (?x zspace || zspace ?y))",
       "( (and ?x ?y) (?x zspace && zspace ?y))",
       "( (not ?x) (! ?x))",

       "( (>= ?x ?y) (?x zspace >= zspace ?y))",
       "( (<= ?x ?y) (?x zspace <= zspace ?y))",

       "( (return ?x) (return zspace ?x ;))",        

       "( (?fun ?x)   (znothing ?fun zlparen ?x zrparen))"  // must be last
       ));
    

    // ****** your code ends here ******

    public static void main( String[] args ) {

        Cons eqnsbat =
            readlist(
              list("(= loss_voltage (* internal_resistance current))",
                   "(= loss_power (* internal_resistance (expt current 2)))",
                   "(= terminal_voltage (- voltage loss_voltage))",
                   "(= power (* terminal_voltage current))",
                   "(= work (* charge terminal_voltage))" ) );
        
        System.out.println("battery = " +
                           solveqns(eqnsbat, (Cons)
             reader("((current 0.3)(internal_resistance 4.0)(voltage 12.0))"),
                                    "terminal_voltage"));
        Cons eqnscirc =
            readlist(
              list("(= acceleration (/ (expt velocity 2) radius))",
                   "(= force        (* mass acceleration))",
                   "(= kinetic_energy   (* (/ mass 2) (expt velocity 2)))",
                   "(= moment_of_inertia (* mass (expt radius 2)))",
                   "(= omega (/ velocity radius))",
                   "(= angular_momentum (* omega moment_of_inertia))" ));
        
        System.out.println("circular = " +
                           solveqns(eqnscirc, (Cons)
                                    reader("((radius 4.0)(mass 2.0)(velocity 3.0))"),
                                    "angular_momentum"));
        Cons eqnslens =
            readlist(
              list("(= focal_length (/ radius 2))",
                   "(= (/ 1 focal_length) (+ (/ 1 image_distance) (/ 1 subject_distance)))",
                   "(= magnification (- (/ image_distance subject_distance)))",
                   "(= image_height (* magnification subject_height))" ));

        
        System.out.println("magnification = " +
                           solveqns(eqnslens, (Cons)
              reader("((subject_distance 6.0)(focal_length 9.0))"),
                                    "magnification"));
 
        Cons eqnslift =
            readlist(
              list("(= gravity     9.80665 )",
                   "(= weight      (* gravity mass))",
                   "(= force       weight)",
                   "(= work        (* force height))",
                   "(= speed       (/ height time))",
                   "(= power       (* force speed))",
                   "(= power       (/ work time)) ))" ));
        
        System.out.println("power = " +
                           solveqns(eqnslift, (Cons)
             reader("((weight 700.0)(height 8.0)(time 10.0)))"),
                                    "power"));
        Cons binaryfn = (Cons) reader(
          "(defun ?function (tree) (if (consp tree) (?combine (?function (first tree)) (?function (rest tree))) ?baseanswer))");

        for ( Cons ptr = substitutions; ptr != null; ptr = rest(ptr) ) {
            Object trans = sublis((Cons) first(ptr), binaryfn);
            System.out.println("sublis:  " + trans.toString()); }

        Cons opttests = readlist( list(
          "(+ 0 foo)",
          "(* fum 1)",
          "(- (- y))",
          "(- (- x y))",
          "(+ foo (* fum 0))"
          ));

        for ( Cons ptr = opttests; ptr != null; ptr = rest(ptr) ) {
            System.out.println("opt:  " + ((Cons)first(ptr)).toString());
            Object trans = transformfp(optpats, first(ptr));
            System.out.println("      " + trans.toString()); }


        Cons derivtests = readlist( list(
          "(deriv x x)",
          "(deriv 3 x)",
          "(deriv z x)",
          "(deriv (+ x 3) x)",
          "(deriv (* x 3) x)",
          "(deriv (* 5 x) x)",
          "(deriv (sin x) x)",
          "(deriv (sin (* 2 x)) x)",
          "(deriv (+ (expt x 2) (+ (* 3 x) 6)) x)",
          "(deriv (sqrt (+ (expt x 2) 2)) x)",
          "(deriv (log (expt (+ 1 x) 3)) x)"

          ));

        for ( Cons ptr = derivtests; ptr != null; ptr = rest(ptr) ) {
            System.out.println("deriv:  " + ((Cons)first(ptr)).toString());
            Object trans = transformfp(derivpats, first(ptr));
            System.out.println("  der:  " + trans.toString());
            Object transopt = transformfp(optpats, trans);
            System.out.println("  opt:  " + transopt.toString()); }

        Cons javatests = readlist( list(
          "(* fum 7)",
          "(setq x y)",
          "(setq x (+ x 1))",
          "(setq area (* pi (expt r 2)))",
          "(if (> x 7) (setq y x))",
          "(if (or (> x 7) (< y 3)) (setq y x))",
          "(if (and (> x 7) (not (< y 3))) (setq y x))",
          "(defun factorial (n) (if (<= n 1) 1 (* n (factorial (- n 1)))))"
          ));

        for ( Cons ptr = javatests; ptr != null; ptr = rest(ptr) ) {
            System.out.println("java:  " + ((Cons)first(ptr)).toString());
            Cons restr = (Cons) transformfp(javarestructpats, first(ptr));
            System.out.println("       " + restr.toString());
            Cons trans = (Cons) transformfp(javapats, restr);
            System.out.println("       " + trans.toString());
            javaprintlist(trans, 0);
            System.out.println(); }


        for ( Cons ptr = substitutions; ptr != null; ptr = rest(ptr) ) {
            Object ltrans = sublis((Cons) first(ptr), binaryfn);
            System.out.println("sublis:  " + ltrans.toString());
            Cons restr = (Cons) transformfp(javarestructpats, ltrans);
            System.out.println("       " + restr.toString());
            Cons trans = (Cons) transformfp(javapats, restr);
            System.out.println("       " + trans.toString());
            javaprintlist(trans, 0);
            System.out.println(); } 

      }

}
