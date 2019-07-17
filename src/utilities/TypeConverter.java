/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import javafx.geometry.Point2D;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TypeConverter {
    
    /**
     * Converts a 2D point object into an array of 2 doubles.
     * @param point the 2D point to convert
     * @return the double array.
     */
    public static Double[] point2double(Point2D point) {
        Double[] location = new Double[2];
        location[0] = point.getX();
        location[1] = point.getY();
        return location;
    }
     
    /**
     * Converts an array of 2 doubles into a 2D point object.
     * @param location the 2D double array
     * @return the 2D point object.
     */
    public static Point2D double2point(Double[] location) {
        Point2D point = new Point2D(location[0], location[1]);
        return point;
    }
    
}
