/**
 * this class Cons implements a Lisp-like Cons cell
 * 
 * @author  Gordon S. Novak Jr.
 * @version 29 Nov 01; 25 Aug 08; 05 Sep 08; 08 Sep 08; 12 Sep 08; 24 Sep 08
 *          02 Oct 09; 12 Feb 10; 04 Oct 12
 */

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

    public static int square(int x) { return x*x; }

    // iterative destructive merge using compareTo
public static Cons dmerj (Cons x, Cons y) {
  if ( x == null ) return y;
   else if ( y == null ) return x;
   else { Cons front = x;
          if ( ((Comparable) first(x)).compareTo(first(y)) < 0)
             x = rest(x);
            else { front = y;
                   y = rest(y); };
          Cons end = front;
          while ( x != null )
            { if ( y == null ||
                   ((Comparable) first(x)).compareTo(first(y)) < 0)
                 { setrest(end, x);
                   x = rest(x); }
               else { setrest(end, y);
                      y = rest(y); };
              end = rest(end); }
          setrest(end, y);
          return front; } }

public static Cons midpoint (Cons lst) {
  Cons current = lst;
  Cons prev = current;
  while ( lst != null && rest(lst) != null) {
    lst = rest(rest(lst));
    prev = current;
    current = rest(current); };
  return prev; }

    // Destructive merge sort of a linked list, Ascending order.
    // Assumes that each list element implements the Comparable interface.
    // This function will rearrange the order (but not location)
    // of list elements.  Therefore, you must save the result of
    // this function as the pointer to the new head of the list, e.g.
    //    mylist = llmergesort(mylist);
public static Cons llmergesort (Cons lst) {
  if ( lst == null || rest(lst) == null)
     return lst;
   else { Cons mid = midpoint(lst);
          Cons half = rest(mid);
          setrest(mid, null);
          return dmerj( llmergesort(lst),
                        llmergesort(half)); } }


    // ****** your code starts here ******
    // add other functions as you wish.

// merges elements from two sets that are members
// of either set
public static Cons union (Cons x, Cons y) {
  return mergeunion(llmergesort(x), llmergesort(y));
}

// following is a helper function for union
public static Cons mergeunion (Cons x, Cons y) {
  if (x == null)
    return y;
  if (y == null)
    return x;
  if(first(x).equals(first(y)))                           // x = y
    return mergeunion(rest(x), y);
  if (((Comparable)first(x)).compareTo(first(y)) < 0)     // x < y
    return cons(first(x), mergeunion(rest(x), y));
  return cons(first(y), mergeunion(x, rest(y)));          // x > y
}

// creates new sets with values unique to the first set
public static Cons setDifference (Cons x, Cons y) {
  return mergediff(llmergesort(x), llmergesort(y));
}

// following is a helper function for setDifference
public static Cons mergediff (Cons x, Cons y) {
  if (x == null)
    return null;
  if (y == null)
    return x;
  if (first(x).equals(first(y)))                          // x = y
    return mergediff(rest(x), rest(y));
  return cons(first(x), mergediff(rest(x), y));
}

// updates existent accounts, applies overdraft fees, 
// ignores non-existent accounts with negative balalances,
// creates new accounts for non-existent accounts with
// positive balances and returns this information as a new list
public static Cons bank(Cons accounts, Cons updates) {
  return mergebank(accounts, llmergesort(updates));
}

// helper function for bank
public static Cons mergebank(Cons x, Cons y) {
  if (x == null)
    return y;
  if (y == null)
    return x;

  // updates existent accounts
  if (first(x).equals(first(y))) {
    int balance = ((Account) first(x)).amount() + ((Account) first(y)).amount();
    Account updated = new Account(((Account)first(x)).name(), balance);
    // applies overdraft fees to accounts with negative balances
    if (balance < 0) {
      updated = new Account(updated.name(), balance - 30);
      System.out.println("Overdraft " + updated.name() + " " + updated.amount());
      return mergebank(cons(updated, rest(x)), rest(y));
    }
    // for balances >= 0
    return mergebank(cons(updated, rest(x)), rest(y));
  }

  // evaluates non-existent accounts
  boolean nonExistent = ((Account)first(x)).name().compareTo(((Account)first(y)).name()) > 0;
  if (nonExistent) {
    int balance = ((Account) first(y)).amount();
    // sets up a temporary account for non-existents
    Account temp = new Account(((Account) first(y)).name(), balance);
    //checks the final balance
    boolean postiveBal = checktransactions(y, temp);

    // obtains and prints the final balance for negative accounts
    if (!postiveBal) {
      while (second(y) != null && temp.name().equals(((Account)second(y)).name())) {
        balance += ((Account)first(rest(y))).amount(); 
        temp = new Account(temp.name(), balance);
        y = rest(y);
      }
      System.out.println("No account " + temp.name() + " " + balance);
      return mergebank(x, rest(y));
    }
    // announces and establishes new accounts
    System.out.println("New account " + temp.name() + " " + balance);
    return mergebank(cons(temp, x), rest(y));
  }

  // conses final account names and balances onto the new list
  return cons(first(x), mergebank(rest(x), y));
}

