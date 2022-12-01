import { assertEquals } from "https://deno.land/std@0.107.0/testing/asserts.ts";

Deno.test("day 3.1", async () => {
  const input = await Deno.readTextFile("./testinput");
  const { gamma, epsilon } = solution1(input);
  assertEquals(gamma, 22);
  assertEquals(epsilon, 9);
});

Deno.test("day 3.2", async () => {
  const input = await Deno.readTextFile("./testinput");
  const { oxygen, co2 } = solution2(input);
  assertEquals(oxygen, 23);
  assertEquals(co2, 10);
});

type Power = {
  gamma: number;
  epsilon: number;
};

type LifeSupport = {
  oxygen: number;
  co2: number;
};

type Count = {
  ones: number;
  zeros: number;
};

type BinaryDigit = 0 | 1;

type BinaryNumber = BinaryDigit[];

export function solution1(input: string): Power {
  const gamma = parse(input).reduce(go, [])
    .map((count) => count.ones > count.zeros ? 1 : 0)
    .join("");
  return {
    gamma: parseInt(gamma, 2),
    epsilon: parseInt(gamma, 2) ^ (2 ** (gamma.length) - 1),
  };

  function go(acc: Count[], n: BinaryNumber): Count[] {
    return n.map((value, index) => {
      const count = acc[index] || { ones: 0, zeros: 0 };
      return value === 1
        ? { ...count, ones: count.ones + 1 }
        : { ...count, zeros: count.zeros + 1 };
    });
  }
}

function parse(input: string): BinaryNumber[] {
  return input.split("\n")
    .filter(Boolean)
    .map((s) => s.split("").map(parseBinary));
}

function parseBinary(s: string): BinaryDigit {
  const n = Number(s);
  if (n === 0 || n === 1) {
    return n;
  } else {
    throw `invalid binary digit: ${n}`;
  }
}

export function solution2(input: string): LifeSupport {
  const numbers = parse(input);
  const oxygen = search(oxygenCriteria, numbers, 0);
  const co2 = search(co2Criteria, numbers, 0);
  return { oxygen, co2 };

  function oxygenCriteria(count: Count): BinaryDigit {
    return count.ones >= count.zeros ? 1 : 0;
  }

  function co2Criteria(count: Count): BinaryDigit {
    return count.ones >= count.zeros ? 0 : 1;
  }

  function search(
    criteria: (count: Count) => BinaryDigit,
    numbers: BinaryNumber[],
    index: number,
  ): number {
    const count = numbers.map((s) => s[index])
      .map(Number)
      .reduce(
        (acc, n) =>
          n === 1
            ? { ...acc, ones: acc.ones + 1 }
            : { ...acc, zeros: acc.zeros + 1 },
        { ones: 0, zeros: 0 },
      );
    const result = numbers.filter((n) => n[index] === criteria(count));
    return result.length === 1
      ? parseInt(result[0].join(""), 2)
      : search(criteria, result, index + 1);
  }
}
