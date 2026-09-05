"""
Renders the captured program output in docs/sample-output into terminal style PNG
images under docs/screenshots.

The text in every image is the real, unedited output of the program: the transcripts
are produced by feeding the input files in docs/demo-scripts to the menu, for example

    java -cp out hospital.app.Main --echo < docs/demo-scripts/01-bst-operations.txt

Usage (from the project root):

    python docs/make-screenshots.py
"""

import os
import sys

from PIL import Image, ImageDraw, ImageFont

HERE = os.path.dirname(os.path.abspath(__file__))
OUTPUT_DIR = os.path.join(HERE, "sample-output")
SCREENSHOT_DIR = os.path.join(HERE, "screenshots")

# Windows console colours.
BACKGROUND = (12, 12, 12)
FOREGROUND = (204, 204, 204)
TITLE_BAR = (32, 32, 32)
TITLE_TEXT = (200, 200, 200)

FONT_SIZE = 15
LINE_HEIGHT = 20
PADDING = 16
TITLE_BAR_HEIGHT = 30

COMMAND = "java -cp out hospital.app.Main"

# (image name, transcript file, first line, last line, window title)
SHOTS = [
    ("01-startup-and-sample-data", "01-bst-operations.txt", 1, 45, "Startup and sample data"),
    ("02-bst-inorder-traversal", "01-bst-operations.txt", 59, 90, "BST - in-order traversal"),
    ("03-bst-tree-structure", "01-bst-operations.txt", 97, 123, "BST - tree structure"),
    ("04-bst-insert-patient", "01-bst-operations.txt", 123, 140, "BST - insert a patient"),
    ("05-bst-search", "01-bst-operations.txt", 177, 216, "BST - search"),
    ("06-bst-delete", "01-bst-operations.txt", 253, 282, "BST - delete a patient"),
    ("07-bst-after-delete", "01-bst-operations.txt", 282, 312, "BST - records after deletion"),
    ("08-queue-display-and-peek", "02-emergency-queue.txt", 67, 110, "Queue - display and peek"),
    ("09-queue-enqueue", "02-emergency-queue.txt", 111, 144, "Queue - enqueue"),
    ("10-queue-dequeue", "02-emergency-queue.txt", 143, 178, "Queue - dequeue (treat next)"),
    ("11-queue-empty-handling", "02-emergency-queue.txt", 300, 330, "Queue - empty queue handling"),
    ("12-stack-display", "03-treatment-stack.txt", 66, 95, "Stack - display records"),
    ("13-stack-push", "03-treatment-stack.txt", 109, 131, "Stack - push"),
    ("14-stack-pop", "03-treatment-stack.txt", 152, 177, "Stack - pop"),
    ("15-stack-empty-handling", "03-treatment-stack.txt", 243, 270, "Stack - empty stack handling"),
    ("16-list-display-visits", "04-visit-history.txt", 66, 92, "Linked list - display visits"),
    ("17-list-add-visit", "04-visit-history.txt", 92, 124, "Linked list - add a visit"),
    ("18-list-search-visit", "04-visit-history.txt", 123, 167, "Linked list - search a visit"),
    ("19-list-remove-visit", "04-visit-history.txt", 166, 204, "Linked list - remove a visit"),
    ("20-integration-treatment", "05-full-workflow.txt", 108, 132, "All four structures in one step"),
    ("21-integration-undo", "05-full-workflow.txt", 231, 252, "Undo a treatment (stack + list)"),
    ("22-system-summary", "05-full-workflow.txt", 318, 346, "System summary"),
    ("23-test-suite-result", "06-test-suite.txt", 118, 150, "Test suite result"),
]


def load_font():
    """Uses Consolas when it is installed, otherwise falls back to any monospace font."""
    candidates = [
        r"C:\Windows\Fonts\consola.ttf",
        r"C:\Windows\Fonts\cour.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf",
        "/System/Library/Fonts/Menlo.ttc",
    ]
    for path in candidates:
        if os.path.exists(path):
            return ImageFont.truetype(path, FONT_SIZE)
    return ImageFont.load_default()


def read_block(file_name, first_line, last_line):
    path = os.path.join(OUTPUT_DIR, file_name)
    with open(path, "r", encoding="utf-8") as handle:
        lines = handle.read().split("\n")
    block = lines[first_line - 1:last_line]
    # Drop blank lines at the top and bottom so the window is tight around the content.
    while block and not block[0].strip():
        block.pop(0)
    while block and not block[-1].strip():
        block.pop()
    return block


def render(name, lines, title, font):
    char_width = font.getlength("M") if hasattr(font, "getlength") else 8
    longest = max((len(line) for line in lines), default = 40)
    width = int(char_width * max(longest, len(title) + 30)) + PADDING * 2
    height = TITLE_BAR_HEIGHT + PADDING * 2 + LINE_HEIGHT * len(lines)

    image = Image.new("RGB", (width, height), BACKGROUND)
    draw = ImageDraw.Draw(image)

    # Title bar with the command that produced the output.
    draw.rectangle([0, 0, width, TITLE_BAR_HEIGHT], fill = TITLE_BAR)
    draw.text((PADDING, 8), COMMAND + "    -    " + title, font = font, fill = TITLE_TEXT)

    y = TITLE_BAR_HEIGHT + PADDING
    for line in lines:
        draw.text((PADDING, y), line, font = font, fill = FOREGROUND)
        y += LINE_HEIGHT

    path = os.path.join(SCREENSHOT_DIR, name + ".png")
    image.save(path)
    return path


def main():
    if not os.path.isdir(OUTPUT_DIR):
        print("Run the demo scripts first, docs/sample-output is missing.")
        return 1

    os.makedirs(SCREENSHOT_DIR, exist_ok = True)
    font = load_font()

    for name, source, first_line, last_line, title in SHOTS:
        lines = read_block(source, first_line, last_line)
        if not lines:
            print("SKIPPED " + name + " - no content in that range")
            continue
        path = render(name, lines, title, font)
        print("wrote " + os.path.relpath(path, os.path.dirname(HERE)) + "  (" + str(len(lines)) + " lines)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
