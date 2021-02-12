/**
 * MilkWeight final project for A team 128
 * Project members: Joshua Faessler, Daniel Kouchekina, Ryan Sun, and Thiago Braga
 */


package application.gui;

import java.io.File;
import java.util.List;

/**
 * Represents a single file selection event handler
 * @author Daniel Kouchekinia
 *
 */
@FunctionalInterface
public interface FileSelectEventHandler {
	public void filesSelected(List<File> files);
}
