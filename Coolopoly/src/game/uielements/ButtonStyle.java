package game.uielements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import misc.design.Design;

public class ButtonStyle {
	
	public Color	backgroundColor			= Design.BUTTON_DEFAULT_BACKGROUND_COLOR,
					hoverBackgroundColor	= Design.BUTTON_DEFAULT_HOVER_BACKGROUND_COLOR,
					clickBackgroundColor	= Design.BUTTON_DEFAULT_CLICK_BACKGROUND_COLOR,
					
					foregroundColor			= Design.BUTTON_DEFAULT_FOREGROUND_COLOR,
					hoverForegroundColor	= Design.BUTTON_DEFAULT_HOVER_FOREGROUND_COLOR,
					clickForegroundColor	= Design.BUTTON_DEFAULT_CLICK_FOREGROUND_COLOR,
					
					borderColor				= Design.BUTTON_DEFAULT_BORDER_COLOR,
					hoverBorderColor		= Design.BUTTON_DEFAULT_HOVER_BORDER_COLOR,
					clickBorderColor		= Design.BUTTON_DEFAULT_CLICK_BORDER_COLOR,
					
					disabledBackgroundColor	= Design.BUTTON_DEFAULT_DISABLED_BACKGROUND_COLOR,
					disabledForegroundColor	= Design.BUTTON_DEFAULT_DISABLED_FOREGROUND_COLOR,
					disabledBorderColor		= Design.BUTTON_DEFAULT_DISABLED_BORDER_COLOR,
					
					selectedBackgroundColor	= Design.BUTTON_DEFAULT_SELECTED_BACKGROUND_COLOR,
					selectedForegroundColor	= Design.BUTTON_DEFAULT_SELECTED_FOREGROUND_COLOR,
					selectedBorderColor		= Design.BUTTON_DEFAULT_SELECTED_BORDER_COLOR;
	
	public Font		labelFont				= Design.BUTTON_LABEL_FONT;
	
	public Stroke	borderStroke			= Design.BUTTON_BORDER_STROKE;
			
	public int		labelHorizontalPadding	= Design.BUTTON_LABEL_HORIZONTAL_PADDING,
					labelVerticalPadding	= Design.BUTTON_LABEL_VERTICAL_PADDING,
					borderRadius			= Design.BUTTON_BORDER_RADIUS;
	
	public float	iconMaxWidthRatio		= Design.BUTTON_ICON_MAX_WIDTH_RATIO,
					iconMaxHeightRatio		= Design.BUTTON_ICON_MAX_HEIGHT_RATIO;

	public ButtonStyle() {}
	
	// COLORS
	
	public ButtonStyle withBackgroundColor(Color c) {
		backgroundColor = c;
		return this;
	}
	
	public ButtonStyle withHoverBackgroundColor(Color c) {
		hoverBackgroundColor = c;
		return this;
	}
	
	public ButtonStyle withClickBackgroundColor(Color c) {
		clickBackgroundColor = c;
		return this;
	}
	
	public ButtonStyle withForegroundColor(Color c) {
		foregroundColor = c;
		return this;
	}
	
	public ButtonStyle withHoverForegroundColor(Color c) {
		hoverForegroundColor = c;
		return this;
	}
	
	public ButtonStyle withClickForegroundColor(Color c) {
		clickForegroundColor = c;
		return this;
	}
	
	public ButtonStyle withBorderColor(Color c) {
		borderColor = c;
		return this;
	}
	
	public ButtonStyle withHoverBorderColor(Color c) {
		hoverBorderColor = c;
		return this;
	}
	
	public ButtonStyle withClickBorderColor(Color c) {
		clickBorderColor = c;
		return this;
	}
	
	public ButtonStyle withDisabledBackgroundColor(Color c) {
		disabledBackgroundColor = c;
		return this;
	}
	
	public ButtonStyle withDisabledForegroundColor(Color c) {
		disabledForegroundColor = c;
		return this;
	}
	
	public ButtonStyle withDisabledBorderColor(Color c) {
		disabledBorderColor = c;
		return this;
	}
	
	public ButtonStyle withSelectedBackgroundColor(Color c) {
		selectedBackgroundColor = c;
		return this;
	}
	
	public ButtonStyle withSelectedForegroundColor(Color c) {
		selectedForegroundColor = c;
		return this;
	}
	
	public ButtonStyle withSelectedBorderColor(Color c) {
		selectedBorderColor = c;
		return this;
	}
	
	// FONT
	
	public ButtonStyle withLabelFont(Font f) {
		labelFont = f;
		return this;
	}
	
	// LABEL PADDING
	
	public ButtonStyle withLabelHorizontalPadding(int p) {
		labelHorizontalPadding = p;
		return this;
	}
	
	public ButtonStyle withLabelVerticalPadding(int p) {
		labelVerticalPadding = p;
		return this;
	}
	
	// STROKE
	
	public ButtonStyle withBorderStroke(Stroke s) {
		borderStroke = s;
		return this;
	}
	
	// RADIUS
	
	public ButtonStyle withBorderRadius(int r) {
		borderRadius = r;
		return this;
	}
	
	// ICON
	
	public ButtonStyle withIconMaxWidthRatio(float r) {
		iconMaxWidthRatio = r;
		return this;
	}
	
	public ButtonStyle withIconMaxHeightRatio(float r) {
		iconMaxHeightRatio = r;
		return this;
	}

}
