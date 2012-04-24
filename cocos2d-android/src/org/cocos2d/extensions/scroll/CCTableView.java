/*
 *	Port from SWScrollView and SWTableView for iphone 
 *	by Rodrigo Collavo on 02/03/2012
 */

package org.cocos2d.extensions.scroll;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.view.MotionEvent;

public class CCTableView extends CCScrollView implements CCScrollViewDelegate{

    public static final int CCTableViewFillTopDown = 1;
    public static final int CCTableViewFillBottomUp = 2;

    /**
     * vertical direction of cell filling
     */
    int      m_vordering;
    /**
     * index set to query the indexes of the cells used.
     */
    //TODO: use C++ object
    //NSMutableIndexSet* indices_;
    HashSet<Integer>  m_indices;
//    CCMutableArray<int*> *m_indices;
    /**
     * cells that are currently in the table
     */
    ArrayList<CCTableViewCell> m_cellsUsed;
    /**
     * free list of cells
     */
    ArrayList<CCTableViewCell> m_cellsFreed;
    /**
     * weak link to the delegate object
     */
    //SWTableViewDelegate*   tDelegate_;

    /**
     * data source
     */
	public CCTableViewDataSource dataSource;
    /**
     * delegate
     */
    //TODO: fix this delegate declaration
    public CCTableViewDelegate tDelegate;
    /**
     * determines how cell is ordered and filled in the view.
     */
    public int verticalFillOrder;
    
    public static CCTableView view(CCTableViewDataSource dataSource, CGSize size)
    {
        return view(dataSource, size, null);
    }
    
    public static CCTableView view(CCTableViewDataSource dataSource, CGSize size, CCNode container)
    {
        CCTableView table;
        table = new CCTableView(size, container);
        table.dataSource = dataSource;
        table._updateContentSize();
        
        return table;
    }
    
    public CCTableView(CGSize size, CCNode container)
    {
    	super(size);
    	
        m_cellsUsed         = new ArrayList<CCTableViewCell>();
        m_cellsFreed        = new ArrayList<CCTableViewCell>();
//        m_indices           = new CCMutableArray<int*>();
        m_indices           = new HashSet<Integer>();
        tDelegate           = null;
        m_vordering         = CCTableViewFillBottomUp;
        super.direction		= CCScrollViewDirectionVertical;
        
        super.delegate = this;
    }
    
    public void setVerticalFillOrder(int fillOrder)
    {
        if (m_vordering != fillOrder) {
            m_vordering = fillOrder;
            if(m_vordering == CCTableViewFillTopDown){
            	//container_.setPosition(CGPoint.ccp(container_.getPosition().x, -container_.getContentSize().height+viewSize.height));
                //This is necessary for scrolling physic
                //setPosition(container_.getPosition());
            }
            
            if (m_cellsUsed.size() > 0) {
                reloadData();
            }
        }
    }
    
    public void reloadData()
    {
        CCTableViewCell cell;
        for (int i=0;i < m_cellsUsed.size(); i++) {
            cell = m_cellsUsed.get(i);
            m_cellsFreed.add(cell);
            cell.setObjectID(m_cellsFreed.indexOf(cell));
            cell.reset();
            if (cell.getParent() == container_) {
                container_.removeChild(cell, true);
            }
        }
        
        m_indices.clear();
        m_cellsUsed = new ArrayList<CCTableViewCell>();
        
        _updateContentSize();
        if (dataSource.numberOfCellsInTableView(this) > 0) {
            scrollViewDidScroll(this);
        }
    }
    
    private CCTableViewCell cellAtIndex(int idx)
    {
        return _cellWithIndex(idx);
    }
    
    private void updateCellAtIndex(int idx)
    {
        if (idx == Integer.MAX_VALUE || idx > dataSource.numberOfCellsInTableView(this)-1) {
            return;
        }
        
        CCTableViewCell cell;
        
        cell = _cellWithIndex(idx);
        if (cell != null) {
            _moveCellOutOfSight(cell);
        }
        cell = dataSource.tableCellAtIndex(this, idx);
        _setIndex(idx, cell);
        _addCellIfNecessary(cell);
    }

    private void insertCellAtIndex(int idx)
    {
        //This function is not used for the momment
        /*
        if (idx == LONG_MAX || idx > dataSource.numberOfCellsInTableView(this)-1) {
            return;
        }
        
        CCTableViewCell     *cell;
        int                 newIdx;
        
        cell        = m_cellsUsed.get(idx);
        if (cell) {
            newIdx = m_cellsUsed.getIndexOfObject(cell);
            for (int i=newIdx; i<m_cellsUsed.size(); i++) {
                cell = m_cellsUsed.get(i);
                _setIndex(cell.getObjectID()+1, cell);
            }
        }
        
        //TODO: ??
        //indices_.shiftIndexesStartingAtIndex(idx, 1);
        m_indices.insert(m_indices.end(), m_indices.size());
        
        //insert a new cell
        cell = dataSource.tableCellAtIndex(this, idx);
        _setIndex(idx, cell);
        _addCellIfNecessary(cell);
        
        _updateContentSize();
         */
    }

