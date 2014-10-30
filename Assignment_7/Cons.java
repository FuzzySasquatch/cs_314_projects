/**
 * this class Cons implements a Lisp-like Cons cell
 * 
 * @author  Gordon S. Novak Jr.
 * @version 29 Nov 01; 25 Aug 08; 05 Sep 08; 08 Sep 08; 12 Sep 08; 24 Sep 08
 *          06 Oct 08; 07 Oct 08; 09 Oct 08; 23 Oct 08; 27 Mar 09; 06 Aug 10
 *          30 Dec 13
 */

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

public static Cons formulas = 
    list( list( "=", "s", list("*", new Double(0.5),
                               list("*", "a",
                                list("expt", "t", new Integer(2))))),
          list( "=", "s", list("+", "s0", list( "*", "v", "t"))),
          list( "=", "a", list("/", "f", "m")),
          list( "=", "v", list("*", "a", "t")),
          list( "=", "f", list("/", list("*", "m", "v"), "t")),
          list( "=", "f", list("/", list("*", "m",
                                         list("expt", "v", new Integer(2))),
                               "r")),
          list( "=", "h", list("-", "h0", list("*", new Double(4.94),
                                               list("expt", "t",
                                                    new Integer(2))))),
          list( "=", "c", list("sqrt", list("+",
                                            list("expt", "a",
                                                 new Integer(2)),
                                            list("expt", "b",
                                                 new Integer(2))))),
          list( "=", "v", list("*", "v0",
                               list("-", new Double(1.0),
                                    list("exp", list("/", list("-", "t"),
                                                     list("*", "r", "c"))))))
          );

    // Note: this list will handle most, but not all, cases.
    // The binary operators - and / have special cases.
public static Cons opposites = 
    list( list( "+", "-"), list( "-", "+"), list( "*", "/"),
          list( "/", "*"), list( "sqrt", "expt"), list( "expt", "sqrt"),
          list( "log", "exp"), list( "exp", "log") );

public static void printanswer(String str, Object answer) {
    System.out.println(str +
                       ((answer == null) ? "null" : answer.toString())); }

    // ****** your code starts here ******

// finds a path to a part of a cave that matches item
public static Cons findpath(Object item, Object cave) {
  Cons path;
  // dead end
  if (cave == null)
    return null;
  // item found
  if (cave.equals(item))
    return list("done");
  if (((String)item).equals(first((Cons)cave)))
    return cons("first", findpath(item, first((Cons)cave)));
  // explore rest
  if ((path = findpath(item, rest((Cons)cave))) != null)
    return cons("rest", path);
  // explore first
  if ((path = findpath(item, first((Cons)cave))) != null)
    return cons("first", path);
  return null;
}

// retrieves the contents of cave at a location specified by path
public static Object follow(Cons path, Object cave) {
  Object instruction = first(path);
  if (path == null)
    return null;
  if (instruction.equals("first"))
    return follow(rest(path), first((Cons)cave));
  if (instruction.equals("rest"))
    return follow(rest(path), rest((Cons)cave));
  return cave;
}

// finds the item in tree2 corresponding to item in tree1
public static Object corresp(Object item, Object tree1, Object tree2) {
  Cons path = findpath(item, tree1);
  return follow(path, tree2);
}

public static Cons solve(Cons e, String v) {
  //System.out.println("e is " + e);
  
  // Object op = op(e);
  // Object rhs = rhs(e);
  // Object lhs = lhs(e);
  if (lhs(e).equals(v)) {
    // System.out.println("e is " + e);
    return e;
  }

  if (rhs(e).equals(v)) {
    // System.out.println("e is " + e);
    return list(op(e), rhs(e), lhs(e));
  }

  

  if (consp(rhs(e))) {
    Object op = op((Cons)rhs(e));
    op = second(assoc(op, opposites));
    //System.out.println(op.equals("expt"));
    //System.out.println("first of lhs of rhs is " + first((Cons)lhs((Cons)rhs(e))));

    

    if ((solve(list("=", list(op, lhs(e), lhs((Cons)rhs(e))), rhs((Cons)rhs(e))), v)) != null) {
      // option 1
      return solve(list("=", list(op, lhs(e), lhs((Cons)rhs(e))), rhs((Cons)rhs(e))), v);
    }



    if ((solve(list("=", list(op, lhs(e), rhs((Cons)rhs(e))), lhs((Cons)rhs(e))), v)) != null) {
      // sqrt
      if (op.equals("sqrt"))
        return solve(list("=", list(op, lhs(e)), lhs((Cons)rhs(e))), v);
      // expt
      if (op.equals("expt")) {
        return solve(list("=", list(op, lhs(e), new Integer(2)), first((Cons)lhs((Cons)rhs(e)))), v);
      }
      // option 2
      return solve(list("=", list(op, lhs(e), rhs((Cons)rhs(e))), lhs((Cons)rhs(e))), v);
    }

    

  }
  // System.out.println("rhs != v and not Cons.");
  return null;
}

