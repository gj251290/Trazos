import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JOptionPane;

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
    private JButton btnLimpiar;
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
    boolean nuevaSeleccion;

    Trazo trazoSeleccionado = null;

    LinkedList<Trazo> trazos = new LinkedList<>();

    public FrmEditor() {
        tbEditor = new JToolBar();
        btnCargar = new JButton();
        btnGuardar = new JButton();
        btnEliminar = new JButton();
        btnLimpiar = new JButton();
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
                        case 0:
                            g.drawLine(x, y, x2, y2);
                            break;
                        case 1:
                            g.drawRect(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));
                            break;
                        case 2:
                            g.drawOval(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));
                            break;
                    }
                } else if (estado == Estado.SELECCIONANDO && !nuevaSeleccion) {

                    g.setColor(Color.YELLOW);
                    g.drawRect(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));
                }

                if (trazoSeleccionado != null) {
                    g.setColor(Color.RED);
                    switch (trazoSeleccionado.tipo) {
                        case "Línea":
                            g.drawLine(trazoSeleccionado.x1, trazoSeleccionado.y1,
                                    trazoSeleccionado.x2, trazoSeleccionado.y2);
                            break;
                        case "Rectángulo":
                            g.drawRect(Math.min(trazoSeleccionado.x1, trazoSeleccionado.x2),
                                    Math.min(trazoSeleccionado.y1, trazoSeleccionado.y2),
                                    Math.abs(trazoSeleccionado.x2 - trazoSeleccionado.x1),
                                    Math.abs(trazoSeleccionado.y2 - trazoSeleccionado.y1));
                            break;
                        case "Círculo":
                            g.drawOval(Math.min(trazoSeleccionado.x1, trazoSeleccionado.x2),
                                    Math.min(trazoSeleccionado.y1, trazoSeleccionado.y2),
                                    Math.abs(trazoSeleccionado.x2 - trazoSeleccionado.x1),
                                    Math.abs(trazoSeleccionado.y2 - trazoSeleccionado.y1));
                            break;
                    }
                }
            }
        };

        btnCargar.setIcon(new ImageIcon(getClass().getResource("/iconos/cargar.png")));
        btnCargar.setToolTipText("Cargar dibujo");
        btnCargar.setPreferredSize(new Dimension(60, 32));
        btnCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCargarClick(evt);
            }
        });
        tbEditor.add(btnCargar);

        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/iconos/guardar.png")));
        btnGuardar.setToolTipText("Guardar dibujo");
        btnGuardar.setPreferredSize(new Dimension(60, 32));
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGuardarClick(evt);
            }
        });
        tbEditor.add(btnGuardar);

        cmbTipo.setModel(new DefaultComboBoxModel<>(new String[] { "Línea", "Rectángulo", "Círculo" }));
        cmbTipo.setPreferredSize(new Dimension(100, 32));
        tbEditor.add(cmbTipo);

        btnSeleccionar.setIcon(new ImageIcon(getClass().getResource("/iconos/seleccionar.png")));
        btnSeleccionar.setToolTipText("Seleccionar trazo");
        btnSeleccionar.setPreferredSize(new Dimension(60, 32));
        btnSeleccionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSeleccionarClick(evt);
            }
        });
        tbEditor.add(btnSeleccionar);

        btnEliminar.setIcon(new ImageIcon(getClass().getResource("/iconos/eliminar.png")));
        btnEliminar.setToolTipText("Eliminar trazo");
        btnEliminar.setPreferredSize(new Dimension(60, 32));
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnEliminarClick(evt);
            }
        });
        tbEditor.add(btnEliminar);

        btnLimpiar.setIcon(new ImageIcon(getClass().getResource("/iconos/limpiar.png")));
        btnLimpiar.setToolTipText("Limpiar lienzo");
        btnLimpiar.setPreferredSize(new Dimension(60, 32));
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                limpiarPanel();
            }
        });
        tbEditor.add(btnLimpiar);

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

        pnlGrafica.setPreferredSize(new Dimension(800, 600));

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
        trazos.clear();
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

        if (trazoSeleccionado != null) {
            g.setColor(Color.RED);
            switch (trazoSeleccionado.tipo) {
                case "Línea":
                    g.drawLine(trazoSeleccionado.x1, trazoSeleccionado.y1,
                            trazoSeleccionado.x2, trazoSeleccionado.y2);
                    break;
                case "Rectángulo":
                    g.drawRect(Math.min(trazoSeleccionado.x1, trazoSeleccionado.x2),
                            Math.min(trazoSeleccionado.y1, trazoSeleccionado.y2),
                            Math.abs(trazoSeleccionado.x2 - trazoSeleccionado.x1),
                            Math.abs(trazoSeleccionado.y2 - trazoSeleccionado.y1));
                    break;
                case "Círculo":
                    g.drawOval(Math.min(trazoSeleccionado.x1, trazoSeleccionado.x2),
                            Math.min(trazoSeleccionado.y1, trazoSeleccionado.y2),
                            Math.abs(trazoSeleccionado.x2 - trazoSeleccionado.x1),
                            Math.abs(trazoSeleccionado.y2 - trazoSeleccionado.y1));
                    break;
            }
        }
    }

    private void btnCargarClick(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar dibujo");

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

            if (selectedFile.exists()) {

                int respuesta = JOptionPane.showConfirmDialog(this,
                        "El archivo ya existe. ¿Deseas reemplazarlo?",
                        "Confirmación de escritura",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
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
        boolean habilitado = cmbTipo.isEnabled();

        if (habilitado) {
            estado = Estado.SELECCIONANDO;
            cmbTipo.setEnabled(false);
            btnSeleccionar.setBackground(Color.orange);
            nuevaSeleccion = true;
            trazoSeleccionado = null;
            repaint();
        } else {
            estado = Estado.NADA;
            cmbTipo.setEnabled(true);
            btnSeleccionar.setBackground(null);
            nuevaSeleccion = false;
        }
    }

    private void btnEliminarClick(ActionEvent evt) {
        if (estado == Estado.SELECCIONANDO && trazoSeleccionado != null) {
            trazos.remove(trazoSeleccionado);
            trazoSeleccionado = null;
            repaint();
        } else {
            if (!trazos.isEmpty()) {
                trazos.removeLast();
                repaint();
            } else {
                JOptionPane.showMessageDialog(null, "No hay trazos");
            }
        }
    }

    private Trazo seleccionarTrazo() {
        if (estado == Estado.SELECCIONANDO) {
            int selXMin = Math.min(x, x2);
            int selXMax = Math.max(x, x2);
            int selYMin = Math.min(y, y2);
            int selYMax = Math.max(y, y2);

            for (Trazo trazo : trazos) {
                int trazoXMin = Math.min(trazo.x1, trazo.x2);
                int trazoXMax = Math.max(trazo.x1, trazo.x2);
                int trazoYMin = Math.min(trazo.y1, trazo.y2);
                int trazoYMax = Math.max(trazo.y1, trazo.y2);

                if (selXMin <= trazoXMin && selXMax >= trazoXMax &&
                        selYMin <= trazoYMin && selYMax >= trazoYMax) {
                    return trazo;
                }
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
        } else if (estado == Estado.NADA) {
            estado = Estado.TRAZANDO;
            x = evt.getX();
            y = evt.getY();
            x2 = x;
            y2 = y;
        } else if (estado == Estado.SELECCIONANDO) {
            if (nuevaSeleccion) {
                x = evt.getX();
                y = evt.getY();
                nuevaSeleccion = false;

                x2 = x;
                y2 = y;
                repaint();
            } else {
                x2 = evt.getX();
                y2 = evt.getY();
                nuevaSeleccion = true;

                trazoSeleccionado = seleccionarTrazo();

                if (trazoSeleccionado == null) {
                    JOptionPane.showMessageDialog(null, "Ningún trazo seleccionado");
                }
                repaint();
            }
        }
    }

    private void pnlGraficaMouseMoved(MouseEvent evt) {
        if (estado == Estado.TRAZANDO) {
            x2 = evt.getX();
            y2 = evt.getY();
            pnlGrafica.repaint();
        } else if (estado == Estado.SELECCIONANDO && !nuevaSeleccion) {
            x2 = evt.getX();
            y2 = evt.getY();
            pnlGrafica.repaint();
        }
    }
}
