import { solution1, solution2 } from "./solution.ts";

const input = await Deno.readTextFile("./input");
const result = solution1(input);
console.log(result);

const result2 = solution2(input);
console.log(result2);
