/*
 * *
 *  This file is part of the bittrex4j project.
 *
 *  @author CCob
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 * /
 */

package com.github.ccob.bittrex4j.dao;

public abstract class Deltas<DeltaType> {

    long nounce;
    DeltaType[] deltas;

    public Deltas(long nounce, DeltaType[] deltas) {
        this.nounce = nounce;
        this.deltas = deltas;
    }

    public DeltaType[] getDeltas() {
        return deltas;
    }

    public long getNounce() {
        return nounce;
    }
}
