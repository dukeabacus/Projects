/**
 * Created by Kevin on 2017-04-16.
 */
public class Pair<L extends Comparable<L>, R extends Comparable<R>> implements Comparable<Pair<L, R>>{

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }
    public R getRight() { return right; }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }


    public int compareTo(Pair<L, R> pair) {
        return left.compareTo(pair.left);
    }
}
