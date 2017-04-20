package edu.hit.aoli.search;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel;

public class SearchEngine extends JFrame {

	private static final long serialVersionUID = 1972699322918200933L;
	
	// icon image
	private static final ImageIcon logoIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/logo.png"));
	private static final ImageIcon appIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/icon_16x16.png"));
	private static final ImageIcon docsIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/docs_24x24.png"));
	private static final ImageIcon spiderIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/spider_24x24.png"));
	private static final ImageIcon authorIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/author_24x24.png"));
	private static final ImageIcon s_thxIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/thx_24x24.png"));
	private static final ImageIcon l_thxIcon = 
			new ImageIcon(SearchEngine.class.getResource("imgs/thx_72x72.png"));
	
	// docs path, index path
	private String docsPath = null;
	private String indexPath = "index/";

	private JTextField editInput;
	private JButton btnSearch;
	private JEditorPane tpShow;
	private JScrollPane jsp;
	private JPanel searchPanel;
	private JPanel logoPanel;
	private JLabel logoLabel;
	private JPanel topPanel;
	// private JLabel topLabel;
	private JMenuBar menuBar;
	private JMenu dataMenu;
	private JMenu aboutMenu;
	private JMenu skinMenu;
	private JMenuItem docsItem;
	private JMenuItem spiderItem;
	private JMenuItem authorItem;
	private JMenuItem thxItem;
	private JMenuItem autumnItem;
	private JMenuItem dustCoffeItem;
	private JMenuItem darkSteelItem;
	private JMenuItem blueSteelItem;
	private JMenuItem bussinessItem;
	private JMenuItem ceruleanItem;
	private JMenuItem challegerItem;
	private JMenuItem graphiteItem;
	private JMenuItem ravenItem;
	private JMenuItem magellanItem;
	

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Properties prop = new Properties();
					File file = new File("skin.properties");
					if (!file.exists()) {
						UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
					}
					else {
						FileInputStream fis = new FileInputStream("skin.properties");
						prop.load(fis);
						String strid = prop.getProperty("skinid");
						if (strid == null) {
							UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
						}
						else {
							int id = Integer.parseInt(strid);
							switch (id) {
							case 1:
								UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
								break;
							case 2:
								UIManager.setLookAndFeel(new SubstanceDustCoffeeLookAndFeel());
								break;
							case 3:
								UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
								break;
							case 4:
								UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
								break;
							case 5:
								UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
								break;
							case 6:
								UIManager.setLookAndFeel(new SubstanceCeruleanLookAndFeel());
								break;
							case 7:
								UIManager.setLookAndFeel(new SubstanceChallengerDeepLookAndFeel());
								break;
							case 8:
								UIManager.setLookAndFeel(new SubstanceGraphiteGlassLookAndFeel());
								break;
							case 9:
								UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
								break;
							case 10:
								UIManager.setLookAndFeel(new SubstanceMagellanLookAndFeel());
								break;
							default:
								UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
							}
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", 
							JOptionPane.ERROR_MESSAGE);
				}
				new SearchEngine().setVisible(true);
			}
		});
	}

	public SearchEngine() {
		createComp();
		addComp();
		initComp();
		setSize(800, 600);
		setLocationRelativeTo(null); // center of screen
		setTitle("搜索一下");
		setIconImage(appIcon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * create components
	 */
	private void createComp() {
		searchPanel = new JPanel();
		logoPanel = new JPanel();
		topPanel = new JPanel();
		tpShow = new JEditorPane();
		editInput = new JTextField();
		btnSearch = new JButton("搜索");
		// topLabel = new JLabel("请输入要搜索的关键字：");
		logoLabel = new JLabel();
		menuBar = new JMenuBar();
		dataMenu = new JMenu("数据");
		skinMenu = new JMenu("皮肤");
		aboutMenu = new JMenu("关于");
		docsItem = new JMenuItem("文档集...");
		spiderItem = new JMenuItem("从网络爬取");
		autumnItem = new JMenuItem("中秋");
		dustCoffeItem = new JMenuItem("黑咖啡");
		darkSteelItem = new JMenuItem("撒哈拉");
		blueSteelItem = new JMenuItem("金属蓝");
		bussinessItem = new JMenuItem("商务");
		ceruleanItem = new JMenuItem("天蓝");
		challegerItem = new JMenuItem("挑战者");
		graphiteItem = new JMenuItem("石墨玻璃");
		ravenItem = new JMenuItem("乌鸦黑");
		magellanItem = new JMenuItem("麦哲伦蓝");
		authorItem = new JMenuItem("作者");
		thxItem = new JMenuItem("致谢");
		jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * add components to frame
	 */
	private void addComp() {

		logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		logoPanel.add(logoLabel);

		searchPanel.setLayout(new BorderLayout());
		// searchPanel.add(topLabel, BorderLayout.WEST);
		searchPanel.add(editInput, BorderLayout.CENTER);
		searchPanel.add(btnSearch, BorderLayout.EAST);

		topPanel.setLayout(new BorderLayout());
		topPanel.add(logoPanel, BorderLayout.NORTH);
		topPanel.add(searchPanel, BorderLayout.SOUTH);

		dataMenu.add(docsItem);
		dataMenu.add(spiderItem);
		skinMenu.add(autumnItem);
		skinMenu.add(dustCoffeItem);
		skinMenu.add(darkSteelItem);
		skinMenu.add(blueSteelItem);
		skinMenu.add(bussinessItem);
		skinMenu.add(ceruleanItem);
		skinMenu.add(challegerItem);
		skinMenu.add(graphiteItem);
		skinMenu.add(ravenItem);
		skinMenu.add(magellanItem);
		aboutMenu.add(authorItem);
		aboutMenu.add(thxItem);
		menuBar.add(dataMenu);
		menuBar.add(skinMenu);
		menuBar.add(aboutMenu);
		setJMenuBar(menuBar);

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		jsp.setViewportView(tpShow);
		add(jsp, BorderLayout.CENTER);
	}

	/**
	 * initialize components
	 */
	private void initComp() {
		logoLabel.setIcon(logoIcon);
		jsp.setBorder(null);
		tpShow.setContentType("text/html; charset=GBK");
		tpShow.setBackground(getBackground());
		tpShow.setEditable(false);
		//tpShow.setPreferredSize(new Dimension(tpShow.getWidth(),tpShow.getHeight()));
		tpShow.addHyperlinkListener(new HyperlinkListener() {
			@Override
	         public void hyperlinkUpdate(HyperlinkEvent e) {
	             if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	                 JEditorPane pane = (JEditorPane) e.getSource();
	                 if (e instanceof HTMLFrameHyperlinkEvent) {
	                     HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
	                     HTMLDocument doc = (HTMLDocument)pane.getDocument();
	                     doc.processHTMLFrameHyperlinkEvent(evt);
	                 } else {
	                     try {
	                         pane.setPage(e.getURL());
	                     } catch (Throwable t) {
	                    	 JOptionPane.showMessageDialog(null, t.getMessage(), "错误", 
	                    			 JOptionPane.ERROR_MESSAGE);
	                     }
	                 }
	             }
	         }
		});

		// menu skin selector
		autumnItem.setActionCommand("1");
		dustCoffeItem.setActionCommand("2");
		darkSteelItem.setActionCommand("3");
		blueSteelItem.setActionCommand("4");
		bussinessItem.setActionCommand("5");
		ceruleanItem.setActionCommand("6");
		challegerItem.setActionCommand("7");
		graphiteItem.setActionCommand("8");
		ravenItem.setActionCommand("9");
		magellanItem.setActionCommand("10");
		SkinSelector skinSelector = new SkinSelector();
		autumnItem.addActionListener(skinSelector);
		dustCoffeItem.addActionListener(skinSelector);
		darkSteelItem.addActionListener(skinSelector);
		blueSteelItem.addActionListener(skinSelector);
		bussinessItem.addActionListener(skinSelector);
		ceruleanItem.addActionListener(skinSelector);
		challegerItem.addActionListener(skinSelector);
		graphiteItem.addActionListener(skinSelector);
		ravenItem.addActionListener(skinSelector);
		magellanItem.addActionListener(skinSelector);
		
		// menu select documents path
		docsItem.setIcon(docsIcon);
		docsItem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		docsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
				InputEvent.CTRL_DOWN_MASK));
		docsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(new File("."));
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setDialogTitle("请选择文档集目录");
				if (jfc.showOpenDialog(SearchEngine.this) == JFileChooser.APPROVE_OPTION) {
					docsPath = jfc.getSelectedFile().getAbsolutePath();
					final Path docDir = Paths.get(docsPath);
					if (!Files.isReadable(docDir)) {
						JOptionPane.showMessageDialog(SearchEngine.this, "文档目录 '" + 
								docDir.toAbsolutePath() + "' 不存在或者不可读", "错误", 
								JOptionPane.ERROR_MESSAGE);
						return ;
					}
					else {
						JDialog dialog = new JDialog(SearchEngine.this, "正在为第一次搜索创建索引...", false);
						dialog.setLayout(new FlowLayout(FlowLayout.CENTER));
						dialog.setSize(220, 70);
						dialog.setResizable(false);
						dialog.setLocationRelativeTo(null);
						JProgressBar jpb = new JProgressBar(JProgressBar.HORIZONTAL);
						jpb.setIndeterminate(true);
						jpb.setStringPainted(true);
						jpb.setString("正在建立索引,请稍候...");
						dialog.add(jpb);
						dialog.setVisible(true);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								indexFiles(indexPath, docsPath, dialog);
							}
						});
					}
					//System.out.println(docsPath);
				}
			}
		});
		
		// menu spider item
		spiderItem.setIcon(spiderIcon);
		spiderItem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		spiderItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
				InputEvent.CTRL_DOWN_MASK));
		spiderItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (spiderItem.isSelected())
					spiderItem.setSelected(false);
				else {
					spiderItem.setSelected(true);
					JOptionPane.showMessageDialog(SearchEngine.this, "正在开发中...", 
							"敬请期待", JOptionPane.OK_OPTION, spiderIcon);
				}
			}
		});
		
		// menu item author 
		authorItem.setIcon(authorIcon);
		authorItem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		authorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 
				InputEvent.CTRL_DOWN_MASK));
		authorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(SearchEngine.this, "作者：李傲\n学号：16S103157", 
						"作者", JOptionPane.OK_OPTION, authorIcon);
			}
		});
		
		// menu item thanks 
		thxItem.setIcon(s_thxIcon);
		thxItem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		thxItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 
				InputEvent.CTRL_DOWN_MASK));
		thxItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(SearchEngine.this, 
						"感谢软件编写过程中使用到的以下资源作者：\n" +
						"检索：lucene\n" + 
						"外观：substance\n" +
						"图标：http://www.iconarchive.com/",
						"致谢", JOptionPane.OK_OPTION, l_thxIcon);
			}
		});
		
		editInput.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		btnSearch.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		// topLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		editInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					startSearch();
			}
		});
		
		// search button
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startSearch();
			}
		});
	}
	
	private void startSearch() {
		String keyword = editInput.getText();
		if (keyword == null || keyword.isEmpty())
			return ;
		keyword = keyword.trim();
		if (keyword.length() == 0)
			return;
		File indexDir = new File(indexPath);
		if (!indexDir.isDirectory() || indexDir.list().length < 1) 
			JOptionPane.showMessageDialog(SearchEngine.this, "请先选择数据来源", 
					"找不到索引文件", JOptionPane.ERROR_MESSAGE);
		else {
			try {
				searchFiles(indexPath, keyword);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(SearchEngine.this, e.getMessage(), 
						"错误", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void indexFiles(String indexPath, String docsPath, JDialog dialog) {
		// create new index files
		boolean create = true;
		final Path docDir = Paths.get(docsPath);
		//Date start = new Date();
		try {
			// System.out.println("Indexing to directory '" + indexPath + "'...");
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);
			writer.close();
			//Date end = new Date();
			// System.out.println(end.getTime() - start.getTime() + " total milliseconds");
			dialog.setVisible(false);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(SearchEngine.this, e.getMessage(), "错误", 
					JOptionPane.ERROR_MESSAGE);
			// System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For
	 * good throughput, put multiple documents into your input file(s). An
	 * example of this is in the benchmark module, which can create "line doc"
	 * files, one document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param path
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @throws IOException
	 *             If there is a low-level I/O error
	 */
	private void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	/** Indexes a single document */
	private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document
			Document doc = new Document();
			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			// Add the last modified date of the file a field named "modified".
			// Use a LongPoint that is indexed (i.e. efficiently filterable with
			// PointRangeQuery). This indexes to milli-second resolution, which
			// is often too fine. You could instead create a number based on
			// year/month/day/hour/minutes/seconds, down the resolution you
			// require.
			// For example the long value 2011021714 would mean
			// February 17, 2011, 2-3 PM.
			doc.add(new LongPoint("modified", lastModified));
			// Add the contents of the file to a field named "contents". Specify
			// a Reader,
			// so that the text of the file is tokenized and indexed, but not
			// stored.
			// Note that FileReader expects the file to be in UTF-8 encoding.
			// If that's not the case searching for special characters will
			// fail.
			doc.add(new TextField("contents",
					new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			
			BufferedReader buffr = new BufferedReader(new FileReader(file.toFile()));
			final int len = 32765;
			char [] cbuf = new char[len];
			buffr.read(cbuf, 0, len);
			buffr.close();
			doc.add(new TextField("content", new String(cbuf), Store.YES));
			writer.addDocument(doc);
		}
	}

	public void searchFiles(String indexPath, String keyword) throws Exception {
		
		String field = "contents"; // search file contents
		int hitsPerPage = 10; // how many hits per page

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser(field, analyzer);
		Query query = parser.parse(keyword);
		doPagingSearch(analyzer, searcher, query, hitsPerPage, keyword);
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search
	 * engine presents pages of size n to the user. The user can then go to the
	 * next page if interested in the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results
	 * are collected to fill 5 result pages. If the user wants to page beyond
	 * this limit, then the query is executed another time and all hits are
	 * collected.
	 * @throws InvalidTokenOffsetsException 
	 * 
	 */
	public void doPagingSearch(Analyzer analyzer, IndexSearcher searcher, Query query, int hitsPerPage, String keyword) throws IOException, InvalidTokenOffsetsException {
		Date timeStart = new Date();
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		Date timeEnd = new Date();
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);
		if (end > hits.length) {
			int op = JOptionPane.showConfirmDialog(SearchEngine.this, "只收集总匹配文档中的前 1 - " +
					hits.length + " , 要收集更多吗？", "提示", JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if (op == JOptionPane.YES_OPTION)
				hits = searcher.search(query, numTotalHits).scoreDocs;
		}
		end = Math.min(hits.length, start + hitsPerPage);
		// highlighter
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<font color=#ff0000>", "</font>");
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		highlighter.setTextFragmenter(new SimpleFragmenter(200)); // display 200 chars
		
		BufferedWriter bufw = new BufferedWriter(new FileWriter(new File("index.html")));
		bufw.write("共找到 " + numTotalHits + " 篇匹配文档， 共用时： " 
				+ (timeEnd.getTime() - timeStart.getTime()) + "ms");
		bufw.newLine();
		showPage(analyzer, searcher, hitsPerPage, keyword, hits, numTotalHits, start, end, highlighter, bufw);
		bufw.close();
	}

	private void showPage(Analyzer analyzer, IndexSearcher searcher, int hitsPerPage, String keyword, ScoreDoc[] hits, int numTotalHits,
			int start, int end, Highlighter highlighter, BufferedWriter bufw)
			throws IOException, InvalidTokenOffsetsException, MalformedURLException {
		for (int i = start; i < end; i++) {
			int id = hits[i].doc;
			Document doc = searcher.doc(id);
			// highlight content
			String text = doc.get("content");
			String str = highlighter.getBestFragment(analyzer.tokenStream("content", text), text);
			if (str == null)
				str = text.substring(0, 300);
			// highlight title
			File file = new File(doc.get("path"));
			String filename = file.getName();
			filename = filename.replaceAll(keyword, "<font color=#ff0000>"+keyword+"</font>");
			// write to html file
			bufw.write("<p><span style=\"font-size: 15px\"><a href=\"" + file.toURI().toURL() + "\">"+filename+
					"</a></span><br>" +str+"</p>");
		}
		
		// show page
		URL url = new File("index.html").toURI().toURL();
		// force update the content
		javax.swing.text.Document docu = tpShow.getDocument();
		docu.putProperty(javax.swing.text.Document.StreamDescriptionProperty, null);
		tpShow.setPage(url);
		
//		// switch pages
//		if (numTotalHits >= end) {
//			
//			if (start - hitsPerPage >= 0) {
//				bufw.write("<p><a href>上一页");
//				System.out.print("(p)revious page, ");
//			}
//			if (start + hitsPerPage < numTotalHits) {
//				System.out.print("(n)ext page, ");
//			}
//			System.out.println("(q)uit or enter number to jump to a page.");
//
//			String line = "ndas";
//			if (line.length() == 0 || line.charAt(0) == 'q') {
//			}
//			if (line.charAt(0) == 'p') {
//				start = Math.max(0, start - hitsPerPage);
//			} else if (line.charAt(0) == 'n') {
//				if (start + hitsPerPage < numTotalHits) {
//					start += hitsPerPage;
//				}
//			} else {
//				int page = Integer.parseInt(line);
//				if ((page - 1) * hitsPerPage < numTotalHits) {
//					start = (page - 1) * hitsPerPage;
//				} else {
//					System.out.println("No such page");
//				}
//			}
//			end = Math.min(numTotalHits, start + hitsPerPage);
//		}
	}
}

class SkinSelector implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		Properties prop = new Properties();
		prop.setProperty("skinid", e.getActionCommand());
		File file = new File("skin.properties");
		try{
			if (!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			prop.store(fos, "Author:LIAO DO NOT EDIT THIS FILE! ");
			fos.close();
			JOptionPane.showMessageDialog(null, "重启软件生效", "应用成功", 
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException ioe){
			JOptionPane.showMessageDialog(null, ioe.getMessage(), "错误", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
}