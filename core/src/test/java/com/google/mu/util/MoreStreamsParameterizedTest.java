package com.google.mu.util;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MoreStreamsParameterizedTest {

  private final StreamKind kind;

  public MoreStreamsParameterizedTest(StreamKind kind) {
    this.kind = kind;
  }

  @Parameters(name = "{index}: {0}")
  public static StreamKind[] params() {
    return StreamKind.values();
  }

  @Test public void streamSizeDivisible() {
    assertThat(MoreStreams.dice(kind.natural(6), 2).collect(toList()))
        .containsExactly(asList(1, 2), asList(3, 4), asList(5, 6)).inOrder();
  }

  @Test public void streamSizeNotDivisible() {
    assertThat(MoreStreams.dice(kind.natural(5), 2).collect(toList()))
        .containsExactly(asList(1, 2), asList(3, 4), asList(5)).inOrder();
  }

  @Test public void streamSizeIsOne() {
    assertThat(MoreStreams.dice(kind.natural(3), 1).collect(toList()))
        .containsExactly(asList(1), asList(2), asList(3)).inOrder();
  }

  @Test public void emptyStream() {
    assertThat(MoreStreams.dice(kind.natural(0), 1).collect(toList()))
        .isEmpty();
  }

  @Test public void spliteratorAndStreamHaveEqualCharacteristics() {
    assertThat(MoreStreams.dice(kind.natural(1), 2).spliterator().characteristics())
        .isEqualTo(MoreStreams.dice(kind.natural(1).spliterator(), 2).characteristics());
  }

  private enum StreamKind {
    DEFAULT  {
      @Override Stream<Integer> natural(int numbers) {
        return IntStream.range(1, numbers + 1).boxed();
      }
    },
    ARRAY_BASED {
      @Override Stream<Integer> natural(int numbers) {
        return Arrays.asList(DEFAULT.natural(numbers).toArray(s -> new Integer[s]))
            .stream();
      }
    },
    TREE_SET_BASED {
      @Override Stream<Integer> natural(int numbers) {
        return new TreeSet<>(DEFAULT.natural(numbers).collect(toList())).stream();
      }
    },
    ;
    
    abstract Stream<Integer> natural(int numbers);
  }
}
