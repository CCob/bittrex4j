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

package com.github.ccob.bittrex4j.listeners;

import com.github.ccob.bittrex4j.dao.ExchangeSummaryState;

public interface UpdateSummaryStateListener extends Listener<ExchangeSummaryState> {
    void onEvent(ExchangeSummaryState exchangeSummaryState);
}
