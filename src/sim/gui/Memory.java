package sim.gui;


	
	import javax.swing.*;
import java.util.Vector;

	public class Memory {
		public static void main(String[] args)
		{	    	
	    int	memory1[]={2,3,4,5,6,7,8,9};
	        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	        
	        for(int i=0;i<memory1.length;i++){
	        	
		        Vector<Object> a1 = new Vector<Object>();
		        
		        a1.add(i);
		        a1.add(memory1[i]);
	        	
		        data.add(a1);

	        	
	        	
	        }
	        
	        Vector<String> headers = new Vector<String>();
	        headers.add("index");
	        headers.add(" data ");


	        JTable table = new JTable( data, headers );

	        JFrame frame = new JFrame();
	        frame.add( new JScrollPane( table ));
	        frame.pack();
	        frame.setVisible( true ); 

	    }
	
	
	
	
	
	
}
