package org.example;

import org.example.view.MainFrame;

public class ApplicationFramework {

    private MainFrame mainFrame;

    public void initializeView() {
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

}
