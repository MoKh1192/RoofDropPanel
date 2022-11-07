
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;

public class RoofDropPanel extends JPanel implements MouseListener
{
	private Color[][] colors;
    private static final Color[] colorChoices = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    
    private JTextField scoreField = new JTextField("0           ");
    private int score = 0;
	
	public static void main(String[] args) throws Exception
	{
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Roof Drop!");
        frame.setResizable(false);
		frame.setVisible(true);
		
		final RoofDropPanel panel = new RoofDropPanel();
		panel.addMouseListener(panel);
        panel.setPreferredSize(new Dimension(500, 500));
        panel.setMinimumSize(new Dimension(500, 500));
		
		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(panel, BorderLayout.CENTER);
        
        panel.scoreField.setEditable(false);
        JPanel south = new JPanel();
        south.add(panel.scoreField);
        JButton restart = new JButton("Restart");
        south.add(restart);
        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.init();
                panel.repaint();
                panel.scoreField.setText("0");
                panel.score = 0;
            }
        });
        c.add(south, BorderLayout.SOUTH);
        
        frame.pack();
	}
	
	public RoofDropPanel()
	{
		init();
	}
    
    public void init() {
        colors = new Color[10][10];
        for(int row=0; row < colors.length; row++) {
            for(int col=0; col < colors[0].length; col++) {
                colors[row][col] = colorChoices[(int)(Math.random()*colorChoices.length)];
            }
        }
    }
	
	//unused methods
	public void mouseClicked(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void paint(Graphics g)
	{
		int panelWidth = getWidth();
		int panelHeight = getHeight();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, panelWidth, panelHeight);
		
		//whenever possible, please use the length's of your array as opposed to numeric literals
		//i.e. array.length instead of 5
		
		//to be assigned...
		int boxWidth = panelWidth / colors[0].length;
		int boxHeight = panelHeight / colors.length;
		
		//you will need to loop over all the boxes in your array, and if they are not null, fill a box of that color
		for(int row=0; row < colors.length; row++) {
            for(int col=0; col < colors[0].length; col++) {
            
                if(colors[row][col] != null) {
                    g.setColor(colors[row][col]);
                    g.fillRect(boxWidth*col, boxHeight*row, boxWidth, boxHeight);
                }
            }
        }
	}
    
    //examines position (row, col) in the colors array - if that color matches the parameter c, then
    //the color in the array ar (row, col) is replaced with null.  Then, the four adjacent spots around (row,col)
    //in the colors array are also analyzed
    //
    //returns the number of colors removed by this function
    public int recursivelyRemove(Color c, int row, int col) {
    
        //BASE CASE 1
        if(row > colors.length-1 || row < 0 || col < 0 || col > colors[0].length-1)  {
        	return 0;
        }
        
        //BASE CASE 2
        if(colors[row][col] != c) {
        	return 0;
        }
        
        //RECURSIVE CASE
        colors[row][col] = null;
        int x = 1;
        x += recursivelyRemove(c, row-1, col);
        x += recursivelyRemove(c, row+1, col);
        x += recursivelyRemove(c, row, col-1);
        x += recursivelyRemove(c, row, col+1);

        return x;
    
    }
    
    //Returns true if colors[row][col] is not null and at least one of the
    //four adjacent colors is the same color
    //preconditions:    0 <= row < colors.length
    //                  0 <= col < colors[0].length
    //
    public boolean clickIsGood(int row, int col) {
        Color c = colors[row][col];
        
        if(c == null)
            return false;
        if(row > 0 && colors[row-1][col] == c)
            return true;
        if(col > 0 && colors[row][col-1] == c)
            return true;
        if(col < colors[0].length-1 && colors[row][col+1] == c)
            return true;
        if(row < colors.length-1 && colors[row+1][col] == c)
            return true;
        
        return false;
    }
    
    //This method causes all color blocks to move down and fill any null spots that were created by a click
    //postconditions:   all colors that are above null spots in the colors array are shifted down
    public void shiftDown() {
    	
    	for(int r = 0; r < colors.length; r++) {
    		for(int c = 0; c < colors[0].length; c++) {
    			if(colors[r][c] == null) {
    				for(int x = r; x > 0; x--) {
    					colors[x][c] = colors[x-1][c];
    				}
					colors[0][c] = null;

    			}
    			
    		}
    	}
    }
    
    //returns true if an entire column in the colors array is true
    //precondition:     if colors[row][col] is not null, then colors[row+i][col] is also not null for all i >= 0
    //                  this is the same as saying there are no nulls beneath a color
    public boolean columnIsFree(int col) {
        return colors[colors.length-1][col] == null;
    }
    
    
    //for every column that is completely blank (all nulls), every color block to the left of the column is shifted to the right 1
    public void shiftRight() {
        for(int c = colors[0].length-1; c > 0; c--) {
        	if(columnIsFree(c) == true) { 
        		for(int r = 0; r < colors.length; r++) {
        			colors[r][c] = colors[r][c-1];
        			colors[r][c-1] = null;
        		}
        		
        	}
        }
    	
    	
    }
	
	//called when the mouse is pressed - determines what row/column the user has clicked
	public void mousePressed(MouseEvent e)
	{
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		
		int boxWidth = panelWidth / colors[0].length;
		int boxHeight = panelHeight / colors.length;
        
        int col = mouseX / boxWidth;
        int row = mouseY / boxHeight;
        
        if(clickIsGood(row, col)) {
            int count = recursivelyRemove(colors[row][col], row, col);
            score += Math.pow(2, count);
            scoreField.setText(score + "");
            shiftDown();
            shiftRight();
        }
        
        
		repaint();
	}
}
