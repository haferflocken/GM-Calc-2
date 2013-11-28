package org.gmcalc2.gui;

import org.haferslick.gui.GUISubcontext;
import org.haferslick.gui.OutputFrame;
import org.haferutil.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class LogDisplay extends GUISubcontext {
	
	private static final String TITLE = "Log";
	
	private Font titleFont, logFont;
	private Color textColor, titleBackgroundColor;
	private Color logViewBackgroundColor;
	private OutputFrame logView;

	// Constructor.
	public LogDisplay(int x, int y, int width, int height, int depth, int scrollBarWidth,
			Font titleFont, Font logFont, Color textColor, Color titleBackgroundColor, Color logViewBackgroundColor) {
		super(x, y, width, height, depth);
		
		this.titleFont = titleFont;
		this.logFont = logFont;
		this.textColor = textColor;
		this.titleBackgroundColor = titleBackgroundColor;
		this.logViewBackgroundColor = logViewBackgroundColor;
		
		makeLog(scrollBarWidth);
	}
	
	private void makeLog(int scrollBarWidth) {
		int logX = x1;
		int logY = y1 + titleFont.getLineHeight();
		int logWidth = width;
		int logHeight = height - titleFont.getLineHeight();
		logView = new OutputFrame(logX, logY, logWidth, logHeight, depth, logFont, textColor, scrollBarWidth, textColor);
		logView.append(Log.getDefaultLog().getContents());
		Log.getDefaultLog().addObserver(logView);
		subcontext.addElement(logView);
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(logViewBackgroundColor);
		g.fillRect(x1, y1, width, height);
		g.setColor(titleBackgroundColor);
		g.fillRect(x1, y1, width, titleFont.getLineHeight());
		
		// Draw the title.
		g.setFont(titleFont);
		g.setColor(textColor);
		g.drawString(TITLE, x1 + 4, y1);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}
	
	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		logView.setWidth(w);
	}
	
	@Override
	public void setHeight(int h) {
		super.setHeight(h);
		logView.setHeight(h + titleFont.getLineHeight());
	}
	
	@Override
	public void destroy() {
		Log.getDefaultLog().removeObserver(logView);
	}

}
