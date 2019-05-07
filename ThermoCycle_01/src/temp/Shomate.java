/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
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
    
    public double getH(double T) {
        return A*T + B*Math.pow(T, 2)/2 + C*Math.pow(T, 3)/3 + D*Math.pow(T, 4)/4 - E/T + F - H;
    }
    
    public double getCp(double T) {
        return A + B*T + C*Math.pow(T, 2) + D*Math.pow(T, 3) - E/Math.pow(T, 2);
    }
    
    public double getS(double T) {
        return A*Math.log(T) + B*T + C*Math.pow(T, 2)/2 + D*Math.pow(T, 3)/3 - E/(2*Math.pow(T, 2)) + G;
    }
}
