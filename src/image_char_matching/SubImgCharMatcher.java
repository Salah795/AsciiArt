//package image_char_matching;
//
//import java.util.*;
//
//public class SubImgCharMatcher {
//
//    private static final int CHAR_RESOLUTION_FACTOR = 16;
//    private static final int MAX_POSSIBLE_BRIGHTNESS = 1;
//    private static final int MIN_POSSIBLE_BRIGHTNESS = 0;
//    private static final int ILLEGAL_BRIGHTNESS = -1;
//
//    private final HashMap<Double, HashSet<Character>> charBrightnessMap;
//    private boolean initializationState;
//    private double minBrightness;
//    private double maxBrightness;
//
//    public SubImgCharMatcher(char[] charset) {
//        this.minBrightness = MAX_POSSIBLE_BRIGHTNESS;
//        this.maxBrightness = MIN_POSSIBLE_BRIGHTNESS;
//        this.initializationState = true;
//        this.charBrightnessMap = new HashMap<>();
//        for(char character: charset) {
//            addChar(character);
//        }
//        modifyAllCharsBrightness();
//        this.initializationState = false;
//    }
//
//    public char getCharByImageBrightness(double brightness) {
//        double minBrightnessDifference = MAX_POSSIBLE_BRIGHTNESS;
//        double closestCharBrightness = ILLEGAL_BRIGHTNESS;
//        for(double charBrightness: this.charBrightnessMap.keySet()) {
//            if(Math.abs(charBrightness - brightness) < minBrightnessDifference) {
//                minBrightnessDifference = Math.abs(charBrightness - brightness);
//                closestCharBrightness = charBrightness;
//            }
//        }
//        return Collections.min(this.charBrightnessMap.get(closestCharBrightness));
//    }
//
//    public void addChar(char c) {
//        double trueValueCounter = 0.0;
//        boolean[][] charBoolArray = CharConverter.convertToBoolArray(c);
//        for(boolean[] boolRow: charBoolArray) {
//            for(boolean boolValue: boolRow) {
//                if(boolValue) {
//                    trueValueCounter++;
//                }
//            }
//        }
//        double charBrightness = trueValueCounter / CHAR_RESOLUTION_FACTOR;
//        if(this.charBrightnessMap.containsKey(charBrightness)) {
//            this.charBrightnessMap.get(charBrightness).add(c);
//        } else {
//            HashSet<Character> charsSet = new HashSet<>();
//            charsSet.add(c);
//            this.charBrightnessMap.put(charBrightness, charsSet);
//        }
//        checkForModify(charBrightness);
//    }
//
//    public void removeChar(char c) {
//        Set<Double> originalBrightnessSet = new HashSet<>(this.charBrightnessMap.keySet());
//        for(double charBrightness: originalBrightnessSet) {
//            if(this.charBrightnessMap.get(charBrightness).contains(c)) {
//                this.charBrightnessMap.get(charBrightness).remove(c);
//                if(this.charBrightnessMap.get(charBrightness).isEmpty()) {
//                    this.charBrightnessMap.remove(charBrightness);
//                    if(this.maxBrightness == charBrightness) {
//                        this.maxBrightness = MIN_POSSIBLE_BRIGHTNESS;
//                        modifyAllCharsBrightness();
//                        return;
//                    }
//                    if(this.minBrightness == charBrightness) {
//                        this.minBrightness = MAX_POSSIBLE_BRIGHTNESS;
//                        modifyAllCharsBrightness();
//                        return;
//                    }
//                }
//            }
//        }
//    }
//
//    public List<Character> getSortedCharset() {
//        ArrayList<Character> charset = new ArrayList<>();
//        for(double charBrightness: this.charBrightnessMap.keySet()) {
//            charset.addAll(this.charBrightnessMap.get(charBrightness));
//        }
//        charset.sort(null);
//        return charset;
//    }
//
//    private void modifyAllCharsBrightness() {
//        Set<Double> originalBrightnessSet = new HashSet<>(this.charBrightnessMap.keySet());
//        for(double charBrightness: originalBrightnessSet) {
//            modifyCharBrightness(charBrightness);
//        }
//    }
//
//    private void modifyCharBrightness(double charBrightness) {
//    HashSet<Character> charsSet = this.charBrightnessMap.remove(charBrightness);
//    double newCharBrightness = (charBrightness - minBrightness) / (maxBrightness - minBrightness);
//    this.charBrightnessMap.put(newCharBrightness, charsSet);
//    }
//
//    private void checkForModify(double charBrightness) {
//        if(charBrightness < this.minBrightness) {
//            this.minBrightness = charBrightness;
//            if(!this.initializationState) {
//                modifyAllCharsBrightness();
//            }
//            return;
//        }
//        if(charBrightness > this.maxBrightness) {
//            this.maxBrightness = charBrightness;
//            if(!this.initializationState) {
//                modifyAllCharsBrightness();
//            }
//        } else if (!initializationState) {
//            modifyCharBrightness(charBrightness);
//        }
//    }
//}



package image_char_matching;


import java.util.*;

/**
 * Handles matching of ASCII characters to image brightness levels.
 * Utilizes pre-computed brightness values for efficiency.
 * @author wassan22, rulayounis
 */
public class SubImgCharMatcher {
    private final Map<Character, Double> charBrightnessMap = new HashMap<>();
    private double maxBrightness = Double.MIN_VALUE;
    private double minBrightness = Double.MAX_VALUE;
    private String roundMethod;

