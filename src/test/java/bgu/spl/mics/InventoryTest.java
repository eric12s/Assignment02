package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    Inventory inv;
    String[] gadgets;

    @BeforeEach
    public void setUp(){
        inv = Inventory.getInstance();
        gadgets = new String[]{"gun", "sunglasses", "rope"};
    }

    @Test
    public void loadTest(){
        inv.load(gadgets);
        assertTrue(inv.getItem("gun"));
        assertTrue(inv.getItem("sunglasses"));
        assertTrue(inv.getItem("rope"));
    }

    @Test
    public void getItemTest(){
        inv.load(new String[]{"test2"});
        assertTrue(inv.getItem("test2"));
        assertFalse(inv.getItem("test3"));
    }

    @Test
    public void printToFileTest(){

    }

}
