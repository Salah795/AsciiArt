package image_char_matching;

import java.util.*;

public class SubImgCharMatcher {

    private static final int CHAR_RESOLUTION_FACTOR = 16;
    private static final int MAX_POSSIBLE_BRIGHTNESS = 1;
    private static final int MIN_POSSIBLE_BRIGHTNESS = 0;
    private static final int ILLEGAL_BRIGHTNESS = -1;

    private final HashMap<Double, HashSet<Character>> charBrightnessMap;
    private boolean initializationState;
    private double minBrightness;
    private double maxBrightness;

    public SubImgCharMatcher(char[] charset) {
        this.minBrightness = MAX_POSSIBLE_BRIGHTNESS;
        this.maxBrightness = MIN_POSSIBLE_BRIGHTNESS;
        this.initializationState = true;
        this.charBrightnessMap = new HashMap<>();
        for(char character: charset) {
            addChar(character);
        }
        modifyAllCharsBrightness();
        this.initializationState = false;
    }

    public char getCharByImageBrightness(double brightness) {
        double minBrightnessDifference = MAX_POSSIBLE_BRIGHTNESS;
        double closestCharBrightness = ILLEGAL_BRIGHTNESS;
        for(double charBrightness: this.charBrightnessMap.keySet()) {
            if(Math.abs(charBrightness - brightness) < minBrightnessDifference) {
                minBrightnessDifference = Math.abs(charBrightness - brightness);
                closestCharBrightness = charBrightness;
            }
        }
        return Collections.min(this.charBrightnessMap.get(closestCharBrightness));
    }

    public void addChar(char c) {
        double trueValueCounter = 0.0;
        boolean[][] charBoolArray = CharConverter.convertToBoolArray(c);
        for(boolean[] boolRow: charBoolArray) {
            for(boolean boolValue: boolRow) {
                if(boolValue) {
                    trueValueCounter++;
                }
            }
        }
        double charBrightness = trueValueCounter / CHAR_RESOLUTION_FACTOR;
        if(this.charBrightnessMap.containsKey(charBrightness)) {
            this.charBrightnessMap.get(charBrightness).add(c);
        } else {
            HashSet<Character> charsSet = new HashSet<>();
            charsSet.add(c);
            this.charBrightnessMap.put(charBrightness, charsSet);
        }
        checkForModify(charBrightness);
    }

    //TODO check again.
    public void removeChar(char c) {
        Set<Double> originalBrightnessSet = new HashSet<>(this.charBrightnessMap.keySet());
        for(double charBrightness: originalBrightnessSet) {
            if(this.charBrightnessMap.get(charBrightness).contains(c)) {
                this.charBrightnessMap.get(charBrightness).remove(c);
                if(this.charBrightnessMap.get(charBrightness).isEmpty()) {
                    this.charBrightnessMap.remove(charBrightness);
                    if(this.maxBrightness == charBrightness) {
                        this.maxBrightness = MIN_POSSIBLE_BRIGHTNESS;
                        modifyAllCharsBrightness();
                        return;
                    }
                    if(this.minBrightness == charBrightness) {
                        this.minBrightness = MAX_POSSIBLE_BRIGHTNESS;
                        modifyAllCharsBrightness();
                        return;
                    }
                }
            }
        }
    }

    public List<Character> getSortedCharset() {
        ArrayList<Character> charset = new ArrayList<>();
        for(double charBrightness: this.charBrightnessMap.keySet()) {
            charset.addAll(this.charBrightnessMap.get(charBrightness));
        }
        charset.sort(null);
        return charset;
    }

    private void modifyAllCharsBrightness() {
        Set<Double> originalBrightnessSet = new HashSet<>(this.charBrightnessMap.keySet());
        for(double charBrightness: originalBrightnessSet) {
            modifyCharBrightness(charBrightness);
        }
    }

    private void modifyCharBrightness(double charBrightness) {
    HashSet<Character> charsSet = this.charBrightnessMap.remove(charBrightness);
    double newCharBrightness = (charBrightness - minBrightness) / (maxBrightness - minBrightness);
    this.charBrightnessMap.put(newCharBrightness, charsSet);
    }

    private void checkForModify(double charBrightness) {
        if(charBrightness < this.minBrightness) {
            this.minBrightness = charBrightness;
            if(!this.initializationState) {
                modifyAllCharsBrightness();
            }
            return;
        }
        if(charBrightness > this.maxBrightness) {
            this.maxBrightness = charBrightness;
            if(!this.initializationState) {
                modifyAllCharsBrightness();
            }
        } else if (!initializationState) {
            modifyCharBrightness(charBrightness);
        }
    }
}
