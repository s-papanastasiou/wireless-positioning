/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package general;

/**
 *
 * @author Gerg
 */
public class ResultPoint {
    
    private final Point draw;
    private final Point global;

    public ResultPoint(Point draw, Point global) {
        this.draw = draw;
        this.global = global;
    }

    public Point getDraw() {
        return draw;
    }

    public Point getGlobal() {
        return global;
    }
    
    
    
}
