/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class RealGas {
    
    // List of Shomate constants
    private final List<Shomate> data;
    
    /**
     * Constructor
     */
    public RealGas() {
        data = new ArrayList();
    }
    
    private Optional<RealGas.Shomate> getShomate(double T) {
        return data.stream().filter(s -> s.Tmin <= T && s.Tmax > T).findFirst();
    }
    
    public OptionalDouble getH(double T) {
        return getShomate(T).map(s -> s.getH(T)).orElse(OptionalDouble.empty());
    }
    
    public OptionalDouble getCp(double T) {
        return getShomate(T).map(s -> s.getCp(T)).orElse(OptionalDouble.empty());
    }
    
    public OptionalDouble getS(double T) {
        return getShomate(T).map(s -> s.getS(T)).orElse(OptionalDouble.empty());
    }
    
    public class Shomate {

        double Tmin, Tmax;
        double A, B, C, D, E, F, G, H;

        private Shomate(double Tmin, double Tmax, double A, double B, double C, double D, double E, double F, double G, double H) {
            this.Tmin = Tmin;
            this.Tmax = Tmax;
            this.A = A;
            this.B = B;
            this.C = C;
            this.D = D;
            this.E = E;
            this.F = F;
            this.G = G;
            this.H = H;
        }
        
        /**
         * Checks to see if the temperature is valid
         * @param T the temperature in K
         * @return True if the temperature is valid
         */
        private boolean checkTemperature(double T) {
            return (Tmin <= T & T < Tmax);
        }
        
        /**
         * Gets the enthalpy at temperature T
         * @param T the temperature in K
         * @return an OptionalDouble containing the enthalpy in the temperature is valid
         */
        public OptionalDouble getH(double T) {
            if (checkTemperature(T)) {
                return OptionalDouble.of(A*T + B*Math.pow(T, 2)/2 + C*Math.pow(T, 3)/3 + D*Math.pow(T, 4)/4 - E/T + F - H);
            }
            return OptionalDouble.empty();
        }

        /**
         * Gets the heat capacity at constant pressure at temperature T
         * @param T the temperature in K
         * @return an OptionalDouble containing the heat capacity at constant pressure in the temperature is valid
         */        
        public OptionalDouble getCp(double T) {
            if (checkTemperature(T)) {
                return OptionalDouble.of(A + B*T + C*Math.pow(T, 2) + D*Math.pow(T, 3) - E/Math.pow(T, 2));
            }
            return OptionalDouble.empty();
        }
        
        /**
         * Gets the entropy at temperature T
         * @param T the temperature in K
         * @return an OptionalDouble containing the entropy in the temperature is valid
         */
        public OptionalDouble getS(double T) {
            if (checkTemperature(T)) {
                return OptionalDouble.of(A*Math.log(T) + B*T + C*Math.pow(T, 2)/2 + D*Math.pow(T, 3)/3 - E/(2*Math.pow(T, 2)) + G);
            }
            return OptionalDouble.empty();
        }
    }
}
