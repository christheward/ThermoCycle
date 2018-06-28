/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

/**
 *
 * @author Chris
 */
public abstract class Condition {
    
    public abstract void execute();
    protected abstract boolean match(Condition cnd);
    
    /**
     * Tries to execute the condition. If it fails then the conditions must be removed.
     * @return True if the condition need to be removed.
     */
    protected final boolean clean() {
        try {
            this.execute();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
}