    /**
     * Constructs a SubImgCharMatcher with a set of characters.
     * Precomputes brightness values for the characters.
     * @param charset The array of characters to use for matching.
     */
    public SubImgCharMatcher(char[] charset) {
        precomputeCharBrightness(charset);
        this.roundMethod = "abs";
    }

    /**
     * Returns the ASCII character with the closest brightness value to the given brightness.
     * If multiple characters have the same brightness difference, the one with the lowest
     * ASCII value is returned.
     * @param brightness The brightness value to match.
     * @return The closest matching character.
     */
    public char getCharByImageBrightness(double brightness) {

        return locateClosestChar(brightness);
    }

    /**
     * Adds a new character to the character set and updates brightness bounds.
     * @param c The character to add.
     */
    public void addChar(char c) {
        if (charBrightnessMap.containsKey(c)) return;

        double brightness = calculateBrightness(c);

        charBrightnessMap.put(c, brightness);
        adjustBrightnessExtremes(brightness);

        if (this.roundMethod.equals("up")){
            brightness = Math.ceil(brightness);
        } else if(this.roundMethod.equals("down")){
            brightness = Math.floor(brightness);
        } else {
            brightness = Math.round(brightness);
        }
    }


    /** Sets the round method
     * possible values: up, dow, abs
     * @param newRoundMethod is a string of the new round method
     * */
    public void setRoundMethod(String newRoundMethod){
        this.roundMethod = newRoundMethod;
    }

    /**
     * Removes a character from the character set.
     * Recomputes brightness bounds if necessary.
     * @param c The character to remove.
     */
    public void removeChar(char c) {
        if (!charBrightnessMap.containsKey(c)) return;

        charBrightnessMap.remove(c);
        recalculateBrightnessBounds();
    }

    /**
     * Gets a list of characters in the current set, sorted by ASCII value.
     * @return A sorted list of characters.
     */
    public List<Character> getSortedChars() {
        return sortCharacterSet();
    }


    /**
     * Precomputes brightness values for all characters in the charset.
     * Updates the map and the minimum/maximum brightness values.
     * @param charset The array of characters for which brightness values will be calculated.
     */
    private void precomputeCharBrightness(char[] charset) {
        for (char c : charset) {
            double brightness = calculateBrightness(c);
            charBrightnessMap.put(c, brightness);
            maxBrightness = Math.max(maxBrightness, brightness);
            minBrightness = Math.min(minBrightness, brightness);
        }
    }

    /**
     * Calculates the brightness of a given character based on its pixel representation.
     * @param c The character whose brightness is being calculated.
     * @return The brightness value as a normalized double.
     */
    private double calculateBrightness(char c) {
        boolean[][] pixelArray = CharConverter.convertToBoolArray(c);
        int whitePixelCount = countWhitePixels(pixelArray);
        return whitePixelCount / Math.pow(16, 2);
    }

    /**
     * Counts the number of white pixels (true values) in a 2D boolean array.
     * @param grid A 2D boolean array representing a character's pixel data.
     * @return The count of white pixels in the array.
     */
    private int countWhitePixels(boolean[][] grid) {
        int whiteCount = 0;
        for (boolean[] row : grid) {
            for (boolean pixel : row) {
                if (pixel) whiteCount++;
            }
        }
        return whiteCount;
    }

    /**
     * Normalizes a brightness value based on the current minimum and maximum brightness values.
     * @param brightness The brightness value to normalize.
     * @return The normalized brightness value between 0 and 1.
     */
    private double normalizeBrightnessValue(double brightness) {
        return (brightness - minBrightness) / (maxBrightness - minBrightness);
    }

    /**
     * Finds the character with the closest brightness value to the target brightness.
     * Uses the smallest ASCII value as a tiebreaker when differences are equal.
     * @param targetBrightness The target brightness value.
     * @return The character closest to the given brightness value.
     */
    private char locateClosestChar(double targetBrightness) {
        char closestChar = '\0';
        double smallestDifference = Double.MAX_VALUE;

        for (Map.Entry<Character, Double> entry : charBrightnessMap.entrySet()) {
            char character = entry.getKey();
            double normalizedBrightness = normalizeBrightnessValue(entry.getValue());
            double difference = Math.abs(normalizedBrightness - targetBrightness);

            if (difference < smallestDifference ||
                    (difference == smallestDifference && character < closestChar)) {
                closestChar = character;
                smallestDifference = difference;
            }
        }

        return closestChar;
    }

    /**
     * Updates the minimum and maximum brightness values after adding a new character.
     * @param brightness The brightness of the newly added character.
     */
    private void adjustBrightnessExtremes(double brightness) {
        maxBrightness = Math.max(maxBrightness, brightness);
        minBrightness = Math.min(minBrightness, brightness);
    }

    /**
     * Recomputes the minimum and maximum brightness values by iterating over the map.
     * Resets the bounds to their initial values if the map is empty.
     */
    private void recalculateBrightnessBounds() {
        if (charBrightnessMap.isEmpty()) {
            maxBrightness = Double.MIN_VALUE;
            minBrightness = Double.MAX_VALUE;
            return;
        }

        maxBrightness = Double.MIN_VALUE;
        minBrightness = Double.MAX_VALUE;

        for (double brightness : charBrightnessMap.values()) {
            maxBrightness = Math.max(maxBrightness, brightness);
            minBrightness = Math.min(minBrightness, brightness);
        }
    }

    /**
     * Retrieves a sorted list of characters currently in the set.
     * @return A list of characters sorted by their ASCII values.
     */
    private List<Character> sortCharacterSet() {
        List<Character> characters = new ArrayList<>(charBrightnessMap.keySet());
        Collections.sort(characters);
        return characters;
    }
}

