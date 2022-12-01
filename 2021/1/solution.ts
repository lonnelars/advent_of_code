import { assertEquals } from "https://deno.land/std@0.107.0/testing/asserts.ts";

Deno.test("day 1 spec", async () => {
  const text = await Deno.readTextFile("./testinput");
  assertEquals(solution1(text), "7");
  assertEquals(solution2(text), "5");
});

type maybeNumber = number | null;
type accumulator = [maybeNumber, number];

export function solution1(text: string): string {
  const numbers: number[] = text.split("\n").filter((n) => n != null && n != "")
    .map((s) => parseInt(s, 10));

  function fn(
    acc: accumulator,
    n: number,
  ): accumulator {
    if (acc[0] === null) {
      return [n, acc[1]];
    } else {
      return n > acc[0] ? [n, acc[1] + 1] : [n, acc[1]];
    }
  }
  const result: accumulator = numbers.reduce(fn, [null, 0]);
  return result[1].toString();
}

type window = [number, number, number];
type accumulator2 = {
  window: window;
  numIncreases: number;
};
type reducer = (acc: accumulator2, n: number) => accumulator2;

export function solution2(text: string): string {
  const reducer: reducer = (acc, n) => {
    const { window, numIncreases } = acc;
    const [_, depth2, depth3] = window;

    const newWindow: window = [depth2, depth3, n];
    const previousSum = sum(window);
    const currentSum = sum(newWindow);
    return currentSum > previousSum
      ? { window: newWindow, numIncreases: numIncreases + 1 }
      : { window: newWindow, numIncreases };
  };

  const numbers: number[] = text.split("\n").filter((n) => n != null && n != "")
    .map((s) => parseInt(s, 10));

  if (numbers.length < 3) {
    throw "less than 3 numbers";
  }

  const result: accumulator2 = numbers.slice(3).reduce(reducer, {
    window: [numbers[0], numbers[1], numbers[2]],
    numIncreases: 0,
  });
  return result.numIncreases.toString();
}

function sum(window: window): number {
  return window.reduce((a, b) => a + b, 0);
}
