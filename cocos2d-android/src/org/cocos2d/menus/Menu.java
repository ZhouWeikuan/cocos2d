package org.cocos2d.menus;

import android.view.MotionEvent;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCRect;
import org.cocos2d.types.CCSize;
import org.cocos2d.types.CCMacros;
import org.cocos2d.events.TouchDispatcher;

import java.util.ArrayList;

public class Menu extends Layer {

    public static final int kDefaultPadding = 5;

    private MenuItem selectedItem;

    private MenuState state;

    enum MenuState {
        kMenuStateWaiting,
        kMenuStateTrackingTouch
    }

    public MenuItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(MenuItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    protected void registerWithTouchDispatcher() {
        TouchDispatcher.sharedDispatcher().addDelegate(this, CCMacros.INT_MIN+1);
    }

    
    /**
     * creates a menu with its items
     */
    public static Menu menu(MenuItem... items) {
        return new Menu(items);
    }

    protected Menu(MenuItem... items) {
        // menu in the center of the screen
        CCSize s = Director.sharedDirector().winSize();

//        CCRect r = [[UIApplication sharedApplication] statusBarFrame];
//        if(Director.sharedDirector().landscape())
//            s.height -= r.size.width;
//        else
//            s.height -= r.size.height;
        setPosition(s.width / 2, s.height / 2);

        setRelativeAnchorPoint(false);
        setAnchorPoint(0.5f, 0.5f);
        setContentSize(s.width, s.height);


        isTouchEnabled_ = true;

        int z = 0;
        for (int i = 0; i < items.length; i++) {
            MenuItem item = items[i];
            addChild(item, z);
            z++;
        }
//	    alignItemsVertically();

        selectedItem = null;
        state = MenuState.kMenuStateWaiting;

    }

    // Menu - Events

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {

        if (state != MenuState.kMenuStateWaiting)
            return TouchDispatcher.kEventIgnored;

        selectedItem = itemForTouch(event);

        if (selectedItem != null) {
            selectedItem.selected();
            state = MenuState.kMenuStateTrackingTouch;
            return TouchDispatcher.kEventHandled;
        }

        return TouchDispatcher.kEventIgnored;
    }

    @Override
    public boolean ccTouchesEnded(MotionEvent event) {
        if (state == MenuState.kMenuStateTrackingTouch) {
            if (selectedItem != null) {
                selectedItem.unselected();
                selectedItem.activate();
            }

            state = MenuState.kMenuStateWaiting;
            return TouchDispatcher.kEventHandled;
        }

        return TouchDispatcher.kEventIgnored;
    }

    @Override
    public boolean ccTouchesCancelled(MotionEvent event) {
        if (state == MenuState.kMenuStateTrackingTouch) {
            if (selectedItem != null) {
                selectedItem.unselected();
            }

            state = MenuState.kMenuStateWaiting;
            return TouchDispatcher.kEventHandled;
        }

        return TouchDispatcher.kEventIgnored;
    }

    @Override
    public boolean ccTouchesMoved(MotionEvent event) {
        if (state == MenuState.kMenuStateTrackingTouch) {
            MenuItem currentItem = itemForTouch(event);

            if (currentItem != selectedItem) {
                if (selectedItem != null) {
                    selectedItem.unselected();
                }
                selectedItem = currentItem;
                if (selectedItem != null) {
                    selectedItem.selected();
                }
            }
            return TouchDispatcher.kEventHandled;
        }

        return TouchDispatcher.kEventIgnored;
    }


    // Menu - Alignment

    /**
     * align items vertically
     */
    public void alignItemsVertically() {
        alignItemsVertically(kDefaultPadding);
    }

    /**
     * align items vertically with padding
     */
    public void alignItemsVertically(float padding) {
        float height = -padding;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            height += item.getHeight() * item.getScaleY() + padding;
        }

        float y = height / 2.0f;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            item.setPosition(0, y - item.getHeight() * item.getScaleY() / 2.0f);
            y -= item.getHeight() * item.getScaleY() + padding;
        }
    }

    /**
     * align items horizontally
     */
    public void alignItemsHorizontally() {
        alignItemsHorizontally(kDefaultPadding);
    }

    /**
     * align items horizontally with padding
     */
    public void alignItemsHorizontally(float padding) {

        float width = -padding;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            width += item.getWidth() * item.getScaleX() + padding;
        }

        float x = width / 2.0f;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            item.setPosition(x - item.getWidth() * item.getScaleX() / 2.0f, 0);
            x -= item.getWidth() * item.getScaleX() + padding;
        }
    }

    /**
     * align items in rows of columns
     */
    public void alignItemsInColumns(int columns[]) {
        ArrayList<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < columns.length; i++) {
            rows.add(columns[i]);
        }

        int height = -5;
        int row = 0, rowHeight = 0, columnsOccupied = 0, rowColumns;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            assert row < rows.size() : "Too many menu items for the amount of rows/columns.";

            rowColumns = rows.get(row);
            assert rowColumns != 0 : "Can't have zero columns on a row";

            rowHeight = (int) Math.max(rowHeight, item.getHeight());
            ++columnsOccupied;

            if (columnsOccupied >= rowColumns) {
                height += rowHeight + 5;

                columnsOccupied = 0;
                rowHeight = 0;
                ++row;
            }
        }

        assert columnsOccupied != 0 : "Too many rows/columns for available menu items.";

        CCSize winSize = Director.sharedDirector().winSize();

        row = 0;
        rowHeight = 0;
        rowColumns = 0;
        float w = 0, x = 0, y = height / 2;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            if (rowColumns == 0) {
                rowColumns = rows.get(row);
                w = winSize.width / (1 + rowColumns);
                x = w;
            }

            rowHeight = Math.max(rowHeight, (int) item.getHeight());
            item.setPosition(x - winSize.width / 2, y - item.getHeight() / 2);

            x += w + 10;
            ++columnsOccupied;

            if (columnsOccupied >= rowColumns) {
                y -= rowHeight + 5;

                columnsOccupied = 0;
                rowColumns = 0;
                rowHeight = 0;
                ++row;
            }
        }
    }

    /**
     * align items in columns of rows
     */

    public void alignItemsInRows(int rows[]) {
        ArrayList<Integer> columns = new ArrayList<Integer>();
        for (int i = 0; i < rows.length; i++) {
            columns.add(rows[i]);
        }

        ArrayList<Integer> columnWidths = new ArrayList<Integer>();
        ArrayList<Integer> columnHeights = new ArrayList<Integer>();

        int width = -10, columnHeight = -5;
        int column = 0, columnWidth = 0, rowsOccupied = 0, columnRows;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            assert column < columns.size() : "Too many menu items for the amount of rows/columns.";

            columnRows = columns.get(column);
            assert columnRows != 0 : "Can't have zero rows on a column";

            columnWidth = (int) Math.max(columnWidth, item.getWidth());
            columnHeight += item.getHeight() + 5;
            ++rowsOccupied;

            if (rowsOccupied >= columnRows) {
                columnWidths.add(columnWidth);
                columnHeights.add(columnHeight);
                width += columnWidth + 10;

                rowsOccupied = 0;
                columnWidth = 0;
                columnHeight = -5;
                ++column;
            }
        }

        assert rowsOccupied != 0 : "Too many rows/columns for available menu items.";

        CCSize winSize = Director.sharedDirector().winSize();

        column = 0;
        columnWidth = 0;
        columnRows = 0;
        float x = -width / 2, y = 0;
        for (int i = 0; i < children.size(); i++) {
            MenuItem item = (MenuItem) children.get(i);
            if (columnRows == 0) {
                columnRows = columns.get(column);
                y = columnHeights.get(column) + winSize.height / 2;
            }

            columnWidth = (int) Math.max(columnWidth, item.getWidth());
            item.setPosition(x + columnWidths.get(column) / 2, y - winSize.height / 2);

            y -= item.getHeight() + 10;
            ++rowsOccupied;

            if (rowsOccupied >= columnRows) {
                x += columnWidth + 5;

                rowsOccupied = 0;
                columnRows = 0;
                columnWidth = 0;
                ++column;
            }
        }
    }

    private MenuItem itemForTouch(MotionEvent event) {
    	CCPoint touchLocation =
    		Director.sharedDirector().convertCoordinate(event.getX(), event.getY());
    	float menuX = getPositionX();
    	float menuY = getPositionY();

    	for (int i = 0; i < children.size(); i++) {
    		MenuItem item = (MenuItem) children.get(i);
    		CCRect r = item.rect();
    		r.origin.x += menuX;
    		r.origin.y += menuY;
    		if (CCRect.containsPoint(r, touchLocation)) {
    			return item;
    		}
    	}
    	return null;
    }
}
