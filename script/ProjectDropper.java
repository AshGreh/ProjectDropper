package scripts;

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
 * Created by Terminator1 on 12/16/2016.
 */
@Script.Manifest(description = "Drops selected inventory items v0.124 Author:Terminator1", name ="Dropper")
public class ProjectDropper extends PollingScript<ClientContext> implements ActionListener{

    private JFrame mainFrame;
    private Boolean exitCall = false, dropState = false;
    private JButton invent[] = new JButton[30],check,drop;
    private int inventSelected[] = new int[28];

    private void prepareGUI(){
        mainFrame = new JFrame("Dropper interface");
        mainFrame.setSize(500,650);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                exitCall = true;
            }
        });
        mainFrame.setLayout(new GridLayout(8,4));
        for(int i = 0;i<28;i++) {
            invent[i] = new JButton(ctx.inventory.itemAt(i).name());
            invent[i].addActionListener(this);
            mainFrame.add(invent[i]);
        }
        check = new JButton("Check");
        drop = new JButton("Drop");
        check.addActionListener(this);
        drop.addActionListener(this);
        mainFrame.add(check);
        mainFrame.add(drop);
        mainFrame.add(new JButton("Soon"));
        mainFrame.add(new JButton("Soon"));
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ctx.controller.stop();
            }
        });
    }

    public void start() {
        prepareGUI();
        mainFrame.setVisible(true);
        for(int i = 0;i < 28;i ++)
            inventSelected[i] = 0;
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
            if(inventSelected[i] == 0) {
                item.interact("Drop");
            }
            i++;
        }
    }

    @Override
    public void poll() {
        check();
        if(dropState) {
            drop();
            dropState = false;
        }
    }

    private void click(int id) {
        if(inventSelected[id] == 0)
            inventSelected[id] = 1;
        else
            inventSelected[id] = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(check))
            check();
        else if(e.getSource().equals(drop)) {
            log.warning("Drop was clicked.");
            dropState = true;
        }
        for(int i=0;i<28;i++) {
            if(e.getSource().equals(invent[i])) {
                click(i);
                break;
            }
        }
    }

}
