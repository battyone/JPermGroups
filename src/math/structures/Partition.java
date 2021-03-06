package math.structures;

public final class Partition {
  private Partition parent;

  public Partition() {
    this.parent = this;
  }

  public boolean combine(Partition p) {
    Partition x = find();
    Partition y = p.find();
    if (x == y) {
      return false;
    } else {
      parent = x.parent = y;
      return true;
    }
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof Partition) {
      Partition p = (Partition) obj;
      return find() == p.find();
    }
    return false;
  }

  @Override public int hashCode() {
    return System.identityHashCode(find());
  }

  private Partition find() {
    if (parent == this) {
      return this;
    }
    return parent = parent.find();
  }
}
