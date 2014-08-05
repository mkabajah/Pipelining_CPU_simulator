// Microprocessor Simulator
// A.Greensted - University of York
// ajg112@ohm.york.ac.uk
// November 2008

package sim.gui;

public interface GUIListener
{
	public void stepPressed();
	public void runPressed(int steps, boolean nonStop, boolean update);
	public void resetPressed();
}
