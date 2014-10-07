// libtest.java      GSN    03 Oct 08; 21 Feb 12; 26 Dec 13
// 

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

interface Functor { Object fn(Object x); }

interface Predicate { boolean pred(Object x); }

@SuppressWarnings("unchecked")
public class libtest {

    // ****** your code starts here ******

// adds up a list of Integer using a LinkedList
public static Integer sumlist(LinkedList<Integer> lst) {
    int size = lst.size();
    return sumlistb(lst, 0, size - 1);
}

// helper function to sumlist
public static Integer sumlistb(LinkedList<Integer> lst, int sum, int size) {
    if (size < 0)
        return sum;
    return sumlistb(lst, sum + lst.get(size), size - 1);
}

// adds up a list of Integer using an ArrayList
public static Integer sumarrlist(ArrayList<Integer> lst) {
    int size = lst.size();
    return sumarrlistb(lst, 0, size - 1);
}

// helper function to sumarrlist
public static Integer sumarrlistb(ArrayList<Integer> lst, int sum, int size) {
    if (size < 0)
        return sum;
    return sumarrlistb(lst, sum + lst.get(size), size - 1);
}

// returns a list of items from the input list that satisfy predicate p
public static LinkedList<Object> subset (Predicate p,
                                          LinkedList<Object> lst) {
    LinkedList<Object> result = new LinkedList<Object>();
    ListIterator<Object> litr = lst.listIterator();

    while (litr.hasNext()) {
        Object item = litr.next();
        if (p.pred(item))
            result.add(item);
    }
    return result;
}

// removes values for a list that do not satisfy predicate p
public static LinkedList<Object> dsubset (Predicate p,
                                           LinkedList<Object> lst) {
    ListIterator<Object> litr = lst.listIterator();

    while (litr.hasNext()) {
        Object item = litr.next();
        if (!p.pred(item))  
            litr.remove();
    }
    return lst;
}

// returns the first item in a list that satisfies the predicate p
public static Object some (Predicate p, LinkedList<Object> lst) {
    ListIterator<Object> litr = lst.listIterator();

    while (litr.hasNext()) {
        Object item = litr.next();
        if (p.pred(item))  
            return item;
    }
    return null;
}

// returns a list containing the results applied by the functor
public static LinkedList<Object> mapcar (Functor f, LinkedList<Object> lst) {
    LinkedList<Object> result = new LinkedList<Object>();
    ListIterator<Object> litr = lst.listIterator();

    while (litr.hasNext()) {
        result.add(f.fn(litr.next()));
    }
    return result;
}

// merges two sorted lists
public static LinkedList<Object> merge (LinkedList<Object> lsta,
                                        LinkedList<Object> lstb) {
    LinkedList<Object> result = new LinkedList<Object>();
    ListIterator<Object> litra = lsta.listIterator();
    ListIterator<Object> litrb = lstb.listIterator();

    while (litra.hasNext() && litrb.hasNext()) {
        Object a = litra.next();
        Object b = litrb.next();
        if (((Comparable)a).compareTo(b) > 0) {
            result.add(b);
            litra.previous();
        }
        else {
            result.add(a);
            litrb.previous();
        }
    }
    // accounts for lists of different lengths
    while (litra.hasNext())
        result.add(litra.next());
    while (litrb.hasNext())
        result.add(litrb.next());

    return result;
}

// returns the input sorted in ascending order
public static LinkedList<Object> sort (LinkedList<Object> lst) {
    if (lst.size() == 1)
        return lst;

    ListIterator<Object> litr = lst.listIterator();
    LinkedList<Object> lsta = new LinkedList<Object>();
    LinkedList<Object> lstb = new LinkedList<Object>();

    // finds the midpoint and creates the first list
    int midpoint = lst.size() / 2;
    for (int i = 0; i < midpoint; i++)
        lsta.add(litr.next());
    // creates the second list
    while (litr.hasNext())
        lstb.add(litr.next());
    return merge(sort(lsta), sort(lstb));
}

// returns a set of elements that are members of both sets
public static LinkedList<Object> intersection (LinkedList<Object> lsta,
                                               LinkedList<Object> lstb) {
    return intersectionb(sort(lsta), sort(lstb));
}

// helper method for intersection
public static LinkedList<Object> intersectionb (LinkedList<Object> lsta,
                                               LinkedList<Object> lstb) {
    LinkedList<Object> result = new LinkedList<Object>();
    ListIterator<Object> litra = lsta.listIterator();
    ListIterator<Object> litrb = lstb.listIterator();

    while (litra.hasNext() && litrb.hasNext()) {
        Object a = litra.next();
        Object b = litrb.next();
        if (((Comparable)a).compareTo(b) > 0)
            litra.previous();
        else if (((Comparable)a).compareTo(b) < 0)
            litrb.previous();
        else // a = b
            result.add(a);
    }
    return result;
}

// returns a new list in the reverse order of the input list
public static LinkedList<Object> reverse (LinkedList<Object> lst) {
    LinkedList<Object> result = new LinkedList<Object>();
    ListIterator<Object> litr = lst.listIterator();

    while (litr.hasNext()) {
        result.addFirst(litr.next());
    }
    return result;
}

