import { assertEquals } from "https://deno.land/std@0.107.0/testing/asserts.ts";

Deno.test("day 2.1", async () => {
  const input = await Deno.readTextFile("./testinput");
  assertEquals(solution1(input), { horizontal: 15, depth: 10, aim: 0 });
});

Deno.test("day 2.2", async () => {
  const input = await Deno.readTextFile("./testinput");
  assertEquals(solution2(input), { horizontal: 15, depth: 60, aim: 10 });
});

type State = {
  horizontal: number;
  depth: number;
  aim: number;
};

type Command = Forward | Up | Down;

type Forward = {
  kind: "forward";
  value: number;
};

type Up = {
  kind: "up";
  value: number;
};

type Down = {
  kind: "down";
  value: number;
};

const initialState: State = { horizontal: 0, depth: 0, aim: 0 };

export const solution1 = (s: string) =>
  parseCommands(s).reduce(runCommand1, initialState);
export const solution2 = (s: string) =>
  parseCommands(s).reduce(runCommand2, initialState);

function parseCommands(text: string): Command[] {
  return text.split("\n")
    .filter(Boolean)
    .map(parseCommand);
}

function parseCommand(s: string): Command {
  const pattern = /([a-z]+)\s+(\d+)/;
  const matches = pattern.exec(s);
  if (matches == null) {
    throw "cannot parse: " + s;
  }
  const [direction, value] = matches.slice(1);
  switch (direction) {
    case "forward":
    case "up":
    case "down":
      return { kind: direction, value: Number(value) };
    default:
      throw "invalid direction: " + direction;
  }
}

function runCommand1(state: State, command: Command): State {
  switch (command.kind) {
    case "forward":
      return { ...state, horizontal: state.horizontal + command.value };
    case "down":
      return { ...state, depth: state.depth + command.value };
    case "up":
      return { ...state, depth: state.depth - command.value };
  }
}

function runCommand2(state: State, command: Command): State {
  switch (command.kind) {
    case "forward":
      return {
        ...state,
        horizontal: state.horizontal + command.value,
        depth: state.depth + state.aim * command.value,
      };
    case "down":
      return { ...state, aim: state.aim + command.value };
    case "up":
      return { ...state, aim: state.aim - command.value };
  }
}
