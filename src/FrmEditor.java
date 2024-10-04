import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.awt.event.MouseEvent;

public class FrmEditor extends JFrame {

    private JButton btnCargar;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnSeleccionar;
    private JComboBox<String> cmbTipo;
    private JToolBar tbEditor;
    private JPanel pnlGrafica;

    Estado estado;
    int x;
    int y;
    int x2;
    int y2;
    Color color;

    LinkedList<Trazo> trazos = new LinkedList<>();

    public FrmEditor() {

        tbEditor = new JToolBar();
        btnCargar = new JButton();
        btnGuardar = new JButton();
        btnEliminar = new JButton();
        cmbTipo = new JComboBox<>();
        btnSeleccionar = new JButton();

        pnlGrafica = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());

                redibujarTrazos(g);

                if (estado == Estado.TRAZANDO) {
                    g.setColor(color);
                    switch (cmbTipo.getSelectedIndex()) {
                        case 0: // Línea
                            g.drawLine(x, y, x2, y2);
                            break;
                        case 1: // Rectángulo
                            g.drawRect(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));
                            break;
                        case 2: // Círculo
                            g.drawOval(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));
                            break;
                    }
                }
            }
        };

        btnCargar.setIcon(new ImageIcon(getClass().getResource("/iconos/AbrirArchivos.png")));
        btnCargar.setToolTipText("Agregar");
        btnCargar.setPreferredSize(new Dimension(60, 50));
        btnCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCargarClick(evt);
            }
        });
        tbEditor.add(btnCargar);

        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/iconos/Guardar.png")));
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.setPreferredSize(new Dimension(60, 50));
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGuardarClick(evt);
            }
        });
        tbEditor.add(btnGuardar);

        cmbTipo.setModel(new DefaultComboBoxModel<>(new String[] { "Línea", "Rectángulo", "Círculo" }));
        cmbTipo.setPreferredSize(new Dimension(100, 50));
        tbEditor.add(cmbTipo);

        btnSeleccionar.setIcon(new ImageIcon(getClass().getResource("/iconos/Seleccionar.png")));
        btnSeleccionar.setToolTipText("Seleccionar");
        btnSeleccionar.setPreferredSize(new Dimension(60, 50));
        btnSeleccionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSeleccionarClick(evt);
            }
        });
        tbEditor.add(btnSeleccionar);

        btnEliminar.setIcon(new ImageIcon(getClass().getResource("/iconos/Eliminar.png")));
        btnEliminar.setToolTipText("Eliminar");
        btnEliminar.setPreferredSize(new Dimension(60, 50));
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnEliminarClick(evt);
            }
        });
        tbEditor.add(btnEliminar);

        pnlGrafica.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                pnlGraficaMouseClicked(evt);
            }
        });
        pnlGrafica.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent evt) {
                pnlGraficaMouseMoved(evt);
            }
        });

        pnlGrafica.setPreferredSize(new Dimension(300, 200));

        getContentPane().add(tbEditor, BorderLayout.NORTH);
        getContentPane().add(pnlGrafica, BorderLayout.CENTER);

        estado = Estado.NADA;
        color = Color.white;

        limpiarPanel();

        this.pack();

        setSize(800, 600);
        setTitle("Editor de gráficas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void limpiarPanel() {
        pnlGrafica.repaint();
    }

    private void redibujarTrazos(Graphics g) {
        for (Trazo trazo : trazos) {
            g.setColor(color);
            switch (trazo.tipo) {
                case "Línea":
                    g.drawLine(trazo.x1, trazo.y1, trazo.x2, trazo.y2);
                    break;
                case "Rectángulo":
                    g.drawRect(Math.min(trazo.x1, trazo.x2), Math.min(trazo.y1, trazo.y2),
                            Math.abs(trazo.x2 - trazo.x1), Math.abs(trazo.y2 - trazo.y1));
                    break;
                case "Círculo":
                    g.drawOval(Math.min(trazo.x1, trazo.x2), Math.min(trazo.y1, trazo.y2),
                            Math.abs(trazo.x2 - trazo.x1), Math.abs(trazo.y2 - trazo.y1));
                    break;
            }
        }
    }

    private void btnCargarClick(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar dibujo");

        // Filtro para solo mostrar archivos .obj
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Dibujos (*.obj)", "obj"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            trazos.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    trazos.add(new Trazo(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                            Integer.parseInt(data[3]), Integer.parseInt(data[4])));
                }
                repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void btnGuardarClick(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar trazo como");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Dibujos (*.obj)", "obj"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".obj")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".obj");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                for (Trazo trazo : trazos) {
                    writer.write(trazo.tipo + "," + trazo.x1 + "," + trazo.y1 + "," + trazo.x2 + "," + trazo.y2);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void btnSeleccionarClick(ActionEvent evt) {

    }

    private void btnEliminarClick(ActionEvent evt) {
        Trazo trazoSeleccionado = seleccionarTrazo(x, y);
        if (trazoSeleccionado != null) {
            trazos.remove(trazoSeleccionado);
            repaint();
        }
    }

    private Trazo seleccionarTrazo(int x, int y) {
        for (Trazo trazo : trazos) {
            if (x >= Math.min(trazo.x1, trazo.x2) && x <= Math.max(trazo.x1, trazo.x2) &&
                    y >= Math.min(trazo.y1, trazo.y2) && y <= Math.max(trazo.y1, trazo.y2)) {
                return trazo;
            }
        }
        return null;
    }

    private void pnlGraficaMouseClicked(MouseEvent evt) {
        if (estado == Estado.TRAZANDO) {
            String tipo = (String) cmbTipo.getSelectedItem();

            trazos.add(new Trazo(tipo, x, y, x2, y2));
            estado = Estado.NADA;
            pnlGrafica.repaint();
        } else {
            estado = Estado.TRAZANDO;
            x = evt.getX();
            y = evt.getY();
            x2 = x;
            y2 = y;
        }
    }

    private void pnlGraficaMouseMoved(MouseEvent evt) {
        if (estado == Estado.TRAZANDO) {

            x2 = evt.getX();
            y2 = evt.getY();
            pnlGrafica.repaint();
        }
    }

}
