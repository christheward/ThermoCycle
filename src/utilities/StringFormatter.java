/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class StringFormatter {
    
    /**
     * Capitalises the first letter of each word using the rules of the default Locale.
     * @param string the string to convert.
     * @return the capitalised string.
     */
    public static String toCapitalFirst(String string) {
        StringBuilder sb = new StringBuilder();
        for (String substring: string.replace(" ", "_") .split("_")) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(StringFormatter.capitalise(substring));
        }
        return sb.toString();
    }
    
    /**
     * Converts the string to camel case using the rules of the default Locale.
     * @param string the string to convert.
     * @return the string in camel case.
     */
    public static String toCamelCase(String string) {
        return StringFormatter.toCapitalFirst(string).replace("_", "");
    }
    
    /**
     * Capitalises the first character of the string and forces all others to lower case.
     * @param string the string
     * @return the string with an upper case first character
     */
    private static String capitalise(String string) {
        if (string.length() > 1 ) {
            return string.substring(0, 1).toUpperCase().concat(string.substring(1).toLowerCase());
        }
        else if (string.length() > 0) {
            return string.toUpperCase();
        }
        else {
            return string;
        }
    }
    
}