    // ****** your code ends here ******

    public static void main(String args[]) {
        LinkedList<Integer> lst = new LinkedList<Integer>();
        lst.add(new Integer(3));
        lst.add(new Integer(17));
        lst.add(new Integer(2));
        lst.add(new Integer(5));
        System.out.println("lst = " + lst);
        System.out.println("sum = " + sumlist(lst));

        ArrayList<Integer> lstb = new ArrayList<Integer>();
        lstb.add(new Integer(3));
        lstb.add(new Integer(17));
        lstb.add(new Integer(2));
        lstb.add(new Integer(5));
        System.out.println("lstb = " + lstb);
        System.out.println("sum = " + sumarrlist(lstb));

        final Predicate myp = new Predicate()
            { public boolean pred (Object x)
                { return ( (Integer) x > 3); }};

        LinkedList<Object> lstc = new LinkedList<Object>();
        lstc.add(new Integer(3));
        lstc.add(new Integer(17));
        lstc.add(new Integer(2));
        lstc.add(new Integer(5));
        System.out.println("lstc = " + lstc);
        System.out.println("subset = " + subset(myp, lstc));

        System.out.println("lstc     = " + lstc);
        System.out.println("dsubset  = " + dsubset(myp, lstc));
        System.out.println("now lstc = " + lstc);

        LinkedList<Object> lstd = new LinkedList<Object>();
        lstd.add(new Integer(3));
        lstd.add(new Integer(17));
        lstd.add(new Integer(2));
        lstd.add(new Integer(5));
        System.out.println("lstd = " + lstd);
        System.out.println("some = " + some(myp, lstd));

        final Functor myf = new Functor()
            { public Integer fn (Object x)
                { return new Integer( (Integer) x + 2); }};

        System.out.println("mapcar = " + mapcar(myf, lstd));

        LinkedList<Object> lste = new LinkedList<Object>();
        lste.add(new Integer(1));
        lste.add(new Integer(3));
        lste.add(new Integer(5));
        lste.add(new Integer(6));
        lste.add(new Integer(9));
        lste.add(new Integer(11));
        lste.add(new Integer(23));
        System.out.println("lste = " + lste);
        LinkedList<Object> lstf = new LinkedList<Object>();
        lstf.add(new Integer(2));
        lstf.add(new Integer(3));
        lstf.add(new Integer(6));
        lstf.add(new Integer(7));
        System.out.println("lstf = " + lstf);
        System.out.println("merge = " + merge(lste, lstf));

        lste = new LinkedList<Object>();
        lste.add(new Integer(1));
        lste.add(new Integer(3));
        lste.add(new Integer(5));
        lste.add(new Integer(7));
        System.out.println("lste = " + lste);
        lstf = new LinkedList<Object>();
        lstf.add(new Integer(2));
        lstf.add(new Integer(3));
        lstf.add(new Integer(6));
        lstf.add(new Integer(6));
        lstf.add(new Integer(7));
        lstf.add(new Integer(10));
        lstf.add(new Integer(12));
        lstf.add(new Integer(17));
        System.out.println("lstf = " + lstf);
        System.out.println("merge = " + merge(lste, lstf));

        LinkedList<Object> lstg = new LinkedList<Object>();
        lstg.add(new Integer(39));
        lstg.add(new Integer(84));
        lstg.add(new Integer(5));
        lstg.add(new Integer(59));
        lstg.add(new Integer(86));
        lstg.add(new Integer(17));
        System.out.println("lstg = " + lstg);

        System.out.println("intersection(lstd, lstg) = "
                           + intersection(lstd, lstg));
        System.out.println("reverse lste = " + reverse(lste));

        System.out.println("sort(lstg) = " + sort(lstg));
        
   }
}
