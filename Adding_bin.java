import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import ij.plugin.filter.PlugInFilter;
import javax.swing.*;
import ij.text.TextWindow;
import ij.io.Opener;
import java.io.*;
import java.net.*;

public class Adding_bin implements PlugInFilter {
 

	static ImageProcessor ip;
	static ij.measure.Calibration cal;
	static double cala;
	static double calb;
	static int signoff;
	static String maintitle;
        		
	public int setup(String arg, ImagePlus imp) {
		maintitle=imp.getTitle();
		maintitle=maintitle+" - ";
		cal = imp.getCalibration(); // take calibration.  This takes care of signed/unsigned problems
		if (cal.calibrated()) {
			double[] calcoeffs=cal.getCoefficients();
			cala=calcoeffs[0];
			calb=calcoeffs[1];
		} else {
			cala=0;
			calb=0;
		}
		signoff = 0-(int)cala;
		
		return DOES_ALL+NO_CHANGES;
	}
	
	public void run(ImageProcessor ipp) {

	ip=ipp.convertToShort(false); // code doesn't work with floats so make into 16bit integer image
	
	Panel panel = new Panel();
	int xfactor=2;
	int yfactor=2;
	
	int new_x, new_y, i, j;
	int new_value=0;
			
	panel.setLayout(new FlowLayout(FlowLayout.CENTER));
	
	GenericDialog gd = new GenericDialog("Adding bin options");
	gd.addMessage("Adding bin\nby Adrian Martin, 14 Aug 2009\n\nSend bugs, suggestions and cash to:\nadrian@sensorsciences.com");
	gd.addNumericField("X bin size",xfactor,0);
	gd.addNumericField("Y bin size",yfactor,0);
	
	gd.addPanel(panel);
	gd.showDialog();
	
	xfactor = (int)gd.getNextNumber();
	yfactor = (int)gd.getNextNumber();
		
	if (gd.wasCanceled()) {
		return;
	}
	
	int newxsize=ip.getWidth()/xfactor;
	int newysize=ip.getHeight()/yfactor;
	
	String bin_title=maintitle+"Rebinned ("+xfactor+","+yfactor+") ";
	ImagePlus new_imp=WindowManager.getImage(bin_title);
	if (new_imp!=null) {
		new_imp.hide();
	}
		
	new_imp=NewImage.createShortImage(bin_title,newxsize,newysize,1,NewImage.FILL_BLACK);
	ImageProcessor new_ip=new_imp.getProcessor();
	
	for (new_x=0; new_x<newxsize; new_x++) {
		for (new_y=0; new_y<newysize; new_y++) {
			for (i=0; i<xfactor; i++) {
				for (j=0; j<yfactor; j++) {
					new_value=ip.getPixel((new_x*xfactor)+i-1, (new_y*yfactor)+j-1)-signoff+new_value;
					}
				}
			new_ip.putPixel(new_x, new_y, new_value);
			new_value=0;
			}
		}
	new_imp.show();
	new_imp.updateAndDraw();
	
	} 
	
	//run

} //Adrian_FWHM