// evaluates the positivity of an account's final balance
public static boolean checktransactions(Cons y, Account tempAct) {
  if (y == null)
    return tempAct.amount() > 0;
  // applies future updates
  if (tempAct.equals(first(y))) {
    int balance = (tempAct.amount() + ((Account) first(y)).amount());
    tempAct = new Account(tempAct.name(), balance); 
    return checktransactions(rest(y), tempAct);
  }
  return checktransactions(rest(y), tempAct); 
}

// merges two sorted arrays x and y to produce a new sorted array
public static String [] mergearr(String [] x, String [] y) {
  return mergearrb(x, y, 0, 0, new String[0]);
}

// helper method for mergearr
public static String [] mergearrb(String [] x, String [] y, int i, int j, String [] z) {
   // x is null
  if (i == x.length) {
    int size = y.length - j + z.length;
    String[] result = new String[size];
    for (int index = 0; index < z.length; index++)
      result[index] = z[index];
    int tempj = j;
    for (int index = 0; index < (y.length - tempj); index++)
      result[index + z.length] = y[j++];
    return result;
  }
   // y is null
  if (j == y.length) {
    int size = x.length - i + z.length;
    String[] result = new String[size];
    for (int index = 0; index < z.length; index++)
      result[index] = z[index];
    int tempi = i;
    for (int index = 0; index < (x.length - tempi); index++)
      result[index + z.length] = x[i++];
    return result;
  }
   // x > y
  if (x[i].compareTo(y[j]) > 0) {
    int size = z.length + 1;
    String[] result = new String[size];
    result[size - 1] = y[j];
    for (int index = 0; index < z.length; index++)
      result[index] = z[index];
    return mergearrb(x, y, i, ++j, result);
  }
  // x <= y
  int size = z.length + 1;
  String[] result = new String[size];
  result[size - 1] = x[i];
  for (int index = 0; index < z.length; index++)
    result[index] = z[index];
  return mergearrb(x, y, ++i, j, result);
}

