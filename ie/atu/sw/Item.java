package ie.atu.sw;

// record Item has two properties base and code of type String
// base stores text part of the mapping file
// code stores text value of the corresponding code
// also Item implements interface Comparable
// it needs for sorting words and suffixes in order to optimisation process of encoding
public record Item(String base, String code) implements Comparable<Item> {
    public int compareTo(Item i) {
        // firstly we sort words by length
        if (this.base().length() != i.base().length()) {
            return i.base().length() - this.base().length();
        }

        // and then by alphabet
        return this.base().compareTo(i.base());
    }
}
