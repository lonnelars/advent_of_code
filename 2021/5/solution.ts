import { assertEquals } from "https://deno.land/std@0.107.0/testing/asserts.ts";
import {
  Record,
  Set,
} from "https://deno.land/x/immutable@4.0.0-rc.14-deno/mod.ts";

Deno.test("day 5.1", async () => {
  const input = await Deno.readTextFile("./testinput");
  const result = solution1(input);
  assertEquals(result, 5);
});

Deno.test("day 5.2", async () => {
  const input = await Deno.readTextFile("./testinput");
  const result = solution2(input);
  assertEquals(result, 12);
});

type Line = Set<Point>;

type Point = { x: number; y: number };

const Point = Record({ x: 0, y: 0 });

type Accumulator = { points: Set<Point>; commonPoints: Set<Point> };

export function solution1(input: string): number {
  const lines: Line[] = parse(input);
  const result: Accumulator = lines.reduce(findCommonPoints, {
    points: Set(),
    commonPoints: Set(),
  });
  return result.commonPoints.count();
}

export function solution2(input: string): number {
  const lines: Line[] = parse2(input);
  const result: Accumulator = lines.reduce(findCommonPoints, {
    points: Set(),
    commonPoints: Set(),
  });
  return result.commonPoints.count();
}

function parse(input: string): Line[] {
  const pattern = /(\d+),(\d+) -> (\d+),(\d+)/;
  return input.split("\n")
    .map((inputLine) => {
      const matches = pattern.exec(inputLine);
      if (matches && matches.length === 5) {
        const p1: Point = { x: Number(matches[1]), y: Number(matches[2]) };
        const p2: Point = { x: Number(matches[3]), y: Number(matches[4]) };
        return [p1, p2];
      } else {
        throw `no match: ${inputLine}`;
      }
    })
    .filter((points) => {
      const [p1, p2] = points;
      return p1.x === p2.x || p1.y === p2.y;
    })
    .map((points) => {
      const [p1, p2] = points;
      if (p1.x === p2.x) {
        let set = Set<Point>();
        for (let y = min(p1.y, p2.y); y <= max(p1.y, p2.y); y++) {
          set = set.add(Point({ x: p1.x, y: y }));
        }
        return set;
      } else {
        let set = Set<Point>();
        for (let x = min(p1.x, p2.x); x <= max(p1.x, p2.x); x++) {
          set = set.add(Point({ x, y: p1.y }));
        }
        return set;
      }
    });
}

function parse2(input: string): Line[] {
  const pattern = /(\d+),(\d+) -> (\d+),(\d+)/;
  return input.split("\n")
    .map((inputLine) => {
      const matches = pattern.exec(inputLine);
      if (matches && matches.length === 5) {
        const p1: Point = { x: Number(matches[1]), y: Number(matches[2]) };
        const p2: Point = { x: Number(matches[3]), y: Number(matches[4]) };
        return [p1, p2];
      } else {
        throw `no match: ${inputLine}`;
      }
    })
    .map((points) => {
      const [p1, p2] = points;
      if (p1.x === p2.x) {
        let set = Set<Point>();
        for (let y = min(p1.y, p2.y); y <= max(p1.y, p2.y); y++) {
          set = set.add(Point({ x: p1.x, y: y }));
        }
        return set;
      } else if (p1.y === p2.y) {
        let set = Set<Point>();
        for (let x = min(p1.x, p2.x); x <= max(p1.x, p2.x); x++) {
          set = set.add(Point({ x, y: p1.y }));
        }
        return set;
      } else {
        // diagonal
        let set = Set<Point>();
        const slope = (p2.y - p1.y) / (p2.x - p1.x);
        const b = p1.y - slope * p1.x;
        for (let x = min(p1.x, p2.x); x <= max(p1.x, p2.x); x++) {
          set = set.add(Point({ x, y: slope * x + b }));
        }
        return set;
      }
    });
}

function findCommonPoints(acc: Accumulator, line: Line): Accumulator {
  return {
    ...acc,
    points: acc.points.union(line),
    commonPoints: acc.commonPoints.union(acc.points.intersect(line)),
  };
}

function min(a: number, b: number): number {
  return a < b ? a : b;
}

function max(a: number, b: number): number {
  return a < b ? b : a;
}
