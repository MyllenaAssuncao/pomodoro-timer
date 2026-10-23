import javax.swing.*;
import java.awt.*;

/**
 * PomodoroTimer — um cronômetro de estudos baseado na técnica Pomodoro.
 *
 * A técnica: você estuda com FOCO por 25 minutos, DESCANSA 5, e a cada 4 blocos
 * de foco faz um descanso mais longo (15 min). Este programa controla esse
 * ciclo sozinho e ainda conta quantos "pomodoros" você já completou.
 */

public class PomodoroTimer extends JFrame {

    // ----- Cores de cada fase (dão a "cara" visual do app) -----
    private static final Color FOCO_COR  = new Color(0xD64541); // vermelho tomate
    private static final Color CURTO_COR = new Color(0x2E9E6B); // verde
    private static final Color LONGO_COR = new Color(0x3B7DD8); // azul
    private static final Color INK       = new Color(0x2A2E35); // cinza bem escuro

    // ----- "Estado" do cronômetro (as informações que mudam enquanto roda) -----
    private TipoSessao atual = TipoSessao.FOCO;
    private int segundosRestantes = atual.getSegundos();
    private int pomodorosConcluidos = 0;

    private final javax.swing.Timer relogio = new javax.swing.Timer(1000, e -> tick());

    // ----- Componentes da tela que precisa atualizar depois -----
    private final JPanel  topo        = new JPanel(new BorderLayout());
    private final JLabel  lblFase     = new JLabel("Foco", SwingConstants.CENTER);
    private final JLabel  lblTempo    = new JLabel("25:00", SwingConstants.CENTER);
    private final JLabel  lblContador = new JLabel("", SwingConstants.CENTER);
    private final JButton btIniciar   = new JButton("Iniciar");

    public PomodoroTimer() {
        setTitle("Pomodoro - Timer de Estudos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 460);
        setMinimumSize(new Dimension(320, 420));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarTopo(),   BorderLayout.NORTH);
        add(criarCentro(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        aplicarCorDaFase();
        atualizarRelogio();
        atualizarContador();
    }

    private JComponent criarTopo() {
        topo.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        lblFase.setForeground(Color.WHITE);
        lblFase.setFont(new Font("SansSerif", Font.BOLD, 20));
        topo.add(lblFase, BorderLayout.CENTER);
        return topo;
    }

    private JComponent criarCentro() {
        JPanel centro = new JPanel();
        centro.setBackground(Color.WHITE);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        lblTempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTempo.setFont(new Font("SansSerif", Font.BOLD, 72));
        lblTempo.setForeground(INK);

        lblContador.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblContador.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblContador.setForeground(new Color(0x7A7F87));
        lblContador.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        centro.add(Box.createVerticalGlue());
        centro.add(lblTempo);
        centro.add(lblContador);
        centro.add(Box.createVerticalGlue());
        return centro;
    }

    private JComponent criarRodape() {
        JPanel rodape = new JPanel(new GridLayout(1, 3, 10, 0));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 16, 18, 16));
        rodape.setBackground(Color.WHITE);

        estilizar(btIniciar);
        btIniciar.addActionListener(e -> iniciarOuPausar());

        JButton btReiniciar = new JButton("Reiniciar");
        estilizar(btReiniciar);
        btReiniciar.addActionListener(e -> reiniciar());

        JButton btPular = new JButton("Pular");
        estilizar(btPular);
        btPular.addActionListener(e -> irParaProximaFase(false));

        rodape.add(btIniciar);
        rodape.add(btReiniciar);
        rodape.add(btPular);
        return rodape;
    }

    // ---------- ações dos botões ----------

    /** Liga ou pausa o cronômetro, conforme o estado atual. */
    private void iniciarOuPausar() {
        if (relogio.isRunning()) {
            relogio.stop();
            btIniciar.setText("Continuar");
        } else {
            relogio.start();
            btIniciar.setText("Pausar");
        }
    }

    /** Volta o tempo cheio da fase atual, sem trocar de fase. */
    private void reiniciar() {
        relogio.stop();
        segundosRestantes = atual.getSegundos();
        btIniciar.setText("Iniciar");
        atualizarRelogio();
    }

    // ---------- o "tique-taque" (acontece a cada 1 segundo) ----------

    private void tick() {
        segundosRestantes--;
        atualizarRelogio();
        if (segundosRestantes <= 0) {
            irParaProximaFase(true);
        }
    }

    private void irParaProximaFase(boolean focoCompletado) {
        Toolkit.getDefaultToolkit().beep();

        if (atual == TipoSessao.FOCO) {
            if (focoCompletado) pomodorosConcluidos++;
            boolean horaDoLongo = focoCompletado && pomodorosConcluidos % 4 == 0;
            atual = horaDoLongo ? TipoSessao.DESCANSO_LONGO : TipoSessao.DESCANSO_CURTO;
        } else {
            atual = TipoSessao.FOCO;
        }

        segundosRestantes = atual.getSegundos();
        aplicarCorDaFase();
        atualizarRelogio();
        atualizarContador();
    }

    // ---------- atualizações de tela ----------

    /** Mostra o tempo restante no formato MM:SS (ex.: 04:09). */
    private void atualizarRelogio() {
        int seguro = Math.max(segundosRestantes, 0);
        int min = seguro / 60;
        int seg = seguro % 60;
        lblTempo.setText(String.format("%02d:%02d", min, seg));
    }

    private void atualizarContador() {
        lblFase.setText(atual.getNome());
        lblContador.setText("Pomodoros concluídos: " + pomodorosConcluidos);
    }

    private void aplicarCorDaFase() {
        Color cor = switch (atual) {
            case FOCO -> FOCO_COR;
            case DESCANSO_CURTO -> CURTO_COR;
            case DESCANSO_LONGO -> LONGO_COR;
        };
        topo.setBackground(cor);
        lblTempo.setForeground(cor);
    }

    private void estilizar(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBackground(new Color(0xF0F2F5));
        b.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignorado) {}
        SwingUtilities.invokeLater(() -> new PomodoroTimer().setVisible(true));
    }
}
