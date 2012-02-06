/*
 *	Port from SWScrollView and SWTableView for iphone 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll;

import org.cocos2d.types.CGSize;

/**
 * Data source that governs table backend data.
 */
public interface CCTableViewDataSource {
        /**
         * cell height for a given table.
         *
         * @param table table to hold the instances of Class
         * @return cell size
         */
        public CGSize cellSizeForTable(CCTableView table);
        /**
         * a cell instance at a given index
         *
         * @param idx index to search for a cell
         * @return cell found at idx
         */
        public CCTableViewCell tableCellAtIndex(CCTableView table, int idx);
        /**
         * Returns number of cells in a given table view.
         *
         * @return number of cells
         */
        public int numberOfCellsInTableView(CCTableView table);
}