// checks to see if text in a markup language is well-formed
public static boolean markup(Cons text) {
  Cons myStack = null;
  boolean wellFormed = true;
  int i = 0;
  while (wellFormed && text != null) {
    String s = (String) first(text);
    // manage opening tags
    if (s.contains("<") && !s.contains("/")) {
      myStack = cons(s, myStack);
    }
    text = rest(text);
    // manage closing tags
    if (s.contains("/")) {
      // account for unexpected tags
      if (myStack == null || !((String)first(myStack)).substring(1).equals(s.substring(2))) {
        wellFormed = false;
        int size = s.length() - 1;
        System.out.println("Bad tag " + s.substring(2, size) + " at pos " + i + " should be " + ((String)first(myStack)));
      }
      // pop tags with matching closing tags
      myStack = rest(myStack);
    }
    i++;
  }
  // account for unclosed tags
  if (myStack != null && wellFormed) {
    wellFormed = false;
    String badTag = ((String)first(myStack));
    int size = badTag.length() - 1;
    System.out.println("Unbalanced tag " + badTag.substring(1, size));
  }
  return (wellFormed);
}

    // ****** your code ends here ******


    public static void main( String[] args )
      { 
        Cons set1 = list("d", "b", "c", "a");
        Cons set2 = list("f", "d", "b", "g", "h");
        System.out.println("set1 = " + Cons.toString(set1));
        System.out.println("set2 = " + Cons.toString(set2));
        System.out.println("union = " + Cons.toString(union(set1, set2)));

        Cons set3 = list("d", "b", "c", "a");
        Cons set4 = list("f", "d", "b", "g", "h");
        System.out.println("set3 = " + Cons.toString(set3));
        System.out.println("set4 = " + Cons.toString(set4));
        System.out.println("difference = " +
                           Cons.toString(setDifference(set3, set4)));

        Cons accounts = list(
               new Account("Arbiter", new Integer(498)),
               new Account("Flintstone", new Integer(102)),
               new Account("Foonly", new Integer(123)),
               new Account("Kenobi", new Integer(373)),
               new Account("Rubble", new Integer(514)),
               new Account("Tirebiter", new Integer(752)),
               new Account("Vader", new Integer(1024)) );

        Cons updates = list(
               new Account("Foonly", new Integer(100)),
               new Account("Flintstone", new Integer(-10)),
               new Account("Arbiter", new Integer(-600)),
               new Account("Garble", new Integer(-100)),
               new Account("Rabble", new Integer(100)),
               new Account("Flintstone", new Integer(-20)),
               new Account("Foonly", new Integer(10)),
               new Account("Tirebiter", new Integer(-200)),
               new Account("Flintstone", new Integer(10)),
               new Account("Flintstone", new Integer(-120))  );
        System.out.println("accounts = " + accounts.toString());
        System.out.println("updates = " + updates.toString());
        Cons newaccounts = bank(accounts, updates);
        System.out.println("result = " + newaccounts.toString());

        String[] arra = {"a", "big", "dog", "hippo"};
        String[] arrb = {"canary", "cat", "fox", "turtle"};
        String[] resarr = mergearr(arra, arrb);
        for ( int i = 0; i < resarr.length; i++ )
            System.out.println(resarr[i]);

        Cons xmla = list( "<TT>", "foo", "</TT>");
        Cons xmlb = list( "<TABLE>", "<TR>", "<TD>", "foo", "</TD>",
                          "<TD>", "bar", "</TD>", "</TR>",
                          "<TR>", "<TD>", "fum", "</TD>", "<TD>",
                          "baz", "</TD>", "</TR>", "</TABLE>" );
        Cons xmlc = list( "<TABLE>", "<TR>", "<TD>", "foo", "</TD>",
                          "<TD>", "bar", "</TD>", "</TR>",
                          "<TR>", "<TD>", "fum", "</TD>", "<TD>",
                          "baz", "</TD>", "</WHAT>", "</TABLE>" );
        Cons xmld = list( "<TABLE>", "<TR>", "<TD>", "foo", "</TD>",
                          "<TD>", "bar", "</TD>", "", "</TR>",
                          "</TABLE>", "</NOW>" );
        Cons xmle = list( "<THIS>", "<CANT>", "<BE>", "foo", "<RIGHT>" );
        Cons xmlf = list( "<CATALOG>",
                          "<CD>",
                          "<TITLE>", "Empire", "Burlesque", "</TITLE>",
                          "<ARTIST>", "Bob", "Dylan", "</ARTIST>",
                          "<COUNTRY>", "USA", "</COUNTRY>",
                          "<COMPANY>", "Columbia", "</COMPANY>",
                          "<PRICE>", "10.90", "</PRICE>",
                          "<YEAR>", "1985", "</YEAR>",
                          "</CD>",
                          "<CD>",
                          "<TITLE>", "Hide", "your", "heart", "</TITLE>",
                          "<ARTIST>", "Bonnie", "Tyler", "</ARTIST>",
                          "<COUNTRY>", "UK", "</COUNTRY>",
                          "<COMPANY>", "CBS", "Records", "</COMPANY>",
                          "<PRICE>", "9.90", "</PRICE>",
                          "<YEAR>", "1988", "</YEAR>",
                          "</CD>", "</CATALOG>");
        System.out.println("xmla = " + xmla.toString());
        System.out.println("result = " + markup(xmla));
        System.out.println("xmlb = " + xmlb.toString());
        System.out.println("result = " + markup(xmlb));
        System.out.println("xmlc = " + xmlc.toString());
        System.out.println("result = " + markup(xmlc));
        System.out.println("xmld = " + xmld.toString());
        System.out.println("result = " + markup(xmld));
        System.out.println("xmle = " + xmle.toString());
        System.out.println("result = " + markup(xmle));
        System.out.println("xmlf = " + xmlf.toString());
        System.out.println("result = " + markup(xmlf));
      }

}
