import { assertEquals } from "https://deno.land/std@0.107.0/testing/asserts.ts";

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

type Line = Point[];

type Point = { x: number; y: number };

export function solution1(input: string): number {
  const lines: Line[] = parse(input);
  const allPoints: Point[] = lines.flatMap((x) => x);
  const counts = new Map<string, number>();
  allPoints.forEach((p) => {
    const pkey = key(p);
    const value = counts.get(pkey);
    counts.set(pkey, (value || 0) + 1);
  });
  let result = 0;
  for (const [_key, value] of counts) {
    if (value > 1) {
      result = result + 1;
    }
  }
  return result;
}

export function solution2(input: string): number {
  const lines: Line[] = parse2(input);
  const allPoints: Point[] = lines.flatMap((x) => x);
  const counts = new Map<string, number>();
  allPoints.forEach((p) => {
    const pkey = key(p);
    const value = counts.get(pkey);
    counts.set(pkey, (value || 0) + 1);
  });
  let result = 0;
  for (const [_key, value] of counts) {
    if (value > 1) {
      result = result + 1;
    }
  }
  return result;
}

function key(p: Point): string {
  return `${p.x},${p.y}`;
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
        const list = [];
        for (let y = min(p1.y, p2.y); y <= max(p1.y, p2.y); y++) {
          list.push({ x: p1.x, y: y });
        }
        return list;
      } else {
        const list = [];
        for (let x = min(p1.x, p2.x); x <= max(p1.x, p2.x); x++) {
          list.push({ x, y: p1.y });
        }
        return list;
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
        const list = [];
        for (let y = min(p1.y, p2.y); y <= max(p1.y, p2.y); y++) {
          list.push({ x: p1.x, y: y });
        }
        return list;
      } else if (p1.y === p2.y) {
        const list = [];
        for (let x = min(p1.x, p2.x); x <= max(p1.x, p2.x); x++) {
          list.push({ x, y: p1.y });
        }
        return list;
      } else {
        // diagonal
        const list = [];
        const slope = (p2.y - p1.y) / (p2.x - p1.x);
        const b = p1.y - slope * p1.x;
        for (let x = min(p1.x, p2.x); x <= max(p1.x, p2.x); x++) {
          list.push({ x, y: slope * x + b });
        }
        return list;
      }
    });
}

function min(a: number, b: number): number {
  return a < b ? a : b;
}

function max(a: number, b: number): number {
  return a < b ? b : a;
}
