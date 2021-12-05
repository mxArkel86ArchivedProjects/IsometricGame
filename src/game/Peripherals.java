package game;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;


public class Peripherals implements KeyListener {
    public Map<Integer, Boolean> keyboard = new HashMap<Integer, Boolean>();

    public boolean keyPressed(char c){
        int code = java.awt.event.KeyEvent.getExtendedKeyCodeForChar(c);
        if(keyboard.get(code)==null)
            return false;
        return keyboard.get(code);
    }

    public boolean keyPressed(int c){
        if(keyboard.get(c)==null)
            return false;
        return keyboard.get(c);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int c =e.getKeyCode();
        keyboard.put(c, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int c =e.getKeyCode();
        keyboard.put(c, false);
    }
    
}
