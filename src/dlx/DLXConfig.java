package dlx;


/**
 * DLX Algorithm Configuration
 * @author Deyuan Guo, Dawei Fan
 */
public class DLXConfig {

	/******************** Public Member Variables ********************/

	/** The character for splitting tiles */
	public final char S = ' ';

	/** The debug information switch */
	public boolean verb = true;

	/******************** Private Member Variables ********************/

	/** Enable spin of tiles or not. */
	private boolean enableSpin = false;

	/** Enable spin of tiles or not. */
	private boolean enableSpinFlip = false;

	/** Enable spin of tiles or not, this is set by compare area. */
	private boolean enableExtra = false;

	/** Directly Fail */
	private boolean directlyFail = false;

	/******************** Public Member Functions ********************/

	public DLXConfig() {
	}

	public boolean isEnableSpin() { return enableSpin; }
	public void setEnableSpin(boolean b) { enableSpin = b; }

	public boolean isEnableSpinFlip() { return enableSpinFlip; }
	public void setEnableSpinFlip(boolean b) { enableSpinFlip = b; }

	public boolean isEnableExtra() { return enableExtra; }
	public void setEnableExtra(boolean b) { enableExtra = b; }

	public boolean isDirectlyFail() { return directlyFail; }
	public void setDirectlyFail(boolean b) { directlyFail = b; }

	/******************** Private Member Functions ********************/

}
