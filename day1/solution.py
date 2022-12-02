import sys

def iter_elves(lines):
    calories = 0
    for line in lines:
        if line == "":
            yield calories
            calories = 0
        else:
            calories += int(line)
    yield calories


input = (
    line.strip()
    for line in sys.stdin.readlines()
)

sorted_elves = sorted(iter_elves(input), reverse=True)

print(f"Part 1: {sorted_elves[0]}")

print(f"Part 2: {sum(sorted_elves[:3])}")
