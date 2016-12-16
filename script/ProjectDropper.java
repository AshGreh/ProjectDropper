package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Genoss on 12/16/2016:1:53 PM
 */
@Script.Manifest(description = "Drops selected inventory items v0.127 Author:Terminator1", name ="Dropper")
public class ProjectDropper extends PollingScript<ClientContext> implements ActionListener,PaintListener {

    private JFrame mainFrame;
    private Boolean dropState = false,dropAbort = false;
    private JButton invent[] = new JButton[30],abort,drop,clear,invert;
    private int inventSelected[] = new int[28];

    private void prepareGUI(){
        mainFrame = new JFrame("Dropper interface");
        mainFrame.setSize(500,650);
        mainFrame.setLayout(new GridLayout(8,4));
        for(int i = 0;i<28;i++) {
            invent[i] = new JButton(ctx.inventory.itemAt(i).name());
            invent[i].addActionListener(this);
            mainFrame.add(invent[i]);
        }
        abort = new JButton("Abort");
        drop = new JButton("Drop");
        clear = new JButton("Clear");
        invert = new JButton("Invert");
        abort.addActionListener(this);
        drop.addActionListener(this);
        clear.addActionListener(this);
        invert.addActionListener(this);
        mainFrame.add(abort);
        mainFrame.add(drop);
        mainFrame.add(clear);
        mainFrame.add(invert);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ctx.controller.stop();
            }
        });
    }

    private void clear() {
        for(int i = 0;i < 28;i ++)
            inventSelected[i] = 0;
    }

    private void invert() {
        for(int i = 0;i < 28;i ++)
            click(i);
    }

    public void start() {
        prepareGUI();
        clear();
        mainFrame.setVisible(true);
    }

    public void stop() {
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
    }

    private void check() {
        for(int i = 0;i < 28;i++)
            if(inventSelected[i] == 0)
                invent[i].setText(ctx.inventory.itemAt(i).name());
            else
                invent[i].setText("Not dropped");
    }
    private void drop() {
        int i = 0;
        for(Item item: ctx.inventory.select()) {

            if(!dropAbort) {
                if (inventSelected[i] == 0) {
                    item.interact("Drop");
                }
            } else {
                log.warning("Drop interrupted");
                clear();
                dropAbort = false;
                break;
            }
            i++;
        }
    }

    private void click(int id) {
        if(inventSelected[id] == 0)
            inventSelected[id] = 1;
        else
            inventSelected[id] = 0;
    }

    @Override
    public void poll() {
        check();
        if(dropState) {
            drop();
            dropState = false;
        }
        Condition.sleep(325);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(abort))
            dropAbort = true;
        else if(e.getSource().equals(drop)) {
            log.warning("Drop was clicked.");
            dropState = true;
        } else if(e.getSource().equals(clear)) {
            clear();
        } else if(e.getSource().equals(invert)) {
            invert();
        }
        for(int i=0;i<28;i++) {
            if(e.getSource().equals(invent[i])) {
                click(i);
                break;
            }
        }
    }

    /*Problems:
      ->Button overhead problem{temp solution implemented}
      ->Drop lock problem{temp solution implemented}
     */
}
