package demo;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import apainter.APainter;
import apainter.BindKey;
import apainter.Device;
import apainter.ExitListener;
import apainter.bind.BindObject;
import apainter.canvas.CanvasHandler;
import apainter.drawer.painttool.Eraser;
import apainter.drawer.painttool.Pen;
import apainter.misc.Angle;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;
import demo.colorpicker.hsv.BoxH;
import demo.colorpicker.hsv.SVIcon;

public class APainterDemo extends JFrame {
	int canvaswidth = 400,canvasheight = 400;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					APainterDemo frame = new APainterDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private APainter apainter;
	private CanvasHandler canvash;
	private JPanel contentPane;
	private JPanel setting;
	private JPanel colorpanel;
	private JPanel panel;
	private JPanel penpanel;
	private JPanel viewpanel;
	private JScrollBar xscroll;
	private JScrollBar yscroll;
	private JPanel view;


	/**
	 * Create the frame.
	 */
	public APainterDemo() {
		setTitle("APainterDemo");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				apainter.exec("exit");
			}
		});
		setBounds(100, 100, 890, 629);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu = new JMenu("ファイル");
		menuBar.add(menu);

		savefilemenu = new JMenuItem("ファイルを保存する");
		savefilemenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser f = new JFileChooser(new File("."));
				FileFilter jf = new FileNameExtensionFilter("jpeg", "jpg","jpeg");
				FileFilter pf = new FileNameExtensionFilter("png", "png");
				FileFilter gf = new FileNameExtensionFilter("gif", "gif");
				f.addChoosableFileFilter(gf);
				f.addChoosableFileFilter(pf);
				f.addChoosableFileFilter(jf);
				int ret = f.showSaveDialog(APainterDemo.this);
				if(ret == 0){
					File file = f.getSelectedFile();
					String fileName = file.getName();
					int point = fileName.lastIndexOf(".");
					String ex;
				    if (point != -1) {
				       ex=fileName.substring(point + 1);
				       if(!("jpg".equals(ex)||"jpeg".equals(ex)||"gif".equals(ex)||"png".equals(ex))){
					    	FileNameExtensionFilter ff  =(FileNameExtensionFilter) f.getFileFilter();
					    	ex=ff.getExtensions()[0];
					    	file = new File(file.getPath()+"."+ex);
				       }
				    }else{
				    	FileNameExtensionFilter ff  =(FileNameExtensionFilter) f.getFileFilter();
				    	ex=ff.getExtensions()[0];
				    	file = new File(file.getPath()+"."+ex);
				    }
				    try {
				    	BufferedImage bf;
				    	if(ex.equals("jpeg")||ex.equals("jpg")){
				    		bf = new BufferedImage(canvash.getWidth(),canvash.getHeight(),BufferedImage.TYPE_INT_RGB);
				    		Graphics2D g = bf.createGraphics();
				    		g.setColor(Color.white);
				    		g.fillRect(0, 0, canvaswidth, canvasheight);
				    		g.drawImage(canvash.getImage(),0,0,null);
				    		g.dispose();
				    	}else bf = canvash.getImage();

						ImageIO.write(bf,ex,file);
					} catch (IOException e1) {
					}
				}

			}
		});

		newcanvasmenu = new JMenuItem("新規キャンバス");
		newcanvasmenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value =JOptionPane.showInputDialog(newcanvasmenu, "キャンバスの幅と高さを入力（,区切り）");
				if(value !=null){
					String[] s = value.split(",");
					if(s.length >=2){
						try{
							int w = Integer.parseInt(s[0]);
							int h= Integer.parseInt(s[1]);

							if(w<=0 || h <= 0||APainter.getMaxCanvasSize() < w || APainter.getMaxCanvasSize() < h){
								JOptionPane.showMessageDialog(APainterDemo.this, "入力値が不正です。値は1~"+APainter.getMaxCanvasSize()+"で指定してください");
								return;
							}
							canvaswidth = w;
							canvasheight = h;

							initCanvas();
						}catch(NumberFormatException ex){
							JOptionPane.showMessageDialog(APainterDemo.this, "入力値が不正です。値は1~"+APainter.getMaxCanvasSize()+"で指定してください");
						}
					}else{
						JOptionPane.showMessageDialog(APainterDemo.this, "入力値が不正です。");
					}
				}
			}
		});
		menu.add(newcanvasmenu);
		menu.add(savefilemenu);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		setting = new JPanel();
		setting.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u8A2D\u5B9A\u9805\u76EEs", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setting.setPreferredSize(new Dimension(400, 10));
		contentPane.add(setting, BorderLayout.EAST);
		setting.setLayout(new GridLayout(0, 2, 0, 0));

		colorpanel = new JPanel();
		colorpanel.setBorder(new TitledBorder(null, "\u8272\u8A2D\u5B9A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setting.add(colorpanel);

		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel_1.setPreferredSize(new Dimension(200, 40));
		colorpanel.add(panel_1);

		frontcolorpanel = new JPanel();
		frontcolorpanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				hsv.setBind(frontcolorbind);
				colorselectfront = true;
			}
		});
		frontcolorpanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		frontcolorpanel.setPreferredSize(new Dimension(30, 30));
		panel_1.add(frontcolorpanel);

		backcolorpanel = new JPanel();
		backcolorpanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				hsv.setBind(backcolorbind);
				colorselectfront = false;
			}
		});
		backcolorpanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		backcolorpanel.setPreferredSize(new Dimension(30, 30));
		panel_1.add(backcolorpanel);

		colorselectpanel = new JPanel();
		colorpanel.add(colorselectpanel);
		hsv = new HSVPanel();
		colorselectpanel.add(hsv);



		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "\u8868\u793A\u8A2D\u5B9A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setting.add(panel);

		label = new JLabel("回転");

		rotSlider = new JSlider(-180,180,0);
		rotSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = rotSlider.getValue();
				Angle a = new Angle(v);
				anglebind.set(a);
				canvash.repaint_only_rotation();
			}
		});

		rotLabel = new JLabel("0");

		label_2 = new JLabel("拡大");

		zoomSlider = new JSlider(-95, 150, 0);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int v = zoomSlider.getValue();
						double d;
						if(v==0)d=1;
						else if(v < 0){
							d = (100+v)*0.01;
						}else{
							d = (v+10)/10d;
						}
						zoombind.set(d);
						canvash.repaint();
					}
				});
			}
		});

		zoomLabel = new JLabel("1");

		rotreset = new JButton("reset");
		rotreset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotSlider.setValue(0);
			}
		});

		btnReset = new JButton("reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomSlider.setValue(0);
			}
		});

		final JCheckBox chckbxQuality = new JCheckBox("quality");
		chckbxQuality.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canvash.setViewQuality(chckbxQuality.isSelected());
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addComponent(label)
									.addGroup(gl_panel.createSequentialGroup()
										.addGap(10)
										.addComponent(rotLabel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
									.addGroup(gl_panel.createSequentialGroup()
										.addGap(10)
										.addComponent(zoomLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
									.addComponent(label_2))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(gl_panel.createSequentialGroup()
										.addComponent(btnReset)
										.addGap(34))
									.addGroup(gl_panel.createSequentialGroup()
										.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
											.addComponent(rotSlider, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
											.addComponent(zoomSlider, 0, 0, Short.MAX_VALUE))
										.addContainerGap())))
							.addGroup(gl_panel.createSequentialGroup()
								.addComponent(rotreset)
								.addGap(38)))
						.addComponent(chckbxQuality)))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(rotLabel))
						.addComponent(rotSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(13)
					.addComponent(rotreset)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(zoomSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(zoomLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(34)
							.addComponent(btnReset)))
					.addGap(17)
					.addComponent(chckbxQuality)
					.addGap(70))
		);
		panel.setLayout(gl_panel);

		penpanel = new JPanel();
		penpanel.setBorder(new TitledBorder(null, "\u7B46\u8A2D\u5B9A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setting.add(penpanel);

		selectpen = new JRadioButton("ペン");
		selectpen.setSelected(true);

		eraselect = new JRadioButton("消し");

		pensize = new JSlider(1, 100, 1);
		pensize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int size = pensize.getValue()*10;
				PenShapeFactory penf = (PenShapeFactory) apainter.exec("getpenf 0");
				PenShape p = penf.createPenShape(size, Device.CPU);
				if(selectpen.isSelected()){
					pen.setPen(p);
				}else if(eraselect.isSelected()){
					era.setPen(p);
				}
				sizelabel.setText("筆サイズ "+pensize.getValue());
			}
		});

		sizelabel = new JLabel("筆サイズ 1");

		noudo = new JSlider(0, 0, 128, 128);
		noudo.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double v = noudo.getValue()/128d;
				if(selectpen.isSelected()){
					pen.setDensity(v);
				}else if(eraselect.isSelected()){
					era.setDensity(v);
				}
				noudolabel.setText("濃度 "+(int)(v*100)+"%");
			}
		});

		noudolabel = new JLabel("濃度 100%");

		minsize = new JSlider();
		minsize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double v = minsize.getValue()/128d;
				if(selectpen.isSelected()){
					pen.setMinSize(v);
				}else if(eraselect.isSelected()){
					era.setMinSize(v);
				}
			}
		});

		minnoudo = new JSlider();
		minnoudo.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double v = minnoudo.getValue()/128d;
				if(selectpen.isSelected()){
					pen.setMinDensity(v);
				}else if(eraselect.isSelected()){
					era.setMinDensity(v);
				}
			}
		});

		JLabel label_4 = new JLabel("最小筆圧でのサイズ、濃度の割合");
		GroupLayout gl_penpanel = new GroupLayout(penpanel);
		gl_penpanel.setHorizontalGroup(
			gl_penpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_penpanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_penpanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_penpanel.createSequentialGroup()
							.addComponent(selectpen)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(eraselect)
							.addContainerGap(80, Short.MAX_VALUE))
						.addGroup(gl_penpanel.createSequentialGroup()
							.addComponent(sizelabel)
							.addContainerGap(130, Short.MAX_VALUE))))
				.addGroup(gl_penpanel.createSequentialGroup()
					.addComponent(noudo, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_penpanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(noudolabel)
					.addContainerGap(146, Short.MAX_VALUE))
				.addGroup(gl_penpanel.createSequentialGroup()
					.addComponent(minsize, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_penpanel.createSequentialGroup()
					.addComponent(minnoudo, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_penpanel.createSequentialGroup()
					.addComponent(label_4)
					.addContainerGap())
				.addGroup(gl_penpanel.createSequentialGroup()
					.addComponent(pensize, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_penpanel.setVerticalGroup(
			gl_penpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_penpanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_penpanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(selectpen)
						.addComponent(eraselect))
					.addGap(7)
					.addComponent(sizelabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(pensize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addComponent(noudolabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(noudo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(label_4)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(minsize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(minnoudo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(12, Short.MAX_VALUE))
		);
		penpanel.setLayout(gl_penpanel);

		viewpanel = new JPanel();
		contentPane.add(viewpanel, BorderLayout.CENTER);
		viewpanel.setLayout(new BorderLayout(0, 0));

		xscroll = new JScrollBar(Adjustable.HORIZONTAL, 0, 2000, 0, 12000);
		xscroll.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int v = e.getValue();
				double x=(v-5000)*zoom*canvaswidth/10000;
				Point2D.Double p = new Point2D.Double(x, posy);
				posbind.set(p);
				canvash.repaintOnlyMove();
			}
		});
		xscroll.setOrientation(Adjustable.HORIZONTAL);
		viewpanel.add(xscroll, BorderLayout.SOUTH);

		yscroll = new JScrollBar(Adjustable.VERTICAL,0,2000,0,12000);
		yscroll.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int v = e.getValue();
				double y=(v-5000)*zoom*canvasheight/10000;
				Point2D.Double p = new Point2D.Double(posx, y);
				posbind.set(p);
				canvash.repaintOnlyMove();
			}
		});
		viewpanel.add(yscroll, BorderLayout.EAST);

		view = new JPanel();
		viewpanel.add(view, BorderLayout.CENTER);


		ButtonGroup buttongroup = new ButtonGroup();
		buttongroup.add(selectpen);
		buttongroup.add(eraselect);


		initAPainter();
	}

	double posx=0,posy=0;
	double zoom=1;
	Angle angle = new Angle();


	boolean colorselectfront = true;

	BindObject
	frontcolorbind = new BindObject(){

		@Override
		public void setValue(Object value) throws Exception {
			float[] f = (float[])value;
			if(colorselectfront)
				hsv.setColor(f[0], f[1], f[2], f[3]);
			Color c = new Color(f[1], f[2], f[3],f[0]);
			frontcolorpanel.setBackground(c);
		}

	},
	backcolorbind = new BindObject(){

		@Override
		public void setValue(Object value) throws Exception {
			float[] f = (float[])value;
			if(!colorselectfront)
				hsv.setColor(f[0], f[1], f[2], f[3]);
			Color c = new Color(f[1], f[2], f[3],f[0]);
			backcolorpanel.setBackground(c);
		}

	}

	,zoombind = new BindObject(){

		@Override
		public void setValue(Object value) throws Exception {
			double d = (Double)value;
			zoom = d;
			ChangeListener[] ls = GUIUtillity.removeAllChangeListeners(zoomSlider);
			int v;
			if(zoom < 1){
				v = (int) ((zoom-1)*100);
			}else{
				v = (int) ((zoom-1)*10);
			}
			zoomSlider.setValue(v);
			GUIUtillity.addAllChangeListeners(zoomSlider, ls);
			zoomLabel.setText(String.format("%3f", zoom));
		}

	}

	,anglebind = new BindObject(){

		@Override
		public void setValue(Object value) throws Exception {
			Angle a = (Angle)value;
			angle = a;
			ChangeListener[] lisn;
			lisn = GUIUtillity.removeAllChangeListeners(rotSlider);
			int deg = (int)angle.degree;
			if(deg > 180)deg = deg-360;
			rotSlider.setValue(deg);
			GUIUtillity.addAllChangeListeners(rotSlider, lisn);
			rotLabel.setText(Integer.toString(deg));
		}

	}

	,posbind=new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			Point2D p = (Point2D)value;
			double x = p.getX(),y=p.getY();
			posx = x;
			posy = y;
			AdjustmentListener[] lisn;
			lisn = GUIUtillity.removeAllAdjustmentListeners(xscroll);
			xscroll.setValue((int)(x*10000/canvaswidth/zoom)+5000);
			GUIUtillity.addAllAdjustmentListenre(xscroll, lisn);

			lisn = GUIUtillity.removeAllAdjustmentListeners(yscroll);
			yscroll.setValue((int)(y*10000/canvasheight/zoom)+5000);
			GUIUtillity.addAllAdjustmentListenre(yscroll, lisn);
		}
	};
	JComponent apainterview ;
	private JSlider rotSlider;
	private JLabel label;
	private JLabel rotLabel;
	private JLabel label_2;
	private JSlider zoomSlider;
	private JLabel zoomLabel;
	private JButton rotreset;
	private JButton btnReset;
	private JPanel panel_1;
	private JPanel frontcolorpanel;
	private JPanel backcolorpanel;
	private JPanel colorselectpanel;
	private HSVPanel hsv;
	private JSlider pensize;
	private JSlider noudo;
	private JRadioButton selectpen;
	private JRadioButton eraselect;
	private JLabel sizelabel;


	private Pen pen;
	private Eraser era;
	private JLabel noudolabel;
	private JSlider minsize;
	private JSlider minnoudo;
	private JMenuItem newcanvasmenu;
	private JMenuItem savefilemenu;
	private void initAPainter(){
		apainter = APainter.createAPainter(Device.CPU);
		apainter.addExitListener(new EL());

		Object o = apainter.exec("getdrawer 0");
		if (o instanceof Pen) {
			pen = (Pen) o;
		}
		o = apainter.exec("getdrawer 1");
		if (o instanceof Eraser) {
			era = (Eraser) o;
		}

		hsv.setBind(frontcolorbind);
		apainter.bind( BindKey.FrontColorBIND,frontcolorbind);
		apainter.bind(BindKey.BackColorBIND,backcolorbind);
		apainter.debagON();

		initCanvas();

	}
	private void initCanvas(){
		CanvasHandler c = apainter.createNewCanvas(canvaswidth, canvasheight);
		if(canvash!=null){
			canvash.dispose();
		}
		canvash = c;

		canvash.bind(posbind, BindKey.CanvasPositionBIND);
		canvash.bind(zoombind, BindKey.ZoomBIND);
		canvash.bind(anglebind, BindKey.AngleBIND);

		apainterview =canvash.getComponent();
		view.removeAll();
		view.add(apainterview,BorderLayout.CENTER);
		view.validate();

	}


	private static class EL implements ExitListener{
		@Override
		public boolean exiting(APainter apainter) {
			return true;
		}

		@Override
		public void exit(APainter apainter) {

		}

		@Override
		public void exited(APainter apainter) {
			System.exit(0);
		}
	}
}

