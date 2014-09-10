/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package general;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.draw);
        hash = 59 * hash + Objects.hashCode(this.global);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultPoint other = (ResultPoint) obj;
        if (!Objects.equals(this.draw, other.draw)) {
            return false;
        }
        if (!Objects.equals(this.global, other.global)) {
            return false;
        }
        return true;
    }
    
    
    
}
