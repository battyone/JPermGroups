package math.algebra.permgroup;

import com.google.common.base.Predicate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import math.structures.permutation.Permutation;

public interface PermGroup<E> extends Set<Permutation<E>> {

  /**
   * Returns the permutation group generated by this group and the specified
   * generators.
   */
  public abstract PermGroup<E> extend(
      Iterable<Permutation<E>> newGenerators);

  public abstract Collection<Permutation<E>> generators();

  public abstract boolean isSubgroupOf(PermGroup<E> g);

  /**
   * Returns the subgroup of elements satisfying all of the specified filters.
   */
  public abstract PermSubgroup<E> subgroup(
      List<? extends Predicate<? super Permutation<E>>> filters);

  /**
   * Returns the subgroup of elements satisfying the specified filter.
   */
  public abstract PermGroup<E> subgroup(
      Predicate<? super Permutation<E>> filter);

  public abstract Set<E> support();

  public abstract boolean stabilizes(Set<E> set);

}