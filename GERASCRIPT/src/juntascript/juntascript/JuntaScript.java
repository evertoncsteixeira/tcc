package juntascript.juntascript;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNRepository;

import montarscript.montarscript.MontarScript;
import repository.ConectarSvn;
import repository.ListarDiferencaTag;
import repository.TagSvn;
import sqlserver.ConectarSql;
import util.IDMGeral;

public class JuntaScript {
	
	private ConectarSvn conectar;
	private JFrame frame;
	private JTextField versaoScript;
	private JTable tabelaScripts;
	private DefaultTableModel tableModel;
	private DefaultTableModel dftm;
	private JTextField repostiroSVN;
	private JTextField usuarioSVN;
	private JPasswordField senhaSVN;
	private SVNRepository repository;
	private JTextField textoConectado;
	private JTextField nomeScript;
	private String siglaSistema;
	private String versao = "V01.0.0";
	private JTextField versaoPrograma;
	private int largBase = 1350;
	private int altBase = 750;
	private String arquivoCriado;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// criar jaela aqui
					JuntaScript window = new JuntaScript();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JuntaScript() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		conectar = new ConectarSvn();
		frame = new JFrame();
		frame.setSize(largBase, altBase);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(
				new WindowAdapter() { 
					public void windowClosing(WindowEvent evt) {	 
							ConectarSvn.apagarCheckout(); 
					}  
				});
		frame.setTitle("Gerar Instalador");
		frame.getContentPane().setLayout(null);

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 53, 824, 479);
		frame.getContentPane().add(tabbedPane);
		
		tabbedPane.setSize(largBase-50, altBase-120);
	
		JPanel conectarSvnTab = new JPanel();
		conectarSvnTab.setBorder(new LineBorder(new Color(0, 0, 0)));
		conectarSvnTab.setSize(200, 200);
		tabbedPane.addTab("Conectar SVN", null, conectarSvnTab, null);
		conectarSvnTab.setLayout(null);

		JLabel lblRepositrioSvn = new JLabel("Reposit\u00F3rio SVN");
		lblRepositrioSvn.setBounds((largBase/4)+63, 114, 114, 14);
		conectarSvnTab.add(lblRepositrioSvn);
		
		repostiroSVN = new JTextField();
		repostiroSVN.setEditable(false);
		repostiroSVN.setBounds((largBase/4)+157, 111, 582, 20);
		conectarSvnTab.add(repostiroSVN);
		repostiroSVN.setColumns(10);

		JLabel lblUsurio = new JLabel("Usu\u00E1rio");
		lblUsurio.setBounds((largBase/4)+63, 163, 48, 14);
		conectarSvnTab.add(lblUsurio);

		usuarioSVN = new JTextField();
		usuarioSVN.setBounds((largBase/4)+157, 160, 299, 20);
		conectarSvnTab.add(usuarioSVN);
		usuarioSVN.setColumns(10);

		JLabel lblSenha = new JLabel("Senha");
		lblSenha.setBounds((largBase/4)+63, 205, 48, 14);
		conectarSvnTab.add(lblSenha);

		senhaSVN = new JPasswordField();
		senhaSVN.setBounds((largBase/4)+157, 202, 298, 20);
		conectarSvnTab.add(senhaSVN);

		textoConectado = new JTextField();
		textoConectado.setEditable(false);
		textoConectado.setForeground(Color.RED);
		textoConectado.setFont(new Font("Tahoma", Font.BOLD, 11));
		textoConectado.setBounds((largBase/4)+81, 345, 752, 20);
		conectarSvnTab.add(textoConectado);
		textoConectado.setColumns(10);
		
		
		JPanel criarScriptsTab = new JPanel();
		criarScriptsTab.setBorder(new LineBorder(new Color(0, 0, 0)));
		tabbedPane.addTab("Criação de Scripts de Instalação", null, criarScriptsTab, null);
		tabbedPane.setEnabledAt(1, false);
		criarScriptsTab.setLayout(null);

		JLayeredPane bordaTag = new JLayeredPane();
		bordaTag.setBounds((largBase/6), 11, 863, 132);
		criarScriptsTab.add(bordaTag);
		bordaTag.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		

		JLabel lblTagAnterior = new JLabel("Tag");
		lblTagAnterior.setBounds(61, 41, 96, 14);
		bordaTag.add(lblTagAnterior);

		final JComboBox cmb_taganterior = new JComboBox();
		cmb_taganterior.setBounds(134, 38, 228, 20);

		bordaTag.add(cmb_taganterior);
		
		
		JPanel testeSql = new JPanel();
		testeSql.setBorder(new LineBorder(new Color(0, 0, 0)));
		tabbedPane.addTab("Testar Scripts", null, testeSql, null);
		tabbedPane.setEnabledAt(2, false);
		testeSql.setLayout(null);
		
		final JTextArea textoSql = new JTextArea();
		textoSql.setLineWrap(true);
		textoSql.setEditable(true);

		JScrollPane scroll = new JScrollPane(textoSql, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds((largBase/9), 25, 950, 500);
		testeSql.add(scroll);
		textoSql.setForeground(Color.BLUE);
		textoSql.setFont(new Font("Tahoma", Font.BOLD, 11));
		JLayeredPane bordaTag2 = new JLayeredPane();
		bordaTag2.setBounds((largBase/10), 11, 1000, 550);
		testeSql.add(bordaTag2);
		
		
		bordaTag2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		
		final JComboBox comboSistema = new JComboBox();
		comboSistema.setBounds((largBase/4)+503, 19, 255, 20);
		frame.getContentPane().add(comboSistema);

		DefaultComboBoxModel comboModelNovo = (DefaultComboBoxModel) comboSistema.getModel();
		comboModelNovo.addElement("CC - Contas Correntes");
		comboModelNovo.addElement("P2 - Poupança");
		comboModelNovo.addElement("E2 - Controle de Convenios de Terceiros");

		repostiroSVN.setText("https://EVERTON/svn/tcc");
		siglaSistema = "CC";
		comboSistema.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String versaoAlterada = (String) comboSistema.getSelectedItem();
				if (versaoAlterada.equals("CC - Contas Correntes")) {
					repostiroSVN.setText("https://EVERTON/svn/tcc/");
					siglaSistema = "CC";
				} else {
					if (versaoAlterada.equals("P2 - Poupança")) {
						repostiroSVN.setText("https://EVERTON/svn/P2");
						siglaSistema = "P2";
					} else {
						repostiroSVN.setText("https://EVERTON/svn/E2");
						siglaSistema = "E2";
					}
				}
			}
		});
		
		final JButton btnConectarSql = new JButton("Testar SQL");

		btnConectarSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConectarSql conectarsql = new ConectarSql();
				conectarsql.conectar();
				try {
					StringBuffer sb = conectarsql.lerArquivoSql(arquivoCriado);
					String[] comandos = sb.toString().split("GO");
					 
					for (int i = 0; i < comandos.length; i++){
						conectarsql.ExecutaScript(comandos[i]);
					}
					 
					JOptionPane.showMessageDialog(null, "Script executado com sucesso!");
					Runtime.getRuntime().exec("explorer.exe " + IDMGeral.caminhoJuntaScript + "CC" + IDMGeral.SEPARADOR_BARRA  + versaoScript.getText() + IDMGeral.SEPARADOR_BARRA );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
		
		btnConectarSql.setBounds((largBase/6)+400, 530, 100, 25);		
		testeSql.add(btnConectarSql);
		testeSql.getRootPane().setDefaultButton(btnConectarSql);

		final JButton btnConectar = new JButton("Conectar ");

		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConectarSvn conectar = new ConectarSvn();
				
				if (usuarioSVN.getText() == null || ("").equals(usuarioSVN.getText())) {
					textoConectado.setText("Usuario não informado !!!");
					return;
				}
				
				if (senhaSVN.getPassword() == null || ("").equals(new String(senhaSVN.getPassword()).trim())) {
					textoConectado.setText("Senha não informada !!!");
					return;
				}
				
				if (repository == null) {
					try {
						repository = conectar.conectarSvn(repostiroSVN.getText().toString(),
								usuarioSVN.getText().toString(), new String(senhaSVN.getPassword()).trim());

					} catch (Throwable e2) {
						JOptionPane.showMessageDialog(frame,
								"Erro ao conectar o SVN , verificar o Repositorio, usuario e senha");
						return;
					}

					textoConectado.setText("Conectado ao SVN , repositorio " + repostiroSVN.getText().toString()
							+ " com o usuario: " + usuarioSVN.getText().toString());
					comboSistema.setEnabled(false);

					try {
						carregaCombo(repostiroSVN.getText().toString(), usuarioSVN.getText().toString(),
								new String(senhaSVN.getPassword()).trim(), cmb_taganterior);
					} catch (Throwable e1) {
						textoConectado.setText("Falha ao conectar ao SVN, verificar o Usuario e Senha estão corretos e se este tem acesso.");
						versaoScript.setText("");
						nomeScript.setText("");
						repository = null;
						tabbedPane.setEnabledAt(1, false);
						comboSistema.setEnabled(true);
						btnConectar.setText("Conectar");
						return;
					}
					
					versaoScript.setText("");
					nomeScript.setText("");
					btnConectar.setText("Desconectar");
					btnConectar.setEnabled(true);
					tabbedPane.setEnabledAt(1, true);
					tabbedPane.setSelectedIndex(1);
					
					geraTabela(new ArrayList<String>());
					tabbedPane.setEnabledAt(1, true);
				} else {
					textoConectado.setText("Desconectado do SVN , com o usuario: " + usuarioSVN.getText().toString());
					ConectarSvn.apagarCheckout();
					versaoScript.setText("");
					nomeScript.setText("");
					repository = null;
					tabbedPane.setEnabledAt(1, false);
					comboSistema.setEnabled(true);
					btnConectar.setText("Conectar");
				}
			}
		});

		JButton btnVerificarScripts = new JButton("Verificar Scripts");
		btnVerificarScripts.addActionListener(new ActionListener() {

			ArrayList<?> listaDiferencas = new ArrayList<Object>();

			public void actionPerformed(ActionEvent e) {

				if (repository != null) {

					ListarDiferencaTag listar = new ListarDiferencaTag();

					String tagversao = (String) cmb_taganterior.getSelectedItem();
					
					listaDiferencas = (ArrayList<?>) listar.listarDiferencaTag(repostiroSVN.getText(), tagversao);
					if (listaDiferencas.isEmpty()) { 
							JOptionPane.showMessageDialog(frame, "Não existe diferença entre as tags");
							return;
					}
					
					
					final ArrayList<String> arrayList = new ArrayList<String>();
					
					for (int i = 0; i < listaDiferencas.size(); i++) {
						String retorno = (String) listaDiferencas.get(i);
						String script = retorno.substring(retorno.lastIndexOf("|") + 1, retorno.length());
						SVNProperties fileProperties = new SVNProperties();
						String autor = null;
						String revisao = null;
						String mensagem = null;
						long lRevisao;
						try {
							repository.getFile( "/tags/"+ tagversao +"/Scripts/"+script , -1 , fileProperties , null );
							revisao = fileProperties.getStringValue("svn:entry:committed-rev");
							lRevisao = Long.parseLong(revisao);
							Collection<?> logEntries;
							try {
								logEntries = repository.log(new String[] { "" }, null,
										lRevisao, lRevisao, true, true);
							

							for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
								SVNLogEntry logEntry = (SVNLogEntry) entries.next();
								if(mensagem == null){
									autor = logEntry.getAuthor();
									mensagem = logEntry.getMessage();
								}else{
									autor+= "//"+logEntry.getAuthor();
									mensagem+= "//"+logEntry.getMessage();
								}
							}
							arrayList.add(retorno+"|"+autor+"|"+revisao+"|"+mensagem);
							} catch (SVNException e2) {}
						} catch (SVNException e1) {}
						
						geraTabela(arrayList);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Necessario conectar ao SVN na aba de Conecção");
					return;
				}
			}
		});
		btnVerificarScripts.setBounds(340, 85, 163, 23);
		bordaTag.add(btnVerificarScripts);
		
		cmb_taganterior.addItemListener(e -> itemSelecionado(e));
