import { assertEquals } from "https://deno.land/std@0.117.0/testing/asserts.ts";

Deno.test("day 6.1", async () => {
  const input = await Deno.readTextFile("./testinput");
  assertEquals(solution1(input), 5934);
});

Deno.test("day 6.2", async () => {
  const input = await Deno.readTextFile("./testinput");
  assertEquals(solution2(input), 26984457539);
});

export function solution1(input: string): number {
  const fish: number[] = parse(input);
  const result: number = evolve(fish, 80);
  return result;
}

export function solution2(input: string): number {
  const fish: number[] = parse(input);
  return evolve(fish, 256);
}

function parse(input: string): number[] {
  return input.split(",").filter(Boolean).map(Number);
}

const evolveOne = memoize(_evolveOne);

function _evolveOne(fish: number, days: number): number {
  if (days === 0) {
    return 1;
  } else {
    if (fish === 0) {
      return evolveOne(6, days - 1) + evolveOne(8, days - 1);
    } else {
      return evolveOne(fish - 1, days - 1);
    }
  }
}

function evolve(fish: number[], days: number): number {
  return fish.map((f) => evolveOne(f, days)).reduce(
    (acc, x) => acc + x,
    0,
  );
}

function memoize(
  fn: (a: number, b: number) => number,
): (a: number, b: number) => number {
  const cache: { [key: string]: number } = {};
  return (a, b) => {
    const key = JSON.stringify([a, b]);
    if (key in cache) {
      return cache[key];
    } else {
      const result = fn(a, b);
      cache[key] = result;
      return result;
    }
  };
}