class HSVPanel extends JComponent{
	float h=1,s=0,v=0;
	int svSize = 130;
	SVIcon svicon = new SVIcon(svSize);
	BoxH hicon = new BoxH(svSize, 15);
	BindObject bindobj;
	JComponent svp = new JComponent() {
		protected void paintComponent(Graphics g) {
			svicon.paintIcon(this, g, 0, 0);
		}
	};
	JComponent hp = new JComponent() {
		protected void paintComponent(Graphics g) {
			hicon.paintIcon(this, g, 0, 0);
		}
	};

	public void setBind(BindObject o){
		bindobj = o;
	}

	public void setColor(float a,float r,float g,float b){
		float[] fs = Color.RGBtoHSB((int)(r*255), (int)(g*255), (int)(b*255), null);
		s = fs[1];
		v = fs[2];
		if(!(s==0))
				h = fs[0]*360;
		svicon.setH(h);
		svicon.setSV(s, v);
		hicon.setH(h);
		hicon.setSV(s, v);
		svp.repaint();
		hp.repaint();
	}

	public HSVPanel() {
		setLayout(new FlowLayout());
		svp.setPreferredSize(new Dimension(svSize,svSize));
		hp.setPreferredSize(new Dimension(15,svSize));
		svicon.setH(0);
		svicon.setSV(0, 0);
		MouseAdapter m = new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				float V = 1-p.y/130f;
				float S = p.x/130f;
				if(V<0)V=0;else if(V>1)V=1;
				if(S<0)S=0;else if(S>1)S=1;
				Color c = Color.getHSBColor(h/360, S, V);
				float[] a = {c.getAlpha()/255f,c.getRed()/255f,c.getGreen()/255f,c.getBlue()/255f};
				if(bindobj!=null)bindobj.set(a);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDragged(e);
			}
		};
		svp.addMouseListener(m);
		svp.addMouseMotionListener(m);
		m = new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int y = e.getY();
				float H = y/130f;
				if(H<0)H=0;else if(H>1)H=1;
				h = H*360;
				Color c = Color.getHSBColor(H, s, v);
				float[] a = {c.getAlpha()/255f,c.getRed()/255f,c.getGreen()/255f,c.getBlue()/255f};
				if(bindobj!=null)bindobj.set(a);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseDragged(e);
			}
		};
		hp.addMouseListener(m);
		hp.addMouseMotionListener(m);
		add(svp);
		add(hp);
	}
}