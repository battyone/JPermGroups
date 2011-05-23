package math.algebra.permgroup;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import junit.framework.TestCase;
import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

public class PermutationGroupTests extends TestCase {
  private final ImmutableSet<Integer> domainP = ImmutableSet.of(1, 2, 3, 4);
  private final ImmutableSet<Integer> domainQ = ImmutableSet.of(5, 6, 7);
  private final ImmutableSet<Integer> domainPQ = ImmutableSet
    .<Integer> builder().addAll(domainP).addAll(domainQ).build();

  private final Permutation<Integer> p1 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 1, 3, 3, 4, 4));
  private final Permutation<Integer> p2 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 3, 3, 1, 4, 4));
  private final Permutation<Integer> p3 = Permutations.permutation(ImmutableMap
    .of(1, 3, 3, 1, 2, 2, 4, 4));
  private final Permutation<Integer> q1 = Permutations.permutation(ImmutableMap
    .of(5, 7, 7, 5, 6, 6));
  private final Permutation<Integer> pq1 = Permutations
    .permutation(ImmutableMap.<Integer, Integer> builder().putAll(p2.asMap())
      .putAll(q1.asMap()).build());

  @SuppressWarnings("unchecked") public void testCyclicGroup1() {
    PermutationGroup<Integer> group1 = Groups.generateGroup(domainP, p1);
    assertEquals(2, group1.size());
    assertTrue(group1.contains(Permutations.identity(domainP)));
    assertTrue(group1.contains(p1));
    assertFalse(group1.contains(p2));
  }

  @SuppressWarnings("unchecked") public void testCyclicGroup2() {
    PermutationGroup<Integer> group2 = Groups.generateGroup(domainP, p2);
    assertEquals(3, group2.size());
    assertTrue(group2.contains(Permutations.identity(domainP)));
    assertFalse(group2.contains(p1));
    assertTrue(group2.contains(p2));
  }

  @SuppressWarnings("unchecked") public void testGroup12() {
    PermutationGroup<Integer> group12 = Groups.generateGroup(domainP, p1, p2);
    assertEquals(6, group12.size());
    assertTrue(group12.contains(Permutations.identity(domainP)));
    assertTrue(group12.contains(p1));
    assertTrue(group12.contains(p2));
    assertTrue(group12.contains(p3));
  }

  @SuppressWarnings("unchecked") public void testSubgroup() {
    PermutationGroup<Integer> group1 = Groups.generateGroup(domainP, p1);
    PermutationGroup<Integer> group2 = Groups.generateGroup(domainP, p2);
    PermutationGroup<Integer> group12 = Groups.generateGroup(domainP, p1, p2);
    assertTrue(group1.isSubgroupOf(group12));
    assertTrue(group2.isSubgroupOf(group12));
    assertTrue(group12.isSubgroupOf(group12));
    assertFalse(group2.isSubgroupOf(group1));
    assertFalse(group1.isSubgroupOf(group2));
    assertFalse(group12.isSubgroupOf(group1));
  }

  @SuppressWarnings("unchecked") public void testSubgroupPredicate() {
    PermutationGroup<Integer> group1 = Groups.generateGroup(domainP, p1);
    PermutationGroup<Integer> group12 = Groups.generateGroup(domainP, p1, p2);
    Predicate<Permutation<Integer>> stabilizes3 =
        new Predicate<Permutation<Integer>>() {

          @Override public boolean apply(Permutation<Integer> input) {
            return Permutations.stabilizes(input, 3);
          }
        };
    assertEquals(group1, group12.subgroup(stabilizes3));
  }
}
