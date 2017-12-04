package common;
  // Unordered tuple
  public class Tuple<F, S> {
    public F first;
    public S second;

    public Tuple(F f, S s) {
      first = f;
      second = s;
    }

    @Override
    public boolean equals( Object o ) {
      if( o instanceof Tuple ) {
        Tuple t = ((Tuple) o);
        return (first.equals( t.first ) && second.equals( t.second )) ||
          (first.equals(t.second) && second.equals( t.first ));
      }

      return false;
    }

    @Override
    public int hashCode() {
      return first.hashCode() ^ second.hashCode();
    }
  }
