// Account.java

    public class Account implements Comparable<Account> {
        private String name;
        private Integer amount;
        public Account(String nm, Integer amt) {
            name = nm;
            amount = amt; }
        public static Account account(String nm, Integer amt) {
            return new Account(nm, amt); }
        public String name() { return name; }
        public Integer amount() { return amount; }
        public boolean equals(Object x) {
            if ( x == null ) return false;
            else if ( getClass() != x.getClass() ) return false;
            else return name.equals( ((Account)x).name); }

        // return -1 to sort this account before x, else 1
        public int compareTo(Account x) {
            if (this.equals(x) && this.amount > 0 && this.amount > x.amount)
                return -1;
            if (this.equals(x) && this.amount < 0 && this.amount > x.amount)
                return 1;
            return this.name().compareTo(x.name());
        }

        public String toString() {
            return ( "(" + this.name + " " + this.amount + ")"); }
    }
