package main;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import logic.BodyLogic;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
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
	private String originalText = null;
	private int line = 0;
	
	public static int getLine() {
		return self.line + 1;
	}
	
	public static String getEditorText() {
		return self.editor.getText();
	}
	
	public static void redrawEditorText() {
		String[] lines = self.originalText.split("\\r?\\n");
		int offset =
			(int) Math.floor(Math.log10(lines.length));
		
		for (int i = 0; i < lines.length; i++) {
			int n = i + 1;
			int offset0 = (int) Math.floor(Math.log10(n));
			String zeroes = "0".repeat(offset - offset0);
			lines[i] = "[" + zeroes + n + "] " + lines[i];
			
			if (i == self.line - 1 ||
				i == lines.length - 1 && self.line == -1)
				lines[i] = ">" + lines[i];
			else
				lines[i] = " " + lines[i];
		}
		
		self.editor.setText(String.join("\r\n", lines));
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
		// Initialize logic sequence parsers.
		
		BodyLogic.initialize();
		
		
		// Get error descriptors.
		
		ArrayList<String> errors = new ArrayList<String>();
		
		try {
			File file = new File(Resources.ERROR_FILENAME);
			java.util.Scanner reader = new java.util.Scanner(file);
			
			while (reader.hasNextLine()) {
				errors.add(reader.nextLine());
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Resources.error_codes = errors.toArray(Resources.error_codes);
		
		
		// Draw window.
		
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
				editor.setText(originalText);
				originalText = null;
			}
		});
		
		Button scanNextLine = new Button(sashForm_4, SWT.NONE);
		scanNextLine.setText("Next Line");
		scanNextLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (line == -1)
					return;
				
				if (originalText == null) {
					self.originalText = getEditorText();
					scanner.file_clear();
				}
				
				String[] lines = originalText.split("\\r?\\n");
				scanner.read_line(lines[line]);
				
				if (line == lines.length - 1)
					setLine(-1);
				else
					setLine(line + 1);

				editor.setEditable(false);
				redrawEditorText();
			}
		});
		
		Button scanAll = new Button(sashForm_4, SWT.NONE);
		scanAll.setText("Scan All");
		scanAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (line == -1)
					return;

				if (originalText == null) {
					self.originalText = getEditorText();
					scanner.file_clear();
				}
				
				String[] lines = getEditorText().split("\\r?\\n");
				
				for (; line < lines.length; line++)
					scanner.read_line(lines[line]);
				
				setLine(-1);
				editor.setEditable(false);
				redrawEditorText();
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
		console_input.setText("");
		
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
