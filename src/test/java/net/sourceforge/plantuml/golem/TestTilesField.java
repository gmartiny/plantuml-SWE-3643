package net.sourceforge.plantuml.golem;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Test suite for the TilesField class.
 */
public class TestTilesField {
    private TilesField tilesField = new TilesField();

    @BeforeEach
    public void resetSharedState() {
        tilesField = null;
    }

    // ============================================================================================
    // createTile tests
    // ============================================================================================

    @Test
    public void testCreateTileNorthPosition() {
    		Tile start = tilesField.getRoot();
        TileGeometry startDirection = TileGeometry.NORTH;
        tilesField.createTile(start, startDirection);
    }

    @Test
    public void testCreateTileInvalidPosition() {
    		assertThrows(NullPointerException.class, tilesField.createTile(tilesField.getRoot(), null);
    }

    @Test
    public void testCreateTileOccupiedPosition() {
				Tile start = TilesField.getRoot();
				TilesField.createTile(start, TileGeometry.NORTH);
				assertDoesNotThrow(TilesField.createTile(start, TileGeometry.NORTH);
    }

    @Test
    public void testCreateTileInvalidTile() {
    		TileGeometry startGeom = TileGeometry.NORTH;
    		assertThrows(IllegalArgumentException.class, tilesField.createTile(Tile(15), startGeom));
    }

    @Test
    public void testCreateTileNullTile() {
				assertThrows(NullPointerException.class, tilesFields.createTile(TilesField.getRoot(), TileGeometry.NORTH));
    }

    // ============================================================================================
    // addPath tests
    // ============================================================================================

    @Test
    public void testPathAdded() {
        tilesField = new TilesField();
			  TileGeometry startDirection = TileGeometry.EAST;
        start = tilesField.getRoot(); 
        dest = new Tile(1);
        tilesField.addPosition(dest, new Position(2, 0, 2, 1));
			
			  int initial = tilesField.getPaths().size();
			
        tilesField.addPath(start, dest, startDirection);
			  int after = tilesField.getPaths().size();
			
				assertEquals(initial + 1, after);
    }

    @Test
    public void testThrowsForNullStart() {
        tilesField = new TilesField();
        start = tilesField.getRoot(); 
        dest = new Tile(1);
        tilesField.addPosition(dest, new Position(2, 0, 2, 1));
				
        assertThrows(NullPointerException.class, () -> {
					tilesField.addPath(null, dest, TileGeometry.EAST);		
				});
				
    }

    @Test
    public void testThrowsForNullDestination() {
        tilesField = new TilesField();
				Tile start = tilesField.getRoot();
				Tile dest = null;
			  TileGeometry startDirection = TileGeometry.EAST;
			
				assertThrows(NullPointerException.class, () -> {
										 tilesField.addPath(start, dest, startDirection);
		    });
    }

    @Test
    public void testThrowsForNullStartDirection() {
        tilesField = new TilesField();
			  Tile start = tilesField.getRoot();
				Tile dest = new Tile(1);
			  tilesField.addPosition(dest, new Position(2, 0, 2, 1));
			  TileGeometry startDirection = null;
				assertThrows(NullPointerException.class, () -> {
					tilesField.addPath(start, dest, startDirection);
				});
    }

    @Test
    public void testThrowsForInvalidPath() {
        tilesField = new TilesField();
        start = tilesField.getRoot(); 
        dest = new Tile(1);
        tilesField.addPosition(dest, new Position(2, 0, 2, 1));
			  TileGeometry startDirection = TileGeometry.CENTER; 
			  assertThrows(IllegalArgumentException.class, () -> {
					tilesField.addPath(start, dest, startDirection);
				});
    }

    // ============================================================================================
    // buildPath tests
    // ============================================================================================

    private static Method getBuildPathFn() {
        Class<?> tilesField = TilesField.class;
        try {
            // get private method
            Method buildPath = tilesField.getDeclaredMethod("buildPath", TileArea.class, TileArea.class);
            // make it public
            buildPath.setAccessible(true);
            return buildPath;
        } catch (NoSuchMethodException ex) {
            // error
        }
        throw new IllegalStateException();
    }

    private static Path buildPath(TilesField tsf, TileArea tileArea1, TileArea tileArea2) throws Throwable {
        Method buildPathFn = getBuildPathFn();
        try {
            return (Path) buildPathFn.invoke(tsf, tileArea1, tileArea2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // rethrow inner exception
            throw e.getTargetException();
        }
        throw new IllegalStateException(); // satisfy compiler
    }

    @Test
    public void testAdjacentTilesCreatePath() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.EAST);
        var tileArea2 = new TileArea(tile2, TileGeometry.WEST);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(2, 0, 3, 1));

        assertEquals(Path.build(tileArea1, tileArea2), buildPath(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testNonAdjacentTilesError() {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.CENTER);
        var tileArea2 = new TileArea(tile2, TileGeometry.NORTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(2, 0, 3, 1));

        assertThrows(
            IllegalArgumentException.class, () -> buildPath(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testSameTileOppositeSidesCreatesPath() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tileArea1 = new TileArea(tile1, TileGeometry.NORTH);
        var tileArea2 = new TileArea(tile1, TileGeometry.SOUTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));

        assertEquals(Path.build(tileArea1, tileArea2), buildPath(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void nonAdjacentSpecialCaseCreatesPath() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.WEST);
        var tileArea2 = new TileArea(tile2, TileGeometry.EAST);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(200, 0, 3, 1));

        assertEquals(Path.build(tileArea1, tileArea2), buildPath(tilesField, tileArea1, tileArea2));
    }

    // ============================================================================================
    // isAdjoining tests
    // ============================================================================================

    private static Method getIsAdjoiningFn() {
        Class<?> tilesField = TilesField.class;
        try {
            // get private method
            Method isAdjoining = tilesField.getDeclaredMethod("isAdjoining", TileArea.class, TileArea.class);
            // make it public
            isAdjoining.setAccessible(true);
            return isAdjoining;
        } catch (NoSuchMethodException ex) {
            // error
        }
        throw new IllegalStateException();
    }

    private static boolean isAdjoining(TilesField tsf, TileArea tileArea1, TileArea tileArea2) throws Throwable {
        Method isAdjoiningFn = getIsAdjoiningFn();
        try {
            return (boolean) isAdjoiningFn.invoke(tsf, tileArea1, tileArea2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // e.printStackTrace();
            throw e.getTargetException();
        }
        throw new IllegalStateException(); // satisfy compiler
    }

    @Test
    public void testIdenticalPositionsAndGeometriesError() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tileArea1 = new TileArea(tile1, TileGeometry.NORTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));

        assertThrows(IllegalArgumentException.class,
            () -> isAdjoining(tilesField, tileArea1, tileArea1));
    }

    @Test
    public void testIdenticalPositionsButNotGeometriesIsTrue() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tileArea1 = new TileArea(tile1, TileGeometry.NORTH);
        var tileArea2 = new TileArea(tile1, TileGeometry.SOUTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));

        assertTrue(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testDifferentPositionsWithOppositesGeometiresIsFalse() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.NORTH);
        var tileArea2 = new TileArea(tile2, TileGeometry.SOUTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(5, 5, 6, 6));

        assertFalse(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testDifferentPostionsNotOppositeGeomsWithGeo1East() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.EAST);
        var tileArea2 = new TileArea(tile2, TileGeometry.SOUTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(5, 5, 6, 6));

        assertFalse(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testDifferentPostionsNotOppositeGeomsWithGeom1West() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.WEST);
        var tileArea2 = new TileArea(tile2, TileGeometry.SOUTH);
        tilesField.addPosition(tile1, new Position(1, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(0, 0, 0, 1));

        assertFalse(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testDifferentPostionsNotOppositeGeomsWithGeom1North() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.NORTH);
        var tileArea2 = new TileArea(tile2, TileGeometry.WEST);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(5, 5, 6, 6));

        assertFalse(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testDifferentPostionsNotOppositeGeomsWithGeom1South() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.SOUTH);
        var tileArea2 = new TileArea(tile2, TileGeometry.WEST);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(0, 2, 1, 0));

