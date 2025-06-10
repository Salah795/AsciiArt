1.
Image: Represents an image in memory using a 2D array of Color objects. It
provides basic utilities for accessing pixel data and dimensions. It serves
as the foundation for processing images.
ImageEditor: provides static utility methods for image processing.
It performs the following key roles:
Padding: Adjusts image dimensions to the nearest power of 2 by adding
white borders.
Sub-image Extraction: Divides an image into smaller, equally-sized
square sub-images.
Brightness Calculation: Computes the average brightness of an image based
on grayscale weights.
This class cannot be instantiated and is designed for helper functions only.
SubImgCharMatcher: maps characters to their visual brightness (based on
their binary pixel representation) and allows matching a character to an
image sub-region based on brightness.
CharConverter: Converts ASCII characters into 2D boolean arrays,
representing their visual appearance in a monochrome grid.
AsciiArtAlgorithm: Implements the core logic for generating ASCII
art. It processes segmented sub-images, computes their brightness,
and matches them to ASCII characters using SubImgCharMatcher.
ConsoleAsciiOutput: Implements AsciiOutput to print a 2D array
of ASCII characters directly to the console, row by row.
HtmlAsciiOutput: It converts ASCII art into a styled HTML document, preserving
the visual structure and appearance using <pre>-like formatting with CSS.
Shell: allows users to interact with the program using text commands to:
Load and process an image.
Manage the character set (add/remove characters, view current set).
Adjust the ASCII resolution (detail level).
Select output method (console or HTML).
Generate and view ASCII art from the input image.


2.
HashMap<Double, HashSet<Character>> charBrightnessMap
Purpose:
Stores a mapping from brightness values to sets of characters that have that brightness.
Why HashMap?
Fast lookup (O(1) average) to find characters by brightness.
Supports efficient insert/delete operations.

HashSet<Character> (inside the map)
Purpose:
Used to store characters sharing the same brightness, preventing duplicates.
Why HashSet?
Ensures no duplicate characters at a brightness level.
Supports fast add, remove, and contains operations (O(1) average).

Set<Double> originalBrightnessSet = new HashSet<>(...)
Purpose:
Temporarily stores a copy of the brightness keys during brightness re-mapping
to avoid ConcurrentModificationException when modifying the map.
Why HashSet?
Simple set to store unique brightness values with fast access.

ArrayList<Character> charset = new ArrayList<>() (in getSortedCharset)
Purpose:
Temporarily holds all characters from all brightness groups, so they can be sorted and returned.
Why ArrayList?
Dynamic and resizable.
Fast sequential access.
Efficient with Collections.sort() for Character.

3.
All exceptions encountered are related to input/output operations, which are covered by `IOException`.
Therefore, we opted to throw `IOException` in all methods dealing with input/output and handle them in the
`run` method. This approach allows us to display appropriate messages to inform the user of any issues.
We determined that creating custom exception classes was unnecessary, as the standard `IOException` class
effectively addressed these scenarios.
