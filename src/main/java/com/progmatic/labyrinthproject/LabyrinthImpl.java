package com.progmatic.labyrinthproject;

import com.progmatic.labyrinthproject.enums.CellType;
import com.progmatic.labyrinthproject.enums.Direction;
import com.progmatic.labyrinthproject.exceptions.CellException;
import com.progmatic.labyrinthproject.exceptions.InvalidMoveException;
import com.progmatic.labyrinthproject.interfaces.Labyrinth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author pappgergely
 */
public class LabyrinthImpl implements Labyrinth {

    private CellType[][] labBoard;
    private Coordinate c;
    private Coordinate playerStart;
    private Coordinate myPosition;
    private CellType myType;

    public LabyrinthImpl() {

    }

    @Override
    public int getWidth() {
        if (labBoard == null){
            return -1;
        }
        return labBoard[0].length;
    }

    @Override
    public int getHeight() {
        if (labBoard == null){
            return -1;
        }
        return labBoard.length;
    }

    @Override
    public void loadLabyrinthFile(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            int width = Integer.parseInt(sc.nextLine());
            int height = Integer.parseInt(sc.nextLine());
            labBoard = new CellType[height][width];
            for (int hh = 0; hh < height; hh++) {
                String line = sc.nextLine();
                for (int ww = 0; ww < width; ww++) {
                    switch (line.charAt(ww)) {
                        case 'W':
                            labBoard[hh][ww]=CellType.WALL;
                            break;
                        case 'E':
                            labBoard[hh][ww]=CellType.END;
                            break;
                        case 'S':
                            labBoard[hh][ww]=CellType.START;
                            break;
                        default:
                            labBoard[hh][ww]=CellType.EMPTY;
                            break;
                    }
                }
            }
        } catch (FileNotFoundException | NumberFormatException ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    public CellType getCellType(Coordinate c) throws CellException {
        if (c.getCol()>= labBoard[0].length || c.getCol()<0 || c.getRow()>= labBoard.length || c.getRow()<0){
            throw new CellException(c.getCol(), c.getRow(), "Not existing field!");
        }
        return labBoard[c.getCol()][c.getRow()];
    }

    @Override
    public void setSize(int width, int height) throws CellException {
        if (width<0 || height<0){
            throw new CellException(width, height, "Not possible size.");
        }
        CellType[][] newLabBoard = new CellType[height][width];
        for (int i = 0; i < newLabBoard.length ; i++) {
            for (int j = 0; j < newLabBoard[i].length ; j++) {
                newLabBoard[i][j] = CellType.EMPTY;
            }
        }
        labBoard = newLabBoard;
    }

    //Sets a given cell coordinates to a cell type.
    //     * After calling a setCellType(c, ct), getCellType(c) should return ct.
    //     * If type equals START, should also set the player's position to c.
    //     * @param c the coordinate which must be overwritten.
    //     * @param type the type to write to the cell.
    //     * @throws CellException if c.row or c.col points to a non-existent index.
    @Override
    public void setCellType(Coordinate c, CellType type) throws CellException {
        try{
            if(labBoard[c.getRow()][c.getCol()]!= CellType.EMPTY){
                throw new InvalidMoveException();
            }
        }catch (InvalidMoveException e){
                e.printStackTrace();
        }
        if (c.getRow()> labBoard.length || c.getRow()<0 || c.getCol()> labBoard[0].length || c.getCol()<0){
            throw new CellException(c.getRow(), c.getCol(), "Not existing field!");
        }
        if(type == CellType.START){
            playerStart = c;
        } else {
            labBoard[c.getRow()][c.getCol()] = type;
        }
    }

    @Override
    public Coordinate getPlayerPosition() {
        return myPosition;
    }

    @Override
    public boolean hasPlayerFinished() {
        if (myPosition.equals(CellType.END)){
            return true;
        }
        return false;
    }

    @Override
    public List<Direction> possibleMoves() {
        List<Direction> dirList = new ArrayList<>();
        Coordinate cDown = new Coordinate(getPlayerPosition().getCol()+1, getPlayerPosition().getRow());
        if (cDown.equals(CellType.EMPTY)){
            dirList.add(Direction.SOUTH);
        }
        Coordinate cUp = new Coordinate(getPlayerPosition().getCol()-1, getPlayerPosition().getRow());
        if (cUp.equals(CellType.EMPTY)){
            dirList.add(Direction.NORTH);
        }
        Coordinate cRight = new Coordinate(getPlayerPosition().getCol(), getPlayerPosition().getRow()+1);
        if (cRight.equals(CellType.EMPTY)){
            dirList.add(Direction.EAST);
        }
        Coordinate cLeft = new Coordinate(getPlayerPosition().getCol(), getPlayerPosition().getRow()-1);
        if (cLeft.equals(CellType.EMPTY)){
            dirList.add(Direction.WEST);
        }
        return dirList;
    }

    //Moves and updates the player's position in the given direction.
    //     * @param direction in which the player wants to move.
    //     * @throws InvalidMoveException if the player tries to move to an invalid cell (eg WALL).

    @Override
    public void movePlayer(Direction direction) throws InvalidMoveException {
        if (! (direction instanceof Direction)){
            throw new InvalidMoveException();
        }
        switch(direction){
            case NORTH:
                if (myPosition.getRow() - 1 < 0 || labBoard[myPosition.getRow() - 1][myPosition.getCol()] == CellType.WALL) {
                    throw new InvalidMoveException();
                } else {
                    myPosition = new Coordinate(myPosition.getCol(), myPosition.getRow() - 1);
                }
                break;
            case SOUTH:
                if (myPosition.getRow() + 1 >= getHeight() || labBoard[myPosition.getRow() + 1][myPosition.getCol()] == CellType.WALL) {
                    throw new InvalidMoveException();
                } else {
                    myPosition = new Coordinate(myPosition.getCol(), myPosition.getRow()+1);
                }
                break;
            case EAST:
                if (myPosition.getCol() + 1 >= getWidth() || labBoard[myPosition.getRow()][myPosition.getCol() + 1] == CellType.WALL ) {
                    throw new InvalidMoveException();
                } else {
                    myPosition = new Coordinate(myPosition.getCol() + 1, myPosition.getRow());
                }
                break;
            case WEST:
                if (myPosition.getCol() - 1 < 0 || labBoard[myPosition.getRow()][myPosition.getCol() - 1] == CellType.WALL) {
                    throw new InvalidMoveException();
                } else {
                    myPosition = new Coordinate(myPosition.getCol() - 1, myPosition.getRow());
                }
                break;
        }
    }

}
