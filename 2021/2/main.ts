import { solution1, solution2 } from "./solution.ts";

const input = await Deno.readTextFile("./input");
[solution1, solution2]
  .map((fn) => fn(input))
  .map((state) => state.horizontal * state.depth)
  .forEach((result) => console.log(result));