        assertFalse(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    @Test
    public void testDifferentPostionsNotOppositeGeomsWithGeom1Center() throws Throwable {
        tilesField = new TilesField();
        var tile1 = new Tile(1);
        var tile2 = new Tile(2);
        var tileArea1 = new TileArea(tile1, TileGeometry.CENTER);
        var tileArea2 = new TileArea(tile2, TileGeometry.SOUTH);
        tilesField.addPosition(tile1, new Position(0, 0, 1, 1));
        tilesField.addPosition(tile2, new Position(5, 5, 6, 6));

        assertFalse(isAdjoining(tilesField, tileArea1, tileArea2));
    }

    // ============================================================================================
    // getFreePosition tests
    // ============================================================================================

    private static Method getGetFreePositionFn() {
        Class<?> tilesField = TilesField.class;
        try {
            // get private method
            Method getFreePosition = tilesField.getDeclaredMethod("getFreePosition", Tile.class, TileGeometry.class);
            // make it public
            getFreePosition.setAccessible(true);
            return getFreePosition;
        } catch (NoSuchMethodException ex) {
            // error
        }
        throw new IllegalStateException(); // satisfy compiler
    }

    private static Position getFreePosition(TilesField tsf, Tile start, TileGeometry position) throws Throwable {
        Method getFreePositionFn = getGetFreePositionFn();
        try {
            return (Position) getFreePositionFn.invoke(tsf, start, position);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // e.printStackTrace();
            throw e.getTargetException();
        }
        throw new IllegalStateException(); // satisfy compiler
    }

    private static Method getGetTileAtFn() {
        Class<?> tilesField = TilesField.class;
        try {
            // get private method
            Method getTileAt = tilesField.getDeclaredMethod("getTileAt", Position.class);
            // make it public
            getTileAt.setAccessible(true);
            return getTileAt;
        } catch (NoSuchMethodException ex) {
            // error
        }
        throw new IllegalStateException(); // satisfy compiler
    }

    private static Tile getTileAt(TilesField tsf, Position position) throws Throwable {
        Method getTileAtFn = getGetTileAtFn();
        try {
            return (Tile) getTileAtFn.invoke(tsf, position);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw e.getTargetException();
        }
        throw new IllegalStateException(); // satisfy compiler
    }

    @Test
    public void testNoConflict() throws Throwable {
        // relies on the root tile
        tilesField = new TilesField();
        var start = tilesField.getRoot();

        var position = getFreePosition(tilesField, start, TileGeometry.SOUTH);
        assertEquals(new Position(0, 2, 1, 3), position);
        assertNull(getTileAt(tilesField, position));
    }

    @Test
    public void testMoveTileWithOneConflict() throws Throwable {
        tilesField = new TilesField();
        var start = tilesField.getRoot();
        tilesField.addPosition(new Tile(1), new Position(0, 2, 1, 3));

        var position = getFreePosition(tilesField, start, TileGeometry.SOUTH);
        assertEquals(new Position(0, 2, 1, 3), position);
        assertNull(getTileAt(tilesField, position));
    }

    @Test
    public void testMoveTileWithTwoConfilcts() throws Throwable {
        tilesField = new TilesField();
        var start = tilesField.getRoot();
        tilesField.addPosition(new Tile(1), new Position(0, 2, 1, 3));
        var tile2 = new Tile(2);
        tilesField.addPosition(tile2, new Position(2, 2, 1, 3));

        var position = getFreePosition(tilesField, start, TileGeometry.SOUTH);
        assertEquals(new Position(0, 2, 1, 3), position);
        assertNull(getTileAt(tilesField, position));
        assertEquals(tile2, getTileAt(tilesField, new Position(4, 2, 3, 3)));
    }

    @Test
    public void testTileShouldNotMoveIfNotBlockingY() throws Throwable {
        tilesField = new TilesField();
        var start = tilesField.getRoot();
        var tile1 = new Tile(1);
        var tile1Pos = new Position(2, 2, 1, 3);
        tilesField.addPosition(tile1, tile1Pos);

        var position = getFreePosition(tilesField, start, TileGeometry.SOUTH);
        assertEquals(new Position(0, 2, 1, 3), position);
        assertNull(getTileAt(tilesField, position));
        assertEquals(tile1, getTileAt(tilesField, tile1Pos));
    }

    @Test
    public void testTileShouldNotMoveIfNotBlockingX() throws Throwable {
        tilesField = new TilesField();
        var start = tilesField.getRoot();
        var tile1 = new Tile(1);
        var tile1Pos = new Position(2, -2, 3, -1);
        tilesField.addPosition(tile1, tile1Pos);

        var position = getFreePosition(tilesField, start, TileGeometry.EAST);
        assertEquals(new Position(2, 0, 3, 1), position);
        assertNull(getTileAt(tilesField, position));
        assertEquals(tile1, getTileAt(tilesField, tile1Pos));
    }

    @Test
    public void testThrowsOnNullStart() {
        tilesField = new TilesField();
        Tile start = null;
        var position = TileGeometry.EAST;

        assertThrows(NullPointerException.class,
            () -> getFreePosition(tilesField, start, position));
    }

    @Test
    public void testThrowsOnNullPosition() {
        tilesField = new TilesField();
        var start = tilesField.getRoot();
        TileGeometry position = null;

        assertThrows(IllegalArgumentException.class,
            () -> getFreePosition(tilesField, start, position));
    }

    @Test
    public void testThrowsOnCenterPosition() {
        tilesField = new TilesField();
        var start = tilesField.getRoot();
        var position = TileGeometry.CENTER;

        assertThrows(IllegalArgumentException.class,
            () -> getFreePosition(tilesField, start, position));
    }
}
