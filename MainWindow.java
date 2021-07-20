import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Label;

public class MainWindow {

	private static MainWindow self;
	protected Shell shlScanner;
	private Text editor;
	private Text console;
	private Text console_input;
	private Label scanLine;
	private Scanner scanner = new Scanner();
	private int line = 0;
	
	public static String getEditorText() {
		return self.editor.getText();
	}
	
	public static void appendConsoleText(String text) {
		self.console.setText(self.console.getText() + text + "\r\n");
	}
	
	public static void clearConsoleText() {
		self.console.setText("");
	}
	
	private static void setLine(int i) {
		self.line = i;
		
		if (i > -1)
			self.scanLine.setText("Cursor is in line " + (i + 1) + ".");
		else
			self.scanLine.setText("Cursor is at the end.");
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		self = this;
		Display display = Display.getDefault();
		createContents();
		shlScanner.open();
		shlScanner.layout();
		while (!shlScanner.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlScanner = new Shell();
		shlScanner.setSize(800, 600);
		shlScanner.setText("Scanner");
		shlScanner.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = new Menu(shlScanner, SWT.BAR);
		shlScanner.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu fileMenu = new Menu(mntmFile);
		mntmFile.setMenu(fileMenu);
		
		MenuItem fileOpen = new MenuItem(fileMenu, SWT.NONE);
		fileOpen.setText("Open");
		
		SashForm sashForm = new SashForm(shlScanner, SWT.SMOOTH);
		sashForm.setSashWidth(10);
		
		SashForm sashForm_3 = new SashForm(sashForm, SWT.SMOOTH | SWT.VERTICAL);
		sashForm_3.setSashWidth(0);
		
		editor = new Text(sashForm_3, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		editor.setFont(SWTResourceManager.getFont("Consolas", 12, SWT.NORMAL));
		
		scanLine = new Label(sashForm_3, SWT.NONE);
		scanLine.setAlignment(SWT.CENTER);
		scanLine.setText("Cursor is in line 1.");
		
		SashForm sashForm_4 = new SashForm(sashForm_3, SWT.NONE);
		
		Button scanReset = new Button(sashForm_4, SWT.NONE);
		scanReset.setText("Reset");
		scanReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scanner = new Scanner();
				clearConsoleText();
				setLine(0);
				editor.setEditable(true);
			}
		});
		
		Button scanNextLine = new Button(sashForm_4, SWT.NONE);
		scanNextLine.setText("Next Line");
		scanNextLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (line == -1)
					return;
				
				String[] lines = getEditorText().split("\\r?\\n");
				scanner.read_line(lines[line]);
				
				if (line == lines.length - 1)
					setLine(-1);
				else
					setLine(line + 1);

				editor.setEditable(false);
			}
		});
		
		Button scanAll = new Button(sashForm_4, SWT.NONE);
		scanAll.setText("Scan All");
		scanAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (line == -1)
					return;
				
				String[] lines = getEditorText().split("\\r?\\n");
				
				for (; line < lines.length; line++)
					scanner.read_line(lines[line]);
				
				setLine(-1);
				editor.setEditable(false);
			}
		});
		
		
		sashForm_4.setWeights(new int[] {1, 1, 1});
		sashForm_3.setWeights(new int[] {552, 24, 24});
		
		SashForm sashForm_1 = new SashForm(sashForm, SWT.SMOOTH | SWT.VERTICAL);
		sashForm_1.setSashWidth(0);
		
		console = new Text(sashForm_1, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		console.setFont(SWTResourceManager.getFont("Consolas", 12, SWT.NORMAL));
		
		SashForm sashForm_2 = new SashForm(sashForm_1, SWT.NONE);
		sashForm_2.setSashWidth(0);
		
		console_input = new Text(sashForm_2, SWT.BORDER);
		
		Button console_input_submit = new Button(sashForm_2, SWT.NONE);
		console_input_submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		console_input_submit.setText("Submit");
		sashForm_2.setWeights(new int[] {400, 100});
		sashForm_1.setWeights(new int[] {576, 24});
		sashForm.setWeights(new int[] {1, 1});

		sashForm_2.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				int width = sashForm_2.getClientArea().width;
				
				if (width > 100) {
					int weight = 1000000 / width;
					sashForm_2.setWeights(new int[] {
							10000 - weight,
							weight
					});
				}
			}
		});
		
		sashForm_1.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				int height = sashForm_1.getClientArea().height;
				
				if (height > 23) {
					int weight = 230000 / height;
					sashForm_1.setWeights(new int[] {
							10000 - weight,
							weight
					});
				}
			}
		});
		
		sashForm_3.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				int height = sashForm_3.getClientArea().height;
				
				if (height > 36) {
					int weight = 230000 / height;
					sashForm_3.setWeights(new int[] {
							10000 - weight * 2,
							weight,
							weight
					});
				}
			}
		});
	}

}
