package math.algebra.permgroup;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;

public abstract class AbstractPermutationGroup<E> extends
    AbstractSet<Permutation<E>> implements PermutationGroup<E> {
  @Override public boolean equals(@Nullable Object o) {
    if (o instanceof AbstractPermutationGroup) {
      @SuppressWarnings("unchecked")
      AbstractPermutationGroup<E> g = (AbstractPermutationGroup) o;
      return size() == g.size() && g.containsAll(generators());
    }
    return super.equals(o);
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#extend(java.lang.Iterable)
   */
  @Override public PermutationGroup<E> extend(
      Iterable<Permutation<E>> newGenerators) {
    List<Permutation<E>> newGs = Lists.newArrayList(generators());
    for (Permutation<E> g : newGenerators) {
      if (!contains(g)) {
        newGs.add(g);
      }
    }
    if (newGs.size() == generators().size()) {
      return this;
    }
    return new RegularPermutationGroup<E>(newGs, CosetTables.create(newGs));
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#generators()
   */
  @Override public abstract Collection<Permutation<E>> generators();

  @Override public boolean isEmpty() {
    return false;
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#isSubgroupOf(math.algebra.permgroup.AbstractPermutationGroup)
   */
  @Override public boolean isSubgroupOf(PermutationGroup<E> g) {
    return size() <= g.size() && g.containsAll(generators());
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#subgroup(java.util.List)
   */
  @Override public Subgroup<E> subgroup(
      List<? extends Predicate<? super Permutation<E>>> filters) {
    return RegularSubgroup.subgroup(this, filters);
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#subgroup(com.google.common.base.Predicate)
   */
  @Override public PermutationGroup<E> subgroup(
      Predicate<? super Permutation<E>> filter) {
    return subgroup(Collections.singletonList(filter));
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#support()
   */
  @Override public abstract Set<E> support();

  @Override public String toString() {
    Collection<Permutation<E>> generators = generators();
    StringBuilder builder = new StringBuilder(generators.size() * 10);
    builder.append('<');
    Joiner.on(", ").appendTo(builder, generators);
    builder.append('>');
    return builder.toString();
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#stabilizes(java.util.Set)
   */
  @Override public boolean stabilizes(Set<E> set) {
    for (Permutation<E> g : generators()) {
      if (!g.stabilizes(set))
        return false;
    }
    return true;
  }

}