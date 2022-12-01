import { solution1, solution2 } from "./solution.ts";

const input = await Deno.readTextFile("./input");
const diagnostic = solution1(input);
console.log(diagnostic.gamma * diagnostic.epsilon);

const diagnostic2 = solution2(input);
console.log(diagnostic2.oxygen * diagnostic2.co2);
