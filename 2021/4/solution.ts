import { assertEquals } from "https://deno.land/std@0.107.0/testing/asserts.ts";

Deno.test("day 4.1", async () => {
  const s = await Deno.readTextFile("./testinput");
  const result = solution1(s);
  assertEquals(result, 4512);
});

Deno.test("day 4.2", async () => {
  const s = await Deno.readTextFile("./testinput");
  const result = solution2(s);
  assertEquals(result, 1924);
});

type State = {
  winners: Winner[];
  boards: Board[];
};

type Winner = {
  board: Board;
  lastNumber: number;
};

type Board = Row[];

type Row = Cell[];

type Cell = {
  value: number;
  marked: boolean;
};

export function solution1(s: string): number {
  const finalState = solution(s);
  const score = calculateScore(finalState.winners[0]);
  return score;
}

export function solution2(s: string): number {
  const finalState = solution(s);
  const score = calculateScore(
    finalState.winners[finalState.winners.length - 1],
  );
  return score;
}

function solution(s: string): State {
  const { numbers, initialState } = parseInput(s);
  const finalState = numbers.reduce(loop, initialState);
  return finalState;

  function loop(acc: State, n: number): State {
    const marked: Board[] = acc.boards.map((board) => mark(board, n));
    return {
      winners: acc.winners.concat(newWinners(marked, n)),
      boards: marked.filter((board) => !isWinner(board)),
    };
  }

  function mark(board: Board, n: number): Board {
    return board.map((row) =>
      row.map((cell) => cell.value === n ? { ...cell, marked: true } : cell)
    );
  }

  function newWinners(boards: Board[], lastNumber: number): Winner[] {
    return boards.filter(isWinner).map((board) => ({ board, lastNumber }));
  }

  function isWinner(board: Board): boolean {
    return completedRows() > 0 || completedColumns() > 0;

    function completedRows(): number {
      return board.map((row) => row.every((cell) => cell.marked))
        .reduce((acc, b) => b ? acc + 1 : acc, 0);
    }

    function completedColumns(): number {
      return board[0].map((_, index) => board.every((row) => row[index].marked))
        .reduce((acc, b) => b ? acc + 1 : acc, 0);
    }
  }
}

function parseInput(s: string): { numbers: number[]; initialState: State } {
  const [head, ...tail] = s.split("\n");
  const numbers: number[] = head.split(",").map(Number);
  const boards: Board[] = parseBoards(tail);
  return { numbers, initialState: { boards, winners: [] } };
}

function parseBoards(lines: string[]): Board[] {
  return group(lines)
    .map((groupOfLines) => groupOfLines.map(toRow));
}

function group(lines: string[]): string[][] {
  const result: string[][] = [];
  lines.forEach((line) => {
    if (isBlank(line)) {
      result.push([]);
    } else {
      result[result.length - 1].push(line);
    }
  });
  return result;
}

function isBlank(s: string): boolean {
  return s == null || s === "";
}

function toRow(s: string): Row {
  return s.split(/\s+/).filter(Boolean).map(toCell);
}

function toCell(s: string): Cell {
  const n = Number(s);
  if (isNaN(n)) {
    throw `not a number: ${s}`;
  } else {
    return {
      marked: false,
      value: n,
    };
  }
}

function calculateScore(winner: Winner): number {
  const { board, lastNumber } = winner;
  const unmarked: Cell[] = board.flatMap((row) =>
    row.filter((cell) => !cell.marked)
  );
  const sum: number = unmarked.reduce((acc, cell) => acc + cell.value, 0);
  return sum * lastNumber;
}
