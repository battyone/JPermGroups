package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkState;
import static math.structures.permutation.Permutations.compose;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.List;

import math.numbertheory.Factorization;
import math.numbertheory.Factorization.Factor;
import math.structures.permutation.Permutation;

public class SylowDecomposition<E> {
  public static <E> SylowDecomposition<E> sylow(AbstractPermutationGroup<E> group,
      BlockSystem<E> system) {
    Factorization factorization = Factorization.factorize(group.size());
    Ordering<Factor> biggestComponent = new Ordering<Factor>() {
      @Override public int compare(Factor left, Factor right) {
        return left.getProduct() - right.getProduct();
      }
    };
    Factor biggestFactor = biggestComponent.max(factorization);
    return sylow(group, system, biggestFactor.getPrime());
  }

  public static <E> SylowDecomposition<E> sylow(AbstractPermutationGroup<E> group,
      BlockSystem<E> system, int p) {
    return new SylowDecomposition<E>(group, system, p);
  }

  private static int factorOut(int n, int p) {
    if (n % p == 0) {
      int n2 = factorOut(n, p * p);
      return (n2 % p == 0) ? n2 / p : n2;
    }
    return n;
  }

  private AbstractPermutationGroup<E> sylowSubgroup;

  private List<Permutation<E>> cosetRepresentatives;

  private final int p;
  private final PermutationGroup<E> group;
  private final BlockSystem<E> blockSystem;

  private SylowDecomposition(AbstractPermutationGroup<E> group,
      BlockSystem<E> blockSystem, int p) {
    this.sylowSubgroup = Groups.trivial();
    this.cosetRepresentatives =
        Lists.newArrayListWithCapacity(factorOut(group.size(), p));
    this.blockSystem = blockSystem;
    this.p = p;
    this.group = group;
    for (Permutation<E> g : group.generators()) {
      pBuild(g);
    }
    checkState(sylowSubgroup.isSubgroupOf(group),
        "Sylow subgroup is not a subgroup");
    checkState(isPGroup(sylowSubgroup, p), "Sylow subgroup is not a p-group");
    checkState(
        group.size() / sylowSubgroup.size() == factorOut(group.size(), p),
        "Sylow subgroup is not a maximal p-subgroup");
    checkState(
        group.size() == cosetRepresentatives.size() * sylowSubgroup.size(),
        "Expected that # of coset representatives %s times Sylow subgroup size "
            + "%s would equal group size %s", cosetRepresentatives.size(),
        sylowSubgroup.size(), group.size());
  }

  public Collection<LeftCoset<E>> asCosetDecomposition() {
    return Collections2.transform(getCosetRepresentatives(),
        new Function<Permutation<E>, LeftCoset<E>>() {
          @Override public LeftCoset<E> apply(Permutation<E> sigma) {
            return LeftCoset.coset(sigma, sylowSubgroup);
          }
        });
  }

  public Collection<Permutation<E>> getCosetRepresentatives() {
    return cosetRepresentatives;
  }

  public int getP() {
    return p;
  }

  public PermutationGroup<E> getSylowSubgroup() {
    return sylowSubgroup;
  }

  private boolean isPGroup(PermutationGroup<E> g, int p) {
    return factorOut(blockSystem.blockAction(g).size(), p) == 1;
  }

  private void pBuild(Permutation<E> alpha) {
    for (Permutation<E> gamma : cosetRepresentatives) {
      AbstractPermutationGroup<E> tmp =
          sylowSubgroup
            .extend(ImmutableList.of(compose(gamma.inverse(), alpha)));
      if (isPGroup(tmp, p)) {
        sylowSubgroup = tmp;
        return;
      }
    }
    cosetRepresentatives.add(alpha);
    for (Permutation<E> g : group.generators()) {
      pBuild(compose(g, alpha));
    }
  }
}
