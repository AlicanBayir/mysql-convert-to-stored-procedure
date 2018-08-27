package com.app.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alicanb on 31.05.2018.
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("SP Generator");
        frame.setContentPane(new ConvertSqlToSp().getConvertSqlToSpPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }
}
