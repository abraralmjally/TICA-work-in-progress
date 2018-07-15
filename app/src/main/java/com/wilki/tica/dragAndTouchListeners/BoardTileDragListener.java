package com.wilki.tica.dragAndTouchListeners;

import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wilki.tica.logicLayer.Pos;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.SquareTypes;
import com.wilki.tica.logicLayer.TaskLayout;

/**
 * Created by John Wilkie on 3/03/2017.
 * Used in task creation. Listens for new tiles being dropped onto the layout or dragged off of the
 * layout.
 */

public class BoardTileDragListener implements View.OnDragListener {

    private RelativeLayout screen;
    private Context cont;
    private final int TILE_WIDTH = 93;
    private final int BOARD_SIZE = 5;
    private ImageView[][] selectedTiles;
    private TaskLayout newTaskLayout;
    private ImageView boardImage;


    /**
     * Initialises class fields.
     * @param screen the create task screen layout.
     * @param cont application context.
     * @param newTaskLayout task layout to be created.
     * @param boardImage the image view of the bank board for tiles to be dropped on.
     */
    public BoardTileDragListener(RelativeLayout screen, Context cont, TaskLayout newTaskLayout,
                                 ImageView boardImage){
        this.screen = screen;
        this.cont = cont;
        this.newTaskLayout = newTaskLayout;
        selectedTiles = new ImageView[BOARD_SIZE][BOARD_SIZE];
        this.boardImage = boardImage;
    }

