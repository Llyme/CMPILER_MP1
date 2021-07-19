import org.eclipse.swt.widgets.Display;
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

public class MainWindow {

	protected Shell shlScanner;
	private Text editor;
	private Text console;
	private static MainWindow self;
	
	public static String getEditorText() {
		return self.editor.getText();
	}
	
	public static void setConsoleText(String text) {
		self.console.setText(text);
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
		shlScanner.setSize(450, 300);
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
				Scanner.read_line(null, getEditorText());
			}
		});
		
		MenuItem scanPerLine = new MenuItem(scanMenu, SWT.NONE);
		scanPerLine.setText("Per Line");
		scanPerLine.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Scanner.read_line(null, getEditorText());
			}
		});
		
		SashForm sashForm = new SashForm(shlScanner, SWT.SMOOTH);
		
		editor = new Text(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		editor.setFont(SWTResourceManager.getFont("Consolas", 12, SWT.NORMAL));
		
		console = new Text(sashForm, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		console.setFont(SWTResourceManager.getFont("Consolas", 12, SWT.NORMAL));
		sashForm.setWeights(new int[] {1, 1});

	}

}
