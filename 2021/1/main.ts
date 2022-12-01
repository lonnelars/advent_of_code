import { solution1, solution2 } from "./solution.ts";

const text = await Deno.readTextFile("./input");
[solution1, solution2]
  .map((f) => f(text))
  .forEach((result) => console.log(result));
