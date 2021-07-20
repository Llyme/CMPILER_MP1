import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;

public class MainWindow {

	protected Shell shlScanner;
	private Text editor;
	private Text console;
	private static MainWindow self;
	private Text console_input;
	
	public static String getEditorText() {
		return self.editor.getText();
	}
	
	public static void appendConsoleText(String text) {
		self.console.setText(self.console.getText() + text + "\r\n");
	}
	
	public static void clearConsoleText() {
		self.console.setText("");
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
		
		MenuItem mntmScan = new MenuItem(menu, SWT.CASCADE);
		mntmScan.setText("Scan");
		
		Menu scanMenu = new Menu(mntmScan);
		mntmScan.setMenu(scanMenu);
		
		MenuItem scanContinuous = new MenuItem(scanMenu, SWT.NONE);
		scanContinuous.setText("Continuous");
		scanContinuous.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				clearConsoleText();
				Scanner.read_line(getEditorText());
			}
		});
		
		MenuItem scanPerLine = new MenuItem(scanMenu, SWT.NONE);
		scanPerLine.setText("Per Line");
		scanPerLine.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				clearConsoleText();
				Scanner.read_line(getEditorText());
			}
		});
		
		SashForm sashForm = new SashForm(shlScanner, SWT.SMOOTH);
		sashForm.setSashWidth(10);
		
		editor = new Text(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		editor.setFont(SWTResourceManager.getFont("Consolas", 12, SWT.NORMAL));
		
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
		sashForm_1.setWeights(new int[] {600, 30});
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
	}

}
