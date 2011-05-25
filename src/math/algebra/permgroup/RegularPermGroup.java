package math.algebra.permgroup;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;

class RegularPermGroup<E> extends AbstractPermGroup<E> {
  private transient CosetTables<E> cosetTables = null;
  final Collection<Permutation<E>> generators;

  /**
   * Constructs the permutation group generated by the specified collection of
   * generators.
   */
  RegularPermGroup(Collection<Permutation<E>> generators) {
    this.generators = generators;
  }

  /**
   * Constructs a permutation group with the specified generators and coset
   * tables.
   */
  RegularPermGroup(Collection<Permutation<E>> generators,
      CosetTables<E> cosetTables) {
    this.generators = ImmutableList.copyOf(generators);
    this.cosetTables = cosetTables;
  }

  /**
   * Constructs the permutation group generated by the permutations in the
   * specified coset tables.
   */
  RegularPermGroup(CosetTables<E> cosetTables) {
    this(cosetTables.getGenerators(), cosetTables);
  }

  @Override public boolean contains(@Nullable Object o) {
    if (o instanceof Permutation) {
      @SuppressWarnings("unchecked")
      Permutation<E> p = (Permutation) o;
      return cosetTables().generates(p);
    }
    return false;
  }

  @SuppressWarnings("unchecked") @Override public boolean containsAll(
      Collection<?> c) {
    if (c instanceof RegularPermGroup) {
      return ((RegularPermGroup) c).isSubgroupOf(this);
    } else if (c instanceof LCoset) {
      LCoset<?> coset = (LCoset<?>) c;
      return containsAll(coset.getGroup())
          && contains(coset.getRepresentative());
    }
    return super.containsAll(c);
  }

  /**
   * Returns the permutation group generated by this group and the specified
   * generators.
   */
  public PermGroup<E> extend(Iterable<Permutation<E>> newGenerators) {
    List<Permutation<E>> newGs = Lists.newArrayList();
    for (Permutation<E> g : newGenerators) {
      if (!contains(g)) {
        newGs.add(g);
      }
    }
    if (newGs.isEmpty()) {
      return this;
    }
    return new RegularPermGroup<E>(newGs, cosetTables().extend(newGs));
  }

  /**
   * Returns a collection of generators for this permutation group.
   */
  @Override public Collection<Permutation<E>> generators() {
    return (cosetTables == null) ? generators : cosetTables.getGenerators();
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return cosetTables().generatedIterator();
  }

  @Override public int size() {
    return cosetTables().size();
  }

  CosetTables<E> cosetTables() {
    return (cosetTables == null) ? cosetTables = CosetTables.create(generators)
        : cosetTables;
  }

  @Override public PermSubgroup<E> subgroup(
      Collection<? extends Predicate<? super Permutation<E>>> filters) {
    if (cosetTables != null) {
      CosetTables<E> tables =
          CosetTables.subgroupTables(cosetTables, generators(), filters);
      PermGroup<E> subgroup =
          new RegularPermGroup<E>(tables.drop(filters.size()));
      Collection<Permutation<E>> reps = tables.take(filters.size()).generated();
      return new SubgroupView<E>(reps, subgroup, this);
    }
    return super.subgroup(filters);
  }
}
