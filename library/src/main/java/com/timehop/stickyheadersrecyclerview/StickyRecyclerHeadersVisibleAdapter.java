/*
 * Copyright (c) 2015 Lineage Labs, Inc.  All Rights Reserved.
 *
 * This Software contains confidential information and trade secrets of
 * Lineage Labs, Inc.  Use, disclosure or reproduction is prohibited
 * without the prior express written permission of Lineage Labs, Inc.
 *
 * LINEAGE LABS, INC MAKES NO REPRESENTATION OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT.  LINEAGE LABS, INC SHALL NOT
 * BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * The Licensed Software and Documentation are deemed to be commercial
 * computer software and commercial computer software documentation as
 * defined in FAR Sections 12.212 and DFARS Section 227.7202.
 *
 */

package com.timehop.stickyheadersrecyclerview;

/**
 * Created by gray on 2/12/16.
 */
public interface StickyRecyclerHeadersVisibleAdapter {

    /**
     *
     * Return true the specified adapter position is visible, false otherwise
     *
     * @param position the adapter position
     */
    boolean isPositionVisible(final int position);
}
