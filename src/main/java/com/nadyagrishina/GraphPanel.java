
package com.nadyagrishina;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

class GraphPanel extends JPanel {

    private static final int SIZE = 40;
    private static final int RADIUS = SIZE / 2;

    private final Map<String, List<String>> graph;

    private final Map<String, Point> positions = Map.of(
            "A", new Point(80, 180),
            "B", new Point(230, 80),
            "C", new Point(400, 80),
            "D", new Point(230, 280),
            "E", new Point(400, 280)
    );

    private final Map<String, String> names = Map.of(
            "A", "Velín",
            "B", "Laboratoř",
            "C", "Sklad",
            "D", "Reaktor",
            "E", "Kyslík"
    );

    private Set<String> reachable;
    private String start = "A";
    private String brokenFrom = "B";
    private String brokenTo = "D";

    public GraphPanel(
            Map<String, List<String>> graph,
            Set<String> reachable
    ) {
        this.graph = graph;
        this.reachable = reachable;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        drawEdges(g2);
        drawModules(g2);
        drawLegend(g2);

        g2.dispose();
    }

    private void drawEdges(Graphics2D g) {
        for (String from : graph.keySet()) {
            for (String to : graph.get(from)) {
                if (from.compareTo(to) >= 0) {
                    continue;
                }

                Point a = positions.get(from);
                Point b = positions.get(to);

                boolean broken =
                        (from.equals(brokenFrom) && to.equals(brokenTo))
                                || (from.equals(brokenTo) && to.equals(brokenFrom));

                if (broken) {
                    g.setColor(Color.RED);
                    g.setStroke(new BasicStroke(
                            3, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND, 0,
                            new float[]{8, 6}, 0
                    ));
                } else {
                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(3));
                }

                g.drawLine(
                        a.x + RADIUS, a.y + RADIUS,
                        b.x + RADIUS, b.y + RADIUS
                );
            }
        }
    }

    private void drawModules(Graphics2D g) {
        for (String name : positions.keySet()) {
            Point p = positions.get(name);

            if (name.equals(start)) {
                g.setColor(Color.BLUE);
            } else if (reachable.contains(name)) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }

            g.fillOval(p.x, p.y, SIZE, SIZE);

            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textX = p.x + (SIZE - fm.stringWidth(name)) / 2;
            int textY = p.y + (SIZE - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(name, textX, textY);

            g.setColor(Color.BLACK);
            int labelX, labelY;
            if (name.equals("B")){
                labelX = p.x - 10;
                labelY = p.y - 10;
            } else {
                labelX = p.x;
                labelY = p.y + 60;
            }
            g.drawString(names.get(name), labelX, labelY);
        }
    }

    private void drawLegend(Graphics2D g) {
        drawLegendItem(g, 20, Color.BLUE, "Výchozí modul");
        drawLegendItem(g, 180, Color.GREEN, "Dostupný modul");
        drawLegendItem(g, 355, Color.RED, "Nedostupný modul");
    }

    private void drawLegendItem(
            Graphics2D g, int x, Color color, String text
    ) {
        g.setColor(color);
        g.fillOval(x, 20, 12, 12);

        g.setColor(Color.BLACK);
        g.drawString(text, x + 17, 31);
    }

    public void updateGraph(
            Set<String> reachable,
            String start,
            String from,
            String to
    ) {
        this.reachable = reachable;
        this.start = start;
        this.brokenFrom = from;
        this.brokenTo = to;
        repaint();
    }
}
