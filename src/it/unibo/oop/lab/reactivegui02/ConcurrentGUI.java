package it.unibo.oop.lab.reactivegui02;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.25;
    private static final double HEIGHT_PERC = 0.12;
    private final JLabel textCurrent;
    private final JButton butUp;
    private final JButton butDown;
    private final JButton butStop;

    private enum Direct {
        UP(1), DOWN(-1), STOP(0);

        private int val;

        Direct(final int val) {
            this.val = val;
        }
    }

    public ConcurrentGUI() {
        super();
        this.butUp = new JButton("Up");
        this.butDown = new JButton("Down");
        this.butStop = new JButton("Stop");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        final Agent myThread = new Agent();
        this.textCurrent = new JLabel(String.valueOf(myThread.getCurrent()));
        panel.add(this.textCurrent);
        panel.add(this.butUp);
        panel.add(this.butDown);
        panel.add(this.butStop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        new Thread(myThread).start();

        this.butUp.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                myThread.setDirection(ConcurrentGUI.Direct.UP.val);
            }
        });
        this.butDown.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                myThread.setDirection(ConcurrentGUI.Direct.DOWN.val);
            }
        });
        this.butStop.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                myThread.setDirection(ConcurrentGUI.Direct.STOP.val);
                myThread.stopCount();
                ConcurrentGUI.this.butUp.setEnabled(false);
                ConcurrentGUI.this.butDown.setEnabled(false);
                ConcurrentGUI.this.butStop.setEnabled(false);
            }
        });
    }

    public class Agent implements Runnable {

        private static final int MILLIS_TIME_IMPULSE = 100;
        private int current;
        private int direction;
        private volatile boolean stop;

        public Agent() {
            this.current = 0;
            this.direction = ConcurrentGUI.Direct.STOP.val;
            this.stop = false;
        }

        public void run() {
            try {
                while (!this.stop) {
                    this.current += this.direction;
                    SwingUtilities.invokeAndWait(() -> 
                        ConcurrentGUI.this.textCurrent.setText(Integer.toString(Agent.this.current)));
                    Thread.sleep(Agent.MILLIS_TIME_IMPULSE);
                }
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        public int getCurrent() {
            return this.current;
        }
        public void setDirection(final int direct) {
            this.direction = direct;
        }
        public void stopCount() {
            this.stop = true;
        }
    }

}
