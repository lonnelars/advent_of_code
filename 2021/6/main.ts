import { solution1, solution2 } from "./solution.ts";

performance.mark("a");
const input = await Deno.readTextFile("./input");
console.log(solution1(input));
console.log(solution2(input));
performance.mark("b");
console.log(performance.measure("a->b", "a", "b"));
