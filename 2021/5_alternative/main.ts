import { solution1, solution2 } from "./solution.ts";

const input: string = await Deno.readTextFile("./input");
console.log(solution1(input));
console.log(solution2(input));