    private void removeCellAtIndex(int idx)
    {
        //This function is not used for the momment
        /*
        if (idx == LONG_MAX || idx > dataSource.numberOfCellsInTableView(this)-1) {
            return;
        }
        
        CCTableViewCell   *cell;
        int         	  newIdx;
        
        cell = _cellWithIndex(idx);
        if (!cell) {
            return;
        }
        
        newIdx = m_cellsUsed.getIndexOfObject(cell);
        
        //remove first
        _moveCellOutOfSight(cell);
        
        m_indices.erase(idx+1);
        for (int i=m_cellsUsed.size()-1; i > newIdx; i--) {
            cell = m_cellsUsed.get(i);
            _setIndex(cell.getObjectID()-1, cell);
        }
         */
    }

    public CCTableViewCell dequeueCell()
    {
        CCTableViewCell cell;
        
        if (m_cellsFreed.size() == 0) {
            return null;
        } else {
            cell = (CCTableViewCell)m_cellsFreed.get(0);
            m_cellsFreed.remove(0);
        }
        return cell;
    }
    
    private void _addCellIfNecessary(CCTableViewCell cell)
    {
        if (cell.getParent() != container_) {
            container_.addChild(cell);
        }
        
        //Inserting the new cell on the proper place (sorted by indexes)
        boolean inserted = false;
        for (int i = 0; i < m_cellsUsed.size(); i++) {
            if(m_cellsUsed.get(i).getObjectID() > cell.getObjectID()){
                m_cellsUsed.add(i, cell);
                inserted = true;
                break;
            }
        }
        if(!inserted) m_cellsUsed.add(cell);
        
        m_indices.add(cell.getObjectID());
    }
    
    void _updateContentSize()
    {
        CGSize     size, cellSize;
        int		   cellCount;
        
        cellSize  = dataSource.cellSizeForTable(this);
        cellCount = dataSource.numberOfCellsInTableView(this);
        
        switch (super.direction) {
            case CCScrollViewDirectionHorizontal:
                size = CGSize.make(cellCount * cellSize.width, cellSize.height);
                break;
            default:
                size = CGSize.make(cellSize.width, cellCount * cellSize.height);
                break;
        }
        setContentSize(size);
    }

    private CGPoint _offsetFromIndex(int index)
    {
        CGPoint offset = __offsetFromIndex(index);
        
        CGSize cellSize = dataSource.cellSizeForTable(this);
        if (m_vordering == CCTableViewFillTopDown) {
            offset.y = container_.getContentSize().height - offset.y - cellSize.height;
        }
        return offset;
    }
    
    private CGPoint __offsetFromIndex(int index)
    {
        CGPoint offset;
        CGSize  cellSize;
        
        cellSize = dataSource.cellSizeForTable(this);
        switch (super.direction) {
            case CCScrollViewDirectionHorizontal:
                offset = CGPoint.ccp(cellSize.width * index, 0.0f);
                break;
            default:
                offset = CGPoint.ccp(0.0f, cellSize.height * index);
                break;
        }
        
        return offset;
    }
    
    private int _indexFromOffset(CGPoint offset)
    {
        int index;
        int maxIdx = dataSource.numberOfCellsInTableView(this)-1;
        
        CGPoint newOffset = CGPoint.make(offset.x, offset.y);
        CGSize cellSize = dataSource.cellSizeForTable(this);
        if (m_vordering == CCTableViewFillTopDown) {
            newOffset.y = container_.getContentSize().height - offset.y - cellSize.height;
        }
        index = Math.max(0, __indexFromOffset(newOffset));
        index = Math.min(index, maxIdx);
        return index;
    }
    
    private int __indexFromOffset(CGPoint offset)
    {
        int  	   index;
        CGSize     cellSize;
        
        cellSize = dataSource.cellSizeForTable(this);
        
        switch (super.direction) {
            case CCScrollViewDirectionHorizontal:
                index = (int) (offset.x/cellSize.width);
                break;
            default:
                index = (int) (offset.y/cellSize.height);
                break;
        }
        
        return index;
    }
    