// public static Double solveit (Cons equations, String var, Cons values) {
// }

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
  System.out.println("Current tree = " + tree);
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
    if (op((Cons)tree).equals("/"))
      return eval(lhs((Cons)tree), bindings) / eval(rhs((Cons)tree), bindings);
    // expt
    if (op((Cons)tree).equals("expt") || op((Cons)tree).equals("exp"))
      return Math.pow(eval(lhs((Cons)tree), bindings), eval(rhs((Cons)tree), bindings));
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

    // ****** your code ends here ******

    public static void main( String[] args ) {

        // printanswer(" ", solve(list("=", "x", "3"), "x"));
        // printanswer(" ", solve(list("=", "3", "x"), "x"));
        // printanswer(" ", solve(list("=", "3", "y"), "x"));

        // printanswer(" ", solve(list("=", "x", list("+", "a", "b")), "a"));
        //printanswer(" ", solve(list("=", "c", list("sqrt", list("+", "a", "b"))), "a"));
        // printanswer(" ", solve(list("=", "x", list("expt", "v", new Integer(2))), "v"));

        // printanswer(" ", solve(list( "=", "f", list("/", list("*", list("expt", "v", new Integer(2)), "m"
        //                                  ),
        //                        "r")), "v"));

        // Cons cave = list("rocks", "gold", list("monster"));
        // Cons path = findpath("gold", cave);
        // printanswer("cave = " , cave);
        // printanswer("path = " , path);
        // printanswer("follow = " , follow(path, cave));

        // Cons caveb = list(list(list("green", "eggs", "and"),
        //                        list(list("ham"))),
        //                   "rocks",
        //                   list("monster",
        //                        list(list(list("gold", list("monster"))))));
        // Cons pathb = findpath("gold", caveb);
        // printanswer("caveb = " , caveb);
        // printanswer("pathb = " , pathb);
        // printanswer("follow = " , follow(pathb, caveb));

        // Cons treea = list(list("my", "eyes"),
        //                   list("have", "seen", list("the", "light")));
        // Cons treeb = list(list("my", "ears"),
        //                   list("have", "heard", list("the", "music")));
        // printanswer("treea = " , treea);
        // printanswer("treeb = " , treeb);
        // printanswer("corresp = " , corresp("light", treea, treeb));
        // System.out.println("formulas = ");
        // Cons frm = formulas;
        // Cons vset = null;
        // while ( frm != null ) {
        //     printanswer("   "  , ((Cons)first(frm)));
        //     // printanswer("vars = ", vars((Cons)first(frm)));
        //     vset = vars((Cons)first(frm));
        //     while ( vset != null ) {
        //         printanswer("       "  ,
        //             solve((Cons)first(frm), (String)first(vset)) );
        //         vset = rest(vset); }
        //     frm = rest(frm); }

        Cons bindings = list( list("a", (Double) 32.0),
                              list("t", (Double) 4.0));
        printanswer("Eval:      " , rhs((Cons)first(formulas)));
        printanswer("  bindings " , bindings);
        printanswer("  result = " , eval(rhs((Cons)first(formulas)), bindings));

       /* printanswer("Tower: " , solveit(formulas, "h0",
                                            list(list("h", new Double(0.0)),
                                                 list("t", new Double(4.0)))));

        printanswer("Car: " , solveit(formulas, "a",
                                            list(list("v", new Double(88.0)),
                                                 list("t", new Double(8.0)))));
        
        printanswer("Capacitor: " , solveit(formulas, "c",
                                            list(list("v", new Double(3.0)),
                                                 list("v0", new Double(6.0)),
                                                 list("r", new Double(10000.0)),
                                                 list("t", new Double(5.0)))));

        printanswer("Ladder: " , solveit(formulas, "b",
                                            list(list("a", new Double(6.0)),
                                                 list("c", new Double(10.0)))));

*/
      }

}
