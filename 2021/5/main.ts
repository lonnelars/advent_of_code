import { solution1, solution2 } from "./solution.ts";

const input: string = await Deno.readTextFile("./input");
const result = solution1(input);
console.log(result);

console.log(solution2(input));