    private CCTableViewCell _cellWithIndex(int cellIndex)
    {
        CCTableViewCell found;
        
        found = null;
        
        Iterator<Integer> i = m_indices.iterator();
        while(i.hasNext()){
        	if(cellIndex == i.next()){

                for (int index = 0; index < m_cellsUsed.size(); index++) {
                    if(m_cellsUsed.get(index).getObjectID() == cellIndex){
                        found = (CCTableViewCell)m_cellsUsed.get(index);
                        break;
                    }
                }
                break;
        	}
        }
        
        return found;
    }
    
    private void _moveCellOutOfSight(CCTableViewCell cell)
    {
        m_cellsFreed.add(cell);
        m_cellsUsed.remove(cell);

        Integer number;
        Iterator<Integer> it = m_indices.iterator();
        while(it.hasNext()){
        	number = it.next();
        	if(cell.getObjectID() == number){
        		m_indices.remove(number);
        		break;
        	}
        }
        
        cell.reset();
        if (cell.getParent() == container_) {
            container_.removeChild(cell, false);
        }
    }
    
    private void _setIndex(int index, CCTableViewCell cell)
    {
        cell.setAnchorPoint(CGPoint.ccp(0.0f, 0.0f));
        cell.setPosition(_offsetFromIndex(index));
        cell.setObjectID(index);
    }
    
    public void scrollViewDidScroll(CCScrollView view)
    {
        int		          startIdx, endIdx, idx, maxIdx;
        CGPoint           offset;
        
        maxIdx = dataSource.numberOfCellsInTableView(this);
        
        if (maxIdx == 0) {
            return; // early termination
        }
        
        offset   = CGPoint.ccpMult(contentOffset(), -1);
        maxIdx   = Math.max(maxIdx - 1, 0);
        
        CGSize cellSize = dataSource.cellSizeForTable(this);
        
        if (m_vordering == CCTableViewFillTopDown) {
            offset.y = offset.y + (viewSize.height/container_.getScaleY()) - cellSize.height;
        }
        startIdx = _indexFromOffset(offset);
        if (m_vordering == CCTableViewFillTopDown) {
            offset.y -= viewSize.height / container_.getScaleY();
        } else {
            offset.y += viewSize.height/container_.getScaleY();
        }
        offset.x += viewSize.width/container_.getScaleX();
            
        endIdx   = _indexFromOffset(offset);

        //Removing cells out of sight starting from the top or left until find the first visible cell
        if (m_cellsUsed.size() > 0) {
            idx = m_cellsUsed.get(0).getObjectID();
            while(idx <startIdx) {
                CCTableViewCell cell = m_cellsUsed.get(0);
                _moveCellOutOfSight(cell);
                if (m_cellsUsed.size() > 0) {
                    idx = m_cellsUsed.get(0).getObjectID();
                } else {
                    break;
                }
            }
        }
        
        //Removing cells out of sight starting from the bottom or right until find the last visible cell
        if (m_cellsUsed.size() > 0) {
            idx = m_cellsUsed.get(m_cellsUsed.size()-1).getObjectID();
            while(idx <= maxIdx && idx > endIdx) {
                CCTableViewCell cell = m_cellsUsed.get(m_cellsUsed.size()-1);
                _moveCellOutOfSight(cell);
                if (m_cellsUsed.size() > 0) {
                    idx = m_cellsUsed.get(m_cellsUsed.size()-1).getObjectID();
                } else {
                    break;
                }
            }
        }
        
        for (int i=startIdx; i <= endIdx; i++) {
            //Checking if there are pending cell to show on screen
            boolean canUpdate = true;
            Iterator<Integer> it = m_indices.iterator();
            while(it.hasNext()){
            	if(i == it.next()){
            		canUpdate = false;
            		break;
            	}
            }
            
            //updating cell for showing on screen
            if(canUpdate)updateCellAtIndex(i);
        }
    }

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
        if (!getVisible()) {
            return false;
        }
        
      //  if (touches_.size() == 1 && !touchMoved_) {
        if (!touchMoved_) {
            int		          index;
            CCTableViewCell   cell;
            CGPoint           point;
            
            point = container_.convertTouchToNodeSpace(event);
            if (m_vordering == CCTableViewFillTopDown) {
                CGSize cellSize = dataSource.cellSizeForTable(this);
                point.y -= cellSize.height;
            }
            index = _indexFromOffset(point);
            cell  = _cellWithIndex(index);
            
            if (cell != null) {
                tDelegate.tableCellTouched(this, cell);
            }
        }
        
        return super.ccTouchesEnded(event);
    }

	@Override
	public void scrollViewDidZoom(CCScrollView view) {
		// TODO Auto-generated method stub
		
	}
}