    /*
     * Used to recognise the stages of a view being dragged and react to them.
     */
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DROP:
                ClipData clipData = dragEvent.getClipData();
                String droppedViewTag = clipData.getItemAt(0).getText().toString();
                if(dropOverBoard(dragEvent)){
                    // tile dropped onto task layout and needs to be placed
                    Pos gridPosition = findGridPositionOnBoard(dragEvent.getX(), dragEvent.getY());
                    Pos positionOnBoard = findActualPosition(gridPosition, view.getX(), view.getY());
                    droppedOnBlankBoard(droppedViewTag, positionOnBoard, gridPosition);
                }else{
                    // tile dropped off of task layout
                    droppedOffBoard(droppedViewTag);
                }
                return true;
        }
        return false;
    }

    /*
     * returns true if the tile was dropped onto the blank board and false otherwise.
     */
    private boolean dropOverBoard(DragEvent dragEvent) {
        boolean overBoard = false;
        float dragY = dragEvent.getY();
        float dragX = dragEvent.getX();
        if(dragY > boardImage.getY() && dragY <= boardImage.getY() + boardImage.getHeight() &&
                dragX > boardImage.getX() && dragX <= boardImage.getX() + boardImage.getWidth()){
            overBoard = true;
        }
        return overBoard;

    }

    /*
     * Using the provided x and y positions, calculates which grid square the tile was dropped on.
     */
    private Pos findGridPositionOnBoard(float dropX, float dropY) {
        int droppedX = (int) dropX;
        int droppedY = (int) dropY;
        droppedX -= boardImage.getX();
        droppedY -= boardImage.getY();
        int toPlaceX = 0;
        int toPlaceY = 0;
        for(int i = 0; i < BOARD_SIZE; i++){
            if(droppedX > i * TILE_WIDTH && droppedX < (i+1) * TILE_WIDTH){
                toPlaceX = i;
            }
        }

        for(int i = 0; i < BOARD_SIZE; i++){
            if(droppedY > i * TILE_WIDTH && droppedY < (i+1) * TILE_WIDTH){
                toPlaceY = i;
            }
        }
        return new Pos(toPlaceY, toPlaceX);
    }

    /*
     * Calculates the actual position to place the dropped tile.
     */
    private Pos findActualPosition(Pos gridPosition, float x, float y) {
        int actualX = (int) x - 5 + (TILE_WIDTH + 2) * gridPosition.getX();
        int actualY = (int) y - 5 + (TILE_WIDTH + 2) * gridPosition.getY();
        actualX += boardImage.getX();
        actualY += boardImage.getY();
        return new Pos(actualY, actualX);
    }

    /*
     * Processes a tile dropped onto the blank board.
     */
    private void droppedOnBlankBoard(String droppedTag, Pos positionOnBoard, Pos gridPosition){
        if(droppedTag.charAt(droppedTag.length()-1) == ']'){
            // dropped tile was already on the board.
            Pos oldPos = getOriginalPosFromTag(droppedTag);
            removeOldImage(oldPos);
            droppedTag = droppedTag.substring(0, droppedTag.length()-5);
        }
        placeNewTileImage(droppedTag, positionOnBoard, gridPosition);
    }

    /*
     * Finds the original grid position of a tile dropped on the board that was already on the
     * board.
     */
    private Pos getOriginalPosFromTag(String tag){
        String position = tag.substring(tag.length()-5,tag.length());
        int posX = Character.getNumericValue(position.charAt(1));
        int posY = Character.getNumericValue(position.charAt(3));
        return new Pos(posY, posX);
    }

    /*
     * Place a new tile image onto the board.
     * @param droppedTag the tag of the dropped tile.
     * @param positionOnBoard actual position to place the new tile image.
     * @param gridPosition the grid position of the new tile image.
     */
    private void placeNewTileImage(String droppedTag, Pos positionOnBoard, Pos gridPosition){
        if(selectedTiles[gridPosition.getY()][gridPosition.getX()] == null){
            // new grid position is empty
            ImageView  newImage = new ImageView(cont);
            newImage.setImageResource(getImageResource(droppedTag));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(93, 93);
            layoutParams.setMargins(positionOnBoard.getX(), positionOnBoard.getY(), 0, 0);
            newImage.setLayoutParams(layoutParams);
            newImage.setTag(droppedTag+("["+ gridPosition.getX() +","+gridPosition.getY() +"]"));
            newImage.setOnTouchListener(new DragTouchListener());
            screen.addView(newImage);
            selectedTiles[gridPosition.getY()][gridPosition.getX()] = newImage;
        }else{
            // grid position already has a tile in it which can be replaced
            selectedTiles[gridPosition.getY()][gridPosition.getX()].setImageResource(
                    getImageResource(droppedTag));
            selectedTiles[gridPosition.getY()][gridPosition.getX()].setTag(
                    droppedTag+("["+ gridPosition.getX() +","+gridPosition.getY() +"]"));
        }
        newTaskLayout.setSquare(getSquareType(droppedTag), gridPosition);
    }

    /*
     * Remove an old image from the board at the grid position specified.
     */
    private void removeOldImage(Pos oldPos){
        ImageView imageToRemove = selectedTiles[oldPos.getY()][oldPos.getX()];
        selectedTiles[oldPos.getY()][oldPos.getX()] = null;
        screen.removeView(imageToRemove);
        newTaskLayout.setSquare(SquareTypes.EMPTY, oldPos);
    }

    /*
     * Get the image resource that matches the tag of the tile provided.
     */
    private int getImageResource(String droppedTag) {
        if(droppedTag.equals(SquareTypes.START.toString())){
            return R.drawable.start_square;
        } else if(droppedTag.equals(SquareTypes.FINISH.toString())){
            return R.drawable.finish_square;
        } else if(droppedTag.equals(SquareTypes.NOISE.toString())){
            return R.drawable.speaker_square;
        } else {
            return R.drawable.blocked_square;
        }
    }

    /*
     * Using the tag of the tile provided returns the type of the tile.
     */
    private SquareTypes getSquareType(String droppedTag){
        if(droppedTag.equals(SquareTypes.START.toString())){
            return SquareTypes.START;
        } else if(droppedTag.equals(SquareTypes.FINISH.toString())){
            return SquareTypes.FINISH;
        } else if(droppedTag.equals(SquareTypes.NOISE.toString())){
            return SquareTypes.NOISE;
        } else {
            return SquareTypes.BLOCKED;
        }
    }

    /*
     * removes tiles from the board if they are dragged off.
     */
    private void droppedOffBoard(String droppedTag){
        int tagLength = droppedTag.length();
        if(droppedTag.charAt(tagLength-1) == ']'){
            Pos oldPos = getOriginalPosFromTag(droppedTag);
            removeOldImage(oldPos);
        }
    }
}