//		cmb_taganterior.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				cmb_taganterior.setSelectedItem(cmb_taganterior.getSelectedItem());
//			}
//		});
//
//		cmb_taganterior.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				String versaoAlterada = (String) cmb_taganterior.getSelectedItem();
//				if (versaoAlterada == null) {
//					versaoScript.setText("");
//				} else {
//					versaoScript.setText(versaoAlterada.substring(0, versaoAlterada.lastIndexOf("_")));
//				}
//			}
//		});


		btnConectar.setBounds((largBase/6)+395, 279, 114, 23);
		conectarSvnTab.add(btnConectar);
		conectarSvnTab.getRootPane().setDefaultButton(btnConectar);
		
		JLabel lblVerso = new JLabel("Vers\u00E3o:");
		lblVerso.setBounds(15, altBase-177, 48, 14);
		conectarSvnTab.add(lblVerso);
		
		versaoPrograma = new JTextField();
		versaoPrograma.setForeground(Color.RED);
		versaoPrograma.setEditable(false);
		versaoPrograma.setBounds(65, altBase-180, 96, 20);
		versaoPrograma.setText(versao);
		conectarSvnTab.add(versaoPrograma);
		versaoPrograma.setColumns(10);

		JScrollPane painelRolagem = new JScrollPane();
		painelRolagem.setBounds(10, 161, 1230, 400);
		criarScriptsTab.add(painelRolagem);
		tabelaScripts = new JTable(tableModel);
		painelRolagem.setViewportView(tabelaScripts);

		versaoScript = new JTextField();
		versaoScript.setBounds(100, altBase-176, 125, 20);
		criarScriptsTab.add(versaoScript);
		versaoScript.setColumns(10);

		versaoScript.setToolTipText("Versao no formato : VXX_XX_X exemplos:  V12_02_1 , V12_02_1a ..");

		JLabel lblNomeDoScript = new JLabel("COMPLEMENTO");
		lblNomeDoScript.setBounds(286, altBase-173, 141, 14);
		criarScriptsTab.add(lblNomeDoScript);

		// botao de criacao do script final.
		JButton btnGerarScript = new JButton("Gerar  Script");
		btnGerarScript.setBounds(678, altBase-176, 146, 20);
		criarScriptsTab.add(btnGerarScript);

		JButton btnDesceScript = new JButton("");
		btnDesceScript.setBounds((largBase / 4) + 910, 279, 33, 23);
		criarScriptsTab.add(btnDesceScript);
		btnDesceScript.setIcon(new ImageIcon(JuntaScript.class.getResource("/img/down.gif")));
		JButton btnSobeScript = new JButton("", new ImageIcon(JuntaScript.class.getResource("/img/up.gif")));
		btnSobeScript.setBounds((largBase / 4) + 910,245, 33, 23);
		criarScriptsTab.add(btnSobeScript);

		nomeScript = new JTextField();
		nomeScript.setColumns(10);
		nomeScript.setBounds(387, altBase-176, 281, 20);
		criarScriptsTab.add(nomeScript);

		nomeScript.setToolTipText(
				"Nome do script, especificar o nome do Cliente ou Sistemas:");

		JLabel label = new JLabel("NOMESCRIPT");
		label.setBounds(10, altBase-173, 101, 14);
		criarScriptsTab.add(label);

		btnSobeScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				boolean selecionadoAtu = (Boolean) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(),0);
				String statusAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 1));
				String scriptAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 2));
				String autorAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 3));
				String revisaoAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 4));
				//String comentarioAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 5));

				boolean selecionadoNovo = false;
				String statusNovo = null;
				String scriptNovo = null;
				String autorNovo = null;
				String revisaoNovo = null;
				//String comentarioNovo = null;

				if (tabelaScripts.getSelectedRow() > 0) {
					selecionadoNovo = (Boolean) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() - 1,0);
					statusNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() - 1, 1));
					scriptNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() - 1, 2));
					autorNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() - 1, 3));
					revisaoNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() - 1, 4));
					//comentarioNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() - 1, 5));

					// novo
					tabelaScripts.getModel().setValueAt(selecionadoAtu, tabelaScripts.getSelectedRow() - 1, 0);
					tabelaScripts.getModel().setValueAt(statusAtu, tabelaScripts.getSelectedRow() - 1, 1);
					tabelaScripts.getModel().setValueAt(scriptAtu, tabelaScripts.getSelectedRow() - 1, 2);
					tabelaScripts.getModel().setValueAt(autorAtu, tabelaScripts.getSelectedRow() - 1, 3);
					tabelaScripts.getModel().setValueAt(revisaoAtu, tabelaScripts.getSelectedRow() - 1, 4);
					//tabelaScripts.getModel().setValueAt(comentarioAtu, tabelaScripts.getSelectedRow() - 1, 5);

					// atu
					tabelaScripts.getModel().setValueAt(selecionadoNovo, tabelaScripts.getSelectedRow(), 0);
					tabelaScripts.getModel().setValueAt(statusNovo, tabelaScripts.getSelectedRow(), 1);
					tabelaScripts.getModel().setValueAt(scriptNovo, tabelaScripts.getSelectedRow(), 2);
					tabelaScripts.getModel().setValueAt(autorNovo, tabelaScripts.getSelectedRow() , 3);
					tabelaScripts.getModel().setValueAt(revisaoNovo, tabelaScripts.getSelectedRow() , 4);
					//tabelaScripts.getModel().setValueAt(comentarioNovo, tabelaScripts.getSelectedRow() , 5);

					tabelaScripts.setRowSelectionInterval(tabelaScripts.getSelectedRow() - 1,
							tabelaScripts.getSelectedRow() - 1);

				}
			}
		});

		btnDesceScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selecionadoAtu = (Boolean) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(),
						0);
				String statusAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 1));
				String scriptAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 2));
				String autorAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 3));
				String revisaoAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 4));
				//String comentarioAtu = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow(), 5));

				boolean selecionadoNovo = false;
				String statusNovo = null;
				String scriptNovo = null;
				String autorNovo = null;
				String revisaoNovo = null;
				//String comentarioNovo = null;

				if (tabelaScripts.getSelectedRow() < tabelaScripts.getModel().getRowCount() - 1) {
					selecionadoNovo = (Boolean) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() + 1,0);
					statusNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() + 1, 1));
					scriptNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() + 1, 2));
					autorNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() + 1, 3));
					revisaoNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() + 1, 4));
					//comentarioNovo = new String((String) tabelaScripts.getModel().getValueAt(tabelaScripts.getSelectedRow() + 1, 5));

					// novo
					tabelaScripts.getModel().setValueAt(selecionadoAtu, tabelaScripts.getSelectedRow() + 1, 0);
					tabelaScripts.getModel().setValueAt(statusAtu, tabelaScripts.getSelectedRow() + 1, 1);
					tabelaScripts.getModel().setValueAt(scriptAtu, tabelaScripts.getSelectedRow() + 1, 2);
					tabelaScripts.getModel().setValueAt(autorAtu, tabelaScripts.getSelectedRow() + 1, 3);
					tabelaScripts.getModel().setValueAt(revisaoAtu, tabelaScripts.getSelectedRow() + 1, 4);
					//tabelaScripts.getModel().setValueAt(comentarioAtu, tabelaScripts.getSelectedRow() + 1, 5);

					// atu
					tabelaScripts.getModel().setValueAt(selecionadoNovo, tabelaScripts.getSelectedRow(), 0);
					tabelaScripts.getModel().setValueAt(statusNovo, tabelaScripts.getSelectedRow(), 1);
					tabelaScripts.getModel().setValueAt(scriptNovo, tabelaScripts.getSelectedRow(), 2);
					tabelaScripts.getModel().setValueAt(autorNovo, tabelaScripts.getSelectedRow() , 3);
					tabelaScripts.getModel().setValueAt(revisaoNovo, tabelaScripts.getSelectedRow() , 4);
					//tabelaScripts.getModel().setValueAt(comentarioNovo, tabelaScripts.getSelectedRow() , 5);

					tabelaScripts.setRowSelectionInterval(tabelaScripts.getSelectedRow() + 1,
							tabelaScripts.getSelectedRow() + 1);

				}
			}
		});

		btnGerarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (repository != null) {

					if (tabelaScripts.getRowCount() == 0) {
						JOptionPane.showMessageDialog(frame, "Realizar a Verificação entre duas Tags !!! ", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (versaoScript.getText() == null || versaoScript.getText().equals("")) {
						JOptionPane.showMessageDialog(frame, "Preencher a versão do script", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					ArrayList<Object> listaSelecionada = new ArrayList<Object>();
					int count = 0;

					for (int i = 0; i < tabelaScripts.getRowCount(); i++) {
						if ((Boolean) tabelaScripts.getModel().getValueAt(i, 0)) {
							count++;
							listaSelecionada.add(tabelaScripts.getModel().getValueAt(i, 2));
						}
					}

					MontarScript montaScript = new MontarScript();

					if (nomeScript.getText() == null) {
						nomeScript.setText("");
					}

					if (count == 0) {
						JOptionPane.showMessageDialog(frame, "Selecionar ao menos um script da grid !!!", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					try {
						conectar.checkout(listaSelecionada, repository, (String) cmb_taganterior.getSelectedItem(), siglaSistema, versaoScript.getText());
						arquivoCriado = montaScript.montarScript(listaSelecionada, versaoScript.getText(), 
								nomeScript.getText(),(String) cmb_taganterior.getSelectedItem(),siglaSistema, usuarioSVN.getText().toString());
						JOptionPane.showMessageDialog(frame, "Arquivo gerado em : " + arquivoCriado);
						tabbedPane.setEnabledAt(2, true);
						tabbedPane.setSelectedIndex(2);
						ConectarSql conectarsql = new ConectarSql();
						StringBuffer sb = conectarsql.lerArquivoSql(arquivoCriado);
						textoSql.setText(sb.toString());
						
					} catch (Throwable e1) {
						JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						tabbedPane.setEnabledAt(2, false);
						return;
					}

					
				} else {
					JOptionPane.showMessageDialog(frame, "Necessario conectar ao SVN na aba de ConexÃ£o");
					return;
				}
			}
		});

		JLabel lblFerramentaParaCricao = new JLabel("Ferramenta para Cria\u00E7\u00E3o de Script de Vers\u00E3o");
		lblFerramentaParaCricao.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFerramentaParaCricao.setBounds((largBase/4)+143, 11, 350, 31);
		frame.getContentPane().add(lblFerramentaParaCricao);
	}

	private Object itemSelecionado(ItemEvent e) {
		// Quando for selecionado um item
		if(e.getStateChange() == ItemEvent.SELECTED){
			String itemSelecionado = (String) e.getItem();
			if (itemSelecionado == null || itemSelecionado.isEmpty()) {
				versaoScript.setText("");
			} else {
				versaoScript.setText(itemSelecionado.substring(0, itemSelecionado.lastIndexOf("_")));
			}
		}
		return null;
	}

	private void carregaCombo(String url, String usuario, String senha, JComboBox cmb) throws Throwable {
		DefaultComboBoxModel comboModel = (DefaultComboBoxModel) cmb.getModel();
		comboModel.removeAllElements();
		List<?> tags = new ArrayList<Object>();

		TagSvn tagSvn = new TagSvn();
		comboModel.addElement("");
		if (repository != null) {
			tags = (ArrayList<?>) tagSvn.recuperaTag(url, usuario, senha);
	
			if (tags == null) {
				comboModel.addElement("");
				throw new RuntimeException("Falha ao conectar ao SVN, verificar o Usuario e Senha estão corretos e se este tem acesso.") ;
			} else {
				for (int linha = tags.size() - 1; linha >= 0; linha--) {
					String x = (String) tags.get(linha);
					if (x.length() > 2 && "V".equals(x.substring(0, 1))) { // MOSTRANDO NA COMBO APENAS AS VERSOES V ALGUMA
																			// COISA.
						if ("V0".equals(x.substring(0, 2)) || "V1".equals(x.substring(0, 2))
								|| "V2".equals(x.substring(0, 2)) || "V3".equals(x.substring(0, 2))
								|| "V4".equals(x.substring(0, 2)) || "V5".equals(x.substring(0, 2))
								|| "V6".equals(x.substring(0, 2)) || "V7".equals(x.substring(0, 2))
								|| "V8".equals(x.substring(0, 2)) || "V9".equals(x.substring(0, 2))) {
							comboModel.addElement(tags.get(linha));
						}

					}
				}
			}
		} else {
			comboModel.addElement("");
		}
	}

	class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getSource();
			if (source instanceof AbstractButton == false)
				return;
			boolean checked = e.getStateChange() == ItemEvent.SELECTED;
			for (int i = 0; i < tabelaScripts.getModel().getRowCount(); i++) {
				tabelaScripts.getModel().setValueAt(new Boolean(checked), i, 0);
			}
		}
	}
	
	private void geraTabela(ArrayList<String> listaDiferencas) {
		Object[] objs2 = { "", "Status", "Script","autor","revisao"};
		Object[][] nova = new Object[listaDiferencas.size()][5];

		for (int i = 0; i < listaDiferencas.size(); i++) {
			String retorno = listaDiferencas.get(i);
			String[] split = retorno.split("\\|");
			nova[i][0] = Boolean.FALSE;
			nova[i][1] = split[0];
			nova[i][2] = split[1];
			nova[i][3] = split[2];
			nova[i][4] = split[3];

		}
		//TODO colocar campo novo com nome e descriï¿½ï¿½o
		dftm = new DefaultTableModel(nova, objs2) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Boolean.class, Object.class, Object.class, Object.class, Object.class };
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		TableColumnModel modeloColuna = tabelaScripts.getColumnModel();

		tabelaScripts.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabelaScripts.setModel(dftm);

		TableColumn tc = modeloColuna.getColumn(0);
		tc.setCellEditor(tabelaScripts.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(tabelaScripts.getDefaultRenderer(Boolean.class));
		tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));
		tc.setPreferredWidth(20);

		modeloColuna.getColumn(1).setPreferredWidth(150);
		modeloColuna.getColumn(2).setPreferredWidth(626);
		modeloColuna.getColumn(3).setPreferredWidth(80);
		modeloColuna.getColumn(4).setPreferredWidth(60);
	}

	class CheckBoxHeader extends JCheckBox implements TableCellRenderer, MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected CheckBoxHeader rendererComponent;
		protected int column;
		protected boolean mousePressed = false;

		public CheckBoxHeader(ItemListener itemListener) {
			rendererComponent = this;
			rendererComponent.addItemListener(itemListener);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					rendererComponent.setForeground(header.getForeground());
					rendererComponent.setBackground(header.getBackground());
					rendererComponent.setFont(header.getFont());
					header.addMouseListener(rendererComponent);
				}
			}
			setColumn(column);
			rendererComponent.setText("Check All");
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			return rendererComponent;
		}

		protected void setColumn(int column) {
			this.column = column;
		}

		public int getColumn() {
			return column;
		}

		protected void handleClickEvent(MouseEvent e) {
			if (mousePressed) {
				mousePressed = false;
				JTableHeader header = (JTableHeader) (e.getSource());
				JTable tableView = header.getTable();
				TableColumnModel columnModel = tableView.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = tableView.convertColumnIndexToModel(viewColumn);
				if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
					doClick();
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			handleClickEvent(e);
			((JTableHeader) e.getSource()).repaint();
		}

		public void mousePressed(MouseEvent e) {
			mousePressed = true;
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
		
		private void itemSelecionado(ItemEvent e){
	
		}
		
	}
	
}
