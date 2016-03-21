package ar.fiuba.tdd.template.tp0;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegExGenerator {

    private int maxLength;
    private Random random;

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
        this.random = new Random();
    }

    private char randomChar() {
        int nroRandom =   (this.random.nextInt(255));
        return (char) nroRandom;
    }


    public boolean isFunctionChar(char character) {
        return (character == '.' || character == '+' || character == '*' || character == '?' || character == '[' || character == ']');
    }

    public boolean isScapeChar(char character) {
        return (character == '\\' );
    }

    public char charRandomIn(String set) {

        if (set.length() > 1) {
            int index = (this.random.nextInt(set.length() - 1));
            return set.charAt(index);
        } else if (set.length() == 1) {
            return set.charAt(0);
        } else {
            return (char) (this.random.nextInt(255));
        }
    }

    public String generateStringFromSet(char character, String bufferSet ) {
        StringBuffer buffer = new StringBuffer();
        if (isFunctionChar(character)) {
            if (character == '*') {
                int amount = (this.random.nextInt(this.maxLength));
                for (int i = 0; i < amount; i++) {
                    buffer.append(this.charRandomIn(bufferSet));
                }
            } else if (character == '+') {
                int amount = (this.random.nextInt(this.maxLength)) + 1;
                for (int i = 0; i < amount; i++) {
                    buffer.append(this.charRandomIn(bufferSet));
                }
            } else if (character == '?') {
                int amount = (this.random.nextInt() >= 0.5) ? 1 : 0;
                if (amount == 1) {
                    buffer.append(this.charRandomIn(bufferSet));
                }
            }
        } else {
            buffer.append(this.charRandomIn(bufferSet));
            if (character != '\0') {
                buffer.append(character);
            }
        }
        return buffer.toString();
    }

    public String generateResult(String regEx) {
        boolean lastIsEsc = Boolean.FALSE;
        boolean openBracket = Boolean.FALSE;
        boolean closeBracket = Boolean.FALSE;
        StringBuffer bufferSet = new StringBuffer();
        StringBuffer buffer = new StringBuffer();
        for (char actualChar : regEx.toCharArray() ) {
            if (openBracket && actualChar != ']') {
                bufferSet.append(actualChar);
            } else if (closeBracket) {
                buffer.append(this.generateStringFromSet(actualChar, bufferSet.toString()));
                closeBracket = Boolean.FALSE;
                bufferSet = new StringBuffer();
            } else {
                if (isFunctionChar(actualChar) && !lastIsEsc) {
                    if (actualChar == '.') {
                        buffer.append(this.randomChar());
                    } else if (actualChar == '[') {
                        openBracket = Boolean.TRUE;
                    } else if (actualChar == ']') {
                        openBracket = Boolean.FALSE;
                        closeBracket = Boolean.TRUE;
                    } else {
                        char lastChar = '\0';
                        if (buffer.length() > 0 && (actualChar == '?' || actualChar == '*') ) {
                            lastChar = buffer.charAt(buffer.length() - 1);
                            String bufferNewSize = buffer.substring(0, buffer.length() - 1);
                            buffer = new StringBuffer(bufferNewSize.toString());
                        }
                        buffer.append(this.generateStringFromSet(actualChar, String.valueOf(lastChar)));
                    }
                    lastIsEsc = Boolean.FALSE;
                } else if (isScapeChar(actualChar)) {
                    lastIsEsc = Boolean.TRUE;
                } else {
                    lastIsEsc = Boolean.FALSE;
                    buffer.append(actualChar);
                }
            }
        }
        if (closeBracket) {
            buffer.append(this.generateStringFromSet('\0', bufferSet.toString()));
        }
        return buffer.toString();
    }

    public List<String> generate(String regEx, int numberOfResults) {
        List<String> listResults = new ArrayList<String>();
        for (int i = 0; i < numberOfResults; i ++) {
            listResults.add(generateResult(regEx));
        }
        return listResults;
    }
}